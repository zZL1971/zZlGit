/**
 *
 */
package com.mw.framework.controller;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mw.framework.bean.Constants;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysReportViewsDao;
import com.mw.framework.domain.SysReportViews;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.util.ShortUrl;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.TemplateController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-23
 *
 */
@Controller
@RequestMapping("/core/tm/*")
public class TemplateController extends BaseController{
	
	@RequestMapping(value = {"/1st/{resource}"}, method = RequestMethod.GET)
	public ModelAndView template1st(@PathVariable String resource,HttpServletRequest request,ModelAndView modelAndView){
		String cache = request.getParameter("xxx");
		
		CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		
		//清理缓存
		if(cache!=null && cache.equals("up")){
			System.out.println("****更新模板*******");
			super.deleteObjectForCache(Constants.TM_GRID_PARSE, resource);
			super.deleteObjectForCache(Constants.TM_GRID_COLUMN, resource);
			super.deleteObjectForCache(Constants.TM_GRID_MODEL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_TREEMODEL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_SEARCHFORM, resource);
			super.deleteObjectForCache(Constants.TM_GRID_CONFIG, resource);
			super.deleteObjectForCache(Constants.TM_GRID_SQL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_TREESQL, resource);
			super.deleteObjectForCache(Constants.TM_GRID_FORM, resource);
			super.deleteObjectForCache(Constants.TM_GRID_GANTMODULE, resource);
			
			//更新流程模板Model层数据
			String[] keys = new String[]{"BPMHistoricTaskInstance"};
			for (String string : keys) {
				super.deleteObjectForCache(Constants.TM_GRID_MODEL, "model:"+string);
			}
			
			//重建视图
			Document doc = super.getXMLForGrid(resource);
			Element node = (Element)doc.selectSingleNode("//grid/sql");
			//SQL依据ORACLE特性，用jdbc写成视图并创建KEY
			String sql = node.getText().trim();
			String vkey = ShortUrl.ShortText(sql)[0];
			//System.out.println(vkey);
			String vname = "xv_"+node.attributeValue("vname");
			jdbcTemplate.execute("create or replace view "+vname+" as "+sql);
			SysReportViews one = commonManager.getOne(vname,SysReportViews.class);
			if(one!=null){
				one.setVkey(vkey);
				one.setVsql(sql);
				commonManager.save(one);
			}else{
				commonManager.save(new SysReportViews(vname, vkey, sql));
			}
			
		}
		
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "TM1stApp");
		modelAndView.getModelMap().put("moduleTitle", "表单模板1st");
		modelAndView.getModelMap().put("flowResource", resource);
		return modelAndView;
	}
	
	@RequestMapping(value = {"/2nd/{resource}"}, method = RequestMethod.GET)
	public ModelAndView template2st(@PathVariable String resource,ModelAndView modelAndView){
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "TM2ndApp");
		modelAndView.getModelMap().put("moduleTitle", "表单模板2nd");
		modelAndView.getModelMap().put("flowResource", resource);
		return modelAndView;
	}
}
