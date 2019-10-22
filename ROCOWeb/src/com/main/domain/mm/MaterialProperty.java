package com.main.domain.mm;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;
/**
 * 属性配置表
 * @author Administrator
 *
 */
@Entity
@Table(name = "MATERIAL_PROPERTY")
public class MaterialProperty  extends UUIDEntity implements Serializable {
	
	/**
	 * 可用状态  用于删除标识
	 * 可用：1
	 * 不可用：X
	 */
	private String  status;
	
	/**
	 * 可用状态
	 */
	@Column(name = "STATUS",length=2)
	public String getStatus() {
		return status;
	}
	/**
	 * 可用状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	private MaterialHead materialHead;
	/**
	 * 属性描述
	 */
	private String propertyDesc;
	/**
	 * 数据字典编码
	 */
	private String propertyCode;
	/**
	 * 排序
	 */
	private Integer orderby;
	/**
	 * 
	 */
	private String infoDesc;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID",referencedColumnName="ID")
	public MaterialHead getMaterialHead() {
		return materialHead;
	}
	public void setMaterialHead(MaterialHead materialHead) {
		this.materialHead = materialHead;
	}
	/**
	 * 属性描述
	 */
	@Column(name = "PROPERTY_DESC",length = 50)
	public String getPropertyDesc() {
		return propertyDesc;
	}
	/**
	 * 属性描述
	 */
	public void setPropertyDesc(String propertyDesc) {
		this.propertyDesc = propertyDesc;
	}
	/**
	 * 数据字典编码
	 */
	@Column(name = "PROPERTY_CODE",length = 32)
	public String getPropertyCode() {
		return propertyCode;
	}
	/**
	 * 数据字典编码
	 */
	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}
	/**
	 * 排序
	 */
	@Column(name = "ORDERBY")
	public Integer getOrderby() {
		return orderby;
	}
	/**
	 * 排序
	 */
	public void setOrderby(Integer orderby) {
		this.orderby = orderby;
	}
	@Column(name = "INFO_DESC",length = 32)
	public String getInfoDesc() {
		return infoDesc;
	}
	public void setInfoDesc(String infoDesc) {
		this.infoDesc = infoDesc;
	}
	
}
