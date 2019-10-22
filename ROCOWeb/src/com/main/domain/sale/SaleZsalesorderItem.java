package com.main.domain.sale;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * @author no value version: 1.00 Date: 20101108 14:49 Last modify Date:
 *         20101108 14:49 CopyRight (c) spro Company 2006 All rights reserved
 *         说明: 销售订单行项数据<br>
 *         变更:<br>
 */
@Entity
@Table(name = "SALE_ZSALESORDER_ITEM")
// 默认构造方法
public class SaleZsalesorderItem extends UUIDEntity implements Serializable {
	public SaleZsalesorderItem() {

	}

	/**
	 * 销售凭证
	 */
	private String vbeln;
	/**
	 * 销售凭证项目
	 */
	private Long posnr;
	/**
	 * 物料单结构中的上层项目
	 */
	private Long uepos;
	/**
	 * 商品
	 */
	private String matnr;
	/**
	 * 已输入物料
	 */
	private String matwa;
	/**
	 * 定价参考物料
	 */
	private String pmatn;
	/**
	 * 物料组
	 */
	private String matkl;
	/**
	 * 销售订单项目短文本
	 */
	private String arktx;
	/**
	 * 销售凭证项目类别
	 */
	private String pstyv;
	/**
	 * 项目类型
	 */
	private String posar;
	/**
	 * 报价和销售订单的拒绝原因
	 */
	private String abgru;
	/**
	 * 描述
	 */
	private String bezei;
	/**
	 * 以销售单位表示的累计订购数量
	 */
	private Double kwmeng;
	/**
	 * 项目与交货有关
	 */
	private String lfrel;
	/**
	 * 计划行日期
	 */
	private Date edatu;
	/**
	 * 累计需求交货数量
	 */
	private Double lsmeng;
	/**
	 * 未清数量
	 */
	private Double obmng;
	/**
	 * 销售单位
	 */
	private String vrkme;
	/**
	 * 工厂(自有或外部)
	 */
	private String werks;
	/**
	 * 库存地点
	 */
	private String lgort;
	/**
	 * 装运点/接收点
	 */
	private String vstel;
	/**
	 * 利润中心
	 */
	private String prctr;
	/**
	 * 净价
	 */
	private Double netpr;
	/**
	 * 产品组
	 */
	private String spart;
	/**
	 * 名称
	 */
	private String vtext;
	/**
	 * 行项目文本，仅仅是为创建销售单的时候传值用。不映射到数据库
	 */
	private String sproSoItemText;
	/**
	 * 参考单据的单据编号 10 采用复制创建,将复制单号给此
	 */
	private String vgbel;
	/**
	 * 参考项目的项目号 采用复制创建,将复制单号的行项给此
	 */
	private Long vgpos;

	private String sproGridItemPartner;
	/**
	 * 目标数量
	 */
	private Double zmeng;

	/**
	 * 导购员
	 */
	private String sproDgy;

	/**
	 * 导购员名称
	 */
	private String sproDgyName;
	private String sproItemHouXu;

	private Double sproNet;
	private Double mwsbp;
	private Double netwr;
	private Double kzwi6;

	/**
	 * 投诉类别
	 */
	private String prsObjnr;

	/**
	 * 电子商务sku编号
	 */
	private String tas;

	/**
	 * 电子商务sku数量
	 */
	private Double kmpmg;

	/**
	 * 电子商务sku描述
	 */
	private String kannr;

	@Column(name = "MWSBP")
	public Double getMwsbp() {
		return mwsbp;
	}

	public void setMwsbp(Double mwsbp) {
		this.mwsbp = mwsbp;
	}

	@Column(name = "NETWR")
	public Double getNetwr() {
		return netwr;
	}

	public void setNetwr(Double netwr) {
		this.netwr = netwr;
	}

