/**
 *
 */
package com.main.model.spm;

import java.util.HashSet;
import java.util.Set;

/**
 * 销售订单-价格更改抬头model
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.main.domain.spm.SalePrModHeader.java
 * @Version 1.0.0
 * @author lkl
 * @time 2016-4-19
 * 
 */
public class SalePrModHeaderModel{

	private String id;
	private String vbeln;// SAP编号
	private String bstkd;//订单编号
	private String auart;//订单类型
	private String bstdk;//订单日期
	private String kunnr;//售达方
	private String name1;//售达方名称
	private Double netwr;//订单总金额
	private Double oNetwr;//旧订单总金额
	private String zterm;//付款条件
	private Double fuFuanMoney;//付款金额
	private Double oFuFuanMoney;//原来的付款金额
	private String zzysfg;//支付方式
	private String zzname;//客户名称
	private String zzphon;//联系方式
	private String abgru;//拒绝原因
	private String tranState;//修改方
	private String vgbel;//借贷项编号
	private String serialNumber;//流水号
	private Double loanAmount;//借贷金额
	private String createUser;
	private String createTime;

	private Set<SalePrModItemModel> itemModels= new HashSet<SalePrModItemModel>();// 行项目

	public SalePrModHeaderModel() {
		super();
	}

	public SalePrModHeaderModel(String id, String vbeln, String bstkd,
			String auart, String bstdk, String kunnr, String name1, Double netwr,
			Double oNetwr, String zterm, Double fuFuanMoney,
			Double oFuFuanMoney, String zzysfg, String zzname, String zzphon,
			Set<SalePrModItemModel> itemModels) {
		super();
		this.id = id;
		this.vbeln = vbeln;
		this.bstkd = bstkd;
		this.auart = auart;
		this.bstdk = bstdk;
		this.kunnr = kunnr;
		this.name1 = name1;
		this.netwr = netwr;
		this.oNetwr = oNetwr;
		this.zterm = zterm;
		this.fuFuanMoney = fuFuanMoney;
		this.oFuFuanMoney = oFuFuanMoney;
		this.zzysfg = zzysfg;
		this.zzname = zzname;
		this.zzphon = zzphon;
		this.itemModels = itemModels;
	}
	
	public SalePrModHeaderModel(String id, String vbeln, String bstkd,
			String auart, String bstdk, String kunnr, String name1, Double netwr,
			Double oNetwr, String zterm, Double fuFuanMoney,
			Double oFuFuanMoney, String zzysfg, String zzname, String zzphon,
			String abgru) {
		super();
		this.id = id;
		this.vbeln = vbeln;
		this.bstkd = bstkd;
		this.auart = auart;
		this.bstdk = bstdk;
		this.kunnr = kunnr;
		this.name1 = name1;
		this.netwr = netwr;
		this.oNetwr = oNetwr;
		this.zterm = zterm;
		this.fuFuanMoney = fuFuanMoney;
		this.oFuFuanMoney = oFuFuanMoney;
		this.zzysfg = zzysfg;
		this.zzname = zzname;
		this.zzphon = zzphon;
		this.abgru = abgru;
	}

	public SalePrModHeaderModel(String id, String vbeln, String bstkd,
			String auart, String bstdk, String kunnr, String name1,
			Double netwr, Double oNetwr, String zterm, Double fuFuanMoney,
			Double oFuFuanMoney, String zzysfg, String zzname, String zzphon,
			String abgru, String tranState, String serialNumber,
			Double loanAmount, String createUser, String createTime) {
		super();
		this.id = id;
		this.vbeln = vbeln;
		this.bstkd = bstkd;
		this.auart = auart;
		this.bstdk = bstdk;
		this.kunnr = kunnr;
		this.name1 = name1;
		this.netwr = netwr;
		this.oNetwr = oNetwr;
		this.zterm = zterm;
		this.fuFuanMoney = fuFuanMoney;
		this.oFuFuanMoney = oFuFuanMoney;
		this.zzysfg = zzysfg;
		this.zzname = zzname;
		this.zzphon = zzphon;
		this.abgru = abgru;
		this.tranState = tranState;
		this.serialNumber = serialNumber;
		this.loanAmount = loanAmount;
		this.createUser = createUser;
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVbeln() {
		return vbeln;
	}

	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}
	
	

	public String getBstkd() {
		return bstkd;
	}

	public void setBstkd(String bstkd) {
		this.bstkd = bstkd;
	}

	public String getAuart() {
		return auart;
	}

	public void setAuart(String auart) {
		this.auart = auart;
	}

	public String getBstdk() {
		return bstdk;
	}

	public void setBstdk(String bstdk) {
		this.bstdk = bstdk;
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

	public Double getNetwr() {
		return netwr;
	}

	public void setNetwr(Double netwr) {
		this.netwr = netwr;
	}
	
	public Double getoNetwr() {
		return oNetwr;
	}

	public void setoNetwr(Double oNetwr) {
		this.oNetwr = oNetwr;
	}

	public String getZterm() {
		return zterm;
	}

	public void setZterm(String zterm) {
		this.zterm = zterm;
	}

	public Double getFuFuanMoney() {
		return fuFuanMoney;
	}

	public void setFuFuanMoney(Double fuFuanMoney) {
		this.fuFuanMoney = fuFuanMoney;
	}

	public Double getoFuFuanMoney() {
		return oFuFuanMoney;
	}

	public void setoFuFuanMoney(Double oFuFuanMoney) {
		this.oFuFuanMoney = oFuFuanMoney;
	}

	public String getZzysfg() {
		return zzysfg;
	}

	public void setZzysfg(String zzysfg) {
		this.zzysfg = zzysfg;
	}

	public String getZzname() {
		return zzname;
	}

	public void setZzname(String zzname) {
		this.zzname = zzname;
	}

	public String getZzphon() {
		return zzphon;
	}

	public void setZzphon(String zzphon) {
		this.zzphon = zzphon;
	}
	
	public Set<SalePrModItemModel> getItemModels() {
		return itemModels;
	}
	
	public void setItemModels(Set<SalePrModItemModel> itemModels) {
		this.itemModels = itemModels;
	}
	
	public String getAbgru() {
		return abgru;
	}

	public void setAbgru(String abgru) {
		this.abgru = abgru;
	}
	
	public String getTranState() {
		return tranState;
	}

	public void setTranState(String tranState) {
		this.tranState = tranState;
	}
	
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}
	
	public String getVgbel() {
		return vgbel;
	}

	public void setVgbel(String vgbel) {
		this.vgbel = vgbel;
	}

	@Override
	public String toString() {
		return "SalePrModHeaderModel [id=" + id + ", vbeln=" + vbeln
				+ ", bstkd=" + bstkd + ", auart=" + auart + ", bstdk=" + bstdk
				+ ", kunnr=" + kunnr + ", name1=" + name1 + ", netwr=" + netwr
				+ ", oNetwr=" + oNetwr + ", zterm=" + zterm + ", fuFuanMoney="
				+ fuFuanMoney + ", oFuFuanMoney=" + oFuFuanMoney + ", zzysfg="
				+ zzysfg + ", zzname=" + zzname + ", zzphon=" + zzphon
				+ ", abgru=" + abgru + "]";
	}
	
}
