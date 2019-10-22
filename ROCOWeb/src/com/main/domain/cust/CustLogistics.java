package com.main.domain.cust;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "CUST_LOGISTICS")
public class CustLogistics extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7341135017519204456L;

	private String kunnr;
	
	private String vkorg;
	
	private String vtweg;
	
	private String spart;
	
	private String kunnrS;

	private CustHeader custHeader;
	
	@Column(name="KUNNR",length=20)
	public String getKunnr() {
		return kunnr;
	}

	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}
	
	@Column(name="VKORG",length=20)
	public String getVkorg() {
		return vkorg;
	}

	public void setVkorg(String vkorg) {
		this.vkorg = vkorg;
	}
	
	@Column(name="VTWEG",length=15)
	public String getVtweg() {
		return vtweg;
	}

	public void setVtweg(String vtweg) {
		this.vtweg = vtweg;
	}
	
	@Column(name="SPART",length=5)
	public String getSpart() {
		return spart;
	}

	public void setSpart(String spart) {
		this.spart = spart;
	}

	@Column(name="KUNNR_S",length=20)
	public String getKunnrS() {
		return kunnrS;
	}

	public void setKunnrS(String kunnrS) {
		this.kunnrS = kunnrS;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public CustHeader getCustHeader() {
		return custHeader;
	}

	public void setCustHeader(CustHeader custHeader) {
		this.custHeader = custHeader;
	}
	
}
