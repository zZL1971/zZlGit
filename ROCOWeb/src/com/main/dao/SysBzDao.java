/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.sys.SysBz;
import com.mw.framework.support.dao.GenericRepository;

public interface SysBzDao extends GenericRepository<SysBz, String> {

	@Query("from SysBz where zid = ?1 order by createTime")
	public List<SysBz> findSysBzsByZid(String zid);
}
