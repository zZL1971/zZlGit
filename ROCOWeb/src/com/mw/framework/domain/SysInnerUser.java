package com.mw.framework.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SYS_INNER_USER")
public class SysInnerUser extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5558679142508130402L;

	public SysInnerUser(){
		
	}
	
	//订单审绘组别
	private String drawingGroup;
	
	//柜体目标
	private String cabinetTarget;
	
	//质量目标
	private String qualityTarget;
	
	private SysUser sysUser;

	

	//订单审绘组别
	@Column(name = "DRAWING_GROUP")
	public String getDrawingGroup() {
		return drawingGroup;
	}

	public void setDrawingGroup(String drawingGroup) {
		this.drawingGroup = drawingGroup;
	}

	//柜体目标
	@Column(name = "CABINET_TARGET")
	public String getCabinetTarget() {
		return cabinetTarget;
	}

	public void setCabinetTarget(String cabinetTarget) {
		this.cabinetTarget = cabinetTarget;
	}

	//质量目标
	@Column(name = "QUALITY_TARGET")
	public String getQualityTarget() {
		return qualityTarget;
	}

	public void setQualityTarget(String qualityTarget) {
		this.qualityTarget = qualityTarget;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

}

