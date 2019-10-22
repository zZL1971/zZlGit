package com.mw.framework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mw.framework.commons.GenericController;
import com.mw.framework.domain.SysActTaskLog;

/**
 * 任务手动分配流水
 * 
 * @author LKL
 * 
 */
@Controller
@RequestMapping("/core/bpm/tasklog/*")
public class SysActTaskLogController extends GenericController<SysActTaskLog> {

	@Override
	protected String getAppName() {
		return "";
	}

	@Override
	protected String[] resultJsonExcludeField() {
		return null;
	}

}
