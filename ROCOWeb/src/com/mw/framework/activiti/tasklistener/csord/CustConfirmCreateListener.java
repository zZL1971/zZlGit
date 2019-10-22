/**
 *
 */
package com.mw.framework.activiti.tasklistener.csord;

import java.util.Date;
import java.util.HashMap;
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
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.manager.SalePrModManager;
import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.dao.ActOperationLogDao;
import com.mw.framework.domain.ActOperationLog;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.MessageUtils;
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
public class CustConfirmCreateListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		// 流程缓存
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		ActOperationLogDao actOperationLogDao = SpringContextHolder
				.getBean("actOperationLogDao");
		String taskId = delegatetask.getId();// jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		Set<IdentityLink> set = delegatetask.getCandidates();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}
		Object assignee = delegatetask.getVariable("assignee");
		Date time = new Date();
		// MemoryCacheUtil.startFlow((String) assignee, taskId, groupId, time);
		TaskService taskService = SpringContextHolder.getBean("taskService");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		SaleHeaderDao saleHeaderDao = SpringContextHolder
				.getBean("saleHeaderDao");
		Object nextflow = taskService.getVariable(delegatetask.getId(),
				"nextflow");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		String actId = delegatetask.getTaskDefinitionKey();
		String actName = delegatetask.getName();
		Object uuid = delegatetask.getVariable("uuid");
		String orderCode = saleHeaderDao.getOne(uuid.toString()).getOrderCode();
		String formatString = null;
		// Object[] parameters = null;
		String smsCode = null;
		Map<String, String> parameters = new HashMap<String, String>();
		if (nextflow != null) {
			if (!nextflow.toString().startsWith("flow_rt_")) {
				smsCode = "62759";
				parameters.put("order", orderCode);
			} else {
				smsCode = "63332";

				List<Map<String, Object>> querylist = jdbcTemplate
						.queryForList(
								"select (select desc_zh_cn from sys_data_dict where trie_id='PJy9ZBN5hBtbxtTUFwQT38' and key_val=err.err_type ) as error_type,err.err_desc from act_ct_ord_err err where err.Mapping_sId=?",
								new Object[] { orderCode });
				if (querylist != null && querylist.size() > 0) {
					Map<String, Object> map = querylist.get(0);
					parameters.put("order", orderCode);
					parameters.put("type", map.get("ERROR_TYPE") == null ? ""
							: map.get("ERROR_TYPE").toString());
					parameters.put("reason", map.get("ERR_DESC") == null ? ""
							: map.get("ERR_DESC").toString());
				}

			}
			try {
				if (formatString != null) {
					List<Map<String, Object>> _list = jdbcTemplate
							.queryForList("select tel from sys_user where kunnr=(select sh.shou_da_fang from sale_header sh where id='"
									+ uuid
									+ "') and money='1' and status='1' and (tel is not null) and rownum=1");
					if (_list != null && _list.size() > 0) {
						Object telephone = _list.get(0).get("TEL");
						if (telephone != null && !StringUtils.isEmpty(smsCode)) {
							MessageUtils.sendMsg(smsCode, telephone.toString(),
									JSONObject.toJSONString(parameters));
						}
					}
				}
			} catch (Exception E) {
				E.printStackTrace();
			}
		}
		redisUtils.startTask(assignee, groupId, taskId, null, time);
		// MemoryCacheUtil.startFlow((String)assignee,taskId, groupId,time);

		if (assignee == null) {
			taskLogService.create(uuid.toString(), actId, actName, taskId);
		}
	}
}
