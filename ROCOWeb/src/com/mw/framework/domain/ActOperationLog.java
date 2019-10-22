package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonDateSerializer;

import java.util.*;

/**
 * 任务操作记录表
 * 
 * @author Mark Wong
 * 
 * 
 */
@Entity
@Table(name = "ACT_OPERATION_LOG")
public class ActOperationLog extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7950068146824731455L;
	/**
	 * 最早操作人
	 */
	private String oldAssignee;
	/**
	 * 最后操作人
	 */
	private String lastAssignee;
	/**
	 * 任务Id
	 */
	private String taskId;

	/**
	 * 任务名称
	 */
	private String actName;
	/**
	 * 任务代码
	 */
	private String actId;
	/**
	 * 映射id
	 */
	private String pid;
	/**
	 * 最早开始时间
	 */
	private Date startTime;
	/**
	 * 最晚结束时间
	 */
	private Date endTime;
	/**
	 * 任务退回次数
	 */
	private int reCount;

	@Column(name = "OLD_ASSIGNEE", length = 40)
	public String getOldAssignee() {
		return oldAssignee;
	}

	public void setOldAssignee(String oldAssignee) {
		this.oldAssignee = oldAssignee;
	}

	@Column(name = "LAST_ASSIGNEE", length = 40)
	public String getLastAssignee() {
		return lastAssignee;
	}

	public void setLastAssignee(String lastAssignee) {
		this.lastAssignee = lastAssignee;
	}

	@Column(name = "ACT_NAME", length = 40)
	public String getActName() {
		return actName;
	}

	public void setActName(String actName) {
		this.actName = actName;
	}

	@Column(name = "ACT_ID", length = 40)
	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}

	@Column(name = "PID", length = 40)
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Column(name = "START_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "END_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "RE_COUNT", length = 10)
	public int getReCount() {
		return reCount;
	}

	public void setReCount(int reCount) {
		this.reCount = reCount;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
