package com.main.domain.sale;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.main.domain.mm.MaterialHead;
import com.mw.framework.bean.impl.UUIDEntity;
@Entity
@Table(name = "SALE_BG_FILE")
public class SaleBgfile extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 可用状态  用于删除标识
	 * 可用：1
	 * 不可用：X
	 */
	private String  status;
	
	/**
	 * 可用状态
	 */
	@Column(name = "STATUS",length=2)
	public String getStatus() {
		return status;
	}
	/**
	 * 可用状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
//	private SaleHeader saleHeader;
	/**
	 * SaleHeaderID
	 */
	private String pid;
	/**
	 * 保存在服务器的文件名称
	 */
	private String  uploadFileName;
	
	/**
	 * 保存路径
	 */
	private String  uploadFilePath;
	
	/**
	 * 文件类型
	 */
	private String fileType;
	
	/**
	 * 上传文件名称
	 */
	private String uploadFileNameOld;
	
	
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 上传人
	 */
	private String createUser;
	/**
	 * 上传日期
	 */
	private Date createTime;
	/**
	 * 是否有效
	 */
	private String statusdesc;
	/**
	 * 文件类
	 */
	private CommonsMultipartFile file;
	private String mappingId;
	
	
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
	
/*	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PID",referencedColumnName="ID")
	public SaleHeader getSaleHeader() {
		return saleHeader;
	}
	
	public void setSaleHeader(SaleHeader saleHeader) {
		this.saleHeader = saleHeader;
	}*/
	
	@Column(name = "MAPPING_ID", length = 32)
    public String getMappingId() {
        return mappingId;
    }

    public void setMappingId(String mappingId) {
        this.mappingId = mappingId;
    }
    
	@Column(name = "UPLOAD_FILE_PATH", length = 100)
	public String getUploadFilePath() {
		return uploadFilePath;
	}
	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	
}


