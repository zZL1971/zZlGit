package com.main.domain.sale;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.ibatis.annotations.One;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 订单额外信息
 * @author Administrator
 *
 */
@Entity
@Table(name="SALE_HEADER_EXT_DATA")
public class SaleHeaderExtData extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3190649829212841609L;
	
	private Integer barCodeDownLoad;
	private SaleHeader saleHeader;
	@Column(name="BAR_CODE_DOWNLOAD",length=4)
	public Integer getBarCodeDownLoad() {
		return barCodeDownLoad;
	}
	public void setBarCodeDownLoad(Integer barCodeDownLoad) {
		this.barCodeDownLoad = barCodeDownLoad;
	}
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID")
	public SaleHeader getSaleHeader() {
		return saleHeader;
	}
	public void setSaleHeader(SaleHeader saleHeader) {
		this.saleHeader = saleHeader;
	}
}
