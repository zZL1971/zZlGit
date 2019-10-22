package com.main.model.cust;

import com.mw.framework.model.BasicModel;

public class CustContactsModel extends BasicModel{

	// 默认构造方法
	public CustContactsModel() {
	}

	/**
	 * 联系人
	 */
	private String parnr;
	/**
	 * 客户
	 */
	private String kunnr;
	/**
	 * 名称
	 */
	private String name1;
	/**
	 * 名
	 */
	private String namev;
	/**
	 * 部门
	 */
	private String abtnr;
	/**
	 * 部门描述
	 */
	private String vtext;
	/**
	 * 电话1
	 */
	private String telf1;
	/**
	 * 称谓
	 */
	private String anred;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * QQ
	 */
	private String qqNum;
	
	public String getParnr() {
		return parnr;
	}
	public void setParnr(String parnr) {
		this.parnr = parnr;
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
	public String getNamev() {
		return namev;
	}
	public void setNamev(String namev) {
		this.namev = namev;
	}
	public String getAbtnr() {
		return abtnr;
	}
	public void setAbtnr(String abtnr) {
		this.abtnr = abtnr;
	}
	public String getVtext() {
		return vtext;
	}
	public void setVtext(String vtext) {
		this.vtext = vtext;
	}
	public String getTelf1() {
		return telf1;
	}
	public void setTelf1(String telf1) {
		this.telf1 = telf1;
	}
	public String getAnred() {
		return anred;
	}
	public void setAnred(String anred) {
		this.anred = anred;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getQqNum() {
		return qqNum;
		}
	public void setQqNum(String qqNum) {
		this.qqNum = qqNum;
		}
	
}