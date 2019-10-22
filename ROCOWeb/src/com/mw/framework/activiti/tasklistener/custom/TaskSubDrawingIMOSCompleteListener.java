/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.redis.RedisUtils;

/**
 * IMOS绘图监听器
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
public class TaskSubDrawingIMOSCompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String taskId = null;
		String groupId = null;
		Object assignee = null;
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		try {
			JdbcTemplate jdbcTemplate = SpringContextHolder
					.getBean("jdbcTemplate");
			// 流程缓存
			taskId = delegateTask.getId();
			assignee = delegateTask.getAssignee();
			String actId=delegateTask.getTaskDefinitionKey();
			Set<IdentityLink> set = delegateTask.getCandidates();
			for (IdentityLink identityLink : set) {
				groupId = identityLink.getGroupId();
			}
			Object uuid = delegateTask.getVariable("subuuid");
			//记录任务结束
			taskLogService.complete(taskId,groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisUtils.endTask(assignee, groupId, taskId);
			//MemoryCacheUtil.endFlow(taskId, groupId, true);
		}
	}

}
