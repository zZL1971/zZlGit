package com.main.manager;

import com.main.bean.BgBean;
import com.main.domain.bg.BgHeader;
import com.main.domain.sys.SysBz;
import com.mw.framework.bean.Message;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;

public interface BgManager extends CommonManager {

	public BgHeader save(BgHeader bgHeader, BgBean bgBean);
	
	public BgHeader save(BgHeader bgHeader, SysBz sysBz);
	
	public Message submit(String id, String status, String title, String step, String remarks,SysUser sysUser);
}
