package com.main.manager;

import java.util.List;

import com.main.domain.sys.SysFile;
import com.mw.framework.manager.CommonManager;

public interface SysFileManager extends CommonManager {

	public List<SysFile> querySysFile(String foreignId);

	public List<SysFile> querySysFile(String foreignId, String fileType);

}
