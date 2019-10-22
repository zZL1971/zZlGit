package com.mw.framework.quartz.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rubyeye.xmemcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mw.framework.bean.Constants;
import com.mw.framework.context.SpringContextHolder;
import com.webservice.IRocoService;

public class TaskCacheQuartzJob {
	private static final Logger logger = LoggerFactory
			.getLogger(TaskCacheQuartzJob.class);

	public void run() {
		try {
			//获取缓存器
//			MemcachedClient memcachedClient = SpringContextHolder
//					.getBean("memcachedClient");
//			JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
//			
//			String sql = "select a.assignee_ as assignee, b.group_id_ as group_id, a.id_ as task_id from act_ru_task a, act_ru_identitylink b, act_hi_actinst aha, act_ct_mapping acm where a.id_ = b.task_id_ and a.id_ = aha.task_id_ and aha.proc_inst_id_ = acm.procinstid and a.name_<>'订单审绘重审' ";
//			
//			List<Map<String, Object>> queryList = jdbcTemplate.queryForList(sql);
//			
//			//从缓存器中获取数据，如果有则提取，没有则新建
//			//任务节点缓存结构为：Map<String,Map<String,List<String>>> 参数为  Map<assignee,Map<groupId,List<TaskId>>>
//			//将查询出来的结果直接覆盖缓存
//			Map<String, Map<String, Map<String,String>>> cacheMap = new HashMap<String, Map<String,Map<String,String>>>();
//			
//			for (Map<String, Object> map : queryList) {
//				//获取参数
//				String assignee=(map.get("assignee")==null?"Unclaimed":(String)map.get("assignee"));
//				String groupId=(String)map.get("group_id");
//				String taskId=(String)map.get("task_id");
//				
//				//赋值
//				Map<String,Map<String,String>> assigneeTaskMap;
//				Map<String,String> taskList;
//				if(cacheMap.get(assignee)==null){
//					cacheMap.put(assignee, new HashMap<String,Map<String,String>>());
//				}
//				assigneeTaskMap=cacheMap.get(assignee);
//				if(cacheMap.get(assignee).get(groupId)==null){
//					cacheMap.get(assignee).put(groupId, new HashMap<String,String>());
//				}
//				taskList=assigneeTaskMap.get(groupId);
//				
//				if(taskList.get(taskId)!=null){
//					continue;
//				}else{
//					taskList.put(taskId, taskId);
//				}
//			}
//			memcachedClient.set(Constants.CACHE_TASK_LIST,0,cacheMap);
			org.apache.cxf.jaxws.JaxWsProxyFactoryBean factoryBean = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();  
			factoryBean.create(IRocoService.class);
	        factoryBean.setAddress("http://172.16.3.239:8080/webservice/rocoService");  
	        IRocoService rocoService = (IRocoService)factoryBean.create();
	        rocoService.resetTaskMap();
	        System.out.println("reset Task Map ");
	        
//          factoryBean = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();  
//			factoryBean.create(IRocoService.class);
//			factoryBean.create(IRocoService.class);
//	        factoryBean.setAddress("http://172.16.3.220:8080/webservice/rocoService");  
//	        rocoService = (IRocoService)factoryBean.create();  
//	        rocoService.resetTaskMap();
//	        System.out.println("reset Task Map ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
