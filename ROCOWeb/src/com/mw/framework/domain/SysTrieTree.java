package com.mw.framework.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SYS_TRIE_TREE"/*
							 * , uniqueConstraints = {
							 * 
							 * @UniqueConstraint(columnNames = {"属性名1", "属性名2"
							 * }) }
							 */)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler","parent","dataDicts" }, ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SysTrieTree extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = 8463679064123746883L;
	@NotEmpty(message = "{dataDic.keyVal.null}")
	@Length(min = 1, max = 24, message = "{dataDic.keyVal.length.illegal}")
	@Pattern(regexp = "[a-zA-Z0-9\\-_]{1,24}", message = "{dataDic.keyVal.illegal}")
	private String keyVal;
	private String type;
	private int orderBy;
	private String icon;
	private String descZhCn;
	private String descZhTw;
	private String descEnUs;

	private String pid;
	private SysTrieTree parent;
	private Set<SysTrieTree> children = new HashSet<SysTrieTree>();
	private Set<SysDataDict> dataDicts = new HashSet<SysDataDict>();

	public SysTrieTree() {
		super();
	}

	public SysTrieTree(String keyVal, String descZhCn) {
		super();
		this.keyVal = keyVal;
		this.descZhCn = descZhCn;
	}

	public SysTrieTree(String keyVal, String descZhCn, SysTrieTree parent) {
		super();
		this.keyVal = keyVal;
		this.descZhCn = descZhCn;
		this.parent = parent;
	}

	public SysTrieTree(String keyVal, String type, int orderBy,
			String descZhCn, String descZhTw, String descEnUs,
			SysTrieTree parent) {
		super();
		this.keyVal = keyVal;
		this.type = type;
		this.orderBy = orderBy;
		this.descZhCn = descZhCn;
		this.descZhTw = descZhTw;
		this.descEnUs = descEnUs;
		this.parent = parent;
	}

	@Column(name = "KEY_VAL", length = 22)
	public String getKeyVal() {
		return keyVal;
	}

	public void setKeyVal(String keyVal) {
		this.keyVal = keyVal;
	}

	@Transient
	public String getPid() {
		if (pid == null && this.parent != null) {
			return this.parent.getId();
		}
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	@OneToMany(mappedBy = "trieTree",fetch=FetchType.LAZY,cascade={ CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE })
	@OrderBy(value = "orderBy ASC")
	public Set<SysDataDict> getDataDicts() {
		return dataDicts;
	}

	public void setDataDicts(Set<SysDataDict> dataDicts) {
		this.dataDicts = dataDicts;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
	//@JsonBackReference
	public SysTrieTree getParent() {
		return parent;
	}

	public void setParent(SysTrieTree parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent",fetch=FetchType.LAZY,cascade={ CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE })
	@OrderBy(value = "orderBy ASC")
	//@JsonManagedReference
	public Set<SysTrieTree> getChildren() {
		return children;
	}

	public void setChildren(Set<SysTrieTree> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "SysTrieTree [Id="+super.id+",createUser="+super.createUser+",createTime="+super.createTime+",updateUser="+super.updateUser+",updateTime="+super.updateTime+",keyVal=" + keyVal + ", type=" + type
				+ ", orderBy=" + orderBy + ", descZhCn=" + descZhCn
				+ ", descZhTw=" + descZhTw + ", descEnUs=" + descEnUs+ "]";
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
