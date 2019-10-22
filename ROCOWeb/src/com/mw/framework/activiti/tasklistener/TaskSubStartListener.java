/**
 *
 */
package com.mw.framework.activiti.tasklistener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.SaleItemDao;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.dao.SysActCTMappingDao;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.utils.ZStringUtils;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.tasklistener.TaskSubStartListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-17
 * 
 */
@SuppressWarnings("serial")
public class TaskSubStartListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {

		CommonManager commonManager = SpringContextHolder
				.getBean("commonManager");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		HistoryService historyService = SpringContextHolder.getBean("historyService");
		SysActCTMappingDao sysActCTMappingDao = SpringContextHolder.getBean("sysActCTMappingDao");
		Map<String, Object> variables = delegateExecution.getVariables();
		
//		  Set<java.util.Map.Entry<String,Object>> entrySet = variables.entrySet(); for
//		  (java.util.Map.Entry<String, Object> entry : entrySet) {
//		  System.out.println(entry.getKey()+"-->"+entry.getValue()); }
		 

		String usergroup = (String) delegateExecution
				.getVariable("groupOfVariableInSubProcess");
		String uuid = null;
		
		if (usergroup != null) {
			// delegateTask.addCandidateGroup(usergroup.toString());
			boolean result = Pattern.matches("^(1|2|3|4|5)[A-Za-z0-9._]+$",
					usergroup);
			if (result) {
				String drawType = usergroup.substring(0, 1);
				uuid = usergroup.substring(1);
				// 绘图类型4:A单, 流程与客户2020一样
				/*
				 * if("4".equals(drawType)){ drawType = "3"; }
				 */
				// 添加绘图类型
				variables.put("drawType", drawType);
				variables.put("subuuid", uuid);
				variables.put("assignee", null);
				// System.out.println(drawType+"-->"+uuid);

				// 写表
				// 2016.05.26，考虑再次重走子流程的各个环节，所以在启动子流程节点处判断是否已经走过子流程，如果走过，则添加old实例编号到mapping表

				Map<String, String[]> map = new HashMap<String, String[]>();
				map.put("ICEQid", new String[] { uuid });
				List<SysActCTMapping> queryByRange = commonManager
						.queryByRange(SysActCTMapping.class, map);

				SysActCTMapping mapping = null;
				if (queryByRange.size() > 0) {
					mapping = queryByRange.get(0);

					String act_id = "";
					if ("1".equals(drawType)) {
						act_id = "usertask1";
					} else if ("2".equals(drawType)) {
						act_id = "usertask2";
					}

					if ("1".equals(drawType) || "2".equals(drawType)) {
						String sql = "select ASSIGNEE_ as ASSIGNEE from act_hi_actinst where proc_inst_id_='"
								+ mapping.getProcinstid()
								+ "' and act_id_='"
								+ act_id
								+ "' and start_time_=(select max(start_time_) from act_hi_actinst t where proc_inst_id_='"
								+ mapping.getProcinstid()
								+ "' and act_id_='"
								+ act_id + "')";
						List<Map<String, Object>> queryForList = jdbcTemplate
								.queryForList(sql);
						if (queryForList.size() > 0) {
							variables.put("assignee",
									queryForList.get(0).get("ASSIGNEE"));
						}
					}

					mapping.setProcInstIdOld(mapping.getProcinstid());
					mapping.setProcinstid(delegateExecution
							.getProcessInstanceId());
					mapping.setProdefid(delegateExecution
							.getProcessDefinitionId());

				} else {
					
					mapping = new SysActCTMapping(uuid,
							delegateExecution.getProcessInstanceId(),
							delegateExecution.getProcessDefinitionId());

					List<Map<String, Object>> saleitem = jdbcTemplate.queryForList("SELECT SI.PID FROM SALE_ITEM SI WHERE SI.ID='"+uuid+"'");
					SaleHeader saleHeader = null;
					if(saleitem.size()>0) {
						saleHeader = commonManager.getOne(String.valueOf(saleitem.get(0).get("PID")), SaleHeader.class);
					}
					mapping.setOrderType(saleHeader.getOrderType());
					SysActCTMapping saleMapping = sysActCTMappingDao.findOne(saleHeader.getId());
					if(saleMapping!=null) {
						List<HistoricTaskInstance> history = historyService.createHistoricTaskInstanceQuery().processInstanceId(saleMapping.getProcinstid()).taskName("订单审绘").orderByTaskCreateTime().desc().list();
						String drawingAssignee = history.get(0).getAssignee();
						List<Map<String, Object>> sysTaskList = jdbcTemplate.queryForList("SELECT ST.REAR FROM SYS_TASK ST WHERE ST.ID=?",drawingAssignee);
						if(sysTaskList.size()>0) {
							String rear = ZStringUtils.resolverStr(sysTaskList.get(0).get("REAR"));
							mapping.setRegio(rear);
						}
					}
					/*List<Map<String, Object>> custHeader = jdbcTemplate.queryForList("SELECT CH.REGIO FROM CUST_HEADER CH WHERE CH.KUNNR='"+saleHeader.getShouDaFang()+"'");
					if(custHeader.size() > 0) {
						mapping.setRegio((String)custHeader.get(0).get("REGIO"));
					}*/
				}
				// SysActCTMapping mapping = new SysActCTMapping(uuid,
				// delegateExecution.getProcessInstanceId(),delegateExecution.getProcessDefinitionId());
				commonManager.save(mapping);

				delegateExecution.setVariables(variables);
			}
		} else {
			System.out.println("获取分组信息失败");
		}
		// 流程缓存
		String taskId = delegateExecution.getId();// jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		
		
		
		//异型板件
//		if(uuid!=null){
//		SaleItem saleItem = commonManager.getOne(uuid, SaleItem.class);
//		MaterialHead findOne=commonManager.getOne(saleItem.getMaterialHeadId(), MaterialHead.class);
//		List<Map<String,Object>> list=jdbcTemplate.queryForList("select * from imos_idbext where info1 in ('A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69')  and orderid=?",new Object[]{saleItem.getOrderCodePosex()});
//		if(list!=null && list.size()>0){
//			findOne.setHasPec("1");
//		}else{
//			findOne.setHasPec("0");
//		}
//		jdbcTemplate.update("update imos_idbext ii set ii.ispec='1' where ii.info1 in ( 'A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69') and orderid= ?",new Object[]{saleItem.getOrderCodePosex()});
//		//行项目 板件厚度信息的添加
//		String sql="select cthickness,(select key_val from sys_data_dict where trie_id='8ZMA82S2G5fwCbvW3oegKD' and desc_zh_cn=cthickness) as board_code from imos_idbext where (typ = '3' and (info1 like 'A%'))  and orderid= ? group by cthickness";
//		list=jdbcTemplate.queryForList(sql,new Object[]{saleItem.getOrderCodePosex()});
//		long boardThickness=0;
//		for (Map<String, Object> map : list) {
//			long code=map.get("BOARD_CODE")==null?0:Long.parseLong(map.get("BOARD_CODE").toString());
//			boardThickness|=code;
//		}
//		findOne.setBoardThickness(""+boardThickness);
//		commonManager.save(findOne);
//		
//		//ispec =4的全部改成ispec=0
//		jdbcTemplate.update("update imos_idbext set ispec='0' where ispec='4' and orderid=?",new Object[]{saleItem.getOrderCodePosex()});
//		}
		// Set<IdentityLink> set=delegateExecution.getVariables();
		// String groupId = null;
		// for (IdentityLink identityLink : set) {
		// groupId=identityLink.getGroupId();
		// }
		// MemoryCacheUtil.startFlow(taskId,groupId);
//		delegateExecution.getParentId();
//		TaskService taskService = SpringContextHolder.getBean("taskService");
//		List<IdentityLink> set = taskService
//				.getIdentityLinksForTask(taskId);
//		String groupId = null;
//		for (IdentityLink identityLink : set) {
//			groupId = identityLink.getGroupId();
//		}
//		MemoryCacheUtil.startFlow(taskId, groupId);
	}

}
