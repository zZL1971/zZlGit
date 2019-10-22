/**
 *
 */
package com.mw.framework.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mw.framework.domain.SysRole;
import com.mw.framework.support.dao.GenericRepository;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.dao.SysOrgan.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-3-9
 *
 */
@Component
public interface SysRoleDao  extends GenericRepository<SysRole, String>{
	
	public Page<SysRole> findById(String id,Pageable pageRequest);
	
	public List<SysRole> findByParent(SysRole parent,Sort sort);
}
