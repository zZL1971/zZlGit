/**
 *
 */
package com.main.dao;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import com.main.domain.mm.MaterialPriceCondition;
import com.mw.framework.support.dao.GenericRepository;


public interface MaterialPriceConditionDao extends GenericRepository<MaterialPriceCondition, String>{
//	@Query("select t from MaterialPriceCondition t left join t.materialHead h where h.id = ?1 order by t.orderby ")
//	public List<MaterialPriceCondition> queryMaterialPriceCondition(String pid);
}
