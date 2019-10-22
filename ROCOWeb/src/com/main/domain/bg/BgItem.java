package com.main.domain.bg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUID32Entity;

@Entity
@Table(name = "BG_ITEM")
public class BgItem extends UUID32Entity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4902703550566477078L;
	// 编号
	private String serialNumber;
	// 名称
	private String name;
	// 规格
	private String spec;
	// 面积
	private String area;
	// 单位
	private String unit;
	// 单价
	private Double unitPrice;
	// 数量
	private Double amount;
	// 金额
	private Double totalPrice;
	// 颜色
	private String colour;
	// 颜色描述
	private String colourDesc;
	// 报价结构描述
	private String itemDesc;
	// 备注
	private String remark;
	// 是否打折
	private String isSale;
	// 类型
	private String type;
	// 折扣
	private Double zheKou;
	// 折扣价
	private Double zheKouJia;
	// 投影面积
	private String touYingArea;
	// 订单状态
	private String status;
	/**
	 * 优先采购订单的项目号
	 */
	private String posex;
	/**
	 * 物料编码
	 */
	private String matnr;
	/**
	 * 物料说明(中文)
	 */
	private String maktx;
	/**
	 * 产品类型
	 */
	private String mtart;

	private BgHeader bgHeader;

	/**
	 * 物料id
	 */
	private String materialHeadId;
	/**
	 * 是否标准产品 1:标准产品，0：非标产品
	 */
	private String isStandard;
	/**
	 * 我的物品id
	 */
	private String myGoodsId;

	private String saleItemId;

	@Column(name = "SERIAL_NUMBER", length = 32)
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Column(name = "NAME", length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "UNIT", length = 10)
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Column(name = "UNIT_PRICE", length = 20)
	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Column(name = "SPEC", length = 50)
	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	@Column(name = "AREA", length = 30)
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "TOTAL_PRICE")
	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Column(name = "COLOUR")
	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	@Column(name = "COLOUR_DESC")
	public String getColourDesc() {
		return colourDesc;
	}

	public void setColourDesc(String colourDesc) {
		this.colourDesc = colourDesc;
	}

	@Column(name = "ITEM_DESC")
	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "IS_SALE")
	public String getIsSale() {
		return isSale;
	}

	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "ZHE_KOU")
	public Double getZheKou() {
		return zheKou;
	}

	public void setZheKou(Double zheKou) {
		this.zheKou = zheKou;
	}

	@Column(name = "ZHE_KOU_JIA")
	public Double getZheKouJia() {
		return zheKouJia;
	}

	public void setZheKouJia(Double zheKouJia) {
		this.zheKouJia = zheKouJia;
	}

	@Column(name = "TOU_YING_AREA")
	public String getTouYingArea() {
		return touYingArea;
	}

	public void setTouYingArea(String touYingArea) {
		this.touYingArea = touYingArea;
	}

	@Column(name = "STATUS")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public BgHeader getBgHeader() {
		return bgHeader;
	}

	public void setBgHeader(BgHeader bgHeader) {
		this.bgHeader = bgHeader;
	}

	@Column(name = "MATERIAL_HEAD_ID", length = 32)
	public String getMaterialHeadId() {
		return materialHeadId;
	}

	public void setMaterialHeadId(String materialHeadId) {
		this.materialHeadId = materialHeadId;
	}

	@Column(name = "is_Standard", length = 2)
	public String getIsStandard() {
		return isStandard;
	}

	public void setIsStandard(String isStandard) {
		this.isStandard = isStandard;
	}

	@Column(name = "SALE_ITEM_ID", length = 32)
	public String getSaleItemId() {
		return saleItemId;
	}

	public void setSaleItemId(String saleItemId) {
		this.saleItemId = saleItemId;
	}

	@Column(name = "MY_GOODS_ID", length = 32)
	public String getMyGoodsId() {
		return myGoodsId;
	}

	public void setMyGoodsId(String myGoodsId) {
		this.myGoodsId = myGoodsId;
	}

	/**
	 * 优先采购订单的项目号
	 */
	@Column(name = "POSEX")
	public String getPosex() {
		return posex;
	}

	/**
	 * 优先采购订单的项目号
	 */
	public void setPosex(String posex) {
		this.posex = posex;
	}

	/**
	 * 物料编码
	 */
	@Column(name = "MATNR")
	public String getMatnr() {
		return matnr;
	}

	/**
	 * 物料编码
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	/**
	 * 物料说明(中文)
	 */
	@Column(name = "MAKTX")
	public String getMaktx() {
		return maktx;
	}

	/**
	 * 物料说明(中文)
	 */
	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}

	/**
	 * 产品类型
	 */
	@Column(name = "MTART")
	public String getMtart() {
		return mtart;
	}

	/**
	 * 产品类型
	 */
	public void setMtart(String mtart) {
		this.mtart = mtart;
	}

}
