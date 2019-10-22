/**
 *
 */
package com.mw.framework.activiti.tasklistener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.apache.cxf.service.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.util.UrlUtil;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;

/**
 * 炸单开始监听器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.tasklistener.TaskCompleteListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-17
 * 
 */
@SuppressWarnings("serial")
public class TaskReceiveCreateListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution arg0) throws Exception {
		CommonManager commonManager = SpringContextHolder
				.getBean("commonManager");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		Object id =arg0.getVariable("subuuid");
		List<Map<String,Object>> list=jdbcTemplate.queryForList("select * from act_hi_actinst aha where aha.proc_inst_id_=(select acm.procinstid from act_ct_mapping acm where acm.id= ? ) and act_id_= ? ",new Object[]{id,"receivetask1"});
		if(list!=null && list.size()>0){
			//有记录的说明是重炸的那么需要把之前的bak文件删除
			List<Map<String,Object>> resultList=jdbcTemplate.queryForList("select order_code_posex from sale_item where id= ?",new Object[]{id});
			Object orderCodePosex=resultList.get(0).get("ORDER_CODE_POSEX");
			//bak文件命名为订单号+行项目号+BAK
			String dirName=orderCodePosex+"_BAK";
			String result=UrlUtil.sendRequest("http://172.16.3.219:1781/IFile/FileDelete?FileName="+dirName, "GET", null, "UTF-8");
			result=(String) JSONObject.parse(result);
			//String[] splitList=result.split("\"");
			jdbcTemplate.update("insert into delete_Bak(operation_time,content) values(sysdate,?)",result);
			
		}
	}

}
