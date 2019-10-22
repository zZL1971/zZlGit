package com.mw.framework.domain;

import java.io.Serializable;

import com.mw.framework.bean.impl.UUIDEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SYS_SERIAL_NUMBER")
public class SerialNumber extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9144829329276620637L;
	private String category;// 前缀
	private Integer lastNumber;// 流水号
	private Integer lastBgNumber;// 补购流水号
	private Integer maxNumber;
	private Integer initialNumber;
	private String remark;
	private String prefixChar;

	@Column(name = "CATEGORY")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name = "LAST_NUMBER")
	public Integer getLastNumber() {
		return lastNumber;
	}

	public void setLastNumber(Integer lastNumber) {
		this.lastNumber = lastNumber;
	}
	@Column(name = "LAST_BGNUMBER")
	public Integer getLastBgNumber() {
		return lastBgNumber;
	}

	public void setLastBgNumber(Integer lastBgNumber) {
		this.lastBgNumber = lastBgNumber;
	}

	@Column(name = "MAX_NUMBER")
	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
	}

	@Column(name = "INITIAL_NUMBER")
	public Integer getInitialNumber() {
		return initialNumber;
	}

	public void setInitialNumber(Integer initialNumber) {
		this.initialNumber = initialNumber;
	}

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "PREFIX_CHAR")
	public String getPrefixChar() {
		return prefixChar;
	}

	public void setPrefixChar(String prefixChar) {
		this.prefixChar = prefixChar;
	}
}
