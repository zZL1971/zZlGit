package com.mw.framework.model;

public class TaskModel {
	private String sn;
	private String orderType;
	private String orderDate;
	private String shouDaFang;
	private String name1;
	private String id;
	private String name;
	private String groupId;
	private String assignee;
	private String createTime;
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getShouDaFang() {
		return shouDaFang;
	}
	public void setShouDaFang(String shouDaFang) {
		this.shouDaFang = shouDaFang;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "TaskModel [sn=" + sn + ", orderType=" + orderType
				+ ", orderDate=" + orderDate + ", shouDaFang=" + shouDaFang
				+ ", name1=" + name1 + ", id=" + id + ", name=" + name
				+ ", groupId=" + groupId + ", assignee=" + assignee
				+ ", createTime=" + createTime + "]";
	}
	
	
	
}
