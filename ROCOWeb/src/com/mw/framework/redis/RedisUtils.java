package com.mw.framework.redis;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import com.alibaba.druid.util.StringUtils;
import com.mw.framework.bean.Constants.RedisMsgType;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.utils.DateTools;

@Component
public class RedisUtils {
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TaskService taskService;
	@Autowired
	private SysDataDictDao sysDataDictDao;
	private SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String TASK_SQL = "select a.assignee_ as assignee, b.group_id_ as group_id, a.id_ as task_id,a.create_time_ as create_time from act_ru_task a, act_ru_identitylink b, act_hi_actinst aha, act_ct_mapping acm where a.id_ = b.task_id_ and a.id_ = aha.task_id_ and aha.proc_inst_id_ = acm.procinstid   and aha.act_name_ <>'订单审绘重审' and b.group_id_ <>'gp_drawing'";
	private static final String GP_DRAWING_SQL = "select a.task_id,a.assignee,'gp_drawing' as group_id,a.create_time_ as create_time from view_bpm_activity_task a,sale_header b where a.uuid=b.id and a.group_id='gp_drawing' and a.task_id in(select d2.task_id from sale_header a1,sale_item b1,material_head c1, view_bpm_activity_task d2  where a1.id=b1.pid and b1.material_head_id=c1.id and d2.uuid=a1.id and   (c1.file_Type='1' or  c1.file_Type is null)  and d2.group_id='gp_drawing' group by d2.task_id,c1.file_Type   having count(*)>0) order by a.create_time_";
	private static final String GP_DRAWING_CAD_SQL = "select a.task_id,a.assignee,'gp_drawing_cad' as group_id,a.create_time_ as create_time from view_bpm_activity_task a,sale_header b where a.uuid=b.id and a.group_id='gp_drawing' and a.task_id not in(select d2.task_id from sale_header a1,sale_item b1,material_head c1, view_bpm_activity_task d2  where a1.id=b1.pid and b1.material_head_id=c1.id and d2.uuid=a1.id and  (c1.file_Type='1' or c1.file_type is null) and d2.group_id='gp_drawing' group by d2.task_id,c1.file_Type   having count(*)>0) order by a.create_time_";
	/**
	 * 任务节点信息全部放入db2中
	 */
	private static final int TASK_DEFAULT_DB_INDEX=2;

	/**
	 * 存入任務
	 * 
	 * @param assignee
	 * @param groupId
	 * @param taskId
	 * @return
	 */
	private boolean saveTask(Jedis jedis, String assignee, String groupId,
			String taskId, Date startTime, boolean sort) {
		
		Transaction trans = jedis.multi();
		String name = assignee + "_" + groupId;
		// 以taskId为权重 以时间为值，以此排序
		// 在对有序集合类型排序时会忽略元素的分数，只对元素自身的值进行排序
		trans.zadd(name, Double.parseDouble(startTime.getTime()+""),taskId);
		List<Object> result = trans.exec();
		// 排序需要在 插入元素之后。
		if (sort) {
			this.sortSet(jedis, name);
		}
		return result != null && result.size() > 0
				&& (((Long) result.get(0)) > 0);
	}

	/**
	 * 删除taskId(结束流程时)
	 * 
	 * @param assignee
	 * @param groupId
	 * @param taskId
	 */
	public void endTask(Object assignee, String groupId, String taskId) {
		assignee = (assignee == null ? "Unclaimed" : assignee);
		Jedis jedis = getResource(RedisMsgType.TASK_WORK);
		try {
			Transaction trans = jedis.multi();
			trans.zrem(assignee + "_" + groupId, taskId);
			//trans.zremrangeByScore(assignee + "_" + groupId, taskId, taskId);
			trans.exec();
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(jedisPool, jedis);
		}
	}

	/**
	 * 新增task(开始流程)
	 * 
	 * @param assignee
	 * @param groupId
	 * @param taskId
	 * @param startTime
	 */
	public void startTask(Object assignee, String groupId, String taskId,
			String difLevel, Date startTime) {
		assignee = (assignee == null ? "Unclaimed" : assignee);
		Jedis jedis = getResource(RedisMsgType.TASK_WORK);
		try {
			Transaction trans = jedis.multi();
			String level = StringUtils.isEmpty(difLevel) ? "" : "_" + difLevel;
			trans.zadd(assignee + "_" + groupId + level,
					Double.parseDouble(startTime.getTime()+""), taskId);
			trans.exec();
		} catch (Exception e) {
			jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(jedisPool, jedis);
		}
	}

	/**
	 * 排序，以开始时间排序，asc
	 */
	public void sortSet(Jedis jedis, String setName) {
		SortingParams params = new SortingParams();
		params.asc();
		jedis.sort(setName, params);
	}

	/**
	 * 獲取jedis實例
	 * 
	 * @return
	 */
	public Jedis getResource(RedisMsgType type) {
		Jedis jedis= jedisPool.getResource();
		//set default message 
		this.setDefaultProp(jedis, type);
		return jedis;
	}

