package com.main.domain.cust;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonDateSerializer;

@Entity
@Table(name = "CUST_EVENT")
public class CustEvent extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5179029398717528778L;
	private String kunnr;//客户编码
	private String name1;//客户名称
	private String zsMatnr;//赠送产品
	private Integer zsNum;//赠送数量
	private Double joinFee;//加盟费
	private String custLevel;//客户等级
	private Double openGift;//开业礼包
	private Double ypCashCoupon;//样品现金券
	private Double ypZheKou;//样品折扣
	private Double returnMoney;//返还金额
	private String isBegin17;//2017520客户权限
	private String isBegin18;//2018520客户权限
	@Column(name = "is_begin17",length = 40)
	public String getIsBegin17() {
		return isBegin17;
	}

	public void setIsBegin17(String isBegin17) {
		this.isBegin17 = isBegin17;
	}
	@Column(name = "is_begin18",length = 40)
	public String getIsBegin18() {
		return isBegin18;
	}

	public void setIsBegin18(String isBegin18) {
		this.isBegin18 = isBegin18;
	}

	@Column(name = "kunnr",length = 40)
	public String getKunnr() {
		return kunnr;
	}
	
	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}
	
	@Column(name = "name1",length = 40)
	public String getName1() {
		return name1;
	}
	
	public void setName1(String name1) {
		this.name1 = name1;
	}
	
	@Column(name = "zs_matnr",length = 250)
	public String getZsMatnr() {
		return zsMatnr;
	}
	
	public void setZsMatnr(String zsMatnr) {
		this.zsMatnr = zsMatnr;
	}
	
	@Column(name = "zs_num",length = 40)
	public Integer getZsNum() {
		return zsNum;
	}
	
	public void setZsNum(Integer zsNum) {
		this.zsNum = zsNum;
	}
	
	@Column(name = "join_fee",length = 180)
	public Double getJoinFee() {
		return joinFee;
	}
	
	public void setJoinFee(Double joinFee) {
		this.joinFee = joinFee;
	}
	
	@Column(name = "cust_level",length = 40)
	public String getCustLevel() {
		return custLevel;
	}
	
	public void setCustLevel(String custLevel) {
		this.custLevel = custLevel;
	}
	
	@Column(name = "open_gift",length = 180)
	public Double getOpenGift() {
		return openGift;
	}
	public void setOpenGift(Double openGift) {
		this.openGift = openGift;
	}
	
	@Column(name = "yp_cash_coupon",length = 180)
	public Double getYpCashCoupon() {
		return ypCashCoupon;
	}
	
	public void setYpCashCoupon(Double ypCashCoupon) {
		this.ypCashCoupon = ypCashCoupon;
	}
	
	@Column(name = "yp_zhe_kou",length = 180)
	public Double getYpZheKou() {
		return ypZheKou;
	}
	
	public void setYpZheKou(Double ypZheKou) {
		this.ypZheKou = ypZheKou;
	}
	
	@Column(name = "return_money",length = 180)
	public Double getReturnMoney() {
		return returnMoney;
	}
	
	public void setReturnMoney(Double returnMoney) {
		this.returnMoney = returnMoney;
	}
	


	public CustEvent(String kunnr, String name1, String zsMatnr, Integer zsNum,
			Double joinFee, String custLevel, Double openGift,
			Double ypCashCoupon, Double ypZheKou, Double returnMoney,
			String isBegin17, String isBegin18) {
		super();
		this.kunnr = kunnr;
		this.name1 = name1;
		this.zsMatnr = zsMatnr;
		this.zsNum = zsNum;
		this.joinFee = joinFee;
		this.custLevel = custLevel;
		this.openGift = openGift;
		this.ypCashCoupon = ypCashCoupon;
		this.ypZheKou = ypZheKou;
		this.returnMoney = returnMoney;
		this.isBegin17 = isBegin17;
		this.isBegin18 = isBegin18;
	}

	public CustEvent(String kunnr, String name1) {
		super();
		this.kunnr = kunnr;
		this.name1 = name1;
	}

	public CustEvent() {
		super();
	}
}