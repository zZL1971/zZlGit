package com.mw.framework.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SALE_SHIPMENT_LOG")
public class SaleShipmentLog extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = -4571992986506918034L;

	private Timestamp outputTime;// 出库时间
	private String sapCode;// SAP订单号
	private String orderCode;// 订单号
	private String posex;// 行项目编号
	private String generalSum;// 通用码数量
	private String carcaseSum;// 柜体数量
	private String shutterSum;// 门板数量
	private String backboardSum;// 背板数量
	private String partsSum;// 配件数量
	private String metalsSum;// 五金数量
	private String sliddoorSum;// 移门数量
	private String plasticSum;// 吸塑门板数量
	private String decorationSum;// 装饰件数量
	private String name;// 名称
	private String custName;// 客户姓名
	private String custPhone;// 客户联系方式
	private String consignee;//收货人
	private String consigneePhone;//收货人联系方式
	private String kunnr;//售达方
	private String kunnrName;//售达方名称
	public Timestamp getOutputTime() {
		return outputTime;
	}
	public void setOutputTime(Timestamp outputTime) {
		this.outputTime = outputTime;
	}
	public String getSapCode() {
		return sapCode;
	}
	public void setSapCode(String sapCode) {
		this.sapCode = sapCode;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getPosex() {
		return posex;
	}
	public void setPosex(String posex) {
		this.posex = posex;
	}
	public String getGeneralSum() {
		return generalSum;
	}
	public void setGeneralSum(String generalSum) {
		this.generalSum = generalSum;
	}
	public String getCarcaseSum() {
		return carcaseSum;
	}
	public void setCarcaseSum(String carcaseSum) {
		this.carcaseSum = carcaseSum;
	}
	public String getShutterSum() {
		return shutterSum;
	}
	public void setShutterSum(String shutterSum) {
		this.shutterSum = shutterSum;
	}
	public String getBackboardSum() {
		return backboardSum;
	}
	public void setBackboardSum(String backboardSum) {
		this.backboardSum = backboardSum;
	}
	public String getPartsSum() {
		return partsSum;
	}
	public void setPartsSum(String partsSum) {
		this.partsSum = partsSum;
	}
	public String getMetalsSum() {
		return metalsSum;
	}
	public void setMetalsSum(String metalsSum) {
		this.metalsSum = metalsSum;
	}
	public String getSliddoorSum() {
		return sliddoorSum;
	}
	public void setSliddoorSum(String sliddoorSum) {
		this.sliddoorSum = sliddoorSum;
	}
	public String getPlasticSum() {
		return plasticSum;
	}
	public void setPlasticSum(String plasticSum) {
		this.plasticSum = plasticSum;
	}
	public String getDecorationSum() {
		return decorationSum;
	}
	public void setDecorationSum(String decorationSum) {
		this.decorationSum = decorationSum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustPhone() {
		return custPhone;
	}
	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getConsigneePhone() {
		return consigneePhone;
	}
	public void setConsigneePhone(String consigneePhone) {
		this.consigneePhone = consigneePhone;
	}
	public String getKunnr() {
		return kunnr;
	}
	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}
	public String getKunnrName() {
		return kunnrName;
	}
	public void setKunnrName(String kunnrName) {
		this.kunnrName = kunnrName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public SaleShipmentLog(Timestamp outputTime, String sapCode,
			String orderCode, String posex, String generalSum,
			String carcaseSum, String shutterSum, String backboardSum,
			String partsSum, String metalsSum, String sliddoorSum,
			String plasticSum, String decorationSum, String name,
			String custName, String custPhone, String consignee,
			String consigneePhone, String kunnr, String kunnrName) {
		super();
		this.outputTime = outputTime;
		this.sapCode = sapCode;
		this.orderCode = orderCode;
		this.posex = posex;
		this.generalSum = generalSum;
		this.carcaseSum = carcaseSum;
		this.shutterSum = shutterSum;
		this.backboardSum = backboardSum;
		this.partsSum = partsSum;
		this.metalsSum = metalsSum;
		this.sliddoorSum = sliddoorSum;
		this.plasticSum = plasticSum;
		this.decorationSum = decorationSum;
		this.name = name;
		this.custName = custName;
		this.custPhone = custPhone;
		this.consignee = consignee;
		this.consigneePhone = consigneePhone;
		this.kunnr = kunnr;
		this.kunnrName = kunnrName;
	}
	
}
