/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.AssignedEntity;

/**
 * 流程错误信息
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.domain.SysActCTOrdError.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-30
 * 
 */
@Entity
@Table(name = "ACT_CT_ORD_STAT")
public class SysActCTOrdStat extends AssignedEntity implements Serializable {
	private static final long serialVersionUID = -4571992986506918034L;

	/**
	 * 流程实例ID
	 */
	private String procinstid;
	/**
	 * 流程定义ID
	 */
	private String prodefid;
	/**
	 * 任务ID
	 */
	private String taskid;
	/**
	 * 任务定义ID
	 */
	private String executionId;
	/**
	 * 任务名称
	 */
	private String taskname;
	/**
	 * 出错原因
	 */
	private String errType;
	/**
	 * 原因描述
	 */
	private String errDesc;

	/**
	 * 任务代办意见
	 */
	private String taskDesc;
	/**
	 * 目标任务节点
	 */
	private String targetTaskDefinitionKey;

	/**
	 * 当前任务节点定义key
	 */
	private String taskDefinitionKey;
	/**
	 * 目标节点名称
	 */
	private String targetTaskName;

	public SysActCTOrdStat() {
		super();
	}

	public SysActCTOrdStat(String procinstid, String prodefid, String taskid,
			String executionId, String taskname,
			String targetTaskDefinitionKey, String taskDefinitionKey,String targetTaskName) {
		super();
		this.procinstid = procinstid;
		this.prodefid = prodefid;
		this.taskid = taskid;
		this.executionId = executionId;
		this.taskname = taskname;
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
		this.taskDefinitionKey = taskDefinitionKey;
		this.targetTaskName = targetTaskName;
	}

	public SysActCTOrdStat(String procinstid, String prodefid, String taskid,
			String executionId, String taskname, String errType,
			String errDesc, String taskDesc, String targetTaskDefinitionKey,
			String taskDefinitionKey,String targetTaskName) {
		super();
		this.procinstid = procinstid;
		this.prodefid = prodefid;
		this.taskid = taskid;
		this.executionId = executionId;
		this.taskname = taskname;
		this.errType = errType;
		this.errDesc = errDesc;
		this.taskDesc = taskDesc;
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
		this.taskDefinitionKey = taskDefinitionKey;
		this.targetTaskName = targetTaskName;
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

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getTaskname() {
		return taskname;
	}

	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	public String getErrType() {
		return errType;
	}

	public void setErrType(String errType) {
		this.errType = errType;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getTargetTaskDefinitionKey() {
		return targetTaskDefinitionKey;
	}

	public void setTargetTaskDefinitionKey(String targetTaskDefinitionKey) {
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getTargetTaskName() {
		return targetTaskName;
	}

	public void setTargetTaskName(String targetTaskName) {
		this.targetTaskName = targetTaskName;
	}

}
