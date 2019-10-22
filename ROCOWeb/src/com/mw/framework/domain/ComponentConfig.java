package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.main.util.ExcellField;
import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "COMPONENT_CONFIG")
public class ComponentConfig extends UUIDEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1595319220790443083L;
	
	@ExcellField(cellVal="部件名称（name)")
	private String componentName;//部件名称
	
	@ExcellField(cellVal="标识码INFO1")
	private String identifyCode;//标识码
	
	@ExcellField(cellVal="对内产品交期")
	private Integer dnDelivery;//对内交期
	
	@ExcellField(cellVal="对外产品交期")
	private Integer dwDelivery;//对外交期
	
	@ExcellField(cellVal="补单交期")
	private Integer repairOrderDelivery;//补单交期
	
	@ExcellField(cellVal="外购标识码")
	private String outSourceIdentifyCode;//外购标识码
	
	@ExcellField(cellVal="物料编码ARTICLE_ID")
	private String materialCode;//物料编码
	
	private String stat;//是否启用
	
	private String isStandard;//是否标准
	
	private String line;//产线

	@Column(name="IS_STANDARD",length=1)
	public String getIsStandard() {
		return isStandard;
	}

	public void setIsStandard(String isStandard) {
		this.isStandard = isStandard;
	}

	@Column(name="LINE",length=10)
	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Column(name="COMPONENT_NAME",length=100)
	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	@Column(name="IDENTIFY_CODE",length=30)
	public String getIdentifyCode() {
		return identifyCode;
	}

	public void setIdentifyCode(String identifyCode) {
		this.identifyCode = identifyCode;
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

	@Column(name="REPAIR_ORDER_DELIVERY")
	public Integer getRepairOrderDelivery() {
		return repairOrderDelivery;
	}

	public void setRepairOrderDelivery(Integer repairOrderDelivery) {
		this.repairOrderDelivery = repairOrderDelivery;
	}

	@Column(name="OUT_SOURCE_IDENTIFY_CODE",length=30)
	public String getOutSourceIdentifyCode() {
		return outSourceIdentifyCode;
	}

	public void setOutSourceIdentifyCode(String outSourceIdentifyCode) {
		this.outSourceIdentifyCode = outSourceIdentifyCode;
	}

	@Column(name="MATERIAL_CODE",length=50)
	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	@Column(name="STAT",length=1)
	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
}
