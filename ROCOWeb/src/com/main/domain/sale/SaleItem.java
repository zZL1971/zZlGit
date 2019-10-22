package com.main.domain.sale;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SALE_ITEM")
public class SaleItem extends UUIDEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2129469632681466038L;
	/**
	 * 优先采购订单的项目号
	 */
	private String posex;
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
	private int amount;
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
	// 交期天数
	private int jiaoQiTianShu;
	// 交期天数-对内
	private int jiaoQiTianShuInner;
	//罚单
	private String tackit;

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
	/**
	 * 物料id
	 */
	private String materialHeadId;
	/**
	 * 散件headid
	 */
	private String sanjianHeadId;
	/**
	 * 补件表id(客服补购&免费订单)
	 */
	private String bujianId;
	/**
	 * 订单类型(我的商品查询创建时的类型)
	 */
	private String ortype;
	/**
	 * 我的物品id
	 */
	private String myGoodsId;
	/**
	 * 是否标准产品 1:标准产品，0：非标产品
	 */
	private String isStandard;
	/**
	 * 是否存在CNC图片 1:是，0：否
	 */
	private String isCnc;

	/**
	 * 标准产品(带属性vc)价格 描述
	 */
	private String materialPropertyItemInfo;
	/**
	 * 标准产品(带属性vc)价格
	 */
	private Double materialPrice;

	private SaleHeader saleHeader;

	private String orderCodePosex;

	private String stateAudit;

	private String jdName;

	private String drawType;

	/**
	 * 拒绝原因
	 */
	private String abgru;
	/**
	 * 裂分行项目的母ID
	 */
	private String parentId;
	/**
	 * 风格
	 */
	private String style;
	
	/**
	 * 销售价格信息
	 */
	private Set<SaleItemPrice> saleItemPrices = new HashSet<SaleItemPrice>();

	/**
	 * sap 行项号
	 */
	private String sapCodePosex;
	/**
	 * sap编号
	 */
	private String sapCode;
	
	@Column(name = "SERIAL_NUMBER", length = 32)
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * 拒绝原因
	 * 
	 * @return
	 */
	@Column(name = "ABGRU")
	public String getAbgru() {
		return abgru;
	}

	public void setAbgru(String abgru) {
		this.abgru = abgru;
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
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
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
	public SaleHeader getSaleHeader() {
		return saleHeader;
	}

	public void setSaleHeader(SaleHeader saleHeader) {
		this.saleHeader = saleHeader;
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

	@Column(name = "is_cnc", length = 2)
	public String getIsCnc() {
		return isCnc;
	}

	public void setIsCnc(String isCnc) {
		this.isCnc = isCnc;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "saleItem")
	public Set<SaleItemPrice> getSaleItemPrices() {
		return saleItemPrices;
	}

	public void setSaleItemPrices(Set<SaleItemPrice> saleItemPrices) {
		this.saleItemPrices = saleItemPrices;
	}

	/**
	 * 优先采购订单的项目号
	 * 
	 * @return
	 */
	@Column(name = "POSEX", length = 32)
	public String getPosex() {
		return posex;
	}

	/**
	 * 优先采购订单的项目号
	 * 
	 * @param posex
	 */
	public void setPosex(String posex) {
		this.posex = posex;
	}

	/**
	 * 物料编码
	 */
	@Column(name = "MATNR", length = 32)
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
	@Column(name = "MAKTX", length = 32)
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
	 * 
	 * @return
	 */
	@Column(name = "MTART", length = 32)
	public String getMtart() {
		return mtart;
	}

	/**
	 * 产品类型
	 * 
	 * @param mtart
	 */
	public void setMtart(String mtart) {
		this.mtart = mtart;
	}

	@Column(name = "MATERIAL_PRICE")
	public Double getMaterialPrice() {
		return materialPrice;
	}

	public void setMaterialPrice(Double materialPrice) {
		this.materialPrice = materialPrice;
	}

	@Column(name = "MATERIAL_PROPERTY_ITEM_INFO", length = 500)
	public String getMaterialPropertyItemInfo() {
		return materialPropertyItemInfo;
	}

	public void setMaterialPropertyItemInfo(String materialPropertyItemInfo) {
		this.materialPropertyItemInfo = materialPropertyItemInfo;
	}

	@Column(name = "SANJIAN_HEAD_ID", length = 32)
	public String getSanjianHeadId() {
		return sanjianHeadId;
	}

	public void setSanjianHeadId(String sanjianHeadId) {
		this.sanjianHeadId = sanjianHeadId;
	}

	@Column(name = "MY_GOODS_ID", length = 32)
	public String getMyGoodsId() {
		return myGoodsId;
	}

	public void setMyGoodsId(String myGoodsId) {
		this.myGoodsId = myGoodsId;
	}

	@Column(name = "ORDER_CODE_POSEX", length = 50)
	public String getOrderCodePosex() {
		return orderCodePosex;
	}

	public void setOrderCodePosex(String orderCodePosex) {
		this.orderCodePosex = orderCodePosex;
	}

	@Column(name = "BUJIAN_ID", length = 32)
	public String getBujianId() {
		return bujianId;
	}

	public void setBujianId(String bujianId) {
		this.bujianId = bujianId;
	}

	@Column(name = "ORTYPE", length = 10)
	public String getOrtype() {
		return ortype;
	}

	public void setOrtype(String ortype) {
		this.ortype = ortype;
	}

	@Column(name = "STATE_AUDIT", length = 30)
	public String getStateAudit() {
		return stateAudit;
	}

	public void setStateAudit(String stateAudit) {
		this.stateAudit = stateAudit;
	}

	@Transient
	public String getJdName() {
		return jdName;
	}

	public void setJdName(String jdName) {
		this.jdName = jdName;
	}

	@Transient
	public String getDrawType() {
		return drawType;
	}

	public void setDrawType(String drawType) {
		this.drawType = drawType;
	}

	@Column(name = "PARENT_ID", length = 40)
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Column(name = "JIAO_QI_TIAN_SHU", length = 4)
	public int getJiaoQiTianShu() {
		return jiaoQiTianShu;
	}

	public void setJiaoQiTianShu(int jiaoQiTianShu) {
		this.jiaoQiTianShu = jiaoQiTianShu;
	}

	@Column(name = "JIAO_QI_TIAN_SHU_INNER", length = 4)
	public int getJiaoQiTianShuInner() {
		return jiaoQiTianShuInner;
	}

	public void setJiaoQiTianShuInner(int jiaoQiTianShuInner) {
		this.jiaoQiTianShuInner = jiaoQiTianShuInner;
	}

	@Column(name = "STYLE", length = 20)
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	@Column(name = "SAP_CODE_POSEX", length = 80)
	public String getSapCodePosex() {
		return sapCodePosex;
	}

	public void setSapCodePosex(String sapCodePosex) {
		this.sapCodePosex = sapCodePosex;
	}
	@Column(name = "SAP_CODE", length = 80)
	public String getSapCode() {
		return sapCode;
	}

	public void setSapCode(String sapCode) {
		this.sapCode = sapCode;
	}

	
	
	//罚单
	@Column(name = "TACKIT", length = 8)
	public String getTackit() {
		return tackit;
	}
	public void setTackit(String tackit) {
		this.tackit = tackit;
	}
	
}
