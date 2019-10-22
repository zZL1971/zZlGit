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
 * @fileName com.mw.framework.model.JdbcExtGridBean.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-13
 * 
 */
public class JdbcExtGridBean {

	private int totalPages;
	private int totalElements;
	private int size;
	
	private List<?> content;
	
	
	public JdbcExtGridBean(List<?> content) {
		super();
		this.content = content;
	}

	public JdbcExtGridBean(int totalPages, int totalElements, int size,
			List<?> content) {
		super();
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.size = size;
		this.content = content;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<?> getContent() {
		return content;
	}

	public void setContent(List<?> content) {
		this.content = content;
	}

}
