package com.mw.framework.model;
/**
 * 孔位信息表 Model
 * @author Chaly
 *
 */
public class HoleInfo {
	private Double ipX;
	private Double ipY;
	private Double orX;
	private Double orY;
	private Integer machining;
	private Double de;
	private Double dia;
	private Double epX;
	private Double epY;
	private Double orZ;
	private String orType;
	private String wgname;
	
	public HoleInfo() {

	}
	
	public HoleInfo(Double ipX, Double ipY, Double orX, Double orY, Integer machining, Double de, Double dia,
			Double epX, Double epY, Double orZ, String orType, String wgname) {
		super();
		this.ipX = ipX;
		this.ipY = ipY;
		this.orX = orX;
		this.orY = orY;
		this.machining = machining;
		this.de = de;
		this.dia = dia;
		this.epX = epX;
		this.epY = epY;
		this.orZ = orZ;
		this.orType = orType;
		this.wgname = wgname;
	}

	public Double getIpX() {
		return ipX;
	}
	public void setIpX(Double ipX) {
		this.ipX = ipX;
	}
	public Double getIpY() {
		return ipY;
	}
	public void setIpY(Double ipY) {
		this.ipY = ipY;
	}
	public Double getOrX() {
		return orX;
	}
	public void setOrX(Double orX) {
		this.orX = orX;
	}
	public Double getOrY() {
		return orY;
	}
	public void setOrY(Double orY) {
		this.orY = orY;
	}
	public Integer getMachining() {
		return machining;
	}
	public void setMachining(Integer machining) {
		this.machining = machining;
	}
	public Double getDe() {
		return de;
	}
	public void setDe(Double de) {
		this.de = de;
	}
	public Double getDia() {
		return dia;
	}
	public void setDia(Double dia) {
		this.dia = dia;
	}
	public Double getEpX() {
		return epX;
	}
	public void setEpX(Double epX) {
		this.epX = epX;
	}
	public Double getEpY() {
		return epY;
	}
	public void setEpY(Double epY) {
		this.epY = epY;
	}
	public Double getOrZ() {
		return orZ;
	}
	public void setOrZ(Double orZ) {
		this.orZ = orZ;
	}
	public String getOrType() {
		return orType;
	}
	public void setOrType(String orType) {
		this.orType = orType;
	}
	@Override
	public String toString() {
		return "HoleInfo [ipX=" + ipX + ", ipY=" + ipY + ", orX=" + orX
				+ ", orY=" + orY + ", machining=" + machining + ", de=" + de
				+ ", dia=" + dia + ", epX=" + epX + ", epY=" + epY + ", orZ="
				+ orZ + ", orType=" + orType + "]";
	}
	public String getWgname() {
		return wgname;
	}
	public void setWgname(String wgname) {
		this.wgname = wgname;
	}
	
}
