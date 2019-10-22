/**
 *
 */
package com.mw.framework.activiti.tasklistener;

import java.util.Date;
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

import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.redis.RedisUtils;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.tasklistener.TaskCreateListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-4
 * 
 */
@SuppressWarnings("serial")
public class TaskCreateListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		HistoryService historyService = SpringContextHolder
				.getBean("historyService");
		JdbcTemplate jdbcTempltate = SpringContextHolder
				.getBean("jdbcTemplate");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		List<HistoricTaskInstance> list = historyService
				.createHistoricTaskInstanceQuery().processInstanceId(
						delegateTask.getExecutionId()).taskDefinitionKey(
						delegateTask.getTaskDefinitionKey())
				.orderByTaskCreateTime().asc().list();

		if (list.size() > 0) {
			for (HistoricTaskInstance historicTaskInstance : list) {
				// delegateTask.setVariable("assignee",
				// historicTaskInstance.getAssignee());
				delegateTask.setAssignee(historicTaskInstance.getAssignee());
				break;
			}
		} else {
			new Exception("未发现节点历史操作用户!");
		}
		// 流程缓存
		String taskId = delegateTask.getId();// jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		Set<IdentityLink> set = delegateTask.getCandidates();
		String actId = delegateTask.getTaskDefinitionKey();
		String actName = delegateTask.getName();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}

		// 分流 分出是gp_drawing 还是 gp_drawing_cad
		Object uuid = delegateTask.getVariable("uuid");
		if (uuid != null) {
			if ("gp_drawing".equals(groupId)) {
				String orderType = jdbcTempltate.queryForObject("SELECT SH.ORDER_TYPE FROM SALE_HEADER SH WHERE SH.ID='"+uuid+"'", String.class);
				if(orderType!=null) {
					if("OR3".equals(orderType)||"OR4".equals(orderType)) {
							groupId = "bg_gp_drawing";
						}else {
							List<Map<String, Object>> fileCount = jdbcTempltate//or c1.file_type is null
									.queryForList("select * from (select a1.ID from sale_header a1, sale_item b1, material_head c1 where a1.id = b1.pid and b1.material_head_id = c1.id and (c1.file_Type = '1' or c1.file_type is null) group by a1.id, c1.file_Type having count(*) > 0) where id = '"
											+ uuid + "'");
							if (fileCount.size() > 0) {
								groupId = "gp_drawing";
								
							} else {
								groupId = "gp_drawing_cad";
							}
						}
					}
			}
		} else {// 如果uuid是null 那么说明为子流程 需要从subuuid里面取
			uuid = delegateTask.getVariable("subuuid");
		}
		Object assignee=delegateTask.getAssignee();

		Date time = new Date();
		redisUtils.startTask(assignee, groupId, taskId, null, time);
		/*
		 * 1.先判断出是退回来的还是刚创建的 2.再查询一下有没有记载过 3.进行统计计算
		 */
		if (assignee == null) {
			taskLogService.create((String) uuid, actId, actName, taskId);
		}
	}

}
