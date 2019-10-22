package com.main.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.main.domain.sys.SysJobPool;
import com.main.manager.SysJobPoolManager;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.GenericController;
/**
 * 我的物品
 * @author zjc
 *
 */
@Controller
@RequestMapping("/main/jobPool/*")
public class SysJobPoolController  extends GenericController<SysJobPool>{
	private static final Logger logger = LoggerFactory.getLogger(SysJobPoolController.class);
	@Autowired
	private SysJobPoolManager sysJobPoolManagerImpl;


	public SysJobPoolManager getSysJobPoolManagerImpl() {
		return sysJobPoolManagerImpl;
	}


	public void setSysJobPoolManagerImpl(SysJobPoolManager sysJobPoolManagerImpl) {
		this.sysJobPoolManagerImpl = sysJobPoolManagerImpl;
	}


	public static Logger getLogger() {
		return logger;
	}


	@Override
	protected String getAppName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String[] resultJsonExcludeField() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Message delete(String[] ids) {
		// TODO Auto-generated method stub
		return new Message("Job-500","该数据不允许删除！！！");
	}
	
    
}
