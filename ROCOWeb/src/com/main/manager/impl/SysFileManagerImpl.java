package com.main.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.main.dao.SysFileDao;
import com.main.domain.sys.SysFile;
import com.main.manager.SysFileManager;
import com.mw.framework.manager.impl.CommonManagerImpl;

@Service("sysFileManager")
@Transactional
public class SysFileManagerImpl extends CommonManagerImpl implements
		SysFileManager {

	@Autowired
	private SysFileDao sysFileDao;

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<SysFile> querySysFile(String foreignId) {
		return sysFileDao.querySysFile(foreignId);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<SysFile> querySysFile(String foreignId, String fileType) {
		return sysFileDao.querySysFile(foreignId, fileType);
	}

}
