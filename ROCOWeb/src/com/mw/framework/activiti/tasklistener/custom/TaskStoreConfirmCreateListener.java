/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.main.dao.SaleItemDao;
import com.main.domain.sale.SaleItem;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysActCTOrdErrDao;
import com.mw.framework.domain.SysActCTOrdErr;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.MessageUtils;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

/**
 * 客户确认监听器（创建）
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName 
 *           com.mw.framework.activiti.tasklistener.custom.TaskStoreConfirmCreateListener
 *           .java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-1
 * 
 */
@SuppressWarnings("serial")
public class TaskStoreConfirmCreateListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		/*
		 * System.out.println("客户确认自动给回销售订单创建人"); String uuid = (String)
		 * delegateTask.getVariable("uuid"); SaleHeaderDao saleHeaderDao =
		 * SpringContextHolder.getBean("saleHeaderDao"); SaleHeader one =
		 * saleHeaderDao.getOne(uuid); if(one!=null){
		 * delegateTask.setAssignee(one.getCreateUser()); }
		 */

		// 获取历史环节，最后一次财务退回原因。
		// 财务退回原因：客户改图，则重新推单
		TaskService taskService = SpringContextHolder.getBean("taskService");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		SysActCTOrdErrDao sysActCTOrdErrDao = SpringContextHolder.getBean("sysActCTOrdErrDao");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		Object nextflow = taskService.getVariable(delegateTask.getId(),
				"nextflow");
		Object uuid = taskService.getVariable(delegateTask.getId(), "uuid");

		// 客户确认将订单状态改成待付款
		jdbcTemplate.update(
				"update sale_header sh set order_status='1' where sh.id=?",
				new Object[] { uuid });

		// Object nextTask = taskService.getVariable(delegateTask.getId(),
		// "nextTask");
		if (nextflow != null && nextflow.toString().startsWith("flow_rt_")) {// 财务退回
			try {
				if(uuid!=null) {
					SysActCTOrdErr sysActCTOrdErr = sysActCTOrdErrDao.findByMappingSidAndErrTypeIsS02(uuid.toString());
					if(sysActCTOrdErr!=null) {
						List<SaleItem> saleItemList = saleItemDao.findBySapCodeBeGroupBy(uuid.toString());
						if(saleItemList.size()>0) {
							for (SaleItem saleItem : saleItemList) {
								JCoDestination connect = SAPConnect.getConnect();
								JCoFunction function = connect.getRepository().getFunction("ZRFC_SD_CHANGE_SO_PO");
								JCoParameterList importParameterList = function
										.getImportParameterList();
								importParameterList.setValue("I_VBELN", saleItem.getSapCode());
								function.execute(connect);
								String string = function.getExportParameterList().getString("E_TYPE");
								if ("S".equals(string)) {
									saleItem.setSapCode("");
									saleItem.setSapCodePosex("");
									commonManager.save(saleItem);
								}
							}
						}
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
				return;
			}
			
			/*String sql = "SELECT E.ERR_TYPE,(SELECT H.ORDER_CODE FROM SALE_HEADER H WHERE H.ID=E.MAPPING_ID) ORDER_CODE,(SELECT H.SAP_ORDER_CODE FROM SALE_HEADER H WHERE H.ID=E.MAPPING_ID) SAP_ORDER_CODE FROM  ACT_CT_ORD_ERR E WHERE E.CREATE_TIME=(select max(er.create_time) from  ACT_CT_ORD_err er  where er.task_definition_key=e.task_definition_key and er.target_task_definition_key=e.target_task_definition_key and er.mapping_id=e.mapping_id) and e.task_definition_key='usertask_finance' and e.target_task_definition_key='usertask_store_confirm' and err_type='S02' and e.mapping_id=?";
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(
					sql, uuid);
			if (queryForList.size() > 0) {
				Map<String, Object> map = queryForList.get(0);
				if ("S02".equals(map.get("ERR_TYPE"))) {// 客户改图
					Object sapcode = map.get("SAP_ORDER_CODE");
					if (sapcode != null
							&& sapcode.toString().trim().length() > 0) {
						// 调用SAP接口处理，取消SAP销售订单
						JCoDestination connect = SAPConnect.getConnect();
						try {
							JCoFunction function = connect.getRepository()
									.getFunction("ZRFC_SD_CHANGE_SO_PO");
							JCoParameterList importParameterList = function
									.getImportParameterList();
							importParameterList.setValue("I_VBELN", sapcode);
							function.execute(connect);

							String string = function.getExportParameterList()
									.getString("E_TYPE");
							if ("S".equals(string)) {
								jdbcTemplate
										.update("UPDATE SALE_HEADER SET SAP_ORDER_CODE=null WHERE ID='"
												+ uuid + "'");
								// SysJobPool jp = new
								// SysJobPool(String.valueOf(map.get("ORDER_CODE")),"A","SALE_JOB","A");
								// commonManager.save(jp);
							}
						} catch (JCoException e) {
							e.printStackTrace();
						}
					}
				}
			}*/
		}
		// 流程缓存
		String taskId = delegateTask.getId();// jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		Set<IdentityLink> set = delegateTask.getCandidates();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}
		Object assignee = delegateTask.getVariable("assignee");
		
		String orderCode = jdbcTemplate.queryForObject(
				"select sh.order_code from sale_header sh where sh.id='" + String.valueOf(uuid)
						+ "'", String.class);
		Date time = new Date();
		redisUtils.startTask(assignee, groupId, taskId, null, time);
		String smsCode = null;
		Map<String, String> parameters = new HashMap<String, String>();
		if (nextflow != null
		// && !nextflow.toString().startsWith("flow_rt_")
		) {
			if (!nextflow.toString().startsWith("flow_rt_")) {
				// formatString="您好，%s单报价已经发出，请注意查收并核对产品信息，如有订单可直接扣款下单或有其他不明之处，请联系审价人员咨询。广东劳卡家具有限公司";
				smsCode = "62759";
				parameters.put("order", orderCode);
			} else {
				List<Map<String, Object>> querylist = jdbcTemplate
						.queryForList(
								"select (select desc_zh_cn from sys_data_dict where trie_id='PJy9ZBN5hBtbxtTUFwQT38' and key_val=err.err_type ) as error_type,err.err_desc from act_ct_ord_err err where err.Mapping_sId=?",
								new Object[] { orderCode });
				if (querylist != null && querylist.size() > 0) {
					// formatString="您好,您当前订单%s从财务确认退回客户确认,错误类型:%s,错误原因:%s,请知悉!如有疑问请联系本司财务人员,谢谢!广东劳卡家具有限公司";
					// Map<String,Object> map=querylist.get(0);
					// parameters=new String[]{
					// orderCode,
					// map.get("ERROR_TYPE")==null?"":map.get("ERROR_TYPE").toString(),
					// map.get("ERR_DESC")==null?"":map.get("ERR_DESC").toString()
					// };
				}

			}
			try {
				if (smsCode != null) {
					List<Map<String, Object>> _list = jdbcTemplate
							.queryForList("select tel from sys_user where kunnr=(select sh.shou_da_fang from sale_header sh where id='"
									+ uuid
									+ "') and money='1' and status='1' and (tel is not null) and rownum=1");
					// String sendMsg=String.format(formatString, parameters);
					if (_list != null && _list.size() > 0) {
						for (Map<String, Object> map2 : _list) {
							Object telephone = map2.get("TEL");
							if (telephone != null
									&& !StringUtils.isEmpty(smsCode)) {
								if(!"dev".equals(ZStringUtils.readProperties())) {
									String messageContext="您好，%s单报价已经发出，请注意查收并核对产品信息，如有订单可直接扣款下单或有其他不明之处，请联系审价人员咨询。广东劳卡家具有限公司";
									if(MessageUtils.sendMsg(smsCode, telephone
											.toString(), JSONObject
											.toJSONString(parameters))){
										jdbcTemplate.update("insert into mes_post_log(telephone,msg,send_time,ORDER_CODE,status,mes_type) values( ? ,? ,systimestamp,?,'1',?)",new Object[]{telephone,String.format(messageContext, orderCode),orderCode,smsCode});
									}else {
										jdbcTemplate.update("insert into mes_post_log(telephone,msg,send_time,ORDER_CODE,status,mes_type) values( ? ,? ,systimestamp,?,'0',?)",new Object[]{telephone,String.format(messageContext, orderCode),orderCode,smsCode});
									}
								}
							}
						}
					}
				}
			} catch (Exception E) {
				E.printStackTrace();
			}

		}

	}

}
