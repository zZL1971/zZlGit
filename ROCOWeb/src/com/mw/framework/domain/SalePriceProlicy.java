package com.mw.framework.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.utils.DateTools;

import freemarker.template.utility.DateUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "SALE_PRICE_PROLICY")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SalePriceProlicy extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7399614095285090778L;


	private String kunnr;
	private String name1;
	private Double discount;//
	private String isable;
	private String saleOrder;
	private Date startTime;
	private Date endTime;
	private String discountStyle;//折扣名称
	private String discountStyle2;//折扣类型
	

	public SalePriceProlicy(String kunnr, String name1, Double discount,
			String isable, String saleOrder, Date starttime, Date endtime,
			String discountstype,String discountstype2) {
		super();
		this.kunnr = kunnr;
		this.name1 = name1;
		this.discount = discount;
		this.isable = isable;
		this.saleOrder = saleOrder;
		this.startTime = starttime;
		this.endTime = endtime;
		this.discountStyle = discountstype;
		this.discountStyle2 = discountstype2;
	}
	
	@Column(name="DISCOUNT_STYLE")
	public String getDiscountStyle() {
		return discountStyle;
	}

	public void setDiscountStyle(String discountStyle) {
		this.discountStyle = discountStyle;
	}
	
	@Column(name="DISCOUNT_STYLE2")
	public String getDiscountStyle2() {
		return discountStyle2;
	}

	public void setDiscountStyle2(String discountStyle2) {
		this.discountStyle2 = discountStyle2;
	}

    @Column(name="KUNNR")
	public String getKunnr() {
		return kunnr;
	}



	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}


    @Column(name="NAME1")
	public String getName1() {
		return name1;
	}



	public void setName1(String name1) {
		this.name1 = name1;
	}


	@Column(name = "DISCOUNT")
	public Double getDiscount() {
		return discount;
	}



	public void setDiscount(Double discount) {
		this.discount = discount;
	}


	@Column(name = "ISABLE")
	public String getIsable() {
		return isable;
	}



	public void setIsable(String isable) {
		this.isable = isable;
	}


	@Column(name = "SALE_ORDER")
	public String getSaleOrder() {
		return saleOrder;
	}



	public void setSaleOrder(String saleOrder) {
		this.saleOrder = saleOrder;
	}


	@Column(name = "START_TIME")
	public Date getStarttime() {
		return startTime;
	}



	public void setStarttime(Date starttime) {
		this.startTime = starttime;
	}


	@Column(name = "END_TIME")
	public Date getEndtime() {
		return endTime;
	}



	public void setEndtime(Date endtime) {
		this.endTime = endtime;
	}


	public SalePriceProlicy(){
		super();
	}
	
}
