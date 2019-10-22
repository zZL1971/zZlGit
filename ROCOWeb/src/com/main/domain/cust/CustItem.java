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
@Table(name = "CUST_ITEM")
public class CustItem extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9028070759323399587L;

	// 默认构造方法
	public CustItem() {
	}

	private String kunnr;

	/**
	 * 开始时间
	 */
	private Date startDate;
	/**
	 * 结束时间
	 */
	private Date endDate;
	/**
	 * 折扣
	 */
	private Double zheKou;
	/**
	 * 待返点总金额
	 */
	private Double total;
	/**
	 * 预计剩余金额
	 */
	private Double shengYu;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 实际已返点金额
	 */
	private Double shiJi;
	/**
	 * 预计已返点金额
	 */
	private Double yuJi;
	/**
	 * 过期金额
	 */
	private Double guoQi;
	/**
	 * 返点名称
	 */
	private String fanDianName;

	private CustHeader custHeader;

	/**
	 * 开始时间
	 * 
	 * @return
	 */
	@Column(name = "START_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * 开始时间
	 * 
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * 结束时间
	 * 
	 * @return
	 */
	@Column(name = "END_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * 结束时间
	 * 
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 折扣
	 * 
	 * @return
	 */
	@Column(name = "ZHE_KOU")
	public Double getZheKou() {
		return zheKou;
	}

	/**
	 * 折扣
	 * 
	 * @param zheKou
	 */
	public void setZheKou(Double zheKou) {
		this.zheKou = zheKou;
	}

	/**
	 * 待返点总金额
	 * 
	 * @return
	 */
	@Column(name = "TOTAL")
	public Double getTotal() {
		return total;
	}

	/**
	 * 待返点总金额
	 * 
	 * @param total
	 */
	public void setTotal(Double total) {
		this.total = total;
	}

	/**
	 * 预计剩余金额
	 * 
	 * @return
	 */
	@Column(name = "SHENG_YU")
	public Double getShengYu() {
		return shengYu;
	}

	/**
	 * 预计剩余金额
	 * 
	 * @param shengYu
	 */
	public void setShengYu(Double shengYu) {
		this.shengYu = shengYu;
	}

	/**
	 * 状态
	 * 
	 * @return
	 */
	@Column(name = "STATUS")
	public String getStatus() {
		return status;
	}

	/**
	 * 状态
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public CustHeader getCustHeader() {
		return custHeader;
	}

	public void setCustHeader(CustHeader custHeader) {
		this.custHeader = custHeader;
	}

	@Column(name = "KUNNR")
	public String getKunnr() {
		return kunnr;
	}

	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}

	/**
	 * 实际已返点金额
	 * 
	 * @return
	 */
	@Column(name = "SHI_JI")
	public Double getShiJi() {
		return shiJi;
	}

	/**
	 * 实际已返点金额
	 * 
	 * @param shiJi
	 */
	public void setShiJi(Double shiJi) {
		this.shiJi = shiJi;
	}

	/**
	 * 预计已返点金额
	 * 
	 * @return
	 */
	@Column(name = "YU_JI")
	public Double getYuJi() {
		return yuJi;
	}

	/**
	 * 预计已返点金额
	 * 
	 * @param yuJi
	 */
	public void setYuJi(Double yuJi) {
		this.yuJi = yuJi;
	}

	/**
	 * 过期金额
	 * 
	 * @return
	 */
	@Column(name = "GUO_QI")
	public Double getGuoQi() {
		return guoQi;
	}

	/**
	 * 过期金额
	 * 
	 * @param guoQi
	 */
	public void setGuoQi(Double guoQi) {
		this.guoQi = guoQi;
	}

	/**
	 * 返点名称
	 * 
	 * @return
	 */
	@Column(name = "FAN_DIAN_NAME")
	public String getFanDianName() {
		return fanDianName;
	}

	/**
	 * 返点名称
	 * 
	 * @param fanDianName
	 */
	public void setFanDianName(String fanDianName) {
		this.fanDianName = fanDianName;
	}

}