package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "SYS_DATA_DICT"/*
							 * , uniqueConstraints = {
							 * @UniqueConstraint(columnNames = {"属性名1", "属性名2"
							 * }) }
							 */)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler", "children" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SysDataDict extends UUIDEntity implements Serializable,Comparable<SysDataDict> {
	private static final long serialVersionUID = 8903762676869424108L;
	
	@NotEmpty(message = "{dataDict.keyVal.null}")
	@Length(min = 1, max = 30, message = "{dataDict.keyVal.length.illegal}")
	@Pattern(regexp = "[a-zA-Z0-9\\-_]{1,30}", message = "{dataDict.keyVal.illegal}")
	private String keyVal;

	@Length(min = 0, max = 4, message = "{dataDict.type.length.illegal}")
	private String type;
	
	private String icon;
	private int orderBy;
	
	private String descZhCn;
	private String descZhTw;
	private String descEnUs;
	
	private String typeDesc;//类型描述
	
	private String typeKey;//类型KEY
	
	private boolean stat=true;//启用禁用

	private SysTrieTree trieTree;
	 

	public SysDataDict() {
		super();
	}

	public SysDataDict(String keyVal, String descZhCn) {
		super();
		this.keyVal = keyVal;
		this.descZhCn = descZhCn;
	}

	public SysDataDict(String keyVal, String descZhCn,
			SysTrieTree trieTree) {
		super();
		this.keyVal = keyVal;
		this.descZhCn = descZhCn;
		this.trieTree = trieTree;
	}

	public SysDataDict(String keyVal, String type, String descZhCn,
			String descZhTw, String descEnUs) {
		super();
		this.keyVal = keyVal;
		this.type = type;
		this.descZhCn = descZhCn;
		this.descZhTw = descZhTw;
		this.descEnUs = descEnUs;
	}

	@Column(name = "KEY_VAL", length = 22)
	public String getKeyVal() {
		return keyVal;
	}

	public void setKeyVal(String keyVal) {
		this.keyVal = keyVal;
	}

	@Column(name = "TYPE", length = 4)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "DESC_ZH_CN")
	public String getDescZhCn() {
		return descZhCn;
	}

	public void setDescZhCn(String descZhCn) {
		this.descZhCn = descZhCn;
	}

	@Column(name = "DESC_ZH_TW")
	public String getDescZhTw() {
		return descZhTw;
	}

	public void setDescZhTw(String descZhTw) {
		this.descZhTw = descZhTw;
	}

	@Column(name = "DESC_EN_US")
	public String getDescEnUs() {
		return descEnUs;
	}

	public void setDescEnUs(String descEnUs) {
		this.descEnUs = descEnUs;
	}

	@Column(name = "ORDER_BY")
	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "trie_id", referencedColumnName = "id")
	public SysTrieTree getTrieTree() {
		return trieTree;
	}

	public void setTrieTree(SysTrieTree trieTree) {
		this.trieTree = trieTree;
	}

	@Override
	public String toString() {
		return "SysDataDict [Id="+super.id+",createUser="+super.createUser+",createTime="+super.createTime+",updateUser="+super.updateUser+",updateTime="+super.updateTime+",keyVal=" + keyVal + ", type=" + type
				+ ", orderBy=" + orderBy + ", descZhCn=" + descZhCn
				+ ", descZhTw=" + descZhTw + ", descEnUs=" + descEnUs+"]";
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Column(name = "TYPE_DESC")
	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	@Column(name = "TYPE_KEY")
	public String getTypeKey() {
		return typeKey;
	}

	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}

	@Column(name = "STAT")
	public boolean isStat() {
		return stat;
	}

	public void setStat(boolean stat) {
		this.stat = stat;
	}

	@Override
	public int compareTo(SysDataDict o) {
		return this.orderBy-o.orderBy;
	}

}
