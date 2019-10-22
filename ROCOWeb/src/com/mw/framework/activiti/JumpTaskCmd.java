/**
 *
 */
package com.mw.framework.activiti;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.task.Comment;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.JumpTask.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-8-28
 * 
 */
public class JumpTaskCmd implements Command<Comment> {

	protected String executionId;
	protected String activityId;

	public JumpTaskCmd(String executionId, String activityId) {
		this.executionId = executionId;
		this.activityId = activityId;
	}

	public Comment execute(CommandContext commandContext) {
		
		for (TaskEntity taskEntity : Context.getCommandContext().getTaskEntityManager().findTasksByExecutionId(executionId)) {
			Context.getCommandContext().getTaskEntityManager().deleteTask(taskEntity, "jump", false);
		}
		
		ExecutionEntity executionEntity = Context.getCommandContext().getExecutionEntityManager().findExecutionById(executionId);
		
		ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
		
		ActivityImpl activity = processDefinition.findActivity(activityId);
		if(activity == null ){ 
    		throw new ActivitiException(this.activityId+" to ActivityImpl is null!");
    	}else{
    		executionEntity.executeActivity(activity);
    	}
		
		return null;
	}

}
