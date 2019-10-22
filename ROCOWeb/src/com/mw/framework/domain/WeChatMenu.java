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
@Table(name = "WECHAT_MENU")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler", "children" }, ignoreUnknown = true)
public class WeChatMenu extends UUIDEntity implements Serializable {

	private static final long serialVersionUID = -4698423244473131660L;
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
	private String pic;
	private String bindtap;

	private Set<WeChat> roles = new HashSet<WeChat>();
	private Set<WeChatMenu> children = new HashSet<WeChatMenu>();
	
	private WeChatMenu parent;



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
	
	
	public String getBindtap() {
		return bindtap;
	}

	public void setBindtap(String bindtap) {
		this.bindtap = bindtap;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
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
	public Set<WeChatMenu> getChildren() {
		return children;
	}

	public void setChildren(Set<WeChatMenu> children) {
		this.children = children;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pid")
	public WeChatMenu getParent() {
		return parent;
	}

	public void setParent(WeChatMenu parent) {
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
	@JoinTable(name = "wechat_role_menu", joinColumns = { @JoinColumn(name = "wechat_menu_id") }, inverseJoinColumns = {@JoinColumn(name = "wechat_role_id") })
//	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "menus", targetEntity = SysRole.class)
	public Set<WeChat> getRoles() {
		return roles;
	}

	public void setRoles(Set<WeChat> roles) {
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
