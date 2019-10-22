package com.mw.framework.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;


/**
 * @Project ROCOWeb
 * @author Mark 
 * @fieldName com.mw.framework.domain.SysActConf.java
 * @time 2016-05-09
 * @version 1.0.0
 */
@Entity
@Table(name = "SYS_ACT_CONF")
public class SysActConf extends UUIDEntity implements Serializable  {
	private String actId;
	private String actName;
	private Timestamp mornShiftStartTime;
	private Timestamp mornShiftEndTime;
	private Timestamp afternShiftStartTime;
	private Timestamp afternShiftEndTime;
	private Integer extraWorkTime;
	private Integer overTime;
	public SysActConf(String actId, String actName, Timestamp mornShiftStartTime,
			Timestamp mornShiftEndTime, Timestamp afternShiftStartTime,
			Timestamp afternShiftEndTime, Integer extraWorkTime, 
			Integer overTime) {
		super();
		this.actId = actId;
		this.actName = actName;
		this.mornShiftStartTime = mornShiftStartTime;
		this.mornShiftEndTime = mornShiftEndTime;
		this.afternShiftStartTime = afternShiftStartTime;
		this.afternShiftEndTime = afternShiftEndTime;
		this.extraWorkTime = extraWorkTime;
		this.overTime = overTime;
	}
	public String getActId() {
		return actId;
	}
	public void setActId(String actId) {
		this.actId = actId;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public Timestamp getMornShiftStartTime() {
		return mornShiftStartTime;
	}
	public void setMornShiftStartTime(Timestamp mornShiftStartTime) {
		this.mornShiftStartTime = mornShiftStartTime;
	}
	public Timestamp getMornShiftEndTime() {
		return mornShiftEndTime;
	}
	public void setMornShiftEndTime(Timestamp mornShiftEndTime) {
		this.mornShiftEndTime = mornShiftEndTime;
	}
	public Timestamp getAfternShiftStartTime() {
		return afternShiftStartTime;
	}
	public void setAfternShiftStartTime(Timestamp afternShiftStartTime) {
		this.afternShiftStartTime = afternShiftStartTime;
	}
	public Timestamp getAfternShiftEndTime() {
		return afternShiftEndTime;
	}
	public void setAfternShiftEndTime(Timestamp afternShiftEndTime) {
		this.afternShiftEndTime = afternShiftEndTime;
	}
	public Integer getExtraWorkTime() {
		return extraWorkTime;
	}
	public void setExtraWorkTime(Integer extraWorkTime) {
		this.extraWorkTime = extraWorkTime;
	}
	public Integer getOverTime() {
		return overTime;
	}
	public void setOverTime(Integer overTime) {
		this.overTime = overTime;
	}
	
	
	
	
	
}
