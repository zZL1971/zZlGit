/**
 *
 */
package com.mw.framework.activiti.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.mw.framework.dao.SysUserDao;
import com.mw.framework.domain.SysGroup;
import com.mw.framework.domain.SysUser;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.manager.CustomUserManager.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-2-6
 * 
 */
public class CustomUserEntityManager extends UserEntityManager {

	@Autowired
	private SysUserDao sysUserDao;
	
	@Override
	public List<Group> findGroupsByUser(String userId) {
		SysUser one = sysUserDao.findOne(userId);
		Set<SysGroup> sysgroups = one.getGroups();
		List<Group> groups = new ArrayList<Group>();
		for (SysGroup group : sysgroups) {
			Group g = new GroupEntity();
			
			g.setId(group.getId());
			g.setName(group.getName());
			g.setType(group.getType());
			
			groups.add(g);
		}
		return groups;
	}

	@Override
	public User findUserById(String userId) {
		UserEntity userEntity = null;
		SysUser one = sysUserDao.findOne(userId);
		
		if(one!=null){
			userEntity = new UserEntity();
			userEntity.setId(one.getId());  
	        userEntity.setFirstName(one.getLoginNo());  
	        userEntity.setLastName(one.getUserName());  
	        userEntity.setPassword(one.getPassword());  
	        userEntity.setEmail(one.getEmail());  
	        userEntity.setRevision(1);  
		}
		
		return userEntity;
	}

	@Override
	public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		//throw new RuntimeException("not implement method.");
		return null;
	}

	@Override
	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId,
			String key) {
		throw new RuntimeException("not implement method.");
	}

		@Override
	public List<String> findUserInfoKeysByUserIdAndType(String userId,
			String type) {
		throw new RuntimeException("not implement method.");
	}

	@Override
	public long findUserCountByQueryCriteria(UserQueryImpl query) {
		throw new RuntimeException("not implement method.");
	}

	@Override
	public User createNewUser(String userId) {
		throw new RuntimeException("not implement method.");
	}

	
}
