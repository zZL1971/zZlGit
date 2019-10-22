/**
 *
 */
package com.mw.framework.activiti;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;

import com.mw.framework.activiti.manager.CustomGroupEntityManager;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.activiti.CustomGroupManager.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-2-6
 *
 */
public class CustomGroupEntityManagerFactory implements  SessionFactory 	 {

	@Override
	public Class<?> getSessionType() {
		return GroupIdentityManager.class;
	}

	@Override
	public Session openSession() {
		return new CustomGroupEntityManager();
	}

}
