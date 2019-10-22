package com.main.domain.sale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 一次性送达方，售达方
 * 
 * @author samguo
 * 
 */
@Entity
@Table(name = "SALE_ONE_CUST")
public class SaleOneCust extends UUIDEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7662748242547384833L;

	/**
	 * 销售订单
	 */
	private String vbeln;
	/**
	 * 客户
	 */
	private String kunnr;
	/**
	 * 称谓
	 */
	private String anred;
	/**
	 * 名称
	 */
	private String saleOneCustName1;
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
	 * 移动电话
	 */
	private String telf1;
	/**
	 * 运输区域
	 */
	private String ort02;
	/**
	 * 收货地址
	 */
	private String socAddress;

	private String saleOneCustType;

	private SaleHeader saleHeader;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public SaleHeader getSaleHeader() {
		return saleHeader;
	}

	public void setSaleHeader(SaleHeader saleHeader) {
		this.saleHeader = saleHeader;
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
	 * 移动电话
	 * 
	 * @return
	 */
	@Column(name = "TELF1")
	public String getTelf1() {
		return telf1;
	}

	/**
	 * 移动电话
	 * 
	 * @param telf1
	 */
	public void setTelf1(String telf1) {
		this.telf1 = telf1;
	}

	/**
	 * 销售订单
	 * 
	 * @return
	 */
	@Column(name = "VBELN")
	public String getVbeln() {
		return vbeln;
	}

	/**
	 * 销售订单
	 * 
	 * @param vbeln
	 */
	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
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
	 * 运输区域
	 * 
	 * @return
	 */
	@Column(name = "ORT02")
	public String getOrt02() {
		return ort02;
	}

	/**
	 * 运输区域
	 * 
	 * @param ort02
	 */
	public void setOrt02(String ort02) {
		this.ort02 = ort02;
	}

	/**
	 * 名称
	 * 
	 * @return
	 */
	@Column(name = "NAME1")
	public String getSaleOneCustName1() {
		return saleOneCustName1;
	}

	/**
	 * 名称
	 * 
	 * @param saleOneCustName1
	 */
	public void setSaleOneCustName1(String saleOneCustName1) {
		this.saleOneCustName1 = saleOneCustName1;
	}

	@Column(name = "TYPE")
	public String getSaleOneCustType() {
		return saleOneCustType;
	}

	public void setSaleOneCustType(String saleOneCustType) {
		this.saleOneCustType = saleOneCustType;
	}

	public String getSocAddress() {
		return socAddress;
	}

	public void setSocAddress(String socAddress) {
		this.socAddress = socAddress;
	}

	 

}
