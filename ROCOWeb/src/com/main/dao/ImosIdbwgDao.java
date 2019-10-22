/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.ImosIdbwg;
import com.mw.framework.support.dao.GenericRepository;

public interface ImosIdbwgDao extends GenericRepository<ImosIdbwg, String> {
	@Query("from ImosIdbwg where orderid = ?1 ")
	public List<ImosIdbwg> findByOrderid(String orderid);
}
