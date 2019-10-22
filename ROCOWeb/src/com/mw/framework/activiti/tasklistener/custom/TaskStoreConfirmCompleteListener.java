/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

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
import com.main.dao.SysJobPoolDao;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sys.SysJobPool;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 * 客户确认监听器（完成）
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
public class TaskStoreConfirmCompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		try {
			TaskService taskService = SpringContextHolder
					.getBean("taskService");
			CommonManager commonManager = SpringContextHolder
					.getBean("commonManager");
			JdbcTemplate jdbcTemplate = SpringContextHolder
					.getBean("jdbcTemplate");
			SysJobPoolDao sysJobPoolDao = SpringContextHolder.getBean("sysJobPoolDao");
			SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
			SaleHeaderDao saleHeaderDao = SpringContextHolder.getBean("saleHeaderDao");

			Object nextflow = taskService.getVariable(delegateTask.getId(),
					"nextflow");
			Object nextTask = taskService.getVariable(delegateTask.getId(),
					"nextTask");
			Object uuid = taskService.getVariable(delegateTask.getId(), "uuid");
			if (nextflow != null && !nextflow.toString().startsWith("flow_rt_")) {
				if ("usertask_finance".equals(nextTask)) {
					// 下一流程节点是财务确认是，放入订单池（传送到SAP系统）
					String saleHeader = "SELECT ORDER_CODE FROM SALE_HEADER WHERE ID=?";
					Map<String, Object> queryForMap = jdbcTemplate.queryForMap(
							saleHeader, uuid);
					if(queryForMap.size()>0) {
						String zuonr = ZStringUtils.resolverStr(queryForMap.get("ORDER_CODE"));
						List<SaleHeader> saleHeaderL = saleHeaderDao.findByCode(zuonr);
						SysJobPool jp = sysJobPoolDao.findByZuonr(zuonr);
						if(jp==null) {
							jp =new SysJobPool(
									queryForMap.get("ORDER_CODE") + "", "A", "B",
									"CREDIT_JOB","N");
						}
						jp.setIsFreeze("0");//0 启用 1 冻结
						jp.setProcInstId(delegateTask.getProcessInstanceId());
						jp.setNum(jp.getNum()+1);//处理次数加一
						jp.setNetwr(saleHeaderL.get(0).getOrderTotal());
						commonManager.save(jp);
					}
				}
				
				//将前端有sap单号的订单的价格信息与sap同步
				SaleHeader saleHeader = commonManager.getOne(uuid.toString(),
						SaleHeader.class);
				if(saleHeader == null) {
					return;
				}
				Boolean sapSuccess = null;// 同步SAP是否成功的标记
				Set<String> successMsgSet = new HashSet<String>();
				Set<String> errorMsgSet = new HashSet<String>();
				JCoDestination connect = SAPConnect.getConnect();
				JCoFunction functionPri = connect.getRepository().getFunction(
						"ZRFC_SD_CHANGE_CANCEL");// SAP创建接口
				JCoTable imMatPriceTable = functionPri.getTableParameterList()
						.getTable("IM_T_PRICE");// 同步修改价格的到SAP的表
				JCoTable imReTable = functionPri.getTableParameterList()
						.getTable("IT_ZSDT007");// 同步取消的到SAP IT_ZSDT007
				List<SaleItem> saleItemList = saleItemDao.findByItemsToSapCodeIsNotNullAndStateAudit(saleHeader.getId(), "E");//E:已审价
				for (SaleItem saleItem : saleItemList) {
					imReTable.appendRow();
					imReTable.setValue("VBELN", saleItem.getSapCode());// SAP编码
					imReTable.setValue("POSNR", saleItem.getSapCodePosex());// 行项目
					//如果行项目没有拒绝原因那么打上60
					imReTable.setValue("ABGRU", (saleItem.getAbgru()==null ||StringUtils.isEmpty(saleItem.getAbgru()))?("QX".equals(saleItem.getStateAudit())?"56":"60"):saleItem.getAbgru());// 拒绝原因
					List<Map<String, Object>> materialPriceLine = jdbcTemplate.queryForList("SELECT SUM(MP.TOTAL_PRICE) AS TOTALPRICE,MP.LINE ||'00' AS LINE FROM MATERIAL_PRICE MP WHERE MP.PID='"+saleItem.getId()+"' GROUP BY MP.LINE");
                    for (Map<String,Object> map : materialPriceLine) {
                    	String line= (map.get("LINE")!=null?String.valueOf(map.get("LINE")):"");
                    	if(!"00".equals(line)) {
                    		imMatPriceTable.appendRow();
                    		imMatPriceTable.setValue("VBELN", saleItem.getSapCode());
                    		imMatPriceTable.setValue("POSEX", saleItem.getSapCodePosex());
                    		imMatPriceTable.setValue("KSCHL", "PR07");
                    		double totalPrice=(map.get("TOTALPRICE")!=null?Double.parseDouble(String.valueOf(map.get("TOTALPRICE"))):0.0);
                    		imMatPriceTable.setValue("KBETR", totalPrice);
                    		imMatPriceTable.setValue("WAERS", "CNY");
                    		imMatPriceTable.setValue("KPEIN",saleItem.getAmount());
                    		imMatPriceTable.setValue("KMEIN",saleItem.getUnit());
                    		imMatPriceTable.setValue("ZZPAR", line);
                    	}
					}
				}
				//获取执行结果
				int row = imMatPriceTable.getNumRows();
				if (row > 0 || imReTable.getNumRows() > 0) {
					functionPri.execute(connect);// SAP---执行
					JCoTable exTReturn = functionPri.getTableParameterList()
							.getTable("ET_RETURN");
					if (exTReturn.getNumRows() > 0) {
						exTReturn.firstRow();
						for (int i = 0; i < exTReturn.getNumRows(); i++, exTReturn
								.nextRow()) {
							Object type = exTReturn.getValue("TYPE");
							Object message = exTReturn.getValue("MESSAGE");
							//Object logNo = exTReturn.getValue("LOG_NO");
							//Object messageV1 = exTReturn.getValue("MESSAGE_V1");
							if (type != null && "S".equals(type.toString())) {// 成功
								if (sapSuccess == null) {
									sapSuccess = true;
								}
								successMsgSet.add(message.toString());
							} else {// 失败信息
								sapSuccess = false;
								errorMsgSet.add(message.toString());
							}
						}
					}
				}
				/*String sapOrderCode = saleHeader.getSapOrderCode();
				if (sapOrderCode != null && !StringUtils.isEmpty(sapOrderCode)) {
					
					JCoDestination connect = SAPConnect.getConnect();
					JCoFunction functionPri = connect.getRepository().getFunction(
							"ZRFC_SD_CHANGE_CANCEL");// SAP创建接口
					JCoTable imModTable = functionPri.getTableParameterList()
							.getTable("IM_T_PRICE");// 同步修改价格的到SAP的表
					JCoTable imReTable = functionPri.getTableParameterList()
							.getTable("IT_ZSDT007");// 同步取消的到SAP IT_ZSDT007
					List<Map<String, Object>> saleItemIdList = jdbcTemplate
							.queryForList("select id from sale_item where pid='"
									+ uuid + "'");

					imReTable.appendRow();
					imReTable.setValue("VBELN", sapOrderCode);// SAP编码

					List<SaleItem> saleItemList = new ArrayList<SaleItem>();
					for (Map<String, Object> map : saleItemIdList) {
						SaleItem saleItem=(SaleItem) commonManager.getOne(
								map.get("ID").toString(), SaleItem.class);
						imReTable.appendRow();
						imReTable.setValue("VBELN", sapOrderCode);// SAP编码
						imReTable.setValue("POSNR", saleItem.getPosex());// 行项目
						//如果行项目没有拒绝原因那么打上60
						imReTable.setValue("ABGRU", (saleItem.getAbgru()==null ||StringUtils.isEmpty(saleItem.getAbgru()))?("QX".equals(saleItem.getStateAudit())?"56":"60"):saleItem.getAbgru());// 拒绝原因
						List<Map<String, Object>> saleItemPriceIdList = jdbcTemplate
								.queryForList("select id from sale_item_price where sale_itemid='"
										+ saleItem.getId() + "'");
						//物料编码----
						String MATNR = saleItem.getMatnr();
						
						//将行项目的价格信息全部放入到Immodtable 中
						for (Map<String, Object> submap : saleItemPriceIdList) {
							SaleItemPrice saleItemPrice=(SaleItemPrice) commonManager
									.getOne(submap.get("ID").toString(),
											SaleItemPrice.class);
							imModTable.appendRow();
							imModTable.setValue("VBELN", sapOrderCode);// SAP编码
							imModTable.setValue("POSNR", saleItem.getPosex());// 行项
							imModTable.setValue("MATNR", MATNR);// 物料编码
							imModTable.setValue("KSCHL", saleItemPrice.getType());// 价格类型pr01...
							// zr01...
							imModTable.setValue("KBETR",
									saleItemPrice.getSubtotal());// 价格类型小计（还没有乘以数量）
							imModTable.setValue("WAERS", "CNY");// 币别码
							imModTable.setValue("KPEIN", "1");// 条件定价单位
						}
					}
					
					
				}*/
			}
//	        String nextflow = (String) delegateTask.getVariable("nextflow");
//			if(!nextflow.startsWith("flow_rt_")){
//			
//			}
			// SalePrModManager
			// salePrModManager=SpringContextHolder.getBean("salePrModManager");
			// SalePrModHeaderModel spmHeaderModel=new SalePrModHeaderModel();
			// Map<String,Object>
			// map=jdbcTemplate.queryForMap("select * from sale_pr_mod_header where bstkd='"+orderCode+"'");
			//
			// if(spmHeaderModel!=null){
			// String pid=spmHeaderModel.getId();
			// List<SalePrModItemModel>
			// spmItemModelList=jdbcTemplate.queryForList("select t.* from sale_pr_mod_item t where t.pid='"+pid+"'",SalePrModItemModel.class);
			// Set<SalePrModItemModel> spmItemModelSet=new
			// HashSet<SalePrModItemModel>(spmItemModelList);
			// salePrModManager.tranToSap(spmHeaderModel, spmItemModelSet);
			// }
			// 流程缓存
			String taskId = delegateTask.getId();
			Set<IdentityLink> set = delegateTask.getCandidates();
			String groupId = null;
			for (IdentityLink identityLink : set) {
				groupId = identityLink.getGroupId();
			}
			RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
			redisUtils.endTask(delegateTask.getVariable("assignee"), groupId, taskId);
			//MemoryCacheUtil.endFlow(taskId, groupId,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
