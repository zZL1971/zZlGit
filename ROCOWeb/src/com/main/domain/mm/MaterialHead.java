package com.main.domain.mm;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 物料主表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "MATERIAL_HEAD")
public class MaterialHead extends UUIDEntity implements Serializable {
	
	private String materialColor;//传sap字段
	
	private String series;//主题系列，可以为空
	
	private String serialNumber;
	/**
	 * loadStatus从哪个页面进入 1:物料， 2：我的物品 显示非标产品 ,3:订单
	 */
	private String loadStatus;

	/**
	 * 可用状态 用于删除标识 可用：1 不可用：X
	 */
	private String status;

	/**
	 * 审核标示
	 */
	private String flowStatus;

	/**
	 * 长
	 */
	private String longDesc;
	/**
	 * 宽
	 */
	private String widthDesc;
	/**
	 * 高
	 */
	private String heightDesc;
	/**
	 * 属性配置描述
	 */
	private String propertyDesc;
	/**
	 * 绘图类型
	 */
	private String drawType;

	/**
	 * 附件类型
	 */
	private String fileType;
	/**
	 * 绘图评级
	 */
	private String drawGrade;

	/**
	 * IMOS服务器选择
	 */
	private String imosPath;
	/**
	 * 审核节点
	 */
	private String auditStatus;
	/**
	 * 是否标准 1:标准， 0：非标
	 */
	private String isStandard;
	/**
	 * 条件类型
	 */
	private String kschl;
	/**
	 * 销售组织
	 */
	private String vkorg;
	/**
	 * 分销渠道
	 */
	private String vtweg;
	/**
	 * 物料编码
	 */
	private String matnr;
	/**
	 * 物料说明(中文)
	 */
	private String maktx;
	/**
	 * 金额
	 */
	private Double kbetr;
	/**
	 * 平米价格
	 */
	private Double price;
	/**
	 *等级价格
	 */
	private Double kbetrDj;
	/**
	 *单位（货币）
	 */
	private String konwa;
	/**
	 * 单位（条件定价单位）
	 */
	private String kpein;
	/**
	 * 单位（条件单位）
	 */
	private String kmein;
	/**
	 * 计算类型（C：数量）
	 */
	private String krech;
	/**
	 * 有效从
	 */
	private Date datbi;
	/**
	 * 有效到
	 */
	private Date datab;
	/**
	 * 物料组
	 */
	private String matkl;

	/**
	 * 物料组，再次划分
	 */
	private String matkl2;
	/**
	 * 外部物料组
	 */
	private String extwg;
	/**
	 * 物料类型
	 */
	private String mtart;
	/**
	 * 基本单位
	 */
	private String meins;
	/**
	 * 毛重
	 */
	private Double brgew;
	/**
	 * 凈重
	 */
	private Double ntgew;
	/**
	 * 重量单位
	 */
	private String gewei;
	/**
	 * 体积
	 */
	private Double volum;
	/**
	 * 体积单位
	 */
	private String voleh;
	/**
	 * 产品组
	 */
	private String spart;
	/**
	 * 产品层次
	 */
	private String prdha;
	/**
	 * 产品层次描述
	 */
	private String vtext;
	/**
	 * 大小尺寸
	 */
	private String groes;

	/**
	 * VC是否可配置
	 */
	private String kzkfg;
	private String kzkfgdesc;
	private String ispic;

	/**
	 * 工厂
	 */
	private String weeks;

	private String color;

	private String style;
	/**
	 * 难度等级
	 */
	private String difLevel;

	private String textureOfMaterial;

	/**
	 * 是否冻结状态 ,A表示被冻结, 空表示没有冻结
	 */
	private String kbstat;
	/**
	 * 删除标记符, X表示已删除,空表示未删除
	 */
	private String loevmKo;
	/**
	 * 所用到板件厚度
	 */
	private String boardThickness;

