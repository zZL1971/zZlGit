package com.mw.framework.domain;

public class Cust {
	
	private String cust_id;
	private String name;
	private String sex;
	private String tel;
	private String address;
	private String jingShouRen;
	private String huXing;
	private String isYangBan;
	private String isAnZhuang;
	private String floor;
	private String orderPayFw;
	private String custRemarks;
	private String birthday;
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getJingShouRen() {
		return jingShouRen;
	}
	public void setJingShouRen(String jingShouRen) {
		this.jingShouRen = jingShouRen;
	}
	public String getHuXing() {
		return huXing;
	}
	public void setHuXing(String huXing) {
		this.huXing = huXing;
	}
	public String getIsYangBan() {
		return isYangBan;
	}
	public void setIsYangBan(String isYangBan) {
		this.isYangBan = isYangBan;
	}
	public String getIsAnZhuang() {
		return isAnZhuang;
	}
	public void setIsAnZhuang(String isAnZhuang) {
		this.isAnZhuang = isAnZhuang;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getOrderPayFw() {
		return orderPayFw;
	}
	public void setOrderPayFw(String orderPayFw) {
		this.orderPayFw = orderPayFw;
	}
	public String getCustRemarks() {
		return custRemarks;
	}
	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}
	@Override
	public String toString() {
		return "[cust_id=" + cust_id + ", name=" + name + ", sex=" + sex
				+ ", tel=" + tel + ", address=" + address + ", jingShouRen="
				+ jingShouRen + ", huXing=" + huXing + ", isYangBan="
				+ isYangBan + ", isAnZhuang=" + isAnZhuang + ", floor=" + floor
				+ ", orderPayFw=" + orderPayFw + ", custRemarks=" + custRemarks
				+ "]";
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getBirthday() {
		return birthday;
	}
	
}
