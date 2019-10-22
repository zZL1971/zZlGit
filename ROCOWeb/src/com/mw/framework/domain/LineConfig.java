package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name="LINE_CONFIG")
public class LineConfig extends UUIDEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 719866923069154965L;

	private String lineCode;//产线名称
	
	private Integer dnDelivery;//对内交期
	
	private Integer dwDelivery;//对外交期
	
	private Integer zoRepairOrder;//单产线 补单交期
	
	private Integer zmRepairOrder;//多产线 补单交期
	
	private Integer zmDelivery;//多产线 叠加 天数 标准单
	
	private String isOutSource;//是否外购
	
	private String isMaterial;//物料识别
	
	private String stat;//是否启用

	@Column(name="LINE_CODE",length=10)
	public String getLineCode() {
		return lineCode;
	}

	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}

	@Column(name="DN_DELIVERY")
	public Integer getDnDelivery() {
		return dnDelivery;
	}

	public void setDnDelivery(Integer dnDelivery) {
		this.dnDelivery = dnDelivery;
	}

	@Column(name="DW_DELIVERY")
	public Integer getDwDelivery() {
		return dwDelivery;
	}

	public void setDwDelivery(Integer dwDelivery) {
		this.dwDelivery = dwDelivery;
	}

	@Column(name="O_REPAIR_ORDER")
	public Integer getZoRepairOrder() {
		return zoRepairOrder;
	}

	public void setZoRepairOrder(Integer zoRepairOrder) {
		this.zoRepairOrder = zoRepairOrder;
	}

	@Column(name="M_REPAIR_ORDER")
	public Integer getZmRepairOrder() {
		return zmRepairOrder;
	}

	public void setZmRepairOrder(Integer zmRepairOrder) {
		this.zmRepairOrder = zmRepairOrder;
	}

	@Column(name="M_DELIVERY")
	public Integer getZmDelivery() {
		return zmDelivery;
	}

	public void setZmDelivery(Integer zmDelivery) {
		this.zmDelivery = zmDelivery;
	}

	@Column(name="IS_OUT_SOURCE",length=1)
	public String getIsOutSource() {
		return isOutSource;
	}

	public void setIsOutSource(String isOutSource) {
		this.isOutSource = isOutSource;
	}

	@Column(name="IS_MATERIAL",length=1)
	public String getIsMaterial() {
		return isMaterial;
	}

	public void setIsMaterial(String isMaterial) {
		this.isMaterial = isMaterial;
	}
	
	@Column(name="STAT",length=1)
	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
}
