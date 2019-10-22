package com.main.domain.sale;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.main.domain.cust.TerminalClient;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonDateSerializer;

@Entity
@Table(name = "SALE_FJ_HEADER")
public class SaleFjHeader extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7565656242572614807L;

	// 默认构造方法
	public SaleFjHeader() {
	}
	/**
	 * 订单头表
	 */
	private SaleHeader saleHeader;
	
	/**
	 * 投诉日期
	 */
	private Date txDate;

	/**
	 * 投诉内容
	 */
	private String content;
	/**
	 * 出错中心1
	 */
	private String cczx1;
	/**
	 * 出错中心2
	 */
	private String cczx2;
	/**
	 * 出错中心3
	 */
	private String cczx3;
	/**
	 * 出错中心4
	 */
	private String cczx4;
	/**
	 * 出错部门1
	 */
	private String ccbm1;
	/**
	 * 出错部门2
	 */
	private String ccbm2;
	/**
	 * 出错部门3
	 */
	private String ccbm3;
	/**
	 * 出错部门4
	 */
	private String ccbm4;

	/**
	 * 出错车间1
	 */
	private String cccj1;
	/**
	 * 出错车间2
	 */
	private String cccj2;
	/**
	 * 出错车间3
	 */
	private String cccj3;
	/**
	 * 出错车间4
	 */
	private String cccj4;

	/**
	 * 出错生产线1
	 */
	private String ccscx1;
	/**
	 * 出错生产线2
	 */
	private String ccscx2;
	/**
	 * 出错生产线3
	 */
	private String ccscx3;
	/**
	 * 出错生产线4
	 */
	private String ccscx4;

	/**
	 * 责任工序1
	 */
	private String zrgx1;
	/**
	 * 责任工序2
	 */
	private String zrgx2;
	/**
	 * 责任工序3
	 */
	private String zrgx3;
	/**
	 * 责任工序4
	 */
	private String zrgx4;

	/**
	 * 出错类别1
	 */
	private String cclb1;
	/**
	 * 出错类别2
	 */
	private String cclb2;
	/**
	 * 出错类别3
	 */
	private String cclb3;
	/**
	 * 出错类别4
	 */
	private String cclb4;

	@Column(name = "CONTENT")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "CCZX1")
	public String getCczx1() {
		return cczx1;
	}

	public void setCczx1(String cczx1) {
		this.cczx1 = cczx1;
	}

	@Column(name = "CCZX2")
	public String getCczx2() {
		return cczx2;
	}

	public void setCczx2(String cczx2) {
		this.cczx2 = cczx2;
	}

	@Column(name = "CCZX3")
	public String getCczx3() {
		return cczx3;
	}

	public void setCczx3(String cczx3) {
		this.cczx3 = cczx3;
	}

	@Column(name = "CCZX4")
	public String getCczx4() {
		return cczx4;
	}

	public void setCczx4(String cczx4) {
		this.cczx4 = cczx4;
	}

	@Column(name = "CCBM1")
	public String getCcbm1() {
		return ccbm1;
	}

	public void setCcbm1(String ccbm1) {
		this.ccbm1 = ccbm1;
	}

	@Column(name = "CCBM2")
	public String getCcbm2() {
		return ccbm2;
	}

	public void setCcbm2(String ccbm2) {
		this.ccbm2 = ccbm2;
	}

	@Column(name = "CCBM3")
	public String getCcbm3() {
		return ccbm3;
	}

	public void setCcbm3(String ccbm3) {
		this.ccbm3 = ccbm3;
	}

	@Column(name = "CCBM4")
	public String getCcbm4() {
		return ccbm4;
	}

	public void setCcbm4(String ccbm4) {
		this.ccbm4 = ccbm4;
	}

	@Column(name = "CCCJ1")
	public String getCccj1() {
		return cccj1;
	}

	public void setCccj1(String cccj1) {
		this.cccj1 = cccj1;
	}

	@Column(name = "CCCJ2")
	public String getCccj2() {
		return cccj2;
	}

	public void setCccj2(String cccj2) {
		this.cccj2 = cccj2;
	}

	@Column(name = "CCCJ3")
	public String getCccj3() {
		return cccj3;
	}

	public void setCccj3(String cccj3) {
		this.cccj3 = cccj3;
	}

	@Column(name = "CCCJ4")
	public String getCccj4() {
		return cccj4;
	}

	public void setCccj4(String cccj4) {
		this.cccj4 = cccj4;
	}

	@Column(name = "CCSCX1")
	public String getCcscx1() {
		return ccscx1;
	}

	public void setCcscx1(String ccscx1) {
		this.ccscx1 = ccscx1;
	}

	@Column(name = "CCSCX2")
	public String getCcscx2() {
		return ccscx2;
	}

	public void setCcscx2(String ccscx2) {
		this.ccscx2 = ccscx2;
	}

	@Column(name = "CCSCX3")
	public String getCcscx3() {
		return ccscx3;
	}

	public void setCcscx3(String ccscx3) {
		this.ccscx3 = ccscx3;
	}

	@Column(name = "CCSCX4")
	public String getCcscx4() {
		return ccscx4;
	}

	public void setCcscx4(String ccscx4) {
		this.ccscx4 = ccscx4;
	}

	@Column(name = "ZRGX1")
	public String getZrgx1() {
		return zrgx1;
	}

	public void setZrgx1(String zrgx1) {
		this.zrgx1 = zrgx1;
	}

	@Column(name = "ZRGX2")
	public String getZrgx2() {
		return zrgx2;
	}

	public void setZrgx2(String zrgx2) {
		this.zrgx2 = zrgx2;
	}

	@Column(name = "ZRGX3")
	public String getZrgx3() {
		return zrgx3;
	}

	public void setZrgx3(String zrgx3) {
		this.zrgx3 = zrgx3;
	}

	@Column(name = "ZRGX4")
	public String getZrgx4() {
		return zrgx4;
	}

	public void setZrgx4(String zrgx4) {
		this.zrgx4 = zrgx4;
	}

	@Column(name = "CCLB1")
	public String getCclb1() {
		return cclb1;
	}

	public void setCclb1(String cclb1) {
		this.cclb1 = cclb1;
	}

	@Column(name = "CCLB2")
	public String getCclb2() {
		return cclb2;
	}

	public void setCclb2(String cclb2) {
		this.cclb2 = cclb2;
	}

	@Column(name = "CCLB3")
	public String getCclb3() {
		return cclb3;
	}

	public void setCclb3(String cclb3) {
		this.cclb3 = cclb3;
	}

	@Column(name = "CCLB4")
	public String getCclb4() {
		return cclb4;
	}

	public void setCclb4(String cclb4) {
		this.cclb4 = cclb4;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = " saleId")
	public SaleHeader getSaleHeader() {
		return saleHeader;
	}

	public void setSaleHeader(SaleHeader saleHeader) {
		this.saleHeader = saleHeader;
	}

	@Column(name = "TX_DATE")
	public Date getTxDate() {
		return txDate;
	}

	public void setTxDate(Date txDate) {
		this.txDate = txDate;
	}

}