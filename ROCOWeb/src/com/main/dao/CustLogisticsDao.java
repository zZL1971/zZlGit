/**
 *
 */
package com.main.dao;

import java.util.List;

import com.main.domain.cust.CustLogistics;
import com.mw.framework.support.dao.GenericRepository;


public interface CustLogisticsDao extends GenericRepository<CustLogistics, String>{
	
	public CustLogistics findByKunnr(String kunnr);
	
	public List<CustLogistics> findByCustHeaderIdAndSpart(String pid,String saleFor);
	
	public CustLogistics findByKunnrAndSpartAndVkorg(String kunnr,String saleFor,String vkorg);
}
