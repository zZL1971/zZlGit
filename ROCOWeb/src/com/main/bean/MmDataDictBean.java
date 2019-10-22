package com.main.bean;

import java.util.List;

public class MmDataDictBean {

	private String name;
	private String code;
	private Double price;
	private List<MmDataDictBean> items;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<MmDataDictBean> getItems() {
		return items;
	}
	public void setItems(List<MmDataDictBean> items) {
		this.items = items;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
}