	/**
	 * 销售凭证
	 */
	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}

	/**
	 * 销售凭证
	 */
	@Column(name = "VBELN")
	public String getVbeln() {
		return vbeln;
	}

	/**
	 * 销售凭证项目
	 */
	public void setPosnr(Long posnr) {
		this.posnr = posnr;
	}

	/**
	 * 销售凭证项目
	 */
	@Column(name = "POSNR")
	public Long getPosnr() {
		return posnr;
	}

	/**
	 * 物料单结构中的上层项目
	 */
	public void setUepos(Long uepos) {
		this.uepos = uepos;
	}

	/**
	 * 物料单结构中的上层项目
	 */
	@Column(name = "UEPOS")
	public Long getUepos() {
		return uepos;
	}

	/**
	 * 商品
	 */
	public void setMatnr(String matnr) {
		this.matnr = StringUtils.upperCase(matnr);
	}

	/**
	 * 商品
	 */
	@Column(name = "MATNR")
	public String getMatnr() {
		return matnr;
	}

	/**
	 * 已输入物料
	 */
	public void setMatwa(String matwa) {
		this.matwa = StringUtils.upperCase(matwa);
	}

	/**
	 * 已输入物料
	 */
	@Column(name = "MATWA")
	public String getMatwa() {
		return matwa;
	}

	/**
	 * 定价参考物料
	 */
	public void setPmatn(String pmatn) {
		this.pmatn = pmatn;
	}

	/**
	 * 定价参考物料
	 */
	@Column(name = "PMATN")
	public String getPmatn() {
		return pmatn;
	}

	/**
	 * 物料组
	 */
	public void setMatkl(String matkl) {
		this.matkl = StringUtils.upperCase(matkl);
	}

	/**
	 * 物料组
	 */
	@Column(name = "MATKL")
	public String getMatkl() {
		return matkl;
	}

	/**
	 * 销售订单项目短文本
	 */
	public void setArktx(String arktx) {
		this.arktx = arktx;
	}

	/**
	 * 销售订单项目短文本
	 */
	@Column(name = "ARKTX")
	public String getArktx() {
		return arktx;
	}

	/**
	 * 销售凭证项目类别
	 */
	public void setPstyv(String pstyv) {
		this.pstyv = StringUtils.upperCase(pstyv);
	}

	/**
	 * 销售凭证项目类别
	 */
	@Column(name = "PSTYV")
	public String getPstyv() {
		return pstyv;
	}

	/**
	 * 项目类型
	 */
	public void setPosar(String posar) {
		this.posar = StringUtils.upperCase(posar);
	}

	/**
	 * 项目类型
	 */
	@Column(name = "POSAR")
	public String getPosar() {
		return posar;
	}

	/**
	 * 报价和销售订单的拒绝原因
	 */
	public void setAbgru(String abgru) {
		this.abgru = StringUtils.upperCase(abgru);
	}

	/**
	 * 报价和销售订单的拒绝原因
	 */
	@Column(name = "ABGRU")
	public String getAbgru() {
		return abgru;
	}

	/**
	 * 描述
	 */
	public void setBezei(String bezei) {
		this.bezei = bezei;
	}

	/**
	 * 描述
	 */
	@Column(name = "BEZEI")
	public String getBezei() {
		return bezei;
	}

	/**
	 * 以销售单位表示的累计订购数量
	 */
	public void setKwmeng(Double kwmeng) {
		this.kwmeng = kwmeng;
	}

	/**
	 * 以销售单位表示的累计订购数量
	 */
	@Column(name = "KWMENG")
	public Double getKwmeng() {
		return kwmeng;
	}

	/**
	 * 项目与交货有关
	 */
	public void setLfrel(String lfrel) {
		this.lfrel = lfrel;
	}

	/**
	 * 项目与交货有关
	 */
	@Column(name = "LFREL")
	public String getLfrel() {
		return lfrel;
	}

	/**
	 * 计划行日期
	 */
	public void setEdatu(Date edatu) {
		this.edatu = edatu;
	}

	/**
	 * 计划行日期
	 */
	@Column(name = "EDATU")
	public Date getEdatu() {
		return edatu;
	}

	/**
	 * 累计需求交货数量
	 */
	public void setLsmeng(Double lsmeng) {
		this.lsmeng = lsmeng;
	}

	/**
	 * 累计需求交货数量
	 */
	@Column(name = "LSMENG")
	public Double getLsmeng() {
		return lsmeng;
	}

	/**
	 * 未清数量
	 */
	public void setObmng(Double obmng) {
		this.obmng = obmng;
	}

	/**
	 * 未清数量
	 */
	@Column(name = "OBMNG")
	public Double getObmng() {
		return obmng;
	}

	/**
	 * 销售单位
	 */
	public void setVrkme(String vrkme) {
		this.vrkme = vrkme;
	}

	/**
	 * 销售单位
	 */
	@Column(name = "VRKME")
	public String getVrkme() {
		return vrkme;
	}

	/**
	 * 工厂(自有或外部)
	 */
	public void setWerks(String werks) {
		this.werks = StringUtils.upperCase(werks);
	}

	/**
	 * 工厂(自有或外部)
	 */
	@Column(name = "WERKS")
	public String getWerks() {
		return werks;
	}

	/**
	 * 库存地点
	 */
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	/**
	 * 库存地点
	 */
	@Column(name = "LGORT")
	public String getLgort() {
		return lgort;
	}

	/**
	 * 装运点/接收点
	 */
	public void setVstel(String vstel) {
		this.vstel = vstel;
	}

	/**
	 * 装运点/接收点
	 */
	@Column(name = "VSTEL")
	public String getVstel() {
		return vstel;
	}

	/**
	 * 利润中心
	 */
	public void setPrctr(String prctr) {
		this.prctr = prctr;
	}

	/**
	 * 利润中心
	 */
	@Column(name = "PRCTR")
	public String getPrctr() {
		return prctr;
	}

	/**
	 * 净价
	 */
	public void setNetpr(Double netpr) {
		this.netpr = netpr;
	}

	/**
	 * 净价
	 */
	@Column(name = "NETPR")
	public Double getNetpr() {
		return netpr;
	}

	/**
	 * 产品组
	 */
	public void setSpart(String spart) {
		this.spart = spart;
	}

	/**
	 * 产品组
	 */
	@Column(name = "SPART")
	public String getSpart() {
		return spart;
	}

	/**
	 * 名称
	 */
	public void setVtext(String vtext) {
		this.vtext = vtext;
	}

	/**
	 * 名称
	 */
	@Column(name = "VTEXT")
	public String getVtext() {
		return vtext;
	}

	@Column(name = "VGBEL")
	public String getVgbel() {
		return vgbel;
	}

	public void setVgbel(String vgbel) {
		// this.vgbel = StringUtils.killPerZero(vgbel);
		this.vgbel = vgbel;
	}

	@Column(name = "VGPOS")
	public Long getVgpos() {
		return vgpos;
	}

	public void setVgpos(Long vgpos) {
		this.vgpos = vgpos;
	}

	@Transient
	public String getSproGridItemPartner() {
		return sproGridItemPartner;
	}

	public void setSproGridItemPartner(String sproGridItemPartner) {
		this.sproGridItemPartner = sproGridItemPartner;
	}

	@Column(name = "ZMENG")
	public Double getZmeng() {
		return zmeng;
	}

	public void setZmeng(Double zmeng) {
		this.zmeng = zmeng;
	}

	@Transient
	public String getSproDgy() {
		return sproDgy;
	}

	public void setSproDgy(String sproDgy) {
		this.sproDgy = sproDgy;
	}

	@Transient
	public String getSproDgyName() {
		return sproDgyName;
	}

	public void setSproDgyName(String sproDgyName) {
		this.sproDgyName = sproDgyName;
	}

	@Transient
	public String getSproItemHouXu() {
		return sproItemHouXu;
	}

	public void setSproItemHouXu(String sproItemHouXu) {
		this.sproItemHouXu = sproItemHouXu;
	}

	@Column(name = "SPRO_NET")
	public Double getSproNet() {
		return sproNet;
	}

	public void setSproNet(Double sproNet) {
		this.sproNet = sproNet;
	}

	/**
	 * 
	 * @return
	 */
	@Transient
	public String getSproSoItemText() {
		return sproSoItemText;
	}

	public void setSproSoItemText(String sproSoItemText) {
		this.sproSoItemText = sproSoItemText;
	}

	@Column(name = "KZWI6")
	public Double getKzwi6() {
		return kzwi6;
	}

	public void setKzwi6(Double kzwi6) {
		this.kzwi6 = kzwi6;
	}

	/**
	 * 投诉类别
	 * 
	 * @return
	 */
	@Column(name = "PRS_OBJNR")
	public String getPrsObjnr() {
		return prsObjnr;
	}

	/**
	 * 投诉类别
	 * 
	 * @param prsObjnr
	 */
	public void setPrsObjnr(String prsObjnr) {
		this.prsObjnr = prsObjnr;
	}

	/**
	 * 电子商务sku编号
	 */
	@Column(name = "TAS")
	public String getTas() {
		return tas;
	}

	/**
	 * 电子商务sku编号
	 */
	public void setTas(String tas) {
		this.tas = tas;
	}

	/**
	 * 电子商务sku数量
	 */
	@Column(name = "KMPMG")
	public Double getKmpmg() {
		return kmpmg;
	}

	/**
	 * 电子商务sku数量
	 */
	public void setKmpmg(Double kmpmg) {
		this.kmpmg = kmpmg;
	}

	/**
	 * 电子商务sku描述
	 */
	@Column(name = "KANNR")
	public String getKannr() {
		return kannr;
	}

	/**
	 * 电子商务sku描述
	 */
	public void setKannr(String kannr) {
		this.kannr = kannr;
	}

}