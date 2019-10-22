/**
 *
 */
package com.mw.framework.controller;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDiagramCmd;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.repository.DeploymentBuilderImpl;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mw.framework.commons.BaseController;
import com.mw.framework.util.UUIDUtils;

import freemarker.core._RegexBuiltins.groupsBI;
/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.ActivitiController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-12-2
 *
 */
@Controller
@RequestMapping("/process")
public class ActivitiController extends BaseController{
 
    @Resource
    ProcessEngine engine;
 
    /**
     * 列出所有流程模板
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView list(ModelAndView mav) {
        mav.addObject("list", Util.list());
        mav.setViewName("process/template");
        return mav;
    }
 
    /**
     * 部署流程
     */
    @RequestMapping("deploy")
    public ModelAndView deploy(String processName, ModelAndView mav) {
 
        RepositoryService service = engine.getRepositoryService();
 
        if (null != processName)
			try {
				service.createDeployment()
				        .addClasspathResource("diagrams/" + processName).deploy();
			} catch (Exception e) {
				e.printStackTrace();
			}
 
        List<ProcessDefinition> list = service.createProcessDefinitionQuery()
                .list();
 
        mav.addObject("list", list);
        mav.setViewName("process/deployed");
        return mav;
    }
 
    /**
     * 已部署流程列表
     */
    @RequestMapping("deployed")
    public ModelAndView deployed(ModelAndView mav) {
 
        RepositoryService service = engine.getRepositoryService();
 
        List<ProcessDefinition> list = service.createProcessDefinitionQuery()
                .list();
 
        mav.addObject("list", list);
        mav.setViewName("process/deployed");
 
        return mav;
    }
 
    /**
     * 启动一个流程实例
     */
    @RequestMapping("start")
    public ModelAndView start(String id, ModelAndView mav) {
 
        RuntimeService service = engine.getRuntimeService();
        
        Map<String,Object> variables = new HashMap<String, Object>();
		variables.put("assignee", null);
		 
        service.startProcessInstanceById(id,variables);
        List<ProcessInstance> list = service.createProcessInstanceQuery()
                .list();
 
        mav.addObject("list", list);
        mav.setViewName("process/started");
 
        return mav;
    }
 
    /**
     * 所有已启动流程实例
     */
    @RequestMapping("started")
    public ModelAndView started(ModelAndView mav) {
 
        RuntimeService service = engine.getRuntimeService();
 
        List<ProcessInstance> list = service.createProcessInstanceQuery()
                .list();
 
        mav.addObject("list", list);
        mav.setViewName("process/started");
 
        return mav;
    }
    
    /**
     * 领取任务
     * @param taskId
     * @param userId
     * @return
     */
    @RequestMapping("claim")
    public ModelAndView claim(String taskId,String userId){
    	TaskService service=engine.getTaskService();
    	Task singleResult = service.createTaskQuery().taskId(taskId).singleResult();
    	service.claim(singleResult.getId(), super.getLoginUser().getId());
    	//Map<String,Object> variables = new HashMap<String, Object>();
		//variables.put("assignee", null);
    	//service.complete(taskId,variables);
    	
    	return new ModelAndView("redirect:task");
    }
    
    /**
     * 放弃任务
     * @param taskId
     * @param userId
     * @return
     */
    @RequestMapping("unclaim")
    public ModelAndView unclaim(String taskId,String userId){
    	TaskService service=engine.getTaskService();
    	Task singleResult = service.createTaskQuery().taskId(taskId).singleResult();
    	service.unclaim(singleResult.getId());
    	
    	return new ModelAndView("redirect:task");
    }
     
