/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.SaleItemDao;
import com.main.domain.sale.SaleItem;
import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.core.MyMapRowMapper;

/**
 * 重走子流程 监听器
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
public class TaskSubDrawingCompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// String taskId=null;
		// String groupId=null;
		// try{
		// nextflow判断是退回还是提交
		String nextflow = (String) delegateTask.getVariable("nextflow");
		String drawType = "";
		// 提交到下一环节
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		TaskLogService taskLogService = SpringContextHolder
				.getBean("taskLogService");
		if (!nextflow.startsWith("flow_rt_")) {
			// saleitemid
			String id = (String) delegateTask.getVariable("subuuid");
			SaleItemDao saleItemDao = SpringContextHolder
					.getBean("saleItemDao");
			SaleItem findOne = saleItemDao.findOne(id);

			findOne.setStateAudit("B");// 已审绘
			List<Map<String, Object>> query = jdbcTemplate.query(
					"select draw_type from material_head where id='"
							+ findOne.getMaterialHeadId() + "'",
					new MyMapRowMapper());
			if (query != null && query.size() > 0) {
				Map<String, Object> map = query.get(0);
				drawType = (String) map.get("drawType");
				delegateTask.setVariable("drawType", drawType);
			}
		}
		// 流程缓存
		String taskId = delegateTask.getId();
		Object assignee = delegateTask.getAssignee();
		Object uuid = delegateTask.getVariable("subuuid");
		Set<IdentityLink> set = delegateTask.getCandidates();
		String actId = delegateTask.getTaskDefinitionKey();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}
		// 记录任务结束
		taskLogService.complete(taskId,groupId);
	}

}
