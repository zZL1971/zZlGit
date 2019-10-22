/**
 *
 */
package com.mw.framework.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.support.dao.GenericRepository;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.dao.SysTrieTreeDao.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-4-12
 *
 */
public interface SysTrieTreeDao  extends GenericRepository<SysTrieTree, String>{
	
	public List<SysTrieTree> findByParentId(String pid);
	
	public Page<SysTrieTree> findById(String id,Pageable pageRequest);
	
	public SysTrieTree findByKeyVal(String keyVal);
}
