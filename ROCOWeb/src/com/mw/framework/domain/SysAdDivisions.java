/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mw.framework.bean.impl.AssignedEntity;
import com.mw.framework.util.annotation.FieldMeta;

/**
 * 中国行政规划
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.domain.SysAdDivisions.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-9
 * 
 */
@Entity
@Table(name = "SYS_AD_DIVISIONS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler", "children" }, ignoreUnknown = true)
public class SysAdDivisions extends AssignedEntity implements Serializable {
	private static final long serialVersionUID = -623276694116427659L;

	@FieldMeta(name = "区划名称")
	private String name;

	@FieldMeta(name = "上级行政代号")
	private SysAdDivisions parent;

	@FieldMeta(name = "区号")
	private String area;

	@FieldMeta(name = "行政等级")
	private String rating;

	@FieldMeta(name = "区域类别")
	private String type;
	
	private Set<SysAdDivisions> children = new HashSet<SysAdDivisions>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pid")
	public SysAdDivisions getParent() {
		return parent;
	}

	public void setParent(SysAdDivisions parent) {
		this.parent = parent;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@OneToMany(mappedBy = "parent")
	public Set<SysAdDivisions> getChildren() {
		return children;
	}

	public void setChildren(Set<SysAdDivisions> children) {
		this.children = children;
	}

}
