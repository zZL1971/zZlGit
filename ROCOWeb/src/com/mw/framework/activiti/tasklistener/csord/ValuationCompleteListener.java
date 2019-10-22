/**
 *
 */
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
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemPriceDao;
import com.main.dao.SysJobPoolDao;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemPrice;
import com.main.domain.sys.SysJobPool;
import com.main.manager.SalePrModManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * 价格审核节点监听器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName 
 *           com.mw.framework.activiti.tasklistener.csord.ValuationCompleteListener
 *           .java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-24
 * 
 */
public class ValuationCompleteListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		// Map<String, Object> variables = delegatetask.getVariables();
		// Set<Entry<String, Object>> entrySet = variables.entrySet();
		/**
		 * nextflow-->flow2 uuid-->GX38rFx39LDbjPapvwRpa7
		 */
		try {
		String uuid = (String) delegatetask.getVariable("uuid");
		SysJobPoolDao sysJobPoolDao = SpringContextHolder.getBean("sysJobPoolDao");
		
		// for (Entry<String, Object> entry : entrySet) {
		// // System.out.println(entry.getKey()+"-->"+entry.getValue());
		// }
		SaleHeaderDao saleHeaderDao = SpringContextHolder
				.getBean("saleHeaderDao");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		SaleItemPriceDao saleItemPriceDao = SpringContextHolder.getBean("saleItemPriceDao");
		SalePrModManager salePrModManager = SpringContextHolder.getBean("salePrModManager");
		SaleHeader saleHeader = saleHeaderDao.findOne(uuid);
		String orderType = "";
/*		if (saleHeader != null) {
			if (ZStringUtils.isNotEmpty(saleHeader.getOrderType())) {
				orderType = saleHeader.getOrderType();
			}
			saleHeader.setCheckPriceUser(delegatetask.getAssignee());
			saleHeaderDao.save(saleHeader);
		}*/
/*		if (ZStringUtils.isEmpty(orderType)) {
			new Exception("请给流程的节点订单类型赋值");
		} else {
			delegatetask.setVariable("orderType", orderType);
		}
*/
		//添加到订单池
		TaskService taskService = SpringContextHolder.getBean("taskService");
		CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		jdbcTemplate.update("update sale_header sh set sh.order_total = (SELECT SUM(S.TOTAL_PRICE) FROM SALE_ITEM S WHERE S.PID = SH.ID AND nvl(s.state_audit, 'xx') <> 'QX') where sh.order_code ='"+saleHeader.getOrderCode()+"'");
		Object nextflow =  taskService.getVariable(delegatetask.getId(), "nextflow");
		Object nextTask =  taskService.getVariable(delegatetask.getId(), "nextTask");
		Object assignee = delegatetask.getAssignee();
		if(nextflow!=null && !nextflow.toString().startsWith("flow_rt_")){
			if ("usertask_finance".equals(nextTask)) {
				// 下一流程节点是财务确认是，放入订单池（传送到SAP系统）
				String saleHeadersql = "SELECT ORDER_CODE FROM SALE_HEADER WHERE ID=?";
				Map<String, Object> queryForMap = jdbcTemplate.queryForMap(saleHeadersql, uuid);
				if (queryForMap.size() > 0) {
					String zuonr = ZStringUtils.resolverStr(queryForMap.get("ORDER_CODE"));
					SysJobPool jp = sysJobPoolDao.findByZuonr(zuonr);
					if (jp == null) {
						jp = new SysJobPool(queryForMap.get("ORDER_CODE") + "", "A", "B", "CREDIT_JOB", "N");
					}
					jp.setIsFreeze("0");// 0 启用 1 冻结
					jp.setProcInstId(delegatetask.getProcessInstanceId());
					jp.setNum(jp.getNum() + 1);// 处理次数加一
					commonManager.save(jp);
				}
			}

			// 将前端有sap单号的订单的价格信息与sap同步
//			SaleHeader saleHeader = commonManager.getOne(uuid.toString(), SaleHeader.class);
			String sapOrderCode = saleHeader.getSapOrderCode();
			Set<SaleItem> saleitem = saleHeader.getSaleItemSet();
			for (SaleItem saleItem2 : saleitem) {
				if(!"QX".equals(saleItem2.getStateAudit())&&"0".equals(saleItem2.getIsStandard())){
					saleItem2.setStateAudit("E");
				}
				saleItemDao.save(saleItem2);
			}
			saleHeader.setCheckPriceUser(delegatetask.getAssignee());
			String orderToalSql = "select sum(sl.total_price) as totalprice from sale_item sl where sl.pid = '"+saleHeader.getId()+"' and nvl(sl.state_audit, 'C') <> 'QX'";
			List<Map<String, Object>> orderToalList = jdbcTemplate.queryForList(orderToalSql);
			if(orderToalList.size()>0) {
				double totalprice = Double.parseDouble(String.valueOf(orderToalList.get(0).get("TOTALPRICE")));
				saleHeader.setOrderTotal(totalprice);
				saleHeader.setFuFuanMoney(totalprice);
			}
			saleHeaderDao.save(saleHeader);
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
//							System.out.println("logNo:" + logNo);
//							System.out.println(type + "-" + messageV1 + ":"
//									+ message);
								successMsgSet.add(message.toString());
							} else {// 失败信息
								sapSuccess = false;
//							System.out.println("失败");
//							System.out.println("logNo:" + logNo);
//							System.out.println(type + "-" + messageV1 + ":"
//									+ message);
								errorMsgSet.add(message.toString());
							}
						}
					}
				}
			}
			// 更新付款金额
			jdbcTemplate
					.update("update sale_header sh  set sh.fu_fuan_money =(decode(sh.fu_fuan_cond, '1', 1, '2', 0, '3', 0.5, '4', 0.3) *(select sum(sl.total_price) from sale_item sl where sl.pid = sh.id and nvl(sl.state_audit, 'C') <> 'QX'))  where sh.id = '"
							+ uuid + "'");
			/*jdbcTemplate
					.update("update sale_header sh set sh.order_total=(select sum(sl.total_price) from sale_item sl where sl.pid = sh.id and nvl(sl.state_audit, 'C') <> 'QX') where sh.id='"
							+ uuid + "'");*/
			String sql = "update sale_item si set si.state_audit='E' where si.pid='"+uuid+"' and si.is_standard='0' AND nvl(si.state_audit, 'C')<>'QX'";
			// 更新状态
