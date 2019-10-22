package com.mw.framework.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "SYS_MENU")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler", "children" }, ignoreUnknown = true)
public class SysMenu extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = -3488783088352124859L;
	private String descZhCn;
	private String descZhTw;
	private String descEnUs;
	private String url;
	private String icon;
	private String pid;
	private boolean expanded = false;
	private boolean leaf = true;
	private int orderBy;
	private String workCode;

	private Set<SysRole> roles = new HashSet<SysRole>();
	private Set<SysMenu> children = new HashSet<SysMenu>();
	
	private SysMenu parent;

	public SysMenu() {
		super();
	}
	
	

	public SysMenu(String descZhCn, SysMenu parent) {
		super();
		this.descZhCn = descZhCn;
		this.parent = parent;
		this.expanded = true;
		this.leaf = false;
	}



	public SysMenu(int orderBy, String descZhCn, String url, String icon,
			String pid, boolean expand, boolean leaf, Set<SysMenu> children,
			SysMenu parent) {

		super();
		this.orderBy = orderBy;
		this.descZhCn = descZhCn;
		this.url = url;
		this.icon = icon;
		this.pid = pid;
		this.expanded = expand;
		this.leaf = leaf;
		this.children = children;
		this.parent = parent;
	}

	@Column(name = "DESC_ZH_CN", length = 100,nullable=false)
	public String getDescZhCn() {
		return descZhCn;
	}

	public void setDescZhCn(String descZhCn) {
		this.descZhCn = descZhCn;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	@Column(insertable = false, updatable = false)
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	 
	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@OneToMany(mappedBy = "parent")
	@OrderBy(value = "sortCode ASC")
	public Set<SysMenu> getChildren() {
		return children;
	}

	public void setChildren(Set<SysMenu> children) {
		this.children = children;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pid")
	public SysMenu getParent() {
		return parent;
	}

	public void setParent(SysMenu parent) {
		this.parent = parent;
	}

	@Transient
	public String getPname() {
		return this.parent!=null?this.parent.descZhCn:null;
	}

	@Column(name = "DESC_ZH_TW", length = 100)
	public String getDescZhTw() {
		return descZhTw;
	}

	public void setDescZhTw(String descZhTw) {
		this.descZhTw = descZhTw;
	}

	@Column(name = "DESC_EN_US", length = 100)
	public String getDescEnUs() {
		return descEnUs;
	}

	public void setDescEnUs(String descEnUs) {
		this.descEnUs = descEnUs;
	}
	
	//多对多双向映射
	@ManyToMany
	@JoinTable(name = "sys_role_menu", joinColumns = { @JoinColumn(name = "menu_id") }, inverseJoinColumns = {@JoinColumn(name = "role_id") })
//	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "menus", targetEntity = SysRole.class)
	public Set<SysRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<SysRole> roles) {
		this.roles = roles;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	public String getWorkCode() {
		return workCode;
	}

	public void setWorkCode(String workCode) {
		this.workCode = workCode;
	}
 
}
