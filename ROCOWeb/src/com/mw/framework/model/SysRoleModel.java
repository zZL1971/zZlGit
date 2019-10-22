/**
 *
 */
package com.mw.framework.model;

import java.util.List;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.model.SysRoleModel.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-8
 * 
 */
public class SysRoleModel {

	private String rid;

	private List<String> menus;

	private List<String> users;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public List<String> getMenus() {
		return menus;
	}

	public void setMenus(List<String> menus) {
		this.menus = menus;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

}
