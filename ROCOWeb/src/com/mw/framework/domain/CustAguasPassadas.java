package com.mw.framework.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "CUST_TRADE_HEADER")
public class CustAguasPassadas  extends UUIDEntity implements Serializable {
/*	 {text:'付款企业编号',dataIndex:'kunnr',width:100,menuDisabled:true},
     {text:'付款方企业账号',dataIndex:'tradeCompanyId',width:100,menuDisabled:true},
     {text:'付款账号',dataIndex:'tradeAccNo',width:100,menuDisabled:true},
     {text:'交易日志号',dataIndex:'jrnNo',width:100,menuDisabled:true},
     {text:'交易日期',dataIndex:'tradeDate',width:100,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d',editor: {
            xtype: 'datefield',
            allowBlank: false,
            format: 'Y-m-d',//'m/d/Y'
            minValue: '2006-01-01' 
     }},
     {text:'经销商编号',dataIndex:'dealerNum',width:100,menuDisabled:true}*/
	private String tradeCompanyId;
	private String tradeAccNo;
	private String jrnNo;
	private String tradeDate;
	private String dealerNum;
	private Date tradeTime;
	private String tradeRemark;
	private String tradePostcript;
	private String tradeDescription;
	private Double tradeAmount;
	private String payer;
	private String resumo;
	private String statusNum;
	private String accName1;
	private String accno2;
	
	
	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}


	public CustAguasPassadas(String tradeCompanyId, String tradeAccNo,
			String jrnNo, String tradeDate, String dealerNum, Date tradeTime,
			String tradeRemark, String tradePostcript, String tradeDescription,
			Double tradeAmount, String payer, String resumo,String statusNum) {
		super();
		this.tradeCompanyId = tradeCompanyId;
		this.tradeAccNo = tradeAccNo;
		this.jrnNo = jrnNo;
		this.tradeDate = tradeDate;
		this.dealerNum = dealerNum;
		this.tradeTime = tradeTime;
		this.tradeRemark = tradeRemark;
		this.tradePostcript = tradePostcript;
		this.tradeDescription = tradeDescription;
		this.tradeAmount = tradeAmount;
		this.payer = payer;
		this.resumo = resumo;
		this.statusNum=statusNum;
	}

	
	public CustAguasPassadas(String tradeAccNo, String jrnNo, String tradeDate,
			Date tradeTime, Double tradeAmount,
			String statusNum,String payer,String accName1,String accno2) {
		super();
		this.tradeAccNo = tradeAccNo;
		this.jrnNo = jrnNo;
		this.tradeDate = tradeDate;
		this.tradeTime = tradeTime;
		this.tradeAmount = tradeAmount;
		this.statusNum = statusNum;
		this.payer = payer;
		this.accName1 = accName1;
		this.accno2 = accno2;
	}

	public String getTradeCompanyId() {
		return tradeCompanyId;
	}
	public void setTradeCompanyId(String tradeCompanyId) {
		this.tradeCompanyId = tradeCompanyId;
	}
	public String getAccName1() {
		return accName1;
	}

	public void setAccName1(String accName1) {
		this.accName1 = accName1;
	}

	public String getAccno2() {
		return accno2;
	}

	public void setAccno2(String accno2) {
		this.accno2 = accno2;
	}

	public String getTradeAccNo() {
		return tradeAccNo;
	}
	public void setTradeAccNo(String tradeAccNo) {
		this.tradeAccNo = tradeAccNo;
	}
	public String getJrnNo() {
		return jrnNo;
	}
	public void setJrnNo(String jrnNo) {
		this.jrnNo = jrnNo;
	}
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getDealerNum() {
		return dealerNum;
	}
	public void setDealerNum(String dealerNum) {
		this.dealerNum = dealerNum;
	}
	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getTradeRemark() {
		return tradeRemark;
	}

	public void setTradeRemark(String tradeRemark) {
		this.tradeRemark = tradeRemark;
	}

	public String getTradePostcript() {
		return tradePostcript;
	}

	public void setTradePostcript(String tradePostcript) {
		this.tradePostcript = tradePostcript;
	}

	public String getTradeDescription() {
		return tradeDescription;
	}

	public void setTradeDescription(String tradeDescription) {
		this.tradeDescription = tradeDescription;
	}

	public Double getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(Double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	@Column(name="resumo",length=255)
	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public String getStatusNum() {
		return statusNum;
	}

	public void setStatusNum(String statusNum) {
		this.statusNum = statusNum;
	}

	@Override
	public String toString() {
		return "CustAguasPassadas [accName1=" + accName1 + ", accno2=" + accno2
				+ ", dealerNum=" + dealerNum + ", jrnNo=" + jrnNo + ", payer="
				+ payer + ", resumo=" + resumo + ", statusNum=" + statusNum
				+ ", tradeAccNo=" + tradeAccNo + ", tradeAmount=" + tradeAmount
				+ ", tradeCompanyId=" + tradeCompanyId + ", tradeDate="
				+ tradeDate + ", tradeDescription=" + tradeDescription
				+ ", tradePostcript=" + tradePostcript + ", tradeRemark="
				+ tradeRemark + ", tradeTime=" + tradeTime + "]";
	}

	
}
