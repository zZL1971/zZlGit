/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mw.framework.websocket.handler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.mw.framework.bean.Constants;
import com.mw.framework.context.SpringContextHolder;

/**
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.websocket.handler.SystemWebSocketHandler.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-5-5
 *
 */
@Component
public class SystemWebSocketHandler implements WebSocketHandler {
	
	private static final LinkedHashMap<String,WebSocketSession> users=new LinkedHashMap<String,WebSocketSession>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("connect to the websocket success......");
        String currUser = String.valueOf(session.getAttributes().get(Constants.CURR_USER_ID));
        users.put(currUser, session);
        
        //获取当前用户信息
        JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
        
        List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("select t.msg_body from sys_mes_send t where t.has_readed='0' and t.receive_user='"+currUser+"'");
        
        if(queryForList.size()>0 && queryForList.size()<4){
        	for (Map<String, Object> map : queryForList) {
            	session.sendMessage(new TextMessage(String.valueOf(map.get("MSG_BODY"))));
    		}
        }else if(queryForList.size()>0 && queryForList.size()>4){
        	session.sendMessage(new TextMessage("[#]"+queryForList.size()));
        }
        
    }

    @Override
    public void handleMessage(WebSocketSession wss, WebSocketMessage<?> wsm) throws Exception {
        TextMessage returnMessage = new TextMessage(wsm.getPayload()
				+ " received at server");
		wss.sendMessage(returnMessage);
    }

    @Override
    public void handleTransportError(WebSocketSession wss, Throwable thrwbl) throws Exception {
        if(wss.isOpen()){
            wss.close();
        }
        //users.remove(wss.getAttributes().get(Constants.CURR_USER_ID));
        System.out.println("websocket connection closed....11111..");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wss, CloseStatus cs) throws Exception {
    	users.remove(wss.getAttributes().get(Constants.CURR_USER_ID));
        System.out.println("websocket connection closed......");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
     
    /**
     * 给所有在线用户发送消息
     *
     * @param message
     * @throws MemcachedException 
     * @throws InterruptedException 
     * @throws TimeoutException 
     */
    public void sendMessageToUsers(TextMessage message)  {
        for (Entry<String, WebSocketSession> user : users.entrySet()) {
        	WebSocketSession webSocketSession = user.getValue();
            try {
                if (webSocketSession.isOpen()) {
                	//System.out.println(user.getKey());
                	webSocketSession.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	/**
	 * 给某个用户发送消息
	 * @param userid
	 * @param message
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public void sendMessageToUser(String userid, TextMessage message) {
		WebSocketSession socketSession = users.get(userid);
		if(socketSession!=null){
			if (socketSession.isOpen()) {
				try {
					socketSession.sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
		}
	}

	public LinkedHashMap<String, WebSocketSession> getUsers() {
		return users;
	}
    
}
