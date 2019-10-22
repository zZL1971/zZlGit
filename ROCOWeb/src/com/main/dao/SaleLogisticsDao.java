package com.main.dao;

import java.util.List;

import com.main.domain.sale.SaleLogistics;
import com.mw.framework.support.dao.GenericRepository;

public interface SaleLogisticsDao extends GenericRepository<SaleLogistics, String>{

	public List<SaleLogistics> findBySaleHeaderId(String pid);
	
	public SaleLogistics findBySaleHeaderIdAndSaleFor(String pid,String saleFor);
}
