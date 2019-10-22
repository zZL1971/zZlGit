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
 * @author no value version: 1.00 Date: 20101108 14:43 Last modify Date:
 *         20101108 14:43 CopyRight (c) spro Company 2006 All rights reserved
 *         说明: 销售订单抬头数据<br>
 *         变更:<br>
 */
@Entity
@Table(name = "SALE_ZSALESORDER_HEADER")
public class SaleZsalesorderHeader extends UUIDEntity implements Serializable {
	// 默认构造方法
	public SaleZsalesorderHeader() {
	}

	/**
	 * 销售和分销凭证号
	 */
	private String vbeln;
	/**
	 * 记录建立日期
	 */
	private Date erdat;
	/**
	 * 输入时间
	 */
	private String erzet;
	/**
	 * 创建对象的人员名称
	 */
	private String ernam;
	/**
	 * 凭证日期 (接收/发送日期)
	 */
	private Date audat;
	/**
	 * 销售凭证类型
	 */
	private String auart;
	/**
	 * 订购原因( 业务原因 )
	 */
	private String augru;
	/**
	 * 描述
	 */
	private String bezei;
	/**
	 * SD 凭证货币
	 */
	private String waerk;
	/**
	 * 销售组织
	 */
	private String vkorg;
	/**
	 * 分销渠道
	 */
	private String vtweg;
	/**
	 * 产品组
	 */
	private String spart;
	/**
	 * 销售组
	 */
	private String vkgrp;
	/**
	 * 销售办事处
	 */
	private String vkbur;
	/**
	 * 请求交货日期
	 */
	private Date vdatu;
	/**
	 * 销售地区
	 */
	private String bzirk;
	/**
	 * 区名
	 */
	private String bztxt;
	/**
	 * 客户组
	 */
	private String kdgrp;
	/**
	 * 名称
	 */
	private String ktext;
	/**
	 * 装运条件
	 */
	private String vsbed;
	/**
	 * 装运条件的描述
	 */
	private String vtext;
	/**
	 * 收付条件代码
	 */
	private String zterm;
	/**
	 * 客户采购订单编号
	 */
	private String bstkd;
	/**
	 * 客户编号1
	 */
	private String kunnr;
	/**
	 * 收票方的公司代码
	 */
	private String bukrsVf;
	/**
	 * 单据条件数
	 */
	private String knumv;
	/**
	 * 用于保存创建人用户
	 */
	private String vsnmrV;
	/**
	 * 导购员
	 */
	private String sproDgy;
	/**
	 * 出具发票冻结
	 */
	private String faksk;

	private String sproHeadParters;
	/**
	 * 参考单据号
	 */
	private String vgbel;
	/**
	 * 送货电话
	 */
	private String dlvTel;
	/**
	 * 客服电话
	 */
	private String svcTel;

	private String sproLgort;

	private Double sproSum;

	/**
	 * 参考单金额
	 */
	private String sproSaleSum;

	/**
	 * 参考单订金
	 */
	private String sproSaleDing;

	private String sproStatus;

	private String sproHeadText;

	/**
	 * 受理人
	 */
	private String name1;
	/**
	 * 反馈人
	 */
	private String name2;
	/**
	 * 回访人
	 */
	private String name3;
	/**
	 * 受理时间
	 */
	private Date erdat1;
	/**
	 * 反馈时间
	 */
	private Date erdat2;
	/**
	 * 回访时间
	 */
	private Date erdat3;
	/**
	 * 品牌编号（专柜名称）
	 */
	private String zbrandnum;
	/**
	 * 投诉方式
	 */
	private String zclaimnr;

	/**
	 * 受理时间2
	 */
	private Date erdat4;
	/**
	 * 回访时间2
	 */
	private Date erdat5;
	/**
	 * 受理人2
	 */
	private String name4;
	/**
	 * 回访人2
	 */
	private String name5;
	/**
	 * 满意度
	 */
	private String zmy;
	/**
	 * 满意度2
	 */
	private String zmy1;
	/**
	 * 处理标识
	 */
	private String zshzt;

	/**
	 * 用于保存创建人
	 * 
	 * @return
	 */
	@Column(name = "VSNMR_V")
	public String getVsnmrV() {
		return vsnmrV;
	}

	public void setVsnmrV(String vsnmrV) {
		this.vsnmrV = vsnmrV;
	}

