/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.MaterialProperty;
import com.mw.framework.support.dao.GenericRepository;

public interface MaterialPropertyDao extends GenericRepository<MaterialProperty, String>{
	@Query("select t from MaterialProperty t left join t.materialHead h  where t.status is null and h.id = ?1 order by t.orderby ")
	public List<MaterialProperty> queryMaterialProperty(String pid);
}
