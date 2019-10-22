/**
 *
 */
package com.mw.framework.model;

import java.util.List;
import java.util.Map;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.model.DataModel.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-22
 * 
 */
public class DataModel {

	private Map<String, Object> map;
	private List<Map<String, Object>> listmap;

	public DataModel() {
		super();
	}

	public DataModel(Map<String, Object> map, List<Map<String, Object>> listmap) {
		super();
		this.map = map;
		this.listmap = listmap;
	}

	public List<Map<String, Object>> getListmap() {
		return listmap;
	}

	public void setListmap(List<Map<String, Object>> listmap) {
		this.listmap = listmap;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
}
