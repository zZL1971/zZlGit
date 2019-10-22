/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.cust.CustContacts;
import com.mw.framework.support.dao.GenericRepository;


public interface CustContactsDao extends GenericRepository<CustContacts, String>{
	
	@Query("from CustContacts where kunnr = ?1 ")
	public List<CustContacts> findContactsByCode(String code);
	
	@Query("from CustContacts where pid = ?1 and abtnr <> '0099'")
	public List<CustContacts> findContactsByPid(String pid);
}
