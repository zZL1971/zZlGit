package com.main.model.cust;

import com.mw.framework.model.BasicModel;

public class SalePriceProlicyModel extends BasicModel{

	private String kunnr;
	private String name1;
	private Double discount;
	private String isable;
	private String saleorder;
	private String starttime;
	private String endtime;
	private String rebatype;
	// 默认构造方法
	public SalePriceProlicyModel() {
	}

	public String getRebatype() {
		return rebatype;
	}

	public void setRebatype(String rebatype) {
		this.rebatype = rebatype;
	}

	public String getKunnr() {
		return kunnr;
	}
	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public String getIsable() {
		return isable;
	}
	public void setIsable(String isable) {
		this.isable = isable;
	}
	public String getSaleorder() {
		return saleorder;
	}
	public void setSaleorder(String saleorder) {
		this.saleorder = saleorder;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

}