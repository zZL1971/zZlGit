package com.main.domain.mm;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mw.framework.bean.impl.UUIDEntity;
/**
 * 属性配置表
 * @author Administrator
 *
 */
@Entity
@Table(name = "MATERIAL_PROPERTY_ITEM")
public class MaterialPropertyItem  extends UUIDEntity implements Serializable {
	
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
	private String info0;
	private String info1;
	private String info2;
	private String info3;
	private String info4;
	private String info5;

	
	private String info0desc;
	private String info1desc;
	private String info2desc;
	private String info3desc;
	private String info4desc;
	private String info5desc;
	
	private Double price;
	private Double num;
	/**
	 * 编号
	 */
	private String  serialNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID",referencedColumnName="ID")
	public MaterialHead getMaterialHead() {
		return materialHead;
	}
	public void setMaterialHead(MaterialHead materialHead) {
		this.materialHead = materialHead;
	}
	
	@Column(name = "INFO0",length = 32)
	public String getInfo0() {
		return info0;
	}
	public void setInfo0(String info0) {
		this.info0 = info0;
	}
	@Column(name = "INFO1",length = 32)
	public String getInfo1() {
		return info1;
	}
	public void setInfo1(String info1) {
		this.info1 = info1;
	}
	@Column(name = "INFO2",length = 32)
	public String getInfo2() {
		return info2;
	}
	public void setInfo2(String info2) {
		this.info2 = info2;
	}
	@Column(name = "INFO3",length = 32)
	public String getInfo3() {
		return info3;
	}
	public void setInfo3(String info3) {
		this.info3 = info3;
	}
	@Column(name = "INFO4",length = 32)
	public String getInfo4() {
		return info4;
	}
	public void setInfo4(String info4) {
		this.info4 = info4;
	}
	@Column(name = "INFO5",length = 32)
	public String getInfo5() {
		return info5;
	}
	public void setInfo5(String info5) {
		this.info5 = info5;
	}
	@Column(name = "PRICE")
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	@Column(name = "NUM")
	public Double getNum() {
		return num;
	}
	public void setNum(Double num) {
		this.num = num;
	}
	@Transient
	public String getInfo0desc() {
		return info0desc;
	}
	public void setInfo0desc(String info0desc) {
		this.info0desc = info0desc;
	}
	@Transient
	public String getInfo1desc() {
		return info1desc;
	}
	public void setInfo1desc(String info1desc) {
		this.info1desc = info1desc;
	}
	@Transient
	public String getInfo2desc() {
		return info2desc;
	}
	public void setInfo2desc(String info2desc) {
		this.info2desc = info2desc;
	}
	@Transient
	public String getInfo3desc() {
		return info3desc;
	}
	public void setInfo3desc(String info3desc) {
		this.info3desc = info3desc;
	}
	@Transient
	public String getInfo4desc() {
		return info4desc;
	}
	public void setInfo4desc(String info4desc) {
		this.info4desc = info4desc;
	}
	@Transient
	public String getInfo5desc() {
		return info5desc;
	}
	public void setInfo5desc(String info5desc) {
		this.info5desc = info5desc;
	}
	@Column(name = "SERIAL_NUMBER",length = 32)
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
}
