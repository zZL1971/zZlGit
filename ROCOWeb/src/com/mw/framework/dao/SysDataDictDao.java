package com.mw.framework.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import com.mw.framework.bean.Constants.Language;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.support.dao.GenericRepository;

@Component
public interface SysDataDictDao extends GenericRepository<SysDataDict, String> {
	
	//@Cacheable(value="andCache",key="#root.args[0]+#root.methodName")
	//public Page<SysDataDict> findByPid(String pid,Pageable pageRequest);
	
	//@Cacheable(value="andCache",key="#root.args[0]+#root.methodName")
	
	//public List<SysDataDict> findByPid(String pid);
	
	//public List<SysDataDict> findByPidForSSM(String pid);
	public void delCache(String keyVal);
	
	public List<SysDataDict> saveForSSM(List<SysDataDict> entities);
	
	public void deleteInBatchForSSM(List<SysDataDict> entities);
	
	public SysDataDict saveForSSM(SysDataDict dataDict,String...strings);
	
	public SysDataDict saveAndFlushForSSM(SysDataDict entity,String...strings);
	
	public SysDataDict get(String pid);
	
	public Page<SysDataDict> findByTrieTreeId(String id,Pageable pageRequest);
	
	public List<SysDataDict> findByTrieTreeIdForSSM(String id);
	
	public List<SysDataDict> findByTrieTreeId(String id);
	
	@Query("from SysDataDict sd where sd.trieTree.id=?1 and sd.stat='1'")
	public List<SysDataDict> findByTrieTreeIdAndStat(String trieTreeId);
	
	@Query("from SysDataDict sd where sd.trieTree.id=? and sd.type=?")
	public List<SysDataDict> findByType(String id,String type);
	
	public List<SysDataDict> findByTrieTreeKeyVal(String key);
	public List<SysDataDict> findByTrieTreeKeyValAndStat(String key,boolean stat);
	
	public void deleteForSSM(String id);
	
	public List<SysDataDict> findByTrieTreeKeyValForSSM(String keyVal);
	
	public List<SysDataDict> findByTrieTreeKeyValAndStatForSSM(String keyVal);
	
	public <T> T getDescForI18N(String dict,String key,Language language);
	
	public SysDataDict findByKeyVal(String keyVal);
	
	public SysDataDict findByTrieTreeKeyValAndKeyVal(String keyVal1,String keyVal2);
}
