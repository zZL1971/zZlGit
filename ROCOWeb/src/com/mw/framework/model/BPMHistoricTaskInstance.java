/**
 *
 */
package com.mw.framework.model;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.serializer.JsonDateFullSerializer;
import com.mw.framework.util.annotation.FieldMeta;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.model.BPMHistoricTaskInstance.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-11
 * 
 */
public class BPMHistoricTaskInstance {

	private String name;
	@FieldMeta(type = "date", format = "Y-m-d H:i:s")
	private Date createTime;
	@FieldMeta(type = "date", format = "Y-m-d H:i:s")
	private Date endTime;
	private String waste;
	private String commentUserId;
	private String commentMessage;
	private String id;
	private String pid;
	private String assignee;
	private String tel;//受理人号码
	private String qqNumber;//受理人QQ
	private String tackit;
	@FieldMeta(type = "date", format = "Y-m-d H:i:s")
	private Date claimTime;

	public BPMHistoricTaskInstance() {
		super();
	}

	public BPMHistoricTaskInstance(String name, Date createTime, Date endTime,
			String commentUserId, String commentMessage, String assignee,
			Date claimTime) {
		super();
		this.name = name;
		this.createTime = createTime;
		this.endTime = endTime;
		this.commentUserId = commentUserId;
		this.commentMessage = commentMessage;
		this.assignee = assignee;
		this.claimTime = claimTime;
	}
	
	public BPMHistoricTaskInstance(String name, Date createTime, Date endTime,
			String commentUserId, String commentMessage, String assignee,
			String tel, String qqNumber, Date claimTime) {
		super();
		this.name = name;
		this.createTime = createTime;
		this.endTime = endTime;
		this.commentUserId = commentUserId;
		this.commentMessage = commentMessage;
		this.assignee = assignee;
		this.tel = tel;
		this.qqNumber = qqNumber;
		this.claimTime = claimTime;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getQqNumber() {
		return qqNumber;
	}

	public void setQqNumber(String qqNumber) {
		this.qqNumber = qqNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonDateFullSerializer.class)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getTackit() {
		return tackit;
	}

	public void setTackit(String tackit) {
		this.tackit = tackit;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonDateFullSerializer.class)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getWaste() {
		if (this.endTime != null && this.createTime != null) {
			long l = this.endTime.getTime() - this.createTime.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			// System.out.println("" + day + "天" + hour + "小时" + min + "分" + s +
			// "秒");
			return (day == 0 ? "" : day + "天") + (hour == 0 ? "" : hour + "小时")
					+ (min == 0 ? "" : min + "分") + s + "秒";
		}
		return waste;
	}

	public void setWaste(String waste) {
		this.waste = waste;
	}

	public String getCommentUserId() {
		return commentUserId;
	}

	public void setCommentUserId(String commentUserId) {
		this.commentUserId = commentUserId;
	}

	public String getCommentMessage() {
		return commentMessage;
	}

	public void setCommentMessage(String commentMessage) {
		this.commentMessage = commentMessage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonDateFullSerializer.class)
	public Date getClaimTime() {
		return claimTime;
	}

	public void setClaimTime(Date claimTime) {
		this.claimTime = claimTime;
	}

}
