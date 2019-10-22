package com.mw.framework.model;

import java.util.List;

/**
 * 板件信息 Model
 * @author Chaly
 *
 */
public class PlateInfo {

	private Double width;
	private Double length;
	private String id;
	private String orderId;
	private String barcode;
	private Integer grid;
	//孔位信息 成一对多的关系
	private List<HoleInfo> holeInfo;
	public Double getWidth() {
		return width;
	}
	public PlateInfo() {
		
	}
	public PlateInfo(Double width, Double length, String id, String orderId, String barcode, int grid) {
		super();
		this.width = width;
		this.length = length;
		this.id = id;
		this.orderId = orderId;
		this.barcode = barcode;
		this.grid = grid;
	}

	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getLength() {
		return length;
	}
	public void setLength(Double length) {
		this.length = length;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public Integer getGrid() {
		return grid;
	}
	public void setGrid(Integer grid) {
		this.grid = grid;
	}
	public List<HoleInfo> getHoleInfo() {
		return holeInfo;
	}
	public void setHoleInfo(List<HoleInfo> holeInfo) {
		this.holeInfo = holeInfo;
	}
	@Override
	public String toString() {
		return "PlateInfo [width=" + width + ", length=" + length + ", id="
				+ id + ", orderId=" + orderId + ", barcode=" + barcode
				+ ", grid=" + grid + ", holeInfo=" + holeInfo + "]";
	}
	
	
}
