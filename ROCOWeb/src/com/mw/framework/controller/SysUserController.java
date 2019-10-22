/**
 *
 */
package com.mw.framework.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.main.dao.CustHeaderDao;
import com.main.domain.cust.CustHeader;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.GenericController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.domain.SysGroup;
import com.mw.framework.domain.SysRole;
import com.mw.framework.domain.SysUser;
import com.mw.framework.domain.WeChat;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.SysUserController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-3-14
 *
 */
@Controller
@RequestMapping("/core/user/*")
public class SysUserController extends GenericController<SysUser>{
	@Autowired
	private CustHeaderDao custHeaderDao;
	@Override
	protected String getAppName() {
		//createUser();
		return "用户管理";
	}
 
	@Override
	protected String[] resultJsonExcludeField() {
		return new String[]{"roles","groups"};
	}
 
	/**
	 * 查询单个对象
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@IgnoreProperties(value={@IgnoreProperty(name = { "hibernateLazyInitializer", "handler", "fieldHandler","custHeader"}, pojo = SysUser.class),
			@IgnoreProperty(name = { "hibernateLazyInitializer", "handler", "fieldHandler", "parent","users","children","menus"}, pojo = SysRole.class),
			@IgnoreProperty(name = { "hibernateLazyInitializer", "handler", "fieldHandler","users"}, pojo = SysGroup.class)})
	@ResponseBody
	public Message get(@PathVariable String id){
		Message msg = null;
		try {
			SysUser user = commonManager.getById(id, SysUser.class);
			msg = new Message(user);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("C-TT-500", "系统未找到对应属性,非法参数!");
		}
		return msg;
	}
	
	/**
	 * 保存关联用户组信息
	 * @param rid
	 * @param mids
	 * @return
	 */
	@RequestMapping(value = {"/group/save"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message saveForGroup(String id,String[] child){
		Message msg = null;
		
		try {
			SysUser user = commonManager.getById(id, SysUser.class);
			
			Set<SysGroup> groups = new HashSet<SysGroup>();
			if(child!=null){
				for (String string : child) {
					SysGroup group =commonManager.getById(string, SysGroup.class);
					groups.add(group);
				}
			}
			
			user.setGroups(groups);
			commonManager.save(user);
			
			/*SysGroup group = commonManager.getOne2("gp_drawing", SysGroup.class);
			String[] strings = new String[]{"admin","test2","test3"};
			Set<SysUser> users = new HashSet<SysUser>();
			for (String string : strings) {
				SysUser user =commonManager.getOne2(string, SysUser.class);
				users.add(user);
			}
			group.setUsers(users);
			commonManager.save(group);*/
			//createUser();
			msg = new Message("保存成功!");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("RO-S-500", e.getLocalizedMessage());
		}
		
		return msg; 
	}
	
	@RequestMapping(value = {"/group/{id}"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","roles","parent","custHeader","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = SysUser.class)})
	@ResponseBody
	public Message getForGroups(@PathVariable String id){
		Message msg = null;
		SysUser user = commonManager.getOne(id, SysUser.class);
		msg = new Message(user.getGroups());
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
			SysUser user = commonManager.getById(id, SysUser.class);
			
			Set<SysRole> roles = new HashSet<SysRole>();
			if(child!=null){
				for (String string : child) {
					SysRole role =commonManager.getById(string, SysRole.class);
					roles.add(role);
				}
			}
			
			user.setRoles(roles);
			commonManager.save(user);
		 
			msg = new Message("保存成功!");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("RO-S-500", e.getLocalizedMessage());
		}
		
		return msg; 
	}
	/**
	 * 保存关联角色信息
	 * @param rid
	 * @param mids
	 * @return
	 */
	@RequestMapping(value = {"/wechatrole/save"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message saveWeChatForRole(String id,String[] child){
		Message msg = null;
		
		try {
			SysUser user = commonManager.getById(id, SysUser.class);
			
			Set<WeChat> roles = new HashSet<WeChat>();
			if(child!=null){
				for (String string : child) {
					if ("root".equals(string)) {
						
					}else{
						WeChat role =commonManager.getById(string, WeChat.class);
						roles.add(role);
					}
				}
			}
			
			user.setWechatRoles(roles);
			commonManager.save(user);
		 
			msg = new Message("保存成功!");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("RO-S-500", e.getLocalizedMessage());
		}
		
		return msg; 
	}
	
	@RequestMapping(value = {"/role/{id}"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","menus","users","parent","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = SysRole.class),
			@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","groups","roles","parent","custHeader","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = SysUser.class)})
	@ResponseBody
	public Message getForRoles(@PathVariable String id){
		Message msg = null;
		SysUser user = commonManager.getOne(id, SysUser.class);
		msg = new Message(user.getRoles());
		return msg;
	}
	@RequestMapping(value = {"/wechatrole/{id}"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","menus","users","parent","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = WeChat.class),
			@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","groups","roles","parent","custHeader","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = SysUser.class)})
	@ResponseBody
	public Message getWeChatForRoles(@PathVariable String id){
		Message msg = null;
		SysUser user = commonManager.getOne(id, SysUser.class);
		msg = new Message(user.getWechatRoles());
		return msg;
	}
	
	public void createUser()
	{	
		String sqlString="select kunnr,ktokd from cust_header where ktokd='Z110' or ktokd='Z210' ";
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sqlString);
		List<SysUser> userList = new ArrayList<SysUser>();
		for (Map<String, Object> map : queryForList) {
			String userId=(String) map.get("KUNNR");
			String loginNo=userId.toLowerCase()+"_01";
			String ktokd=(String) map.get("ktokd");
			try{
				SysUser user = new SysUser();
				user.setId(loginNo);
				user.setKunnr(userId);
				user.setEmail("user@qq.com");
				user.setPassword("a");
				user.setUserType("A");
				user.setMoney(true);
				user.setUserName(loginNo);
				user.setRowStatus("1");
				//user.setCustHeader(custHeader);
//				custHeaderDao= SpringContextHolder.getBean("custHeaderDao");
//				List<CustHeader> findByCode = custHeaderDao.findByCode(userId);
//				if (findByCode != null && findByCode.size() > 0) {
//					user.setCustHeader(findByCode.get(0));
//				}
				//用户审核角色
				String[] groupchild={"gp_store"};
				Set<SysGroup> groups = new HashSet<SysGroup>();
				if(groupchild!=null){
					for (String string : groupchild) {
						SysGroup group =commonManager.getById(string, SysGroup.class);
						groups.add(group);
					}
				}
				
			
				
				//菜单
				String[] child={"DzoxmuG7xnaxVkLA2T5wKU"};
				Set<SysRole> roles = new HashSet<SysRole>();
				if(child!=null){
					for (String string : child) {
						SysRole role =commonManager.getById(string, SysRole.class);
						roles.add(role);
					}
				}
				user.setGroups(groups);
				user.setRoles(roles);
				userList.add(user);
				
				//user = commonManager.getById(loginNo, SysUser.class);
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("客户号:"+ktokd);
			}
			
		}
		commonManager.save(userList);
	}
}
