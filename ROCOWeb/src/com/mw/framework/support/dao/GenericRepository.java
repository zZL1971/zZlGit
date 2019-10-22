/**
 *
 */
package com.mw.framework.support.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.mw.framework.bean.OrderBy;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.support.dao.MyRepository.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-4-15
 * 
 */
@NoRepositoryBean
public interface GenericRepository<T, ID extends Serializable> extends
		JpaRepository<T, ID>,JpaSpecificationExecutor<T> {
	
	<S extends T> S save(S entity,String...strings);
	
	<S extends T> S saveAndFlush(S entity,String...strings);
	
	List<T> queryByNativeQuery(String sql);
	
	List<T> queryByNativeQuery(String sql,Class<?> clazz);
	
	Page<T> queryByRange(Map<String, String[]> parameterMap,int page,int limit,OrderBy...orders);
	
	List<T> queryByRange(Map<String, String[]> parameterMap,OrderBy...orders);
	
	Page<T> queryByRange(Object object,int page,int limit,OrderBy...orders);
	
	List<T> queryByRange(Object object,OrderBy...orders);
	
	List<T> createQuery(String hql);
	
	List<T> createQuery(CriteriaQuery<T> criteriaQuery);
	
	//List<T> queryByRecursive(String sql);
	//List<T> queryByRecursive(String sql,Class<?> clazz);
	//List<T> queryByFilter(PropertyFilter filter,Sort sort);
	//List<T> findAll(Specification<T> spec);
	//Page<T> findAll(Specification<T> spec, Pageable pageable);  
	//List<T> findAll(Specification<T> spec, Sort sort); 
}
