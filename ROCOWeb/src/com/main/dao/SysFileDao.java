/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.sys.SysFile;
import com.mw.framework.support.dao.GenericRepository;

public interface SysFileDao extends GenericRepository<SysFile, String> {

	@Query("from SysFile where foreignId = ?1 ")
	public List<SysFile> querySysFile(String foreignId);

	@Query("from SysFile where foreignId = ?1 and fileType = ?2 ")
	public List<SysFile> querySysFile(String foreignId, String fileType);

}
