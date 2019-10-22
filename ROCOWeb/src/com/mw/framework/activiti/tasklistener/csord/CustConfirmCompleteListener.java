package com.mw.framework.activiti.tasklistener.csord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.util.StringUtils;
import com.main.dao.SysJobPoolDao;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemPrice;
import com.main.domain.sys.SysJobPool;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 * 客户确认监听器（完成）(补件流程)
 * 
 * @author lrz 2016-05-12
 *
 */
public class CustConfirmCompleteListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		try {
			TaskService taskService = SpringContextHolder.getBean("taskService");
			CommonManager commonManager = SpringContextHolder.getBean("commonManager");
			JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
			RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
			// String variableLocal = (String)
			// taskService.getVariableLocal(delegateTask.getId(), "nextflow");
			SysJobPoolDao sysJobPoolDao = SpringContextHolder.getBean("sysJobPoolDao");
			Object nextflow = taskService.getVariable(delegateTask.getId(), "nextflow");
			Object nextTask = taskService.getVariable(delegateTask.getId(), "nextTask");
			Object uuid = taskService.getVariable(delegateTask.getId(), "uuid");
			Object assignee = delegateTask.getAssignee();
			if (nextflow != null && !nextflow.toString().startsWith("flow_rt_")) {
				/*
				 * if("usertask_finance".equals(nextTask)){ //如不存在财务退回，则是新单，直接放入订单池 String
				 * saleHeader = "SELECT ORDER_CODE,ORDER_TYPE FROM SALE_HEADER WHERE ID=?";
				 * Map<String, Object> queryForMap = jdbcTemplate.queryForMap(saleHeader,uuid);
				 * if("OR3".equals(queryForMap.get("ORDER_TYPE"))){//部件补购单 SysJobPool jp = new
				 * SysJobPool(queryForMap.get("ORDER_CODE")+"","A","A","SALE_JOB","");
				 * commonManager.save(jp); }
				 * 
				 * }
				 */

				if ("usertask_finance".equals(nextTask)) {
					// 下一流程节点是财务确认是，放入订单池（传送到SAP系统）
					String saleHeader = "SELECT ORDER_CODE FROM SALE_HEADER WHERE ID=?";
					Map<String, Object> queryForMap = jdbcTemplate.queryForMap(saleHeader, uuid);
					if (queryForMap.size() > 0) {
						String zuonr = ZStringUtils.resolverStr(queryForMap.get("ORDER_CODE"));
						SysJobPool jp = sysJobPoolDao.findByZuonr(zuonr);
						if (jp == null) {
							jp = new SysJobPool(queryForMap.get("ORDER_CODE") + "", "A", "B", "CREDIT_JOB", "N");
						}
						jp.setIsFreeze("0");// 0 启用 1 冻结
						jp.setProcInstId(delegateTask.getProcessInstanceId());
						jp.setNum(jp.getNum() + 1);// 处理次数加一
						commonManager.save(jp);
					}
				}

				// 将前端有sap单号的订单的价格信息与sap同步
				SaleHeader saleHeader = commonManager.getOne(uuid.toString(), SaleHeader.class);
				String sapOrderCode = saleHeader.getSapOrderCode();
				if (sapOrderCode != null && !StringUtils.isEmpty(sapOrderCode)) {
					Boolean sapSuccess = null;// 同步SAP是否成功的标记
					Set<String> successMsgSet = new HashSet<String>();
					Set<String> errorMsgSet = new HashSet<String>();
					JCoDestination connect = SAPConnect.getConnect();
					JCoFunction functionPri = connect.getRepository().getFunction("ZRFC_SD_CHANGE_CANCEL");// SAP创建接口
					JCoTable imModTable = functionPri.getTableParameterList().getTable("IM_T_PRICE");// 同步修改价格的到SAP的表
					JCoTable imReTable = functionPri.getTableParameterList().getTable("IT_ZSDT007");// 同步取消的到SAP
																									// IT_ZSDT007
					List<Map<String, Object>> saleItemIdList = jdbcTemplate
							.queryForList("select id from sale_item where pid='" + uuid + "'");

					imReTable.appendRow();
					imReTable.setValue("VBELN", sapOrderCode);// SAP编码

					List<SaleItem> saleItemList = new ArrayList<SaleItem>();
					for (Map<String, Object> map : saleItemIdList) {
						SaleItem saleItem = (SaleItem) commonManager.getOne(map.get("ID").toString(), SaleItem.class);
						imReTable.appendRow();
						imReTable.setValue("VBELN", sapOrderCode);// SAP编码
						imReTable.setValue("POSNR", saleItem.getPosex());// 行项目
						// 如果行项目没有拒绝原因那么打上60
						imReTable.setValue("ABGRU",
								(saleItem.getAbgru() == null || StringUtils.isEmpty(saleItem.getAbgru()))
										? ("QX".equals(saleItem.getStateAudit()) ? "56" : "60")
										: saleItem.getAbgru());// 拒绝原因
						List<Map<String, Object>> saleItemPriceIdList = jdbcTemplate.queryForList(
								"select id from sale_item_price where sale_itemid='" + saleItem.getId() + "'");
						// 物料编码----
						String MATNR = saleItem.getMatnr();

						// 将行项目的价格信息全部放入到Immodtable 中
						for (Map<String, Object> submap : saleItemPriceIdList) {
							SaleItemPrice saleItemPrice = (SaleItemPrice) commonManager
									.getOne(submap.get("ID").toString(), SaleItemPrice.class);
							imModTable.appendRow();
							imModTable.setValue("VBELN", sapOrderCode);// SAP编码
							imModTable.setValue("POSNR", saleItem.getPosex());// 行项
							imModTable.setValue("MATNR", MATNR);// 物料编码
							imModTable.setValue("KSCHL", saleItemPrice.getType());// 价格类型pr01...
							// zr01...
							imModTable.setValue("KBETR", saleItemPrice.getSubtotal());// 价格类型小计（还没有乘以数量）
							imModTable.setValue("WAERS", "CNY");// 币别码
							imModTable.setValue("KPEIN", "1");// 条件定价单位
						}
					}

					// 获取执行结果
					int row = imModTable.getNumRows();
					if (row > 0 || imReTable.getNumRows() > 0) {
						functionPri.execute(connect);// SAP---执行
						JCoTable exTReturn = functionPri.getTableParameterList().getTable("ET_RETURN");
						if (exTReturn.getNumRows() > 0) {
							exTReturn.firstRow();
							for (int i = 0; i < exTReturn.getNumRows(); i++, exTReturn.nextRow()) {
								Object type = exTReturn.getValue("TYPE");
								Object message = exTReturn.getValue("MESSAGE");
								Object logNo = exTReturn.getValue("LOG_NO");
								Object messageV1 = exTReturn.getValue("MESS" + "AGE_V1");
								if (type != null && "S".equals(type.toString())) {// 成功
									// System.out.println("成功");
									if (sapSuccess == null) {
										sapSuccess = true;
									}
//								System.out.println("logNo:" + logNo);
//								System.out.println(type + "-" + messageV1 + ":"
//										+ message);
									successMsgSet.add(message.toString());
								} else {// 失败信息
									sapSuccess = false;
//								System.out.println("失败");
//								System.out.println("logNo:" + logNo);
//								System.out.println(type + "-" + messageV1 + ":"
//										+ message);
									errorMsgSet.add(message.toString());
								}
							}
						}
					}
				}

			}
			// 流程缓存
			String taskId = delegateTask.getId();
			Set<IdentityLink> set = delegateTask.getCandidates();
			String groupId = null;
			for (IdentityLink identityLink : set) {
				groupId = identityLink.getGroupId();
			}
			// MemoryCacheUtil.endFlow(taskId,groupId,true);
			redisUtils.endTask(assignee, groupId, taskId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
