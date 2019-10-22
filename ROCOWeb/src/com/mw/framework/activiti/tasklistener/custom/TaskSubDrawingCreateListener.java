/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.SaleItemDao;
import com.main.domain.sale.SaleItem;
import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;

/**
 * 订单重审(创建)监听器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName 
 *           com.mw.framework.activiti.tasklistener.custom.TaskValuationCompleteListener
 *           .java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-1
 * 
 */
@SuppressWarnings("serial")
public class TaskSubDrawingCreateListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// itemid
		String id = (String) delegateTask.getVariable("subuuid");
		// jdbc
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		TaskLogService taskLogService = SpringContextHolder
				.getBean("taskLogService");
		// historic
		HistoryService historyService = SpringContextHolder
				.getBean("historyService");
		String actName = delegateTask.getName();
		String actId = delegateTask.getTaskDefinitionKey();
		Object uuid = delegateTask.getVariable("subuuid");
		String taskId = delegateTask.getId();
		List<Map<String, Object>> queryForList = jdbcTemplate
				.queryForList("select b.procinstid from sale_item a,act_ct_mapping b where a.pid=b.id and a.id='"
						+ id + "'");

		if (queryForList.size() > 0) {
			// query for order drawing task historic
			List<HistoricTaskInstance> list = historyService
					.createHistoricTaskInstanceQuery().processInstanceId(
							String.valueOf(queryForList.get(0)
									.get("PROCINSTID"))).taskDefinitionKey(
							"usertask_drawing").orderByTaskCreateTime().desc()
					.list();
			if (list.size() > 0) {
				for (HistoricTaskInstance historicTaskInstance : list) {
					// delegateTask.setVariable("assignee",
					// historicTaskInstance.getAssignee());
					delegateTask
							.setAssignee(historicTaskInstance.getAssignee());
					break;
				}
			} else {
				new Exception("未发现主流程【起草】节点历史操作用户!");
			}
		}
		// 流程缓存
		// String
		// taskId=delegateTask.getId();//jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		// Set<IdentityLink> set=delegateTask.getCandidates();
		// String groupId = null;
		// for (IdentityLink identityLink : set) {
		// groupId=identityLink.getGroupId();
		// }
		// TaskService taskService=SpringContextHolder.getBean("taskService");
		// long time=0;
		// if(taskService==null){
		// Task
		// task=taskService.createTaskQuery().taskId(taskId).singleResult();
		// if(task==null){
		// time=new java.util.Date().getTime();
		// }else{
		// time=task.getCreateTime().getTime();
		// }
		// }
		// MemoryCacheUtil.startFlow(taskId, groupId,time);
		String assignee = delegateTask.getAssignee();
		/*
		 * 1.先判断出是退回来的还是刚创建的 2.再查询一下有没有记载过 3.进行统计计算
		 */
		if (assignee == null) {
			taskLogService.create((String) uuid, actId, actName, taskId);
		}
	}

}
