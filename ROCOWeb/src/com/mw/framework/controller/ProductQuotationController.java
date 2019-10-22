/**
 *
 */
package com.mw.framework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 产品报价流程控制器(测试)
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.ProductQuotationController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-4
 *
 */
@Controller
@RequestMapping("/bpm/order")
public class ProductQuotationController {

	/**
	 * 发起产品报价
	 */
	public void launch(){
		//创建产品报价单据
		
		//放入BPM产品报价环节
		
		//提交任务
		
	}
	
	/**
	 * 审核绘图
	 */
	public void trialDrawing(){
		//获取订单非标行项目
		
		//根据行项目开启任务分发数量
		
		//提交任务
		
	}
	
	/**
	 * 2020绘图、IMOS绘图(含附件才能提交流程)
	 */
	public void drawing(){
		//检查对应行项目是否已经上传附件
		
		//提交任务
		
	}
	
	/**
	 * 普通节点审核确定(物料审核、反馈订单审绘、反馈至门店、订单审价)
	 */
	public void verification(){
		//传入流程节点
		
		//提交任务
		
	}
	
	/**
	 * 反馈至门店-->反馈至订单审绘(含附件才能提交流程)
	 */
	public void backToStore(){
		
		
	}
	
	/**
	 * 模拟炸单过程(正式环境为webservices)
	 */
	public void friedDocuments(){
		//传入炸单流程节点
		
		//发送恢复流程指令
		
	}

	
	/**
	 * 终止当前流程
	 */
	public void endbpm(){
		//传入当前节点
		
		//找到当前流程end节点
		
		//直接跳转到end节点
		
	}
	
	/**
	 * 门店确认(需提交财务凭证)
	 */
	public void storeSubmit(){
		//传入流程节点
		
		//传入财务凭证
		
		//提交任务
		
	}

	/**
	 * 单据审批历史记录查询(传入订单编号)(聚合)
	 */
	public void history(){
		//传入订单编号
		
		//获取主表审核记录
		
		//获取从表审核记录
		
		//返回聚合数据至前台
		
	}

}
