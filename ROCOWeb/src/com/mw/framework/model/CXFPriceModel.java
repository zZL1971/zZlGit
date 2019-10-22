package com.mw.framework.model;

/**
 * 
 * @author Chaly
 *
 */
public class CXFPriceModel {

	/*"type": "01",
	"name": "外侧板",
	"color": "阿波罗红",
	"wide": "600",
	"high": "250",
	"deep": "18",
	"amount": "1",
	"unit": "平米",
	"area": "0.15",
	"unitPrice": "132",
	"totalPrice": "20",
	"rebate": "1",
	"netPrice": "20",
	"line": "A"*/
	private String type;//分类
	private String name;//名称
	private String color;//颜色
	private String wide;//宽
	private String high;//高
	private String deep;//深
	private String amount;//数量
	private String unit;//单位
	private String area;//计量/面积
	private String unitPrice;//单价
	private String totalPrice;//总价
	private String rebate;//折扣
	private String netPrice;//净价
	private String line;//产线
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getWide() {
		return wide;
	}
	public void setWide(String wide) {
		this.wide = wide;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getDeep() {
		return deep;
	}
	public void setDeep(String deep) {
		this.deep = deep;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getRebate() {
		return rebate;
	}
	public void setRebate(String rebate) {
		this.rebate = rebate;
	}
	public String getNetPrice() {
		return netPrice;
	}
	public void setNetPrice(String netPrice) {
		this.netPrice = netPrice;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	
}
