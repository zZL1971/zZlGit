package com.mw.framework.model;

import java.util.List;

import com.mw.framework.domain.SysXmlControlText;

public class GenernalModel<T> {

	private List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
}
