package com.main.domain.cust;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.main.domain.sale.SaleHeader;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonDateSerializer;

@Entity
@Table(name = "TERMINAL_CLIENT")
public class TerminalClient extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4025101722250641957L;

	// 默认构造方法
	public TerminalClient() {
	}

	/**
	 * 订单头表
	 */
	private SaleHeader saleHeader;

	/**
	 * 姓名
	 */
	private String name1;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 年龄
	 */
	private Integer age;
	/**
	 * 生日
	 */
	private Date birthday;
	/**
	 * 身份证号码
	 */
	private String shenFenHao;

	/**
	 * 联系方式
	 */
	private String tel;

	/**
	 * 专卖店编码
	 */
	private String code;
	/**
	 * 专卖店名称
	 */
	private String name;
	/**
	 * 专卖店经手人
	 */
	private String jingShouRen;

	/**
	 * 安装户型
	 */
	private String huXing;

	/**
	 * 是否为样板
	 */
	private String isYangBan;
	/**
	 * 是否需要安装
	 */
	private String isAnZhuang;
	/**
	 * 是否有电梯
	 */
	private String isDianTi;
	/**
	 * 楼层
	 */
	private String floor;
	/**
	 * 安装地址
	 */
	private String address;
	/**
	 * 订单金额范围
	 */
	private String orderPayFw;

	/**
	 * 备注
	 */
	private String custRemarks;
	
	/**
	 * 客户Id
	 */
	private String custId;
	
	/**
	 * 投诉次数
	 */
	private String tousucishu;
	
	/**
	 * 安装日期
	 */
	private Date anzhuanDay;
	
	/**
	 * 问题出现
	 */
	private String problem;
	
	private String orderEvent;//活动政策标识
	@Column(name = "EVENT_STATUS",length = 10)
	public String getEventStatus() {
		return eventStatus;
	}
	@Column(name = "ORDER_EVENT",length = 10)
	public String getOrderEvent() {
		return orderEvent;
	}
	public void setOrderEvent(String orderEvent) {
		this.orderEvent = orderEvent;
	}
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}

	private String eventStatus;//优惠券使用状态，1未使用，2使用中，3已使用，4已过期

	public void setCustId(String custId) {
		this.custId = custId;
	}
	@Column(name = "CUST_ID")
	public String getCustId() {
		return custId;
	}
	/**
	 * 姓名
	 * 
	 * @return
	 */
	@Column(name = "NAME1")
	public String getName1() {
		return name1;
	}

	/**
	 * 姓名
	 * 
	 * @param name1
	 */
	public void setName1(String name1) {
		this.name1 = name1;
	}

	/**
	 * 性别
	 * 
	 * @return
	 */
	@Column(name = "SEX")
	public String getSex() {
		return sex;
	}

	/**
	 * 性别
	 * 
	 * @param sex
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * 年龄
	 * 
	 * @return
	 */
	@Column(name = "AGE")
	public Integer getAge() {
		return age;
	}

	/**
	 * 年龄
	 * 
	 * @param age
	 */
	public void setAge(Integer age) {
		this.age = age;
	}

	/**
	 * 生日
	 * 
	 * @return
	 */
	@Column(name = "BIRTHDAY")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * 生日
	 * 
	 * @param birthday
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * 身份证号码
	 * 
	 * @return
	 */
	@Column(name = "SHEN_FEN_HAO")
	public String getShenFenHao() {
		return shenFenHao;
	}

	/**
	 * 身份证号码
	 * 
	 * @param shenFenHao
	 */
	public void setShenFenHao(String shenFenHao) {
		this.shenFenHao = shenFenHao;
	}

	/**
	 * 联系方式
	 * 
	 * @return
	 */
	@Column(name = "TEL")
	public String getTel() {
		return tel;
	}

	/**
	 * 联系方式
	 * 
	 * @param tel
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * 专卖店编码
	 * 
	 * @return
	 */
	@Column(name = "CODE")
	public String getCode() {
		return code;
	}

	/**
	 * 专卖店编码
	 * 
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 专卖店名称
	 * 
	 * @return
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * 专卖店名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 专卖店经手人
	 * 
	 * @return
	 */
	@Column(name = "JING_SHOU_REN")
	public String getJingShouRen() {
		return jingShouRen;
	}

	/**
	 * 专卖店经手人
	 * 
	 * @param jingShouRen
	 */
	public void setJingShouRen(String jingShouRen) {
		this.jingShouRen = jingShouRen;
	}

	/**
	 * 安装户型
	 * 
	 * @return
	 */
	@Column(name = "HU_XING")
	public String getHuXing() {
		return huXing;
	}

	/**
	 * 安装户型
	 * 
	 * @param huXing
	 */
	public void setHuXing(String huXing) {
		this.huXing = huXing;
	}

	/**
	 * 是否为样板
	 * 
	 * @return
	 */
	@Column(name = "IS_YANG_BAN")
	public String getIsYangBan() {
		return isYangBan;
	}

	/**
	 * 是否为样板
	 * 
	 * @param isYangBan
	 */
	public void setIsYangBan(String isYangBan) {
		this.isYangBan = isYangBan;
	}
	
	/**
	 * 参考订单
	 */
	private String pOrderCode;

	/**
	 * 是否需要安装
	 * 
	 * @return
	 */
	@Column(name = "IS_AN_ZHUANG")
	public String getIsAnZhuang() {
		return isAnZhuang;
	}

	/**
	 * 是否需要安装
	 * 
	 * @param isAnZhuang
	 */
	public void setIsAnZhuang(String isAnZhuang) {
		this.isAnZhuang = isAnZhuang;
	}

	/**
	 * 是否有电梯
	 * 
	 * @return
	 */
	@Column(name = "IS_DIAN_TI")
	public String getIsDianTi() {
		return isDianTi;
	}

	/**
	 * 是否有电梯
	 * 
	 * @param isDianTi
	 */
	public void setIsDianTi(String isDianTi) {
		this.isDianTi = isDianTi;
	}

	/**
	 * 楼层
	 * 
	 * @return
	 */
	@Column(name = "FLOOR")
	public String getFloor() {
		return floor;
	}

	/**
	 * 楼层
	 * 
	 * @param floor
	 */
	public void setFloor(String floor) {
		this.floor = floor;
	}

	/**
	 * 安装地址
	 * 
	 * @return
	 */
	@Column(name = "ADDRESS")
	public String getAddress() {
		return address;
	}

	/**
	 * 安装地址
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 终端客户实体
	 * 
	 * @OneToOne：一对一关联 cascade：级联,它可以有有五个值可选,分别是： CascadeType.PERSIST：级联新建
	 *                 CascadeType.REMOVE : 级联删除 CascadeType.REFRESH：级联刷新
	 *                 CascadeType.MERGE ： 级联更新 CascadeType.ALL ： 以上全部四项
	 * @JoinColumn:外键字段saleId（映射主表SaleHeader的id字段）
	 * @return
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = " saleId")
	public SaleHeader getSaleHeader() {
		return saleHeader;
	}

	/**
	 * 订单头表
	 * 
	 * @param saleHeader
	 */
	public void setSaleHeader(SaleHeader saleHeader) {
		this.saleHeader = saleHeader;
	}

	/**
	 * 订单金额范围
	 */
	@Column(name = "ORDER_PAY_FW")
	public String getOrderPayFw() {
		return orderPayFw;
	}

	/**
	 * 订单金额范围
	 */
	public void setOrderPayFw(String orderPayFw) {
		this.orderPayFw = orderPayFw;
	}

	/**
	 * 备注
	 * 
	 * @return
	 */
	@Column(name = "CUST_REMARKS")
	public String getCustRemarks() {
		return custRemarks;
	}

	/**
	 * 备注
	 * 
	 * @param custRemarks
	 */
	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}
	
	public String getTousucishu() {
		return tousucishu;
	}
	public void setTousucishu(String tousucishu) {
		this.tousucishu = tousucishu;
	}
	
	public Date getAnzhuanDay() {
		return anzhuanDay;
	}
	public void setAnzhuanDay(Date anzhuanDay) {
		this.anzhuanDay = anzhuanDay;
	}
	
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	
	public String getpOrderCode() {
		return pOrderCode;
	}
	public void setpOrderCode(String pOrderCode) {
		this.pOrderCode = pOrderCode;
	}
	
}