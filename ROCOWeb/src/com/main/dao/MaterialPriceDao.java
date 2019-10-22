package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.MaterialPrice;
import com.mw.framework.support.dao.GenericRepository;

public interface MaterialPriceDao extends GenericRepository<MaterialPrice, String>{

	@Query(value="from MaterialPrice mp where mp.pid=?")
	List<MaterialPrice> findByPid(String pid);

}
