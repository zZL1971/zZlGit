/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Spring;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.MaterialHeadDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleLogisticsDao;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleLogistics;
import com.main.manager.SalePrModManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.ZStringUtils;

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
public class TaskCustomCreateListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		JdbcTemplate jdbcTemplate=SpringContextHolder.getBean("jdbcTemplate");
		RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
		HistoryService historyService=SpringContextHolder.getBean("historyService");
		SaleLogisticsDao saleLogisticsDao = SpringContextHolder.getBean("saleLogisticsDao");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		MaterialHeadDao materialHeadDao = SpringContextHolder.getBean("materialHeadDao");
        //流程缓存
		//System.out.println(delegatetask.getProcessInstanceId());
		//String taskId=jdbcTemplate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegatetask.getProcessInstanceId()+"'",String.class);
		//MemoryCacheUtil.startFlow(taskId);
		String uuid = (String) delegatetask.getVariable("uuid");
		SaleHeaderDao saleHeaderDao = SpringContextHolder
				.getBean("saleHeaderDao");
		SaleHeader saleHeader = new SaleHeader();
		
		String taskId=delegatetask.getId();//jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		Set<IdentityLink> set=delegatetask.getCandidates();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId=identityLink.getGroupId();
		}
		if(uuid!=null) {
			saleHeader = saleHeaderDao.findOne(uuid);
		}
		if("OR3".equals(saleHeader.getOrderType())||"OR4".equals(saleHeader.getOrderType())) {
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
			}else {
				new Exception("未发现节点历史操作用户!");
				String procinstid = delegatetask.getProcessInstanceId();
				String sql ="SELECT AC.ASSIGNEE_ FROM ACT_HI_ACTINST AC WHERE AC.PROC_INST_ID_='"+procinstid+"' AND AC.ACT_NAME_='客户起草'";
				List<Map<String, Object>> assigneeList = jdbcTemplate.queryForList(sql);
				if(assigneeList.size()>0){
					Map<String, Object> assignees = assigneeList.get(0);
					String assigne = (String) assignees.get("ASSIGNEE_");
					delegatetask.setAssignee(assigne);
				}
			}
//			saleHeader.setOrderStatus("A");
			saleHeaderDao.save(saleHeader);
			Object assignee=delegatetask.getAssignee();
			Date time=new Date();
			redisUtils.startTask(assignee, groupId, taskId,null, time);
		}else {
			Object assignee=delegatetask.getVariable("assignee");
			Date time=new Date();
			redisUtils.startTask(assignee, groupId, taskId,null, time);
		}
		//获取 没有审价的 或者 没有审核的
		if(saleHeader != null) {
			Map<String,String> saleForMap=new HashMap<String, String>();
			List<SaleItem> findItems = saleItemDao.findItemsByPid(saleHeader.getId());
			for (SaleItem saleItem : findItems) {
				String stateAudit = saleItem.getStateAudit();
				if(!("B".equals(stateAudit )||"D".equals(stateAudit)||"E".equals(stateAudit)||"QX".equals(stateAudit))){
					MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					if(materialHead==null) {
						throw new TypeCastException("数据异常");
					}
					saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
				}
				
			}
			List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(saleHeader.getId());
			if(saleLogisticsList.size()>0 && saleForMap.size()>0) {
				for (Entry<String,String> entry : saleForMap.entrySet()) {
					for (SaleLogistics saleLogistics : saleLogisticsList) {
						if(entry.getKey().equals(saleLogistics.getSaleFor())) {
							saleLogisticsDao.delete(saleLogistics);
						}
					}
				}
			}
		}
	}
}
