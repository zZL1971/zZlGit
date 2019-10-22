/**
 *
 */
package com.mw.framework.activiti.tasklistener.csord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.main.dao.MaterialFileDao;
import com.main.dao.MaterialHeadDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemFjDao;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sys.SysJobPool;
import com.main.manager.SalePrModManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.FieldFunction;
import com.webservice.RocoImos;

/**
 * 价格审核节点监听器(创建)
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
public class ValuationCreateListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		RocoImos rocoImos = new RocoImos();
		Map<String, Object> map = delegateTask.getVariables();
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		SysDataDictDao sysDataDictDao = SpringContextHolder
				.getBean("sysDataDictDao");
		SysTrieTreeDao sysTrieTreeDao = SpringContextHolder
				.getBean("sysTrieTreeDao");
		SaleHeaderDao saleHeaderDao = SpringContextHolder
				.getBean("saleHeaderDao");
		MaterialHeadDao materialHeadDao = SpringContextHolder
				.getBean("materialHeadDao");
		MaterialFileDao materialFileDao = SpringContextHolder
				.getBean("materialFileDao");
		RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
		SerialNumberManager serialNumberManager = SpringContextHolder
				.getBean("serialNumberManager");
		TaskService taskService = SpringContextHolder.getBean("taskService");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		SaleItemFjDao saleItemFjDao = SpringContextHolder
				.getBean("saleItemFjDao");
		// for (Object obj : map.keySet()) {
		// System.out.println(map.get(obj));
		// }
		String id = (String) delegateTask.getVariable("uuid");
		SalePrModManager salePrModManager = SpringContextHolder
				.getBean("salePrModManager");

		//审价阶段需要把订单状态改为待报价
		jdbcTemplate.update("update sale_header sh set order_status='0' where sh.id=?",new Object[]{id});

		// 流程缓存
		String taskId = delegateTask.getId();// jdbcTempltate.queryForObject("select id_ from act_ru_task where proc_inst_id_='"+delegateTask.getProcessInstanceId()+"'",String.class);
		Set<IdentityLink> set = delegateTask.getCandidates();
		
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}
		Object nextflow =  taskService.getVariable(delegateTask.getId(), "nextflow");
//		if(nextflow!=null && !nextflow.toString().startsWith("flow_rt_")){
//			groupId="gp_valuation";
//		}
		groupId="gp_valuation";
		Object assignee = delegateTask.getAssignee();
		Date time = new Date();
		//MemoryCacheUtil.startFlow((String) assignee, taskId, groupId, time);
		redisUtils.startTask(assignee, groupId, taskId, null, time);
		
		SaleHeader saleHeader = saleHeaderDao.findOne(id);
		
		// 橱柜---imos数据分行
		String sql = "select max(to_number(posex)) as posex from sale_item where pid='"
				+ id + "'";
		String lastPosex = jdbcTemplate.queryForObject(sql, String.class);
		/*if ("1".equals(saleHeader.getSaleFor())) {// saleFor=1表示为橱柜,橱柜的imos数据需要拆分
			List<SaleItem> saleItemList = saleItemDao.findItemsByPid(id);
			// 第一步先拿配置信息,如果没有配置则不进行拆分
			// 先拿分类配置,再拿详细配置

			sql = "select id from sys_trie_tree where  key_val=?";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,
					new Object[] { "BRANCH_TYPE" });
			StringBuilder sb = new StringBuilder();

			Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();
			Map<String, String> nameMap = new HashMap<String, String>();
			Map<String, String> nMap = new HashMap<String, String>();
			Map<String, Map<String, String>> sizeMap = new HashMap<String, Map<String, String>>();
			List<String> delIds = new ArrayList<String>();
			int cg03index = 0;
			if (list != null && list.size() > 0) {

				List<SysTrieTree> trieTreeList = sysTrieTreeDao
						.findByParentId(list.get(0).get("ID").toString());
				for (int index = 0; index < saleItemList.size(); index++) {
					SaleItem saleItem = saleItemList.get(index);

					for (int innerIndex = 0; innerIndex < trieTreeList.size(); innerIndex++) {
						// resultList.clear();
						delIds.clear();

						sb.delete(0, sb.length());
						List<SysDataDict> dataDicts = sysDataDictDao
								.findByTrieTreeKeyValAndStatForSSM(trieTreeList
										.get(innerIndex).getKeyVal());
						for (SysDataDict sysDataDict : dataDicts) {
							sb.append(",'" + sysDataDict.getKeyVal() + "'");
						}
						String orderCodePosex = saleItem.getOrderCodePosex();
						Map<String, Integer> matnrMap = new HashMap<String, Integer>();
						sql = "select * from imos_idbext ii where ii.orderid=? and info1 in ("
								+ sb.substring(1) + ")";
						List<Map<String, Object>> dataList = jdbcTemplate
								.queryForList(sql, new Object[] { saleItem
										.getOrderCodePosex() });
						for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
							String matname = (String) dataList.get(dataIndex)
									.get("MATNAME");
							String name = (String) dataList.get(dataIndex).get(
									"NAME");
							String width = dataList.get(dataIndex).get("WIDTH") == null ? ""
									: "" + dataList.get(dataIndex).get("WIDTH");
							String length = dataList.get(dataIndex).get(
									"LENGTH") == null ? "" : ""
									+ dataList.get(dataIndex).get("LENGTH");
							String deapth = dataList.get(dataIndex).get(
									"THICKNESS") == null ? "" : ""
									+ dataList.get(dataIndex).get("THICKNESS");
							delIds.add((String) dataList.get(dataIndex).get(
									"ID"));
							List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
							resultList.add(dataList.get(dataIndex));
							// 一种物料以行项目+数量的方式展现
							String info1 = (String) dataList.get(dataIndex)
									.get("INFO1");
							String matnr = "";
							if (!"CG03".equals(info1)) {
								matnr = (String) dataList.get(dataIndex).get(
										"ARTICLE_ID");
							}
							// if("CG03".equals((String)dataList.get(dataIndex).get("INFO1"))){
							// articleId=(String)
							// dataList.get(dataIndex).get("RENDER");
							// }

							rocoImos.getBomUp(dataList.get(dataIndex).get(
									"PARENTID"), orderCodePosex, resultList,
									delIds);
							rocoImos.getBomDown(dataList.get(dataIndex).get(
									"ID"), orderCodePosex, resultList, delIds);
							if (!StringUtils.isEmpty(matnr)) {
								if (!matnrMap.containsKey(matnr)) {
									matnrMap.put(matnr, 1);
									resultMap.put(matnr, resultList);
								} else {
									int amount = matnrMap.get(matnr);
									matnrMap.put(matnr, amount + 1);
									resultList.addAll(resultMap.get(matnr));
									resultMap.put(matnr, resultList);
								}
							} else {
								// 表明为CG03
								String name2 = "EC" + cg03index++;
								nameMap.put(name2, matname);
								nMap.put(name2, name);
								matnrMap.put(name2, 1);
								resultMap.put(name2, resultList);
								Map<String, String> size = new HashMap<String, String>();
								size.put("width", width);
								size.put("length", length);
								size.put("thickness", deapth);
								sizeMap.put(name2, size);
							}
						}
						if (delIds.size() > 0) {
							boolean flag=false;
							// 补入数据
							List<MaterialHead> materialHeadList = null;
							MaterialHead materialHead = null;
							for (String matnr : matnrMap.keySet()) {
								int amount = matnrMap.get(matnr);

								if (!matnr.startsWith("EC")) {
									materialHeadList = materialHeadDao
											.findStandardByMatnr(matnr);

									if (materialHeadList != null
											&& materialHeadList.size() > 0) {
										flag=true;
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

										dataList = resultMap.get(matnr);
										for (int _index = 0; _index < dataList
												.size(); _index++) {
											dataList.get(_index).put("ORDERID",
													targetOrderCodePosex);
										}
										//rocoImos.addList(dataList);
									}
								} else {
									flag=true;
									List<Map<String, Object>> pdfResultList = jdbcTemplate
											.queryForList(
													"select * from material_file mf where mf.file_type='PDF' and mf.pid=? and mf.upload_file_name_old like '石台%' and status is null and rownum=1",
													new Object[] { saleItem
															.getMaterialHeadId() });
									Map<String, Object> pdfResult = (pdfResultList == null || pdfResultList
											.size() == 0) ? null
											: pdfResultList.get(0);
									MaterialHead mh = new MaterialHead();
									MaterialFile mf = new MaterialFile();
									FieldFunction.copyValue(mh, materialHeadDao
											.findOne(saleItem
													.getMaterialHeadId()));
									mh.setId(UUID.randomUUID().toString()
											.replace("-", ""));
									// mh.setId(UUID.randomUUID()
									// .toString()
									// .replaceAll("-", ""));
									String imosName = nameMap.get(matnr);
									String keyVal = imosName.substring(imosName
											.indexOf("RC00") + 5);
									List<Map<String, Object>> _list = jdbcTemplate
											.queryForList("select sdd.key_val from sys_data_dict sdd ,sys_trie_tree stt where sdd.trie_id=stt.id and stt.key_val='60' and sdd.desc_zh_cn='"
													+ keyVal + "'");
									if (_list != null && _list.size() > 0) {
										mh.setColor((String) _list.get(0).get(
												"KEY_VAL"));
									}
									String curSerialNumber = serialNumberManager
											.curSerialNumberFullYY("MM", 8);
									mh.setTextureOfMaterial("60");
									mh.setMatkl("1403");
									mh.setSaleFor("2");
									mh.setWidthDesc(sizeMap.get(matnr).get(
											"width"));
									mh.setHeightDesc(sizeMap.get(matnr).get(
											"thickness"));
									mh.setLongDesc(sizeMap.get(matnr).get(
											"length"));
									mh.setSerialNumber(curSerialNumber);
									mh.setZztyar(0.0);
									mh.setZzymfs(0.0);
									mh.setZzymss(0);
									mh.setZzzkar(0.0);
									mh.setZzxsfs(0.0);
									mh.setMtart("Z008");
									mh.setGroes("L" + mh.getLongDesc() + "XW"
											+ mh.getWidthDesc() + "XD"
											+ mh.getHeightDesc());

									mh
											.setMaktx("石英石"
													+ nMap.get(matnr)
													+ keyVal
													+ "L"
													+ (int) Double
															.parseDouble(StringUtils
																	.isEmpty(mh
																			.getLongDesc()) ? "0"
																	: mh
																			.getLongDesc())
													+ "XW"
													+ (int) Double
															.parseDouble(StringUtils
																	.isEmpty(mh
																			.getWidthDesc()) ? "0"
																	: mh
																			.getWidthDesc())
													+ "XH"
													+ (int) Double
															.parseDouble(StringUtils
																	.isEmpty(mh
																			.getHeightDesc()) ? "0"
																	: mh
																			.getHeightDesc())
													+ "MM");
									mh = materialHeadDao.save(mh);

									if (pdfResult != null
											&& pdfResult.size() > 0) {
										// 文件
										mf.setUploadFileName((String) pdfResult
												.get("UPLOAD_FILE_NAME"));
										mf
												.setUploadFileNameOld((String) pdfResult
														.get("UPLOAD_FILE_NAME_OLD"));
										mf.setUploadFilePath((String) pdfResult
												.get("UPLOAD_FILE_PATH"));
										mf.setStatus(null);
										mf.setFileType((String) pdfResult
												.get("FILE_TYPE"));
										mf.setMaterialHead(mh);
										materialFileDao.save(mf);
									}

									// shitai
									lastPosex = ""
											+ (Integer.parseInt(lastPosex) + 10);
									String posex = String.format("%04d",
											Integer.parseInt(lastPosex));
									String targetOrderCodePosex = saleHeader
											.getOrderCode()
											+ posex;
									SaleItem newSaleItem = new SaleItem();
									FieldFunction.copyValue(newSaleItem,
											saleItem);
									newSaleItem.setId(UUID.randomUUID()
											.toString().replace("-", ""));
									newSaleItem
											.setSaleItemPrices(new HashSet<SaleItemPrice>());
									newSaleItem.setMatnr("");
									newSaleItem.setSaleHeader(saleItem
											.getSaleHeader());
									newSaleItem
											.setOrderCodePosex(targetOrderCodePosex);
									newSaleItem.setPosex(lastPosex);
									// newSaleItem
									// .setId(UUID.randomUUID()
									// .toString()
									// .replaceAll("-", ""));
									newSaleItem.setAmount(amount);
									newSaleItem.setIsStandard("0");
									newSaleItem.setTouYingArea("0");
									newSaleItem.setMaterialHeadId(mh.getId());
									newSaleItem.setTotalPrice(0.0);
									newSaleItem.setMaktx(mh.getMaktx());
									newSaleItem.setRemark(StringUtils
											.isEmpty(saleItem.getPosex()) ? ""
													: saleItem.getRemark() + "("
															+ saleItem.getPosex() + "行部件)");

									newSaleItem.setParentId(saleItem.getId());
									saleItemDao.save(newSaleItem);

									dataList = resultMap.get(matnr);
									for (int _index = 0; _index < dataList
											.size(); _index++) {
										dataList.get(_index).put("ORDERID",
												targetOrderCodePosex);
									}
									//rocoImos.addList(dataList);
								}
								
							}
							// 删除IMOS信息
							rocoImos.del(delIds,saleItem.getOrderCodePosex());
						}
					}

				}

			}
		} else {
			sql = "select * from imos_to_standard";
			List<Map<String, Object>> imos2StList = jdbcTemplate
					.queryForList(sql);
			List<String> matnrList = new ArrayList<String>();
			Map<String, String> mappingMap = new HashMap<String, String>();
			List<String> delIds = new ArrayList<String>();
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
					delIds.clear();
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
							delIds.add((String) dataList.get(index).get("ID"));
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
					if (delIds.size() > 0) {
						// 删除IMOS信息
						StringBuilder _sb = new StringBuilder();
						for (String ids : delIds) {
							_sb.append(",'" + ids + "'");
						}
						jdbcTemplate.update(
								"update imos_idbext ii set name2='过滤名称' where ii.orderid=? and ii.id in ("
										+ _sb.substring(1) + ")",
								new Object[] { saleitem.getOrderCodePosex() });
						// rocoImos.del(delIds, saleitem.getOrderCodePosex());
						// 补入数据
						// for (int index2 = 0; index2 <
						// resultList.size();
						// index2++) {
						// resultList.get(index2).put("ORDERID",
						// targetOrderCodePosex);
						// }
						// rocoImos.addList(resultList);
						List<com.main.domain.mm.MaterialHead> materialHeadList = null;
						com.main.domain.mm.MaterialHead materialHead = null;
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
		}*/
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
		salePrModManager.addItemPrice(id);

	}
}