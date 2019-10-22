package com.main.domain.sys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 物料文件表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "SYS_FILE")
public class SysFile extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6304725746663671746L;
	/**
	 * 保存在服务器的文件名称
	 */
	private String uploadFileName;
	/**
	 * 上传文件名称
	 */
	private String uploadFileNameOld;
	/**
	 * 保存路径
	 */
	private String uploadFilePath;
	/**
	 * 文件类型
	 */
	private String fileType;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 关联ID
	 */
	private String foreignId;
	/**
	 * 文件类
	 */
	private CommonsMultipartFile file;

	/**
	 * 保存在服务器的文件名称
	 */
	@Column(name = "UPLOAD_FILE_NAME", length = 50)
	public String getUploadFileName() {
		return uploadFileName;
	}

	/**
	 * 保存在服务器的文件名称
	 */
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	/**
	 * 上传文件名称
	 */
	@Column(name = "UPLOAD_FILE_NAME_OLD", length = 50)
	public String getUploadFileNameOld() {
		return uploadFileNameOld;
	}

	/**
	 * 上传文件名称
	 */
	public void setUploadFileNameOld(String uploadFileNameOld) {
		this.uploadFileNameOld = uploadFileNameOld;
	}

	/**
	 * 文件类型
	 */
	@Column(name = "FILE_TYPE", length = 10)
	public String getFileType() {
		return fileType;
	}

	/**
	 * 文件类型
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 *文件
	 */
	@Transient
	public CommonsMultipartFile getFile() {
		return file;
	}

	/**
	 *文件
	 */
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	@Column(name = "UPLOAD_FILE_PATH", length = 50)
	public String getUploadFilePath() {
		return uploadFilePath;
	}

	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}

	/**
	 * 备注
	 */
	@Column(name = "REMARK", length = 250)
	public String getRemark() {
		return remark;
	}

	/**
	 * 备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 关联ID
	 * 
	 * @return
	 */
	@Column(name = "FOREIGN_ID", length = 250)
	public String getForeignId() {
		return foreignId;
	}

	/**
	 * 关联ID
	 * 
	 * @param foreignId
	 */
	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}
}
