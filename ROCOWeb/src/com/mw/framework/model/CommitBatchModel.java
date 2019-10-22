package com.mw.framework.model;

import java.util.List;
/**
 * 批量审批任务model
 * @author LKL
 *
 */
public class CommitBatchModel {
	/*
	 * 订单ID
	 */
	private List<String> mappingIds;
	/*
	 * 订单编号
	 */
	private List<String> mappingNos;
	/**
	 * 任务ID
	 */
	private List<String> currentflows;
	/**
	 * 任务处理人
	 */
	private List<String> assigneeList;
	/**
	 * 下一个流程
	 */
	private List<String> nextflows;
	
	private String desc;
	private String errType;
	private String errDesc;
	
	public CommitBatchModel() {
		super();
	}
	
	public List<String> getAssigneeList() {
		return assigneeList;
	}

	public void setAssigneeList(List<String> assigneeList) {
		this.assigneeList = assigneeList;
	}

	public List<String> getMappingIds() {
		return mappingIds;
	}
	public void setMappingIds(List<String> mappingIds) {
		this.mappingIds = mappingIds;
	}
	public List<String> getMappingNos() {
		return mappingNos;
	}
	public void setMappingNos(List<String> mappingNos) {
		this.mappingNos = mappingNos;
	}
	
	public List<String> getCurrentflows() {
		return currentflows;
	}

	public void setCurrentflows(List<String> currentflows) {
		this.currentflows = currentflows;
	}

	public List<String> getNextflows() {
		return nextflows;
	}

	public void setNextflows(List<String> nextflows) {
		this.nextflows = nextflows;
	}

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getErrType() {
		return errType;
	}
	public void setErrType(String errType) {
		this.errType = errType;
	}
	public String getErrDesc() {
		return errDesc;
	}
	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}
	
}
