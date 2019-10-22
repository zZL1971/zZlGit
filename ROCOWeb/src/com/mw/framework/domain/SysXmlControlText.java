package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SYS_XML_CONTROL_TEXT")
public class SysXmlControlText extends UUIDEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8431307905073432076L;

	/* {xtype:'rownumberer',width:30,align:'right'},
	 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
	 {text:"是否启用",dataIndex:'stat',width:100,editor:statOr_,renderer: function(value,metadata,record){ 
			return statOr_.getStore().getById(value).get('text');
		}},
	 {text:"销售分类",dataIndex:'saleFor',width:100,editor:saleFor,renderer: function(value,metadata,record){ 
	 	    return saleFor.getStore().getById(value).get('text');
	 }},
	 {text:'文本编码',dataIndex:'textCode',width:110,menuDisabled:true},
	 {text:'类型',dataIndex:'type',width:120,menuDisabled:true},
	 {text:'类型描述',dataIndex:'typeDesc',width:180,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
	 {text:'控制文本(双击添加内容)',dataIndex:'text',width:200,menuDisabled:true,listeners:{
		 dblclick:function(){
			 Ext.create('SMSWeb.view.xml.AddXMLContextGrid',{
				 
			 }).show();
		 }
	 }},
	 {text:'文本描述',dataIndex:'textDesc',width:200,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
	 {text:'最近修改人',dataIndex:'updateUser',width:100,menuDisabled:true},
	 {text:'最近修改日期',dataIndex:'updateTime',width:130,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'},
	 {text:'文本创建日期',dataIndex:'createTime',width:130,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'}*/
	private String stat;
	
	private String saleFor;
	
	private Integer textCode;
	
	private String type;
	
	private String typeDesc;
	
	private String text;
	
	private String textDesc;
	
	private String counter;

	@Column(name = "STAT", length = 30)
	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
	@Column(name = "SALE_FOR", length = 30)
	public String getSaleFor() {
		return saleFor;
	}

	public void setSaleFor(String saleFor) {
		this.saleFor = saleFor;
	}
	@Column(name = "TEXT_CODE", length = 30)
	public Integer getTextCode() {
		return textCode;
	}

	public void setTextCode(Integer textCode) {
		this.textCode = textCode;
	}
	@Column(name = "TYPE", length = 30)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@Column(name = "TYPE_DESC", length = 30)
	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}
	@Column(name = "TEXT", length = 30)
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	@Column(name = "TEXT_DESC", length = 30)
	public String getTextDesc() {
		return textDesc;
	}

	public void setTextDesc(String textDesc) {
		this.textDesc = textDesc;
	}
	@Column(name = "COUNTER", length = 50)
	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}
	
}