	/**
	 * 销售和分销凭证号
	 */
	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}

	/**
	 * 销售和分销凭证号
	 */
	@Column(name = "VBELN")
	public String getVbeln() {
		return vbeln;
	}

	/**
	 * 记录建立日期
	 */
	public void setErdat(Date erdat) {
		this.erdat = erdat;
	}

	/**
	 * 记录建立日期
	 */
	@Column(name = "ERDAT")
	public Date getErdat() {
		return erdat;
	}

	// /**
	// * 输入时间
	// */
	// public void setErzet(String erzet) {
	// this.erzet = erzet;
	// }
	//
	// /**
	// * 输入时间
	// */
	// @Column(name = "ERZET")
	// public String getErzet() {
	// return erzet;
	// }

	/**
	 * 创建对象的人员名称
	 */
	public void setErnam(String ernam) {
		this.ernam = ernam;
	}

	/**
	 * 创建对象的人员名称
	 */
	@Column(name = "ERNAM")
	public String getErnam() {
		return ernam;
	}

	/**
	 * 凭证日期 (接收/发送日期)
	 */
	public void setAudat(Date audat) {
		this.audat = audat;
	}

	/**
	 * 凭证日期 (接收/发送日期)
	 */
	@Column(name = "AUDAT")
	public Date getAudat() {
		return audat;
	}

	/**
	 * 销售凭证类型
	 */
	public void setAuart(String auart) {
		this.auart = StringUtils.upperCase(auart);
	}

	/**
	 * 销售凭证类型
	 */
	@Column(name = "AUART")
	public String getAuart() {
		return auart;
	}

	/**
	 * 订购原因( 业务原因 )
	 */
	public void setAugru(String augru) {
		this.augru = StringUtils.upperCase(augru);
	}

	/**
	 * 订购原因( 业务原因 )
	 */
	@Column(name = "AUGRU")
	public String getAugru() {
		return augru;
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
	 * SD 凭证货币
	 */
	public void setWaerk(String waerk) {
		this.waerk = StringUtils.upperCase(waerk);
	}

	/**
	 * SD 凭证货币
	 */
	@Column(name = "WAERK")
	public String getWaerk() {
		return waerk;
	}

	/**
	 * 销售组织
	 */
	public void setVkorg(String vkorg) {
		this.vkorg = StringUtils.upperCase(vkorg);
	}

	/**
	 * 销售组织
	 */
	@Column(name = "VKORG")
	public String getVkorg() {
		return vkorg;
	}

	/**
	 * 分销渠道
	 */
	public void setVtweg(String vtweg) {
		this.vtweg = StringUtils.upperCase(vtweg);
	}

	/**
	 * 分销渠道
	 */
	@Column(name = "VTWEG")
	public String getVtweg() {
		return vtweg;
	}

	/**
	 * 产品组
	 */
	public void setSpart(String spart) {
		this.spart = StringUtils.upperCase(spart);
	}

	/**
	 * 产品组
	 */
	@Column(name = "SPART")
	public String getSpart() {
		return spart;
	}

	/**
	 * 销售组
	 */
	public void setVkgrp(String vkgrp) {
		this.vkgrp = StringUtils.upperCase(vkgrp);
	}

	/**
	 * 销售组
	 */
	@Column(name = "VKGRP")
	public String getVkgrp() {
		return vkgrp;
	}

	/**
	 * 销售办事处
	 */
	public void setVkbur(String vkbur) {
		this.vkbur = StringUtils.upperCase(vkbur);
	}

	/**
	 * 销售办事处
	 */
	@Column(name = "VKBUR")
	public String getVkbur() {
		return vkbur;
	}

	/**
	 * 请求交货日期
	 */
	public void setVdatu(Date vdatu) {
		this.vdatu = vdatu;
	}

	/**
	 * 请求交货日期
	 */
	@Column(name = "VDATU")
	public Date getVdatu() {
		return vdatu;
	}

	/**
	 * 销售地区
	 */
	public void setBzirk(String bzirk) {
		this.bzirk = bzirk;
	}

	/**
	 * 销售地区
	 */
	@Column(name = "BZIRK")
	public String getBzirk() {
		return bzirk;
	}

	/**
	 * 区名
	 */
	public void setBztxt(String bztxt) {
		this.bztxt = bztxt;
	}

	/**
	 * 区名
	 */
	@Column(name = "BZTXT")
	public String getBztxt() {
		return bztxt;
	}

	/**
	 * 客户组
	 */
	public void setKdgrp(String kdgrp) {
		this.kdgrp = StringUtils.upperCase(kdgrp);
	}

	/**
	 * 客户组
	 */
	@Column(name = "KDGRP")
	public String getKdgrp() {
		return kdgrp;
	}

	/**
	 * 名称
	 */
	public void setKtext(String ktext) {
		this.ktext = ktext;
	}

	/**
	 * 名称
	 */
	@Column(name = "KTEXT")
	public String getKtext() {
		return ktext;
	}

	/**
	 * 装运条件
	 */
	public void setVsbed(String vsbed) {
		this.vsbed = StringUtils.upperCase(vsbed);
	}

	/**
	 * 装运条件
	 */
	@Column(name = "VSBED")
	public String getVsbed() {
		return vsbed;
	}

	/**
	 * 装运条件的描述
	 */
	public void setVtext(String vtext) {
		this.vtext = vtext;
	}

	/**
	 * 装运条件的描述
	 */
	@Column(name = "VTEXT")
	public String getVtext() {
		return vtext;
	}

	/**
	 * 收付条件代码
	 */
	public void setZterm(String zterm) {
		this.zterm = StringUtils.upperCase(zterm);
	}

	/**
	 * 收付条件代码
	 */
	@Column(name = "ZTERM")
	public String getZterm() {
		return zterm;
	}

	/**
	 * 客户采购订单编号
	 */
	public void setBstkd(String bstkd) {
		this.bstkd = bstkd;
	}

	/**
	 * 客户采购订单编号
	 */
	@Column(name = "BSTKD")
	public String getBstkd() {
		return bstkd;
	}

	/**
	 * 客户编号1
	 */
	public void setKunnr(String kunnr) {
		this.kunnr = StringUtils.upperCase(kunnr);
	}

	/**
	 * 客户编号1
	 */
	@Column(name = "KUNNR")
	public String getKunnr() {
		return kunnr;
	}

	/**
	 * 收票方的公司代码
	 */
	public void setBukrsVf(String bukrsVf) {
		this.bukrsVf = bukrsVf;
	}

	/**
	 * 收票方的公司代码
	 */
	@Column(name = "BUKRS_VF")
	public String getBukrsVf() {
		return bukrsVf;
	}

	/**
	 * 单据条件数
	 */
	public void setKnumv(String knumv) {
		this.knumv = knumv;
	}

	/**
	 * 单据条件数
	 */
	@Column(name = "KNUMV")
	public String getKnumv() {
		return knumv;
	}

	@Column(name = "SPRO_DGY")
	public String getSproDgy() {
		return sproDgy;
	}

	public void setSproDgy(String sproDgy) {
		this.sproDgy = sproDgy;
	}

	@Transient
	public String getSproHeadParters() {
		return sproHeadParters;
	}

	public void setSproHeadParters(String sproHeadParters) {
		this.sproHeadParters = sproHeadParters;
	}

	@Column(name = "FAKSK")
	public String getFaksk() {
		return faksk;
	}

	public void setFaksk(String faksk) {
		this.faksk = faksk;
	}

	@Column(name = "DLV_TEL")
	public String getDlvTel() {
		return dlvTel;
	}

	public void setDlvTel(String dlvTel) {
		this.dlvTel = dlvTel;
	}

	@Column(name = "SVC_TEL")
	public String getSvcTel() {
		return svcTel;
	}

	public void setSvcTel(String svcTel) {
		this.svcTel = svcTel;
	}

	@Column(name = "VGBEL")
	public String getVgbel() {
		return vgbel;
	}

	public void setVgbel(String vgbel) {
		this.vgbel = vgbel;
	}

	@Column(name = "SPRO_LGORT")
	public String getSproLgort() {
		return sproLgort;
	}

	public void setSproLgort(String sproLgort) {
		this.sproLgort = sproLgort;
	}

	@Column(name = "SPRO_SUM")
	public Double getSproSum() {
		return sproSum;
	}

	public void setSproSum(Double sproSum) {
		this.sproSum = sproSum;
	}

	@Column(name = "SPRO_STATUS")
	public String getSproStatus() {
		return sproStatus;
	}

	public void setSproStatus(String sproStatus) {
		this.sproStatus = sproStatus;
	}

	@Transient
	public String getSproHeadText() {
		return sproHeadText;
	}

	public void setSproHeadText(String sproHeadText) {
		this.sproHeadText = sproHeadText;
	}

	/**
	 * 受理人
	 * 
	 * @return
	 */
	@Column(name = "NAME1")
	public String getName1() {
		return name1;
	}

	/**
	 * 受理人
	 * 
	 * @param name1
	 */
	public void setName1(String name1) {
		this.name1 = name1;
	}

	/**
	 * 反馈人
	 * 
	 * @return
	 */
	@Column(name = "NAME2")
	public String getName2() {
		return name2;
	}

	/**
	 * 反馈人
	 * 
	 * @param name2
	 */
	public void setName2(String name2) {
		this.name2 = name2;
	}

	/**
	 * 回访人
	 * 
	 * @return
	 */
	@Column(name = "NAME3")
	public String getName3() {
		return name3;
	}

	/**
	 * 回访人
	 * 
	 * @param name3
	 */
	public void setName3(String name3) {
		this.name3 = name3;
	}

	/**
	 * 受理时间
	 * 
	 * @return
	 */
	@Column(name = "ERDAT1")
	public Date getErdat1() {
		return erdat1;
	}

	/**
	 * 受理时间
	 * 
	 * @param erdat1
	 */
	public void setErdat1(Date erdat1) {
		this.erdat1 = erdat1;
	}

	/**
	 * 反馈时间
	 * 
	 * @return
	 */
	@Column(name = "ERDAT2")
	public Date getErdat2() {
		return erdat2;
	}

	/**
	 * 反馈时间
	 * 
	 * @param erdat2
	 */
	public void setErdat2(Date erdat2) {
		this.erdat2 = erdat2;
	}

	/**
	 * 回访时间
	 * 
	 * @return
	 */
	@Column(name = "ERDAT3")
	public Date getErdat3() {
		return erdat3;
	}

	/**
	 * 回访时间
	 * 
	 * @param erdat3
	 */
	public void setErdat3(Date erdat3) {
		this.erdat3 = erdat3;
	}

	/**
	 * 品牌编号（专柜名称）
	 * 
	 * @return
	 */
	@Column(name = "ZBRANDNUM")
	public String getZbrandnum() {
		return zbrandnum;
	}

	/**
	 * 品牌编号（专柜名称）
	 * 
	 * @param zbrandnum
	 */
	public void setZbrandnum(String zbrandnum) {
		this.zbrandnum = zbrandnum;
	}

	/**
	 * 投诉方式
	 * 
	 * @return
	 */
	@Column(name = "ZCLAIMNR")
	public String getZclaimnr() {
		return zclaimnr;
	}

	/**
	 * 投诉方式
	 * 
	 * @param zclaimnr
	 */
	public void setZclaimnr(String zclaimnr) {
		this.zclaimnr = zclaimnr;
	}

	/**
	 * 受理时间2
	 * 
	 * @return
	 */
	@Column(name = "ERDAT4")
	public Date getErdat4() {
		return erdat4;
	}

	/**
	 * 受理时间2
	 * 
	 * @param erdat4
	 */
	public void setErdat4(Date erdat4) {
		this.erdat4 = erdat4;
	}

	/**
	 * 回访时间2
	 * 
	 * @return
	 */
	@Column(name = "ERDAT5")
	public Date getErdat5() {
		return erdat5;
	}

	/**
	 * 回访时间2
	 * 
	 * @param erdat5
	 */
	public void setErdat5(Date erdat5) {
		this.erdat5 = erdat5;
	}

	/**
	 * 受理人2
	 * 
	 * @return
	 */
	@Column(name = "NAME4")
	public String getName4() {
		return name4;
	}

	/**
	 * 受理人2
	 * 
	 * @param name4
	 */
	public void setName4(String name4) {
		this.name4 = name4;
	}

	/**
	 * 回访人2
	 * 
	 * @return
	 */
	@Column(name = "NAME5")
	public String getName5() {
		return name5;
	}

	/**
	 * 回访人2
	 * 
	 * @param name5
	 */
	public void setName5(String name5) {
		this.name5 = name5;
	}

	/**
	 * 满意度
	 * 
	 * @return
	 */
	@Column(name = "ZMY")
	public String getZmy() {
		return zmy;
	}

	/**
	 * 满意度
	 * 
	 * @param zmy
	 */
	public void setZmy(String zmy) {
		this.zmy = zmy;
	}

	/**
	 * 满意度2
	 * 
	 * @return
	 */
	@Column(name = "ZMY1")
	public String getZmy1() {
		return zmy1;
	}

	/**
	 * 满意度2
	 * 
	 * @param zmy1
	 */
	public void setZmy1(String zmy1) {
		this.zmy1 = zmy1;
	}

	/**
	 * 处理标识
	 * 
	 * @return
	 */
	@Column(name = "ZSHZT")
	public String getZshzt() {
		return zshzt;
	}

	/**
	 * 处理标识
	 * 
	 * @param zshzt
	 */
	public void setZshzt(String zshzt) {
		this.zshzt = zshzt;
	}

	/**
	 * 参考单金额
	 * 
	 * @return
	 */
	@Transient
	public String getSproSaleSum() {
		return sproSaleSum;
	}

	/**
	 * 参考单金额
	 * 
	 * @param sproSaleSum
	 */
	public void setSproSaleSum(String sproSaleSum) {
		this.sproSaleSum = sproSaleSum;
	}

	/**
	 * 参考单订金
	 * 
	 * @return
	 */
	@Transient
	public String getSproSaleDing() {
		return sproSaleDing;
	}

	/**
	 * 参考单订金
	 * 
	 * @param sproSaleDing
	 */
	public void setSproSaleDing(String sproSaleDing) {
		this.sproSaleDing = sproSaleDing;
	}

}