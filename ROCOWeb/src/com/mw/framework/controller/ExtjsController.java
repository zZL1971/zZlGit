/**
 *
 */
package com.mw.framework.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.ExtjsController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-23
 *
 */
@Controller
@RequestMapping("/core/ext/base/*")
public class ExtjsController extends BaseController{

	/**
	 * 跳转到图片查看页面
	 * @param id
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = {"/pic/{id}"}, method = RequestMethod.GET)
	public ModelAndView picIndex(@PathVariable String id,ModelAndView modelAndView){
		modelAndView.setViewName("core/pic");
		modelAndView.getModelMap().put("uuid",id);
		return modelAndView;
	}

	@RequestMapping(value = {"/dd/table/{tableName}/{desc}"}, method = RequestMethod.GET)
	@ResponseBody
	public Message getDictForTable(@PathVariable String tableName,@PathVariable String desc,String id, String columns,boolean showKey){
		
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("select "+(id==null?"id":id)+" as id,"+desc+" as text"+(columns!=null && columns.trim().length()>0?","+columns:"")+" from "+tableName);
		List<Map<String, Object>> dicts = new ArrayList<Map<String,Object>>();
		
		//空的选项
		Map<String,Object> mapNvl = new LinkedHashMap<String, Object>();
		mapNvl.put("id", "");
		mapNvl.put("text","—请选择—");
		dicts.add(mapNvl);
		
		for (Map<String, Object> queryMap : queryForList) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", queryMap.get("id"));
			map.put("text",(showKey?queryMap.get("id")+"-":"")+queryMap.get("text"));//可以支持国际化
			
			if(columns!=null && columns.length()>0){
				String[] split = columns.split(",");
				for (String string : split) {
					map.put(string, queryMap.get(string));
				}
			}
			
			dicts.add(map);
		}
	 
		return new Message(dicts);
	}
}
