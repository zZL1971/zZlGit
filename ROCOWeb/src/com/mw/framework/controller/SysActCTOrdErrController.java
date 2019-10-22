/**
 *
 */
package com.mw.framework.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mw.framework.bean.Message;
import com.mw.framework.commons.GenericController;
import com.mw.framework.domain.SysActCTOrdErr;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.controller.SysActCTOrdErrController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2016-4-13
 * 
 */
@Controller
@RequestMapping("/core/ord/err/*")
public class SysActCTOrdErrController extends GenericController<SysActCTOrdErr> {
	
	@Override
	protected String getAppName() {
		return null;
	}

	@Override
	protected String[] resultJsonExcludeField() {
		return null;
	}
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	/**
	 * 只查询订单审绘环节出错类型和原因描述  rh
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/findByErrorInfo",method=RequestMethod.GET)
	@ResponseBody
	public Message findByErrorInfo(String id) {
		Message msg=null;
		List<Map<String, Object>> errorInfoList = jdbcTemplate.queryForList("SELECT COUNT(1) AS NM,AC.CREATE_TIME,AC.ERR_TYPE,AC.ERR_REA,AC.ERR_DESC FROM ACT_CT_ORD_ERR AC WHERE AC.TARGET_TASK_NAME='订单审绘' AND AC.MAPPING_ID=? GROUP BY AC.ERR_TYPE,AC.ERR_DESC,AC.ERR_REA,AC.CREATE_TIME ORDER BY AC.CREATE_TIME DESC",id);
		Map<String,Object> infoMap=new HashMap<String, Object>();
		if(errorInfoList.size()>0) {
			infoMap=errorInfoList.get(0);
		}
		msg =new Message(infoMap);
		return msg;
	}
}
