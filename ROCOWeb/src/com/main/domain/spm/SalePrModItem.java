/**
 *
 */
package com.main.domain.spm;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 销售订单-价格更改明细
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.main.domain.spm.SalePrModHeader.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2016-4-19
 * 
 */
@Entity
@Table(name = "SALE_PR_MOD_ITEM")
public class SalePrModItem extends UUIDEntity implements Serializable {

	private String posnr;// 行项目
	private String mabnr;// 物料编码
	private Integer kwmeng;// 订单数量
	private String arktx;// 物料描述
	private String abgru;// 拒绝原因
	private String stateAudit;// 是否取消
	private Double pr00;// 付款金额(计算后的金额)
	private Double pr01;// 商品原价(含税)
	private Double pr02;// 赠送（活动）
	private Double pr03;// 产品折扣
	private Double pr04;// 活动折扣
	private Double pr05;// 产品免费（统计用）
	private Double zr01;// 运输费(含税)
	private Double zr02;// 返修费(含税)
	private Double zr03;// 安装服务费(含税)
	private Double zr04;// 设计费(含税)
	private Double zr05;// 订单变更管理费(含税)
	private Double zr06;// 客服支持
	private Double zr07;// 订单价格调整(含税)
	private String old;// 如果是原来的就为“1”，空就是改价格后的
	private SalePrModHeader salePrModHeader;// 销售订单-价格更改抬头

	public SalePrModItem() {
		super();
	}

	public SalePrModItem(String posnr, String mabnr, Integer kwmeng,
			String arktx, String abgru, String stateAudit, Double pr00,
			Double pr01, Double pr02, Double pr03, Double pr04, Double pr05,
			Double zr01, Double zr02, Double zr03, Double zr04, Double zr05,
			Double zr06, Double zr07, String old) {
		super();
		this.posnr = posnr;
		this.mabnr = mabnr;
		this.kwmeng = kwmeng;
		this.arktx = arktx;
		this.abgru = abgru;
		this.stateAudit = stateAudit;
		this.pr00 = pr00;
		this.pr01 = pr01;
		this.pr02 = pr02;
		this.pr03 = pr03;
		this.pr04 = pr04;
		this.pr05 = pr05;
		this.zr01 = zr01;
		this.zr02 = zr02;
		this.zr03 = zr03;
		this.zr04 = zr04;
		this.zr05 = zr05;
		this.zr06 = zr06;
		this.setZr07(zr07);
		this.old = old;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public SalePrModHeader getSalePrModHeader() {
		return salePrModHeader;
	}

	public void setSalePrModHeader(SalePrModHeader salePrModHeader) {
		this.salePrModHeader = salePrModHeader;
	}

	public String getPosnr() {
		return posnr;
	}

	public void setPosnr(String posnr) {
		this.posnr = posnr;
	}

	public String getMabnr() {
		return mabnr;
	}

	public void setMabnr(String mabnr) {
		this.mabnr = mabnr;
	}

	public Integer getKwmeng() {
		return kwmeng;
	}

	public void setKwmeng(Integer kwmeng) {
		this.kwmeng = kwmeng;
	}

	public String getArktx() {
		return arktx;
	}

	public void setArktx(String arktx) {
		this.arktx = arktx;
	}

	public String getAbgru() {
		return abgru;
	}

	public void setAbgru(String abgru) {
		this.abgru = abgru;
	}

	public String getStateAudit() {
		return stateAudit;
	}

	public void setStateAudit(String stateAudit) {
		this.stateAudit = stateAudit;
	}

	public Double getPr00() {
		return pr00;
	}

	public void setPr00(Double pr00) {
		this.pr00 = pr00;
	}

	public Double getPr01() {
		return pr01;
	}

	public void setPr01(Double pr01) {
		this.pr01 = pr01;
	}

	public Double getPr02() {
		return pr02;
	}

	public void setPr02(Double pr02) {
		this.pr02 = pr02;
	}

	public Double getPr03() {
		return pr03;
	}

	public void setPr03(Double pr03) {
		this.pr03 = pr03;
	}

	public Double getPr04() {
		return pr04;
	}

	public void setPr04(Double pr04) {
		this.pr04 = pr04;
	}

	public Double getPr05() {
		return pr05;
	}

	public void setPr05(Double pr05) {
		this.pr05 = pr05;
	}

	public Double getZr01() {
		return zr01;
	}

	public void setZr01(Double zr01) {
		this.zr01 = zr01;
	}

	public Double getZr02() {
		return zr02;
	}

	public void setZr02(Double zr02) {
		this.zr02 = zr02;
	}

	public Double getZr03() {
		return zr03;
	}

	public void setZr03(Double zr03) {
		this.zr03 = zr03;
	}

	public Double getZr04() {
		return zr04;
	}

	public void setZr04(Double zr04) {
		this.zr04 = zr04;
	}

	public Double getZr05() {
		return zr05;
	}

	public void setZr05(Double zr05) {
		this.zr05 = zr05;
	}

	public Double getZr06() {
		return zr06;
	}

	public void setZr06(Double zr06) {
		this.zr06 = zr06;
	}

	public Double getZr07() {
		return zr07;
	}

	public void setZr07(Double zr07) {
		this.zr07 = zr07;
	}

	public String getOld() {
		return old;
	}

	public void setOld(String old) {
		this.old = old;
	}

	@Override
	public String toString() {
		return "SalePrModItem [posnr=" + posnr + ", mabnr=" + mabnr
				+ ", kwmeng=" + kwmeng + ", arktx=" + arktx + ", abgru="
				+ abgru + ", stateAudit=" + stateAudit + ", pr00=" + pr00
				+ ", pr01=" + pr01 + ", pr02=" + pr02 + ", pr03=" + pr03
				+ ", pr04=" + pr04 + ", pr05=" + pr05 + ", zr01=" + zr01
				+ ", zr02=" + zr02 + ", zr03=" + zr03 + ", zr04=" + zr04
				+ ", zr05=" + zr05 + ", zr06=" + zr06+ " old=" + old + "]";
	}

}
