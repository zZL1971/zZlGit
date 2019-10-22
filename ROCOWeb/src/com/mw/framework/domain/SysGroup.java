/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mw.framework.bean.impl.AssignedEntity;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.domain.SysGroup.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-2-6
 * 
 */

@Entity(name = "SYS_GROUP")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler","users"},ignoreUnknown=true)
public class SysGroup extends AssignedEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private int sort;
	
	private Set<SysUser> users = new HashSet<SysUser>();
	
	public SysGroup() {
		super();
	}

	public SysGroup(String id,String name, String type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
 

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	
	//被控端
	//@ManyToMany(mappedBy="groups")
	
	//双控模式
	@ManyToMany(cascade={CascadeType.PERSIST})
	@JoinTable(name="sys_user_group", joinColumns={  @JoinColumn(name = "group_id") }, 
		    inverseJoinColumns={@JoinColumn(name="user_id")})
	//@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "groups", targetEntity = SysUser.class)
	public Set<SysUser> getUsers() {
		return users;
	}

	public void setUsers(Set<SysUser> users) {
		this.users = users;
	}

}
