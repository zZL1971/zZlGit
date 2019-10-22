package com.main.controller;

import java.util.List;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.main.dao.SysBzDao;
import com.main.domain.sys.SysBz;
import com.mw.framework.commons.BaseController;
import com.mw.framework.manager.CommonManager;

/**
 *
 */
@Controller
@RequestMapping("/main/sysBz/*")
public class SysBzController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(SysBzController.class);
	@Autowired
	private CommonManager commonManager;

	@Autowired
	private SysBzDao sysBzDao;

	@RequestMapping(value = "/findSysBzsByZid", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray findSysBzsByZid(String zid) {
		List<SysBz> findSysBzsByZid = sysBzDao.findSysBzsByZid(zid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort" };
		//System.out.println(JSONArray.fromObject(findSysBzsByZid, super
		//		.getJsonConfig(strings)));
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		return JSONArray.fromObject(findSysBzsByZid, super.getJsonConfig(
				"yyyy-MM-dd HH:mm:ss", strings));
	}

}
