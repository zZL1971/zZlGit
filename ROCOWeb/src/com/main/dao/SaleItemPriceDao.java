/**
 *
 */
package com.main.dao;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.sale.SaleItemPrice;
import com.mw.framework.support.dao.GenericRepository;


public interface SaleItemPriceDao extends GenericRepository<SaleItemPrice, String>{
	@Query("select t from SaleItemPrice t left join t.saleItem h where h.id = ?1 order by t.orderby ")
	public List<SaleItemPrice> querySaleItemPrice(String pid);
}
