package com.main.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.ActOperationLogDao;
import com.mw.framework.domain.ActOperationLog;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.ZStringUtils;

import oracle.jdbc.OracleTypes;

@Service
@Transactional
public class TaskLogService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ActOperationLogDao actOperationLogDao;

	/**
	 * 记录完成任务
	 * 
	 * @param pid
	 * @param actId
	 * @param groupId
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void complete(final String taskId,final String groupId) {
		final String sql = "{call SAVE_ACT_LOG_DATA(?,?,?,?,?)}";
		RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
		try{
		Map<String,Object> resultList = (Map<String,Object>) jdbcTemplate.execute(
				new CallableStatementCreator() {

					@Override
					public CallableStatement createCallableStatement(
							Connection arg0) throws SQLException {
						String storedProc = sql;// 调用的sql
						CallableStatement cs = arg0.prepareCall(storedProc);
						cs.setString(1, taskId);// 设置输入参数的值
						cs.setString(2, groupId);
						cs.registerOutParameter(3, OracleTypes.VARCHAR);
						cs.registerOutParameter(4, OracleTypes.VARCHAR);
						cs.registerOutParameter(5, OracleTypes.CURSOR);// 注册输出参数的类型
						return cs;
					}
				}, new CallableStatementCallback() {

					@Override
					public Object doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						// TODO Auto-generated method stub
						cs.execute();
						Map<String, Object> result = new HashMap<String, Object>();
						String flag = "";
						String msg = "";
						List resultsMap = new ArrayList();
						flag = cs.getString(3);
						msg = cs.getString(4);
						if(!"N".equals(flag)) {
							ResultSet rs = (ResultSet) cs.getObject(5);// 获取游标一行的值
							while (rs.next()) {// 转换每行的返回值到Map中
								Map rowMap = new HashMap();
								rowMap.put("assignee", rs.getString("assignee"));
								rowMap.put("userName", rs.getString("user_name"));
								rowMap.put("userGroup", rs.getString("user_group"));
								rowMap.put("orderCount", rs.getInt("order_count"));
								rowMap.put("punishCount", rs.getInt("punish_count"));
								rowMap.put("reCount", rs.getInt("re_count"));
								rowMap.put("rowsNumber", rs.getInt("rows_number"));
								rowMap.put("sjNum", rs.getInt("sj_num"));
								rowMap.put("target", rs.getInt("target"));
								rowMap.put("logDate", rs.getObject("log_date"));
								rowMap.put("completeSquare",
										rs.getFloat("complete_square"));
								rowMap.put("extCount", rs.getFloat("ext_count"));
								resultsMap.add(rowMap);
							}
							rs.close();
							result.put("flag", flag);
							result.put("msg", msg);
							result.put("result", resultsMap);
						}
						return result;
					}

				});
		String flag=String.valueOf(resultList.get("flag"));
		String msg=String.valueOf(resultList.get("msg"));
		List<Map<String,Object>> result= (List)resultList.get("result");
		jdbcTemplate.update("insert into TASK_EXECUTE_LOG(ID,CREATE_TIME,FLAG,MSG,RES,TASKID) VALUES(SYS_GUID(),SYSDATE,?,?,?,?)",new Object[]{flag,msg,JSONObject.toJSONString(result),taskId});
		if(result!=null && result.size()>0){
			redisUtils.saveTaskLog(String.valueOf(result.get(0).get("userGroup")), String.valueOf(result.get(0).get("assignee")), JSONObject.toJSONString(result));
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Message completeN(final String taskId,String groupId) {
		Message msg = null;
		net.sf.json.JSONObject obj=new net.sf.json.JSONObject();
		Map<String, String> messageMap=new HashMap<String, String>();
		messageMap.put("ERR_INFO", "统计成功");
		messageMap.put("SUCCESS", "true");
		if(taskId==null&&"".equals(taskId)) {
			messageMap.put("ERR_INFO", "执行任务ID 为空值");
			messageMap.put("SUCCESS", "false");
			obj.putAll(messageMap);
			msg = new Message(obj);
			return msg;
		}
		List<Map<String, Object>> taskDetail = jdbcTemplate.queryForList("SELECT ARI.GROUP_ID_," + 
				"           AR.ASSIGNEE_," + 
				"           AC.ID," + 
				"           substr(AR.PROC_DEF_ID_, 0, instr(AR.PROC_DEF_ID_, ':') - 1) AS V_PROC_KEY," + 
				"           AR.PROC_DEF_ID_"
				+ " FROM ACT_RU_TASK AR, ACT_RU_IDENTITYLINK ARI, ACT_CT_MAPPING AC" + 
				"     WHERE AR.ID_ = ARI.TASK_ID_" + 
				"       AND AC.PROCINSTID = AR.PROC_INST_ID_" + 
				"       AND AR.ID_ = ?",taskId);
		if(taskDetail.size()<=0) {
			messageMap.put("ERR_INFO", "无 此执行任务 的详细信息");
			messageMap.put("SUCCESS", "false");
			obj.putAll(messageMap);
			msg = new Message(obj);
			return msg;
		}
		String group = ZStringUtils.resolverStr(taskDetail.get(0).get("GROUP_ID_"));
		String assignee = ZStringUtils.resolverStr(taskDetail.get(0).get("ASSIGNEE_"));
		String id = ZStringUtils.resolverStr(taskDetail.get(0).get("ID"));
		String procKey = ZStringUtils.resolverStr(taskDetail.get(0).get("V_PROC_KEY"));
		//String procDefId = ZStringUtils.resolverStr(taskDetail.get(0).get("PROC_DEF_ID_"));
		List<Map<String, Object>> groupDetail = jdbcTemplate.queryForList("SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD,SYS_TRIE_TREE ST WHERE SD.TRIE_ID = ST.ID AND ST.KEY_VAL='ACT_GROUP' AND SD.TYPE_KEY=? AND SD.DESC_EN_US=?",procKey,group);
		if(groupDetail.size()<=0) {
			messageMap.put("ERR_INFO", "未授权 分组 信息");
			messageMap.put("SUCCESS", "false");
			obj.putAll(messageMap);
			msg = new Message(obj);
			return msg;
		}
		String groupKeyVal = ZStringUtils.resolverStr(groupDetail.get(0).get("KEY_VAL"));
		double vTyar=0.0;
		int posexNum=0;
		int sjNum=0;
		if("subProductQuotation".equals(procKey)) {
			Integer times = jdbcTemplate.queryForObject("SELECT COUNT(1) ACT_MANAGER_LOG AM WHERE AM.ID='"+id+"' AND AM.TASK_NAME='"+groupId+"'", Integer.class);
			if(times <= 0) {
				List<Map<String, Object>> orderDetail = jdbcTemplate.queryForList("SELECT SUM(NVL(MH.ZZTYAR,0)) ZZTYAR,SUM(CASE WHEN SH.ORDER_TYPE NOT IN ('OR2','OR3','OR4','OR5','OR6') THEN 1 ELSE 0 END) AS POSEX_NUM,SUM(CASE WHEN SH.ORDER_TYPE IN ('OR2') THEN 1 ELSE 0 END) AS SJ_NUM FROM MATERIAL_HEAD MH ,SALE_ITEM SI,SALE_HEADER SH WHERE SH.ID=SI.PID AND SI.MATERIAL_HEAD_ID = MH.ID AND SI.ID=?", id);
				if(orderDetail.size()>0) {
					vTyar = Double.parseDouble(String.valueOf(orderDetail.get(0).get("ZZTYAR")));
					posexNum = Integer.parseInt(String.valueOf(orderDetail.get(0).get("POSEX_NUM")));
					sjNum = Integer.parseInt(String.valueOf(orderDetail.get(0).get("SJ_NUM")));
					if(vTyar>0) {
						int result = jdbcTemplate.update("INSERT INTO ACT_MANAGER_LOG VALUES (?, ?)",id,groupId);
						if(result<0) {
							messageMap.put("ERR_INFO", "保存 单个行项信息失败");
							messageMap.put("SUCCESS", "false");
							obj.putAll(messageMap);
							msg = new Message(obj);
							return msg;
						}
						jdbcTemplate.update("UPDATE ACT_DAY_LOG AD SET AD.ROWS_NUMBER=NVL(AD.ROWS_NUMBER,0)+"+posexNum+" WHERE AD.ASSIGNEE=? AND AD.LOG_DATE=TO_DATE(TO_CHAR(SYSDATE, 'yyyy-mm-dd'), 'yyyy-mm-dd') AND AD.USER_GROUP=?",assignee,groupKeyVal);
					}
				}
			}
		}else{
			List<Map<String, Object>> saleItemDetail = jdbcTemplate.queryForList("SELECT SI.ID FROM SALE_ITEM SI WHERE SI.PID=?",id);
			if(saleItemDetail.size()<=0) {
				messageMap.put("ERR_INFO", "找不到订单Id: '"+id+"' 信息");
				messageMap.put("SUCCESS", "false");
				obj.putAll(messageMap);
				msg = new Message(obj);
				return msg;
			}
			for (Map<String, Object> saleItem : saleItemDetail) {
				String saleItemId = ZStringUtils.resolverStr(saleItem.get("ID"));
				Integer timer = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM ACT_MANAGER_LOG AM WHERE AM.ID=? AND AM.TASK_NAME=?",new Object[] {saleItemId,groupId},Integer.class);
				if(timer<=0) {
					List<Map<String, Object>> orderSaleDetail = jdbcTemplate.queryForList("SELECT SUM(NVL(MH.ZZTYAR,0)) AS ZZTYAR,SUM(CASE WHEN SH.ORDER_TYPE NOT IN ('OR2','OR3','OR4','OR5','OR6') THEN 1 ELSE 0 END) AS POSEX_NUM,SUM(CASE WHEN SH.ORDER_TYPE IN ('OR2') THEN 1 ELSE 0 END) AS SJ_NUM FROM MATERIAL_HEAD MH, SALE_ITEM SI,SALE_HEADER SH WHERE SH.ID=SI.PID AND SI.MATERIAL_HEAD_ID = MH.ID AND SI.ID=?",saleItemId);
					if(orderSaleDetail.size()<=0) {
						messageMap.put("ERR_INFO", "找不到行项Id: '"+saleItemId+"' 信息");
						messageMap.put("SUCCESS", "false");
						obj.putAll(messageMap);
						msg = new Message(obj);
						return msg;
					}
					vTyar += Double.parseDouble(String.valueOf(orderSaleDetail.get(0).get("ZZTYAR")));
					posexNum += Integer.parseInt(String.valueOf(orderSaleDetail.get(0).get("POSEX_NUM")));
					sjNum += Integer.parseInt(String.valueOf(orderSaleDetail.get(0).get("SJ_NUM")));
					int result = jdbcTemplate.update("INSERT INTO ACT_MANAGER_LOG VALUES(?,?)", saleItemId,groupId);
					if(result<0) {
						messageMap.put("ERR_INFO", "更新失败");
						messageMap.put("SUCCESS", "false");
						obj.putAll(messageMap);
						msg = new Message(obj);
						return msg;
					}
				}
			}
			if(posexNum>0||sjNum>0) {
				jdbcTemplate.update("UPDATE ACT_DAY_LOG AD SET AD.ORDER_COUNT=NVL(AD.ORDER_COUNT,0)+1,AD.ROWS_NUMBER=NVL(AD.ROWS_NUMBER,0)+"+posexNum+" WHERE AD.ASSIGNEE=? AND AD.LOG_DATE=TO_DATE(TO_CHAR(SYSDATE, 'yyyy-mm-dd'), 'yyyy-mm-dd') AND AD.USER_GROUP=?",assignee,groupKeyVal);
			}
		}
		if(vTyar>0) {
			jdbcTemplate.update("UPDATE ACT_DAY_LOG AD SET AD.COMPLETE_SQUARE=NVL(AD.COMPLETE_SQUARE,0)+"+vTyar+" WHERE AD.ASSIGNEE=? AND AD.LOG_DATE=TO_DATE(TO_CHAR(SYSDATE, 'yyyy-mm-dd'), 'yyyy-mm-dd') AND AD.USER_GROUP=?",assignee,groupKeyVal);
			obj.putAll(messageMap);
			msg = new Message(obj);
		}
		return msg;
	}
	/**
	 * 记录开始任务
	 * 
	 * @param pid
	 * @param actId
	 * @param actName
	 * @param taskId
	 */
	public void create(String pid, String actId, String actName, String taskId) {
		ActOperationLog actOperationLog = new ActOperationLog();
		actOperationLog.setActId(actId);
		actOperationLog.setActName(actName);
		actOperationLog.setTaskId(taskId);
		actOperationLog.setPid((String) pid);
		actOperationLog.setStartTime(new Date());

		actOperationLogDao.save(actOperationLog);

	}

}
