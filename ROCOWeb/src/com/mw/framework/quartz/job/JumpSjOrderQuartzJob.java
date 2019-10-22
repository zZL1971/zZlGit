package com.mw.framework.quartz.job;

import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.task.Task;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleLogisticsDao;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleLogistics;
import com.mw.framework.activiti.JumpTaskCmd;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.utils.ZStringUtils;

public class JumpSjOrderQuartzJob {

	public void run() {
		SaleHeaderDao saleHeaderDao = SpringContextHolder.getBean("saleHeaderDao");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		TaskService taskService = SpringContextHolder.getBean("taskService");
		SaleLogisticsDao saleLogisticsDao = SpringContextHolder.getBean("saleLogisticsDao");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<Map<String, Object>> saleHeaderList = jdbcTemplate.queryForList("SELECT SH.ID, AR.ID_ AS TASK_ID, AO.TASKNAME FROM SALE_HEADER SH LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AC.PROCINSTID = AR.PROC_INST_ID_ LEFT JOIN ACT_CT_ORD_ERR AO ON SH.ID = AO.MAPPING_ID WHERE SH.ORDER_TYPE = 'OR2' AND AR.NAME_ = '订单审绘' AND AO.ID IS NULL");
		for (Map<String, Object> map : saleHeaderList) {
			String id = ZStringUtils.resolverStr(map.get("ID"));
			String taskId = ZStringUtils.resolverStr(map.get("TASK_ID"));
			SaleHeader saleHeader = saleHeaderDao.findOne(id);
			List<SaleItem> findItemsByPid = saleItemDao.findItemsByPid(id);
			if("OR2".equals(saleHeader.getOrderType())) {
				List<SaleItem> sjSaleItems = saleItemDao.findByMatnrIsSj(saleHeader.getId(), "102999996");
				String jiaoQi = "";
				if(sjSaleItems.size()>0) {
					jiaoQi = "3";
					if(findItemsByPid.size()-sjSaleItems.size()>0) {
						jiaoQi = "8";
					}
				}
				List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(id);
				if(saleLogisticsList.size()>0) {
					for (SaleLogistics saleLogistics : saleLogisticsList) {
						saleLogistics.setDeliveryDay(jiaoQi);
					}
				}
				saleLogisticsDao.save(saleLogisticsList);
				Task task = taskService.createTaskQuery().taskId(taskId)
						.singleResult();
				taskService.addComment(taskId, null, "JOBCHAIN");
				TaskServiceImpl taskServiceImpl=(TaskServiceImpl)taskService;
				taskServiceImpl.getCommandExecutor().execute(new JumpTaskCmd(task.getExecutionId(), "usertask_valuation"));
				jdbcTemplate.update("UPDATE ACT_HI_ACTINST AH SET AH.END_TIME_=SYSDATE WHERE AH.TASK_ID_='"+taskId+"' AND AH.PROC_INST_ID_='"+task.getProcessInstanceId()+"' AND AH.ACT_NAME_='订单审绘'");
			}
		}
	}
}
