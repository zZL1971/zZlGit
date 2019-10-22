/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.cust.CustHeader;
import com.mw.framework.support.dao.GenericRepository;



public interface CustHeaderDao extends GenericRepository<CustHeader, String>{
	
	@Query("from CustHeader where kunnr = ?1 ")
	public List<CustHeader> findByCode(String code);
	
	@Query("from CustHeader where kunnr = ?")
	public CustHeader finByKunnr(String kunnr);

}
