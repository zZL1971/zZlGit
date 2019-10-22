/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.model.MultipartFileModel.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-24
 * 
 */
@Entity
@Table(name = "SYS_MULTIPART_FILE")
public class SysMultipartFile extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private String smbFileName;
	private String upFileName;
	private String smbPath;
	private String fileType;
	private String remark;
	private String mappingId;

	public SysMultipartFile() {
		super();
	}

	public SysMultipartFile(String smbFileName, String upFileName,
			String smbPath, String fileType) {
		super();
		this.smbFileName = smbFileName;
		this.upFileName = upFileName;
		this.smbPath = smbPath;
		this.fileType = fileType;
	}

	public SysMultipartFile(String smbFileName, String upFileName,
			String smbPath, String fileType, String remark) {
		super();
		this.smbFileName = smbFileName;
		this.upFileName = upFileName;
		this.smbPath = smbPath;
		this.fileType = fileType;
		this.remark = remark;
	}

	public String getSmbFileName() {
		return smbFileName;
	}

	public void setSmbFileName(String smbFileName) {
		this.smbFileName = smbFileName;
	}

	public String getUpFileName() {
		return upFileName;
	}

	public void setUpFileName(String upFileName) {
		this.upFileName = upFileName;
	}

	public String getSmbPath() {
		return smbPath;
	}

	public void setSmbPath(String smbPath) {
		this.smbPath = smbPath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

}
