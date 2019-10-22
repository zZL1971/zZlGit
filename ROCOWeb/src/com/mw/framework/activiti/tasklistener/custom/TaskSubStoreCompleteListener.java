/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;

import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.redis.RedisUtils;

/**
 * 客服监听器
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
public class TaskSubStoreCompleteListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		String groupId = null;
		String taskId = null;
		Object assignee = null;
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		try {
			// 流程缓存
			taskId = delegatetask.getId();
			assignee = delegatetask.getAssignee();
			Set<IdentityLink> set = delegatetask.getCandidates();
			for (IdentityLink identityLink : set) {
				groupId = identityLink.getGroupId();
			}
			//记录任务结束
			taskLogService.complete(taskId,groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisUtils.endTask(assignee, groupId, taskId);
		}

	}
}
