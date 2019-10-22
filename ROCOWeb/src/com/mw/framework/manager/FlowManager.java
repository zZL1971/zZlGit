/**
 *
 */
package com.mw.framework.manager;

import java.util.Map;

import com.mw.framework.bean.Message;
import com.mw.framework.model.BPMHistoricTaskInstance;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.manager.BPMManager.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-10
 *
 */
public interface FlowManager {

	public Message jump(String taskId,String target,String userId);
	
	public Map<String,BPMHistoricTaskInstance> getFlowStatus(String uuid);
}
