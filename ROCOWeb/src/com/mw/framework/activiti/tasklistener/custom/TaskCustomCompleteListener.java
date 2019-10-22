/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.task.IdentityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.main.dao.CustLogisticsDao;
import com.main.dao.MaterialHeadDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleLogisticsDao;
import com.main.domain.cust.CustLogistics;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleLogistics;
import com.main.manager.SaleManager;
import com.mw.framework.activiti.JumpTaskCmd;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.ZStringUtils;

/**
 * 起草方案监听器
 * 流程：起草-->订单审汇
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName 
 *           com.mw.framework.activiti.tasklistener.custom.TaskCustomCompleteListener
 *           .java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-20
 * 
 */
@SuppressWarnings("serial")
public class TaskCustomCompleteListener implements TaskListener {
/*	@Autowired
	private SaleManager saleManager;*/
	@Override
	public void notify(DelegateTask delegateTask) {
		Map<String, Object> variables = delegateTask.getVariables();
		Set<Entry<String, Object>> entrySet = variables.entrySet();
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		/**
		 * nextflow-->flow2 uuid-->GX38rFx39LDbjPapvwRpa7
		 */
		String uuid = (String) delegateTask.getVariable("uuid");
		Object assignee=delegateTask.getAssignee();
		// for (Entry<String, Object> entry : entrySet) {
		// // System.out.println(entry.getKey()+"-->"+entry.getValue());
		// }

		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		SaleManager saleManager = SpringContextHolder.getBean("saleManager");
		MaterialHeadDao materialHeadDao = SpringContextHolder.getBean("materialHeadDao");
		SerialNumberManager serialNumberManager = SpringContextHolder
				.getBean("serialNumberManager");
		SaleHeaderDao saleHeaderDao = SpringContextHolder
				.getBean("saleHeaderDao");
		RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
		TaskService taskService = SpringContextHolder.getBean("taskService");
		SaleLogisticsDao saleLogisticsDao = SpringContextHolder.getBean("saleLogisticsDao");
		CustLogisticsDao custLogisticsDao = SpringContextHolder.getBean("custLogisticsDao");
		Object nextflow =  taskService.getVariable(delegateTask.getId(), "nextflow");
		saleManager.taskCustomComplete(nextflow,uuid,delegateTask,redisUtils,assignee);
	}
}
