/**
 *
 */
package com.main.dao;

import com.main.domain.sys.SysJobPool;
import com.mw.framework.support.dao.GenericRepository;

public interface SysJobPoolDao extends GenericRepository<SysJobPool, String> {
 
	public SysJobPool findByZuonr(String zuonr);
}
