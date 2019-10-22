/**
 *
 */
package com.mw.framework.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.mw.framework.bean.Constants;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.SysMenu;
import com.mw.framework.manager.CommonManager;
@Controller
@RequestMapping("/core/main/*")
public class MainController  extends BaseController{
	
	@Autowired
	private CommonManager commonManager;
	
	
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public ModelAndView index(ModelAndView modelAndView){
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "MainApp");//对应app/index/trie.js
		modelAndView.getModelMap().put("moduleTitle", "ROCO 门店接单系统v1.0");//功能页面标题JSP
		
		//获取当前登录用户列表
		/*LinkedHashMap<String, Object> map = super.getObjectForCache(Constants.LOGIN_USER_LIST);
		Set<Entry<String,Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			System.out.println(entry.getKey()+"|"+entry.getValue());
		}*/
		/*
		//获取BPM任务节点
		FlowManager flowManager = SpringContextHolder.getBean("flowManager");
		
		Map<String, BPMHistoricTaskInstance> flowStatus = flowManager.getFlowStatus("HTZSjYAV26gc5JxfSomDTa");
		*/
		return modelAndView;
	}
	
	@RequestMapping(value = "/menu2", method = RequestMethod.GET)
	@ResponseBody
	public JsonNode getLoginMenus2()throws ParseException { 
		return null;
	}
	
	@RequestMapping(value = "/menu", method = RequestMethod.GET)
	@ResponseBody
	public JsonNode getLoginMenus()throws ParseException { 
		
//		SysOrgan organ = new SysOrgan(1, "市场部");
		
//		SysUser loginUser = commonManager.getOne("001", SysUser.class);
//		loginUser.getOrganes().add(organ);
//		commonManager.save(loginUser);
		
		List<SysMenu> all = commonManager.getAll(SysMenu.class);
		if(all.size()==0){
			//System.out.println(super.jdbcTemplate);
			
			//初始化菜单
			jdbcTemplate.execute("insert into sys_menu(id,create_user,create_time,desc_zh_cn,EXPANDED,LEAF,ORDER_BY) values('root','admin',sysdate,'root',1,1,1)");
		}else if(all.size()==1){
			
			SysMenu one = commonManager.getOne("root", SysMenu.class);
			
			//添加默认菜单
			String[] fst = new String[]{"订单管理","工作列队","流程管理","主数据管理","系统管理"};
			
			List<SysMenu> menuList = new ArrayList<SysMenu>();
			for (String string : fst) {
				SysMenu menu = new SysMenu(string, one);
				menuList.add(menu);
			}
			
			commonManager.save(menuList);
			
		}
		
		//按照当前权限进行过滤
		//String sesql = "select id,desc_en_us,desc_zh_cn,url,pid,user_id from view_user_menu a start with a.id='root' and a.user_id='"+super.getLoginUser().getId()+"'  connect by prior a.id=a.pid and a.user_id='"+super.getLoginUser().getId()+"' order siblings by a.order_by";
		List<Map<String,Object>> menus = super.getSessionAttr(Constants.CURR_ROLE_MENUS);
		return super.queryForTreeNode(menus,false,false,false);
		
	}
}


