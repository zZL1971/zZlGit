package com.mw.framework.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "ACT_CT_TASK_LOG")
public class SysActTaskLog extends UUIDEntity implements Serializable {
	/**
	 * 单号
	 */
	private String orderCode;
	/**
	 * 任务编号
	 */
	private String taskNo;
	/**
	 * 任务名称
	 */
	private String taskName;
	/**
	 * 处理人
	 */
	private String assignee;

	/**
	 * 上次处理人
	 */
	private String oldAssignee;

	/**
	 * 任务标识
	 */
	private String taskId;

	/**
	 * 任务分组
	 */
	private String groupId;

	/**
	 * 版本
	 */
	private String proDefID;

	/**
	 * 售达方
	 */
	private String shouDaFang;

	/**
	 * 售达方名称
	 */
	private String name1;
	
	/**
	 * 订单类型
	 */
	private String orderType;
	
	/**
	 * 订单日期
	 */
	private Date orderDate;

	public SysActTaskLog() {
		super();
	}
	
	public SysActTaskLog(String orderCode, String taskNo, String taskName,
			String assignee, String oldAssignee, String taskId, String groupId,
			String proDefID, String shouDaFang, String name1, String orderType,
			Date orderDate) {
		super();
		this.orderCode = orderCode;
		this.taskNo = taskNo;
		this.taskName = taskName;
		this.assignee = assignee;
		this.oldAssignee = oldAssignee;
		this.taskId = taskId;
		this.groupId = groupId;
		this.proDefID = proDefID;
		this.shouDaFang = shouDaFang;
		this.name1 = name1;
		this.orderType = orderType;
		this.orderDate = orderDate;
	}


	@Column(name = "ORDER_TYPE")
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	@Column(name = "ORDER_DATE")
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	@Column(name = "SHOU_DA_FANG")
	public String getShouDaFang() {
		return shouDaFang;
	}

	public void setShouDaFang(String shouDaFang) {
		this.shouDaFang = shouDaFang;
	}

	@Column(name = "NAME1")
	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	@Column(name = "TASK_ID")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Column(name = "GROUP_ID")
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Column(name = "PRO_DEF_ID")
	public String getProDefID() {
		return proDefID;
	}

	public void setProDefID(String proDefID) {
		this.proDefID = proDefID;
	}

	@Column(name = "OLD_ASSIGNEE")
	public String getOldAssignee() {
		return oldAssignee;
	}

	public void setOldAssignee(String oldAssignee) {
		this.oldAssignee = oldAssignee;
	}

	@Column(name = "ORDER_CODE")
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "TASK_NO")
	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	@Column(name = "TASK_NAME")
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Column(name = "ASSIGNEE")
	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@Override
	public String toString() {
		return "SysActTaskLog [orderCode=" + orderCode + ", taskNo=" + taskNo
				+ ", taskName=" + taskName + ", assignee=" + assignee
				+ ", oldAssignee=" + oldAssignee + "]";
	}

}