    /**
     * 进入任务列表 
     */
    @RequestMapping("task")
    public ModelAndView task(ModelAndView mav){
        TaskService service=engine.getTaskService();
        RuntimeService runtimeService = engine.getRuntimeService();
        RepositoryService repositoryService = engine.getRepositoryService();
        HistoryService historyService = engine.getHistoryService();
        
        //获取所有的任务列表
        List<Task> list = service.createTaskQuery().list();
        //获取指定用户组的任务列表
        List<Task> tasks = service.createTaskQuery()/*.taskCandidateGroup("group1")*/.list();
       
        for (Task task : tasks) {
        	System.out.println("【"+task.getId()+"】当前环节: " + task.getName() + "-->【处理人】" + task.getAssignee());
        	//获取当前流程的参数
        	/*Map<String, Object> variables = service.getVariables(task.getId());
        	Set<Entry<String,Object>> entrySet = variables.entrySet();
        	for (Entry<String, Object> entry : entrySet) {
				System.out.println(entry.getKey()+"-->"+entry.getValue());
			}*/
        	
        	// 1.获取流程定义
        	ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        	// 2.获取流程实例
        	ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        	// 3.通过流程实例查找当前活动的ID
        	String activitiId = execution.getActivityId();
        	 // 4.通过活动的ID在流程定义中找到对应的活动对象
        	//List<ActivityImpl> activities = pd.getActivities();
        	//System.out.println("activities.size="+activities.size());
        	 ActivityImpl activity = pd.findActivity(activitiId);
        	 if(activity!=null){
        		 
        		// 5.通过活动对象找当前活动的所有出口
            	 List<PvmTransition> transitions =  activity.getOutgoingTransitions();
            	 
            	 //测试多流向控制跳转
            	 //activity.createOutgoingTransition(transitions.get(0).getId());
            	 
            	 //TransitionImpl createOutgoingTransition = activity.createOutgoingTransition();
            	 
            	 List<PvmTransition> outTransitionsTemp = null;
            	 // 6.提取所有出口的名称，封装成集合
            	 for (PvmTransition trans : transitions) {
            		 //System.out.println("出口流向："+trans.getId()+"--->"+trans.getDestination().getProperty("name"));
            		 
            		 PvmActivity ac = trans.getDestination(); //获取线路的终点节点    
                     if("exclusiveGateway".equals(ac.getProperty("type"))){
                    	 outTransitionsTemp = ac.getOutgoingTransitions();
                    	 for(PvmTransition trans1 : outTransitionsTemp){
                    		 System.out.println("分支出口流向："+trans1.getId()+"--->"+trans1.getDestination().getProperty("name"));
                    	 }
                     }else if("userTask".equals(ac.getProperty("type"))){
                    	 System.out.println("直接出口流向："+trans.getId()+"--->"+trans.getDestination().getProperty("name"));
                     }else{
                    	 System.out.println("其他直接出口流向："+trans.getId()+"--->"+trans.getDestination().getProperty("name"));
                     }
            		 
            		 //TransitionImpl transitionImpl = (TransitionImpl) trans; 
            		 //if(trans.getId().equals("flow14")){
            			 //activity.findOutgoingTransition(trans.getId());
            		 //}
            	     //String transName = (String) trans.getProperty("name");
            	     //System.out.println(transName);
            	 }
        	 }
        	 
        	 //当前流程的历史流向
        	 //ProcessInstance pi =runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult(); 
			//含流向
        	 /*List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricActivityInstanceStartTime().asc().list();
         	 for (HistoricActivityInstance historicActivityInstance : activityInstances) {
         		 
				System.out.println("Historic: "+JSONObject.fromObject(historicActivityInstance).toString());
			}*/
        	 System.out.println("**********历史流程**********");
         	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         	 
         	 //不含流向
         	List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByTaskCreateTime().asc().list();
         	for (HistoricTaskInstance historicTaskInstance : taskInstances) {
         		System.out.println("历史流程: "+historicTaskInstance.getName()+"\t"+(historicTaskInstance.getCreateTime()!=null?"开始"+sdf.format(historicTaskInstance.getCreateTime()):"")+"\t"+(historicTaskInstance.getEndTime()!=null?"结束"+sdf.format(historicTaskInstance.getEndTime()):""));
         		List<Comment> comments = service.getTaskComments(historicTaskInstance.getId());
         		for (Comment comment : comments) {
					System.out.println("历史流程-代办意见: "+comment.getUserId()+":"+comment.getFullMessage());
				}
         	
         		List<HistoricVariableInstance> list2 = historyService.createHistoricVariableInstanceQuery().taskId(historicTaskInstance.getId()).list();
         		for (HistoricVariableInstance historicVariableInstance : list2) {
					System.out.println("【历史流程-节点独立参数】："+historicVariableInstance.getVariableName()+"-->"+historicVariableInstance.getValue());
				}
			}
         	
         	//历史输入local数据
         	/*List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery().activityInstanceId(task.getProcessInstanceId()).orderByTime().asc().list();
			for (HistoricDetail historicDetail : historicDetails) {
				System.out.println(JSONObject.fromObject(historicDetail).toString());
			}*/
         	
         	 List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByProcessInstanceId().asc().list();
         	for (HistoricVariableInstance historicVariableInstance : variableInstances) {
				
         		System.out.println("historicVariable: "+historicVariableInstance.getId()+"-->"+historicVariableInstance.getVariableName()+"-->"+historicVariableInstance.getValue());
			}
         	 
        	   
        	//接受任务
        	//service.claim(task.getId(), "admin");
        	//委托任务
        	//service.delegateTask(task.getId(), "cc");
        }
        /*System.out.println("-------------------------");
        List<Execution> list2 = runtimeService.createExecutionQuery().activityId("receivetask1").list();
        for (Execution execution : list2) {
			System.out.println(execution.getId()+"-->"+execution.getActivityId());
			runtimeService.signal(execution.getId());
		}*/
        
        //ProcessInstance pi = runtimeService.startProcessInstanceByKey("myProcess");
        //System.out.println(pi.getId());
        
        
        //Execution execution = runtimeService.createExecutionQuery().processInstanceId("myProductQuotation").activityId("receivetask1").singleResult();
        
        //System.out.println(execution);
      
        //获取指定用户的任务列表
        //tasks = service.createTaskQuery().taskAssignee("admin").list();
        
        //用户和用户组的关系
        //IdentityService identityService = engine.getIdentityService();
        
        //新增用户组
        //Group newGroup = identityService.newGroup("2");
        //System.out.println("newGroup-->"+newGroup);
        
        //新增用户
        //User newUser = identityService.newUser("admin");
        //System.out.println("newUser-->"+newUser);
        
        //当前用户所属的组有哪些
        /*List<Group> groups = identityService.createGroupQuery().groupMember("admin").list();
        for (Group group : groups) {
			System.out.println(group.getId());
		}*/
        
        //当前用户组有哪些用户
        /*List<User> users = identityService.createUserQuery().memberOfGroup("gruop1").list();
        for (User user : users) {
			System.out.println("user-->"+user.getId());
		}*/
        
        //删除已部署流程
        //RepositoryService repositoryService = engine.getRepositoryService();
        //repositoryService.deleteDeployment("10001");
        
        mav.addObject("list", tasks);
        mav.setViewName("process/task");
        return mav;
    }
     
