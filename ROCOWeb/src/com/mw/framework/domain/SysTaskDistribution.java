/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonTimestampSerializer;

/**
 * 流程任务重新分配记录表
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.domain.SysTaskDistribution.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-5-12
 * 
 */
@Entity
@Table(name = "SYS_TASK_DISTRIBUTION")
public class SysTaskDistribution extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private String serialNumber;
	private String uuid;
	private String taskId;
	private String taskName;
	private String groupId;
	private String oldAssignee;
	private String assignee;
	private Date turnTime;

	public SysTaskDistribution() {
		super();
	}

	public SysTaskDistribution(String serialNumber, String uuid, String taskId,
			String taskName, String groupId, String oldAssignee, Date turnTime) {
		super();
		this.serialNumber = serialNumber;
		this.uuid = uuid;
		this.taskId = taskId;
		this.taskName = taskName;
		this.groupId = groupId;
		this.oldAssignee = oldAssignee;
		this.turnTime = turnTime;
	}

	public SysTaskDistribution(String serialNumber, String uuid, String taskId,
			String taskName, String groupId, String oldAssignee,
			String assignee, Date turnTime) {
		super();
		this.serialNumber = serialNumber;
		this.uuid = uuid;
		this.taskId = taskId;
		this.taskName = taskName;
		this.groupId = groupId;
		this.oldAssignee = oldAssignee;
		this.assignee = assignee;
		this.turnTime = turnTime;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getOldAssignee() {
		return oldAssignee;
	}

	public void setOldAssignee(String oldAssignee) {
		this.oldAssignee = oldAssignee;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@Column(name = "TURN_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonTimestampSerializer.class)
	public Date getTurnTime() {
		return turnTime;
	}

	public void setTurnTime(Date turnTime) {
		this.turnTime = turnTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
