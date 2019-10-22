package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name = "OPERATION_LOG")
public class OperationLog extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7399614095285090776L;
	
	
	private String tableName;
	private String columnName;
	private String columnValueOld;
	private String columnValueNew;
	private String targetid;

	

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnValueOld() {
		return columnValueOld;
	}

	public void setColumnValueOld(String columnValueOld) {
		this.columnValueOld = columnValueOld;
	}

	public String getColumnValueNew() {
		return columnValueNew;
	}

	public void setColumnValueNew(String columnValueNew) {
		this.columnValueNew = columnValueNew;
	}

	public String getTargetid() {
		return targetid;
	}

	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}

	
	public OperationLog(String tableName, String columnName,
			String columnValueOld, String columnValueNew, String targetid) {
		super();
		this.tableName = tableName;
		this.columnName = columnName;
		this.columnValueOld = columnValueOld;
		this.columnValueNew = columnValueNew;
		this.targetid = targetid;
	}

	public OperationLog(){
		super();
	}
	
}
