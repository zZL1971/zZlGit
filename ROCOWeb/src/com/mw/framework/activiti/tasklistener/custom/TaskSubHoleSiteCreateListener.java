/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.redis.RedisUtils;

/**
 孔位审核监听器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName 
 *           com.mw.framework.activiti.tasklistener.csord.CustomerServiceCompleteListener
 *           .java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-23
 * 
 */
public class TaskSubHoleSiteCreateListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		HistoryService historyService=SpringContextHolder.getBean("historyService");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		String taskId = delegatetask.getId();
		Set<IdentityLink> set = delegatetask.getCandidates();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}
		//获取上一个操作人,并设置
		List<HistoricTaskInstance> list = historyService
				.createHistoricTaskInstanceQuery()
				.processInstanceId(delegatetask.getExecutionId())
				.taskDefinitionKey(delegatetask.getTaskDefinitionKey())
				.orderByTaskCreateTime().asc().list();

		if (list.size() > 0) {
			for (HistoricTaskInstance historicTaskInstance : list) {
				delegatetask.setAssignee(historicTaskInstance.getAssignee());
				break;
			}
		} else {
			new Exception("未发现节点历史操作用户!");
			//设置孔位出错审核人员
			String procinstid = delegatetask.getProcessInstanceId();
			JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
			String sql ="SELECT AC.ASSIGNEE_ FROM ACT_HI_ACTINST AC WHERE AC.PROC_INST_ID_='"+procinstid+"' AND AC.ACT_NAME_='孔位出错审核'";
			List<Map<String, Object>> assigneeList = jdbcTemplate.queryForList(sql);
			if(assigneeList.size()>0){
				Map<String, Object> assignees = assigneeList.get(0);
				String assigne = (String) assignees.get("ASSIGNEE_");
				delegatetask.setAssignee(assigne);
			}
		}
		Object assignee=delegatetask.getAssignee();
		Date time = new Date();
		redisUtils.startTask(assignee, groupId, taskId, null, time);
		String actId=delegatetask.getTaskDefinitionKey();
		String actName=delegatetask.getName();
		Object uuid = delegatetask.getVariable("subuuid");
		
		/*
		 * 1.先判断出是退回来的还是刚创建的 2.再查询一下有没有记载过 3.进行统计计算
		 */
		if (assignee == null) {
			taskLogService.create((String) uuid, actId, actName, taskId );
		}
		

	}
}
