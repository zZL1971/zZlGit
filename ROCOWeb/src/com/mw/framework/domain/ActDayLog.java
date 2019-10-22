package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonDateSerializer;

/**
 * 任务日统计表
 * 
 * @author Mark Wong
 * 
 */
@Entity
@Table(name = "ACT_DAY_LOG")
public class ActDayLog extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2678555439587412814L;
	/**
	 * 记录日期
	 */
	private Date logDate;
	/**
	 * 操作人
	 */
	private String assignee;
	/**
	 * 任务组别
	 */
	private String groupId;
	/**
	 * 完成订单数量
	 */
	private int orderCount;
	/**
	 * 退回数量
	 */
	private int reCount;
	/**
	 * 额外补数
	 */
	private int extCount;
	/**
	 * 罚单数量
	 */
	private int punishCount;

	/**
	 * PROC_DEF_KEY
	 */
	private String procDefKey;

	/**
	 * 完成方数
	 */
	private float completeSquare;

	/**
	 * 完成行项目数
	 */
	private int rowsNumber;

	/**
	 * 完成散件数
	 */
	private int sjNum;
	/**
	 * 目标数
	 */
	private int target;

	@Column(name = "LOG_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	@Column(name = "ASSIGNEE", length = 40)
	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@Column(name = "ORDER_COUNT", length = 10)
	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	@Column(name = "RE_COUNT", length = 10)
	public int getReCount() {
		return reCount;
	}

	public void setReCount(int reCount) {
		this.reCount = reCount;
	}

	@Column(name = "EXT_COUNT", length = 10)
	public int getExtCount() {
		return extCount;
	}

	public void setExtCount(int extCount) {
		this.extCount = extCount;
	}

	@Column(name = "PUNISH_COUNT", length = 10)
	public int getPunishCount() {
		return punishCount;
	}

	public void setPunishCount(int punishCount) {
		this.punishCount = punishCount;
	}

	@Column(name = "GROUP_ID", length = 40)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Column(name = "PROC_DEF_KEY", length = 40)
	public String getProcDefKey() {
		return procDefKey;
	}

	public void setProcDefKey(String procDefKey) {
		this.procDefKey = procDefKey;
	}

	@Column(name = "COMPLETE_SQUARE", length = 20)
	public float getCompleteSquare() {
		return completeSquare;
	}

	public void setCompleteSquare(float completeSquare) {
		this.completeSquare = completeSquare;
	}

	@Column(name = "ROWS_NUMBER", length = 20)
	public int getRowsNumber() {
		return rowsNumber;
	}

	public void setRowsNumber(int rowsNumber) {
		this.rowsNumber = rowsNumber;
	}

	@Column(name = "SJ_NUM", length = 20)
	public int getSjNum() {
		return sjNum;
	}

	public void setSjNum(int sjNum) {
		this.sjNum = sjNum;
	}

	@Column(name = "TARGET", length = 10)
	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

}
