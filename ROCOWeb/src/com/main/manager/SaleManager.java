package com.main.manager;

import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;

import com.mw.framework.bean.Message;
import com.main.bean.SaleBean;
import com.main.domain.cust.CustItem;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemPrice;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.redis.RedisUtils;

public interface SaleManager extends CommonManager {

	public SaleHeader save(SaleHeader saleHeader,SaleBean saleBean);

	public List<SaleItemPrice> querySaleItemPrice(String pid);

	public Double calculationPrice(List<SaleItemPrice> saleItemPrices,
			int amount,boolean updateFlag,String shouDafang);
	public CustItem getCustItem(String shoudafang);
	
	public CustItem getCustItem(String shoudafang,Date orderDate);

	public void deleteSaleItemByIds(String[] ids, String custCode);

	public List<SaleItemPrice> getSaleItemPrice(SaleItem saleItem);
	
	public SaleItemPrice getSaleItemPrice(String id);
	
	public void deleteSaleByIds(String[] ids);
	
	public Double  getFuFuanMoney(SaleHeader saleHeader);
	
	public Message taskCustomComplete (Object nextflow, String uuid, DelegateTask delegateTask, RedisUtils redisUtils, Object assignee);
	/**
	 * 订单保存,新增界面
	 * @param saleHeader
	 * @param saleBean
	 * @param createUser
	 * @return
	 */
	public SaleHeader saveFB(SaleHeader saleHeader, SaleBean saleBean,String createUser);

	public void deleteMaterialPrice(List<MaterialPrice> materialPriceList);


	
}
