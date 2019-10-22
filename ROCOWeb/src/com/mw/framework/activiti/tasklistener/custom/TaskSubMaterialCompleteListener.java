/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.task.IdentityLink;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.main.service.TaskLogService;
import com.main.util.UrlUtil;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.redis.RedisUtils;

/**
 * 物料审核监听器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName 
 *           com.mw.framework.activiti.tasklistener.custom.TaskValuationCompleteListener
 *           .java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-1
 * 
 */
@SuppressWarnings("serial")
public class TaskSubMaterialCompleteListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String taskId = null;
		String groupId = null;
		Object assignee = null;
		String uuid=(String) delegateTask.getVariable("subuuid");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		 Map<String, VariableInstance> procinsid = delegateTask.getVariableInstances();
		  VariableInstance next = procinsid.get("nextflow");
		  String nextflow = next.getTextValue();
		  if(nextflow.startsWith("flow_rt_")){
			try {
				if(uuid!=null) {
					//有记录的说明是重炸的那么需要把之前的bak文件删除
					List<Map<String,Object>> resultList=jdbcTemplate.queryForList("select order_code_posex from sale_item where id= ?",new Object[]{uuid});
					Object orderCodePosex=resultList.get(0).get("ORDER_CODE_POSEX");
					//bak文件命名为订单号+行项目号+BAK
					String dirName=orderCodePosex+"_BAK";
					String result;
					result = UrlUtil.sendRequest("http://172.16.3.219:1781/IFile/FileDelete?FileName="+dirName, "GET", null, "UTF-8");
					result=(String) JSONObject.parse(result);
					jdbcTemplate.update("insert into delete_Bak(operation_time,content) values(sysdate,?)",result);
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		  }
		RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
		TaskLogService taskLogService=SpringContextHolder.getBean("taskLogService");
		try {
			taskId = delegateTask.getId();
			assignee = delegateTask.getAssignee();
			Set<IdentityLink> set = delegateTask.getCandidates();
			for (IdentityLink identityLink : set) {
				groupId = identityLink.getGroupId();
			}
			taskLogService.complete(taskId,groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			redisUtils.endTask(assignee, groupId, taskId);
		}
	}

}
