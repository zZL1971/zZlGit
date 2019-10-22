/**
 *
 */
package com.main.dao;


import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.cust.TerminalClient;
import com.main.domain.sale.SaleHeader;
import com.mw.framework.support.dao.GenericRepository;

public interface TerminalClientDao extends
		GenericRepository<TerminalClient, String> {
	@Query("from TerminalClient where sale_id = ?1 ")
	public TerminalClient findBysaleid(String code);
}
