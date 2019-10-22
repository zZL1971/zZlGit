package com.main.domain.sys;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SYS_JOB_POOL")
public class SysJobPool extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5668053098371967L;
	
	private String bukrs;// 公司代码
	private String zuonr;// 订单号
	private String kunnr;// KUNNR售达方（客户）
	private Double netwr;// 订单总额
	private String sapStatus;// 生成SAP 号 状态 N：未生成，Y：生成成功，P：未全部完成
	private String jobStatus = "A";// 处理标志 A未处理 B处理中 C处理成功 D处理失败
	private Integer num = 0;// 处理次数
	private Date beginDate;// 处理开始时间
	private Date endDate;// 处理结束时间
	private String msg;// 处理信息
	private String sourceType;// 数据类型 A自动 B手工
	private String mark;// 备注
	private String jobType;// 任务类型 SALE_JOB：人工， CREDIT_JOB： 系统
	private Integer batch;// 批次
	private String procInstId;//任务流程
	private String isFreeze;//是否冻结
	public SysJobPool() {
		super();
	}

	public SysJobPool(String zuonr, String sourceType, String jobType) {
		super();
		this.zuonr = zuonr;
		this.sourceType = sourceType;
		this.jobType = jobType;
	}
	public SysJobPool(String zuonr, String jobStatus, String sourceType,
			String jobType) {
		super();
		this.zuonr = zuonr;
		this.jobStatus = jobStatus;
		this.sourceType = sourceType;
		this.jobType = jobType;
	}

	public SysJobPool(String zuonr, String jobStatus, String sourceType,
			String jobType,String sapStatus) {
		super();
		this.zuonr = zuonr;
		this.jobStatus = jobStatus;
		this.sourceType = sourceType;
		this.jobType = jobType;
		this.sapStatus=sapStatus;
	}

	@Column(name="BUKRS",length=10)
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	@Column(name="ZUONR",length=50)
	public String getZuonr() {
		return zuonr;
	}

	public void setZuonr(String zuonr) {
		this.zuonr = zuonr;
	}

	@Column(name="KUNNR",length=10)
	public String getKunnr() {
		return kunnr;
	}

	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}

	@Column(name="NETWR")
	public Double getNetwr() {
		return netwr;
	}

	public void setNetwr(Double netwr) {
		this.netwr = netwr;
	}

	@Column(name="JOB_STATUS",length=5)
	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@Column(name="NUM")
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	@Column(name="BEGIN_DATE")
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	@Column(name="END_DATE")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name="MSG",length=255)
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Column(name="SOURCE_TYPE",length=5)
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name="MARK",length=255)
	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	@Column(name="BATCH")
	public Integer getBatch() {
		return batch;
	}

	public void setBatch(Integer batch) {
		this.batch = batch;
	}

	@Column(name="SAP_STATUS",length=5)
	public String getSapStatus() {
		return sapStatus;
	}

	public void setSapStatus(String sapStatus) {
		this.sapStatus = sapStatus;
	}

	@Column(name="JOB_TYPE",length=15)
	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	@Column(name="PROC_INST_ID_",length=64)
	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	@Column(name="IS_FREEZE",length=2)
	public String getIsFreeze() {
		return isFreeze;
	}

	public void setIsFreeze(String isFreeze) {
		this.isFreeze = isFreeze;
	}

}