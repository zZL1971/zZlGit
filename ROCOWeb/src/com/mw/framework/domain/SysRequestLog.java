/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.domain.SysRequestLog.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2016-5-6
 * 
 */
@Entity
@Table(name = "SYS_REQUEST_LOG")
public class SysRequestLog extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = 6115685606666910360L;
	private String userName;
	private String role;
	private String ua;
	private String ia;
	private String na;
	private String url;
	private String params;
	private Date requestLastTime;
	private Date requestTime;
	private Date responseTime;
	private String responseStatus;
	private String responseObject;
	private String method;
	private String ex;
	private String sessionId;

	public SysRequestLog() {
		super();
	}

	public SysRequestLog(String userName, String role, String ua, String ia,
			String na, String url, String params, Date requestLastTime,
			Date requestTime, Date responseTime, String responseStatus,
			String responseObject, String method, String ex, String sessionId) {
		super();
		this.userName = userName;
		this.role = role;
		this.ua = ua;
		this.ia = ia;
		this.na = na;
		this.url = url;
		this.params = params;
		this.requestLastTime = requestLastTime;
		this.requestTime = requestTime;
		this.responseTime = responseTime;
		this.responseStatus = responseStatus;
		this.responseObject = responseObject;
		this.method = method;
		this.ex = ex;
		this.sessionId = sessionId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public String getIa() {
		return ia;
	}

	public void setIa(String ia) {
		this.ia = ia;
	}

	public String getNa() {
		return na;
	}

	public void setNa(String na) {
		this.na = na;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "PARAMS", columnDefinition = "CLOB", nullable = true)
	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEx() {
		return ex;
	}

	public void setEx(String ex) {
		this.ex = ex;
	}

	public Date getRequestLastTime() {
		return requestLastTime;
	}

	public void setRequestLastTime(Date requestLastTime) {
		this.requestLastTime = requestLastTime;
	}

	public Date getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(String responseObject) {
		this.responseObject = responseObject;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
