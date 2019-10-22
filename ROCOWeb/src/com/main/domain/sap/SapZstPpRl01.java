package com.main.domain.sap;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.util.JsonDateSerializer;

/**
 * 公共备注信息
 * 
 * @author samguo
 * 
 */
@Entity
@Table(name = "SAP_ZST_PP_RL01")
public class SapZstPpRl01 extends UUIDEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3803817106041558613L;

	// 默认构造方法
	public SapZstPpRl01() {

	}

	/**
	 * 工厂 CHAR 4
	 */
	private String werks;
	/**
	 * 工厂日历：周 ACCP 6
	 */
	private String week;
	/**
	 * 公共假日和工厂日历的日期 DATS 8
	 */
	private Date date;
	/**
	 * 星期几 CHAR 1
	 */
	private String libai;
	/**
	 * TEXT40 文本，40 个字符长 CHAR 40
	 */
	private String text;
	/**
	 * 是否为工作日 CHAR 1
	 */
	private String work;

	/**
	 * 工厂 CHAR 4
	 */
	@Column(name = "WERKS", length = 8)
	public String getWerks() {
		return werks;
	}

	/**
	 * 工厂 CHAR 4
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}

	/**
	 * 工厂日历：周 ACCP 6
	 */
	@Column(name = "WEEK", length = 6)
	public String getWeek() {
		return week;
	}

	/**
	 * 工厂日历：周 ACCP 6
	 */
	public void setWeek(String week) {
		this.week = week;
	}

	/**
	 * 公共假日和工厂日历的日期 DATS 8
	 */
	@Column(name = "WERKS_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getDate() {
		return date;
	}

	/**
	 * 公共假日和工厂日历的日期 DATS 8
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * 星期几 CHAR 1
	 */
	@Column(name = "LIBAI", length = 1)
	public String getLibai() {
		return libai;
	}

	/**
	 * 星期几 CHAR 1
	 */
	public void setLibai(String libai) {
		this.libai = libai;
	}

	/**
	 * TEXT40 文本，40 个字符长 CHAR 40
	 */
	@Column(name = "TEXT", length = 80)
	public String getText() {
		return text;
	}

	/**
	 * TEXT40 文本，40 个字符长 CHAR 40
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 是否为工作日 CHAR 1
	 */
	@Column(name = "WORK", length = 1)
	public String getWork() {
		return work;
	}

	/**
	 * 是否为工作日 CHAR 1
	 */
	public void setWork(String work) {
		this.work = work;
	}

}