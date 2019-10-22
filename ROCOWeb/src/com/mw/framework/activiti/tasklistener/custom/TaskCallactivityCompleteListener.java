package com.mw.framework.activiti.tasklistener.custom;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import com.main.dao.SaleHeaderDao;
import com.main.domain.sale.SaleHeader;
import com.main.manager.SaleManager;
import com.mw.framework.context.SpringContextHolder;

/**
 * 子流程完成事件
 * 
 * @Project
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.activiti.tasklistener.custom.
 *           TaskCallactivityCompleteListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-10
 * 
 */
@SuppressWarnings("serial")
public class TaskCallactivityCompleteListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		// 添加定价条件代码移到 TaskValuationCreateListener
		/*String id = (String) delegateExecution.getVariable("uuid");
		SaleManager saleManager = SpringContextHolder.getBean("saleManager");
		SaleHeaderDao saleHeaderDao = SpringContextHolder
				.getBean("saleHeaderDao");
		SaleHeader saleHeader = saleHeaderDao.findOne(id);
		double fuFuanMoney = 0;
		
		fuFuanMoney=saleManager.getFuFuanMoney(saleHeader);
		saleHeader.setFuFuanMoney(fuFuanMoney);
		saleManager.save(saleHeader);//保存订单，定价条件会加上
*/	}
}
