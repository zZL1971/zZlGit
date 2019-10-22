/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.apache.ibatis.type.TypeException;
import org.springframework.jdbc.core.JdbcTemplate;

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
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.sap.jco3.SAPConnect;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

/**
 * 订单审价监听器
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
public class TaskValuationCompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String taskId = null;
		String groupId = null;
		Object assignee = null;
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		SysJobPoolDao sysJobPoolDao = SpringContextHolder.getBean("sysJobPoolDao");
		CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		SaleHeaderDao saleHeaderDao = SpringContextHolder.getBean("saleHeaderDao");
		SaleItemPriceDao saleItemPriceDao = SpringContextHolder.getBean("saleItemPriceDao");
		SalePrModManager salePrModManager = SpringContextHolder.getBean("salePrModManager");
		String uuid = (String) delegateTask.getVariable("uuid");
		// nextflow判断是退回还是提交
		String nextflow = (String) delegateTask.getVariable("nextflow");
		JdbcTemplate jdbcTemplate = SpringContextHolder
				.getBean("jdbcTemplate");
		jdbcTemplate.update(
				"UPDATE SALE_HEADER SET CHECK_PRICE_USER=? where id=?",
				new Object[] { delegateTask.getAssignee(), uuid });

		// 提交到下一环节
		if (!nextflow.startsWith("flow_rt_")) {
			// 更新付款金额
			jdbcTemplate
					.update("update sale_header sh  set sh.fu_fuan_money =(decode(sh.fu_fuan_cond, '1', 1, '2', 0, '3', 0.5, '4', 0.3) *(select sum(sl.total_price) from sale_item sl where sl.pid = sh.id and nvl(sl.state_audit, 'C') <> 'QX'))  where sh.id = '"
							+ uuid + "'");
			jdbcTemplate
					.update("update sale_header sh set sh.order_total=(select sum(sl.total_price) from sale_item sl where sl.pid = sh.id and nvl(sl.state_audit, 'C') <> 'QX') where sh.id='"
							+ uuid + "'");
			// 更新状态
			jdbcTemplate
					.update(
							"UPDATE SALE_ITEM SET STATE_AUDIT='E' where pid=? AND IS_STANDARD='0' AND nvl(state_audit, 'C')<>'QX'",
							new Object[] { uuid });
		} else {
			// 退回
			SaleHeader saleHeader = saleHeaderDao.findOne(uuid);
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
		// 流程缓存
		taskId = delegateTask.getId();
		assignee = delegateTask.getAssignee();
		Set<IdentityLink> set = delegateTask.getCandidates();
		groupId = null;
		for (IdentityLink identityLink : set) {
			groupId = identityLink.getGroupId();
		}
		redisUtils.endTask(assignee, groupId, taskId);
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
