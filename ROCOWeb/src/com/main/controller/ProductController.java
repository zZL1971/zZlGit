package com.main.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;

@Controller
@RequestMapping(value = "/control/product/")
public class ProductController extends BaseController{

	private final String NUMBER_KEY = "NUM_ITEM_CONTROL";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private SysTrieTreeDao sysTrieTreeDao;
	
	@Autowired
	private SysDataDictDao sysDataDictDao;
	
	@RequestMapping(value = "/{type}" ,method = RequestMethod.GET)
	@ResponseBody
	public Message number(@PathVariable("type") String type) {
		Message msg = new Message(null);
		msg.put("isPlus", false);
		if("number".equals(type)) {
			String matnr = super.getRequest().getParameter("matnr");
			SysTrieTree sysTrieTree = sysTrieTreeDao.findByKeyVal(NUMBER_KEY);
			if(sysTrieTree != null) {
				Map<String, SysDataDict> enableDicts = getEnableDict(sysTrieTree.getDataDicts());
				if(enableDicts.get(matnr)!=null) {
					msg.put("isPlus", true);
				}
				msg.setSuccess(true);
			}
		}
		return msg;
	}
	
	/**
	 * 获取启用的字典
	 * @return
	 */
	protected Map<String,SysDataDict> getEnableDict(Set<SysDataDict> dataDicts){
		Map<String,SysDataDict> enableDicts = null;
		if(dataDicts != null) {
			enableDicts = new HashMap<String, SysDataDict>(dataDicts.size());
			for (SysDataDict sysDataDict : dataDicts) {
				if(sysDataDict.isStat()) {
					enableDicts.put(sysDataDict.getKeyVal(), sysDataDict);
				}
			}
			return enableDicts;
		}
		return enableDicts;
	}
}
