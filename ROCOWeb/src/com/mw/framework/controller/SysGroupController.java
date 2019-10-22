/**
 *
 */
package com.mw.framework.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mw.framework.bean.Message;
import com.mw.framework.commons.GenericController;
import com.mw.framework.domain.SysGroup;
import com.mw.framework.domain.SysUser;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.SysMenuController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-8
 *
 */
@Controller
@RequestMapping("/core/group/*")
public class SysGroupController extends GenericController<SysGroup>{
	 
	@Override
	protected String getAppName() {
		return "用户组管理";
	}
	 
	@Override
	protected String[] resultJsonExcludeField() {
		return null;
	}
	
	/**
	 * 查询单个对象
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@IgnoreProperties(value={@IgnoreProperty(name = { "hibernateLazyInitializer", "handler", "fieldHandler"}, pojo = SysGroup.class),
			@IgnoreProperty(name = {"hibernateLazyInitializer", "handler", "fieldHandler","groups","roles","custHeader","createUser","createTime","updateUser","updateTime","rowStatus","password"}, pojo = SysUser.class)})
	@ResponseBody
	public Message get(@PathVariable String id){
		Message msg = null;
		try {
			SysGroup group = commonManager.getById(id, SysGroup.class);
			msg = new Message(group);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("C-TT-500", "系统未找到对应属性,非法参数!");
		}
		return msg;
	}
	
	/**
	 * 初始化：用户
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"/user/{id}"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler","group","roles","custHeader","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = SysUser.class)})
	@ResponseBody
	public Message getForRoles(@PathVariable String id){
		Message msg = null;
		SysGroup group = commonManager.getOne(id, SysGroup.class);
		msg = new Message(group.getUsers());
		return msg;
	}
	
	/**
	 * 保存关联用户信息
	 * @param rid
	 * @param mids
	 * @return
	 */
	@RequestMapping(value = {"/user/save"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message saveForUser(String id,String[] child){
		Message msg = null;
		
		try {
			SysGroup group = commonManager.getById(id, SysGroup.class);
			
			Set<SysUser> users = new HashSet<SysUser>();
			if(child!=null){
				for (String string : child) {
					SysUser user =commonManager.getById(string, SysUser.class);
					users.add(user);
				}
			}
			
			group.setUsers(users);
			commonManager.save(group);
		 
			msg = new Message("保存成功!");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("RO-S-500", e.getLocalizedMessage());
		}
		
		return msg; 
	}
}
