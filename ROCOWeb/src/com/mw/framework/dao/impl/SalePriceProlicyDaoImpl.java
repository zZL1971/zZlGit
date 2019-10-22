/**
 *
 */
package com.mw.framework.dao.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.springframework.beans.factory.annotation.Autowired;

import com.mw.framework.bean.Constants.Language;
import com.mw.framework.dao.SalePriceProlicyDao;

import com.mw.framework.domain.SalePriceProlicy;

import com.mw.framework.manager.CommonManager;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.dao.impl.DataDicDaoImpl.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-2-25
 *
 */
public class SalePriceProlicyDaoImpl  {

	//private static final String NAMESPACE="sys:dicts:";
	@Autowired
	private SalePriceProlicyDao SalePriceProlicyDao;
	
	public SalePriceProlicy saveAndFlushForSSM(SalePriceProlicy entity,String...strings){
		
		return SalePriceProlicyDao.saveAndFlush(entity, strings);
		//return null;
	}
	
	public SalePriceProlicy saveForSSM(SalePriceProlicy entity,String...strings){

		return SalePriceProlicyDao.save(entity, strings);
		//return null;
	}
	
}
