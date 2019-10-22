/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.bg.BgItem;
import com.mw.framework.support.dao.GenericRepository;

public interface BgItemDao extends GenericRepository<BgItem, String> {

	@Query("from BgItem where pid = ?1 order by to_number(posex)")
	public List<BgItem> findItemsByPid(String pid);
}
