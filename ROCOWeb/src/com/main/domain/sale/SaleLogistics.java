package com.main.domain.sale;

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

/**
 * @TODO 订单物流信息
 * @author Chaly
 *
 */
@Entity
@Table(name="SALE_LOGISTICS")
public class SaleLogistics extends UUIDEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 258557591722789832L;

	private String deliveryDay;
	
	private String zzjqlb;
	
	private String saleFor;
	
	private String kunnrS;
	
	private String deliveryIdentify;
	
	private Date ppcDate;
	
	private Date psDate;
	
	private Date pcDate;
	
	private Date pbDate;
	
	private Date poDate;
	
	private SaleHeader saleHeader;

	private String sapCode;
	/**
	 * 交期天数
	 * @return
	 */
	@Column(name="DELIVERY_DAY",length=10)
	public String getDeliveryDay() {
		return deliveryDay;
	}

	public void setDeliveryDay(String deliveryDay) {
		this.deliveryDay = deliveryDay;
	}

	/**
	 * 交期类别
	 * @return
	 */
	@Column(name="ZZJQLB",length=5)
	public String getZzjqlb() {
		return zzjqlb;
	}

	public void setZzjqlb(String zzjqlb) {
		this.zzjqlb = zzjqlb;
	}

	/**
	 * 产品组
	 * @return
	 */
	@Column(name="SALE_FOR",length=5)
	public String getSaleFor() {
		return saleFor;
	}

	public void setSaleFor(String saleFor) {
		this.saleFor = saleFor;
	}

	/**
	 * 物流 送达方
	 * @return
	 */
	@Column(name="KUNNR_S",length=20)
	public String getKunnrS() {
		return kunnrS;
	}

	public void setKunnrS(String kunnrS) {
		this.kunnrS = kunnrS;
	}

	/**
	 * 内外交期标识
	 * @return
	 */
	@Column(name="DELIVERY_IDENTIFY",length=5)
	public String getDeliveryIdentify() {
		return deliveryIdentify;
	}

	public void setDeliveryIdentify(String deliveryIdentify) {
		this.deliveryIdentify = deliveryIdentify;
	}

	/**
	 * 生产预完工日期
	 * @return
	 */
	@Column(name="PPC_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getPpcDate() {
		return ppcDate;
	}

	public void setPpcDate(Date ppcDate) {
		this.ppcDate = ppcDate;
	}

	/**
	 * 预计出货日期
	 * @return
	 */
	@Column(name="PS_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getPsDate() {
		return psDate;
	}

	public void setPsDate(Date psDate) {
		this.psDate = psDate;
	}

	/**
	 * 计划完工日期
	 * @return
	 */
	@Column(name="PC_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getPcDate() {
		return pcDate;
	}

	public void setPcDate(Date pcDate) {
		this.pcDate = pcDate;
	}

	/**
	 * 实际入库日期
	 * @return
	 */
	@Column(name="PB_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getPbDate() {
		return pbDate;
	}

	public void setPbDate(Date pbDate) {
		this.pbDate = pbDate;
	}

	/**
	 * 实际出库日期 
	 * @return
	 */
	@Column(name="PO_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getPoDate() {
		return poDate;
	}

	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}

	@ManyToOne(fetch=FetchType.LAZY)//使用懒加载
	@JoinColumn(name="PID",referencedColumnName="ID")
	public SaleHeader getSaleHeader() {
		return saleHeader;
	}

	public void setSaleHeader(SaleHeader saleHeader) {
		this.saleHeader = saleHeader;
	}

	@Column(name="SAP_CODE",length=20)
	public String getSapCode() {
		return sapCode;
	}

	public void setSapCode(String sapCode) {
		this.sapCode = sapCode;
	}
	
}
