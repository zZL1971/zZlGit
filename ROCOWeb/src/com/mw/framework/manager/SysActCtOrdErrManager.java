package com.mw.framework.manager;

import org.activiti.engine.task.Task;

public interface SysActCtOrdErrManager {

	/**
	 * 记录 每个节点 的出错信息 以及系统出错 记录
	 * @param task
	 * @param mappingId
	 * @param errType
	 * @param errRea
	 * @param errDesc
	 * @param nextTaskName
	 * @param mappingNo
	 * @param nextTask
	 */
	public void saveSysActCtOrdErrLoger(Task task, String mappingId ,String errType,String tackit, String errRea, String errDesc, String nextTaskName,String mappingNo,String nextTask, String logerType);
	public void saveSysActCtOrdErrLoger1(Task task, String mappingId ,String errType,String tackit, String errDesc, String nextTaskName,String mappingNo,String nextTask, String logerType);
}
