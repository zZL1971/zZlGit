/**
 *
 */
package com.mw.framework.activiti.tasklistener.csord;

import java.util.Date;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.main.dao.SaleHeaderDao;
import com.main.domain.sale.SaleHeader;
import com.main.manager.SalePrModManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.ZStringUtils;

/**
 * 客服监听器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName 
 *           com.mw.framework.activiti.tasklistener.csord.CustomerServiceCompleteListener
 *           .java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-23
 * 
 */
public class CustomerServiceCompleteListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		// Map<String, Object> variables = delegatetask.getVariables();
		// Set<Entry<String, Object>> entrySet = variables.entrySet();
		/**
		 * nextflow-->flow2 uuid-->GX38rFx39LDbjPapvwRpa7
		 */
		String uuid = (String) delegatetask.getVariable("uuid");
		// for (Entry<String, Object> entry : entrySet) {
		// // System.out.println(entry.getKey()+"-->"+entry.getValue());
		// }

		SerialNumberManager serialNumberManager = SpringContextHolder.getBean("serialNumberManager");
		SaleHeaderDao saleHeaderDao = SpringContextHolder.getBean("saleHeaderDao");

		SaleHeader saleHeader = saleHeaderDao.findOne(uuid);
		// 订单类型
		String orderType = saleHeader.getOrderType();
		if (ZStringUtils.isEmpty(orderType)) {
			new Exception("请给流程的节点订单类型赋值");
		} else {
			delegatetask.setVariable("orderType", orderType);
		}
//		List<SaleItem> findItemsByPid = saleItemDao.findItemsByPid(saleHeader.getId());
		//订单编号
		Date orderDate = saleHeader.getOrderDate();
		if(ZStringUtils.isEmpty(orderDate)){
		    saleHeader.setOrderDate(new Date());
		}
		String orderCodeNew = "";
		String orderCodeBg = "";
		String serialNumber = saleHeader.getOrderCode();
		//订单编号为空  ，参考单号不为空时
		if (ZStringUtils.isEmpty(serialNumber)) {
			if("OR3".equals(orderType)||"OR4".equals(orderType)){ //部件补购单

				orderCodeNew = saleHeader.getShouDaFang() + serialNumberManager.curSerialNumberFullYY(saleHeader.getShouDaFang()
								+ DateTools.getDateYY(), 4);
				
				orderCodeBg = orderCodeNew.substring(2,orderCodeNew.length());
			}
			saleHeader.setOrderCode(orderCodeNew);
		}
		
		saleHeaderDao.save(saleHeader);
		
	}
}
