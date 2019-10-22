package com.main.domain.sys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 公共备注信息
 * 
 * @author samguo
 * 
 */
@Entity
@Table(name = "SYS_BZ")
public class SysBz extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3481759814035604170L;

	// 默认构造方法
	public SysBz() {

	}

	/**
	 * 关联ID
	 */
	private String zid;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 审核退回标题
	 */
	private String text;

	/**
	 * 关联ID
	 */
	public void setZid(String zid) {
		this.zid = zid;
	}

	/**
	 * 关联ID
	 */
	@Column(name = "ZID")
	public String getZid() {
		return zid;
	}

	/**
	 * 备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 备注
	 */
	@Column(name = "REMARK", length = 4000)
	public String getRemark() {
		return remark;
	}

	/**
	 * 审核退回标题
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 审核退回标题
	 */
	@Column(name = "TEXT")
	public String getText() {
		return text;
	}
}