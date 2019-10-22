/**
 *
 */
package com.mw.framework.activiti.tasklistener.custom;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.SaleItemDao;
import com.main.domain.sale.SaleItem;
import com.mw.framework.context.SpringContextHolder;

/**
 * 子流程结束开始监听器
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.activiti.tasklistener.custom.TaskSubEndStartListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-28
 *
 */
public class TaskSubEndStartListener  implements ExecutionListener{
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		//itemid
		String id = (String) delegateExecution.getVariable("subuuid");
		
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");

		JdbcTemplate jdbcTemplate =SpringContextHolder.getBean("jdbcTemplate");
		
		SaleItem findOne = saleItemDao.findOne(id);
		if(!"QX".equals(findOne.getStateAudit()))
		{
			findOne.setStateAudit("D");//柜体审核完成
		}
		
		msgProcess(jdbcTemplate, findOne);
	}
	private void msgProcess(JdbcTemplate jdbcTemplate,SaleItem findOne){
		//查找工艺路线11_开头的物料，判断宽度大于150的，把INFO1替换成A62(万能板)，工艺路线替换为1_16；查找工艺路线1_开头的物料，判断宽度小于100的，把INFO1替换成A37(封板)，工艺路线替换为11_20
//		String sql="update imos_idbext t set t.id_serie = '1_16', t.info1 = 'A62'  where instr(t.id_serie, '11_') = 1 and t.width >= 160 and t.length >= 600 and orderid = (select si.order_code_posex from sale_item si where si.id = ?) ";
//		int z=jdbcTemplate.update(sql, new Object[]{id});
//		if(z>0){
//			System.out.println("enter-------------------------------------------");
//			System.out.println(id);
//		}
		//子流程结束时触发，柜体体板件移至窄板件线
//		String sql = 
//			 "update imos_idbext ii" +
//			 "   set ii.id_serie = '11_20', ii.info1 = 'A23',ii.ispec='0'" + 
//			 " where (ii.width <= 100 or ii.length<=100 )" + 
//			 "   and instr(ii.id_serie, '1_') = 1" + 
//			 "   and instr(ii.info1, 'A') = 1" + 
//			 "   and ii.orderid = (select si.order_code_posex from sale_item si where si.id = ? and (select sh.order_type from sale_header sh where sh.id=si.pid)<>'OR7') ";
//		int z = jdbcTemplate.update(sql,new Object[]{findOne.getId()});
	}

}
