package com.main.domain.mm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.main.util.ExcellField;
import com.mw.framework.bean.impl.UUIDEntity;



/**
 * 物料价格 信息 Bean
 * @author Chaly
 * @version V1.0.0.0
 * @date 2018-11-02 11:57:34
 */
@Entity
@Table(name = "MATERIAL_PRICE")
public class MaterialPrice extends UUIDEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6145761116632575497L;
	
	@ExcellField(cellVal="颜色")
	private String color;//颜色
	
	@ExcellField(cellVal="宽")
	private Integer wide;//宽
	
	@ExcellField(cellVal="高")
	private Integer high;//高
	
	@ExcellField(cellVal="深")
	private Integer deep;//深
	
	@ExcellField(cellVal="数量")
	private Integer amount;//数量
	
	@ExcellField(cellVal="单位")
	private String unit;//单位
	
	@ExcellField(cellVal="面积")
	private Double area;//计量/面积
	
	@ExcellField(cellVal="单价")
	private Double unitPrice;//单价
	
	@ExcellField(cellVal="总价")
	private Double totalPrice;//总价
	
	@ExcellField(cellVal="折扣")
	private Double rebate;//折扣
	
	@ExcellField(cellVal="净价")
	private Double netPrice;//净价
	
	@ExcellField(cellVal="产线")
	private String line;//产线
	
	@ExcellField(cellVal="类型")
	private String type;//类型
	
	@ExcellField(cellVal="名称")
	private String name;//名称
	
	private String pid;
	
	@Column(name = "TYPE", length = 30)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Column(name = "NAME", length = 50)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "COLOR", length = 30)
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	@Column(name = "WIDE")
	public Integer getWide() {
		return wide;
	}
	public void setWide(Integer wide) {
		this.wide = wide;
	}
	@Column(name = "HIGH")
	public Integer getHigh() {
		return high;
	}
	public void setHigh(Integer high) {
		this.high = high;
	}
	@Column(name = "DEEP")
	public Integer getDeep() {
		return deep;
	}
	public void setDeep(Integer deep) {
		this.deep = deep;
	}
	@Column(name = "AMOUNT")
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	@Column(name = "UNIT", length = 20)
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	@Column(name = "AREA")
	public Double getArea() {
		return area;
	}
	public void setArea(Double area) {
		this.area = area;
	}
	@Column(name = "UNIT_PRICE")
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	@Column(name = "TOTAL_PRICE")
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	@Column(name = "REBATE")
	public Double getRebate() {
		return rebate;
	}
	public void setRebate(Double rebate) {
		this.rebate = rebate;
	}
	@Column(name = "NET_PRICE")
	public Double getNetPrice() {
		return netPrice;
	}
	public void setNetPrice(Double netPrice) {
		this.netPrice = netPrice;
	}
	@Column(name = "LINE", length = 30)
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	@Column(name = "PID")
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	
}
