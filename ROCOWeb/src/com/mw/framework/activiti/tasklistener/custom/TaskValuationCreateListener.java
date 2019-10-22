/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.main.dao.MaterialHeadDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemFjDao;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemFj;
import com.main.manager.SalePrModManager;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.FieldFunction;
import com.webservice.RocoImos;

/**
 * 订单审价（创建）监听器
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
public class TaskValuationCreateListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		RocoImos rocoImos = new RocoImos();
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		HistoryService historyService = SpringContextHolder
				.getBean("historyService");
		SysTrieTreeDao sysTrieTreeDao = SpringContextHolder
				.getBean("sysTrieTreeDao");
		SaleHeaderDao saleHeaderDao = SpringContextHolder
				.getBean("saleHeaderDao");
		MaterialHeadDao materialHeadDao = SpringContextHolder
				.getBean("materialHeadDao");
		SaleItemFjDao saleItemFjDao = SpringContextHolder
				.getBean("saleItemFjDao");
		RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		String id = (String) delegateTask.getVariable("uuid");
		SalePrModManager salePrModManager = SpringContextHolder
				.getBean("salePrModManager");

		List<HistoricTaskInstance> list = historyService
				.createHistoricTaskInstanceQuery().processInstanceId(
						delegateTask.getExecutionId()).taskDefinitionKey(
						delegateTask.getTaskDefinitionKey())
				.orderByTaskCreateTime().asc().list();
		if (list.size() > 0) {
			for (HistoricTaskInstance historicTaskInstance : list) {
				delegateTask.setAssignee(historicTaskInstance.getAssignee());
				break;
			}
		} else {
			new Exception("未发现节点历史操作用户!");
		}
		//审价阶段需要把订单状态改为待报价
		jdbcTemplate.update("update sale_header sh set order_status='0',FU_FUAN_COND='1' where sh.id=?",new Object[]{id});

		// 流程缓存
		String taskId = delegateTask.getId();
		Set<IdentityLink> set = delegateTask.getCandidates();
		
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}
		groupId="gp_valuation";
		Object assignee = delegateTask.getAssignee();
		Date time = new Date();
		redisUtils.startTask(assignee, groupId, taskId, null, time);
		
		SaleHeader saleHeader = saleHeaderDao.findOne(id);
		
		// 橱柜---imos数据分行
		String sql = "select max(to_number(posex)) as posex from sale_item where pid='"
				+ id + "'";
		String lastPosex = jdbcTemplate.queryForObject(sql, String.class);
		List<SaleItem> saleItems = saleItemDao.findItemsByPid(saleHeader.getId());
		List<String> delIds = new ArrayList<String>();
		for (SaleItem saleItem : saleItems) {
			MaterialHead materialHeader = materialHeadDao.findOne(saleItem.getMaterialHeadId());
			if(materialHeader != null) {
				if("1".equals(materialHeader.getSaleFor())) {
					SysTrieTree treeId = sysTrieTreeDao.findByKeyVal("BRANCH_TYPE");
					List<SysTrieTree> cgList = sysTrieTreeDao.findByParentId(treeId.getId());
					for (SysTrieTree cg : cgList) {
						if("CG01".equals(cg.getKeyVal())) {
							String imosCGSql = "select * from imos_idbext ii where ii.orderid=? and info1 = ?";
							List<Map<String, Object>> imosList = jdbcTemplate.queryForList(imosCGSql, new Object[] {saleItem.getOrderCodePosex(),cg.getKeyVal()});
							for (Map<String, Object> imos : imosList) {
								String info1 = (String) imos
										.get("INFO1");
								String matnr = "";
								if (!"CG03".equals(info1)) {
									matnr = (String) imos.get(
											"ARTICLE_ID");
								}
								delIds.add((String)imos.get("ID"));
								int amount = 1;
								List<MaterialHead> materialHeadList = null;
								MaterialHead materialHead = null;
								materialHeadList = materialHeadDao
										.findStandardByMatnr(matnr);
								
								if (materialHeadList != null
										&& materialHeadList.size() > 0) {
									lastPosex = ""
											+ (Integer.parseInt(lastPosex) + 10);
									String posex = String.format("%04d",
											Integer.parseInt(lastPosex));
									String targetOrderCodePosex = saleHeader
											.getOrderCode()
											+ posex;
									
									materialHead = materialHeadList.get(0);
									SaleItem newSaleItem = new SaleItem();
									FieldFunction.copyValue(newSaleItem,
											saleItem);
									newSaleItem.setId(UUID.randomUUID()
											.toString().replace("-", ""));
									newSaleItem.setSaleHeader(saleItem
											.getSaleHeader());
									newSaleItem
									.setOrderCodePosex(targetOrderCodePosex);
									newSaleItem.setPosex(lastPosex);
									newSaleItem.setAmount(amount);
									newSaleItem.setIsStandard("1");
									newSaleItem.setTouYingArea("0");
									newSaleItem
									.setMaterialHeadId(materialHead
											.getId());
									newSaleItem.setTotalPrice(materialHead
											.getKbetr()
											* amount);
									newSaleItem.setRemark(StringUtils
											.isEmpty(saleItem.getPosex()) ? ""
													: saleItem.getRemark() + "("
													+ saleItem.getPosex() + "行部件)");
									newSaleItem.setUnit(materialHead
											.getKmein());
									newSaleItem.setMatnr(matnr);
									newSaleItem.setMaktx(materialHead
											.getMaktx());
									newSaleItem.setParentId(saleItem
											.getId());
									saleItemDao.save(newSaleItem);
								}
							}
						}
					}
					if(delIds.size()>0)rocoImos.del(delIds,saleItem.getOrderCodePosex());
				}else {
					sql = "select * from imos_to_standard";
					List<Map<String, Object>> imos2StList = jdbcTemplate
							.queryForList(sql);
					List<String> matnrList = new ArrayList<String>();
					Map<String, String> mappingMap = new HashMap<String, String>();
					List<String> delIdsstandard = new ArrayList<String>();
					Map<String,Integer> cntMap=new HashMap<String, Integer>(); 

					if (imos2StList != null && imos2StList.size() > 0) {
						StringBuilder sb = new StringBuilder();
						for (Map<String, Object> map1 : imos2StList) {
							mappingMap.put((String) map1.get("ARTICLE_ID"),
									(String) map1.get("MATNR"));
							sb.append(",'" + map1.get("ARTICLE_ID") + "'");
						}
						List<SaleItem> saleItemList = saleItemDao.findItemsByPid(id);
						for (SaleItem saleitem : saleItemList) {
							delIdsstandard.clear();
							matnrList.clear();
							sql = "select * from imos_idbext ii where ii.orderid =? and ii.typ='8' and nvl(name2,'1') <>'过滤名称' and (ii.article_id in ("
									+ sb.substring(1)
									+ ") or instr(ii.article_id,'13')=1)";
							List<Map<String, Object>> dataList = jdbcTemplate
									.queryForList(sql, new Object[] { saleitem
											.getOrderCodePosex() });
							if (dataList != null && dataList.size() > 0) {
								// 存在多个成品 那么需要拆分多个行号
								for (int index = 0; index < dataList.size(); index++) {
									// 把需要删除的imos数据的ID放入
									delIdsstandard.add((String) dataList.get(index).get("ID"));
									int cnt=Integer.parseInt(dataList.get(index).get("CNT").toString());
									String articleId = (String) dataList.get(index)
											.get("ARTICLE_ID");
									if (!articleId.startsWith("13")) {
										matnrList.add(mappingMap.get((String) dataList
												.get(index).get("ARTICLE_ID")));
										cntMap.put(mappingMap.get((String) dataList
												.get(index).get("ARTICLE_ID")), cnt);
									} else {
										matnrList.add(articleId);
										cntMap.put(articleId, cnt);
									}
								}
							}
							// 数据处理
							if (delIdsstandard.size() > 0) {
								// 删除IMOS信息
								StringBuilder _sb = new StringBuilder();
								for (String ids : delIdsstandard) {
									_sb.append(",'" + ids + "'");
								}
								jdbcTemplate.update(
										"update imos_idbext ii set name2='过滤名称' where ii.orderid=? and ii.id in ("
												+ _sb.substring(1) + ")",
										new Object[] { saleitem.getOrderCodePosex() });
								List<MaterialHead> materialHeadList = null;
								MaterialHead materialHead = null;
								for (String matnr : matnrList) {
									//数量为imos数量*行项目数量
									int amount = cntMap.get(matnr)*saleitem.getAmount();
									materialHeadList = materialHeadDao
											.findStandardByMatnr(matnr);
									if (materialHeadList != null
											&& materialHeadList.size() > 0) {
										lastPosex = ""
												+ (Integer.parseInt(lastPosex) + 10);
										String posex = String.format("%04d", Integer
												.parseInt(lastPosex));
										String targetOrderCodePosex = saleHeader
												.getOrderCode()
												+ posex;

										materialHead = materialHeadList.get(0);
										SaleItem newSaleItem = new SaleItem();
										SaleItemFj newSaleItemFj = new SaleItemFj();
										newSaleItem.setSaleHeader(saleitem
												.getSaleHeader());
										newSaleItem
												.setOrderCodePosex(targetOrderCodePosex);
										newSaleItem.setPosex(lastPosex);
										newSaleItem.setId(UUID.randomUUID().toString()
												.replaceAll("-", ""));
										newSaleItem.setAmount(amount);
										newSaleItem.setIsStandard("1");
										newSaleItem.setTouYingArea("0");
										newSaleItem.setRemark(StringUtils
												.isEmpty(saleitem.getPosex()) ? ""
														: saleitem.getRemark() + "("
																+ saleitem.getPosex() + "行部件)");
										newSaleItem.setMaterialHeadId(materialHead
												.getId());
										newSaleItem.setTotalPrice(materialHead
												.getKbetr()
												* amount);
										newSaleItem.setUnit(materialHead.getKmein());
										newSaleItem.setMatnr(matnr);
										newSaleItem.setMaktx(materialHead.getMaktx());
										newSaleItem.setParentId(saleitem.getId());
										newSaleItem.setMyGoodsId(UUID.randomUUID()
												.toString().replaceAll("-", ""));
										newSaleItem = saleItemDao.save(newSaleItem);
										newSaleItemFj
												.setSaleItemId(newSaleItem.getId());
										newSaleItemFj.setMyGoodsId(newSaleItem
												.getMyGoodsId());
										newSaleItemFj.setZzazdr(StringUtils
												.isEmpty(saleitem.getPosex()) ? ""
												: saleitem.getRemark() + "("
														+ saleitem.getPosex() + "行部件)");
										saleItemFjDao.save(newSaleItemFj);
									}
								}
							}
						}
					}
				}
			}
		}
		/**
		 * 初始化销售价格信息
		 * 
		 * @param saleId
		 *            订单主键
		 * @return
		 */
		Message msg = salePrModManager.addItemPrice(id);
		if("PR-ITEMPRRICE-500".equals(msg.getErrorCode())) {
			throw new RuntimeException(msg.getErrorMsg());
		}

	}

}
