package com.main.domain.sale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SALE_ITEM_PRICE")
public class SaleItemPrice extends UUIDEntity implements java.io.Serializable{
	/**
	 * 类型
	 */
	private String type;
	/**
	 * 价格类型描述
	 */
	private String typeDsec;
	
	/**
	 * 加或减
	 */
	private String plusOrMinus;
	/**
	 * 运算条件  加减乘除（保存数据字典的id）
	 */
	private String condition;
	/**
	 * 运算值
	 */
	private Double conditionValue;
	
	/**
	 * 乘数量
	 */
	private String isTakeNum;
	
	/**
	 * 运算小计
	 */
	private Double subtotal;
	
	
	/**
	 * 运算总计
	 */
	private Double total;
	/**
	 * 排序
	 */
	private Integer orderby;
	/**
	 * 主表
	 */
	private SaleItem saleItem;
	
	@Column(name = "TYPE_DSEC",length = 50)
	public String getTypeDsec() {
		return typeDsec;
	}
	public void setTypeDsec(String typeDsec) {
		this.typeDsec = typeDsec;
	}
	@Column(name = "CONDITION",length = 32)
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	@Column(name = "CONDITION_VALUE")
	public Double getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(Double conditionValue) {
		this.conditionValue = conditionValue;
	}
	@Column(name = "ORDERBY")
	public Integer getOrderby() {
		return orderby;
	}
	public void setOrderby(Integer orderby) {
		this.orderby = orderby;
	}
	/**
	 * 主表
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALE_ITEMID",referencedColumnName="ID")
	public SaleItem getSaleItem() {
		return saleItem;
	}
	/**
	 * 主表
	 */
	public void setSaleItem(SaleItem saleItem) {
		this.saleItem = saleItem;
	}
	@Column(name = "PLUS_OR_MINUS",length = 2)
	public String getPlusOrMinus() {
		return plusOrMinus;
	}
	public void setPlusOrMinus(String plusOrMinus) {
		this.plusOrMinus = plusOrMinus;
	}
	
	@Column(name = "IS_TAKE_NUM",length = 2)
	public String getIsTakeNum() {
		return isTakeNum;
	}
	public void setIsTakeNum(String isTakeNum) {
		this.isTakeNum = isTakeNum;
	}
	
	@Column(name = "SUBTOTAL",length = 50)
	public Double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}
	
	@Column(name = "TOTAL",length = 50)
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	@Column(name = "TYPE",length = 10)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
