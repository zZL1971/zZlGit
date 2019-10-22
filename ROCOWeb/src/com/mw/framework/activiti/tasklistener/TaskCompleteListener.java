/**
 *
 */
package com.mw.framework.activiti.tasklistener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.util.StringUtils;
import com.main.controller.SaleController;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SysJobPoolDao;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sys.SysJobPool;
import com.main.service.TaskLogService;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.redis.RedisUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

/**
 * 订单审绘监听器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.tasklistener.TaskCompleteListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-17
 * 
 */
@SuppressWarnings("serial")
public class TaskCompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// nextflow判断是退回还是提交
		String taskId = null;
		String groupId = null;
		Object assignee = null;
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		SaleController saleController = SpringContextHolder
				.getBean("saleController");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		SaleItemDao saleItemDao=SpringContextHolder.getBean("saleItemDao");
		SysJobPoolDao sysJobPoolDao=SpringContextHolder.getBean("sysJobPoolDao");
		Map<String, Object> variables = delegateTask.getVariables();
		 Object ordercode = variables.get("pnumber");
		 Object ordertype = variables.get("orderType");
//			String usergroup = (String) delegateTask
//					.getVariable("groupOfVariableInSubProcess");
			variables.put("bgordercode", ordercode);
			delegateTask.setVariables(variables);
		try {
			String nextflow = (String) delegateTask.getVariable("nextflow");

			CommonManager commonManager = SpringContextHolder
					.getBean("commonManager");
			JdbcTemplate jdbcTemplate = SpringContextHolder
					.getBean("jdbcTemplate");

			Object variable = delegateTask.getVariable("uuid");
			// SaleHeaderDao saleHeaderDao =
			// SpringContextHolder.getBean("saleHeaderDao");

			// 更新主表状态
			String headerSql = "update SALE_HEADER set CHECK_DRAW_USER='"
				+ delegateTask.getAssignee() + "' where id='" + variable
					+ "'";
			int update = jdbcTemplate.update(headerSql);
			if (update != 1) {
				System.out.println(1 / 0);
			}
			/*
			 * SaleHeader saleHeader =
			 * saleHeaderDao.findOne(variable.toString()); if (saleHeader !=
			 * null) { saleHeader.setCheckDrawUser(delegateTask.getAssignee());
			 * saleHeaderDao.save(saleHeader); }
			 */
			// SaleItemDao saleItemDao =
			// SpringContextHolder.getBean("saleItemDao");

			// 提交到下一步
			taskId = delegateTask.getId();
			if (!nextflow.startsWith("flow_rt_")) {
				List<String> keys = new ArrayList<String>();
				List<Map<String, Object>> query = jdbcTemplate
						.query(
								"select a.id,a.material_head_id,a.is_standard,a.state_audit from SALE_ITEM a,material_head b "
										+ " where a.Material_Head_Id=b.id and a.pid='"
										+ variable + "'",
								new MapRowMapper(true));
				for (Map<String, Object> map : query) {
					// 非标
					if ("0".equals(map.get("isStandard"))) {
						// D：柜体审核完成，E：已审价，QX:取消 ---不走子流程
						if (!("D".equals(map.get("stateAudit"))
								|| "E".equals(map.get("stateAudit")) || "QX"
								.equals(map.get("stateAudit")))) {
							MaterialHead materialHead = commonManager.getOne(
									map.get("materialHeadId").toString(),
									MaterialHead.class);
							if (materialHead.getDrawType() != null) {
								keys.add(materialHead.getDrawType()
										+ map.get("id"));
							}
							System.out.print(map.get("id").toString());
							String itemSql = "update SALE_ITEM set STATE_AUDIT='B' where id='"
									+ map.get("id").toString() + "'";
							int update2 = jdbcTemplate.update(itemSql);

							if (update2 != 1) {
								System.out.println(1 / 0);
							}

							// 只要是有parentId的 行项目需要删除，imos信息需要删除，其余的行号重排
							String sql = "select order_code_posex from sale_item si where si.parent_id='"
									+ map.get("id").toString() + "'";
							List<Map<String, Object>> list = jdbcTemplate
									.queryForList(sql);
							if (list != null && list.size() > 0) {
								for (Map<String, Object> map1 : list) {
									sql = "delete from sale_item_price where sale_itemid=(select si.id from sale_item si where si.order_code_posex=?)";
									jdbcTemplate.update(sql,
											new Object[] { map1
													.get("ORDER_CODE_POSEX") });
									sql = "delete from sale_item where order_code_posex=?";
									jdbcTemplate.update(sql,
											new Object[] { map1
													.get("ORDER_CODE_POSEX") });
									sql = "delete from imos_idbext where orderid=?";
									jdbcTemplate.update(sql,
											new Object[] { map1
													.get("ORDER_CODE_POSEX") });
								}
								// 其余行号重排序
								saleController
										.rearrangePosex((String) variable);
							}
							// B：已审绘
							// SaleItem saleItem =
							// saleItemDao.findOne(map.get("id").toString());
							// saleItem.setStateAudit("B");
						}
					}
				}
				// 从表数据
				// System.out.println("推送子流程完成!");
				// 绘图类型+UUID
				delegateTask.setVariable("assigneeList", keys);
				saleController.rearrangePosex((String) variable);
				//记录任务结束
				Set<IdentityLink> set = delegateTask.getCandidates();
				for (IdentityLink identityLink : set) {
					groupId = identityLink.getGroupId();
				}
				taskLogService.complete(taskId,groupId);
			} else {
				// 退回
				// 订单在退回的时候,只要有sap订单就删除PO号,清楚SAP订单号
				if (variable != null) {
					SaleHeader sh = commonManager.getOne(variable.toString(),
							SaleHeader.class);
					//OMS 系统 三期优化 将SAP_CODE 移至 行项中， 退回时，需要将 行项 中 所有SAP 号删除 以及 删除过账池 数据
					if(sh!=null) {
						List<SaleItem> saleItemList = saleItemDao.findItemsByPid(sh.getId());
						if(saleItemList.size()>0) {
							for (SaleItem saleItem : saleItemList) {
								String sapCode = saleItem.getSapCode();
								JCoDestination connect = com.mw.framework.sap.jco3.SAPConnect
										.getConnect();
								JCoFunction function = connect
										.getRepository().getFunction(
												"ZRFC_SD_CHANGE_SO_PO");
								JCoParameterList importParameterList = function
										.getImportParameterList();
								importParameterList.setValue("I_VBELN", sapCode);
								function.execute(connect);
								saleItem.setSapCode("");
								saleItem.setSapCodePosex("");
								commonManager.save(saleItem);
							}
						}else {
							// 如果不存在 行项号 需要在 流程记录中添加一条记录 保存记录
							return;
						}
						SysJobPool sysJobPool = sysJobPoolDao.findByZuonr(sh.getOrderCode());
						if(sysJobPool!=null) {
							commonManager.delete(sysJobPool);
						}
					}else {
						// 流程 记录有问题 可能存在 需要退回 的操作
						return;
					}
					/*if (sh.getSapOrderCode() != null
							&& !StringUtils.isEmpty(sh.getSapOrderCode())) {
						com.sap.conn.jco.JCoDestination connect = com.mw.framework.sap.jco3.SAPConnect
								.getConnect();
						com.sap.conn.jco.JCoFunction function = connect
								.getRepository().getFunction(
										"ZRFC_SD_CHANGE_SO_PO");
						com.sap.conn.jco.JCoParameterList importParameterList = function
								.getImportParameterList();
						importParameterList.setValue("I_VBELN", sh
								.getSapOrderCode());
						function.execute(connect);
						sh.setSapOrderCode(null);
					}*/
				}
			}
			// 流程缓存
			assignee = delegateTask.getAssignee();
			String actId = delegateTask.getTaskDefinitionKey();
			Set<IdentityLink> set = delegateTask.getCandidates();
			groupId = null;
			for (IdentityLink identityLink : set) {
				groupId = identityLink.getGroupId();
			}
			String procDefId = delegateTask.getProcessDefinitionId();
			Object uuid = delegateTask.getVariable("uuid");
			if (uuid != null) {
				String orderType = jdbcTemplate.queryForObject("SELECT SH.ORDER_TYPE FROM SALE_HEADER SH WHERE SH.ID='"+uuid+"'", String.class);
				if(orderType!=null) {
					if("OR3".equals(orderType)||"OR4".equals(orderType)) {
						groupId = "bg_gp_drawing";
					}else {
						List<Map<String, Object>> fileCount = jdbcTemplate
								.queryForList("select * from (select a1.ID from sale_header a1, sale_item b1, material_head c1 where a1.id = b1.pid and b1.material_head_id = c1.id and (c1.file_Type = '1' or c1.file_type is null) group by a1.id, c1.file_Type having count(*) > 0) where id = '"
										+ uuid + "'");
						if (fileCount.size() > 0) {
							groupId = "gp_drawing";
						} else {
							groupId = "gp_drawing_cad";
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisUtils.endTask(assignee, groupId, taskId);
//			MemoryCacheUtil.endFlow(taskId, groupId, false);
		}
	}

}
