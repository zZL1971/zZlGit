package com.mw.framework.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;
@Entity
@Table(name="SYS_USER_GRUMBLE")
public class UserGrumble extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = -4571992986506918034L;

	private Integer grumbleType;
	private String grumbleContent;
	private Date expectedTime;
	private String remarks;
	
	@Column(name="GRUMBLE_TYPE")
	public Integer getGrumbleType() {
		return grumbleType;
	}

	public void setGrumbleType(Integer grumbleType) {
		this.grumbleType = grumbleType;
	}
	@Column(name="GRUMBLE_CONTENT",length=2000)
	public String getGrumbleContent() {
		return grumbleContent;
	}

	public void setGrumbleContent(String grumbleContent) {
		this.grumbleContent = grumbleContent;
	}
	@Column(name="EXPECTED_TIME")
	public Date getExpectedTime() {
		return expectedTime;
	}

	public void setExpectedTime(Date expectedTime) {
		this.expectedTime = expectedTime;
	}
	@Column(name="REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
