package com.main.domain.cust;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "CUST_CONTACTS")
public class CustContacts extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4182063059022875050L;

	// 默认构造方法
	public CustContacts() {
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
	private String telNumber;

	/**
	 * QQ
	 */
	private String qqNum;

	/**
	 * 称谓
	 */
	private String anred;
	/**
	 * 状态
	 */
	private String status;

	private CustHeader custHeader;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public CustHeader getCustHeader() {
		return custHeader;
	}

	public void setCustHeader(CustHeader custHeader) {
		this.custHeader = custHeader;
	}

	/**
	 * 联系人
	 * 
	 * @return
	 */
	@Column(name = "PARNR")
	public String getParnr() {
		return parnr;
	}

	/**
	 * 联系人
	 * 
	 * @param parnr
	 */
	public void setParnr(String parnr) {
		this.parnr = parnr;
	}

	/**
	 * 客户
	 * 
	 * @return
	 */
	@Column(name = "KUNNR")
	public String getKunnr() {
		return kunnr;
	}

	/**
	 * 客户
	 * 
	 * @param kunnr
	 */
	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}

	/**
	 * 名称
	 * 
	 * @return
	 */
	@Column(name = "NAME1")
	public String getName1() {
		return name1;
	}

	/**
	 * 名称
	 * 
	 * @param name1
	 */
	public void setName1(String name1) {
		this.name1 = name1;
	}

	/**
	 * 名
	 * 
	 * @return
	 */
	@Column(name = "NAMEV")
	public String getNamev() {
		return namev;
	}

	/**
	 * 名
	 * 
	 * @param namev
	 */
	public void setNamev(String namev) {
		this.namev = namev;
	}

	/**
	 * 部门
	 * 
	 * @return
	 */
	@Column(name = "ABTNR")
	public String getAbtnr() {
		return abtnr;
	}

	/**
	 * 部门
	 * 
	 * @param abtnr
	 */
	public void setAbtnr(String abtnr) {
		this.abtnr = abtnr;
	}

	/**
	 * 电话1
	 * 
	 * @return
	 */
	@Column(name = "TELF1")
	public String getTelNumber() {
		return telNumber;
	}

	/**
	 * 电话1
	 * 
	 * @param telf1
	 */
	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	/**
	 * 称谓
	 * 
	 * @return
	 */
	@Column(name = "ANRED")
	public String getAnred() {
		return anred;
	}

	/**
	 * 称谓
	 * 
	 * @param anred
	 */
	public void setAnred(String anred) {
		this.anred = anred;
	}

	@Column(name = "STATUS")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "VTEXT")
	public String getVtext() {
		return vtext;
	}

	public void setVtext(String vtext) {
		this.vtext = vtext;
	}

	/**
	 * QQ
	 */
	@Column(name = "QQ_NUM")
	public String getQqNum() {
		return qqNum;
	}

	public void setQqNum(String qqNum) {
		this.qqNum = qqNum;
	}

}