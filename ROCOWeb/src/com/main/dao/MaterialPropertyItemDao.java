/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import com.main.domain.mm.MaterialPropertyItem;
import com.mw.framework.support.dao.GenericRepository;

public interface MaterialPropertyItemDao extends GenericRepository<MaterialPropertyItem, String>{
	@Query("select t from MaterialPropertyItem t left join t.materialHead h where h.id = ?1  ")
	public List<MaterialPropertyItem> queryMaterialPropertyItem(String pid);
}
