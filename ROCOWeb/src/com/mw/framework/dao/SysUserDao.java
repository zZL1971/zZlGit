/**
 *
 */
package com.mw.framework.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import com.mw.framework.domain.SysUser;
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
public interface SysUserDao  extends GenericRepository<SysUser, String>{
	
	public SysUser findByLoginNoAndPassword(String loginNo,String password);
	public SysUser findByIdAndPassword(String id,String password);
	
	
	/**
	 * 根据组织架构Id查询对应的用户数量
	 * @param id
	 * @return
	 */
	//@Query(value="SELECT s from SysUser s,SysRole o where o.id in elements(s.roles) and o.id=?1")
	//public List<SysUser> findByOrganeId(String id);
}
