/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Spring;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.manager.SalePrModManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
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
public class TaskFinanceCreateListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		Object uuid = delegatetask.getVariable("uuid");
		//客户确认将订单状态改成待付款
        jdbcTemplate.update("update sale_header sh set order_status='2' where sh.id=?",new Object[]{uuid});
        
		String taskId=delegatetask.getId();//jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		Set<IdentityLink> set=delegatetask.getCandidates();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId=identityLink.getGroupId();
		}
		Object assignee = delegatetask.getVariable("assignee");
		Date time = new Date();
		redisUtils.startTask(assignee, groupId, taskId, null, time);
	}
}
