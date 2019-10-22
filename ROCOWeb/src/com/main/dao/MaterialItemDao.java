/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.MaterialItem;
import com.mw.framework.support.dao.GenericRepository;


public interface MaterialItemDao extends GenericRepository<MaterialItem, String>{
	@Query("select t from MaterialItem t left join t.materialHead h where h.id = ?1 ")
	public List<MaterialItem> queryMaterialItem(String pid);
}
