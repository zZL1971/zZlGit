/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.ImosIdbext;
import com.mw.framework.support.dao.GenericRepository;

public interface ImosIdbextDao extends GenericRepository<ImosIdbext, String> {
	@Query("from ImosIdbext where orderid = ?1 ")
	public List<ImosIdbext> findByOrderid(String orderid);
}
