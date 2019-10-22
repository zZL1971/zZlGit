/**
 *
 */
package com.mw.framework.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.mw.framework.domain.SysActCTOrdErr;
import com.mw.framework.support.dao.GenericRepository;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.dao.SysActCTMappingDao.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-16
 *
 */
public interface SysActCTOrdErrDao  extends GenericRepository<SysActCTOrdErr, String>{

	@Query("from SysActCTOrdErr aco where aco.mappingId=?1 and aco.errType=?2 and aco.errRea=?3")
	public List<SysActCTOrdErr> findBySysActCtOrdErrList(String mappingId,String errType,String errRea);
	
	@Query("from SysActCTOrdErr aco where aco.mappingSid=?1 and aco.errType='S02'")
	public SysActCTOrdErr findByMappingSidAndErrTypeIsS02(String mappingSid);
}
