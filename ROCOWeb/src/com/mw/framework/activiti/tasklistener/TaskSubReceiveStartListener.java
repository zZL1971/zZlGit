/**
 *
 */
package com.mw.framework.activiti.tasklistener;

import java.util.Map;
import java.util.regex.Pattern;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.manager.CommonManager;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.activiti.tasklistener.TaskSubStartListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-17
 *
 */
@SuppressWarnings("serial")
public class TaskSubReceiveStartListener implements ExecutionListener{
	
	
	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		
		//TODO 
	}
 
	 

}