	/**
	 * 得到排序下標
	 * 
	 * @param map
	 * @param time
	 * @return
	 */
	@SuppressWarnings("unused")
	private int getIndex(Map<String, String> map, Date time) {
		int index = 0;
		Date compileTime = new Date();
		try {
			for (; index < map.size(); index++) {
				compileTime = simp.parse(map.get("" + index));
				if (compileTime.getTime() > time.getTime()) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}

	/**
	 * 领取任务，如果抛出异常，说明事务执行错误
	 * 
	 * @param groupId
	 *            任务组
	 * @param taskId
	 *            任务Id
	 * @param srcAssignee
	 *            任务原执行者
	 * @param desAssignee
	 *            任务目标执行者
	 * @return
	 */
	public boolean claimTask(String groupId, String taskId, Object srcAssignee,
			Object desAssignee) {
		srcAssignee = (srcAssignee == null ? "Unclaimed" : srcAssignee);
		desAssignee = (desAssignee == null ? "Unclaimed" : desAssignee);
		Jedis jedis = getResource(RedisMsgType.TASK_WORK);
		Boolean flag = false;
		TaskService taskService = SpringContextHolder.getBean("taskService");
		Transaction trans = null;
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 因为订单审绘重审和订单审绘的groupid是一样的,但是订单审绘重审不该计算到订单审绘任务池中
		// ,所以对于groupId为gp_drawing的任务需要辨别
		if ("gp_drawing".equals(groupId)) {
			// 如果包含sub说明是子流程
			// 那么groupId需要需要重新被定义
			if (task != null && task.getProcessDefinitionId().contains("sub")) {
				groupId += "_sub";
			}
		}
		try {
			// 任务以 taskId为score 以startTime 为值 存入到 assignee+"_"+groupId 的有序set中
			// 这样做可以直接用sort去排序，redis中set的排序依据是starttime
			String srcSetName = srcAssignee + "_" + groupId;
			String targetSetName = desAssignee + "_" + groupId;
			// 获取到taskId对应的starttime
			Double time=jedis.zscore(srcSetName, taskId);
//			Set<String> startTimeSet = jedis.zrangeByScore(srcSetName, taskId,
//					taskId);
			String startTime = time==null?"":(time+"");
//			for (String string : startTimeSet) {
//				startTime = string;
//			}
			if (StringUtils.isEmpty(startTime)) {
				startTime = ""
						+ taskService.createTaskQuery().taskId(taskId)
								.singleResult().getCreateTime().getTime();
			}
			trans = jedis.multi();
			trans.zrem/*rangeByScore*/(srcSetName, taskId, taskId);
			// 再加入到目标人的任务列表中
			trans.zadd(targetSetName, Double.parseDouble(startTime), taskId);
			List<Object> result = trans.exec();
			for (int index = 0; index < result.size(); index++) {
				// 如果有报错，那么会出现异常
				System.out.println(result.get(index));
				// Integer.parseInt((String) result.get(index));
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
			if (trans != null) {
				try {
					trans.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 重置任务清单
	 */
	public void resetTask() {
		synchronized (this) {
			//任务放在1DB中
		Jedis jedis = getResource(RedisMsgType.TASK_WORK);
		//先清空所有任务
		jedis.flushAll();
		try {
			// 普通任务列表
			List<Map<String, Object>> taskList = jdbcTemplate
					.queryForList(TASK_SQL);
			// 审绘任务列表
			List<Map<String, Object>> drawList = jdbcTemplate
					.queryForList(GP_DRAWING_SQL);
			// 审绘的cad 任务列表 vb
			List<Map<String, Object>> cadDrawList = jdbcTemplate
					.queryForList(GP_DRAWING_CAD_SQL);
			taskList.addAll(drawList);
			taskList.addAll(cadDrawList);
			// 加入到任务池中
			for (Map<String, Object> map : taskList) {
				String assignee = map.get("ASSIGNEE") == null ? "Unclaimed"
						: map.get("ASSIGNEE").toString();
				String groupId = map.get("GROUP_ID").toString();
				Date startTime = (Date) map.get("CREATE_TIME");
				String taskId = map.get("TASK_ID").toString();
				if (this.saveTask(jedis, assignee, groupId, taskId, startTime,
						false)) {
					// 保存失败 ,存入到保存失败的map中，
					// trans.h("error_add_map", taskId);
				}
			}
		} catch (Exception e) {
			jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(jedisPool, jedis);
		}
		}
	}

	/**
	 * 查询指定用户的指定组中的任务个数
	 * 
	 * @param assignee
	 * @param groupId
	 * @return
	 */
	public long getSize(Object assignee, String groupId) {
		long size = 0;
		Jedis jedis = null;
		try {
			assignee = (assignee == null ? "Unclaimed" : assignee);
			jedis = getResource(RedisMsgType.TASK_WORK);
			size = jedis.zcard(assignee + "_" + groupId);
		} catch (Exception e) {
			jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			returnResource(jedisPool, jedis);
		}
		return size;
		//return checkTask(assignee,groupId);
	}

	/**
	 * 释放连接到连接池中
	 * 
	 * @param pool
	 * @param jedis
	 */
	public void returnResource(JedisPool pool, Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * 获取到任务列表中的第一个任务
	 * 
	 * @param assignee
	 * @param groupId
	 * @return taskId
	 */
	public long getFirst(Object assignee, String groupId) {
		long result = 0;
		assignee = (assignee == null ? "Unclaimed" : assignee);
		Jedis jedis = getResource(RedisMsgType.TASK_WORK);
		try {
			Set<Tuple> tuples = jedis.zrangeWithScores(
					assignee + "_" + groupId, 0, 0);
			Tuple[] tupleList = new Tuple[1];
			tupleList = tuples.toArray(tupleList);
			String value=tupleList[0].getElement();
			Double score=tupleList[0].getScore();
			if(value.length()>10){
				//
				jedis.zadd(assignee + "_" + groupId, Double.parseDouble(value),""+score);
				jedis.zremrangeByScore(assignee + "_" + groupId, value, value);
				return Math.round(score);
			}else{
				return Long.parseLong((tupleList[0].getElement()));
			}
		} catch (Exception e) {
			// jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			jedisPool.returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取未领取的订单
	 * 
	 * @param groupIdBase
	 * @param level
	 * @return
	 */
	public long getUnclaimedTask(String groupId, String level) {
		String mapName="Unclaimed"+"_"+groupId;
//		if(!checkExists(mapName)){
//			return this.getUnclaimedTaskWithLv(groupId, level);
//		}else{
			return this.getFirst(null, groupId);
//		}
	}
	/**
	 * 查找为领取的订单数量
	 * @param groupIdBase
	 * @param level
	 * @return
	 */
	public long getUnclaimedSize(String groupId,String level){
		String mapName="Unclaimed"+"_"+groupId;
		//if(!checkExists(mapName)){
			//return this.getUnclaimedSizeWithLv(groupId, level);
		//}else{
			return this.getSize(null, groupId);
		//}
	}
	/**
	 * 查找为领取的订单数量
	 * @param groupIdBase
	 * @param level
	 * @return
	 */
	public long getUnclaimedSizeWithLv(String groupIdBase, String level){
		String groupId = groupIdBase;
//		if (!StringUtils.isEmpty(level)) {
//			groupId += "_" + level;
//		}
		long size = this.getSize(null, groupId);
//		if(!StringUtils.isEmpty(level)){
//		if (size == 0 && !"0".equals(level)) {
//
//			// taskId等于0说明没有
//			int lv = Integer.parseInt(level);
//			lv--;
//
//			size=this.getUnclaimedSizeWithLv(groupIdBase, "" + lv);
//
//		}
//		}
		return size;
	}
	/**
	 * 判断redis是否存在该名字的数据
	 * @param name
	 * @return
	 */
	private boolean checkExists(String name){
		Jedis jedis=null;
		try{
			jedis=getResource(RedisMsgType.TASK_WORK);
			return jedis.exists(name);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(jedis!=null){
				jedisPool.returnResource(jedis);
			}
		}
		return false;
		
	}
	/**
	 * 通过级别和组别来找到未领取的订单
	 * @param groupIdBase
	 * @param level
	 * @return
	 */
	private long getUnclaimedTaskWithLv(String groupIdBase, String level) {
		String groupId = groupIdBase;

		if (!StringUtils.isEmpty(level)) {
			groupId += "_" + level;
		}
		long taskId = this.getFirst(null, groupId);
//		if (taskId == 0 && !StringUtils.isEmpty(level) &&!"0".equals(level)) {
//
//			// taskId等于0说明没有
//			int lv = Integer.parseInt(level);
//			lv--;
//
//			taskId=this.getUnclaimedTask(groupIdBase, "" + lv);
//
//		}
		return taskId;

	}
	/**
	 * 校正错误数据
	 * @param assignee
	 * @param groupId
	 * @return
	 */
	public Long checkTask(Object assignee,String groupId){
		Jedis jedis=null;
		//Transaction tranction=null;
		Long result=Long.MAX_VALUE;
		TaskService taskService=SpringContextHolder.getBean("taskService");
		try{
			String mapName=(assignee==null?"Unclaimed":assignee)+"_"+groupId;
			jedis=getResource(RedisMsgType.TASK_WORK);
			//tranction=jedis.multi();
			Long size=jedis.zcard(mapName);
//			Long sizelong=(size==null?0:size.get());
			//Long size=this.getSize(assignee, groupId);
			Set<Tuple> dataSet=jedis.zrangeWithScores(mapName, 0, (size==0?1:size)-1);
			List<String> taskList=new ArrayList<String>();
			for (Tuple tuple : dataSet) {
				String taskId=(Long.parseLong(tuple.getElement()))+"";
				Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
				if(taskList.contains(taskId) ||task==null || (assignee!=null && !assignee.equals(task.getAssignee()))){
					jedis.zrem(mapName, taskId);
				}else{
					taskList.add(taskId);
				}
			}
			//tranction.exec();
			result= jedis.zcard(mapName);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
			if(jedis!=null){
				//tranction.close();
				returnResource(jedisPool, jedis);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 保存任务记录
	 * @param userGroup
	 * @param assignee
	 * @param res
	 */
	public void saveTaskLog(String userGroup,String assignee,String res){
		Jedis jedis=null;
		try{
		String mapName="task_log_"+userGroup;
		Map<String,String> map=new HashMap<String, String>();
		map.put(assignee, res);
		jedis=getResource(RedisMsgType.TASK_WORK);
		jedis.hmset(mapName, map);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
			if(jedis!=null){
				//tranction.close();
				returnResource(jedisPool, jedis);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 
	 * @param jedis
	 * @param type
	 */
	public void setDefaultProp(Jedis jedis ,RedisMsgType type){
		jedis.select(type.ordinal());
	}
	
	/**
	 * 客服审核分区任务
	 * @param userId
	 * @param group
	 * @param groupId
	 * @return
	 */
	public JSONArray getCustomerGroupOrderTask(String userId,String group,String groupId) {
		JSONArray array=null;
		Date sysDate=new Date();
		sysDate.setTime(System.currentTimeMillis());
		List<Map<String, Object>> meselfData = null;
		List<Map<String, Object>> himselfData = null;
		if("gp_customer_service".equals(group)) {
			String weselfgyDrwingSql="SELECT AR.ID_, AC.URGENT_TYPE,AC.ID,TO_CHAR(AR.CREATE_TIME_,'yyyy-mm-dd hh:MM:ss') AS CREATE_TIME" + 
					"  FROM ACT_RU_TASK AR" + 
					"  LEFT JOIN ACT_CT_MAPPING AC" + 
					"    ON AR.PROC_INST_ID_ = AC.PROCINSTID" + 
					" WHERE AR.ASSIGNEE_ IS NULL" + 
					"   AND AC.REGIO IN" + 
					"       (SELECT SD.KEY_VAL" + 
					"          FROM SYS_DATA_DICT SD" + 
					"         WHERE SD.TRIE_ID =" + 
					"               (SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL = 'REGIO')" + 
					"           AND SD.TYPE_KEY = '"+groupId+"')" + 
					"   AND (AC.TASK_TYPE = '1' OR AC.TASK_TYPE IS NULL) AND AR.NAME_='客服审核'" + 
					"   ORDER BY AC.URGENT_TYPE, AR.CREATE_TIME_, decode(AC.ORDER_TYPE, 'OR3',  '1', 'OR2',  '2',  'OR4', '3', 'OR1',  '4', '5')";
			meselfData = jdbcTemplate.queryForList(weselfgyDrwingSql);
			String himeGpDrwingSql="SELECT AR.ID_, AC.URGENT_TYPE,AC.ID,TO_CHAR(AR.CREATE_TIME_,'yyyy-mm-dd hh:MM:ss') AS CREATE_TIME" + 
					"  FROM ACT_RU_TASK AR" + 
					"  LEFT JOIN ACT_CT_MAPPING AC" + 
					"    ON AR.PROC_INST_ID_ = AC.PROCINSTID" + 
					" WHERE AR.ASSIGNEE_ IS NULL" + 
					"   AND AC.REGIO IN" + 
					"       (SELECT SD.KEY_VAL" + 
					"          FROM SYS_DATA_DICT SD" + 
					"         WHERE SD.TRIE_ID =" + 
					"               (SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL = 'REGIO')" + 
					"           AND SD.TYPE_KEY != '"+groupId+"')" + 
					"   AND (AC.TASK_TYPE = '1' OR AC.TASK_TYPE IS NULL) AND AR.NAME_='客服审核'" + 
					"   ORDER BY AC.URGENT_TYPE, AR.CREATE_TIME_, decode(AC.ORDER_TYPE, 'OR3',  '1', 'OR2',  '2',  'OR4', '3', 'OR1',  '4', '5')";
			himselfData = jdbcTemplate.queryForList(himeGpDrwingSql);
			if(meselfData.size()>0) {
					String meCreateTime = (String) meselfData.get(0).get("CREATE_TIME");
					Date meDate = DateTools.strToDate(meCreateTime, DateTools.defaultFormat);
					String urgentType = (String) meselfData.get(0).get("URGENT_TYPE");
					if(urgentType!=null&&!"".equals(urgentType)) {
						array = returnTaskInfo(meselfData);
						return array;
					}else {
						if(meDate.getDay()!=sysDate.getDay()) {//判断 本组订单非当天订单 才进行本组 与其他组任务 进行时间比较
							//如果本组订单时间 小于其他组订单时间 则取本组订单
							array = returnTaskInfo(meselfData);
							return array;
						}else {
							if(himselfData.size()>0) {
								String himCreateTime = (String) himselfData.get(0).get("CREATE_TIME");
								Date himDate = DateTools.strToDate(himCreateTime, DateTools.defaultFormat);
								urgentType = (String) himselfData.get(0).get("URGENT_TYPE");
								if(urgentType!=null&&!"".equals(urgentType)) {
									array = returnTaskInfo(himselfData);
									return array;
								}else{
									if(meDate.getDay()<=himDate.getDay()){
										array = returnTaskInfo(meselfData);
										return array;
									}else{
										array = returnTaskInfo(himselfData);
										return array;
									}
								}
							}else {
								array = returnTaskInfo(meselfData);
								return array;
							}
						}
					}
			}
		}
		if(meselfData.size()==0) {
			if(himselfData.size()==0) {
				// 如果代码执行到这 代表已经没有任务了
				array =new JSONArray();
				array.add(0,null);
				return array;
			}else {
				array = returnTaskInfo(himselfData);
				return array;
			}
		}
		return array;
	}
	/**
	 * 获取分区分组任务
	 * @param userId 用户Id 
	 * @param group 任务Id
	 * @return
	 */
	public JSONArray getGroupOrderTask(String userId,String group,String groupId){
		JSONArray array=null;
		Date sysDate=new Date();
		sysDate.setTime(System.currentTimeMillis());
		List<Map<String, Object>> meselfData = null;
		List<Map<String, Object>> himselfData = null;
		if("gp_drawing".equals(group)) {
			String weselfgyDrwingSql="SELECT AR.ID_, AC.URGENT_TYPE,AC.ID,AR.CREATE_TIME_ AS CREATE_TIME" + 
					"  FROM ACT_RU_TASK AR" + 
					"  LEFT JOIN ACT_CT_MAPPING AC" + 
					"    ON AR.PROC_INST_ID_ = AC.PROCINSTID" + 
					" WHERE AR.ASSIGNEE_ IS NULL" + 
					"   AND AC.REGIO IN" + 
					"       (SELECT SD.KEY_VAL" + 
					"          FROM SYS_DATA_DICT SD" + 
					"         WHERE SD.TRIE_ID =" + 
					"               (SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL = 'REGIO')" + 
					"           AND SD.TYPE_KEY = '"+groupId+"')" + 
					"   AND (AC.TASK_TYPE = '1' OR AC.TASK_TYPE IS NULL) AND AR.NAME_='订单审绘' GROUP BY AR.ID_, AC.URGENT_TYPE,AC.ID,AR.CREATE_TIME_,AC.ORDER_TYPE HAVING DECODE(AC.ORDER_TYPE,'OR3',0,'OR4',0,'OR2',0,1)>=1" + 
					"   ORDER BY AC.URGENT_TYPE, AR.CREATE_TIME_ ";
			meselfData = jdbcTemplate.queryForList(weselfgyDrwingSql);
			String himeGpDrwingSql="SELECT AR.ID_, AC.URGENT_TYPE,AC.ID,AR.CREATE_TIME_ AS CREATE_TIME" + 
					"  FROM ACT_RU_TASK AR" + 
					"  LEFT JOIN ACT_CT_MAPPING AC" + 
					"    ON AR.PROC_INST_ID_ = AC.PROCINSTID" + 
					" WHERE AR.ASSIGNEE_ IS NULL" + 
					"   AND AC.REGIO IN" + 
					"       (SELECT SD.KEY_VAL" + 
					"          FROM SYS_DATA_DICT SD" + 
					"         WHERE SD.TRIE_ID =" + 
					"               (SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL = 'REGIO')" + 
					"           AND SD.TYPE_KEY != '"+groupId+"')" + 
					"   AND (AC.TASK_TYPE = '1' OR AC.TASK_TYPE IS NULL) AND AR.NAME_='订单审绘' GROUP BY AR.ID_, AC.URGENT_TYPE,AC.ID,AR.CREATE_TIME_,AC.ORDER_TYPE HAVING DECODE(AC.ORDER_TYPE,'OR3',0,'OR4',0,'OR2',0,1)>=1" + 
					"   ORDER BY AC.URGENT_TYPE, AR.CREATE_TIME_ ";
			himselfData = jdbcTemplate.queryForList(himeGpDrwingSql);
			if(meselfData.size()>0) {
					Date meDate = (Date) meselfData.get(0).get("CREATE_TIME");
					//Date meDate = DateTools.strToDate(meCreateTime, DateTools.defaultFormat);
					String urgentType = (String) meselfData.get(0).get("URGENT_TYPE");
					if(urgentType!=null&&!"".equals(urgentType)) {
						array = returnTaskInfo(meselfData);
						return array;
					}else {
						if(meDate.getDate()<sysDate.getDate()) {//判断 本组订单非当天订单 才进行本组 与其他组任务 进行时间比较
							//如果本组订单时间 小于其他组订单时间 则取本组订单
							array = returnTaskInfo(meselfData);
							return array;
						}else {
							if(himselfData.size()>0) {
								Date himDate = (Date) himselfData.get(0).get("CREATE_TIME");
								//Date himDate = DateTools.strToDate(himCreateTime, DateTools.defaultFormat);
								urgentType = (String) himselfData.get(0).get("URGENT_TYPE");
								if(urgentType!=null&&!"".equals(urgentType)) {
									array = returnTaskInfo(himselfData);
									return array;
								}else{
									if(meDate.getDate()<=himDate.getDate()){
										array = returnTaskInfo(meselfData);
										return array;
									}else{
										array = returnTaskInfo(himselfData);
										return array;
									}
								}
							}else {
								array = returnTaskInfo(meselfData);
								return array;
							}
						}
					}
			}
		}else {
			String weselfgyMaterialSql="SELECT AR.ID_," + 
					"       AC.URGENT_TYPE," + 
					"       AC.ID," + 
					"       TO_CHAR(AR.CREATE_TIME_, 'yyyy-mm-dd hh:MM:ss') AS CREATE_TIME" + 
					"       FROM ACT_CT_MAPPING AC" + 
					"          LEFT JOIN ACT_RU_TASK AR ON AC.PROCINSTID=AR.PROC_INST_ID_ WHERE AR.NAME_='物料审核' AND AC.REGIO='"+groupId+"' AND AR.ASSIGNEE_ IS NULL" + 
					"          ORDER BY AC.URGENT_TYPE," + 
					"          AR.CREATE_TIME_," + 
					"          decode(AC.ORDER_TYPE, 'OR3', '1', 'OR2', '2',  'OR4', '3', 'OR1', '4', '5')";
			meselfData = jdbcTemplate.queryForList(weselfgyMaterialSql);
			if(meselfData.size()>0) {
				array = returnTaskInfo(meselfData);
				return array;
			}else {
				array =new JSONArray();
				array.add(0,null);
				return array;
			}
		}
		if(meselfData.size()==0) {
			if(himselfData.size()==0) {
				// 如果代码执行到这 代表已经没有任务了
				array =new JSONArray();
				array.add(0,null);
				return array;
			}else {
				array = returnTaskInfo(himselfData);
				return array;
			}
		}
		return array;
	}

	public JSONArray returnTaskInfo(List<Map<String, Object>> meselfData) {
		JSONArray array;
		Object urgentType = meselfData.get(0).get("URGENT_TYPE");
		Object groupRears = meselfData.get(0).get("ID_");
		array = new JSONArray();
		array.add(0, groupRears);
		if(urgentType!=null&&!"".equals(urgentType)){
			array.add(1, jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
		}
		return array;
	}
	/*public JSONArray getGroupOrderTask(String userId,String group){
		JSONArray array=null;
		//查询当前用户所在的分组
		List<Map<String, Object>> groupre = jdbcTemplate.queryForList("SELECT ST.REAR FROM SYS_TASK ST WHERE ST.ID='"+userId+"'");
		String groupId=null;
		if(groupre.size()>0){
			groupId= (String) groupre.get(0).get("REAR");
		}
		String flowName="gp_drawing".equals(group)?"订单审绘":"物料审核";
		String where=null;
		//分出是否为cad 根据fileType 
		if("gp_drawing_cad".equals(group)){
			where = " AND MH.FILE_TYPE = '2' ";
		}else if("gp_drawing".equals(group)){
			where = " AND (MH.FILE_TYPE = '1' OR MH.FILE_TYPE IS NULL) ";
		}else{
			//物料审核不分cad
			where ="";
		}
		List<Map<String, Object>> rearInfo1 = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,AR.CREATE_TIME_,SH.URGENT_TYPE,CH.REGIO,AR.ID_,SH.ORDER_TYPE FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID = SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_ = AC.PROCINSTID WHERE AR.NAME_ = '"+flowName+"' AND CH.REGIO IN (SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID =(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL = 'REGIO')AND SD.TYPE_KEY = '"+groupId+"') "+where+" AND AR.ASSIGNEE_ IS NULL ORDER BY SH.URGENT_TYPE,AR.CREATE_TIME_,decode(SH.ORDER_TYPE,'OR3','1','OR2','2','OR4','3','OR1','4','5')");
		if(rearInfo1.size()==0){
			rearInfo1 = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,AR.CREATE_TIME_,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID = SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_ = AC.PROCINSTID WHERE AR.NAME_ = '"+flowName+"' AND CH.REGIO IN (SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID =(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL = 'REGIO')AND SD.TYPE_KEY = '"+groupId+"') "+where+" AND AR.ASSIGNEE_ IS NULL ORDER BY SH.URGENT_TYPE,AR.CREATE_TIME_");
		}
		if(rearInfo1.size()>0){
			// URGENT_TYPE 加急订单预先处理
			Object urgentType = rearInfo1.get(0).get("URGENT_TYPE");
			Object groupRears = rearInfo1.get(0).get("ID_");
			array = new JSONArray();
			array.add(0, groupRears);
			if(urgentType!=null&&!"".equals(urgentType)){
				array.add(1, jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
			}
			return array;
		}else{
			//获取所有分组 排除没有订单的分组
			String groupRears = null;
			if(!("gp_drawing_cad".equals(group))){
				List<Map<String, Object>> baseNum = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,AR.CREATE_TIME_,SH.URGENT_TYPE,CH.REGIO,AR.ID_,SH.ORDER_TYPE FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"'  AND AR.ASSIGNEE_ IS NULL "+where+" ORDER BY SH.URGENT_TYPE, AR.CREATE_TIME_,decode(SH.ORDER_TYPE,'OR3','1','OR2','2','OR4','3','OR1','4','5')");
				if(baseNum.size()==0){
					baseNum = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,AR.CREATE_TIME_,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"'  AND AR.ASSIGNEE_ IS NULL  ORDER BY SH.URGENT_TYPE,AR.CREATE_TIME_");//AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL)
				}
				if(baseNum.size()>0){
					Object urgentType = baseNum.get(0).get("URGENT_TYPE");
					groupRears = (String) baseNum.get(0).get("ID_");
					array =new JSONArray();
					array.add(0 , groupRears);
					if(urgentType!=null&&!"".equals(urgentType)){
						array.add(1 , jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
						return array;
					}
				}
				List<Map<String, Object>> groups = jdbcTemplate.queryForList("SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='TASK_GROUP') AND SD.KEY_VAL <> '"+groupId+"' ORDER BY SD.KEY_VAL ASC");
				for (Map<String, Object> map : groups) {
					if(groupRears==null){
						String groupTwo = (String)map.get("KEY_VAL");
						Integer rearInfo = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT SH.ORDER_CODE) FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID = SH.ID  LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL) ",Integer.class);//AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL)
						if(rearInfo==0){
							rearInfo = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT SH.ORDER_CODE) FROM SALE_HEADER SH LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN SALE_ITEM SI ON SH.ID=SI.PID LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID  LEFT JOIN ACT_RU_TASK AR  ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"')",Integer.class);
						}
						if(rearInfo>0){
							Integer nums = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT SH.ORDER_CODE) FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NOT NULL AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL) AND AR.ASSIGNEE_ IN (SELECT ST.ID FROM SYS_TASK ST WHERE ST.REAR='"+groupTwo+"' )",Integer.class);
							List<Map<String, Object>> baseNum = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,AR.CREATE_TIME_,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NULL AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL) ORDER BY SH.URGENT_TYPE, AR.CREATE_TIME_");
							if(nums==0){
								nums = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT SH.ORDER_CODE) FROM SALE_HEADER SH LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN SALE_ITEM SI ON SH.ID=SI.PID LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID  LEFT JOIN ACT_RU_TASK AR  ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NOT NULL AND AR.ASSIGNEE_ IN (SELECT ST.ID FROM SYS_TASK ST WHERE ST.REAR='"+groupTwo+"' )",Integer.class);
								baseNum = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,AR.CREATE_TIME_,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NULL  ORDER BY SH.URGENT_TYPE,AR.CREATE_TIME_");//AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL)
							}
							if("gp_drawing".equals(group)){
								Integer userRear = jdbcTemplate.queryForObject("SELECT COUNT(ST.ID) FROM SYS_USER SU LEFT JOIN SYS_TASK ST ON SU.ID=ST.ID LEFT JOIN SYS_USER_ROLE SUR ON SU.ID=SUR.USER_ID LEFT JOIN SYS_ROLE SR ON SUR.ROLE_ID=SR.ID WHERE SR.REMARK='"+group+"' AND ST.REAR='"+groupTwo+"'",Integer.class);
								if((rearInfo-(userRear * 2))>0){
									if(rearInfo-nums-(userRear *2-nums)-Math.abs((nums-(rearInfo-baseNum.size())))>0){//nums.size()-((userRear.size()*2)-(rearInfo.size()-nums.size()))>0
										if(baseNum.size()>0){
											Object urgentType = baseNum.get(0).get("URGENT_TYPE");
											groupRears = (String) baseNum.get(0).get("ID_");
											array =new JSONArray();
											if(urgentType!=null&&!"".equals(urgentType)){
												array.add(0 , groupRears);
												array.add(1 , jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
												return array;
											}else{
												array =new JSONArray();
												array.add(0 , groupRears);
											}
										}
										
										for (Map<String, Object> num : baseNum) {
											Date orderDate = (Date)num.get("CREATE_TIME");
											Object urgentType = num.get("URGENT_TYPE");
											if(urgentType!=null&&!"".equals(urgentType)){
												array =new JSONArray();
												array.add(0,(String)num.get("ID_"));
												array.add(1,jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
												return array;
											}else{
												long timer = orderDate.getTime();
												if(timer<contrastTime){
													contrastTime=timer;
													groupRears=(String)num.get("ID_");
												}
											}
										}
									}
								}else{
									continue;
								}
							}else{
								//(rearInfo.size()-nums.size())-(30-nums.size())>0
								if(rearInfo-((rearInfo-baseNum.size())-nums)>30){
									if(baseNum.size()>0){
										Object urgentType = baseNum.get(0).get("URGENT_TYPE");
										groupRears = (String) baseNum.get(0).get("ID_");
										array =new JSONArray();
										if(urgentType!=null&&!"".equals(urgentType)){
											array.add(0 , groupRears);
											array.add(1 , jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
											return array;
										}else{
											array.add(0 , groupRears);
										}
									}
									for (Map<String, Object> num : baseNum) {
										Date orderDate = (Date)num.get("CREATE_TIME");
										Object urgentType = num.get("URGENT_TYPE");
										if(urgentType!=null&&!"".equals(urgentType)){
											array =new JSONArray();
											array.add(0,(String)num.get("ID_"));
											array.add(1,jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
											return array;
										}else{
											long timer = orderDate.getTime();
											if(timer<contrastTime){
												contrastTime=timer;
												groupRears=(String)num.get("ID_");
											}
										}
									}
								}else{
									continue;
								}
							}
						}
					}else{
						array =new JSONArray();
						array.add(0,groupRears);
						return array;
					}
				}
			}
		}
		return array;
	}*/
	/**
	 * 获取分区分组任务
	 * @param userId 用户Id 
	 * @param group 任务Id
	 * @return
	 */
	/*public JSONArray getGroupOrderTask(String userId,String group){
		JSONArray array=null;
		//查询当前用户所在的分组
		String groupId = jdbcTemplate.queryForObject("SELECT ST.REAR FROM SYS_TASK ST WHERE ST.ID='"+userId+"'",String.class);
		//获取当前分组的所有地区存储到list集合
		//List<String> groupRears=new ArrayList<String>();
		Jedis jedis = getResource(RedisMsgType.TASK_WORK);
		long contrastTime=Long.MAX_VALUE;
		String groupRears=null;
		String flowName="gp_drawing".equals(group)?"订单审绘":"物料审核";
		List<Map<String, Object>> rearInfo1 = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,SH.CREATE_TIME,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID = SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_ = AC.PROCINSTID WHERE AR.NAME_ = '订单审绘' AND CH.REGIO IN (SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID =(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL = 'REGIO')AND SD.TYPE_KEY = '"+groupId+"') AND (MH.FILE_TYPE = '1' OR MH.FILE_TYPE IS NULL) ORDER BY SH.URGENT_TYPE,SH.CREATE_TIME");
		try{
			Set<String> tasks = jedis.zrange("Unclaimed_"+group, 0, Long.MAX_VALUE);
			if(tasks.size()>0){
				for (String task : tasks) {
					if(rearInfo1.size()>0){
						List<Map<String, Object>> groupInfo = jdbcTemplate.queryForList("SELECT SH.CREATE_TIME, CH.REGIO, SH.URGENT_TYPE FROM SALE_HEADER SH LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG=CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID=AC.ID LEFT JOIN ACT_RU_TASK AR ON AC.PROCINSTID=AR.PROC_INST_ID_ WHERE AR.ID_='"+task+"'");
						if(groupInfo.size()==0){
							groupInfo=jdbcTemplate.queryForList("SELECT SH.CREATE_TIME,CH.REGIO,SH.URGENT_TYPE FROM ACT_RU_TASK AR LEFT JOIN ACT_CT_MAPPING AC ON AR.PROC_INST_ID_=AC.PROCINSTID LEFT JOIN SALE_ITEM SI ON SI.ID=AC.ID LEFT JOIN SALE_HEADER SH ON SH.ID=SI.PID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG=CH.KUNNR WHERE AR.ID_='"+task+"'");
						}
						if(groupInfo.size()>0){
							Date orderDate = (Date)groupInfo.get(0).get("CREATE_TIME");
							Object regio = groupInfo.get(0).get("REGIO");
							Object urgentType = groupInfo.get(0).get("URGENT_TYPE");
							for (Map<String, Object> map : rearInfo1) {
								String rear = (String)map.get("KEY_VAL");
								if(rear!=null&&!"".equals(rear)){
									if(rear.equals(regio)){
										if(urgentType!=null&&!"".equals(urgentType)){
											array =new JSONArray();
											array.add(0,task);
											array.add(1,jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
											return array;
										}else{
											long timer = orderDate.getTime();
											if(timer<contrastTime){
												contrastTime=timer;
												groupRears=task;
											}
										}
									}
								}
							}
						}
					}
				
			}
			//if(groupRears==null){//此组没有订单   需要从其他分组中获取订单
			//获取所有分组 排除没有订单的分组
			if(!("gp_drawing_cad".equals(group))){
				List<Map<String, Object>> groups = jdbcTemplate.queryForList("SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='TASK_GROUP') AND SD.KEY_VAL <> '"+groupId+"' ORDER BY SD.KEY_VAL ASC");
				for (Map<String, Object> map : groups) {
					if(groupRears==null){
						contrastTime=Long.MAX_VALUE;
						String groupTwo = (String)map.get("KEY_VAL");
						List<Map<String, Object>> rearInfo = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,SH.CREATE_TIME,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID = SH.ID  LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL)");
						if(rearInfo.size()==0){
							rearInfo = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,SH.CREATE_TIME,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN SALE_ITEM SI ON SH.ID=SI.PID LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID  LEFT JOIN ACT_RU_TASK AR  ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"')");
						}
						if(rearInfo.size()>0){
							List<Map<String, Object>> nums = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,SH.CREATE_TIME,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NOT NULL AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL) AND AR.ASSIGNEE_ IN (SELECT ST.ID FROM SYS_TASK ST WHERE ST.REAR='"+groupTwo+"' )");
							List<Map<String, Object>> baseNum = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,SH.CREATE_TIME,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NULL AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL) ");
							if(nums.size()==0){
								nums = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,SH.CREATE_TIME,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN SALE_ITEM SI ON SH.ID=SI.PID LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID  LEFT JOIN ACT_RU_TASK AR  ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NOT NULL AND AR.ASSIGNEE_ IN (SELECT ST.ID FROM SYS_TASK ST WHERE ST.REAR='"+groupTwo+"' )");
								baseNum = jdbcTemplate.queryForList("SELECT DISTINCT SH.ORDER_CODE,SH.CREATE_TIME,SH.URGENT_TYPE,CH.REGIO,AR.ID_ FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SI.PID=SH.ID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AR.PROC_INST_ID_=AC.PROCINSTID WHERE AR.NAME_ ='"+flowName+"' AND CH.REGIO IN(SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='REGIO') AND SD.TYPE_KEY='"+groupTwo+"') AND AR.ASSIGNEE_ IS NULL AND (MH.FILE_TYPE='1' OR MH.FILE_TYPE IS NULL) ");

							}
							if("gp_drawing".equals(group)){
								List<Map<String, Object>> userRear = jdbcTemplate.queryForList("SELECT ST.TASK_NUM,ST.ID FROM SYS_USER SU LEFT JOIN SYS_TASK ST ON SU.ID=ST.ID LEFT JOIN SYS_USER_ROLE SUR ON SU.ID=SUR.USER_ID LEFT JOIN SYS_ROLE SR ON SUR.ROLE_ID=SR.ID WHERE SR.REMARK='"+group+"' AND ST.REAR='"+groupTwo+"'");
								if((rearInfo.size()-(userRear.size()*2))>0){
									//nums.size()-((userRear.size()*2)-(rearInfo.size()-nums.size()))>0
									//(rearInfo.size()-(rearInfo.size()-nums.size()))-((userRear.size()*2)-(rearInfo.size()-nums.size()))>0
									//(rearInfo.size()-baseNum.size())-((userRear.size()*2)-nums.size())>0
									if(rearInfo.size()-nums.size()-(userRear.size()*2-nums.size())-Math.abs((nums.size()-(rearInfo.size()-baseNum.size())))>0){//nums.size()-((userRear.size()*2)-(rearInfo.size()-nums.size()))>0
										for (Map<String, Object> num : baseNum) {
											Date orderDate = (Date)num.get("CREATE_TIME");
											Object urgentType = num.get("URGENT_TYPE");
											if(urgentType!=null&&!"".equals(urgentType)){
												array =new JSONArray();
												array.add(0,(String)num.get("ID_"));
												array.add(1,jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
												return array;
											}else{
												long timer = orderDate.getTime();
												if(timer<contrastTime){
													contrastTime=timer;
													groupRears=(String)num.get("ID_");
												}
											}
										}
									}
								}else{
									continue;
								}
							}else{
								//(rearInfo.size()-nums.size())-(30-nums.size())>0
								if(rearInfo.size()-((rearInfo.size()-baseNum.size())-nums.size())>30){
									for (Map<String, Object> num : baseNum) {
										Date orderDate = (Date)num.get("CREATE_TIME");
										Object urgentType = num.get("URGENT_TYPE");
										if(urgentType!=null&&!"".equals(urgentType)){
											array =new JSONArray();
											array.add(0,(String)num.get("ID_"));
											array.add(1,jdbcTemplate.queryForObject("SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='URGENT_TYPE') AND SD.KEY_VAL='"+(String)urgentType+"' ", String.class));
											return array;
										}else{
											long timer = orderDate.getTime();
											if(timer<contrastTime){
												contrastTime=timer;
												groupRears=(String)num.get("ID_");
											}
										}
									}
								}else{
									continue;
								}
							}
						}
					}else{
						array =new JSONArray();
						array.add(0,groupRears);
						return array;
					}
				}
			}
		}
		if(groupRears!=null){
			array =new JSONArray();
			array.add(0,groupRears);
		}
		return array;
		}catch(Exception e){
			e.printStackTrace();
			returnResource(jedisPool, jedis);
		}finally{
			returnResource(jedisPool, jedis);
		}
		return null;
	}
	/**
	 * 获取加急订单任务
	 * @param taskNode
	 * @return
	 */
	public JSONArray getJiaJiOrderTask(String taskNode){
		JSONArray array=null;
		String taskName="gp_hole_examine".equals(taskNode)?"孔位审核":"gp_shiftcount".equals(taskNode)?"移门算料":"gp_drawing_2020".equals(taskNode)?"2020绘图":"gp_drawing_imos".equals(taskNode)?"IMOS绘图":"gp_valuation".equals(taskNode)?"价格审核":null;
		if(taskName!=null){
			List<Map<String, Object>> tasks = jdbcTemplate.queryForList("SELECT AR.ID_,SD.DESC_ZH_CN,SH.ORDER_CODE,SH.CREATE_TIME FROM SALE_HEADER SH LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AC.PROCINSTID = AR.PROC_INST_ID_ LEFT JOIN SYS_DATA_DICT SD ON SH.URGENT_TYPE = SD.KEY_VAL WHERE SH.URGENT_TYPE IS NOT NULL AND AR.NAME_='"+taskName+"' AND AR.ASSIGNEE_ IS NULL ORDER BY SH.CREATE_TIME");
			if(tasks.size()==0){
				tasks = jdbcTemplate.queryForList("SELECT DISTINCT AR.ID_,SD.DESC_ZH_CN,SH.ORDER_CODE,SH.CREATE_TIME FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SH.ID=SI.PID LEFT JOIN ACT_CT_MAPPING AC ON SI.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AC.PROCINSTID = AR.PROC_INST_ID_ LEFT JOIN SYS_DATA_DICT SD ON SH.URGENT_TYPE = SD.KEY_VAL WHERE SH.URGENT_TYPE IS NOT NULL AND AR.NAME_='"+taskName+"' AND AR.ASSIGNEE_ IS NULL ORDER BY SH.CREATE_TIME");
			}
			if(tasks.size()>0){
				array=new JSONArray();
				String taskId = (String) tasks.get(0).get("ID_");
				String descZHCN = (String) tasks.get(0).get("DESC_ZH_CN");
				array.add(0, taskId);
				array.add(1, descZHCN);
			}
		}
		return array;
	}
	/**
	 * 获取补单任务
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public JSONArray getOrderTask(String userId, String groupId) {
		JSONArray array=null;
		Date sysDate=new Date();
		sysDate.setTime(System.currentTimeMillis());
		List<Map<String, Object>> meselfData = null;
		List<Map<String, Object>> himselfData = null;
		String weselfgyDrwingSql="SELECT AR.ID_,ac.order_type, AC.URGENT_TYPE,AC.ID,TO_CHAR(AR.CREATE_TIME_,'yyyy-mm-dd hh:MM:ss') AS CREATE_TIME " + 
				"            FROM ACT_RU_TASK AR " + 
				"            LEFT JOIN ACT_CT_MAPPING AC " + 
				"              ON AR.PROC_INST_ID_ = AC.PROCINSTID " + 
				"           WHERE AR.ASSIGNEE_ IS NULL " + 
				"             AND (AC.TASK_TYPE = '1' OR AC.TASK_TYPE IS NULL) AND AR.NAME_='订单审绘' AND AC.ORDER_TYPE IN ('OR3','OR4')" + 
				"             ORDER BY AC.CREATE_TIME";
		meselfData = jdbcTemplate.queryForList(weselfgyDrwingSql);
		if(meselfData.size()>0) {
			String meCreateTime = (String) meselfData.get(0).get("CREATE_TIME");
			Date meDate = DateTools.strToDate(meCreateTime, DateTools.defaultFormat);
			String urgentType = (String) meselfData.get(0).get("URGENT_TYPE");
			array = returnTaskInfo(meselfData);
			return array;
		}
		if(meselfData.size()==0) {
			array =new JSONArray();
			array.add(0,null);
			return array;
		}
		return array;
	}
}
