/**
 *
 */
package com.mw.framework.model;

import java.io.Serializable;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.model.OnLineUserModel.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-13
 * 
 */
public class OnLineUserModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String userId;
	private String userName;
	private String roles;
	private String createTime;
	private String optTime;
	private String session;
	private String na;
	private String ia;

	public OnLineUserModel() {
		super();
	}

	public OnLineUserModel(String userId, String userName, String roles,
			String createTime, String optTime, String session,String na,String ia) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.roles = roles;
		this.createTime = createTime;
		this.optTime = optTime;
		this.session = session;
		this.na = na;
		this.ia = ia;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getOptTime() {
		return optTime;
	}

	public void setOptTime(String optTime) {
		this.optTime = optTime;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getNa() {
		return na;
	}

	public void setNa(String na) {
		this.na = na;
	}

	public String getIa() {
		return ia;
	}

	public void setIa(String ia) {
		this.ia = ia;
	}

}
