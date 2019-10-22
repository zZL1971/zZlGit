/**
 *
 */
package com.mw.framework.activiti.manager;

import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.manager.MyCompanyGroupManager.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-2-6
 * 
 */
public class CustomGroupEntityManager extends GroupEntityManager {
	private static Logger log = LoggerFactory.getLogger(CustomGroupEntityManager.class);
	
	@Override
	public List<Group> findGroupsByUser(String userId) {
		log.info("findGroupByUser called with userId: " + userId);
		return super.findGroupsByUser(userId);
	}

	@Override
	public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
		log.info("findGroupByQueryCriteria called, query: " + query+ " page: " + page);
		return super.findGroupByQueryCriteria(query, page);
	}

	@Override
	public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
		log.info("findGroupCountByQueryCriteria called, query: " + query);
		return super.findGroupCountByQueryCriteria(query);
	}

	@Override
	public Group createNewGroup(String groupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteGroup(String groupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupQuery createNewGroupQuery() {
		log.debug("createNewGroupQuery called");
		return super.createNewGroupQuery();
	}

	@Override
	public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
		log.info("findGroupCountByNativeQuery called");
		return super.findGroupCountByNativeQuery(parameterMap);
	}

	@Override
	public List<Group> findGroupsByNativeQuery(
			Map<String, Object> parameterMap, int firstResult, int maxResults) {
		log.info("findGroupsByNativeQuery called");
		return super.findGroupsByNativeQuery(parameterMap, firstResult, maxResults);
	}
	
	
}
