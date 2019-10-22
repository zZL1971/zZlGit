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
import com.mw.framework.domain.SysMenu;
import com.mw.framework.domain.SysRole;
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
@RequestMapping("/core/menu/*")
public class SysMenuController extends GenericController<SysMenu>{
	 
	@Override
	protected String getAppName() {
		return null;
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
	@IgnoreProperties(value={@IgnoreProperty(name = { "hibernateLazyInitializer", "handler", "fieldHandler","children"}, pojo = SysMenu.class),
			@IgnoreProperty(name = { "hibernateLazyInitializer", "handler", "fieldHandler", "parent","children","users","menus"}, pojo = SysRole.class)})
	@ResponseBody
	public Message get(@PathVariable String id){
		Message msg = null;
		try {
			SysMenu menu = commonManager.getById(id, SysMenu.class);
			msg = new Message(menu);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("C-TT-500", "系统未找到对应属性,非法参数!");
		}
		return msg;
	}
	
	/**
	 * 保存关联角色信息
	 * @param rid
	 * @param mids
	 * @return
	 */
	@RequestMapping(value = {"/role/save"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message saveForRole(String id,String[] child){
		Message msg = null;
		
		try {
			SysMenu menu = commonManager.getById(id, SysMenu.class);
			
			Set<SysRole> roles = new HashSet<SysRole>();
			if(child!=null){
				for (String string : child) {
					SysRole role =commonManager.getById(string, SysRole.class);
					roles.add(role);
				}
			}
			
			menu.setRoles(roles);
			commonManager.save(menu);
		 
			msg = new Message("保存成功!");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("RO-S-500", e.getLocalizedMessage());
		}
		
		return msg; 
	}
	
	@RequestMapping(value = {"/role/{id}"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","menus","users","parent","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = SysRole.class)})
	@ResponseBody
	public Message getForRoles(@PathVariable String id){
		Message msg = null;
		SysMenu menu = commonManager.getOne(id, SysMenu.class);
		msg = new Message(menu.getRoles());
		return msg;
	}
}
