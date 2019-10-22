/**
 *
 */
package com.mw.framework.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.model.ExtDataField.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-14
 * 
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ExtDataField implements Serializable{
	private static final long serialVersionUID = -2536262857562296033L;
	private String dateFormat;
	private String defaultValue;
	private String mapping;
	private String name;
	private String type;
	private String columname;

	public ExtDataField(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public ExtDataField(String dateFormat, String defaultValue, String mapping,
			String name, String columname, String type) {
		super();
		this.dateFormat = dateFormat;
		this.defaultValue = defaultValue;
		this.mapping = mapping;
		this.name = name;
		this.columname = columname;
		this.type = type;
	}

	public ExtDataField() {
		super();
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
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

	public String getColumname() {
		return columname;
	}

	public void setColumname(String columname) {
		this.columname = columname;
	}

}
