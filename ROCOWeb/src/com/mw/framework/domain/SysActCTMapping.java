/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.AssignedEntity;

/**
 * 业务表单与bpm 流程实例编号关联
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.domain.SysActCTMapping.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-16
 * 
 */
@Entity
@Table(name = "ACT_CT_MAPPING")
public class SysActCTMapping extends AssignedEntity implements Serializable {
	private static final long serialVersionUID = -4571992986506918034L;

	/**
	 * 流程实例ID
	 */
	private String procinstid;
	/**
	 * 流程定义ID
	 */
	private String prodefid;

	private String procInstIdOld;

	//地区
	private String regio;
	//URGENT_TYPE 加急类型
	private String urgentType;
	//任务类型
	private String taskType;
	//订单类型
	private String orderType;
	public SysActCTMapping(String id, String procinstid, String prodefid) {
		super();
		this.procinstid = procinstid;
		super.id = id;
		this.prodefid = prodefid;
	}

	public SysActCTMapping(String id, String procinstid, String prodefid,
			String procInstIdOld) {
		super();
		super.id = id;
		this.procinstid = procinstid;
		this.prodefid = prodefid;
		this.procInstIdOld = procInstIdOld;
	}

	public SysActCTMapping() {
		super();
	}

	public String getProcinstid() {
		return procinstid;
	}

	public void setProcinstid(String procinstid) {
		this.procinstid = procinstid;
	}

	public String getProdefid() {
		return prodefid;
	}

	public void setProdefid(String prodefid) {
		this.prodefid = prodefid;
	}

	public String getProcInstIdOld() {
		return procInstIdOld;
	}

	public void setProcInstIdOld(String procInstIdOld) {
		this.procInstIdOld = procInstIdOld;
	}

	public String getUrgentType() {
		return urgentType;
	}

	public void setUrgentType(String urgentType) {
		this.urgentType = urgentType;
	}

	public String getRegio() {
		return regio;
	}

	public void setRegio(String regio) {
		this.regio = regio;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