//			 int a  =jdbcTemplate.update(sql);
		}else {
			// 退回
			if(saleHeader!=null) {
				List<SaleItem> saleItemList = saleItemDao.findByItemsToSapCodeIsNotNullAndStateAudit(saleHeader.getOrderCode(), "C");
				for (SaleItem saleItem : saleItemList) {
					JCoDestination connect = SAPConnect.getConnect();
					try {
						JCoFunction function = connect.getRepository()
								.getFunction("ZRFC_SD_CHANGE_SO_PO");
						JCoParameterList importParameterList = function
								.getImportParameterList();
						importParameterList.setValue("I_VBELN", saleItem.getSapCode());
						function.execute(connect);
						String string = function.getExportParameterList()
								.getString("E_TYPE");
						if ("S".equals(string)) {
							saleItem.setSapCode("");
							saleItem.setSapCodePosex("");
							commonManager.save(saleItem);
						}else {
							throw new TypeCastException("删除PO号失败");
						}
					} catch (JCoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				SysJobPool sysJobPool = sysJobPoolDao.findByZuonr(saleHeader.getOrderCode());
				if(sysJobPool!=null) {
					commonManager.delete(sysJobPool);
				}
				/*List<SaleItem> saleItemDSet = saleItemDao.findByItemsAndStateAudit(saleHeader.getOrderCode(), "D");
				for (SaleItem saleItem : saleItemDSet) {
					if("0".equals(saleItem.getIsStandard())) {//标准配套产品不需要重新生成 定价条件
						List<SaleItemPrice> saleItemPriceList = saleItemPriceDao.querySaleItemPrice(saleItem.getId());
						salePrModManager.reCalculate(saleItemPriceList, saleHeader.getShouDaFang(), saleHeader.getOrderDate(),"FLOW");//已审价的需要 将折扣返点 重算
						jdbcTemplate.execute("DELETE SALE_ITEM_PRICE SP WHERE SP.SALE_ITEMID='"+saleItem.getId()+"'");
						jdbcTemplate.update("UPDATE SALE_ITEM SI SET SI.TOTAL_PRICE=0.0 WHERE SI.ID=?",new Object[] {saleItem.getId()});
					}
				}
				List<SaleItem> saleItemCSet = saleItemDao.findByItemsAndStateAudit(saleHeader.getOrderCode(), "C");
				for (SaleItem saleItem : saleItemCSet) {
					if("0".equals(saleItem.getIsStandard())) {//标准配套产品不需要重新生成 定价条件
						List<SaleItemPrice> saleItemPriceList = saleItemPriceDao.querySaleItemPrice(saleItem.getId());
						salePrModManager.reCalculate(saleItemPriceList, saleHeader.getShouDaFang(), saleHeader.getOrderDate(),"FLOW");//已审价的需要 将折扣返点 重算
						jdbcTemplate.execute("DELETE SALE_ITEM_PRICE SP WHERE SP.SALE_ITEMID='"+saleItem.getId()+"'");
						jdbcTemplate.update("UPDATE SALE_ITEM SI SET SI.TOTAL_PRICE=0.0 WHERE SI.ID=?",new Object[] {saleItem.getId()});
					}
				}*/
				updateMaterialPrice(saleItemDao,saleHeader,saleItemPriceDao,salePrModManager,jdbcTemplate,"D");
				updateMaterialPrice(saleItemDao,saleHeader,saleItemPriceDao,salePrModManager,jdbcTemplate,"C");
			}
		
		}
        //流程缓存
		String taskId=delegatetask.getId();
		Set<IdentityLink> set=delegatetask.getCandidates();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId=identityLink.getGroupId();
		}
		redisUtils.endTask(assignee, groupId, taskId);
//		MemoryCacheUtil.endFlow(taskId,groupId,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateMaterialPrice(SaleItemDao saleItemDao,SaleHeader saleHeader,SaleItemPriceDao saleItemPriceDao,SalePrModManager salePrModManager,JdbcTemplate jdbcTemplate,String status) {
		// TODO Auto-generated method stub
		List<SaleItem> saleItemCSet = saleItemDao.findByItemsAndStateAudit(saleHeader.getOrderCode(), status);
		for (SaleItem saleItem : saleItemCSet) {
			if("0".equals(saleItem.getIsStandard())) {//标准配套产品不需要重新生成 定价条件
				List<SaleItemPrice> saleItemPriceList = saleItemPriceDao.querySaleItemPrice(saleItem.getId());
				salePrModManager.reCalculate(saleItemPriceList, saleHeader.getShouDaFang(), saleHeader.getOrderDate(),"FLOW");//已审价的需要 将折扣返点 重算
				jdbcTemplate.execute("DELETE SALE_ITEM_PRICE SP WHERE SP.SALE_ITEMID='"+saleItem.getId()+"'");
				jdbcTemplate.update("UPDATE SALE_ITEM SI SET SI.TOTAL_PRICE=0.0 WHERE SI.ID=?",new Object[] {saleItem.getId()});
			}
		}
	}

}