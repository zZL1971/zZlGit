package com.mw.framework.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.mw.framework.domain.SerialNumber;
import com.mw.framework.support.dao.GenericRepository;

public interface SerialNumberDao extends
		GenericRepository<SerialNumber, String> {
	@Query("from SerialNumber where category = ?1 ")
	public List<SerialNumber> findByCategory(String category);
}
