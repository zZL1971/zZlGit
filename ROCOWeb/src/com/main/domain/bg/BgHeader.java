package com.main.domain.bg;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "BG_HEADER")
public class BgHeader extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3437335425409782011L;

	// 默认构造方法
	public BgHeader() {
	}

	/**
	 * 变更单号
	 */
	private String bgCode;
	/**
	 * 订单编号
	 */
	private String orderCode;
	/**
	 * 申请客户
	 */
	private String clients;
	/**
	 * 变更类型
	 */
	private String bgType;
	/**
	 * 联系人
	 */
	private String contacts;
	/**
	 * 联系电话
	 */
	private String tel;
	/**
	 * 订单状态
	 */
	private String orderStatus;
	/**
	 * 原因
	 */
	private String reason;
	/**
	 * 备注
	 */
	private String remarks;
	
	private Set<BgItem> bgItemSet;
	/**
	 * 提交变更时当前环节
	 */
	private String jdName;
	@Column(name = "BG_CODE")
	public String getBgCode() {
		return bgCode;
	}

	public void setBgCode(String bgCode) {
		this.bgCode = bgCode;
	}

	@Column(name = "ORDER_CODE")
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "CLIENTS")
	public String getClients() {
		return clients;
	}

	public void setClients(String clients) {
		this.clients = clients;
	}

	@Column(name = "BG_TYPE")
	public String getBgType() {
		return bgType;
	}

	public void setBgType(String bgType) {
		this.bgType = bgType;
	}

	@Column(name = "CONTACTS")
	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	@Column(name = "TEL")
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Column(name = "ORDER_STATUS")
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Column(name = "REASON")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bgHeader")
	public Set<BgItem> getBgItemSet() {
		return bgItemSet;
	}

	public void setBgItemSet(Set<BgItem> bgItemSet) {
		this.bgItemSet = bgItemSet;
	}
	
	@Column(name="JD_NAME",length=40)
	public String getJdName() {
		return jdName;
	}

	public void setJdName(String jdName) {
		this.jdName = jdName;
	}

}