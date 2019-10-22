package com.main.domain.sale;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.main.domain.cust.TerminalClient;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonDateSerializer;

@Entity
@Table(name = "SALE_HEADER")
public class SaleHeader extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1030034999114184251L;

	// 默认构造方法
	public SaleHeader() {
	}

	private Set<SaleItem> saleItemSet = new HashSet<SaleItem>();

	private Set<SaleOneCust> saleOneCustSet = new HashSet<SaleOneCust>();

	/**
	 * 终端客户实体
	 */
	private TerminalClient terminalClient;
	/**
	 * 销售附加
	 */
	private SaleFjHeader saleFjHeader;

	/**
	 * 订单编号
	 */
	private String orderCode;
	
	/**
	 * 订单流水号
	 */
	private String serialNumber;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * SAP订单编号
	 */
	private String sapOrderCode;
	
	/**
	 * SAP订单创建时间
	 */
	private Date sapCreateDate;
	/**
	 * 售达方（客户）
	 */
	private String shouDaFang;
	/**
	 * 送达方（物流园）
	 */
	private String songDaFang;
	/**
	 * 订单日期
	 */
	private Date orderDate;
	/**
	 * 订单状态
	 */
	private String orderStatus;
	/**
	 * 交期天数
	 */
	private String jiaoQiTianShu;
	private String jiaoQiStyle;
	private Date yuJiDate3;
	/**
	 * 预计出货日期
	 */
	private Date yuJiDate;
	
	/**
	 * 计划完工日期
	 */
	private Date yuJiDate2;
	/**
	 *实际出货日期
	 */
	private Date shiJiDate;
	/**
	 * 实际入库日期
	 */
	private Date shiJiDate2;
	/**
	 * 店面联系电话
	 */
	private String dianMianTel;
	/**
	 * 设计师联系电话
	 */
	private String designerTel;
	/**
	 * 处理时效
	 */
	private String handleTime;
	/**
	 * 订单总额
	 */
	private Double orderTotal;
	/**
	 * 支付方式
	 */
	private String payType;
	/**
	 * 付款条件
	 */
	private String fuFuanCond;
	/**
	 * 付款金额
	 */
	private Double fuFuanMoney;
	/**
	 * 活动类型
	 */
	private String huoDongType;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 是否样品
	 */
	private String isYp;
	/**
	 * 是否通知质检检测
	 */
	private String isQc;
	
	/**
	 * 是否通知客服检测
	 */
	private String isKf;
	/**
	 * 是否需要木架
	 */
	private String isMj;
	/**
	 * 父订单编号
	 */
	private String pOrderCode;
	/**
	 * 审绘员
	 */
	private String checkDrawUser;
	/**
	 * 审价员
	 */
	private String checkPriceUser;
	/**
	 * 财务确认员
	 */
	private String confirmFinanceUser;
	
	/**
	 * 拒绝原因
	 */
	private String abgru;
	
	/**
	 * 借贷项金额
	 */
	private Double loanAmount;
	
	/**
	 * 借贷项编号
	 */
	private String vgbel;
	
	/**
	 * 记录文件是否下载次数
	 */
	private Integer loadTime;
	
	
	/**
	 * 成本中心
	 */
	private String zzkostl;
	/**
	 * 内部订单号
	 */
	private String zzaufnr;
	
	/**
	 * 订单额外信息
	 */
	private SaleHeaderExtData saleHeaderExtData;
	
	/**
	 * 加急类型
	 */
	private String urgentType;
	
	
	/**
	 * 销售方式
	 */
	private String saleFor;
	
	/**
	 * 加急时间
	 */
	private Date urgentTime;
	
	/**
	 * 店(1店/N店)
	 */
	private String shop;
	
	/**
	 * 店分类(1店/N店)
	 */
	private String shopCls;
	
	private Set<SaleLogistics> saleLogisticsSet = new HashSet<SaleLogistics>();
	
	public String getZzkostl() {
		return zzkostl;
	}

	public void setZzkostl(String zzkostl) {
		this.zzkostl = zzkostl;
	}

	public String getZzaufnr() {
		return zzaufnr;
	}

	public void setZzaufnr(String zzaufnr) {
		this.zzaufnr = zzaufnr;
	}
	/**
	 * 记录文件是否下载次数
	 * @return
	 */
	@Column(name = "LOAD_TIME")
	public Integer getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(Integer loadTime) {
		this.loadTime = loadTime;
	}

	public Date getYuJiDate2() {
		return yuJiDate2;
	}

	public void setYuJiDate2(Date yuJiDate2) {
		this.yuJiDate2 = yuJiDate2;
	}

	/*
	 * 借贷项编号
	 */
	@Column(name = "VGBEL")
	public String getVgbel() {
		return vgbel;
	}

	public void setVgbel(String vgbel) {
		this.vgbel = vgbel;
	}

	/**
	 * 借贷项金额
	 */
	@Column(name = "LOAN_AMOUNT")
	public Double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}

	/**
	 * 拒绝原因
	 * @return
	 */
	@Column(name = "ABGRU")
	public String getAbgru() {
		return abgru;
	}

	public void setAbgru(String abgru) {
		this.abgru = abgru;
	}

	/**
	 * 订单编号
	 * 
	 * @return
	 */
	@Column(name = "ORDER_CODE")
	public String getOrderCode() {
		return orderCode;
	}

	/**
	 * 订单编号
	 * 
	 * @param orderCode
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/**
	 * 订单类型
	 * 
	 * @return
	 */
	@Column(name = "ORDER_TYPE")
	public String getOrderType() {
		return orderType;
	}

	/**
	 * 订单类型
	 * 
	 * @param orderType
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * 售达方（客户）
	 * 
	 * @return
	 */
	@Column(name = "SHOU_DA_FANG")
	public String getShouDaFang() {
		return shouDaFang;
	}

	/**
	 * 售达方（客户）
	 * 
	 * @param shouDaFang
	 */
	public void setShouDaFang(String shouDaFang) {
		this.shouDaFang = shouDaFang;
	}

	/**
	 * 送达方（物流园）
	 * 
	 * @return
	 */
	@Column(name = "SONG_DA_FANG")
	public String getSongDaFang() {
		return songDaFang;
	}

	/**
	 * 送达方（物流园）
	 * 
	 * @param songDaFang
	 */
	public void setSongDaFang(String songDaFang) {
		this.songDaFang = songDaFang;
	}

	/**
	 * 订单日期
	 * 
	 * @return
	 */
	@Column(name = "ORDER_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getOrderDate() {
		return orderDate;
	}

	/**
	 * 订单日期
	 * 
	 * @param orderDate
	 */
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	/**
	 * 订单状态
	 * 
	 * @return
	 */
	@Column(name = "ORDER_STATUS")
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * 订单状态
	 * 
	 * @param orderStatus
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * 交期天数
	 * 
	 * @return
	 */
	@Column(name = "JIAO_QI_TIAN_SHU")
	public String getJiaoQiTianShu() {
		return jiaoQiTianShu;
	}

	/**
	 * 交期天数
	 * 
	 * @param jiaoQiTianShu
	 */
	public void setJiaoQiTianShu(String jiaoQiTianShu) {
		this.jiaoQiTianShu = jiaoQiTianShu;
	}
	/**
	 * 交期类型
	 * @return
	 */
	@Column(name = "JIAO_QI_STYLE")
	public String getJiaoQiStyle() {
		return jiaoQiStyle;
	}

	/**
	 * 交期类型
	 * @return
	 */
	public void setJiaoQiStyle(String jiaoQiStyle) {
		this.jiaoQiStyle = jiaoQiStyle;
	}
	

	@Column(name = "YU_JI_DATE3")
	public Date getYuJiDate3() {
		return yuJiDate3;
	}

	public void setYuJiDate3(Date yuJiDate3) {
		this.yuJiDate3 = yuJiDate3;
	}

	/**
	 * 预计出货日期
	 * 
	 * @return
	 */
	@Column(name = "YU_JI_DATE")
	public Date getYuJiDate() {
		return yuJiDate;
	}

	/**
	 * 预计出货日期
	 * 
	 * @param yuJiDate
	 */
	public void setYuJiDate(Date yuJiDate) {
		this.yuJiDate = yuJiDate;
	}

	/**
	 * 实际货日期
	 * 
	 * @return
	 */
	@Column(name = "SHI_JI_DATE")
	public Date getShiJiDate() {
		return shiJiDate;
	}

	/**
	 * 
	 * 实际货日期
	 * 
	 * @param shiJiDate
	 */
	public void setShiJiDate(Date shiJiDate) {
		this.shiJiDate = shiJiDate;
	}

	/**
	 * 实际货日期2
	 * 
	 * @return
	 */
	@Column(name = "SHI_JI_DATE2")
	public Date getShiJiDate2() {
		return shiJiDate2;
	}

	/**
	 * 实际货日期2
	 * 
	 * @param shiJiDate2
	 */
	public void setShiJiDate2(Date shiJiDate2) {
		this.shiJiDate2 = shiJiDate2;
	}

	/**
	 * 店面联系电话
	 * 
	 * @return
	 */
	@Column(name = "DIAN_MIAN_TEL")
	public String getDianMianTel() {
		return dianMianTel;
	}

	/**
	 * 店面联系电话
	 * 
	 * @param dianMianTel
	 */
	public void setDianMianTel(String dianMianTel) {
		this.dianMianTel = dianMianTel;
	}

	/**
	 * 设计师联系电话
	 * 
	 * @return
	 */
	@Column(name = "DESIGNER_TEL")
	public String getDesignerTel() {
		return designerTel;
	}

	/**
	 * 设计师联系电话
	 * 
	 * @param designerTel
	 */
	public void setDesignerTel(String designerTel) {
		this.designerTel = designerTel;
	}

	/**
	 * 处理时效
	 * 
	 * @return
	 */
	@Column(name = "HANDLE_TIME")
	public String getHandleTime() {
		return handleTime;
	}

	/**
	 * 处理时效
	 * 
	 * @param handleTime
	 */
	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}

	/**
	 * 订单总额
	 * 
	 * @return
	 */
	@Column(name = "ORDER_TOTAL")
	public Double getOrderTotal() {
		return orderTotal;
	}

	/**
	 * 订单总额
	 * 
	 * @param orderTotal
	 */
	public void setOrderTotal(Double orderTotal) {
		this.orderTotal = orderTotal;
	}

	/**
	 * 付款条件
	 * 
	 * @return
	 */
	@Column(name = "FU_FUAN_COND")
	public String getFuFuanCond() {
		return fuFuanCond;
	}

	/**
	 * 付款条件
	 * 
	 * @param fuFuanCond
	 */
	public void setFuFuanCond(String fuFuanCond) {
		this.fuFuanCond = fuFuanCond;
	}

	/**
	 * 付款金额
	 * 
	 * @return
	 */
	@Column(name = "FU_FUAN_MONEY")
	public Double getFuFuanMoney() {
		return fuFuanMoney;
	}

	/**
	 * 付款金额
	 * 
	 * @param fuFuanMoney
	 */
	public void setFuFuanMoney(Double fuFuanMoney) {
		this.fuFuanMoney = fuFuanMoney;
	}

	/**
	 * 订单头表
	 * 
	 * @OneToOne：一对一关联 mappedBy = "saleHeader"：意思是说这里的一对一配置参考了saleHeader
	 *                 saleHeader又是什么呢
	 *                 ?saleHeader是TerminalClient类中的getSaleHeader(
	 *                 ),注意不是TerminalClient类中的
	 *                 saleHeader属性,TerminalClient类中的OneToOne配置就是在getSaleHeader
	 *                 ()方法上面配的.
	 *                 如果TerminalClient类中的getSaleHeader()方法改成getIdSaleHeader
	 *                 (),其他不变的话, 这里就要写成：mappedBy = "idSaleHeader"
	 * @return
	 */
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "saleHeader")
	public TerminalClient getTerminalClient() {
		return terminalClient;
	}

	/**
	 * 终端客户实体
	 * 
	 * @param terminalClient
	 */
	public void setTerminalClient(TerminalClient terminalClient) {
		this.terminalClient = terminalClient;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "saleHeader")
	public Set<SaleItem> getSaleItemSet() {
		return saleItemSet;
	}

	public void setSaleItemSet(Set<SaleItem> saleItemSet) {
		this.saleItemSet = saleItemSet;
	}

	/**
	 * SAP订单编号
	 * 
	 * @return
	 */
	@Column(name = "SAP_ORDER_CODE")
	public String getSapOrderCode() {
		return sapOrderCode;
	}

	/**
	 * SAP订单编号
	 * 
	 * @param sapOrderCode
	 */
	public void setSapOrderCode(String sapOrderCode) {
		this.sapOrderCode = sapOrderCode;
	}

	/**
	 * 备注
	 * 
	 * @return
	 */
	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}

	/**
	 * 备注
	 * 
	 * @param remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * 支付方式
	 * 
	 * @return
	 */
	@Column(name = "PAY_TYPE")
	public String getPayType() {
		return payType;
	}

	/**
	 * 支付方式
	 * 
	 * @param payType
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}

	/**
	 * 活动类型
	 * 
	 * @return
	 */
	@Column(name = "HUO_DONG_TYPE")
	public String getHuoDongType() {
		return huoDongType;
	}

	/**
	 * 活动类型
	 * 
	 * @param huoDongType
	 */
	public void setHuoDongType(String huoDongType) {
		this.huoDongType = huoDongType;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "saleHeader")
	public Set<SaleOneCust> getSaleOneCustSet() {
		return saleOneCustSet;
	}

	public void setSaleOneCustSet(Set<SaleOneCust> saleOneCustSet) {
		this.saleOneCustSet = saleOneCustSet;
	}

	/**
	 * 是否样品
	 */
	@Column(name = "IS_YP")
	public String getIsYp() {
		return isYp;
	}

	/**
	 * 是否样品
	 */
	public void setIsYp(String isYp) {
		this.isYp = isYp;
	}

	/**
	 * 父订单编号
	 */
	@Column(name = "P_ORDER_CODE")
	public String getpOrderCode() {
		return pOrderCode;
	}

	/**
	 * 父订单编号
	 */
	public void setpOrderCode(String pOrderCode) {
		this.pOrderCode = pOrderCode;
	}

	/**
	 * 销售附加
	 */
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "saleHeader")
	public SaleFjHeader getSaleFjHeader() {
		return saleFjHeader;
	}

	/**
	 * 销售附加
	 */
	public void setSaleFjHeader(SaleFjHeader saleFjHeader) {
		this.saleFjHeader = saleFjHeader;
	}

	/**
	 * 审绘员
	 */
	@Column(name = "CHECK_DRAW_USER", length = 20)
	public String getCheckDrawUser() {
		return checkDrawUser;
	}

	/**
	 * 审绘员
	 */
	public void setCheckDrawUser(String checkDrawUser) {
		this.checkDrawUser = checkDrawUser;
	}

	/**
	 * 审价员
	 */
	@Column(name = "CHECK_PRICE_USER", length = 20)
	public String getCheckPriceUser() {
		return checkPriceUser;
	}

	/**
	 * 审价员
	 */
	public void setCheckPriceUser(String checkPriceUser) {
		this.checkPriceUser = checkPriceUser;
	}

	/**
	 * 财务确认员
	 */
	@Column(name = "CONFIRM_FINANCE_USER", length = 20)
	public String getConfirmFinanceUser() {
		return confirmFinanceUser;
	}

	/**
	 * 财务确认员
	 */
	public void setConfirmFinanceUser(String confirmFinanceUser) {
		this.confirmFinanceUser = confirmFinanceUser;
	}

	public Date getSapCreateDate() {
		return sapCreateDate;
	}

	public void setSapCreateDate(Date sapCreateDate) {
		this.sapCreateDate = sapCreateDate;
	}
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "saleHeader")
	public SaleHeaderExtData getSaleHeaderExtData() {
		return saleHeaderExtData;
	}

	public void setSaleHeaderExtData(SaleHeaderExtData saleHeaderExtData) {
		this.saleHeaderExtData = saleHeaderExtData;
	}

	@Column(name = "URGENT_TYPE",length = 20)
	public String getUrgentType() {
		return urgentType;
	}

	public void setUrgentType(String urgentType) {
		this.urgentType = urgentType;
	}
	
	@Column(name = "URGENT_TIME")
	public Date getUrgentTime() {
		return urgentTime;
	}

	public void setUrgentTime(Date urgentTime) {
		this.urgentTime = urgentTime;
	}
	
	@Column(name = "SALE_FOR", length = 40)
	public String getSaleFor() {
		return saleFor;
	}

	public void setSaleFor(String saleFor) {
		this.saleFor = saleFor;
	}
	
	@Column(name = "SHOP", length = 10)
	public String getShop() {
		return shop;
	}
	@Column(name = "SHOP_CLS", length = 10)
	public String getShopCls() {
		return shopCls;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public void setShopCls(String shopCls) {
		this.shopCls = shopCls;
	}
	
	@Column(name = "SERIAL_NUMBER", length = 50)
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * 物流信息 产品组
	 * @return
	 */
	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="saleHeader")
	public Set<SaleLogistics> getSaleLogisticsSet() {
		return saleLogisticsSet;
	}

	public void setSaleLogisticsSet(Set<SaleLogistics> saleLogisticsSet) {
		this.saleLogisticsSet = saleLogisticsSet;
	}

	@Column(name = "IS_QC")
	public String getIsQc() {
		return isQc;
	}

	public void setIsQc(String isQc) {
		this.isQc = isQc;
	}
	
	@Column(name = "IS_KF")
	public String getIsKf() {
		return isKf;
	}

	public void setIsKf(String isKf) {
		this.isKf = isKf;
	}

	@Column(name = "IS_MJ")
	public String getIsMj() {
		return isMj;
	}

	public void setIsMj(String isMj) {
		this.isMj = isMj;
	}
	
	
	
}