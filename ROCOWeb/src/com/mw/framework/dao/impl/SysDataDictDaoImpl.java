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
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
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
public class SysDataDictDaoImpl  {

	private static final String NAMESPACE="sys:dicts:";
	@Autowired
	private SysDataDictDao sysDataDictDao;
	@Autowired
	private SysTrieTreeDao sysTrieTreeDao;
	
	@Autowired
	private MemcachedClient memcachedClient;
	
	@Autowired
	private CommonManager commonManager;
	
	public void delCache(final String keyVal){
		try {
			boolean delete = memcachedClient.delete(NAMESPACE+keyVal);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
	
	public List<SysDataDict> saveForSSM(List<SysDataDict> entities){
		for (SysDataDict sysDataDict : entities) {
			SysTrieTree one = sysTrieTreeDao.getOne(sysDataDict.getTrieTree().getId());
			this.delCache(one.getKeyVal());
			this.delCache(one.getId());
		}
		return sysDataDictDao.save(entities);
	}
	
	public void deleteInBatchForSSM(List<SysDataDict> entities){
		for (SysDataDict sysDataDict : entities) {
			SysTrieTree one = sysTrieTreeDao.getOne(sysDataDict.getTrieTree().getId());
			this.delCache(one.getKeyVal());
			this.delCache(one.getId());
		}
		sysDataDictDao.deleteInBatch(entities);
	}
	
	
	public SysDataDict saveAndFlushForSSM(SysDataDict entity,String...strings){
		SysTrieTree one = sysTrieTreeDao.findOne(entity.getTrieTree().getId());
		this.delCache(one.getKeyVal());
		this.delCache(one.getId());
		return sysDataDictDao.saveAndFlush(entity, strings);
	}
	
	public SysDataDict saveForSSM(SysDataDict entity,String...strings){
		SysTrieTree one = sysTrieTreeDao.getOne(entity.getTrieTree().getId());
		this.delCache(one.getKeyVal());
		this.delCache(one.getId());
		return sysDataDictDao.save(entity, strings);
	}
	
	public void deleteForSSM(String id){
		SysDataDict sysDataDict = sysDataDictDao.get(id);
		if(sysDataDict!=null){
			SysTrieTree one = sysTrieTreeDao.getOne(sysDataDict.getTrieTree().getId());
			this.delCache(one.getKeyVal());
			this.delCache(one.getId());
			sysDataDictDao.delete(id);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SysDataDict> findByTrieTreeIdForSSM(String id){
		List<SysDataDict> findByTrieTreeId = null;
		try {
			Object object = memcachedClient.get(NAMESPACE+id);
			if(object==null){
				findByTrieTreeId = sysDataDictDao.findByTrieTreeId(id);
				Collections.sort(findByTrieTreeId);
				memcachedClient.set(NAMESPACE+id, 0, findByTrieTreeId);
			}else{
				findByTrieTreeId =  (List<SysDataDict>) object;
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		
		return findByTrieTreeId;
	}
	
	@SuppressWarnings("unchecked")
	public List<SysDataDict> findByTrieTreeKeyValForSSM(final String keyVal){
		List<SysDataDict> findByTrieTreeKeyVal = null;
		try {
			Object object = memcachedClient.get(NAMESPACE+keyVal);
			
			if(object==null){
				findByTrieTreeKeyVal = sysDataDictDao.findByTrieTreeKeyVal(keyVal);
				Collections.sort(findByTrieTreeKeyVal);
				memcachedClient.set(NAMESPACE+keyVal, 0, findByTrieTreeKeyVal);
			}else{
				findByTrieTreeKeyVal =  (List<SysDataDict>) object;
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		
		return findByTrieTreeKeyVal;
	}
	
	
	public List<SysDataDict> findByTrieTreeKeyValAndStatForSSM(final String keyVal){
		List<SysDataDict> findByTrieTreeKeyVal = null;
		try {
			Object object = memcachedClient.get(NAMESPACE+keyVal);
			if(object==null){
				findByTrieTreeKeyVal = sysDataDictDao.findByTrieTreeKeyValAndStat(keyVal,true);
				Collections.sort(findByTrieTreeKeyVal);
				memcachedClient.set(NAMESPACE+keyVal, 0, findByTrieTreeKeyVal);
			}else{
				findByTrieTreeKeyVal =  (List<SysDataDict>) object;
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		
		return findByTrieTreeKeyVal;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getDescForI18N(String dict,String key,Language language){
		List<SysDataDict> findByTrieTreeKeyValForSSM = findByTrieTreeKeyValForSSM(dict);
		for (SysDataDict sysDataDict : findByTrieTreeKeyValForSSM) {
			if(sysDataDict.getKeyVal().equals(key))
			switch (language) {
			case zh_CN:
				return (T) sysDataDict.getDescZhCn();
			case zh_TW:
				return (T) sysDataDict.getDescZhTw();		
			case en_US:
				return (T) sysDataDict.getDescEnUs();
			default:
				break;
			}
		}
		return null;
	}
	
	public SysDataDict getSysDataDict(String dict,String key){
		Map<String,String[]> parameterMap = new LinkedHashMap<String, String[]>();
    	parameterMap.put("ICEQtrieTree__keyVal", new String[]{dict});
        List<SysDataDict> queryByRange = commonManager.queryByRange(SysDataDict.class, parameterMap);
		for (SysDataDict sysDataDict : queryByRange) {
			if(sysDataDict.getKeyVal().equals(key))
			{
				return sysDataDict;
			}
		}
		return null;
	}
	
	
	
	//@ReadThroughMultiCache(namespace = NAMESPACE, expiration = 3600)
    //@CacheName("appCache")
	public List<SysDataDict> findByPidForSSM(String pid){
		System.out.println("调用约定Dao实现find方法");
		//查看mencached缓存的值
		//Cache cache = SpringContextHolder.getBean("appCache");
		//System.out.println();
		//sysDataDictDao.findOne(pid);
		return /*sysDataDictDao.findByPid(pid)*/null;
	}
	
	//com.google.code.ssm.Cache@UpdateSingleCache(namespace=NAMESPACE, expiration = 3600)
	//@CacheName("appCache")
	//@InvalidateMultiCache(namespace= NAMESPACE, expiration = 3600)
	public void saveForSSM(SysDataDict dataDic){
		System.out.println("调用约定Dao实现save方法");
		
//		sysDataDictDao.save(dataDic);
	}
	
	//@ReadThroughSingleCache(namespace = NAMESPACE, expiration = 3600)
    //@CacheName("appCache")
	public SysDataDict get(String id){
		System.out.println("调用查询单个对象的缓存,看是否成功");
		
		/*try {
			Object object = appCache.get("hello", SerializationType.PROVIDER);
			System.out.println("object:"+object);
			Collection<String> aliases = appCache.getAliases();
			for (String string : aliases) {
				System.out.println(string);
			}
			
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		}*/
		return /*sysDataDictDao.findOne(id)*/null;
	}
}
