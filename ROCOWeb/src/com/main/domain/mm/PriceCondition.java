package com.main.domain.mm;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;
/**
 * 存放定价条件默认数据
 * @author Administrator
 *
 */
@Entity
@Table(name = "PRICE_CONDITION")
public class PriceCondition  extends UUIDEntity implements Serializable {
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
	
	/**
	 * 类型
	 */
	private String type;
	/**
	 * 描述
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
