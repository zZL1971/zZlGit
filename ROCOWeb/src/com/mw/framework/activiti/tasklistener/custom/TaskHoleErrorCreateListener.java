package com.mw.framework.activiti.tasklistener.custom;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.IdentityLink;

import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.redis.RedisUtils;

@SuppressWarnings("serial")
public class TaskHoleErrorCreateListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegatetask) {
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		HistoryService historyService=SpringContextHolder.getBean("historyService");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		String taskId = delegatetask.getId();// jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
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
		}
		String assignee = delegatetask.getAssignee();
		Date time = new Date();
		// 将任务丢进任务池
		redisUtils.startTask(assignee, groupId, taskId, null, time);
	}

}