    /**
     *完成当前节点 
     */
    @RequestMapping("complete")
    public ModelAndView complete(ModelAndView mav,String id){
         
        TaskService service=engine.getTaskService();
        Map<String,Object> variables = new HashMap<String, Object>();
        variables.put("assigneeList", Arrays.asList("usergroup1","usergroup2"));
        variables.put("drawType", 1);
        variables.put("someVariableInMainProcess", "你妹的传参数!");
        
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setId("");
        groupEntity.setName("");
        groupEntity.setRevision(1);
        groupEntity.setType("");
        
        //代办意见
        //Task task = service.createTaskQuery().taskId(id).singleResult();
        //批注人的名称
        Authentication.setAuthenticatedUserId("zhangsan"+UUIDUtils.base58Uuid().substring(5,10));
        //批注信息
        service.addComment(id, null, "同意!");
        
        service.setVariableLocal(id, "flowState", 1);
        
        service.complete(id,variables);
        
        return new ModelAndView("redirect:task");
    }
    
    /**
     *删除部署
     */
    @RequestMapping("deleteDeployment")
    public ModelAndView deleteDeployment(ModelAndView mav,String id){
         
    	RepositoryService repositoryService = engine.getRepositoryService();
        repositoryService.deleteDeployment(id);
         
        return new ModelAndView("redirect:deployed");
    }
 
    /**
     * 所有已启动流程实例
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	@RequestMapping("graphics")
    public void graphics(String definitionId, String instanceId,
            String taskId, ModelAndView mav, HttpServletResponse response)
            throws IOException {
         
        response.setContentType("image/png");
        Command<InputStream> cmd = null;
        
        InputStream cmd2 = null;
 
        if (definitionId != null) {
            cmd = new GetDeploymentProcessDiagramCmd(definitionId);
        }
 
        if (instanceId != null) {
        	
        	BpmnModel bpmnModel= engine.getRepositoryService().getBpmnModel(instanceId); 
        	
            cmd = (Command<InputStream>) engine.getProcessEngineConfiguration().getProcessDiagramGenerator()
            .generateDiagram(bpmnModel, "png", 
            		engine.getProcessEngineConfiguration().getActivityFontName(),
            		engine.getProcessEngineConfiguration().getLabelFontName(), 
            		engine.getProcessEngineConfiguration().getClassLoader(),1.0);
        }
 
        if (taskId != null) {
            Task task = engine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
            
            BpmnModel bpmnModel= engine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId()); 
            
            cmd2 = engine.getProcessEngineConfiguration().getProcessDiagramGenerator()
            .generateDiagram(bpmnModel, "png", 
            		engine.getProcessEngineConfiguration().getActivityFontName(),
            		engine.getProcessEngineConfiguration().getLabelFontName(), 
            		engine.getProcessEngineConfiguration().getClassLoader(),1.0);
        }
        
        if (cmd != null) {
            InputStream is = engine.getManagementService().executeCommand(cmd);
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = is.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        }else if (cmd2 != null) {
            InputStream is = cmd2;
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = is.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        }
    }
}
