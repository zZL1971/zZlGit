package com.mw.framework.manager;

import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;

import com.main.domain.sys.SysJobPool;

public interface SysMesSendManager extends CommonManager {

	public void saveSysMesSend(String title, String content, String sendUser,Set<String> userSet);
	
	public void sendUser(String title, String content, String sendUser,Set<String> userSet);
	
	public void sendUser(String title, String content, String sendUser,String receiveUser);
	
	public void sendUser(String title, String content, String sendUser,String receiveUser,Boolean isRead);
	
	public void sendUser(String content, String sendUser,String receiveUser);
	
	public void sendUser(String content, String sendUser,String receiveUser,Boolean isRead);

	public void sendAllUser(String title, String content, String sendUser);
	
	public void sendMsg2Cus(JdbcTemplate jdbcTemplate, SysJobPool sysJobPool, String value);
}
