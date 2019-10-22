/**
 *组织架构实体类
 */
package com.mw.framework.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 一对多中的多方
 * @JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
 * 一对多中的一方 
 * @JsonIgnoreProperties(ignoreUnknown=true)
 * 
 * 也可以在list、set集合上面set方法加上属性@JsonIgnore
 */
@Entity
@Table(name = "WECHAT")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler","children"},ignoreUnknown=true)
public class WeChat extends UUIDEntity implements Serializable {

	
	private static final long serialVersionUID = -3519668260360686188L;
	private String remark;
	private String descZhCn;
	private String descZhTw;
	private String descEnUs;
	private WeChat parent;
	private String pid;
	private int orderBy;

	private Set<SysUser> users = new HashSet<SysUser>();
	
	private Set<WeChatMenu> menus = new HashSet<WeChatMenu>();

	private Set<WeChat> children = new HashSet<WeChat>();

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}



	@OneToMany(mappedBy = "parent")
	@OrderBy(value = "sortCode ASC")
	public Set<WeChat> getChildren() {
		return children;
	}

	public void setChildren(Set<WeChat> children) {
		this.children = children;
	}

	@ManyToOne
	@JoinColumn(name = "pid")
	public WeChat getParent() {
		return parent;
	}

	public void setParent(WeChat parent) {
		this.parent = parent;
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

	@ManyToMany
	@JoinTable(name = "WECHAT_ROLE", joinColumns = { @JoinColumn(name = "wechat_role_id") }, inverseJoinColumns = {@JoinColumn(name = "wechat_user_id") })
//	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "roles", targetEntity = SysUser.class)
	public Set<SysUser> getUsers() {
		return users;
	}

	public void setUsers(Set<SysUser> users) {
		this.users = users;
	}

	@Column(name = "DESC_ZH_CN", length = 100, nullable = false)
	public String getDescZhCn() {
		return descZhCn;
	}

	
	public void setDescZhCn(String descZhCn) {
		this.descZhCn = descZhCn;
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
	@JoinTable(name = "wechat_role_menu", joinColumns = { @JoinColumn(name = "wechat_role_id") }, inverseJoinColumns = {@JoinColumn(name = "wechat_menu_id") })
	//@ManyToMany(targetEntity = SysMenu.class, cascade = { CascadeType.MERGE,CascadeType.PERSIST })
	//@JoinTable(name = "sys_role_menu", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "menu_id") })
	@OrderBy(value="parent asc,orderBy asc")
	public Set<WeChatMenu> getMenus() {
		return menus;
	}

	public void setMenus(Set<WeChatMenu> menus) {
		this.menus = menus;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	

}
