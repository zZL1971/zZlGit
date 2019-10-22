/**
 *
 */
package com.main.dao;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.MaterialPriceCondition;
import com.main.domain.mm.PriceCondition;
import com.mw.framework.support.dao.GenericRepository;


public interface PriceConditionDao extends GenericRepository<PriceCondition, String>{
	@Query("select t from PriceCondition t  where t.status is null order by t.orderby ")
	public List<PriceCondition> queryPriceCondition();
}
