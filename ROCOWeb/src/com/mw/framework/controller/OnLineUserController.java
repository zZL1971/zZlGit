/**
 *
 */
package com.mw.framework.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nl.justobjects.pushlet.core.Session;
import nl.justobjects.pushlet.core.SessionManager;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.WebSocketSession;

import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.model.OnLineUserModel;
import com.mw.framework.websocket.handler.SystemWebSocketHandler;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.OnLineUserController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-13
 *
 */
@Controller
@RequestMapping("/core/online/user/*")
public class OnLineUserController extends BaseController{
	
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public ModelAndView index(ModelAndView modelAndView,String cache){
		
		String resource = "ON_LINE_USER_LIST";
		//清理缓存
		if(cache!=null && cache.equals("up")){
			System.out.println("****更新模板*******");
			super.deleteObjectForCache(Constants.TM_GRID_PARSE, resource);
			super.deleteObjectForCache(Constants.TM_GRID_MODEL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_CONFIG, resource);
			super.deleteObjectForCache(Constants.TM_GRID_SQL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_TREESQL, resource);
		}
		
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module","OnLineUserApp");
		modelAndView.getModelMap().put("moduleTitle", "在线用户");
		modelAndView.getModelMap().put("flowResource", "ON_LINE_USER_LIST");
		return modelAndView;
	}
	
	/**
	 * 在线用户列表
	 * @return
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JdbcExtGridBean onLineUserList(){
		//LinkedHashMap<String, OnLineUserModel> objectForCache = super.getObjectForCache(Constants.LOGIN_USER_LIST);
		//Set<Entry<String,OnLineUserModel>> entrySet = objectForCache.entrySet();
		List<OnLineUserModel> content = new ArrayList<OnLineUserModel>();
		
		
		SystemWebSocketHandler systemWebSocketHandler  = SpringContextHolder.getBean("systemWebSocketHandler");
		
		String parameter = super.getRequest().getParameter("ICCPuserId");
		
		LinkedHashMap<String,WebSocketSession> users = systemWebSocketHandler.getUsers();
		
		//websocket用户列表
		for (Entry<String, WebSocketSession> user : users.entrySet()) {
			Map<String, Object> attributes = user.getValue().getAttributes();
			String userName = String.valueOf(attributes.get(Constants.CURR_USER_NAME));
			String na = String.valueOf(attributes.get(Constants.CURR_USER_NA));
			String ia = String.valueOf(attributes.get(Constants.CURR_USER_IA));
			
			OnLineUserModel userModel = new OnLineUserModel(user.getKey(),userName,null,null,null,user.getValue().getId(),na,ia);
			content.add(userModel);
		}
		
		//pushlet用户列表
		Session[] sessions = SessionManager.getInstance().getSessions();
		for (int i = 0; i < sessions.length; i++) {
			OnLineUserModel userModel = new OnLineUserModel(sessions[i].getId(),null,null,null,null,null,null,null);
			content.add(userModel);
		}
	 
		return new JdbcExtGridBean(content);
	}
	
	/**
	 * 剔除用户
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message onLineUserRemove(@RequestParam(value = "ids") String[] ids){
		Message msg = null;
		LinkedHashMap<String, Map<String,Object>> objectForCache = super.getObjectForCache(Constants.LOGIN_USER_LIST);
		
		for (String string : ids) {
			objectForCache.remove(string);
		}
		super.setObjectForCache(Constants.LOGIN_USER_LIST, objectForCache);
		
		msg = new Message("剔除成功!");
		return msg;
	}
}
