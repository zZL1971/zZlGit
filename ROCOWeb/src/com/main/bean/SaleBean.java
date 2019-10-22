package com.main.bean;

import java.util.List;

import com.main.domain.cust.TerminalClient;
import com.main.domain.sale.SaleBgfile;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleOneCust;

public class SaleBean {

	private TerminalClient terminalClient;
	
	
	private SaleBgfile saleBgfile;

	private List<SaleItem> saleItemList;

	private List<SaleItem> delItems;
	
	private List<SaleOneCust> saleOneCustList;

	public TerminalClient getTerminalClient() {
		return terminalClient;
	}

	public void setTerminalClient(TerminalClient terminalClient) {
		this.terminalClient = terminalClient;
	}

	public List<SaleItem> getSaleItemList() {
		return saleItemList;
	}

	public void setSaleItemList(List<SaleItem> saleItemList) {
		this.saleItemList = saleItemList;
	}

	public List<SaleItem> getDelItems() {
		return delItems;
	}

	public void setDelItems(List<SaleItem> delItems) {
		this.delItems = delItems;
	}

	public List<SaleOneCust> getSaleOneCustList() {
		return saleOneCustList;
	}

	public void setSaleOneCustList(List<SaleOneCust> saleOneCustList) {
		this.saleOneCustList = saleOneCustList;
	}


	public SaleBgfile getSaleBgfile() {
		return saleBgfile;
	}

	public void setSaleBgfile(SaleBgfile saleBgfile) {
		this.saleBgfile = saleBgfile;
	}
	
	
}
