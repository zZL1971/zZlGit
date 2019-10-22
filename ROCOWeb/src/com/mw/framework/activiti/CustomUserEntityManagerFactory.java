/**
 *
 */
package com.mw.framework.activiti;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;

import com.mw.framework.activiti.manager.CustomUserEntityManager;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.activiti.CustomUserEntityManagerFactory.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-2-6
 *
 */
public class CustomUserEntityManagerFactory implements SessionFactory{
 
	@Override
	public Class<?> getSessionType() {
		return UserIdentityManager.class;
	}

 
	@Override
	public Session openSession() {
		return new CustomUserEntityManager();
	}

}
