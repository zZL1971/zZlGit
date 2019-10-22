package com.main.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dao.CustHeaderDao;
import com.main.dao.CustItemDao;
import com.main.manager.CustManager;
import com.mw.framework.manager.impl.CommonManagerImpl;

@Service("custManager")
@Transactional
public class CustManagerImpl extends CommonManagerImpl implements CustManager {

	@Autowired
	private CustHeaderDao custHeaderDao;
	@Autowired
	private CustItemDao custItemDao;

}
