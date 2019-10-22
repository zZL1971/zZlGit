/**
 *
 */
package com.mw.framework.activiti;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;


/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.activiti.TaskUserAssignService.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-3
 *
 */
public class TaskUserAssignService {
	/**
	 * 获取多实体子流程的执行用户集合
	 * @param execution
	 * @return
	 * @throws Execption
	 */
	@SuppressWarnings("unchecked")
	public List<String> getMultipleUser(ActivityExecution execution) throws Exception{
		
		String nodeId=execution.getActivity().getId();
		ExecutionEntity executionEnt=(ExecutionEntity) execution;
		
		//System.out.println("88888888");
		
		
		/*List<String> userIds =(List<String>)execution.getVariable("assigneeList");
		for (String string : userIds) {
			System.out.println("userIds-->"+string);
		}*/
		
		List<String> groups = (List<String>) execution.getVariable("assigneeList");
		/*for (String string : groups) {
			System.out.println("multiple group："+string);
		}*/
		
		/*List<String> userIds=(List<String>)execution.getVariable(BpmConst.SUBPRO_MULTI_USERIDS);
		
		if(userIds!=null) return userIds;
		Map<String,FlowNode> nodeMap= NodeCache.getByActDefId(executionEnt.getProcessDefinitionId());
		FlowNode subProcessNode=nodeMap.get(nodeId);
		FlowNode firstNode=subProcessNode.getSubFirstNode();
		
		FlowNode secodeNode=firstNode.getNextFlowNodes().get(0);
		
		List<String> userList=nodeUserMapLocal.get().get(secodeNode.getNodeId());
		
		logger.debug("userList size:" + userList.size());
		
		execution.setVariable(BpmConst.SUBPRO_MULTI_USERIDS, userList);*/
		
		//List<String> userIds=new ArrayList<String>();/*= (List<String>) execution.getVariable("multiUserids");*/
		
		//userIds.add("A01");
		//userIds.add("A02");
		
		
		return groups;
	}
}
