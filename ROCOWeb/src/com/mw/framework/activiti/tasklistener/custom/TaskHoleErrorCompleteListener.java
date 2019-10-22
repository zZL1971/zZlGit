package com.mw.framework.activiti.tasklistener.custom;

import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;

import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.redis.RedisUtils;

@SuppressWarnings("serial")
public class TaskHoleErrorCompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String taskId = null;
		String groupId = null;
		Object assignee = null;
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		try {
			taskId = delegateTask.getId();
			assignee = delegateTask.getAssignee();
			Set<IdentityLink> set = delegateTask.getCandidates();
			for (IdentityLink identityLink : set) {
				groupId = identityLink.getGroupId();
			}
			taskLogService.complete(taskId,groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisUtils.endTask(assignee, groupId, taskId);
//			MemoryCacheUtil.endFlow(taskId, groupId, false);		
		}
	}
}
