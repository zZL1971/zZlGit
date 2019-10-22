package com.main.manager;

import java.util.List;

import com.main.domain.cust.CustHeader;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sys.SysJobPool;
import com.mw.framework.bean.Message;
import com.mw.framework.manager.CommonManager;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public interface SysJobPoolManager{
	/**
	 * 单个检查客户信贷
	 * @param creditPool
	 * @return
	 */
	public Message checkCreditBYCust(SysJobPool creditPool);
	public Message checkCreditBYCust1(SysJobPool creditPool);
	
	/**
	 * 传送物料到SAP
	 * @param connect SAP连接池
	 * @param saleHeader订单
	 * @param vkorg销售组织
	 * @param vtweg分销渠道
	 * @param saleItemList 
	 * @return
	 * @throws JCoException
	 */

	public Message sendMM(JCoDestination connect,SaleHeader saleHeader,List<SaleItem> saleItemList,String vkorg,String vtweg) throws JCoException;
	
	/**
	 * 传送订单数据
	 * @param connect SAP 连接
	 * @param saleHeader订单
	 * @param saleItemList
	 * @param custHeader 客户
	 * @param vkorg 销售组织
	 * @param vtweg分销渠道
	 * @param spart 产品组 
	 * @param userId 用户
	 * @return
	 * @throws JCoException
	 */
	public Message sendSale(JCoDestination connect,SaleHeader saleHeader,List<SaleItem> saleItemList,CustHeader custHeader,String userId) throws JCoException;
	
	/**
	 * 根据订单号传送订单到SAP
	 * @param code 订单号
	 * @param userId 传送者
	 * @return
	 */
	public Message sendSaleByCode(String code,String userId);
}
