/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.sale.SaleOneCust;
import com.mw.framework.support.dao.GenericRepository;

public interface SaleOneCustDao extends GenericRepository<SaleOneCust, String> {

	@Query("from SaleOneCust where pid = ?1")
	public List<SaleOneCust> findSaleOneCustsByPid(String pid);
}
