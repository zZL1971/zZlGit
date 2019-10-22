/**
 *
 */
package com.mw.framework.manager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.main.bean.MaterialBean;
import com.main.bean.SaleBean;
import com.main.domain.mm.MaterialComplainid;
import com.main.domain.sale.SaleHeader;
import com.mw.framework.bean.OrderBy;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.manager.CommonManager.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-6-19
 *
 */
public interface CommonManager {

	public <T> T save(Object obj);
	
	/**
	 * 保存并更新流水号
	 * @param obj
	 * @param strings 流水号字段，可以写多个
	 */
	public <T> T save(Object obj,String...strings);
	
	public <T> List<T> save(Iterable<?> iterable);
	
	public void delete(Serializable id,Class<?> clazz);
	
	public void delete(Object obj);
	
	public void delete(Iterable<?> iterable, Class<?> clazz);
	
	public void delete(Serializable[] serializables, Class<?> clazz);
	
	public <T> T getOne(Serializable id,Class<?> clazz);
	
	public <T> T getById(Serializable id,Class<?> clazz);
	
	public <T> List<T> getAll(Class<?> clazz);
	
	public <T> List<T> createQueryByIn(Class<?> clazz, String field, Set set);
	
	public <T> Page<T> queryByRange(Class<?> clazz,Map<String, String[]> parameterMap,int page,int limit,OrderBy...orders);
	
	public <T> List<T> queryByRange(Class<?> clazz,Map<String, String[]> parameterMap,OrderBy...orders);
	
	public <T> Page<T> queryByRange(Object object,int page,int limit,OrderBy...orders);
	
	public <T> List<T> queryByRange(Object object,OrderBy...orders);

	
}
