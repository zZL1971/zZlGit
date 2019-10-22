/**
 *
 */
package com.mw.framework.dao;

import java.util.List;

import com.mw.framework.domain.SysMenu;
import com.mw.framework.support.dao.GenericRepository;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.dao.SysMenuDao.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-3-14
 *
 */
public interface SysMenuDao extends GenericRepository<SysMenu, String>{
	public List<SysMenu> findByPid(String pid);
}
