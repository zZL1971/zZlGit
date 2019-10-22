package com.main.domain.sys;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

@Entity
@Table(name="METHOD_EXEDUCT_TIME")
public class MethodExeductTime extends UUIDEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9192049888748337875L;

	private String methodName;
	private String clsName;
	private Double exeducTime;
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getClsName() {
		return clsName;
	}
	public void setClsName(String clsName) {
		this.clsName = clsName;
	}
	public Double getExeducTime() {
		return exeducTime;
	}
	public void setExeducTime(Double exeducTime) {
		this.exeducTime = exeducTime;
	}
	
}
