package com.main.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.main.domain.sale.SaleItemPrice;
import com.main.model.spm.SalePrModHeaderModel;
import com.main.model.spm.SalePrModItemModel;
import com.mw.framework.bean.Message;
import com.mw.framework.manager.CommonManager;

public interface SalePrModManager extends CommonManager{
	/**
	 * 根据订单编号或者SAP编号查询销售订单，（价格列转行）
	 * @param bstkd 订单编号
	 * @param vbeln SAP编号
	 * @return
	 */
	public Map<String,Object>searchSaleOrder(String bstkd,String vbeln);
	/** 
	 * 修改价格
	 * @param spmHeaderModel 订单抬头
	 * @param spmItemSetModel 修改过的行项目
	 * @param toSap 是否需要传到SAP
	 * @return
	 */
	public Message saveSalePrMod(SalePrModHeaderModel spmHeaderModel,Set<SalePrModItemModel> spmItemSetModel,boolean toSap);
	/**
	 * 计算出定价条件的小计总计
	 * @param prModItemModel 行项目
	 * @param queryForList 计算顺序
	 * @return
	 */
	public List<Map<String,Object>> calcuPrSubtotal(SalePrModItemModel prModItemModel,List<Map<String,Object>> queryForList);
	/**
	 * 根据XML字符串保存修改的价格
	 * @param xml
	 * @return
	 */
	public Message savePrModXML(String xml);
	
	/**
	 * 初始化销售价格信息
	 * @param saleId 订单主键
	 * @return
	 */
	public Message addItemPrice(String saleId);
	
	/**
	 * 借贷项
	 * @param xml
	 * @return
	 */
	public Message saveLoanAmount(String xml);
	
	/**
	 * 财务确认更新返点
	 * @param id  订单id
	 */
	public void updateCust(String orderCode);
	
	/**
	 * 取消订单行项目要减去返点
	 * @param orderCode
	 * @param itemNo
	 */
	public void cancelOrder(String bgCode);
	
	/**
	 * 重新 计算 返点金额
	 * @param saleItemPriceList
	 * @param kunnr
	 * @param orderDate
	 */
	public void reCalculate(List<SaleItemPrice> saleItemPriceList ,String kunnr ,Date orderDate,String status) ;
}
