/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.MaterialFile;
import com.mw.framework.support.dao.GenericRepository;

public interface MaterialFileDao extends GenericRepository<MaterialFile, String>{
	@Query("select t from MaterialFile t left join t.materialHead h where  t.status is null and h.id = ?1 and t.fileType = ?2  order by t.createTime desc ")
	public List<MaterialFile> queryMaterialFile(String pid,String fileType);
}
