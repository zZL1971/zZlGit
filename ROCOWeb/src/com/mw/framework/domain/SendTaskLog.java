package com.mw.framework.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "send_task")
public class SendTaskLog extends UUIDEntity implements Serializable{
	private static final long serialVersionUID = -4571992986506918034L;
	
	private String taskId;
	private String groupId;
	private String assignee;
	private int taskType;
	private int status;
	private String taskCreateTime;
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public int getTaskType() {
		return taskType;
	}
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
	
	public String getTaskCreateTime() {
		return taskCreateTime;
	}
	public void setTaskCreateTime(String taskCreateTime) {
		this.taskCreateTime = taskCreateTime;
	}
	
	public SendTaskLog(String taskId, String groupId, String assignee,
			int taskType, int status, String taskCreateTime) {
		super();
		this.taskId = taskId;
		this.groupId = groupId;
		this.assignee = assignee;
		this.taskType = taskType;
		this.status = status;
		this.taskCreateTime = taskCreateTime;
	}
	public SendTaskLog(){
		
	}
}
