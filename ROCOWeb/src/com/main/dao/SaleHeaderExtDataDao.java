/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.sale.SaleHeaderExtData;
import com.mw.framework.support.dao.GenericRepository;

public interface SaleHeaderExtDataDao extends GenericRepository<SaleHeaderExtData, String> {

	@Query("from SaleHeaderExtData where pid = ?1  ")
	public List<SaleHeaderExtData> findItemsByPid(String pid);
}
