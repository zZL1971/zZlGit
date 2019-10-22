package com.main.domain.sys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SYS_JOB_POOL_ERROR")
public class SysJobPoolError extends UUIDEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1399305660069976782L;

	private String errorInfo;
	
	private String errorCode;
	
	private String errorOrderCode;
	
	private String errorSapCode;
	
	private String errorType;
	
	private String isManager;
	
	private String pid;

	@Column(name="ERROR_INFO",length=255)
	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@Column(name="ERROR_CODE",length=10)
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	@Column(name="ERROR_ORDER_CODE",length=50)
	public String getErrorOrderCode() {
		return errorOrderCode;
	}

	public void setErrorOrderCode(String errorOrderCode) {
		this.errorOrderCode = errorOrderCode;
	}
	
	@Column(name="ERROR_SAP_CODE",length=30)
	public String getErrorSapCode() {
		return errorSapCode;
	}

	public void setErrorSapCode(String errorSapCode) {
		this.errorSapCode = errorSapCode;
	}

	@Column(name="ERROR_TYPE",length=10)
	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	@Column(name="ERROR_MANAGER",length=1)
	public String getIsManager() {
		return isManager;
	}

	public void setIsManager(String isManager) {
		this.isManager = isManager;
	}

	@Column(name="PID",length=50)
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
}
