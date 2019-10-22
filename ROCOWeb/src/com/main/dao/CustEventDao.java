/**
 *
 */
package com.main.dao;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.cust.CustEvent;
import com.mw.framework.support.dao.GenericRepository;


public interface CustEventDao extends GenericRepository<CustEvent, String>{
	@Query("from CustEvent where kunnr = ?1 ")
	public CustEvent findByKunnr(String kunnr);
	
}
	