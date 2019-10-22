/**
 *
 */
package com.main.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.cust.CustItem;
import com.mw.framework.support.dao.GenericRepository;


public interface CustItemDao extends GenericRepository<CustItem, String>{
	
	@Query("from CustItem where kunnr = ?1 ")
	public List<CustItem> findItemsByCode(String code);
	
	@Query("from CustItem where pid = ?1 ")
	public List<CustItem> findItemsByPid(String pid);
	
	@Query("from CustItem where kunnr = ?1 and status='1' and shengYu>=0")
	public List<CustItem> findItemsByCode1(String code);
	
	@Query("from CustItem where kunnr = ?1 and status='1' and shengYu>=0 and startDate<=?2 and endDate>=?2")
	public List<CustItem> findItemsByCode1(String code,Date orderDate);
}
