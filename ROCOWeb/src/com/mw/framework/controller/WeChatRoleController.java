package com.mw.framework.controller;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.GenericController;
import com.mw.framework.domain.SysMenu;
import com.mw.framework.domain.SysRole;
import com.mw.framework.domain.SysUser;
import com.mw.framework.domain.WeChat;
import com.mw.framework.domain.WeChatMenu;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;

@Controller
@RequestMapping("/core/wechatrole/*")
public class WeChatRoleController extends GenericController<WeChat> {

	@Override
	protected String getAppName() {
		return "角色管理";
	}

	@Override
	protected String[] resultJsonExcludeField() {
		return null;
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
			WeChat role = commonManager.getById(id, WeChat.class);
			/*
			Set<SysUser> users = new HashSet<SysUser>();
			if(child!=null)
			for (String string : child) {
				SysUser user = commonManager.getById(string, SysUser.class);
				users.add(user);
			}
			role.setUsers(users);
			commonManager.save(role);
			msg = new Message("保存成功!");*/
			
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("RO-S-500", e.getLocalizedMessage());
		}
		
		return msg; 
	}
	
	/**
	 * 保存关联菜单信息
	 * @param rid
	 * @param mids
	 * @return
	 */
	@RequestMapping(value = {"/menu/save"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message saveForMenu(String id,String[] child){
		Message msg = null;
		
		try {
			if(!"root".equals(id)){
				WeChat role = commonManager.getById(id, WeChat.class);
				
				Set<WeChatMenu> menus = new HashSet<WeChatMenu>();
				if(child!=null)
				for (String string : child) {
					WeChatMenu menu = commonManager.getById(string, WeChatMenu.class);
					menus.add(menu);
				}
				role.setMenus(menus);
				commonManager.save(role);
				msg = new Message("保存成功!");
			}else{
				msg = new Message("RO-S-500", "root角色不能分配菜单！");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			msg = new Message("RO-S-500", e.getLocalizedMessage());
		}
		
		return msg; 
	}

	/**
	 * 初始化弹窗选中:用户
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"/user/{id}"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","roles","parent","custHeader"}, pojo = SysUser.class)})
	@ResponseBody
	public Message getForUsers(@PathVariable String id){
		Message msg = null;
		WeChat role = commonManager.getOne(id, WeChat.class);
		msg = new Message(role.getUsers());
		return msg;
	}
	
	/**
	 * 初始化弹窗选中:菜单
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"/menu/{id}"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children","roles","parent","createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = WeChatMenu.class)})
	@ResponseBody
	public Message getForMenu(@PathVariable String id){
		Message msg = null;
		WeChat role = commonManager.getOne(id, WeChat.class);
		msg = new Message(role.getMenus());
		return msg;
	}
	
	/**
	 * 查询单个对象
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@IgnoreProperties(value={@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children" ,"users","menus","parent"}, pojo = WeChat.class)})
	@ResponseBody
	public Message get(@PathVariable String id){
		Message msg = null;
		try {
			WeChat one = commonManager.getOne(id, WeChat.class);
			msg = new Message(one);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("C-TT-500", "系统未找到对应属性,非法参数!");
		}
		return msg;
	}
	
	/**
	 * 查询单个对象 for tree
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"/tree"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@Deprecated
	public JsonNode getForTree(){
		//ORACLE递归查询
		String sql = "select id,desc_zh_cn,pid,remark,to_char(create_time,'yyyy-MM-dd HH24:mm:ss') as create_time,create_user,to_char(update_time,'yyyy-MM-dd HH24:mm:ss') as update_time,update_user from WeChat a start with a.pid is null connect by prior a.id=a.pid order siblings by order_by";
		
		JsonNode queryForTreeNode = super.queryForTreeNode(sql,false,false,false);
		return queryForTreeNode;
	}
	
	/**
	 * 查询配置菜单(显示复选框)
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/menu", method = RequestMethod.GET)
	@ResponseBody
	@Deprecated
	public JsonNode getConfigMenus()throws ParseException { 
		//递归取数
		String sql = "select id,desc_en_us,desc_zh_cn,url,pid from WECHAT_MENU a start with a.id ='root' connect by prior a.id=a.pid order siblings by order_by";
		return super.queryForTreeNode(sql,true,true,false);
	}
	
	/**
	 * 角色菜单预览
	 * @param id
	 */
	@RequestMapping(value = "/menuview", method = RequestMethod.GET)
	@ResponseBody
	@Deprecated
	public JsonNode getMenusForRole(String id){
		String sql = "select id,desc_en_us,desc_zh_cn,url,pid from wechat_role_menu a inner join WECHAT_MENU b on a.wechat_menu_id=b.id where a.wechat_role_id='"+id+"' start with b.id ='root' connect by prior b.id=b.pid order siblings by b.order_by";
		return super.queryForTreeNode(sql, true, true,false);
	}

}
