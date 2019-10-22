/**
 *
 */
package com.mw.framework.manager.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;



import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mw.framework.bean.OrderBy;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.support.dao.GenericRepository;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.manager.CommonManagerImpl.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-6-19
 *
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Service("commonManager")
@Transactional
public class CommonManagerImpl implements CommonManager {
	private static final Logger logger = LoggerFactory.getLogger(CommonManagerImpl.class);
	
	//@Autowired
	//private SysSerialNumberDao serialNumberDao;
//	import javax.persistence.EntityManager;
	@PersistenceContext(unitName = "entityManagerFactory")  
    private EntityManager entityManager;  
     
    @PersistenceUnit(unitName = "entityManagerFactory")  
    private EntityManagerFactory entityManagerFactory;
	
	private GenericRepository getDao(Class clazz) {
		if (clazz == null) {
			return SpringContextHolder.getBean("");
		}
		GenericRepository dao = null;
		String firstLetter = clazz.getSimpleName().substring(0, 1).toLowerCase();
		String className = firstLetter + clazz.getSimpleName().substring(1,clazz.getSimpleName().indexOf("_")!=-1?clazz.getSimpleName().indexOf("_"):clazz.getSimpleName().length());
		try {
			dao = SpringContextHolder.getBean(className + "Dao");
			//logger.info("获取"+className + "Dao成功!");
		} catch (RuntimeException e) {
			logger.error("没有配置"+className + "Dao");
		}
		return dao;
	}
	
	@Override
	public <T> T save(Object obj) {
		Object save = getDao(obj.getClass()).save(obj);
		logger.info("调用保存方法，并提交事务!");
		return (T)save ;
	}
	
	@Override
	public <T> T save(Object obj,String...strings) {
		Object save = getDao(obj.getClass()).save(obj,strings);
		logger.info("调用保存含流水编号方法，并提交事务!");
		return (T)save ;
	}

	@Override
	public void delete(Serializable id, Class<?> clazz) {
		getDao(clazz).delete(id);
		logger.info("调用删除方法，并提交事务!");
	}
	
	@Override
	public void delete(Iterable<?> iterable, Class<?> clazz){
		getDao(clazz).delete(iterable);
	}
	
	@Override
	public void delete(Serializable[] serializables, Class<?> clazz){
		for (Serializable serializable : serializables) {
			this.delete(serializable, clazz);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public <T> T getOne(Serializable id,Class<?> clazz){
		//logger.info("调用查询单个对象方法，不需要事务处理!");
		Object one = getDao(clazz).findOne(id);
		return (T) one;
	}
	
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public <T> T getById(Serializable id,Class<?> clazz){
		//logger.info("调用查询单个对象方法，不需要事务处理!");
		Object one = getDao(clazz).getOne(id);
		return (T) one;
	}
	
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public <T> List<T> getAll(Class<?> clazz){
		logger.info("调用查询所有对象方法，不需要事务处理!");
		return getDao(clazz).findAll();
	}
	
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public <T> List<T> createQueryByIn(Class<?> clazz, String field, Set set){
		logger.info("调用查询对象方法，不需要事务处理!");
//		System.out.println("======>"+entityManagerFactory);
		if(set==null || set.size()==0){
//			return getDao(clazz).findAll();
			return (List<T>)new ArrayList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> createQuery = criteriaBuilder.createQuery(clazz);
		Root<?> from = createQuery.from(clazz);
		Path<Object> path = from.get(field);
		Predicate predicate = path.in(set);
		createQuery.where(predicate);
		List<?> resultList = entityManager.createQuery(createQuery).getResultList();
		return (List<T>) resultList;
	}
	
	@Override
	public <T> List<T> save(Iterable<?> iterable){
		List save = getDao(iterable.iterator().next().getClass()).save(iterable);
		logger.info("调用批量保存方法，并提交事务!");
		return save;
	}
	
	@Override
	public <T> Page<T> queryByRange(Class<?> clazz,Map<String, String[]> parameterMap,int page,int limit,OrderBy...orders){
		return getDao(clazz).queryByRange(parameterMap, page, limit, orders);
	}
 
	@Override
	public <T> List<T> queryByRange(Class<?> clazz,Map<String, String[]> parameterMap, OrderBy... orders) {
		return  getDao(clazz).queryByRange(parameterMap, orders);
	}

	 
	@Override
	public <T> Page<T> queryByRange(Object object, int page, int limit,OrderBy... orders) {
		return getDao(object.getClass()).queryByRange(object, page, limit, orders);
	}

	 
	@Override
	public <T> List<T> queryByRange(Object object, OrderBy... orders) {
		return getDao(object.getClass()).queryByRange(object, orders);
	}
 
	@Override
	public void delete(Object obj) {
		getDao(obj.getClass()).delete(obj);
	}
	 

}
