package com.mw.framework.model;

import java.util.List;

import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="CXFOrderModel")
public class CXFOrderModel {

	private String orderCode;
	
	private String userId;
	
	private List<CXFWebFileModel> file;

	private List<CXFPriceModel> priceList;
	public final String getOrderCode() {
		return orderCode;
	}

	public final void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public final String getUserId() {
		return userId;
	}

	public final void setUserId(String userId) {
		this.userId = userId;
	}

	@XmlMimeType("application/json")
	public final List<CXFWebFileModel> getFile() {
		return file;
	}

	public final void setFile(List<CXFWebFileModel> file) {
		this.file = file;
	}
	@XmlMimeType("application/json")
	public List<CXFPriceModel> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<CXFPriceModel> priceList) {
		this.priceList = priceList;
	}
	
}
