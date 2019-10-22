package com.main.bean;

import java.util.List;

import com.main.domain.cust.CustContacts;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustItem;

public class CustBean {

	private CustHeader custHeader;

	private List<CustItem> custItemList;

	private List<CustContacts> custContactsList;

	public CustHeader getCustHeader() {
		return custHeader;
	}

	public void setCustHeader(CustHeader custHeader) {
		this.custHeader = custHeader;
	}

	public List<CustItem> getCustItemList() {
		return custItemList;
	}

	public void setCustItemList(List<CustItem> custItemList) {
		this.custItemList = custItemList;
	}

	public List<CustContacts> getCustContactsList() {
		return custContactsList;
	}

	public void setCustContactsList(List<CustContacts> custContactsList) {
		this.custContactsList = custContactsList;
	}

}
