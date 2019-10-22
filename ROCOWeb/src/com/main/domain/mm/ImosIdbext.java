package com.main.domain.mm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.AssignedEntity;
/**
 * 物料信息-- 移门
 * @author Administrator
 *
 */
@Entity
@Table(name = "IMOS_IDBEXT")
public class ImosIdbext extends AssignedEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//订单编号
	public String orderid;
	
	//类型
	public Integer typ;

	//板件类型
	public Integer parttype;
	
	public String mainline;
	
	//名称1
	public String name;
	
	//名称2
	public String name2;
	
	//单个部件
	public String kp;
	
	//定义部件
	public String kms;
	
	//长度
	public Double length;
	
	//宽度
	public Double width;
	
	//厚度
	public Double thickness;
	
	//ID From Imos
	//public String id;
	
	//父ID
	public String parentid;
	
	//数量
	public Integer cnt;
	
	//PVC同色封边_06x22
	public String articleId;
	
	//条形码
	public String barcode;
	
	//流水号
	public String ncno;
	
	//正面条码
	public String cncBarcode1;
	
	//反面条码
	public String cncBarcode2;
	
	//门板开启方向
	public String hinge;
	
	//纹理 
	public String ispec;
	
	//纹理
	public String grid;
	
	//角度
	public Double gror;
	
	//封边号码
	public Integer edgeId=0;
	
	//封边类型  U长接，K短接，G斜接，D未定义
	public String edgeTrans;
	
	//先贴面后封边1先封边后贴面U未定义
	public String surfTrans;
	
	//价格
	public Double price;
	
	//重量
	public Double weight;
	
	//体积
	public Double area;
	
	//板件序列编码 
	public String idSerie;
	
	//ID描述
	public String idText;
	
	//ID流水号
	public String idNcno;
	
	//0没有NC程序1有NC程序
	public Double ncFlag;
	
	//bom标识 0没有BOM1有BOM 
	public Double bomFlag;
	//剪裁标识 0没有裁切单1有裁切单
	public Double cutFlag;
	
	//信息1
	public String info1;
	//信息2
	public String info2;
	//信息3
	public String info3;
	//信息4
	public String info4;
	//信息5
	public String info5;
	
	//第二校验码
	public String checksum2;
	
	//第一校验
	public String checksum;
	
	//颜色1
	public String color1;
	
	//颜色2
	public String color2;
	
	//板件ID
	public String idbgplId;
	
	//材料ID
	public String matid;
	
	//材料名
	public String matname;
	
	public String surftid;
	public String surftnam;
	public String surfbid;
	public String surfbnam;
	public String surftgrid;
	public Double surftgror;
	public String surfbgrid;
	
	public Double surfbgror;
	public Double surftlength;
	public Double surftwidth;
	public Double surftthickness;
	public Double surfblength;
	public Double surfbwidth;
	public Double surfbthickness;
	public Double mpeType;
	public String render;
	
	public String text1;
	public String text2;
	public Integer exportStatus;
	public String exportTimestamp;
	public String order_id;
	public String supplier;
	public String employee;
	public String commission;
	public String customer;
	public String datecreate;
	public String shippingDate;
	public String deliveryDate;
	public String delivName1;
	public String delivName2;
	public String delivStreet;
	public String delivPostboxTown;
	public String delivCountry;
	public String xxx;
	
	//开料长度
	public Double clength;
	
	//开料宽度
	public Double cwidth;
	
	//厚度
	public Double cthickness;
	
	//是否创建物料
	private String zmtif;
	
	//销售组织
	private String vkorg;
	
	//分销渠道
	private String vtweg;
	
	//物料类型
	private String mtart;
	
	//物料组
	private String matkl;
	
	private String tecSide;
	
	private String tecGroove;
	
	private String tecPore;
	
	private String tecXlk;
	
	private String tecTrough;
	
	private String cabinetName;
	
	private String tecPoreNums;
	@Column(name = "TEC_SIDE")
	public String getTecSide() {
		return tecSide;
	}
	public void setTecSide(String tecSide) {
		this.tecSide = tecSide;
	}
	@Column(name = "TEC_GROOVE")
	public String getTecGroove() {
		return tecGroove;
	}
	public void setTecGroove(String tecGroove) {
		this.tecGroove = tecGroove;
	}
	@Column(name = "TEC_PORE")
	public String getTecPore() {
		return tecPore;
	}
	public void setTecPore(String tecPore) {
		this.tecPore = tecPore;
	}
	@Column(name = "TEC_XLK")
	public String getTecXlk() {
		return tecXlk;
	}
	public void setTecXlk(String tecXlk) {
		this.tecXlk = tecXlk;
	}
	@Column(name = "TEC_TROUGH")
	public String getTecTrough() {
		return tecTrough;
	}
	public void setTecTrough(String tecTrough) {
		this.tecTrough = tecTrough;
	}
	
	@Column(name = "TEC_PORE_NUMS")
	public String getTecPoreNums() {
		return tecPoreNums;
	}
	public void setTecPoreNums(String tecPoreNums) {
		this.tecPoreNums = tecPoreNums;
	}
	
	@Column(name = "CABINET_NAME")
	public String getCabinetName() {
		return cabinetName;
	}
	public void setCabinetName(String cabinetName) {
		this.cabinetName = cabinetName;
	}
	@Column(name = "ORDERID",length=30)
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	
	@Column(name = "TYP")
	public Integer getTyp() {
		return typ;
	}
	public void setTyp(Integer typ) {
		this.typ = typ;
	}
	
	@Column(name = "PARTTYPE")
	public Integer getParttype() {
		return parttype;
	}
	public void setParttype(Integer parttype) {
		this.parttype = parttype;
	}
	
	@Column(name = "MAINLINE",length=30)
	public String getMainline() {
		return mainline;
	}
	public void setMainline(String mainline) {
		this.mainline = mainline;
	}
	
	@Column(name = "NAME",length=30)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "NAME2",length=80)
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	
	@Column(name = "KP",length=80)
	public String getKp() {
		return kp;
	}
	public void setKp(String kp) {
		this.kp = kp;
	}
	
	@Column(name = "KMS",length=80)
	public String getKms() {
		return kms;
	}
	public void setKms(String kms) {
		this.kms = kms;
	}
	
	@Column(name = "LENGTH")
	public Double getLength() {
		return length;
	}
	public void setLength(Double length) {
		this.length = length;
	}
	
	@Column(name = "WIDTH")
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	
	@Column(name = "THICKNESS")
	public Double getThickness() {
		return thickness;
	}
	public void setThickness(Double thickness) {
		this.thickness = thickness;
	}
	
	@Column(name = "PARENTID",length=30)
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
	@Column(name = "CNT")
	public Integer getCnt() {
		return cnt;
	}
	public void setCnt(Integer cnt) {
		this.cnt = cnt;
	}
	
	@Column(name = "ARTICLE_ID",length=50)
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	
	@Column(name = "BARCODE",length=30)
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	@Column(name = "NCNO",length=12)
	public String getNcno() {
		return ncno;
	}
	public void setNcno(String ncno) {
		this.ncno = ncno;
	}
	
	@Column(name = "CNC_BARCODE1",length=30)
	public String getCncBarcode1() {
		return cncBarcode1;
	}
	public void setCncBarcode1(String cncBarcode1) {
		this.cncBarcode1 = cncBarcode1;
	}
	
	@Column(name = "CNC_BARCODE2",length=30)
	public String getCncBarcode2() {
		return cncBarcode2;
	}
	public void setCncBarcode2(String cncBarcode2) {
		this.cncBarcode2 = cncBarcode2;
	}
	
	@Column(name = "HINGE",length=12)
	public String getHinge() {
		return hinge;
	}
	public void setHinge(String hinge) {
		this.hinge = hinge;
	}
	
	@Column(name = "ISPEC",length=3)
	public String getIspec() {
		return ispec;
	}
	public void setIspec(String ispec) {
		this.ispec = ispec;
	}
	
	@Column(name = "GRID",length=3)
	public String getGrid() {
		return grid;
	}
	public void setGrid(String grid) {
		this.grid = grid;
	}
	
	@Column(name = "GROR")
	public Double getGror() {
		return gror;
	}
	public void setGror(Double gror) {
		this.gror = gror;
	}
	
	@Column(name = "EDGE_ID")
	public Integer getEdgeId() {
		return edgeId;
	}
	public void setEdgeId(Integer edgeId) {
		this.edgeId = edgeId;
	}
	
	@Column(name = "EDGE_TRANS",length=2)
	public String getEdgeTrans() {
		return edgeTrans;
	}
	public void setEdgeTrans(String edgeTrans) {
		this.edgeTrans = edgeTrans;
	}
	
	@Column(name = "SURF_TRANS",length=2)
	public String getSurfTrans() {
		return surfTrans;
	}
	public void setSurfTrans(String surfTrans) {
		this.surfTrans = surfTrans;
	}
	
	@Column(name = "PRICE")
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	@Column(name = "WEIGHT")
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	@Column(name = "AREA")
	public Double getArea() {
		return area;
	}
	public void setArea(Double area) {
		this.area = area;
	}
	
	@Column(name = "ID_SERIE",length=64)
	public String getIdSerie() {
		return idSerie;
	}
	public void setIdSerie(String idSerie) {
		this.idSerie = idSerie;
	}
	
	@Column(name = "ID_TEXT",length=64)
	public String getIdText() {
		return idText;
	}
	public void setIdText(String idText) {
		this.idText = idText;
	}
	
	@Column(name = "ID_NCNO",length=32)
	public String getIdNcno() {
		return idNcno;
	}
	public void setIdNcno(String idNcno) {
		this.idNcno = idNcno;
	}
	
	@Column(name = "NC_FLAG")
	public Double getNcFlag() {
		return ncFlag;
	}
	public void setNcFlag(Double ncFlag) {
		this.ncFlag = ncFlag;
	}
	
	@Column(name = "BOM_FLAG")
	public Double getBomFlag() {
		return bomFlag;
	}
	public void setBomFlag(Double bomFlag) {
		this.bomFlag = bomFlag;
	}
	
	@Column(name = "CUT_FLAG")
	public Double getCutFlag() {
		return cutFlag;
	}
	public void setCutFlag(Double cutFlag) {
		this.cutFlag = cutFlag;
	}
	
	@Column(name = "INFO1",length=80)
	public String getInfo1() {
		return info1;
	}
	public void setInfo1(String info1) {
		this.info1 = info1;
	}
	
	@Column(name = "INFO2",length=80)
	public String getInfo2() {
		return info2;
	}
	public void setInfo2(String info2) {
		this.info2 = info2;
	}
	
	@Column(name = "INFO3",length=80)
	public String getInfo3() {
		return info3;
	}
	public void setInfo3(String info3) {
		this.info3 = info3;
	}
	
	@Column(name = "INFO4",length=80)
	public String getInfo4() {
		return info4;
	}
	public void setInfo4(String info4) {
		this.info4 = info4;
	}
	
	@Column(name = "INFO5",length=80)
	public String getInfo5() {
		return info5;
	}
	public void setInfo5(String info5) {
		this.info5 = info5;
	}
	
	@Column(name = "CHECKSUM2",length=30)
	public String getChecksum2() {
		return checksum2;
	}
	public void setChecksum2(String checksum2) {
		this.checksum2 = checksum2;
	}
	
	@Column(name = "CHECKSUM",length=30)
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	@Column(name = "COLOR1",length=60)
	public String getColor1() {
		return color1;
	}
	public void setColor1(String color1) {
		this.color1 = color1;
	}
	
	@Column(name = "COLOR2",length=60)
	public String getColor2() {
		return color2;
	}
	public void setColor2(String color2) {
		this.color2 = color2;
	}
	
	@Column(name = "IDBGPL_ID",length=30)
	public String getIdbgplId() {
		return idbgplId;
	}
	public void setIdbgplId(String idbgplId) {
		this.idbgplId = idbgplId;
	}
	
	@Column(name = "MATID",length=32)
	public String getMatid() {
		return matid;
	}
	public void setMatid(String matid) {
		this.matid = matid;
	}
	
	@Column(name = "MATNAME",length=50)
	public String getMatname() {
		return matname;
	}
	public void setMatname(String matname) {
		this.matname = matname;
	}
	
	@Column(name = "SURFTID",length=32)
	public String getSurftid() {
		return surftid;
	}
	public void setSurftid(String surftid) {
		this.surftid = surftid;
	}
	
	@Column(name = "SURFTNAM",length=50)
	public String getSurftnam() {
		return surftnam;
	}
	public void setSurftnam(String surftnam) {
		this.surftnam = surftnam;
	}
	
	@Column(name = "SURFBID",length=32)
	public String getSurfbid() {
		return surfbid;
	}
	public void setSurfbid(String surfbid) {
		this.surfbid = surfbid;
	}
	
	@Column(name = "SURFBNAM",length=50)
	public String getSurfbnam() {
		return surfbnam;
	}
	public void setSurfbnam(String surfbnam) {
		this.surfbnam = surfbnam;
	}
	
	@Column(name = "SURFTGRID",length=3)
	public String getSurftgrid() {
		return surftgrid;
	}
	public void setSurftgrid(String surftgrid) {
		this.surftgrid = surftgrid;
	}
	
	@Column(name = "SURFTGROR")
	public Double getSurftgror() {
		return surftgror;
	}
	public void setSurftgror(Double surftgror) {
		this.surftgror = surftgror;
	}
	
	@Column(name = "SURFBGRID",length=3)
	public String getSurfbgrid() {
		return surfbgrid;
	}
	public void setSurfbgrid(String surfbgrid) {
		this.surfbgrid = surfbgrid;
	}
	
	@Column(name = "SURFBGROR")
	public Double getSurfbgror() {
		return surfbgror;
	}
	public void setSurfbgror(Double surfbgror) {
		this.surfbgror = surfbgror;
	}
	
	@Column(name = "SURFTLENGTH")
	public Double getSurftlength() {
		return surftlength;
	}
	public void setSurftlength(Double surftlength) {
		this.surftlength = surftlength;
	}
	
	@Column(name = "SURFTWIDTH")
	public Double getSurftwidth() {
		return surftwidth;
	}
	public void setSurftwidth(Double surftwidth) {
		this.surftwidth = surftwidth;
	}
	
	@Column(name = "SURFTTHICKNESS")
	public Double getSurftthickness() {
		return surftthickness;
	}
	public void setSurftthickness(Double surftthickness) {
		this.surftthickness = surftthickness;
	}
	
	@Column(name = "SURFBLENGTH")
	public Double getSurfblength() {
		return surfblength;
	}
	public void setSurfblength(Double surfblength) {
		this.surfblength = surfblength;
	}
	
	@Column(name = "SURFBWIDTH")
	public Double getSurfbwidth() {
		return surfbwidth;
	}
	public void setSurfbwidth(Double surfbwidth) {
		this.surfbwidth = surfbwidth;
	}
	
	@Column(name = "SURFBTHICKNESS")
	public Double getSurfbthickness() {
		return surfbthickness;
	}
	public void setSurfbthickness(Double surfbthickness) {
		this.surfbthickness = surfbthickness;
	}
	
	@Column(name = "MPE_TYPE")
	public Double getMpeType() {
		return mpeType;
	}
	public void setMpeType(Double mpeType) {
		this.mpeType = mpeType;
	}
	
	@Column(name = "RENDER",length=64)
	public String getRender() {
		return render;
	}
	public void setRender(String render) {
		this.render = render;
	}
	
	@Column(name = "TEXT1",length=80)
	public String getText1() {
		return text1;
	}
	public void setText1(String text1) {
		this.text1 = text1;
	}
	
	@Column(name = "TEXT2",length=80)
	public String getText2() {
		return text2;
	}
	public void setText2(String text2) {
		this.text2 = text2;
	}
	
	@Column(name = "EXPORT_STATUS")
	public Integer getExportStatus() {
		return exportStatus;
	}
	public void setExportStatus(Integer exportStatus) {
		this.exportStatus = exportStatus;
	}
	
	@Column(name = "EXPORT_TIMESTAMP",length=30)
	public String getExportTimestamp() {
		return exportTimestamp;
	}
	public void setExportTimestamp(String exportTimestamp) {
		this.exportTimestamp = exportTimestamp;
	}
	
	@Column(name = "ORDER_ID",length=50)
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String orderId) {
		order_id = orderId;
	}
	
	@Column(name = "SUPPLIER",length=255)
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	@Column(name = "EMPLOYEE",length=30)
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	
	@Column(name = "COMMISSION",length=30)
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	
	@Column(name = "CUSTOMER",length=30)
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	
	@Column(name = "DATECREATE",length=30)
	public String getDatecreate() {
		return datecreate;
	}
	public void setDatecreate(String datecreate) {
		this.datecreate = datecreate;
	}
	
	@Column(name = "SHIPPING_DATE",length=30)
	public String getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	
	@Column(name = "DELIVERY_DATE",length=30)
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	@Column(name = "DELIV_NAME1",length=30)
	public String getDelivName1() {
		return delivName1;
	}
	public void setDelivName1(String delivName1) {
		this.delivName1 = delivName1;
	}
	
	@Column(name = "DELIV_NAME2",length=30)
	public String getDelivName2() {
		return delivName2;
	}
	public void setDelivName2(String delivName2) {
		this.delivName2 = delivName2;
	}
	
	@Column(name = "DELIV_STREET",length=30)
	public String getDelivStreet() {
		return delivStreet;
	}
	public void setDelivStreet(String delivStreet) {
		this.delivStreet = delivStreet;
	}
	
	@Column(name = "DELIV_POSTBOX_TOWN",length=30)
	public String getDelivPostboxTown() {
		return delivPostboxTown;
	}
	public void setDelivPostboxTown(String delivPostboxTown) {
		this.delivPostboxTown = delivPostboxTown;
	}
	
	@Column(name = "DELIV_COUNTRY",length=30)
	public String getDelivCountry() {
		return delivCountry;
	}
	public void setDelivCountry(String delivCountry) {
		this.delivCountry = delivCountry;
	}
	
	@Column(name = "XXX",length=30)
	public String getXxx() {
		return xxx;
	}
	public void setXxx(String xxx) {
		this.xxx = xxx;
	}
	@Column(name = "CLENGTH")
	public Double getClength() {
		return clength;
	}
	public void setClength(Double clength) {
		this.clength = clength;
	}
	@Column(name = "CWIDTH")
	public Double getCwidth() {
		return cwidth;
	}
	public void setCwidth(Double cwidth) {
		this.cwidth = cwidth;
	}
	@Column(name = "CTHICKNESS")
	public Double getCthickness() {
		return cthickness;
	}
	public void setCthickness(Double cthickness) {
		this.cthickness = cthickness;
	}
	public String getZmtif() {
		return zmtif;
	}
	public void setZmtif(String zmtif) {
		this.zmtif = zmtif;
	}
	public String getVkorg() {
		return vkorg;
	}
	public void setVkorg(String vkorg) {
		this.vkorg = vkorg;
	}
	public String getVtweg() {
		return vtweg;
	}
	public void setVtweg(String vtweg) {
		this.vtweg = vtweg;
	}
	public String getMtart() {
		return mtart;
	}
	public void setMtart(String mtart) {
		this.mtart = mtart;
	}
	public String getMatkl() {
		return matkl;
	}
	public void setMatkl(String matkl) {
		this.matkl = matkl;
	}
	
}
