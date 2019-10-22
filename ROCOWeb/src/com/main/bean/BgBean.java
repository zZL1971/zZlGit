package com.main.bean;

import java.util.List;

import com.main.domain.bg.BgHeader;
import com.main.domain.bg.BgItem;

public class BgBean {

	private BgHeader bgHeader;

	private List<BgItem> bgItemList;

	public BgHeader getBgHeader() {
		return bgHeader;
	}

	public void setBgHeader(BgHeader bgHeader) {
		this.bgHeader = bgHeader;
	}

	public List<BgItem> getBgItemList() {
		return bgItemList;
	}

	public void setBgItemList(List<BgItem> bgItemList) {
		this.bgItemList = bgItemList;
	}

}
