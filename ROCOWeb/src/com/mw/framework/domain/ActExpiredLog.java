package com.mw.framework.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "ACT_EXPIRED_LOG")
public class ActExpiredLog extends UUIDEntity implements Serializable{
	private static final long serialVersionUID = -4571992986506918034L;
	
	private String actId;
	private String mappingId;
	private String actName;
	private Integer expiredType;
	private String expiredDesc;
	private String assignee;
	private String procInstId;
	private String historyId;
	
	private Long duration;
	public String getActId() {
		return actId;
	}
	public void setActId(String actId) {
		this.actId = actId;
	}
	public String getMappingId() {
		return mappingId;
	}
	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public Integer getExpiredType() {
		return expiredType;
	}
	public void setExpiredType(Integer expiredType) {
		this.expiredType = expiredType;
	}
	public String getExpiredDesc() {
		return expiredDesc;
	}
	public void setExpiredDesc(String expiredDesc) {
		this.expiredDesc = expiredDesc;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public String getHistoryId() {
		return historyId;
	}
	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}
	public ActExpiredLog(
			Integer expiredType, String expiredDesc, String assignee,
			 String historyId, Long duration) {
		super();
		this.expiredType = expiredType;
		this.expiredDesc = expiredDesc;
		this.assignee = assignee;
		this.historyId = historyId;
		this.duration = duration;
	}
}