	/**
	 * 是否包含异型
	 */
	private String hasPec;
	/**
	 * 销售分类
	 */
	private String saleFor;
	/**
	 * 数量
	 */
	private String zkwmeng;
	/**
	 * 交期天数
	 */
	private String zzjqts;
	/**
	 * 物料编码
	 */
	@Column(name = "MATNR", length = 30)
	public String getMatnr() {
		return matnr;
	}
	
	@Column(name = "materialColor", length = 80)
	public String getMaterialColor() {
		return materialColor;
	}

	public void setMaterialColor(String materialColor) {
		this.materialColor = materialColor;
	}

	@Column(name = "SERIES", length = 30)
	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}
	
	/**
	 * 物料编码
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	/**
	 * 物料类型
	 */
	@Column(name = "MTART", length = 10)
	public String getMtart() {
		return mtart;
	}

	/**
	 * 物料类型
	 */
	public void setMtart(String mtart) {
		this.mtart = mtart;
	}

	/**
	 * 工厂
	 */
	@Column(name = "WEEKS", length = 10)
	public String getWeeks() {
		return weeks;
	}

	/**
	 * 工厂
	 */
	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	/**
	 * 销售组织
	 */
	@Column(name = "VKORG", length = 10)
	public String getVkorg() {
		return vkorg;
	}

	/**
	 * 销售组织
	 */
	public void setVkorg(String vkorg) {
		this.vkorg = vkorg;
	}

	/**
	 * 分销渠道
	 */
	@Column(name = "VTWEG", length = 10)
	public String getVtweg() {
		return vtweg;
	}

	/**
	 * 分销渠道
	 */
	public void setVtweg(String vtweg) {
		this.vtweg = vtweg;
	}

	/**
	 * 物料说明(中文)
	 */
	@Column(name = "MAKTX", length = 80)
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
	 * 基本单位
	 */
	@Column(name = "MEINS", length = 10)
	public String getMeins() {
		return meins;
	}

	/**
	 * 基本单位
	 */
	public void setMeins(String meins) {
		this.meins = meins;
	}

	/**
	 * 物料组
	 */
	@Column(name = "MATKL", length = 20)
	public String getMatkl() {
		return matkl;
	}

	/**
	 * 物料组
	 */
	public void setMatkl(String matkl) {
		this.matkl = matkl;
	}

	/**
	 * 产品组
	 */
	@Column(name = "SPART", length = 10)
	public String getSpart() {
		return spart;
	}

	/**
	 * 产品组
	 */
	public void setSpart(String spart) {
		this.spart = spart;
	}

	/**
	 * 产品层次
	 */
	@Column(name = "PRDHA", length = 30)
	public String getPrdha() {
		return prdha;
	}

	/**
	 * 产品层次
	 */
	public void setPrdha(String prdha) {
		this.prdha = prdha;
	}

	/**
	 * 毛重
	 */
	@Column(name = "BRGEW")
	public Double getBrgew() {
		return brgew;
	}

	/**
	 * 毛重
	 */
	public void setBrgew(Double brgew) {
		this.brgew = brgew;
	}

	/**
	 * 凈重
	 */
	@Column(name = "NTGEW")
	public Double getNtgew() {
		return ntgew;
	}

	/**
	 * 凈重
	 */
	public void setNtgew(Double ntgew) {
		this.ntgew = ntgew;
	}

	/**
	 * 重量单位
	 */
	@Column(name = "GEWEI", length = 10)
	public String getGewei() {
		return gewei;
	}

	/**
	 * 重量单位
	 */
	public void setGewei(String gewei) {
		this.gewei = gewei;
	}

	/**
	 * 体积
	 */
	@Column(name = "VOLUM")
	public Double getVolum() {
		return volum;
	}

	/**
	 * 体积
	 */
	public void setVolum(Double volum) {
		this.volum = volum;
	}

	/**
	 * 体积单位
	 */
	@Column(name = "VOLEH", length = 10)
	public String getVoleh() {
		return voleh;
	}

	/**
	 * 体积单位
	 */
	public void setVoleh(String voleh) {
		this.voleh = voleh;
	}

	/**
	 * 大小尺寸
	 */
	@Column(name = "GROES", length = 70)
	public String getGroes() {
		return groes;
	}

	/**
	 * 大小尺寸
	 */
	public void setGroes(String groes) {
		this.groes = groes;
	}

	/**
	 * 文件信息
	 */
	private Set<MaterialFile> materialFileSet = new HashSet<MaterialFile>();
	/**
	 * 物料信息
	 */
	private Set<MaterialItem> materialItemSet = new HashSet<MaterialItem>();
	/**
	 * 定价条件
	 */
	// private Set<MaterialPriceCondition> materialPriceConditionSet = new
	// HashSet<MaterialPriceCondition>();
	/**
	 * 属性信息
	 */
	private Set<MaterialProperty> materialPropertySet = new HashSet<MaterialProperty>();
	/**
	 * 属性信息具体参数
	 */
	private Set<MaterialPropertyItem> materialPropertyItemSet = new HashSet<MaterialPropertyItem>();

	/**
	 * 文件信息
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "materialHead")
	public Set<MaterialFile> getMaterialFileSet() {
		return materialFileSet;
	}

	/**
	 * 文件信息
	 */
	public void setMaterialFileSet(Set<MaterialFile> materialFileSet) {
		this.materialFileSet = materialFileSet;
	}

	/**
	 * 物料信息
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "materialHead")
	public Set<MaterialItem> getMaterialItemSet() {
		return materialItemSet;
	}

	/**
	 * 物料信息
	 */
	public void setMaterialItemSet(Set<MaterialItem> materialItemSet) {
		this.materialItemSet = materialItemSet;
	}

	/**
	 * 属性信息
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "materialHead")
	public Set<MaterialProperty> getMaterialPropertySet() {
		return materialPropertySet;
	}

	/**
	 * 属性信息
	 */
	public void setMaterialPropertySet(Set<MaterialProperty> materialPropertySet) {
		this.materialPropertySet = materialPropertySet;
	}

	/**
	 * 属性信息具体参数
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "materialHead")
	public Set<MaterialPropertyItem> getMaterialPropertyItemSet() {
		return materialPropertyItemSet;
	}

	/**
	 * 属性信息具体参数
	 */
	public void setMaterialPropertyItemSet(
			Set<MaterialPropertyItem> materialPropertyItemSet) {
		this.materialPropertyItemSet = materialPropertyItemSet;
	}

	/**
	 * 属性配置描述
	 */
	@Column(name = "PROPERTY_DESC", length = 250)
	public String getPropertyDesc() {
		return propertyDesc;
	}

	/**
	 * 属性配置描述
	 */
	public void setPropertyDesc(String propertyDesc) {
		this.propertyDesc = propertyDesc;
	}

	/**
	 * 长
	 */
	@Column(name = "LONG_DESC", length = 30)
	public String getLongDesc() {
		return longDesc;
	}

	/**
	 * 长
	 */
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}

	/**
	 * 宽
	 */
	@Column(name = "WIDTH_DESC", length = 30)
	public String getWidthDesc() {
		return widthDesc;
	}

	/**
	 * 宽
	 */
	public void setWidthDesc(String widthDesc) {
		this.widthDesc = widthDesc;
	}

	/**
	 * 高
	 */
	@Column(name = "HEIGHT_DESC", length = 30)
	public String getHeightDesc() {
		return heightDesc;
	}

	/**
	 * 高
	 */
	public void setHeightDesc(String heightDesc) {
		this.heightDesc = heightDesc;
	}

	/**
	 * 绘图类型
	 * 
	 * @return
	 */
	@Column(name = "DRAW_TYPE", length = 32)
	public String getDrawType() {
		return drawType;
	}

	public void setDrawType(String drawType) {
		this.drawType = drawType;
	}

	/**
	 * 绘图评级
	 * 
	 * @return
	 */
	@Column(name = "DRAW_GRADE", length = 32)
	public String getDrawGrade() {
		return drawGrade;
	}

	public void setDrawGrade(String drawGrade) {
		this.drawGrade = drawGrade;
	}

	/**
	 * 审核节点
	 * 
	 * @return
	 */
	@Column(name = "AUDIT_STATUS", length = 32)
	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	/**
	 * 是否标准
	 * 
	 * @return
	 */
	@Column(name = "IS_STANDARD", length = 2)
	public String getIsStandard() {
		return isStandard;
	}

	public void setIsStandard(String isStandard) {
		this.isStandard = isStandard;
	}

	/**
	 * 定价条件
	 * 
	 * @return
	 */
	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy =
	// "materialHead")
	// public Set<MaterialPriceCondition> getMaterialPriceConditionSet() {
	// return materialPriceConditionSet;
	// }
	/**
	 * 定价条件
	 * 
	 * @return
	 */
	// public void setMaterialPriceConditionSet(
	// Set<MaterialPriceCondition> materialPriceConditionSet) {
	// this.materialPriceConditionSet = materialPriceConditionSet;
	// }
	/**
	 * 可用状态
	 */
	@Column(name = "STATUS", length = 2)
	public String getStatus() {
		return status;
	}

	/**
	 * 可用状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	@Transient
	public String getLoadStatus() {
		return loadStatus;
	}

	public void setLoadStatus(String loadStatus) {
		this.loadStatus = loadStatus;
	}

	@Transient
	public String getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	/**
	 * 条件类型
	 */
	@Column(name = "KSCHL", length = 10)
	public String getKschl() {
		return kschl;
	}

	/**
	 * 条件类型
	 */
	public void setKschl(String kschl) {
		this.kschl = kschl;
	}

	/**
	 * 金额
	 */
	@Column(name = "KBETR")
	public Double getKbetr() {
		return kbetr;
	}

	/**
	 * 金额
	 */
	public void setKbetr(Double kbetr) {
		this.kbetr = kbetr;
	}

	/**
	 *等级价格
	 */
	@Column(name = "KBETR_DJ")
	public Double getKbetrDj() {
		return kbetrDj;
	}

	/**
	 *等级价格
	 */
	public void setKbetrDj(Double kbetrDj) {
		this.kbetrDj = kbetrDj;
	}

	/**
	 *单位（货币）
	 */
	@Column(name = "KONWA", length = 10)
	public String getKonwa() {
		return konwa;
	}

	/**
	 *单位（货币）
	 */
	public void setKonwa(String konwa) {
		this.konwa = konwa;
	}

	/**
	 * 单位（条件定价单位）
	 */
	@Column(name = "KPEIN", length = 10)
	public String getKpein() {
		return kpein;
	}

	/**
	 * 单位（条件定价单位）
	 */
	public void setKpein(String kpein) {
		this.kpein = kpein;
	}

	/**
	 * 单位（条件单位）
	 */
	@Column(name = "KMEIN", length = 10)
	public String getKmein() {
		return kmein;
	}

	/**
	 * 单位（条件单位）
	 */
	public void setKmein(String kmein) {
		this.kmein = kmein;
	}

	/**
	 * 计算类型（C：数量）
	 */
	@Column(name = "KRECH", length = 10)
	public String getKrech() {
		return krech;
	}

	/**
	 * 计算类型（C：数量）
	 */
	public void setKrech(String krech) {
		this.krech = krech;
	}

	/**
	 * 有效从
	 */
	@Column(name = "DATBI")
	public Date getDatbi() {
		return datbi;
	}

	/**
	 * 有效从
	 */
	public void setDatbi(Date datbi) {
		this.datbi = datbi;
	}

	/**
	 * 有效到
	 */
	@Column(name = "DATAB")
	public Date getDatab() {
		return datab;
	}

	/**
	 * 有效到
	 */
	public void setDatab(Date datab) {
		this.datab = datab;
	}

	/**
	 * 外部物料组
	 */
	@Column(name = "EXTWG", length = 20)
	public String getExtwg() {
		return extwg;
	}

	/**
	 * 外部物料组
	 */
	public void setExtwg(String extwg) {
		this.extwg = extwg;
	}

	/**
	 * 产品层次描述
	 */
	@Column(name = "VTEXT", length = 80)
	public String getVtext() {
		return vtext;
	}

	/**
	 * 产品层次描述
	 */
	public void setVtext(String vtext) {
		this.vtext = vtext;
	}

	/** 附加B **/
	/**
	 * 颜色及材质：
	 */
	private String zzcomt;
	/**
	 * 产品用途区分：
	 */
	private String zzcpyt;
	/**
	 * 是否外购物料：
	 */
	private String zzwgfg;
	/**
	 * 投影面积平方数
	 */
	private Double zztyar;
	/**
	 * 板件展开面积平方数
	 */
	private Double zzzkar;
	/**
	 * 移门方数
	 */
	private Double zzymfs;
	/**
	 * 移门扇数
	 */
	private int zzymss;
	/**
	 * 产品等级
	 */
	private String zzcpdj;

	// ZZXSFS ZZXSFS 吸塑方数 QUAN 15 2
	private Double zzxsfs;

	/**
	 * 审核状态
	 */
	private String stateAudit;

	/**
	 * 颜色及材质：
	 */
	@Column(name = "ZZCOMT", length = 60)
	public String getZzcomt() {
		return zzcomt;
	}

	/**
	 * 颜色及材质：
	 */
	public void setZzcomt(String zzcomt) {
		this.zzcomt = zzcomt;
	}

	/**
	 * 产品用途区分：
	 */
	@Column(name = "ZZCPYT", length = 30)
	public String getZzcpyt() {
		return zzcpyt;
	}

	/**
	 * 产品用途区分：
	 */
	public void setZzcpyt(String zzcpyt) {
		this.zzcpyt = zzcpyt;
	}

	/**
	 * 是否外购物料：
	 */
	@Column(name = "ZZWGFG", length = 2)
	public String getZzwgfg() {
		return zzwgfg;
	}

	/**
	 * 是否外购物料：
	 */
	public void setZzwgfg(String zzwgfg) {
		this.zzwgfg = zzwgfg;
	}

	/**
	 * 投影面积平方数
	 */
	@Column(name = "ZZTYAR")
	public Double getZztyar() {
		return zztyar;
	}

	/**
	 * 投影面积平方数
	 */
	public void setZztyar(Double zztyar) {
		this.zztyar = zztyar;
	}

	/**
	 * 板件展开面积平方数
	 */
	@Column(name = "ZZZKAR")
	public Double getZzzkar() {
		return zzzkar;
	}

	/**
	 * 板件展开面积平方数
	 */
	public void setZzzkar(Double zzzkar) {
		this.zzzkar = zzzkar;
	}

	/**
	 * 移门方数
	 */
	@Column(name = "ZZYMFS")
	public Double getZzymfs() {
		return zzymfs;
	}

	/**
	 * 移门方数
	 */
	public void setZzymfs(Double zzymfs) {
		this.zzymfs = zzymfs;
	}

	/**
	 * 移门扇数
	 */
	@Column(name = "ZZYMSS")
	public int getZzymss() {
		return zzymss;
	}

	/**
	 * 移门扇数
	 */
	public void setZzymss(int zzymss) {
		this.zzymss = zzymss;
	}

	/**
	 * 产品等级
	 */
	@Column(name = "ZZCPDJ", length = 10)
	public String getZzcpdj() {
		return zzcpdj;
	}

	/**
	 * 产品等级
	 */
	public void setZzcpdj(String zzcpdj) {
		this.zzcpdj = zzcpdj;
	}

	/** 附加B **/
	@Column(name = "SERIAL_NUMBER", length = 30)
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/** 颜色 **/
	@Column(name = "COLOR", length = 30)
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	/** 材质 **/
	@Column(name = "TEXTURE_OF_MATERIAL", length = 30)
	public String getTextureOfMaterial() {
		return textureOfMaterial;
	}

	public void setTextureOfMaterial(String textureOfMaterial) {
		this.textureOfMaterial = textureOfMaterial;
	}

	/** 炸单价 **/
	@Column(name = "PRICE", length = 30)
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	/** 是否VC **/
	@Column(name = "KZKFG", length = 1)
	public String getKzkfg() {
		return kzkfg;
	}

	public void setKzkfg(String kzkfg) {
		this.kzkfg = kzkfg;
	}

	/** 文件类型 **/
	@Column(name = "FILE_TYPE", length = 1)
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/** 吸塑方数 QUAN 15 2 **/
	@Column(name = "ZZXSFS")
	public Double getZzxsfs() {
		return zzxsfs;
	}

	public void setZzxsfs(Double zzxsfs) {
		this.zzxsfs = zzxsfs;
	}

	@Transient
	public String getKzkfgdesc() {
		return kzkfgdesc;
	}

	public void setKzkfgdesc(String kzkfgdesc) {
		this.kzkfgdesc = kzkfgdesc;
	}

	@Transient
	public String getIspic() {
		return ispic;
	}

	public void setIspic(String ispic) {
		this.ispic = ispic;
	}

	/**
	 * 订单编号
	 */
	private String orderCode;

	@Transient
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/** IMOS 路径 **/
	@Column(name = "IMOS_PATH", length = 30)
	public String getImosPath() {
		return imosPath;
	}

	public void setImosPath(String imosPath) {
		this.imosPath = imosPath;
	}

	/**
	 * 审核状态
	 */
	@Transient
	public String getStateAudit() {
		return stateAudit;
	}

	public void setStateAudit(String stateAudit) {
		this.stateAudit = stateAudit;
	}

	/**
	 * 我的商品id
	 */
	private String myGoodsId;

	@Transient
	public String getMyGoodsId() {
		return myGoodsId;
	}

	public void setMyGoodsId(String myGoodsId) {
		this.myGoodsId = myGoodsId;
	}

	/**
	 * 冻结状态 : A表示被冻结 ,空为未冻结
	 * 
	 * @param kbstat
	 */
	public void setKbstat(String kbstat) {
		this.kbstat = kbstat;
	}

	@Column(name = "KBSTAT", length = 1)
	public String getKbstat() {
		return kbstat;
	}

	/**
	 * 删除标记 : X为已删除 , 空为未删除
	 * 
	 * @param loevmKo
	 */
	public void setLoevmKo(String loevmKo) {
		this.loevmKo = loevmKo;
	}

	@Column(name = "LOEVM_KO", length = 1)
	public String getLoevmKo() {
		return loevmKo;
	}

	@Column(name = "MATKL2", length = 20)
	public String getMatkl2() {
		return matkl2;
	}

	public void setMatkl2(String matkl2) {
		this.matkl2 = matkl2;
	}

	@Column(name = "BOARD_THICKNESS", length = 40)
	public String getBoardThickness() {
		return boardThickness;
	}

	public void setBoardThickness(String boardThickness) {
		this.boardThickness = boardThickness;
	}

	@Column(name = "HAS_PEC", length = 1)
	public String getHasPec() {
		return hasPec;
	}

	public void setHasPec(String hasPec) {
		this.hasPec = hasPec;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getDifLevel() {
		return difLevel;
	}

	public void setDifLevel(String difLevel) {
		this.difLevel = difLevel;
	}

	@Column(name = "SALE_FOR", length = 40)
	public String getSaleFor() {
		return saleFor;
	}

	public void setSaleFor(String saleFor) {
		this.saleFor = saleFor;
	}

	@Column(name = "ZKWMENG")
	public String getZkwmeng() {
		return zkwmeng;
	}

	public void setZkwmeng(String zkwmeng) {
		this.zkwmeng = zkwmeng;
	}
	@Column(name = "ZZJQTS")
	public String getZzjqts() {
		return zzjqts;
	}

	public void setZzjqts(String zzjqts) {
		this.zzjqts = zzjqts;
	}
}
