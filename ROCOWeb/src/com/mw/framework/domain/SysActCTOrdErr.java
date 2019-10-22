/**
 *
 */
package com.mw.framework.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.mw.framework.bean.impl.AssignedEntity;
import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 流程错误信息
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.domain.SysActCTOrdError.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-30
 * 
 */
@Entity
@Table(name = "ACT_CT_ORD_ERR")
public class SysActCTOrdErr extends UUIDEntity implements Serializable {
	private static final long serialVersionUID = -4571992986506918034L;

	/**
	 * 流程实例ID
	 */
	@NotEmpty(message = "{actCtoOrdErr.procinstid.null}")
	private String procinstid;
	/**
	 * 流程定义ID
	 */
	@NotEmpty(message = "{actCtoOrdErr.prodefid.null}")
	private String prodefid;
	/**
	 * 任务ID
	 */
	@NotEmpty(message = "{actCtoOrdErr.taskid.null}")
	private String taskid;
	/**
	 * 任务定义ID
	 */
	@NotEmpty(message = "{actCtoOrdErr.executionId.null}")
	private String executionId;
	/**
	 * 发现环节
	 */
	@NotEmpty(message = "{actCtoOrdErr.taskname.null}")
	private String taskname;
	
	/**
	 * 出错环节
	 */
	@NotEmpty(message = "{actCtoOrdErr.targetTaskName.null}")
	private String targetTaskName;
	/**
	 * 出错类型
	 */
	//@NotEmpty(message = "{actCtoOrdErr.errType.null}")
	private String errType;
	//罚单
	private String tackit;
	/**
	 * 出错原因
	 */
	//@NotEmpty(message = "{actCtoOrdErr.errRea.null}")
	private String errRea;
	/**
	 * 原因描述
	 */
	private String errDesc;
	/**
	 * 目标任务节点
	 */
	@NotEmpty(message = "{actCtoOrdErr.targetTaskDefinitionKey.null}")
	private String targetTaskDefinitionKey;

	@NotEmpty(message = "{actCtoOrdErr.taskDefinitionKey.null}")
	private String taskDefinitionKey;

	/**
	 * 关联Id
	 */
	@NotEmpty(message = "{actCtoOrdErr.mappingId.null}")
	private String mappingId;
	/**
	 * 关联单号
	 */
	@NotEmpty(message = "{actCtoOrdErr.mappingSid.null}")
	private String mappingSid;

	/**
	 * 记录 当前记录 出现的次数
	 */
	private Integer logerNum=0;
	
	/**
	 * 记录 当前 流程 记录的 类型 SALE :订单出错 SYS:系统流程节点出错
	 */
	private String logerType;
	public SysActCTOrdErr() {
		super();
	}

	public SysActCTOrdErr(String procinstid, String prodefid, String taskid,
			String executionId, String taskname, String targetTaskName,
			String errType,String errRea, String errDesc, String targetTaskDefinitionKey,
			String taskDefinitionKey, String mappingId, String mappingSid) {
		super();
		this.procinstid = procinstid;
		this.prodefid = prodefid;
		this.taskid = taskid;
		this.executionId = executionId;
		this.taskname = taskname;
		this.targetTaskName = targetTaskName;
		this.errType = errType;
		this.errRea = errRea;
		this.errDesc = errDesc;
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
		this.taskDefinitionKey = taskDefinitionKey;
		this.mappingId = mappingId;
		this.mappingSid = mappingSid;
	}




	public SysActCTOrdErr(String procinstid, String prodefid, String taskid,
			String executionId, String taskname,
			String targetTaskDefinitionKey, String taskDefinitionKey) {
		super();
		this.procinstid = procinstid;
		this.prodefid = prodefid;
		this.taskid = taskid;
		this.executionId = executionId;
		this.taskname = taskname;
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public SysActCTOrdErr(String procinstid, String prodefid, String taskid,
			String executionId, String taskname, String errType,String errRea,
			String errDesc, String targetTaskDefinitionKey,
			String taskDefinitionKey) {
		super();
		this.procinstid = procinstid;
		this.prodefid = prodefid;
		this.taskid = taskid;
		this.executionId = executionId;
		this.taskname = taskname;
		this.errType = errType;
		this.errRea = errRea;
		this.errDesc = errDesc;
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
		this.taskDefinitionKey = taskDefinitionKey;
	}

	@Column(name="PROCINSTID",length=60)
	public String getProcinstid() {
		return procinstid;
	}

	public void setProcinstid(String procinstid) {
		this.procinstid = procinstid;
	}

	@Column(name="PRODEFID",length=60)
	public String getProdefid() {
		return prodefid;
	}

	public void setProdefid(String prodefid) {
		this.prodefid = prodefid;
	}

	@Column(name="TASKID",length=60)
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	@Column(name="TASKNAME",length=20)
	public String getTaskname() {
		return taskname;
	}

	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	@Column(name="ERR_TYPE",length=10)
	public String getErrType() {
		return errType;
	}

	public void setErrType(String errType) {
		this.errType = errType;
	}
	@Column(name="TACKIT",length=10)
	public String getTackit() {
		return tackit;
	}

	public void setTackit(String tackit) {
		this.tackit = tackit;
	}

	@Column(name = "ERR_REA",length=10)
	public String getErrRea() {
		return errRea;
	}

	public void setErrRea(String errRea) {
		this.errRea = errRea;
	}

	@Column(name="ERR_DESC",length=255)
	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	@Column(name="EXECUTION_ID",length=60)
	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	@Column(name="TARGET_TASK_DEFINITION_KEY",length=80)
	public String getTargetTaskDefinitionKey() {
		return targetTaskDefinitionKey;
	}

	public void setTargetTaskDefinitionKey(String targetTaskDefinitionKey) {
		this.targetTaskDefinitionKey = targetTaskDefinitionKey;
	}

	@Column(name="TASK_DEFINITION_KEY",length=80)
	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	@Column(name="MAPPING_ID",length=60)
	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	@Column(name="TARGET_TASK_NAME",length=60)
	public String getTargetTaskName() {
		return targetTaskName;
	}

	public void setTargetTaskName(String targetTaskName) {
		this.targetTaskName = targetTaskName;
	}

	@Column(name="MAPPING_SID",length=60)
	public String getMappingSid() {
		return mappingSid;
	}

	public void setMappingSid(String mappingSid) {
		this.mappingSid = mappingSid;
	}

	@Column(name="LOGER_NUM")
	public Integer getLogerNum() {
		return logerNum;
	}

	public void setLogerNum(Integer logerNum) {
		this.logerNum = logerNum;
	}

	@Column(name="LOGER_TYPE",length=10)
	public String getLogerType() {
		return logerType;
	}

	public void setLogerType(String logerType) {
		this.logerType = logerType;
	}

}
