/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.sale.SaleHeader;
import com.mw.framework.support.dao.GenericRepository;

public interface SaleHeaderDao extends GenericRepository<SaleHeader, String> {
	@Query("from SaleHeader where orderCode = ?1 ")
	public List<SaleHeader> findByCode(String code);
}
