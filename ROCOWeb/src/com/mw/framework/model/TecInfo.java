package com.mw.framework.model;

public class TecInfo {
	private String orderId;
	private String id;
	private String tecGroove;
	private String tecSide;
	private String tecTRough;
	private String tecPore;
	private String tecXlk;
	private Integer tecPoreNum;
	public String getTecGroove() {
		return tecGroove;
	}
	public void setTecGroove(String tecGroove) {
		this.tecGroove = tecGroove;
	}
	public String getTecSide() {
		return tecSide;
	}
	public void setTecSide(String tecSide) {
		this.tecSide = tecSide;
	}
	public String getTecTRough() {
		return tecTRough;
	}
	public void setTecTRough(String tecTRough) {
		this.tecTRough = tecTRough;
	}
	public String getTecPore() {
		return tecPore;
	}
	public void setTecPore(String tecPore) {
		this.tecPore = tecPore;
	}
	public String getTecXlk() {
		return tecXlk;
	}
	public void setTecXlk(String tecXlk) {
		this.tecXlk = tecXlk;
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "TecInfo [orderId=" + orderId + ", id=" + id + ", tecGroove="
				+ tecGroove + ", tecSide=" + tecSide + ", tecTRough="
				+ tecTRough + ", tecPore=" + tecPore + ", tecXlk=" + tecXlk
				+ "]";
	}
	public Integer getTecPoreNum() {
		return tecPoreNum;
	}
	public void setTecPoreNum(Integer tecPoreNum) {
		this.tecPoreNum = tecPoreNum;
	}

}
