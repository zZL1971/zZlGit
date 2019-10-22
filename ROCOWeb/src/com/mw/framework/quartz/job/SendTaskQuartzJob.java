package com.mw.framework.quartz.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.manager.MyGoodsManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.domain.SendTaskLog;
import com.webservice.IRocoService;

public class SendTaskQuartzJob {
	private static final Logger logger = LoggerFactory
			.getLogger(SendTaskQuartzJob.class);

	/**
	 * 删除冗余数据，（商品没有下订单，我的商品界面只做标记删除，因为商品数据是在订单激活的时候才删除的）
	 */
	public void run() {
		if(true){
			return ;
		}
		JdbcTemplate jdbcTemplate=SpringContextHolder.getBean("jdbcTemplate");
		
		List<Map<String,Object>> taskList239=jdbcTemplate.queryForList(" select * from send_task where status<>0 and assignee='220'");
		ArrayList<SendTaskLog> taskList1=new ArrayList<SendTaskLog>();
		for (Map<String, Object> map : taskList239) {
			taskList1.add(new SendTaskLog(map.get("TASK_ID").toString(), map.get("GROUP_ID")==null?"":map.get("GROUP_ID").toString(), map.get("ASSIGNEE")==null?"": map.get("ASSIGNEE").toString(), ((BigDecimal)map.get("TASK_TYPE")).intValue(), ((BigDecimal)map.get("STATUS")).intValue(),map.get("task_create_time").toString()));
		}
		
		if(taskList1!=null && taskList1.size()>0){
			org.apache.cxf.jaxws.JaxWsProxyFactoryBean factoryBean = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
		
			factoryBean.create(IRocoService.class);
			factoryBean.setAddress("http://172.16.3.239:8080/webservice/rocoService");  
          
			IRocoService rocoService = (IRocoService)factoryBean.create();
			rocoService.sendTaskList(taskList1);
			System.out.println("send task1 Size="+taskList1.size());
		}
		
//		List<Map<String,Object>> taskList220=jdbcTemplate.queryForList(" select * from send_task where status<>0 and assignee='239'");
//		ArrayList<SendTaskLog> taskList2=new ArrayList<SendTaskLog>();
//		for (Map<String, Object> map : taskList220) {
//		taskList2.add(new SendTaskLog(map.get("TASK_ID").toString(), map.get("GROUP_ID")==null?"":map.get("GROUP_ID").toString(), map.get("ASSIGNEE")==null?"": map.get("ASSIGNEE").toString(), ((BigDecimal)map.get("TASK_TYPE")).intValue(), ((BigDecimal)map.get("STATUS")).intValue()));
//		}
//		
//		if(taskList2!=null && taskList2.size()>0){
//			org.apache.cxf.jaxws.JaxWsProxyFactoryBean factoryBean = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
//		
//			factoryBean.create(IRocoService.class);
//			factoryBean.setAddress("http://172.16.3.220:8080/webservice/rocoService");  
//         
//			IRocoService rocoService = (IRocoService)factoryBean.create();
//			rocoService.sendTaskList(taskList2);
//			System.out.println("send task2 Size="+taskList2.size());
//		}
       
//        List<Map<String,Object>> list=jdbcTemplate.queryForList(" select * from send_task where task_id='"+taskId+"' and task_type=0");
//        if(list.size()==0){
//        	jdbcTemplate.update("insert into send_task(task_id,group_id,assignee,task_type,status) values('"+taskId+"','"+groupId+"',null,0,1)");
//        }else{
//        	jdbcTemplate.update("update send_task set status=1 where task_id='"+taskId+"' and task_type=0");
        }

}
