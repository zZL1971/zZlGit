/**
 *
 */
package com.mw.framework.manager.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mw.framework.activiti.JumpTaskCmd;
import com.mw.framework.bean.Message;
import com.mw.framework.dao.SysActCTMappingDao;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.manager.FlowManager;
import com.mw.framework.model.BPMHistoricTaskInstance;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.manager.impl.BPMManagerImpl.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-10
 * 
 */
@Service("flowManager")
@Transactional
public class FlowManagerImpl implements FlowManager {

	@Autowired
	RepositoryService repositoryService;

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	TaskService taskService;

	@Autowired
	FormService formService;

	@Autowired
	HistoryService historyService;

	@Autowired
	ManagementService managementService;
	
	@Autowired
	SysActCTMappingDao ctMappingDao;

	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	public void deployed() {

	}

	public void start() {

	}

	public void end() {

	}

	public void complete() {

	}

	public void historic() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,BPMHistoricTaskInstance> getFlowStatus(String uuid) {
		SysActCTMapping mapping = ctMappingDao.findOne(uuid);
		
		HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().processInstanceId(mapping.getProcinstid());
		
		Map<String,BPMHistoricTaskInstance> map = new LinkedHashMap<String, BPMHistoricTaskInstance>();
		List<HistoricTaskInstance> historicTaskInstances = historicTaskInstanceQuery.orderByTaskCreateTime().asc().list();
		for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
			//System.out.println(historicTaskInstance.getTaskDefinitionKey()+"|"+historicTaskInstance.getName()+"|"+historicTaskInstance.getAssignee());
			BPMHistoricTaskInstance instance = new BPMHistoricTaskInstance(historicTaskInstance.getName(), historicTaskInstance.getCreateTime(), historicTaskInstance.getEndTime(), null, null, historicTaskInstance.getAssignee(), historicTaskInstance.getClaimTime());
			List<Comment> comments = taskService.getTaskComments(historicTaskInstance.getId());
	  		for (Comment comment : comments) {
	  			instance.setCommentUserId(comment.getUserId());
	  			instance.setCommentMessage(comment.getFullMessage());
			}
			map.put(historicTaskInstance.getTaskDefinitionKey(), instance);
		}
		
		Set<Entry<String,BPMHistoricTaskInstance>> entrySet = map.entrySet();
		for (Entry<String, BPMHistoricTaskInstance> entry : entrySet) {
			System.out.println(entry.getKey()+"|"+entry.getValue());
		}
		
		return map;
	}

	@Override
	public Message jump(String taskId, String target,String userId) {
		Message msg = null;
		try {
			Task task = taskService.createTaskQuery().taskId(taskId)
					.singleResult();

			if (task != null) {
				if(userId!=null)
				taskService.addComment(task.getId(), null, userId);
				TaskServiceImpl taskServiceImpl=(TaskServiceImpl)taskService;
				taskServiceImpl.getCommandExecutor().execute(new JumpTaskCmd(task.getExecutionId(), target));  
				
				//更新财务节点的结束时间
				String uSql="update act_hi_actinst hi set hi.end_time_=sysdate where hi.act_id_ in ('usertask_finance','usertask4') and hi.act_name_='财务确认' and hi.end_time_ is null and hi.proc_inst_id_ in ( select m.procinstid from act_ct_mapping m ,sale_header h where m.id=h.id and h.order_status is null ) and hi.task_id_='"+taskId+"'";
				//添加流程记录 2016-06-07 
				jdbcTemplate.update(uSql);
				
				msg = new Message("任务已经结束!");
				/*ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService
						.getProcessDefinition(task.getProcessDefinitionId());
				ExecutionEntity execution = (ExecutionEntity) runtimeService
						.createExecutionQuery()
						.executionId(task.getExecutionId()).singleResult();
				ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

				// 获取当前流程的活动节点
				ActivityImpl currActivity = pd.findActivity(execution
						.getActivityId());

				// 清空当前流向
				List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);
				// 创建新流向
				TransitionImpl newTransition = currActivity
						.createOutgoingTransition();

				// 目标节点
				ActivityImpl pointActivity = null;

				List<ActivityImpl> activities = processDefinition
						.getActivities();
				
				if (target==null) {
					for (ActivityImpl activityImpl : activities) {
						System.out.println(activityImpl.getId());
						List<PvmTransition> pvmTransitionList = activityImpl
								.getOutgoingTransitions();
						if (pvmTransitionList.isEmpty()) {
							pointActivity = activityImpl;
						}
					}
				}else{
					// 根据节点ID，获取对应的活动节点  
					pointActivity = ((ProcessDefinitionImpl) processDefinition)  
			                 .findActivity(target);
				}

				// 设置新流向的目标节点
				newTransition.setDestination(pointActivity);
				
				if(task.getAssignee()!=null && task.getAssignee().trim().length()>0){
					
				}else{
					// 接受任务
					//taskService.claim(taskId,userId);
					task.setAssignee(userId);
				}
				
				try {
					// 执行转向任务
					taskService.complete(taskId);
					msg = new Message("任务已终止!");
				} catch (Exception e) {
					e.printStackTrace();
					msg = new Message("TASK-500", "流程终止失败!");
				}
				// 删除目标节点新流入
				pointActivity.getIncomingTransitions().remove(newTransition);

				// 还原以前流向
				restoreTransition(currActivity, oriPvmTransitionList);*/

				
			} else {
				msg = new Message("TASK-500", "任务不存在!");
			}

		} catch (Exception e) {
			msg = new Message("TASK-500", e.getLocalizedMessage());
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * 根据任务ID获取流程定义
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
			String taskId) throws Exception {
		// RepositoryService repositoryService = engine.getRepositoryService();
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(findTaskById(taskId)
						.getProcessDefinitionId());

		if (processDefinition == null) {
			throw new Exception("流程定义未找到!");
		}

		return processDefinition;
	}

	/**
	 * 根据任务ID获得任务实例
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	private TaskEntity findTaskById(String taskId) throws Exception {
		// TaskService taskService = engine.getTaskService();
		TaskEntity task = (TaskEntity) taskService.createTaskQuery()
				.taskId(taskId).singleResult();
		if (task == null) {
			throw new Exception("任务实例未找到!");
		}
		return task;
	}
	
	/**
	 * 还原指定活动节点流向
	 * 
	 * @param activityImpl
	 *            活动节点
	 * @param oriPvmTransitionList
	 *            原有节点流向集合
	 */
	private void restoreTransition(ActivityImpl activityImpl,
			List<PvmTransition> oriPvmTransitionList) {
		// 清空现有流向
		List<PvmTransition> pvmTransitionList = activityImpl
				.getOutgoingTransitions();
		pvmTransitionList.clear();
		// 还原以前流向
		for (PvmTransition pvmTransition : oriPvmTransitionList) {
			pvmTransitionList.add(pvmTransition);
		}
	}


	/**
	 * 清空指定活动节点流向
	 * 
	 * @param activityImpl
	 *            活动节点
	 * @return 节点流向集合
	 */
	private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
		// 存储当前节点所有流向临时变量
		List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
		// 获取当前节点所有流向，存储到临时变量，然后清空
		List<PvmTransition> pvmTransitionList = activityImpl
				.getOutgoingTransitions();
		for (PvmTransition pvmTransition : pvmTransitionList) {
			oriPvmTransitionList.add(pvmTransition);
		}
		pvmTransitionList.clear();

		return oriPvmTransitionList;
	}
}
