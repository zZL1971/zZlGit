/**
 *
 */
package com.mw.framework.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SysJobPoolDao;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sys.SysJobPool;
import com.main.manager.SysJobPoolManager;
import com.main.service.TaskLogService;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysActCTMappingDao;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.domain.SysActTaskLog;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.FlowManager;
import com.mw.framework.manager.SysActCtOrdErrManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.BPMHistoricTaskInstance;
import com.mw.framework.model.CommitBatchModel;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.model.TaskBatchModel;
import com.mw.framework.model.TaskModel;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.ControlTimeUtil;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.StringUtil;
import com.mw.framework.utils.ZStringUtils;

import net.sf.json.JSONArray;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.controller.BPMController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-11
 * 
 */
@Controller
@RequestMapping("/core/bpm/*")
public class BPMController extends BaseController{
	public static String gp_drawing_cad_sql = "select a.task_id from view_bpm_activity_task a,sale_header b where a.uuid=b.id and a.group_id='gp_drawing' "
			+ " and a.assignee is null and a.task_id not in(select d2.task_id from sale_header a1,sale_item b1,material_head c1,"
			+ " view_bpm_activity_task d2  where a1.id=b1.pid and b1.material_head_id=c1.id and d2.uuid=a1.id and  (c1.file_Type='1' or c1.file_type is null)"
			+ " and d2.group_id='gp_drawing' group by d2.task_id,c1.file_Type   having count(*)>0) order by a.create_time_";

	public static String gp_drawing_sql = "select a.task_id from view_bpm_activity_task a,sale_header b where a.uuid=b.id and a.group_id='gp_drawing' "
			+ " and a.assignee is null and a.task_id in(select d2.task_id from sale_header a1,sale_item b1,material_head c1,"
			+ " view_bpm_activity_task d2  where a1.id=b1.pid and b1.material_head_id=c1.id and d2.uuid=a1.id and   (c1.file_Type='1' or  c1.file_Type is null) "
			+ " and d2.group_id='gp_drawing' group by d2.task_id,c1.file_Type   having count(*)>0) order by a.create_time_";

	@Autowired
	RepositoryService repositoryService;

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	TaskService taskService;
	@Autowired
	TaskLogService taskLogService;

	@Autowired
	FormService formService;

	@Autowired
	HistoryService historyService;

	@Autowired
	ManagementService managementService;

	@Autowired
	private CommonManager commonManager;

	@Autowired
	private FlowManager flowManager;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private SaleHeaderDao saleHeaderDao;
	@Autowired
	private SaleItemDao saleItemDao;
	
	@Autowired
	private SysJobPoolDao sysJobPoolDao;

	@Autowired
	private SysActCtOrdErrManager sysActCtOrdErrManager;
	/**
	 * 根据bpm文件名获取已部署的流程
	 * 
	 * @param bpm
	 * @return
	 */
	@RequestMapping(value = { "/deployed/{bpm}" }, method = RequestMethod.GET)
	@ResponseBody
	public Message deployed(@PathVariable String bpm) {
		Message message = null;
		// RepositoryService service = engine.getRepositoryService();

		List<Map<String, Object>> deployedList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapDefault = new HashMap<String, Object>();
		mapDefault.put("id", "");
		mapDefault.put("text", "--请选择--");
		deployedList.add(mapDefault);
		List<ProcessDefinition> list = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionResourceNameLike("%" + bpm + "%").list();
		for (ProcessDefinition processDefinition : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", processDefinition.getId());
			map.put("text", processDefinition.getName() + "-V"
					+ processDefinition.getVersion());
			deployedList.add(map);
		}

		message = new Message(deployedList);

		return message;
	}

	/**
	 * 根据已部署的ID获取对应已启动的流程
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/started" }, method = RequestMethod.GET)
	@ResponseBody
	public Message started(@RequestParam String id) {
		Message message = new Message("ok");
		// RuntimeService service = engine.getRuntimeService();
		List<Map<String, Object>> startedList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapDefault = new HashMap<String, Object>();
		mapDefault.put("id", "");
		mapDefault.put("text", "--请选择--");
		startedList.add(mapDefault);

		List<ProcessInstance> list = runtimeService
				.createProcessInstanceQuery().processDefinitionId(id).list();
		for (ProcessInstance processInstance : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", processInstance.getId());
			map.put("text", processInstance.getId());
			startedList.add(map);
		}
		message = new Message(startedList);
		return message;
	}

	/**
	 * 根据流程实例获取对应的任务
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/currentFlow" }, method = RequestMethod.GET)
	@ResponseBody
	public Message currentFlow(@RequestParam String id) {
		Message message = new Message("ok");
		List<Map<String, Object>> currentFlowList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapDefault = new HashMap<String, Object>();
		mapDefault.put("id", "");
		mapDefault.put("text", "--请选择--");
		currentFlowList.add(mapDefault);

		// TaskService taskService = engine.getTaskService();

		List<Task> list = taskService.createTaskQuery().processInstanceId(id)
				.list();
		for (Task task : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", task.getId());
			map.put("text", task.getName());
			currentFlowList.add(map);
		}

		message = new Message(currentFlowList);
		return message;
	}

	/**
	 * 根据任务ID获取所有出口环节
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/taskFlow" }, method = RequestMethod.GET)
	@ResponseBody
	public Message taskFlow(@RequestParam String id) {
		Message message = null;
		String orderType = null;
		// TaskService taskService = engine.getTaskService();
		// RepositoryService repositoryService = engine.getRepositoryService();
		// RuntimeService runtimeService = engine.getRuntimeService();
		String sql = "select sh.order_type from sale_header sh left JOIN act_ct_mapping ac ON ac.id = sh.id left join act_ru_task ar on ar.proc_inst_id_=ac.procinstid where ar.id_='"+id+"'";
		List<Map<String, Object>> ordrtypeList = jdbcTemplate.queryForList(sql);
		if(ordrtypeList.size()>0){
			orderType = (String) ordrtypeList.get(0).get("order_type");
		}
		List<Map<String, Object>> taskFlowList = new ArrayList<Map<String, Object>>();
		// Map<String,Object> mapDefault = new HashMap<String, Object>();
		// mapDefault.put("id", "");
		// mapDefault.put("text", "--请选择--");
		// taskFlowList.add(mapDefault);

		Task task = taskService.createTaskQuery().taskId(id).singleResult();

		if (task != null && task.getAssignee() != null
				&& task.getAssignee().trim().length() > 0) {

			// System.out.println(task.getProcessDefinitionId());

			ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService
					.getProcessDefinition(task.getProcessDefinitionId());
			ExecutionEntity execution = (ExecutionEntity) runtimeService
					.createExecutionQuery().executionId(task.getExecutionId())
					.singleResult();
			ActivityImpl activity = pd.findActivity(execution.getActivityId());

			if (activity != null) {
				List<PvmTransition> transitions = activity
						.getOutgoingTransitions();

				List<PvmTransition> outTransitionsTemp = null;
				// 6.提取所有出口的名称，封装成集合
				for (PvmTransition trans : transitions) {
					PvmActivity ac = trans.getDestination(); // 获取线路的终点节点
					if ("exclusiveGateway".equals(ac.getProperty("type"))) {
						
						if (ac.getId().startsWith("auto_")) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("id", trans.getId());
							map.put("text", trans.getProperty("name"));
							map.put("target", trans.getId());
							taskFlowList.add(map);
						} else {
							outTransitionsTemp = ac.getOutgoingTransitions();
							for (PvmTransition trans1 : outTransitionsTemp) {
								// System.out.println("分支出口流向："+trans1.getId()+"--->"+trans1.getDestination().getProperty("name")+"--->"+trans1.getDestination().getId());
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("id", trans1.getId());
								if("OR4".equals(orderType)&&"flow22".equals(trans1.getId())) {
									continue;
								}
								if("OR3".equals(orderType)&&"flow23".equals(trans1.getId())) {
									continue;
								}
								if("OR2".equals(orderType)&&"flow_rt_usertask_drawing".equals(trans1.getId())) {
									continue;
								}
								
								map.put("text", (trans1.getId().startsWith(
										"flow_rt_") ? "【退回】-" : "【提交】-")
										+ trans1.getDestination().getProperty(
												"name"));
								map.put("target", trans1.getDestination()
										.getId());
								taskFlowList.add(map);
							}
						}

					} else if ("userTask".equals(ac.getProperty("type"))) {
						// System.out.println("直接出口流向："+trans.getId()+"--->"+trans.getDestination().getProperty("name")+"--->"+trans.getDestination().getId());
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", trans.getId());
						map.put("text",
								(trans.getId().startsWith("flow_rt_") ? "【退回】-"
										: "【提交】-")
										+ trans.getDestination().getProperty(
												"name"));
						map.put("target", trans.getDestination().getId());
						taskFlowList.add(map);
					} else {
						// System.out.println("其他直接出口流向："+trans.getId()+"--->"+trans.getDestination().getProperty("name")+"--->"+trans.getDestination().getId());
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", trans.getId());
						map.put("text",
								(trans.getId().startsWith("flow_rt_") ? "【退回】-"
										: "【提交】-")
										+ trans.getDestination().getProperty(
												"name"));
						map.put("target", trans.getDestination().getId());
						taskFlowList.add(map);
					}
				}
			}
		}

		message = new Message(taskFlowList);
		return message;
	}

	@RequestMapping(value = { "/historic" }, method = RequestMethod.GET)
	@ResponseBody
	public Message historic(@RequestParam String id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Task task = taskService.createTaskQuery().taskId(id).singleResult();

		List<BPMHistoricTaskInstance> historicTaskInstances = new ArrayList<BPMHistoricTaskInstance>();
		if (task != null) {
			List<HistoricTaskInstance> taskInstances = historyService
					.createHistoricTaskInstanceQuery().processInstanceId(
							task.getProcessInstanceId())
					.orderByTaskCreateTime().asc().list();
			List<HistoricActivityInstance> list = historyService
					.createHistoricActivityInstanceQuery().processInstanceId(
							task.getProcessInstanceId()).list();

			// List<ProcessInstance> list2 =
			// runtimeService.createProcessInstanceQuery().list();

			for (HistoricTaskInstance historicTaskInstance : taskInstances) {

				// 判断当前节点是否为子节点
				// System.out.println(historicTaskInstance.getParentTaskId());

				BPMHistoricTaskInstance bpmHistoricTaskInstance = new BPMHistoricTaskInstance();

				bpmHistoricTaskInstance.setCreateTime(historicTaskInstance
						.getCreateTime());
				bpmHistoricTaskInstance.setEndTime(historicTaskInstance
						.getEndTime());
				bpmHistoricTaskInstance.setAssignee(historicTaskInstance
						.getAssignee());
				bpmHistoricTaskInstance.setClaimTime(historicTaskInstance
						.getClaimTime());

				// 获取当前节点的上一个节点信息
				String upActivity = "";
				for (HistoricActivityInstance historicActivityInstanceEnd : list) {
					if (historicTaskInstance.getCreateTime() != null
							&& historicActivityInstanceEnd.getEndTime() != null
							&& sdf
									.format(
											historicTaskInstance
													.getCreateTime())
									.equals(
											sdf
													.format(historicActivityInstanceEnd
															.getEndTime()))) {
						upActivity = historicActivityInstanceEnd
								.getActivityName();
						break;
					}
				}

				bpmHistoricTaskInstance.setName(upActivity
						+ "<font color='blue'>-->"
						+ historicTaskInstance.getName() + "</font>");

				// System.out.println("历史流程: "+historicTaskInstance.getId()+"\t"+historicTaskInstance.getParentTaskId()+"\t"+historicTaskInstance.getAssignee()+"\t"+historicTaskInstance.getClaimTime()+"\t"+historicTaskInstance.getOwner()+"\t"+historicTaskInstance.getExecutionId()+"\t"+historicTaskInstance.getName()+"\t"+(historicTaskInstance.getCreateTime()!=null?"开始"+sdf.format(historicTaskInstance.getCreateTime()):"")+"\t"+(historicTaskInstance.getEndTime()!=null?"结束"+sdf.format(historicTaskInstance.getEndTime()):""));
				List<Comment> comments = taskService
						.getTaskComments(historicTaskInstance.getId());
				for (Comment comment : comments) {
					// System.out.println("历史流程-代办意见: "+comment.getUserId()+":"+comment.getFullMessage());
					bpmHistoricTaskInstance.setCommentUserId(comment
							.getUserId());
					bpmHistoricTaskInstance.setCommentMessage(comment
							.getFullMessage());
				}
				// System.out.println("*********************");
				historicTaskInstances.add(bpmHistoricTaskInstance);
			}
		} else {
			// System.out.println("已完成流程实例直接读取历史记录的数据，不用从任务列表转查");
			/*
			 * List<HistoricProcessInstance> list =
			 * historyService.createHistoricProcessInstanceQuery().list(); for
			 * (HistoricProcessInstance historicProcessInstance : list) {
			 * System.out.println(historicProcessInstance); }
			 */
			/*
			 * List<HistoricTaskInstance> list2 =
			 * historyService.createHistoricTaskInstanceQuery
			 * ().processInstanceId(id).list(); for (HistoricTaskInstance
			 * historicTaskInstance : list2) {
			 * //System.out.println(historicTaskInstance
			 * .getProcessInstanceId()+"-->"
			 * +historicTaskInstance.getTaskDefinitionKey
			 * ()+"|"+historicTaskInstance.getName()); BPMHistoricTaskInstance
			 * bpmHistoricTaskInstance = new BPMHistoricTaskInstance();
			 * bpmHistoricTaskInstance
			 * .setCreateTime(historicTaskInstance.getCreateTime());
			 * bpmHistoricTaskInstance
			 * .setEndTime(historicTaskInstance.getEndTime());
			 * bpmHistoricTaskInstance
			 * .setAssignee(historicTaskInstance.getAssignee());
			 * bpmHistoricTaskInstance
			 * .setClaimTime(historicTaskInstance.getClaimTime());
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * bpmHistoricTaskInstance.setName(upActivity+"<font color='blue'>-->"
			 * +historicTaskInstance.getName()+"</font>");
			 * 
			 * List<Comment> comments =
			 * taskService.getTaskComments(historicTaskInstance.getId()); for
			 * (Comment comment : comments) {
			 * //System.out.println("历史流程-代办意见: "+
			 * comment.getUserId()+":"+comment.getFullMessage());
			 * bpmHistoricTaskInstance.setCommentUserId(comment.getUserId());
			 * bpmHistoricTaskInstance
			 * .setCommentMessage(comment.getFullMessage()); }
			 * 
			 * historicTaskInstances.add(bpmHistoricTaskInstance); }
			 */

