/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.bg.BgHeader;
import com.mw.framework.support.dao.GenericRepository;

public interface BgHeaderDao extends GenericRepository<BgHeader, String> {
	@Query("from BgHeader where bgCode = ?1 ")
	public List<BgHeader> findByCode(String code);
}
