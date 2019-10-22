/**
 *
 */
package com.mw.framework.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.websocket.handler.SystemWebSocketHandler;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.MessageController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-5-4
 *
 */
@Controller
@RequestMapping("/core/msg/*")
public class MessageController extends BaseController{
	static Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Bean
    public SystemWebSocketHandler systemWebSocketHandler() {
        //return new InfoSocketEndPoint();
        return new SystemWebSocketHandler();
    }
    
	@RequestMapping("/isRead")
    @ResponseBody
    public Message isRead(){
    	Message msg = null;
    	try {
			jdbcTemplate.execute("update sys_mes_send t set t.has_readed=1 where t.receive_user='"+super.getLoginUserId()+"'");
			msg = new Message("已全部标记为已读");
		} catch (DataAccessException e) {
			msg = new Message("MSG-AR-500",e.getLocalizedMessage());
			e.printStackTrace();
		}
    	
    	return msg;
    }
    
	 
	//@RequestMapping("/auditing")
    //@ResponseBody
    public String auditing(HttpServletRequest request){
		//SystemWebSocketHandler systemWebSocketHandler= SpringContextHolder.getBean("systemWebSocketHandler");
        //无关代码都省略了
        //int unReadNewsCount = 0;//adminService.getUnReadNews(username);
        //systemWebSocketHandler.sendMessageToUser("admin", new TextMessage(unReadNewsCount + ""));
        return "";
    }
	
	@RequestMapping("/send")
    @ResponseBody
    public Message broadcast(HttpServletRequest request,String type,String content){
		Message msg = null;
		try {
			mesSendManager.sendAllUser( type, content,super.getLoginUserId());
			msg = new Message(type+"发送成功!");
		} catch (Exception e) {
			msg = new Message("MSG-500",e.getLocalizedMessage());
			e.printStackTrace();
		}
        return msg;
    }
}