			// List<HistoricActivityInstance> list =
			// historyService.createHistoricActivityInstanceQuery().processInstanceId(id).orderByHistoricActivityInstanceStartTime().asc().list();
			Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
			formats.put("START_TIME_", new SimpleDateFormat(
					DateTools.fullFormat));
			formats
					.put("END_TIME_",
							new SimpleDateFormat(DateTools.fullFormat));
			List<Map<String, Object>> historyList = jdbcTemplate
					.queryForList(
							"select a.*,b.tel,b.qq_number,si.tackit,si.order_code_posex from act_hi_actinst a right join act_ct_mapping cm on a.proc_inst_id_=cm.procinstid left join sale_item si on si.id=cm.id left join sys_user b on a.assignee_=b.id where a.proc_inst_id_=? order by a.start_time_ asc",
							new Object[] { id });
			int w = 0;
			int y = 0;
			Integer z = null;
			int x = 0;
			BPMHistoricTaskInstance callActivity = null;
			for (int i = 0; i < historyList.size(); i++) {
				// for (HistoricActivityInstance historicActivityInstance :
				// list) {
				// System.out.println(historicActivityInstance);
				// HistoricActivityInstance historicActivityInstance =
				// list.get(i);
				Map<String, Object> historyMap = historyList.get(i);
				// 判断是否为子流程(多个则显示计数器)
				String actType = historyMap.get("ACT_TYPE_") == null ? ""
						: historyMap.get("ACT_TYPE_").toString();// 流程类型
				String actId = historyMap.get("ACT_ID_") == null ? ""
						: historyMap.get("ACT_ID_").toString();// 流程ID
				String tackit = historyMap.get("TACKIT") == null ? ""
						: historyMap.get("TACKIT").toString();// 罚单
				String tackit_status="";
				String taskId = historyMap.get("TASK_ID_") == null ? ""
						: historyMap.get("TASK_ID_").toString();
				String startTime = historyMap.get("START_TIME_") == null ? ""
						: historyMap.get("START_TIME_").toString();// 开始时间
				String endTime = historyMap.get("END_TIME_") == null ? ""
						: historyMap.get("END_TIME_").toString();// 结束时间
				String assignee = historyMap.get("ASSIGNEE_") == null ? ""
						: historyMap.get("ASSIGNEE_").toString();// 受理人
				String actName = historyMap.get("ACT_NAME_") == null ? ""
						: historyMap.get("ACT_NAME_").toString();// 流程名称
				if(historyMap.get("order_code_posex")!=null&&tackit.endsWith("1")&&endTime!=""){
					String orderCodePosex=historyMap.get("order_code_posex").toString();
					if(!taskId.equals("")){
						List<Map<String, Object>> list = jdbcTemplate.queryForList("select a.tackit from act_ct_ord_err A WHERE A.MAPPING_SID='"+orderCodePosex+"' AND A.TASKID='"+taskId+"'");
						if(list!=null&&list.size()>0){
							tackit_status=list.get(0).get("tackit") == null ? ""
									: list.get(0).get("tackit").toString();
						}
					}
				}
				String tel = historyMap.get("TEL") == null ? "" : historyMap
						.get("TEL").toString();// 电话号码
				String qqNumber = historyMap.get("QQ_NUMBER") == null ? ""
						: historyMap.get("QQ_NUMBER").toString();// QQ号码
				if (actType.equals("callActivity")) {
					if (!ZStringUtils.isEmpty(endTime)) {
						y++;
					} else {
						w++;
					}
					if (callActivity == null) {
						callActivity = new BPMHistoricTaskInstance();
						callActivity.setCreateTime(DateTools.strToDate(
								startTime, DateTools.fullFormat));
						callActivity.setName(actName);
						historicTaskInstances.add(callActivity);
						z = x;
						x++;
					}
				} else if (actType.equals("exclusiveGateway")) {

				} else {
					BPMHistoricTaskInstance bpmHistoricTaskInstance = new BPMHistoricTaskInstance();
					bpmHistoricTaskInstance.setCreateTime(DateTools.strToDate(
							startTime, DateTools.fullFormat));

					bpmHistoricTaskInstance.setEndTime(DateTools.strToDate(
							endTime, DateTools.fullFormat));

					bpmHistoricTaskInstance.setAssignee(assignee);

					bpmHistoricTaskInstance.setName(actName);
					Map<String, String> menusMap = (Map<String, String>) super
							.getSession().getAttribute(
									Constants.CURR_ROLE_MENUS_MAP);
					bpmHistoricTaskInstance.setTackit(tackit_status.equals("1")?"是":"");
					if (!actId.equals("usertask_valuation")) {// 其他环节不可以看到价格审核人的信息
						bpmHistoricTaskInstance.setTel(tel);
						bpmHistoricTaskInstance.setQqNumber(qqNumber);
					}

					if (menusMap.containsKey("core/bpm/BPM_TASK_LIST_U06")) {// 拥有确认报价权限可以看到所有环节审核人的信息
						bpmHistoricTaskInstance.setTel(tel);
						bpmHistoricTaskInstance.setQqNumber(qqNumber);
					}
					String takId = historyMap.get("TASK_ID_") == null ? ""
							: historyMap.get("TASK_ID_").toString();// 任务ID
					List<Comment> comments = taskService.getTaskComments(takId);
					for (Comment comment : comments) {
						// System.out.println("历史流程-代办意见: "+comment.getUserId()+":"+comment.getFullMessage());
						bpmHistoricTaskInstance.setCommentUserId(comment
								.getUserId());
						bpmHistoricTaskInstance.setCommentMessage(comment
								.getFullMessage());
					}

					historicTaskInstances.add(bpmHistoricTaskInstance);
					x++;
					// HistoricTaskInstance singleResult =
					// historyService.createHistoricTaskInstanceQuery().processInstanceId(id).taskId(historicActivityInstance.getActivityId()).singleResult();
					// System.out.println(singleResult);
					// bpmHistoricTaskInstance.setClaimTime(historicActivityInstance.get);
				}
			}
			if (callActivity != null) {
				historicTaskInstances.get(z).setName(
						/* callActivity.getName() */"子流程" + "[" + y + "/"
								+ (y + w) + "]");
				// callActivity
			}

		}

		return new Message(historicTaskInstances);
	}

	// public Message historic(@RequestParam String id){
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//			
	// Task task = taskService.createTaskQuery().taskId(id).singleResult();
	//			
	// List<BPMHistoricTaskInstance> historicTaskInstances = new
	// ArrayList<BPMHistoricTaskInstance>();
	// if(task!=null){
	// List<HistoricTaskInstance> taskInstances =
	// historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByTaskCreateTime().asc().list();
	// List<HistoricActivityInstance> list =
	// historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
	//				 
	// //List<ProcessInstance> list2 =
	// runtimeService.createProcessInstanceQuery().list();
	//				
	// for (HistoricTaskInstance historicTaskInstance : taskInstances) {
	//					
	// //判断当前节点是否为子节点
	// //System.out.println(historicTaskInstance.getParentTaskId());
	//					
	// BPMHistoricTaskInstance bpmHistoricTaskInstance = new
	// BPMHistoricTaskInstance();
	//					
	// bpmHistoricTaskInstance.setCreateTime(historicTaskInstance.getCreateTime());
	// bpmHistoricTaskInstance.setEndTime(historicTaskInstance.getEndTime());
	// bpmHistoricTaskInstance.setAssignee(historicTaskInstance.getAssignee());
	// bpmHistoricTaskInstance.setClaimTime(historicTaskInstance.getClaimTime());
	//					
	// //获取当前节点的上一个节点信息
	// String upActivity = "";
	// for (HistoricActivityInstance historicActivityInstanceEnd : list) {
	// if(historicTaskInstance.getCreateTime()!=null
	// &&historicActivityInstanceEnd.getEndTime()!=null &&
	// sdf.format(historicTaskInstance.getCreateTime()).equals(sdf.format(historicActivityInstanceEnd.getEndTime()))){
	// upActivity = historicActivityInstanceEnd.getActivityName();
	// break;
	// }
	// }
	//					
	// bpmHistoricTaskInstance.setName(upActivity+"<font color='blue'>-->"+historicTaskInstance.getName()+"</font>");
	//					
	// //System.out.println("历史流程: "+historicTaskInstance.getId()+"\t"+historicTaskInstance.getParentTaskId()+"\t"+historicTaskInstance.getAssignee()+"\t"+historicTaskInstance.getClaimTime()+"\t"+historicTaskInstance.getOwner()+"\t"+historicTaskInstance.getExecutionId()+"\t"+historicTaskInstance.getName()+"\t"+(historicTaskInstance.getCreateTime()!=null?"开始"+sdf.format(historicTaskInstance.getCreateTime()):"")+"\t"+(historicTaskInstance.getEndTime()!=null?"结束"+sdf.format(historicTaskInstance.getEndTime()):""));
	// List<Comment> comments =
	// taskService.getTaskComments(historicTaskInstance.getId());
	// for (Comment comment : comments) {
	// //System.out.println("历史流程-代办意见: "+comment.getUserId()+":"+comment.getFullMessage());
	// bpmHistoricTaskInstance.setCommentUserId(comment.getUserId());
	// bpmHistoricTaskInstance.setCommentMessage(comment.getFullMessage());
	// }
	// //System.out.println("*********************");
	// historicTaskInstances.add(bpmHistoricTaskInstance);
	// }
	// }else{
	// System.out.println("已完成流程实例直接读取历史记录的数据，不用从任务列表转查");
	// /*List<HistoricProcessInstance> list =
	// historyService.createHistoricProcessInstanceQuery().list();
	// for (HistoricProcessInstance historicProcessInstance : list) {
	// System.out.println(historicProcessInstance);
	// }*/
	// /*List<HistoricTaskInstance> list2 =
	// historyService.createHistoricTaskInstanceQuery().processInstanceId(id).list();
	// for (HistoricTaskInstance historicTaskInstance : list2) {
	// //System.out.println(historicTaskInstance.getProcessInstanceId()+"-->"+historicTaskInstance.getTaskDefinitionKey()+"|"+historicTaskInstance.getName());
	// BPMHistoricTaskInstance bpmHistoricTaskInstance = new
	// BPMHistoricTaskInstance();
	// bpmHistoricTaskInstance.setCreateTime(historicTaskInstance.getCreateTime());
	// bpmHistoricTaskInstance.setEndTime(historicTaskInstance.getEndTime());
	// bpmHistoricTaskInstance.setAssignee(historicTaskInstance.getAssignee());
	// bpmHistoricTaskInstance.setClaimTime(historicTaskInstance.getClaimTime());
	//					
	//				 
	//					
	// bpmHistoricTaskInstance.setName(upActivity+"<font color='blue'>-->"+historicTaskInstance.getName()+"</font>");
	//					
	// List<Comment> comments =
	// taskService.getTaskComments(historicTaskInstance.getId());
	// for (Comment comment : comments) {
	// //System.out.println("历史流程-代办意见: "+comment.getUserId()+":"+comment.getFullMessage());
	// bpmHistoricTaskInstance.setCommentUserId(comment.getUserId());
	// bpmHistoricTaskInstance.setCommentMessage(comment.getFullMessage());
	// }
	//			  		
	// historicTaskInstances.add(bpmHistoricTaskInstance);
	// }*/
	//				
	// List<HistoricActivityInstance> list =
	// historyService.createHistoricActivityInstanceQuery().processInstanceId(id).orderByHistoricActivityInstanceStartTime().asc().list();
	//				
	// int w = 0;
	// int y = 0;
	// Integer z = null;
	// int x = 0;
	// BPMHistoricTaskInstance callActivity = null;
	// for(int i=0;i<list.size();i++){
	// //for (HistoricActivityInstance historicActivityInstance : list) {
	// //System.out.println(historicActivityInstance);
	// HistoricActivityInstance historicActivityInstance = list.get(i);
	//					
	// //判断是否为子流程(多个则显示计数器)
	// if(historicActivityInstance.getActivityType().equals("callActivity")){
	// if(historicActivityInstance.getEndTime()!=null){
	// y++;
	// }else{
	// w++;
	// }
	// if(callActivity==null){
	// callActivity = new BPMHistoricTaskInstance();
	// callActivity.setCreateTime(historicActivityInstance.getStartTime());
	// callActivity.setName(historicActivityInstance.getActivityName());
	// historicTaskInstances.add(callActivity);
	// z = x;
	// x++;
	// }
	// }else
	// if(historicActivityInstance.getActivityType().equals("exclusiveGateway")){
	//						
	// }else{
	// BPMHistoricTaskInstance bpmHistoricTaskInstance = new
	// BPMHistoricTaskInstance();
	// bpmHistoricTaskInstance.setCreateTime(historicActivityInstance.getStartTime());
	// bpmHistoricTaskInstance.setEndTime(historicActivityInstance.getEndTime());
	// bpmHistoricTaskInstance.setAssignee(historicActivityInstance.getAssignee());
	//						
	// bpmHistoricTaskInstance.setName(historicActivityInstance.getActivityName());
	//						
	// List<Comment> comments =
	// taskService.getTaskComments(historicActivityInstance.getTaskId());
	// for (Comment comment : comments) {
	// //System.out.println("历史流程-代办意见: "+comment.getUserId()+":"+comment.getFullMessage());
	// bpmHistoricTaskInstance.setCommentUserId(comment.getUserId());
	// bpmHistoricTaskInstance.setCommentMessage(comment.getFullMessage());
	// }
	//				  		
	// historicTaskInstances.add(bpmHistoricTaskInstance);
	// x++;
	// //HistoricTaskInstance singleResult =
	// historyService.createHistoricTaskInstanceQuery().processInstanceId(id).taskId(historicActivityInstance.getActivityId()).singleResult();
	// //System.out.println(singleResult);
	// //bpmHistoricTaskInstance.setClaimTime(historicActivityInstance.get);
	// }
	// }
	// if(callActivity!=null){
	// historicTaskInstances.get(z).setName(/*callActivity.getName()*/"子流程"+"["+y+"/"+(y+w)+"]");
	// //callActivity
	// }
	//				
	// }
	//			
	// return new Message(historicTaskInstances);
	// }

	@RequestMapping(value = { "/tree/historic" }, method = RequestMethod.GET)
	@ResponseBody
	@Deprecated
	public JsonNode historicForTree(@RequestParam String node) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// HistoryService historyService = engine.getHistoryService();
		// TaskService taskService = engine.getTaskService();
		Task task = taskService.createTaskQuery().taskId(node).singleResult();
		List<BPMHistoricTaskInstance> historicTaskInstances = new ArrayList<BPMHistoricTaskInstance>();
		if (task != null) {

			BPMHistoricTaskInstance root = new BPMHistoricTaskInstance();
			root.setId("root");
			root.setName("ROOT");
			historicTaskInstances.add(root);

			List<HistoricTaskInstance> taskInstances = historyService
					.createHistoricTaskInstanceQuery().processInstanceId(
							task.getProcessInstanceId())
					.orderByHistoricTaskInstanceStartTime().asc().list();
			List<HistoricActivityInstance> list = historyService
					.createHistoricActivityInstanceQuery().processInstanceId(
							task.getProcessInstanceId()).list();

			HistoricTaskInstance historicTaskInstance2 = taskInstances.get(0);
			BPMHistoricTaskInstance first = new BPMHistoricTaskInstance();
			first.setId(historicTaskInstance2.getEndTime() == null ? sdf
					.format(historicTaskInstance2.getEndTime()) : "" + "");
			first.setName(historicTaskInstance2.getName());
			first.setAssignee(historicTaskInstance2.getAssignee());
			first.setCreateTime(historicTaskInstance2.getStartTime());
			first.setEndTime(historicTaskInstance2.getEndTime());
			first.setPid("root");

			for (int i = 1; i < taskInstances.size(); i++) {
				HistoricTaskInstance historicTaskInstance = taskInstances
						.get(i);
				// }
				// for (HistoricTaskInstance historicTaskInstance :
				// taskInstances) {
				BPMHistoricTaskInstance bpmHistoricTaskInstance = new BPMHistoricTaskInstance();

				bpmHistoricTaskInstance.setCreateTime(historicTaskInstance
						.getCreateTime());
				bpmHistoricTaskInstance.setEndTime(historicTaskInstance
						.getEndTime());
				bpmHistoricTaskInstance.setAssignee(historicTaskInstance
						.getAssignee());
				bpmHistoricTaskInstance.setClaimTime(historicTaskInstance
						.getClaimTime());

				// 获取当前节点的上一个节点信息
				String upActivity = "";
				String upActivityId = "";
				String upTaskId = "";
				String upId = "";
				for (HistoricActivityInstance historicActivityInstanceEnd : list) {
					if (historicTaskInstance.getCreateTime() != null
							&& historicActivityInstanceEnd.getEndTime() != null
							&& sdf
									.format(
											historicTaskInstance
													.getCreateTime())
									.equals(
											sdf
													.format(historicActivityInstanceEnd
															.getEndTime()))) {
						upActivity = historicActivityInstanceEnd
								.getActivityName();
						upActivityId = historicActivityInstanceEnd
								.getActivityId();
						upTaskId = historicActivityInstanceEnd.getTaskId();
						upId = historicActivityInstanceEnd.getId();
						break;
					}
				}
				System.out.println(upActivityId);
				System.out.println(upId);
				bpmHistoricTaskInstance.setName(upActivity
						+ "<font color='blue'>-->"
						+ historicTaskInstance.getName() + "</font>");
				bpmHistoricTaskInstance.setPid(upTaskId);
				bpmHistoricTaskInstance.setId(historicTaskInstance.getId());

				// System.out.println("历史流程: "+historicTaskInstance.getId()+"\t【"+upId+"|"+upActivityId+"|"+upTaskId+"|"+upActivity+"】\t"+historicTaskInstance.getAssignee()+"\t"+historicTaskInstance.getClaimTime()+"\t"+historicTaskInstance.getOwner()+"\t"+historicTaskInstance.getExecutionId()+"\t"+historicTaskInstance.getName()+"\t"+(historicTaskInstance.getCreateTime()!=null?"开始"+sdf.format(historicTaskInstance.getCreateTime()):"")+"\t"+(historicTaskInstance.getEndTime()!=null?"结束"+sdf.format(historicTaskInstance.getEndTime()):""));
				List<Comment> comments = taskService
						.getTaskComments(historicTaskInstance.getId());
				for (Comment comment : comments) {
					// System.out.println("历史流程-代办意见: "+comment.getUserId()+":"+comment.getFullMessage());
					bpmHistoricTaskInstance.setCommentUserId(comment
							.getUserId());
					bpmHistoricTaskInstance.setCommentMessage(comment
							.getFullMessage());
				}
				historicTaskInstances.add(bpmHistoricTaskInstance);
			}
		}

		return super
				.queryForTreeNode(historicTaskInstances, "pid", false, false,
						"name", "createTime", "endTime", "commentUserId",
						"commentMessage", "waste", "assignee", "claimTime",
						"pid", "id");
		// return new Message(historicTaskInstances);
	}

	/**
	 * 跳转到显示流程图界面（高亮已经审批过的流程节点）
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/jumpGraphics" }, method = RequestMethod.GET)
	public String jumpGraphics(String definitionId, String processInstanceId,
			HttpServletRequest request) {
		// RepositoryService repositoryService = engine.getRepositoryService();
		// RuntimeService runtimeService = engine.getRuntimeService();

		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery().processDefinitionId(
						definitionId).singleResult();
		ProcessDefinitionImpl pdImpl = (ProcessDefinitionImpl) processDefinition;
		if (pdImpl != null) {
			String processDefinitionId = pdImpl.getId();
			ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
					.getDeployedProcessDefinition(processDefinitionId);

			List<ActivityImpl> activitiList = def.getActivities();// 获得当前任务的所有节点
			List<String> ActiveActivityIds = runtimeService
					.getActiveActivityIds(processInstanceId);
			List<ActivityImpl> actImpls = new ArrayList<ActivityImpl>();
			for (String activeId : ActiveActivityIds) {
				for (ActivityImpl activityImpl : activitiList) {
					String id = activityImpl.getId();
					if (activityImpl.isScope()) {
						if (activityImpl.getActivities().size() > 1) {
							List<ActivityImpl> subAcList = activityImpl
									.getActivities();
							for (ActivityImpl subActImpl : subAcList) {
								String subid = subActImpl.getId();
								// System.out.println("subImpl:" + subid);
								if (activeId.equals(subid)) {// 获得执行到那个节点
									actImpls.add(subActImpl);
									break;
								}
							}
						}
					}
					if (activeId.equals(id)) {// 获得执行到那个节点
						actImpls.add(activityImpl);
					}
				}
			}
			request.setAttribute("actImpls", actImpls);
			request.setAttribute("definitionId", definitionId);
		}

		return "process/graphics";
	}

	@RequestMapping(value = { "/graphics" }, method = RequestMethod.GET)
	public void graphics(String definitionId, String taskId,
			HttpServletResponse response) throws IOException {
		// RepositoryService repositoryService = engine.getRepositoryService();
		if (definitionId != null && definitionId.trim().length() > 0) {
			ProcessDefinition procDef = repositoryService
					.createProcessDefinitionQuery().processDefinitionId(
							definitionId).singleResult();
			String diagramResourceName = procDef.getDiagramResourceName();
			InputStream imageStream = repositoryService.getResourceAsStream(
					procDef.getDeploymentId(), diagramResourceName);
			int len = 0;
			byte[] b = new byte[1024];
			while ((len = imageStream.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		}
	}

	/**
	 * 发起审批
	 * 
	 * @param bpm
	 * @param uuid
	 * @return
	 */
	@RequestMapping(value = { "/startFlow" }, method = RequestMethod.GET)
	@ResponseBody
	public Message startFlow(String bpm, String uuid) {
		// RepositoryService repositoryService = engine.getRepositoryService();
		// RuntimeService runtimeService = engine.getRuntimeService();
		// TaskService taskService = engine.getTaskService();
		Message message = null;

		// 当前uuid是否已经发起过流程
		Object one = commonManager.getOne(uuid, SysActCTMapping.class);
		if (one == null) {
			// 判断是否已经部署流程
			List<ProcessDefinition> processDefinitions = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionResourceNameLike("%" + bpm + "%")
					.orderByProcessDefinitionVersion().desc().list();

			// 获取最新的流程定义
			if (processDefinitions.size() > 0) {
				ProcessDefinition processDefinition = processDefinitions.get(0);

				// 设置默认参数
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("assignee", null);

				// 创建新的实例
				ProcessInstance startProcessInstanceById = runtimeService
						.startProcessInstanceById(processDefinition.getId(),
								variables);
				SaleHeader saleHeader = commonManager.getById(uuid, SaleHeader.class);
				SysActCTMapping mapping = new SysActCTMapping(uuid,
						startProcessInstanceById.getId(), processDefinition
								.getId());
				mapping.setOrderType(saleHeader.getOrderType());
				List<Map<String, Object>> custHeader = jdbcTemplate.queryForList("SELECT CH.REGIO FROM CUST_HEADER CH WHERE CH.KUNNR='"+saleHeader.getShouDaFang()+"'");
				if(custHeader.size() > 0) {
					mapping.setRegio((String)custHeader.get(0).get("REGIO"));
				}
				commonManager.save(mapping);

				// 根据实例编号获取当前活动流程节点
				List<Task> tasks = taskService.createTaskQuery()
						.processInstanceId(startProcessInstanceById.getId())
						.list();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("taskId", tasks.get(0).getId());
				map.put("taskName", tasks.get(0).getName());
				map.put("prodefid", mapping.getProdefid());
				map.put("procinstid", mapping.getProcinstid());

				taskService.claim(tasks.get(0).getId(), super.getLoginUserId());// 当前登录系统的用户领取任务该任务

				message = new Message(map);
				message.setObj(map);
			} else {
				message = new Message("V-START-ER", "流程未发布，请联系系统管理员!");
			}
		} else {
			message = new Message("V-START-ER", "已经发起过审批了，请不要重复发起!");
			message.setMsg("YES-START-TASK");
		}

		return message;
	}

	/**
	 * 已发起审批，读取状态信息
	 * 
	 * @param bpm
	 * @param uuid
	 * @return
	 */
	@RequestMapping(value = { "/startedFlow" }, method = RequestMethod.GET)
	@ResponseBody
	public Message startedFlow(String uuid, String xtype) {
		// TaskService taskService = engine.getTaskService();
		// HistoryService historyService = engine.getHistoryService();
		Message message = null;

		try {
			SysActCTMapping mapping = commonManager.getOne(uuid,
					SysActCTMapping.class);
			Map<String, Object> map = new HashMap<String, Object>();
			if (mapping != null) {

				String currUser = super.getLoginUserId();
				//HttpSession session = super.getSession();
				
				//获取孔位出错信息
				String holesql="SELECT SI.HOLE_MESSAGE AS holemessage,SI.TACKIT FROM SALE_ITEM SI WHERE SI.ID='"+uuid+"'";
				List<Map<String, Object>> holemessage = jdbcTemplate.queryForList(holesql);
				
				// 根据实例编号获取当前活动流程节点
				List<Task> tasks = taskService.createTaskQuery()
						.processInstanceId(mapping.getProcinstid())
						./* .taskCandidateGroup(arg0) */taskAssignee(currUser)
						.list();
				
				map.put("prodefid", mapping.getProdefid());// 流程定义ID
				map.put("procinstid", mapping.getProcinstid());// 流程实例ID
				map.put("docStatus", 2);// 流程状态：0：未审批 1：审批中2：审批结束
				if (holemessage.size()>0) {
					map.put("holemessage", holemessage.get(0).get("HOLEMESSAGE"));//孔位出错信息
					map.put("tackit", holemessage.get(0).get("TACKIT"));//开罚单
				}
				if (tasks.size() > 0) {
					map.put("taskId", tasks.get(0).getId());// 任务ID
					map.put("taskdefId", tasks.get(0).getTaskDefinitionKey());// 任务定义ID
					map.put("taskName", tasks.get(0).getName());// 任务名称
					// 获取当前任务节点的分组信息
					List<IdentityLink> identityLinksForTask = taskService
							.getIdentityLinksForTask(tasks.get(0).getId());
					for (IdentityLink identityLink : identityLinksForTask) {
						if (identityLink.getGroupId() != null) {
							map.put("taskGroup", identityLink.getGroupId());// 任务分组信息
						}
						if(identityLink.getUserId()!= null) {
							map.put("userId", identityLink.getUserId());
						}
					}
					map.put("docStatus", 1);
					
					map.put("assignee", true/* tasks.get(0).getAssignee() */);
				} else {
					tasks = taskService.createTaskQuery().processInstanceId(
							mapping.getProcinstid()).list();

					if (tasks.size() > 0) {
						map.put("taskId", tasks.get(0).getId());// 任务ID
						map.put("taskdefId", tasks.get(0)
								.getTaskDefinitionKey());// 任务定义ID
						map.put("taskName", tasks.get(0).getName());// 任务名称

						// 获取当前任务节点的分组信息
						List<IdentityLink> identityLinksForTask = taskService
								.getIdentityLinksForTask(tasks.get(0).getId());
						for (IdentityLink identityLink : identityLinksForTask) {
							if (identityLink.getGroupId() != null) {
								map.put("taskGroup", identityLink.getGroupId());// 任务分组信息
							}
						}
						map.put("docStatus", 1);
						map.put("assignee", false);
					} else {
						// 后去流程历史记录的最后一个环节看是不是子流程，如果是子显示子流程的编号
						List<HistoricActivityInstance> list = historyService
								.createHistoricActivityInstanceQuery()
								.processInstanceId(mapping.getProcinstid())
								.orderByHistoricActivityInstanceEndTime()
								.desc().list();
						if (list.size() > 0) {
							HistoricActivityInstance historicActivityInstance = list
									.get(0);
							if (historicActivityInstance.getActivityType()
									.equals("callActivity")) {
								map.put("taskId", historicActivityInstance
										.getTaskId());// 任务ID
								map.put("taskName", historicActivityInstance
										.getActivityName());// 任务名称
								map.put("taskdefId", historicActivityInstance
										.getActivityId());// 任务定义ID
								map.put("docStatus", 1);
								map.put("assignee", false);
							}
						}
					}

					map.put("assignee", false);
				}
				message = new Message(map);
				message.setObj(map);
			} else {
				map.put("docStatus", 0);
				message = new Message(map);
			}
		} catch (Exception e) {
			message = new Message("D-STARTED-ER", e.getLocalizedMessage());
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * 提交审批
	 * 
	 * @param currentflow
	 *            当前环节编号
	 * @param nextflow
	 *            下一环节
	 * @param mappingId
	 *            关联表单id
	 * @param mappingNo
	 *            关联表单编号
	 * @param desc
	 *            代办意见
	 * @param errType
	 *            出错类型
	 * @param errDesc
	 *            退回原因
	 * @return
	 */
	@RequestMapping(value = { "/commitFlow" }, method = RequestMethod.POST)
	@ResponseBody
	public Message commitFlow(String currentflow, String nextflow,
			String mappingId, String mappingNo, String desc, String errType,
			String errDesc, String errRea,String tackit) {
		// TaskService taskService = engine.getTaskService();
		// HistoryService historyService = engine.getHistoryService();
		Message message = null;
		SysDataDictDao dictDao = SpringContextHolder
				.getBean("sysDataDictDao");
		String errTypeDesc = null;
		if (errType != null) {
			// 获取数据字典对应的显示值
			List<SysDataDict> dicts = dictDao
					.findByTrieTreeKeyValForSSM("ERROR_ORD_TYPE");
			for (SysDataDict sysDataDict : dicts) {
				if (sysDataDict.getKeyVal().equals(errType)) {
					errTypeDesc = sysDataDict.getDescZhCn();
					break;
				}
			}
		}

		String errReaDesc="OK";
		errRea="1";
//		if (errRea != null) {
//			// 获取数据字典对应的显示值
//			List<SysDataDict> dicts = dictDao
//					.findByTrieTreeKeyValForSSM("ERROR_ORD_REA");
//			for (SysDataDict sysDataDict : dicts) {
//				if (sysDataDict.getKeyVal().equals(errRea)) {
//					errReaDesc = sysDataDict.getDescZhCn();
//					break;
//				}
//			}
//		}
		Task task = taskService.createTaskQuery().taskId(currentflow)
				.singleResult();
		try {
			// 传递参数
			Map<String, Object> variables = new HashMap<String, Object>();
			String historicAssignee = null;
			String nextTask = null;
			String nextTaskName = null;
			if (task != null) {
				// 验证处理当前任务的用户是否为领取任务的用户
				if (!super.getLoginUserId().equals(task.getAssignee())) {
					message = new Message("BPM-VU-500", "当前任务已被"
							+ task.getAssignee() + "用户领取并处理");
					super.mesSendManager.sendUser(super.getLoginUserId()
							+ "在操作你<font color=#FF8C00>【" + task.getName()
							+ "】</font>的单据，已被系统拒绝了。", super.getLoginUserId(),
							task.getAssignee(), true);
					return message;
				}
				// 获取当前环节所有的出口节点
				ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService
						.getProcessDefinition(task.getProcessDefinitionId());
				ExecutionEntity execution = (ExecutionEntity) runtimeService
						.createExecutionQuery().executionId(
								task.getExecutionId()).singleResult();
				ActivityImpl activity = pd.findActivity(execution
						.getActivityId());

				if (activity != null) {
					List<PvmTransition> transitions = activity
							.getOutgoingTransitions();

					List<PvmTransition> outTransitionsTemp = null;
					// 6.提取所有出口的名称，封装成集合
					for (PvmTransition trans : transitions) {
						PvmActivity ac = trans.getDestination(); // 获取线路的终点节点
						if ("exclusiveGateway".equals(ac.getProperty("type"))) {
							outTransitionsTemp = ac.getOutgoingTransitions();
							for (PvmTransition trans1 : outTransitionsTemp) {
								if (trans1.getId().equals(nextflow)) {
									if ("callActivity".equals(trans1
											.getDestination().getProperty(
													"type"))
											|| "receiveTask".equals(trans1
													.getDestination()
													.getProperty("type"))) {
										List<PvmTransition> outgoingTransitions = trans1
												.getDestination()
												.getOutgoingTransitions();
										for (PvmTransition pvmTransition : outgoingTransitions) {
											PvmActivity destination = pvmTransition
													.getDestination();
											if ("userTask".equals(destination
													.getProperty("type"))) {
												nextTask = destination.getId();
												nextTaskName = (String) destination
														.getProperty("name");
												break;
											}
										}
									} else {
										nextTask = trans1.getDestination()
												.getId();
										nextTaskName = (String) trans1
												.getDestination().getProperty(
														"name");
										break;
									}
								}
							}
						} else if ("userTask".equals(ac.getProperty("type"))) {
							if (trans.getId().equals(nextflow)) {
								nextTask = trans.getDestination().getId();
								nextTaskName = (String) trans.getDestination()
										.getProperty("name");
								break;
							}
						} else if("receiveTask".equals(ac.getProperty("type"))) {
							if (trans.getId().equals(nextflow)) {
								nextTask = trans.getDestination().getId();
								nextTaskName = (String) trans.getDestination()
										.getProperty("name");
								break;
							}
						}
						else if ("endEvent".equals(ac.getProperty("type"))) {

						}else {
							System.out.println("其他直接出口流向："
									+ trans.getId()
									+ "--->"
									+ trans.getDestination()
											.getProperty("name") + "--->"
									+ trans.getDestination().getId());
							if (trans.getId().equals(nextflow)) {
								nextTask = trans.getDestination().getId();
								nextTaskName = (String) trans.getDestination()
										.getProperty("name");
								break;
							}
						}
					}
				}

				// 获取流程历史节点的该节点的assignee用户
				if (nextTask != null) {
					List<HistoricTaskInstance> list = historyService
							.createHistoricTaskInstanceQuery()
							.processInstanceId(task.getProcessInstanceId())
							.taskDefinitionKey(nextTask)
							.orderByTaskCreateTime().desc().list();

					if (list.size() == 0) {
						List<Map<String, Object>> queryForList = jdbcTemplate
								.queryForList(
										"select * from act_ct_mapping where id=?",
										mappingId);

						if (queryForList.size() > 0) {
							Map<String, Object> map = queryForList.get(0);
							if (map.get("PROC_INST_ID_OLD") != null
									&& map.get("PROC_INST_ID_OLD").toString()
											.trim().length() > 0) {
								list = historyService
										.createHistoricTaskInstanceQuery()
										.processInstanceId(
												map.get("PROC_INST_ID_OLD")
														.toString())
										.taskDefinitionKey(nextTask)
										.orderByTaskCreateTime().desc().list();
							}
						}
					}

					if (list.size() > 0) {
						for (HistoricTaskInstance historicTaskInstance : list) {
							variables.put("assignee", historicTaskInstance
									.getAssignee());
							historicAssignee = historicTaskInstance
									.getAssignee();
							break;
						}
					} else {
						variables.put("assignee", null);
					}

				} else {
					variables.put("assignee", null);
				}

			} else {
				message = new Message("S-TASK-E", "任务节点不存在!");
				return message;
			}
//			if (nextflow.startsWith("flow_rt_")) {
			if(desc != null && desc.length()>0){
				taskService.addComment(currentflow, null, desc);
			}else{
				String logerType="SALE";//订单出错 SALE
				sysActCtOrdErrManager.saveSysActCtOrdErrLoger1(task, mappingId, errType,tackit, errDesc, nextTaskName, mappingNo, nextTaskName,logerType);
				taskService.addComment(currentflow, null, errTypeDesc + ":"+errReaDesc+":"
						+ errDesc);
			}
				
				
			// 下一个节点跳转条件
			taskService.setVariableLocal(currentflow, "nextflow", nextflow);

			variables.put("uuid", mappingId);
			variables.put("pnumber", mappingNo);

			variables.put("nextflow", nextflow);

			variables.put("nextTask", nextTask);
			taskService.complete(currentflow, variables);

			/*SysActCTOrdStat ordStat = commonManager.getOne(mappingId,
					SysActCTOrdStat.class);
			if (ordStat != null) {
				ordStat.setProcinstid(task.getProcessInstanceId());
				ordStat.setProdefid(task.getProcessDefinitionId());
				ordStat.setTaskid(task.getId());
				ordStat.setExecutionId(task.getExecutionId());
				ordStat.setTaskname(task.getName());
				ordStat.setErrType(errType);
				ordStat.setErrDesc(errDesc);
				ordStat.setTaskDesc(desc);
				ordStat.setTaskDefinitionKey(task.getTaskDefinitionKey());
				ordStat.setTargetTaskDefinitionKey(nextTask);
			} else {
				ordStat = new SysActCTOrdStat(task.getProcessInstanceId(), task
						.getProcessDefinitionId(), task.getId(), task
						.getExecutionId(), task.getName(), errType, errDesc,
						desc, nextTask, task.getTaskDefinitionKey(), "");
				ordStat.setId(mappingId);
			}
			commonManager.save(ordStat);*/

			if (historicAssignee != null) {
				if (nextflow.startsWith("flow_rt_")) {
					super.mesSendManager.sendUser("你审批的<font color=#FF8C00>【"
							+ mappingNo + "】</font>因<font color=#FF8C00>【"
							+ errTypeDesc + "】</font>被退回.", super
							.getLoginUserId(), historicAssignee, true);
				} else {
					super.mesSendManager.sendUser("你退回的<font color=#FF8C00>【"
							+ mappingNo + "】</font>被<font color=#FF8C00>【"
							+ historicAssignee + "】</font>已修改并提交给你审批.", super
							.getLoginUserId(), historicAssignee, true);
				}
			}
			message = new Message("操作成功！");
			SaleHeader saleHeader = saleHeaderDao.findOne(mappingId);
			if(saleHeader!=null) {
				if(("OR3".equals(saleHeader.getOrderType())||"OR4".equals(saleHeader.getOrderType()))) {
					if("flow3".equals(nextflow)) {
						message = new Message("订单号："+saleHeader.getOrderCode());
					}else {
						message = new Message("操作成功！");
					}
				}else {
					message = new Message("操作成功！");
				}
			}
			// 退回到起草状态时，更新非标产品的状态 A
			/*
			 * if("flow_rt_usertask_store".equals(nextflow)){
			 * jdbcTemplate.update(
			 * "update material_head h set h.ROW_STATUS='A' where h.is_standard=0 and h.id in ( select sl.material_head_id from sale_item sl where sl.pid='"
			 * +mappingId+"')"); }
			 */

		} catch (org.activiti.engine.ActivitiOptimisticLockingException e) {
			message = new Message("S-COMMIT-E", "已经提交了,别点那么多次嘛。");
		} catch (Exception e) {
			message = new Message("S-COMMIT-E", e.getLocalizedMessage());
			String sql = "select * from act_hi_identitylink t where t.task_id_= ? ";
			List<Map<String, Object>> innerList = jdbcTemplate.queryForList(
					sql, new Object[] { task.getId() });
			if (innerList != null && innerList.size() > 0) {
				/*String groupId = innerList.get(0).get("GROUP_ID_") == null ? ""
						: innerList.get(0).get("GROUP_ID_").toString();*/
//				MemoryCacheUtil.endFlow(task.getId(), groupId, true);
			}
			// MemoryCacheUtil.endFlow(task.geti, groupId, flag);
			e.printStackTrace();
		}
		return message;
	}

	@RequestMapping(value = { "/taskDistribution" }, method = RequestMethod.GET)
	public ModelAndView template2st(ModelAndView modelAndView) {
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "TDBApp");
		modelAndView.getModelMap().put("moduleTitle", "任务重新分配");
		return modelAndView;
	}

	/**
	 * 获取所有的激活的任务节点
	 * 
	 * @return
	 */
	@RequestMapping("/taskDistribution/datalist")
	@ResponseBody
	public JdbcExtGridBean getTaskList(HttpServletRequest request) {
		return super.simpleQuery("select * from vact_task_list where 1=1 and sn is not null ",
				false, null, request);
	}

	@RequestMapping("/groupusers2/{groupId}")
	@ResponseBody
	public Message getUsersForGroup2(@PathVariable String groupId,
			String userId[]) {
		Message msg = null;
		String sql = "select id,id||'-'||user_name as user_name from sys_user a,sys_user_group b where  a.id=b.user_id and b.group_id='"
				+ groupId + "'";

		for (int i = 0; i < userId.length; i++) {
			String uid = userId[i];
			if (uid != null && uid.trim().length() > 0) {
				sql += " and user_id<>'" + uid + "'";
			}
		}

		List<Map<String, Object>> query = jdbcTemplate.query(sql,
				new MapRowMapper(true));

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "");
		map.put("user_name", "");

		data.add(map);
		data.addAll(query);

		msg = new Message(data);
		return msg;
	}

	@RequestMapping("/groupusers/{groupId}")
	@ResponseBody
	public Message getUsersForGroup(@PathVariable String groupId, String userId) {
		Message msg = null;
		String sql = "select id,id||'-'||user_name as user_name from sys_user a,sys_user_group b where    a.status = 1 and  a.id=b.user_id and b.group_id='"
				+ groupId + "'";

		if (userId != null && userId.trim().length() > 0) {
			//sql += " and user_id<>'" + userId + "'";
		}

		List<Map<String, Object>> query = jdbcTemplate.query(sql,
				new MapRowMapper(true));

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "");
		map.put("user_name", "");

		data.add(map);
		data.addAll(query);

		msg = new Message(data);
		return msg;
	}

	/**
	 * 批量重新分配任务
	 * 
	 * @param taskbatch
	 * @return
	 */
	@RequestMapping("/taskDistribution/save2")
	@ResponseBody
	public Message taskDistributionBatch(@RequestBody TaskBatchModel taskbatch) {
		Message msg = null;
		String nextUser = taskbatch.getAssignee();
		for (TaskModel taskmodel : taskbatch.getTasks()) {
			String id = taskmodel.getId();
			try {
				Task task = taskService.createTaskQuery().taskId(id)
						.singleResult();
				List<Map<String, Object>> taskMsgList = jdbcTemplate
						.queryForList(" select group_id_ from act_ru_identitylink where task_id_='"
								+ task.getId() + "'");
				String groupId = null;
				for (Map<String, Object> map : taskMsgList) {
					groupId = map.get("group_id_").toString();
				}
				if (task.getAssignee() != null) {
					// 放弃任务
					//MemoryCacheUtil.endFlow(id, groupId, true);
					taskService.unclaim(id);
					//MemoryCacheUtil.startFlow(null, id, groupId, task
					redisUtils.claimTask(groupId, id, task.getAssignee(), null);
					//		.getCreateTime().getTime());
					msg = new Message("任务已放弃!");
				} else {
					msg = new Message("任务未被领取，无需放弃任务!");
				}

				if (nextUser != null && nextUser.trim().length() > 0) {
					//MemoryCacheUtil.endFlow(id, groupId, true);
					taskService.claim(id, nextUser);
					redisUtils.claimTask(groupId, id, task.getAssignee(), nextUser);
					//MemoryCacheUtil.startFlow(nextUser, id, groupId, task
					//		.getCreateTime().getTime());
					msg = new Message("任务已给" + nextUser + "处理!");
				}

				// 保存任务重新分配记录
				SysActTaskLog sysActTaskLog = new SysActTaskLog(taskmodel
						.getSn(), id, task.getName(), nextUser, task
						.getAssignee(), task.getTaskDefinitionKey(), taskmodel
						.getGroupId(), task.getProcessDefinitionId(), taskmodel
						.getShouDaFang(), taskmodel.getName1(), taskmodel
						.getOrderType(), DateTools.stringToDate(taskmodel
						.getOrderDate()));
				commonManager.save(sysActTaskLog);

			} catch (Exception e) {
				msg = new Message("BPM-TD-500", "系统故障, 操作失败!");
				e.printStackTrace();
			}
		}
		return msg;
	}

	/**
	 * 任务重新分配
	 * 
	 * @param taskId
	 * @param nextUser
	 * @return
	 */
	@RequestMapping("/taskDistribution/save")
	@ResponseBody
	public Message taskDistribution(String id, String nextUser, String sn,
			String groupId, String shouDaFang, String name1, String orderType,
			String orderDate,String urgentType) {
		Message msg = null;

		try {
			Task task = taskService.createTaskQuery().taskId(id).singleResult();
			List<SaleHeader> saleHeaderList=saleHeaderDao.findByCode(sn);
			SaleHeader saleHeader=null;
			SysActCTMapping mapping =null;
			//SN可能为订单头的ordercode  也可能是行项目的orderCodePosex
			//如果saleHeaderList有结果  那么说明sn是订单头的
			if(saleHeaderList!=null && saleHeaderList.size()>0){
				saleHeader=saleHeaderList.get(0);
				mapping = commonManager.getOne(saleHeader.getId(), SysActCTMapping.class);
				if(!StringUtils.isEmpty(urgentType)){
					saleHeader.setUrgentType(urgentType);
					saleHeader.setUrgentTime(new Date());
					mapping.setUrgentType(urgentType);
				}
			}else{
				//如果SaleHeaderList的size 为0   说明没结果======>  sn为行项目的orderCodePosex
				List<SaleItem> saleItemList=saleItemDao.findByOrderCodePosex(sn);
				if(saleItemList!=null && saleItemList.size()>0){
					SaleItem saleItem=saleItemList.get(0);
					mapping = commonManager.getOne(saleItem.getId(), SysActCTMapping.class);
					if(!StringUtils.isEmpty(urgentType)){
						saleItem.getSaleHeader().setUrgentType(urgentType);
						saleItem.getSaleHeader().setUrgentTime(new Date());
						mapping.setUrgentType(urgentType);
					}
					
				}
			}
			if (task.getAssignee() != null) {
				// 放弃任务
				//MemoryCacheUtil.endFlow(id, groupId, true);
				taskService.unclaim(id);
				//MemoryCacheUtil.startFlow(null, id, groupId, task
				//		.getCreateTime().getTime());
				redisUtils.claimTask(groupId, id, task.getAssignee(), null);
				msg = new Message("任务已放弃!");
			} else {
				msg = new Message("任务未被领取，无需放弃任务!");
			}

			if (nextUser != null && nextUser.trim().length() > 0) {
//				MemoryCacheUtil.endFlow(id, groupId, true);
				taskService.claim(id, nextUser);
				redisUtils.claimTask(groupId, id, task.getAssignee(), nextUser);
//				MemoryCacheUtil.startFlow(nextUser, id, groupId, task
//						.getCreateTime().getTime());
				msg = new Message("任务已给" + nextUser + "处理!");
			}

			// 保存任务重新分配记录
			SysActTaskLog sysActTaskLog = new SysActTaskLog(sn, id, task
					.getName(), nextUser, task.getAssignee(), task
					.getTaskDefinitionKey(), groupId, task
					.getProcessDefinitionId(), shouDaFang, name1, orderType,
					DateTools.stringToDate(orderDate));
			commonManager.save(sysActTaskLog);

		} catch (Exception e) {
			msg = new Message("BPM-TD-500", "系统故障, 操作失败!");
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * 领取任务
	 * 
	 * @param taskId
	 * @param userId
	 * @return
	 */
	@RequestMapping("claim")
	@ResponseBody
	public Message claim(String taskId, String uuid) {
		Message msg = null;
		try {
			if(!StringUtils.isEmpty(taskId)){
			//String str = super.getLoginUserId();
			Task singleResult = taskService.createTaskQuery().taskId(taskId)
					.singleResult();
			taskService.claim(singleResult.getId(), super.getLoginUserId());
			msg = new Message("任务已领取!");
			super.mesSendManager.sendUser("任务已领取！", super.getLoginUserId(),
					super.getLoginUserId(), true);
			}else{
				msg = new Message("任务已领取!");
			}
		} catch (Exception e) {
			msg = new Message("BPM-UCL-500", "任务放弃失败！");
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 放弃任务
	 * 
	 * @param taskId
	 * @param userId
	 * @return
	 */
	@RequestMapping("unclaim")
	@ResponseBody
	public Message unclaim(String taskId, String uuid) {
		Message msg = null;

		try {
			Task singleResult = taskService.createTaskQuery().taskId(taskId)
					.singleResult();
			taskService.unclaim(singleResult.getId());
			msg = new Message("任务已放弃!");
			super.mesSendManager.sendUser("任务已放弃！", super.getLoginUserId(),
					super.getLoginUserId(), true);
		} catch (Exception e) {
			msg = new Message("BPM-UCL-500", "任务放弃失败！");
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 炸单推送
	 * 
	 * @param executionId
	 * @return
	 */
	@RequestMapping(value = { "/friedSinglePush" }, method = RequestMethod.GET)
	@ResponseBody
	public Message friedSinglePush(String executionId) {
		Message message = null;
		try {
			// RuntimeService runtimeService = engine.getRuntimeService();
			Execution execution = runtimeService.createExecutionQuery()
					.activityId("receivetask1").executionId(executionId)
					.singleResult();
			runtimeService.signal(execution.getId());
			message = new Message("ok！");
		} catch (Exception e) {
			e.printStackTrace();
			message = new Message("S-SIGNAL-E", e.getLocalizedMessage());
		}

		return message;
	}

	/**
	 * 跳转到BPM任务列表
	 */
	@RequestMapping(value = { "/{resource}" }, method = RequestMethod.GET)
	public ModelAndView index(@PathVariable String resource,
			HttpServletRequest request, ModelAndView modelAndView) {

		String cache = request.getParameter("xxx");

		// 清理缓存
		if (cache != null && cache.equals("up")) {
			System.out.println("****更新模板*******");
			super.deleteObjectForCache(Constants.TM_GRID_PARSE, resource);
			super.deleteObjectForCache(Constants.TM_GRID_COLUMN, resource);
			super.deleteObjectForCache(Constants.TM_GRID_MODEL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_TREEMODEL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_SEARCHFORM, resource);
			super.deleteObjectForCache(Constants.TM_GRID_CONFIG, resource);
			super.deleteObjectForCache(Constants.TM_GRID_SQL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_TREESQL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_FORM, resource);
			super.deleteObjectForCache(Constants.TM_GRID_GANTMODULE, resource);

			// 更新流程模板Model层数据
			String[] keys = new String[] { "BPMHistoricTaskInstance" };
			for (String string : keys) {
				super.deleteObjectForCache(Constants.TM_GRID_MODEL, "model:"
						+ string);
			}
		}

		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "BPMApp");
		modelAndView.getModelMap().put("moduleTitle", "BPM任务列表");
		modelAndView.getModelMap().put("flowResource", resource);

		return modelAndView;
	}

	/**
	 * 获取随机任务
	 * 
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(value = { "/randomTask/{groupId}" }, method = {
			RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Message randomTask(@PathVariable String groupId, String ptype) {
		Message msg = null;
		String userId=super.getLoginUserId();
		//SysUser user=super.getLoginUser();
		/*if(!DisableIllegal.DisableIllegal()){
			msg=new Message("Disable-Illegal","疑似开挂请关闭进程");
			return msg;
		}*/
		/*if(userId.startsWith("cl")||userId.startsWith("kf")||userId.startsWith("cw")||userId.startsWith("test")){
			msg=new Message("Disable-Illegal","无订单");
			return msg;
		}else{
			redisUtils.resetTask();
			if(true){
				msg=new Message("Disable-Illegal","无订单");
				return msg;
			}
		}*/
		// System.gc();
		//重置任务池
//		if(!"admin".equals(userId)){
//			return null;
//		}
//		if (true) {
//			redisUtils.resetTask();
//		}
		// String gp_drawing_sql =
		// " SELECT * FROM ( SELECT * from ACT_RU_TASK_DRAWING  where (total_2020=0 and total_cad=0) or total_2020>0 order by create_time_ asc ) WHERE ROWNUM=1";
		//
		// String gp_drawing_cad_sql =
		// "SELECT * FROM ( SELECT * from ACT_RU_TASK_DRAWING  where  (total_2020=0 and total_cad=0) or total_2020=0 order by create_time_ asc ) WHERE ROWNUM=1";
		if("gp_drawing".equals(groupId)||"gp_material".equals(groupId)){
			Date date=new Date();
			if(!ControlTimeUtil.checkTime(date)){
				msg = new Message("RT-ER-101", "禁止领取任务！<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;任务领取时间<br/>" +
						"早上：8：30——12：30，下午：13：30——17：30");
				return msg;
			}
		}
		try {
			if ("KUNNR".equals(ptype)) {
				ptype = super.getLoginUser().getCustHeader().getKunnr();
			}
			// 如果领取的组中 未领取的单子数量为0 则提示没有随机任务
			if(redisUtils.getUnclaimedSize(groupId, ""//user.getSkillLevel()
					)==0){
				msg = new Message("RT-ER-101", "暂时还没有随机任务!");
				return msg;
			} else
			// 如果该用户这个组中有任务
			// 将任务个数与组别任务个数比较
				if(redisUtils.getSize(userId, groupId)>0){
					int limit = 0;
					long size = redisUtils.checkTask(userId, groupId);
					if ("gp_drawing_cad".equals(groupId)) {
						limit = 4;
					} else if ("gp_drawing".equals(groupId)) {
						limit = 2;
					} else if ("gp_material".equals(groupId)
							|| "gp_hole_examine".equals(groupId)
							||"gp_bg_material".equals(groupId)) {
						limit = 5;
					} else if ("gp_shiftcount".equals(groupId)) {
						limit = 10;
					} else if ("gp_drawing_2020".equals(groupId)
							|| "gp_drawing_imos".equals(groupId)) {
						limit = 3;
					} else if ("gp_valuation".equals(groupId)) {
						limit = 1;
					}else if ("gp_hole_examine_error".equals(groupId)) {
						limit = 2;
					}else if("gp_customer_service".equals(groupId)) {
						limit=50;
					}else if ("bg_gp_drawing".equals(groupId)) {
						limit=10;
					}
					if (size >= limit) {
						msg = new Message("RT-ER-102", "已经领了" + size
							+ "任务,不能领取更多的任务!");
					return msg;
					}
//				}
			}
			boolean flag = false;
			int err_count=1;
			Map<String ,Integer> _map=new HashMap<String, Integer>();
			// 用来存放暂不确定的task，
			// 1 在create的bpm
			// 但是create事件没有完成，只是执行到了将taskId存入cacheMap中而已，这种task，在DB里面找不到 即
			// taskservice获取出来则为null
			// 2 抢任务的情况
			//List<String> unsureTask = new ArrayList<String>();
			// 循环领取，直到领到以及领完为止
			while (!flag) {
				if (redisUtils.getUnclaimedSize(groupId, ""//user.getSkillLevel()
						)==0) {
					msg = new Message("RT-ER-101", "暂时还没有随机任务!");
					return msg;
				} else {
					String taskId=null;
					JSONArray array =null;
					if("gp_drawing".equals(groupId)||"gp_material".equals(groupId)){
						String group = (String) super.getRequest().getSession().getAttribute("GROUPID");
						if(group!=null&&!"".equals(group)) {
							array= redisUtils.getGroupOrderTask(userId, groupId,group);
							if(array!=null){
								if(array.size()>1){
									super.mesSendManager.sendUser("此订单为<font color=#FF8C00>【"+array.getString(1)+"】</font>！！！请审单员预先尽快处理~~~","admin" , super
											.getLoginUserId(), true);
									taskId=array.getString(0);
								}else{
									taskId=array.getString(0);
								}
							}
						}else {
							msg=new Message("Disable-Illegal","请联系管理员配置领单组");
							return msg;
						}
					}else if("gp_customer_service_1".equals(groupId)) {//gp_customer_service暂时不开放
						String group = (String) super.getRequest().getSession().getAttribute("GROUPID");
						if(group!=null&&!"".equals(group)) {
							array= redisUtils.getCustomerGroupOrderTask(userId, groupId,group);//客服审核分区
							if(array!=null){
								if(array.size()>1){
									super.mesSendManager.sendUser("此订单为<font color=#FF8C00>【"+array.getString(1)+"】</font>！！！请审单员预先尽快处理~~~","admin" , super
											.getLoginUserId(), true);
									taskId=array.getString(0);
								}else{
									taskId=array.getString(0);
								}
							}
						}else {
							msg=new Message("Disable-Illegal","请联系管理员配置领单组");
							return msg;
						}
					}else if("bg_gp_drawing".equals(groupId)) {
						String group = (String) super.getRequest().getSession().getAttribute("GROUPID");
						if(group!=null&&!"".equals(group)) {
							array= redisUtils.getOrderTask(userId, groupId);
							if(array!=null){
								if(array.size()>1){
									super.mesSendManager.sendUser("此订单为<font color=#FF8C00>【"+array.getString(1)+"】</font>！！！请审单员预先尽快处理~~~","admin" , super
											.getLoginUserId(), true);
									taskId=array.getString(0);
								}else{
									taskId=array.getString(0);
								}
							}
						}else {
							msg=new Message("Disable-Illegal","请联系管理员配置领单组");
							return msg;
						}
					} else{
						array = redisUtils.getJiaJiOrderTask(groupId);
						if(array==null){
							taskId=redisUtils.getUnclaimedTask(groupId,""
									//user.getSkillLevel()
									)+"";
						}else{
							String jiaJitaskId=array.getString(0);
							taskId = jiaJitaskId;
							super.mesSendManager.sendUser("此订单为<font color=#FF8C00>【"+array.getString(1)+"】</font>！！！请审单员预先尽快处理~~~","admin" , super
									.getLoginUserId(), true);
						}
						
					}
					Task task =null;
					if(taskId!=null&&!"null".equals(taskId)){
						task = taskService.createTaskQuery().taskId(taskId).singleResult();
					}else{
						msg=new Message("Disable-Illegal","暂无订单可领取");
						return msg;
					}
					// 不确定task 中的第一种情况
					if (task==null) {
						// task_msg1 则用来装 taskservice不能找到的taskId
						 
						 
						 //modify by Mark for claim task  on 20180118 --start--
						 //如果查出來taskId的task為空 那麽進入重排  
						 //由于出现领单比流程开始的更快,建议加上缓冲次数  默认 为10
						if(_map.containsKey(taskId)){
							 err_count=_map.get(taskId);
							 err_count=err_count+1;
							 _map.put(taskId,err_count);
						 }else{
							 _map.put(taskId, 1);
							 err_count=1;
						 }
						if(err_count>=10){
						  redisUtils.endTask(null, groupId, taskId);
						}else{  //将任务放到池末,用于 缓冲
							redisUtils.startTask(null, groupId, taskId, "", new Date());
						}
						//modify by Mark for claim task  on 20180118 --end--
						continue;
					} else if (StringUtil.isEmpty(task.getAssignee())) {
						//如果任務沒有人領取  才可以領取使用
						try {
								int limit = 0;
								long size = redisUtils.checkTask(userId, groupId);
								if ("gp_drawing_cad".equals(groupId)) {
									limit = 4;
								} else if ("gp_drawing".equals(groupId)) {
									limit = 2;
								} else if ("gp_material".equals(groupId)
										|| "gp_hole_examine".equals(groupId)
										||"gp_bg_material".equals(groupId)) {
									limit = 5;
								} else if ("gp_shiftcount".equals(groupId)) {
									limit = 10;
								} else if ("gp_drawing_2020".equals(groupId)
										|| "gp_drawing_imos".equals(groupId)) {
									limit = 3;
								} else if ("gp_valuation".equals(groupId)) {
									limit = 1;
								}
								else if ("gp_hole_examine_error".equals(groupId)) {
									limit = 1;
								}else if("gp_customer_service".equals(groupId)) {
									limit=50;
								}else if ("bg_gp_drawing".equals(groupId)) {
									limit=10;
								}
								if (size >= limit) {
									msg = new Message("RT-ER-102", "已经领了" + size
										+ "任务,不能领取更多的任务!");
									return msg;
								}else{
									flag = true;
									taskService.claim(taskId, super.getLoginUser()
											.getId());
									redisUtils.claimTask(groupId, taskId, null, super.getLoginUser().getId());
									msg = new Message("随机任务已领取!");
								}
						} catch (ActivitiTaskAlreadyClaimedException e) {
							flag = false;
							redisUtils.endTask(null, groupId, taskId);
							continue;
						} catch (org.activiti.engine.ActivitiObjectNotFoundException e) {
							flag = false;
							redisUtils.endTask(null, groupId, taskId);
							continue;
						}/*catch (org.activiti.engine.ActivitiOptimisticLockingException e){
							System.out.println(groupId+"节点处理事务失败，此原因是activiti 并发执行task 更形version 导致");
						}*/
					} else {
						//如果 任務已經有人領取了 那麽將這個任務從列表中刪除
						redisUtils.endTask(null, groupId, taskId);
						continue;
					}
				}

			}
		} catch (NullPointerException e) {
		} catch (Exception e) {
			redisUtils.resetTask();
			e.printStackTrace();
			msg = new Message("RT-ER-500", e.getLocalizedMessage());
		}
		return msg;
	}
	/**
	 * 终止流程
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/endProcess/{taskId}" }, method = RequestMethod.GET)
	@ResponseBody
	public Message endProcess(@PathVariable String taskId) {
		String[] taskIds = taskId.split(",");
		StringBuffer msg = new StringBuffer();
		for (int i = 0; i < taskIds.length; i++) {
			String id = taskIds[i];
			if (ZStringUtils.isNotEmpty(id)) {
				Message m = flowManager.jump(id, "endevent1", "");
				if (m.getSuccess()) {
					msg.append("任务" + id + ":" + m.getMsg() + "<br/>");
				} else {
					msg.append("任务" + id + ":" + m.getErrorMsg() + "<br/>");
				}
			}
		}
		return new Message(msg);
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

	/**
	 * 反向排序list集合，便于驳回节点按顺序显示
	 * 
	 * @param list
	 * @return
	 */
	private List<ActivityImpl> reverList(List<ActivityImpl> list) {
		List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
		// 由于迭代出现重复数据，排除重复
		for (int i = list.size(); i > 0; i--) {
			if (!rtnList.contains(list.get(i - 1)))
				rtnList.add(list.get(i - 1));
		}
		return rtnList;
	}

	/**
	 * 根据当前任务ID，查询可以驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 */
	public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception {
		List<ActivityImpl> rtnList = null;
		// if (processOtherService.isJointTask(taskId)) {// 会签任务节点，不允许驳回
		// rtnList = new ArrayList<ActivityImpl>();
		// } else {
		rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId, null),
				new ArrayList<ActivityImpl>(), new ArrayList<ActivityImpl>());
		// }
		return reverList(rtnList);
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
		TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(
				taskId).singleResult();
		if (task == null) {
			throw new Exception("任务实例未找到!");
		}
		return task;
	}

	/**
	 * 根据任务ID获取对应的流程实例
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	private ProcessInstance findProcessInstanceByTaskId(String taskId)
			throws Exception {
		// RuntimeService runtimeService = engine.getRuntimeService();
		// 找到流程实例
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery().processInstanceId(
						findTaskById(taskId).getProcessInstanceId())
				.singleResult();
		if (processInstance == null) {
			throw new Exception("流程实例未找到!");
		}
		return processInstance;
	}

	/**
	 * 迭代循环流程树结构，查询当前节点可驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param currActivity
	 *            当前活动节点
	 * @param rtnList
	 *            存储回退节点集合
	 * @param tempList
	 *            临时存储节点集合（存储一次迭代过程中的同级userTask节点）
	 * @return 回退节点集合
	 */
	private List<ActivityImpl> iteratorBackActivity(String taskId,
			ActivityImpl currActivity, List<ActivityImpl> rtnList,
			List<ActivityImpl> tempList) throws Exception {
		// 查询流程定义，生成流程树结构
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

		// 当前节点的流入来源
		List<PvmTransition> incomingTransitions = currActivity
				.getIncomingTransitions();
		// 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
		List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
		// 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
		List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
		// 遍历当前节点所有流入路径
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			ActivityImpl activityImpl = transitionImpl.getSource();
			String type = (String) activityImpl.getProperty("type");
			/**
			 * 并行节点配置要求：<br>
			 * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
			 */
			if ("parallelGateway".equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId
						.lastIndexOf("_") + 1);
				if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
					return rtnList;
				} else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
					parallelGateways.add(activityImpl);
				}
			} else if ("startEvent".equals(type)) {// 开始节点，停止递归
				return rtnList;
			} else if ("userTask".equals(type)) {// 用户任务
				tempList.add(activityImpl);
			} else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
				currActivity = transitionImpl.getSource();
				exclusiveGateways.add(currActivity);
			}
		}

		/**
		 * 迭代条件分支集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : exclusiveGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}

		/**
		 * 迭代并行集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : parallelGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}

		/**
		 * 根据同级userTask集合，过滤最近发生的节点
		 */
		currActivity = filterNewestActivity(processInstance, tempList);
		if (currActivity != null) {
			// 查询当前节点的流向是否为并行终点，并获取并行起点ID
			String id = findParallelGatewayId(currActivity);
			if (id != null) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
				rtnList.add(currActivity);
			} else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
				currActivity = findActivitiImpl(taskId, id);
			}

			// 清空本次迭代临时集合
			tempList.clear();
			// 执行下次迭代
			iteratorBackActivity(taskId, currActivity, rtnList, tempList);
		}
		return rtnList;
	}

	/**
	 * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
	 * 
	 * @param activityImpl
	 *            当前节点
	 * @return
	 */
	private String findParallelGatewayId(ActivityImpl activityImpl) {
		List<PvmTransition> incomingTransitions = activityImpl
				.getOutgoingTransitions();
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			activityImpl = transitionImpl.getDestination();
			String type = (String) activityImpl.getProperty("type");
			if ("parallelGateway".equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId
						.lastIndexOf("_") + 1);
				if ("END".equals(gatewayType.toUpperCase())) {
					return gatewayId.substring(0, gatewayId.lastIndexOf("_"))
							+ "_start";
				}
			}
		}
		return null;
	}

	/**
	 * 根据流入任务集合，查询最近一次的流入任务节点
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param tempList
	 *            流入任务集合
	 * @return
	 */
	private ActivityImpl filterNewestActivity(ProcessInstance processInstance,
			List<ActivityImpl> tempList) {
		while (tempList.size() > 0) {
			ActivityImpl activity_1 = tempList.get(0);
			HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
					processInstance, activity_1.getId());
			if (activityInstance_1 == null) {
				tempList.remove(activity_1);
				continue;
			}

			if (tempList.size() > 1) {
				ActivityImpl activity_2 = tempList.get(1);
				HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
						processInstance, activity_2.getId());
				if (activityInstance_2 == null) {
					tempList.remove(activity_2);
					continue;
				}

				if (activityInstance_1.getEndTime().before(
						activityInstance_2.getEndTime())) {
					tempList.remove(activity_1);
				} else {
					tempList.remove(activity_2);
				}
			} else {
				break;
			}
		}
		if (tempList.size() > 0) {
			return tempList.get(0);
		}
		return null;
	}

	/**
	 * 查询指定任务节点的最新记录
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param activityId
	 * @return
	 */
	private HistoricActivityInstance findHistoricUserTask(
			ProcessInstance processInstance, String activityId) {
		HistoricActivityInstance rtnVal = null;
		// HistoryService historyService = engine.getHistoryService();
		// 查询当前流程实例审批结束的历史节点
		List<HistoricActivityInstance> historicActivityInstances = historyService
				.createHistoricActivityInstanceQuery().activityType("userTask")
				.processInstanceId(processInstance.getId()).activityId(
						activityId).finished()
				.orderByHistoricActivityInstanceEndTime().desc().list();
		if (historicActivityInstances.size() > 0) {
			rtnVal = historicActivityInstances.get(0);
		}

		return rtnVal;
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
	 * 根据任务ID和节点ID获取活动节点 <br>
	 * 
	 * @param taskId
	 *            任务ID
	 * @param activityId
	 *            活动节点ID <br>
	 *            如果为null或""，则默认查询当前活动节点 <br>
	 *            如果为"end"，则查询结束节点 <br>
	 * 
	 * @return
	 * @throws Exception
	 */
	private ActivityImpl findActivitiImpl(String taskId, String activityId)
			throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

		// 获取当前活动节点ID
		if (activityId != null) {
			activityId = findTaskById(taskId).getTaskDefinitionKey();
		}

		// 根据流程定义，获取该流程实例的结束节点
		if (activityId != null && activityId.toUpperCase().equals("END")) {
			for (ActivityImpl activityImpl : processDefinition.getActivities()) {
				List<PvmTransition> pvmTransitionList = activityImpl
						.getOutgoingTransitions();
				if (pvmTransitionList.isEmpty()) {
					return activityImpl;
				}
			}
		}

		// 根据节点ID，获取对应的活动节点
		ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
				.findActivity(activityId);

		return activityImpl;
	}

	/**
	 * 下单界面提交 (激活流程-》提交流程)
	 * 
	 * @param currentflow
	 *            当前环节编号
	 * @param nextflow
	 *            下一环节
	 * @param mappingId
	 *            关联表单id
	 * @param mappingNo
	 *            关联表单编号
	 * @param desc
	 *            代办意见
	 * @param errType
	 *            出错类型
	 * @param errDesc
	 *            退回原因
	 * @return
	 */
	@RequestMapping(value = { "/saleFlowCmmit" }, method = RequestMethod.POST)
	@ResponseBody
	public Message saleFlowCmmit(String saleId, String orderType) {
		Message message = null;
		try {
			if (!"".equals(saleId) && saleId != null) {
				List<Map<String, Object>> ql = jdbcTemplate
						.queryForList("select cur.act_name_ from act_ord_curr_node4 cur where cur.id='"
								+ saleId + "'");

				if (ql.size() > 0) {
					Object val = ql.get(0).get("ACT_NAME_");
					if (!"起草".equals(val) && !"".equals(val)
							&& !"null".equals(val) && val != null&&!"客户起草".equals(val)&&!"客服起草".equals(val)) {
						return new Message("SALE-V-500", "该单不是起草状态，提交失败！");
					}
				}
				String cl = "select count(cl.id) lnum from sale_item cl where nvl(cl.state_audit,'C')<>'QX' and cl.pid='"
						+ saleId + "'";
				List<Map<String, Object>> cls = jdbcTemplate.queryForList(cl);
				String num = cls.get(0).get("LNUM").toString();
				if(!"OR3".equals(orderType)&&!"OR4".equals(orderType)) {
					if ("0".equals(num)) {
						return new Message("SALE-V-500", "订单没有行项目，不能提交");
					}
				}
				String sql = "select nvl(wm_concat(l.maktx),'Q') maktx from sale_item l where l.is_standard='0' and l.material_head_id in (select sl.material_head_id from sale_item sl where sl.is_standard='0' and sl.pid = '"
						+ saleId + "') and l.pid <> '" + saleId + "'";
				List<Map<String, Object>> msql = jdbcTemplate.queryForList(sql);
				if (msql.size() > 0
						&& !"Q".equals(msql.get(0).get("MAKTX").toString())) {
					return new Message("SALE-V-500",
							"一下产品已经下过单,不能再下,请重新新建产品。 :"
									+ msql.get(0).get("MAKTX").toString());
				}
			}

			// 激活流程
			String bpm = "MainProductQuotation";
			if ("buDan" == orderType || "OR3".equals(orderType)
					|| "OR4".equals(orderType)) {
				//做个标识
				bpm = "NewCustomerServiceOrdProcess";
			}

			message = this.startFlow(bpm, saleId);
			if (!message.getSuccess()
					&& "YES-START-TASK".equals(message.getMsg())) {
				message = this.startedFlow(saleId, "search");
			}
			if (message.getSuccess()) {
				// 提交流程
				HashMap<String, Object> map = (HashMap<String, Object>) message
						.getObj();
				String currentflow = (String) map.get("taskId");
				String nextflow = null;
				if("OR3".equals(orderType)||"OR4".equals(orderType)) {
					 nextflow = "flow2";
				}else {
					 nextflow = "OR2".equals(orderType)?"flow_usertask_valuation_z":"flow_usertask_drawing_z";//"flow2";
				}
				String mappingId = saleId;
				String mappingNo = null;
				String desc = "下单提交";
				String errType = null;
				String errDesc = null;
				String taskName =  (String) map.get("taskName");
					message = this.commitFlow(currentflow, nextflow, mappingId,
							mappingNo, desc, errType, errDesc,"","");
					// 提交成功后，更新非标产品的状态 C C=已下单并不是起草状态
					if (message.getSuccess()) {
						//更新订单状态为待报价
						jdbcTemplate.update("update sale_header sh set order_status='0' where sh.id=?",new Object[]{saleId});
						
						jdbcTemplate
						.update("update material_head h set h.ROW_STATUS='C' where h.is_standard=0 and h.id in ( select sl.material_head_id from sale_item sl where sl.pid='"
								+ saleId + "')");
						String sql = "SELECT H.ORDER_CODE FROM SALE_HEADER H WHERE H.ID='"
								+ saleId + "'";
						String serialsql = "SELECT H.SERIAL_NUMBER FROM SALE_HEADER H WHERE H.ID='"
								+ saleId + "'";
						List<Map<String, Object>> sale = jdbcTemplate
								.queryForList(sql);
						List<Map<String, Object>> serialsael = jdbcTemplate
								.queryForList(serialsql);
						String orderCode = (String) sale.get(0).get("ORDER_CODE");
						message = new Message(orderCode);
						
					}
			} else {
				return message;
			}

		} catch (Exception e) {
			message = new Message("S-COMMIT-E", e.getLocalizedMessage());
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * 财务确认-批量 首页
	 * 
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = { "/finance/index" }, method = RequestMethod.GET)
	public ModelAndView financeBatchIndex(ModelAndView modelAndView) {
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "FinanceApp");
		modelAndView.getModelMap().put("moduleTitle", "财务确认");
		return modelAndView;
	}

	/**
	 * 查询财务确认数据
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/finance/datalist")
	@ResponseBody
	public JdbcExtGridBean getFinanceList(HttpServletRequest request) {
		String userId = super.getLoginUserId();
		return super
				.simpleQuery(
						"select * "
						+ "from (select (select count(*) from sys_file sf where b.id = sf.foreign_id) fcount,"
						+ "DECODE(SJP.JOB_STATUS,'A','系统正在拆单','B','系统正在过账','C','系统已过账','D','需手动过账', '未进入过账池') BILL_NAME, "
						+ "a.uuid, sjp.job_status,DECODE(sjp.SAP_STATUS,'N','推单失败','Y','推单成功') SAP_STATUS, "
						+ "a.group_id, a.assignee, "
						+ "a.claim_Time, c.name1, "
						+ "a.task_id,c.regio, "
						+ "b.order_code,b.shou_da_fang,b.order_type,"
						+ "b.order_date,b.order_total,"
						+ "b.fu_fuan_cond,b.fu_fuan_money,"
						+ "b.handle_time,b.load_time,sjp.msg as error_msg,"
						+ "c.bzirk "
						+ "from view_bpm_activity_task a,sale_header b,cust_header c, sys_job_pool  sjp "
						+ "where a.uuid = b.id "
						+ "and c.kunnr = b.shou_da_fang  and b.order_code = sjp.zuonr(+) "
						+ "and a.group_id = 'gp_finance' and nvl(sjp.job_status, 'A') not in ('C')) "
						+ "where 1 = 1 and (assignee is null or assignee = '"+userId+"')", false, null, request);
	}

	/**
	 * 批量审批
	 * 
	 * @param commitBatchModel
	 * @return
	 */
	@RequestMapping(value = { "/commitBatch" }, method = RequestMethod.POST)
	@ResponseBody
	public Message commitBatch(@RequestBody CommitBatchModel commitBatchModel) {
		Set<Message> msgSet = new HashSet<Message>();
		List<String> mappingIds = commitBatchModel.getMappingIds();// 订单ID
		List<String> mappingNos = commitBatchModel.getMappingNos();// 订单编号
		List<String> currentflows = commitBatchModel.getCurrentflows();// 任务Id
		List<String> nextflows = commitBatchModel.getNextflows();
		if (mappingIds.size() != mappingNos.size()) {
			msgSet.add(new Message("出现未知异常！"));
			return new Message(msgSet);
		}

		// 任务领取人为空，则先领取任务
		for (String taskId : currentflows) {
			Task singleResult = taskService.createTaskQuery().taskId(taskId)
					.singleResult();
			if (singleResult != null) {
				if (singleResult.getAssignee() != null
						&& singleResult.getAssignee().trim().length() > 0) {

				} else {
					claim(taskId, null);// 先领取任务
				}
			}
		}

		for (int i = 0; i < mappingIds.size(); i++) {
			String id = mappingIds.get(i);
			String no = mappingNos.get(i);
			//String nextflow = nextflows.get(i);
			//String currentflow = currentflows.get(i);

			Message msg = validateTranSap(id);
			if (msg.getSuccess()) {
				// msg = commitFlow(currentflow, nextflow, id, no, " ", null,
				// null);// 提交
			}
			if (!msg.getSuccess()) {// 审批不成功
				msgSet.add(new Message(no + ":" + msg.getErrorMsg()));
			}
		}
		if (msgSet.size() < 1) {
			msgSet.add(new Message("全部审批成功"));
		}
		return new Message(msgSet);

	}

	@RequestMapping(value = { "/manageErrorOrder" }, method = RequestMethod.POST)
	@ResponseBody
	public Message manageErrorOrder(@RequestBody CommitBatchModel commitBatchModel) {
		Message msg=null;
		List<String> orderCodes = commitBatchModel.getMappingIds();
		for (String orderCode : orderCodes) {
			Map<String, String[]> parameterMap = new HashMap<String, String[]>();
			parameterMap.put("ICEQzuonr", new String[] { orderCode });
			List<SysJobPool> resultList = commonManager.queryByRange(
					SysJobPool.class, parameterMap);
			if(resultList.size()>0) {
				SysJobPool sysJobPool = resultList.get(0);
				List<SaleItem> saleItems = saleItemDao.findByItems(orderCode);
				if(saleItems.size()>0){sysJobPool.setSapStatus("N");}else{sysJobPool.setSapStatus("Y");}
				if("N".equals(sysJobPool.getSapStatus())) {//如果是N 则表示未生成SAP号 P表示部分生成SAP号
					sysJobPool.setJobStatus("A");
					sysJobPool.setJobType("CREDIT_JOB");
					sysJobPool.setSourceType("B");
				}else {//否则表示已经生成
					sysJobPool.setJobStatus("B");
				}
				commonManager.save(sysJobPool);
			}else {
				//则需要生成一条新数据
			}
		}
		msg = new Message("处理成功");
		return msg;
	}
	/**
	 * 根据订单抬头Id 检查信贷释放订单
	 * 
	 * @param saleHeadId
	 * @return
	 */
	public Message validateTranSap(String saleHeadId) {

		//String sql = "select (select c.bukrs from Cust_Header c where c.kunnr=h.shou_da_fang and rownum=1) BUKRS,h.SHOU_DA_FANG,NVL(H.ORDER_CODE,0) ORDER_CODE,H.ORDER_TOTAL,NVL(H.SAP_ORDER_CODE,NULL) SAP_ORDER_CODE from sale_header h where h.id=? ";
		//List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql,saleHeadId);
		SaleHeader saleHeader = saleHeaderDao.findOne(saleHeadId);
		if(saleHeader==null) {
			//订单不存在
			return new Message("SD-CK-500", "没有找到有效订单");
		}
		SysJobPoolManager jobPoolManager = SpringContextHolder.getBean("sysJobPoolManagerImpl");
		SysActCTMappingDao sysActCTMappingDao = SpringContextHolder.getBean("sysActCTMappingDao");
		String orderCode=saleHeader.getOrderCode();
		SysJobPool sysJobPool = sysJobPoolDao.findByZuonr(orderCode);
		if(sysJobPool==null) {
			sysJobPool = new SysJobPool(orderCode, "A", "B",
					"CREDIT_JOB","N");//初始化 任务池
			sysJobPool.setIsFreeze("0");//在任务池 代表此任务 并未结束
			sysJobPool.setBeginDate(new Date());
			SysActCTMapping mapping = sysActCTMappingDao.findOne(saleHeadId);
			if(mapping!=null) {
				sysJobPool.setProcInstId(mapping.getProcinstid());
			}
			List<SaleItem> saleItemList = saleItemDao.findByItems(saleHeadId);
			if(saleItemList.size()<=0) {
				sysJobPool.setSapStatus("Y");//如果SAP 编号 为null 没有一个 则代表 已经生成SAP号了 此时的任务状态应该是 在处理中 且为手工处理
				sysJobPool.setJobStatus("B");
				sysJobPool.setJobType("SALE_JOB");
			}
			/*return new Message("SD-CK-500", "单号:" + orderCode + "非任务");*/
		}
		if("N".equals(sysJobPool.getSapStatus())) {
			//N 表示 未生成 SAP 号 未生成SAP号 需要生成 不可以进行释放订单
			//如果 为N 保存 任务
			commonManager.save(sysJobPool);
			return new Message("SD-CK-500", orderCode + "未生成SAP号");
		}
		if(!"SALE_JOB".equals(sysJobPool.getJobType())) {
			// 非 则表示 还在自动过账任务中 不可进行 手工释放订单
			return new Message("SD-CK-500", orderCode + "还在自动过账任务中");
		}
		sysJobPool.setSourceType("A");
		/*if (queryForList.size() > 0) {

			Map<String, Object> map = queryForList.get(0);

			//Object sapCode = map.get("SAP_ORDER_CODE");
			String ordCode = String.valueOf(map.get("ORDER_CODE"));
			if (sapCode != null && sapCode.toString().trim().length() > 0) {

			} else {
				return new Message("SD-CK-500", "单号" + ordCode + "未产生SAP单号!");
			}

			// System.out.println(1/0);

			// 2016二期优化：财务审核环节，如果信贷额度够返回true
			SysJobPool jobPool = new SysJobPool();
			Map<String, String[]> parameterMap = new HashMap<String, String[]>();
			parameterMap.put("ICEQjobType", new String[] { "SALE_JOB" });
			parameterMap.put("ICEQsourceType", new String[] { "B" });
			parameterMap.put("ICEQzuonr", new String[] { ordCode });
			List<SysJobPool> resultList = commonManager.queryByRange(
					SysJobPool.class, parameterMap);
			if (resultList != null && resultList.size() > 0) {
				jobPool = resultList.get(0);
				//jobPool.setJobStatus("C");
				//*//**获取最新的sap号防止释放错误*//*
				//jobPool.setSapCode(String.valueOf(sapCode));
			} else {
				jobPool = new SysJobPool("", "SALE_JOB", "P");
				jobPool.setZuonr(ordCode);
				//jobPool.setJobStatus("C");
				jobPool.setKunnr(String.valueOf(map.get("SHOU_DA_FANG")));
				//jobPool.setSapCode(String.valueOf(sapCode));
				jobPool.setBukrs(String.valueOf(map.get("BUKRS")));
				BigDecimal bigDecimal = (BigDecimal) map.get("ORDER_TOTAL");

				jobPool.setNetwr(bigDecimal.doubleValue());
				
			}

		} else {
			return new Message("SD-CK-500", "单号" + saleHeadId + "不存在");
		}*/
		return jobPoolManager.checkCreditBYCust(sysJobPool);
	}

}
