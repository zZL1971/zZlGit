package com.main.domain.cust;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.main.dao.CustHeaderDao;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.context.SpringContextHolder;

@Entity
@Table(name = "CUST_HEADER")
public class CustHeader extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9107328546690460235L;

	// 默认构造方法
	public CustHeader() {
	}

	/**
	 * 帐户组
	 */
	private String ktokd;
	/**
	 * 客户编号
	 */
	private String kunnr;
	/**
	 * 公司代码
	 */
	private String bukrs;
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
	 * 客户名称描述
	 */
	private String name1;
	/**
	 * 搜索项
	 */
	private String sortl;
	/**
	 * 店面面积（平方）
	 */
	private Double building;
	/**
	 * 街道/门牌号
	 */
	private String street;
	/**
	 * 邮政编码
	 */
	private String pstlz;
	/**
	 * 城市
	 */
	private String mcod3;
	/**
	 * 国家
	 */
	private String land1;
	/**
	 * 地区
	 */
	private String regio;
	/**
	 * 区域
	 */
	private String ort02;
	/**
	 * 统驭科目
	 */
	private String akont;
	/**
	 * 销售地区
	 */
	private String bzirk;
	/**
	 * 销售部门
	 */
	private String vkbur;
	/**
	 * 销售组
	 */
	private String vkgrp;
	/**
	 * 货币
	 */
	private String waers;
	/**
	 * 客户定价过程
	 */
	private String kalks;
	/**
	 * 税分类
	 */
	private String taxkd;
	/**
	 * 送达方（指定合作伙伴为SH）
	 */
	private String kunnrS;
	/**
	 * 电话
	 */
	private String tel;
	/**
	 * 客户电话
	 */
	private String telnum;

	/**
	 * 信贷额度
	 */
	private String xinDai;
	/**
	 * 折扣明细
	 */
	private Set<CustItem> custItemSet;
	/**
	 * 联系人
	 */
	private Set<CustContacts> custContactsSet;
	/**
	 * 物流信息
	 */
	private Set<CustLogistics> custLogisticsSet;
	/**
	 * 审绘员
	 */
	private String checkDrawUser;
	/**
	 * 橱柜标识
	 */
	private String zzcupboard;
	/**
	 * 木门标示
	 */
	private String zztimber;
	/**
	 * 客户级别
	 */
	private String custLevel;

	private String zproduct;
	/**
	 * 电话
	 * 
	 * @return
	 */
	@Column(name = "TEL")
	public String getTel() {
		return tel;
	}

	/**
	 * 电话
	 * 
	 * @param tel
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * 客户电话
	 * 
	 * @return
	 */
	@Column(name = "TELNUM")
	public String getTelnum() {
		return telnum;
	}

	/**
	 * 客户电话
	 * 
	 * @param telnum
	 */
	public void setTelnum(String telnum) {
		this.telnum = telnum;
	}

	/**
	 * 信贷额度
	 * 
	 * @return
	 */
	@Column(name = "XIN_DAI")
	public String getXinDai() {
		return xinDai;
	}

	/**
	 * 信贷额度
	 * 
	 * @param xinDai
	 */
	public void setXinDai(String xinDai) {
		this.xinDai = xinDai;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "custHeader")
	public Set<CustItem> getCustItemSet() {
		return custItemSet;
	}

	public void setCustItemSet(Set<CustItem> custItemSet) {
		this.custItemSet = custItemSet;
	}

	/**
	 * 帐户组
	 * 
	 * @return
	 */
	@Column(name = "KTOKD")
	public String getKtokd() {
		return ktokd;
	}

	/**
	 * 帐户组
	 * 
	 * @param ktokd
	 */
	public void setKtokd(String ktokd) {
		this.ktokd = ktokd;
	}

	/**
	 * 客户编号
	 * 
	 * @return
	 */
	@Column(name = "KUNNR")
	public String getKunnr() {
		return kunnr;
	}

	/**
	 * 客户编号
	 * 
	 * @param kunnr
	 */
	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}

	/**
	 * 公司代码
	 * 
	 * @return
	 */
	@Column(name = "BUKRS")
	public String getBukrs() {
		return bukrs;
	}

	/**
	 * 公司代码
	 * 
	 * @param bukrs
	 */
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	/**
	 * 销售组织
	 * 
	 * @return
	 */
	@Column(name = "VKORG")
	public String getVkorg() {
		return vkorg;
	}

	/**
	 * 销售组织
	 * 
	 * @param vkorg
	 */
	public void setVkorg(String vkorg) {
		this.vkorg = vkorg;
	}

	/**
	 * 分销渠道
	 * 
	 * @return
	 */
	@Column(name = "VTWEG")
	public String getVtweg() {
		return vtweg;
	}

	/**
	 * 分销渠道
	 * 
	 * @param vtweg
	 */
	public void setVtweg(String vtweg) {
		this.vtweg = vtweg;
	}

	/**
	 * 产品组
	 * 
	 * @return
	 */
	@Column(name = "SPART")
	public String getSpart() {
		return spart;
	}

	/**
	 * 产品组
	 * 
	 * @param spart
	 */
	public void setSpart(String spart) {
		this.spart = spart;
	}

	/**
	 * 客户名称描述
	 * 
	 * @return
	 */
	@Column(name = "NAME1")
	public String getName1() {
		return name1;
	}

	/**
	 * 客户名称描述
	 * 
	 * @param name1
	 */
	public void setName1(String name1) {
		this.name1 = name1;
	}

	/**
	 * 搜索项
	 * 
	 * @return
	 */
	@Column(name = "SORTL")
	public String getSortl() {
		return sortl;
	}

	/**
	 * 搜索项
	 * 
	 * @param sortl
	 */
	public void setSortl(String sortl) {
		this.sortl = sortl;
	}

	/**
	 * 店面面积（平方）
	 * 
	 * @return
	 */
	@Column(name = "BUILDING")
	public Double getBuilding() {
		return building;
	}

	/**
	 * 店面面积（平方）
	 * 
	 * @param building
	 */
	public void setBuilding(Double building) {
		this.building = building;
	}

	/**
	 * 街道/门牌号
	 * 
	 * @return
	 */
	@Column(name = "STREET")
	public String getStreet() {
		return street;
	}

	/**
	 * 街道/门牌号
	 * 
	 * @param street
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * 邮政编码
	 * 
	 * @return
	 */
	@Column(name = "PSTLZ")
	public String getPstlz() {
		return pstlz;
	}

	/**
	 * 邮政编码
	 * 
	 * @param pstlz
	 */
	public void setPstlz(String pstlz) {
		this.pstlz = pstlz;
	}

	/**
	 * 城市
	 * 
	 * @return
	 */
	@Column(name = "MCOD3")
	public String getMcod3() {
		return mcod3;
	}

	/**
	 * 城市
	 * 
	 * @param mcod3
	 */
	public void setMcod3(String mcod3) {
		this.mcod3 = mcod3;
	}

	/**
	 * 国家
	 * 
	 * @return
	 */
	@Column(name = "LAND1")
	public String getLand1() {
		return land1;
	}

	/**
	 * 国家
	 * 
	 * @param land1
	 */
	public void setLand1(String land1) {
		this.land1 = land1;
	}

	/**
	 * 地区
	 * 
	 * @return
	 */
	@Column(name = "REGIO")
	public String getRegio() {
		return regio;
	}

	/**
	 * 地区
	 * 
	 * @param regio
	 */
	public void setRegio(String regio) {
		this.regio = regio;
	}

	/**
	 * 区域
	 * 
	 * @return
	 */
	@Column(name = "ORT02")
	public String getOrt02() {
		return ort02;
	}

	/**
	 * 区域
	 * 
	 * @param ort02
	 */
	public void setOrt02(String ort02) {
		this.ort02 = ort02;
	}

	/**
	 * 统驭科目
	 * 
	 * @return
	 */
	@Column(name = "AKONT")
	public String getAkont() {
		return akont;
	}

	/**
	 * 统驭科目
	 * 
	 * @param akont
	 */
	public void setAkont(String akont) {
		this.akont = akont;
	}

	/**
	 * 销售地区
	 * 
	 * @return
	 */
	@Column(name = "BZIRK")
	public String getBzirk() {
		return bzirk;
	}

	/**
	 * 销售地区
	 * 
	 * @param bzirk
	 */
	public void setBzirk(String bzirk) {
		this.bzirk = bzirk;
	}

	/**
	 * 销售部门
	 * 
	 * @return
	 */
	@Column(name = "VKBUR")
	public String getVkbur() {
		return vkbur;
	}

	/**
	 * 销售部门
	 * 
	 * @param vkbur
	 */
	public void setVkbur(String vkbur) {
		this.vkbur = vkbur;
	}

	/**
	 * 销售组
	 * 
	 * @return
	 */
	@Column(name = "VKGRP")
	public String getVkgrp() {
		return vkgrp;
	}

	/**
	 * 销售组
	 * 
	 * @param vkgrp
	 */
	public void setVkgrp(String vkgrp) {
		this.vkgrp = vkgrp;
	}

	/**
	 * 货币
	 * 
	 * @return
	 */
	@Column(name = "WAERS")
	public String getWaers() {
		return waers;
	}

	/**
	 * 货币
	 * 
	 * @param waers
	 */
	public void setWaers(String waers) {
		this.waers = waers;
	}

	/**
	 * 客户定价过程
	 * 
	 * @return
	 */
	@Column(name = "KALKS")
	public String getKalks() {
		return kalks;
	}

	/**
	 * 客户定价过程
	 * 
	 * @param kalks
	 */
	public void setKalks(String kalks) {
		this.kalks = kalks;
	}

	/**
	 * 税分类
	 * 
	 * @return
	 */
	@Column(name = "TAXKD")
	public String getTaxkd() {
		return taxkd;
	}

	/**
	 * 税分类
	 * 
	 * @param taxkd
	 */
	public void setTaxkd(String taxkd) {
		this.taxkd = taxkd;
	}

	/**
	 * 送达方（指定合作伙伴为SH）
	 * 
	 * @return
	 */
	@Column(name = "KUNNR_S")
	public String getKunnrS() {
		return kunnrS;
	}

	/**
	 * 送达方（指定合作伙伴为SH）
	 * 
	 * @param kunnrS
	 */
	public void setKunnrS(String kunnrS) {
		this.kunnrS = kunnrS;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "custHeader")
	public Set<CustContacts> getCustContactsSet() {
		return custContactsSet;
	}

	public void setCustContactsSet(Set<CustContacts> custContactsSet) {
		this.custContactsSet = custContactsSet;
	}

	/**
	 * 取当前客户的送达方信息
	 * 
	 * @return
	 */
	@Transient
	public CustHeader getCustHeaderS() {
		if (this.kunnrS == null || this.kunnrS.equals("")) {
			return null;
		} else {
			CustHeaderDao custHeaderDao = SpringContextHolder
					.getBean("custHeaderDao");
			List<CustHeader> findByCode = custHeaderDao.findByCode(this.kunnrS);
			if (findByCode == null || findByCode.size() == 0) {
				return null;
			} else {
				return findByCode.get(0);
			}
		}
	}

	/**
	 * 审绘员
	 * 
	 * @return
	 */
	@Column(name = "CHECK_DRAW_USER")
	public String getCheckDrawUser() {
		return checkDrawUser;
	}

	/**
	 * 审绘员
	 * 
	 * @param checkDrawUser
	 */
	public void setCheckDrawUser(String checkDrawUser) {
		this.checkDrawUser = checkDrawUser;
	}

	/**
	 * 橱柜标识
	 * 
	 * @return
	 */
	@Column(name = "ZZCUPBOARD", length = 10)
	public String getZzcupboard() {
		return zzcupboard;
	}

	/**
	 * 橱柜标识
	 * 
	 * @param zzCupBoard
	 */
	public void setZzcupboard(String zzcupboard) {
		this.zzcupboard = zzcupboard;
	}

	/**
	 * 木门标识
	 * 
	 * @return
	 */
	@Column(name = "ZZTIMBER", length = 10)
	public String getZztimber() {
		return zztimber;
	}

	/**
	 * 木门标识
	 * 
	 * @param zzTimber
	 */
	public void setZztimber(String zztimber) {
		this.zztimber = zztimber;
	}

	@Column(name="ZPRODUCT",length=50)
	public String getZproduct() {
		return zproduct;
	}

	public void setZproduct(String zproduct) {
		this.zproduct = zproduct;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "custHeader")
	public Set<CustLogistics> getCustLogisticsSet() {
		return custLogisticsSet;
	}

	public void setCustLogisticsSet(Set<CustLogistics> custLogisticsSet) {
		this.custLogisticsSet = custLogisticsSet;
	}

}