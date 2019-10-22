package com.mw.framework.quartz.job;

import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.domain.sale.SaleItem;
import com.main.factory.ProductFactory;
import com.main.factory.ProductionLineDataFactory;
import com.main.factory.TechnologyFactory;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.manager.CommonManager;

/**
 * 补购炸弹定时器
 * 
 * @Project ROCOWeb
 * @Version 1.0.0
 * @author H
 * @time 2018-12-12
 */
public class BgDismantleDrawingQuartzJob {

	private static final Logger logger = LoggerFactory.getLogger(BgDismantleDrawingQuartzJob.class);

	private static String sumAreaSql = "update material_head t set t.zzzkar=("
			+ " select NVL(sum(t1.AREA),0)  from  imos_idbext t1,sale_item t2 "
			+ " where t2.order_code_posex=t1.orderid and  typ =3  and t2.id=? )"
			+ " where  t.id =(select material_head_id from sale_item where id=?)";
	private static String sumAreaXiSu = "update material_head t set t.zzxsfs=("
			+ " select NVL(sum(t1.AREA),0)  from  imos_idbext t1,sale_item t2 "
			+ " where t2.order_code_posex=t1.orderid and  typ =3  and t2.id=? " + " and t1.ID_SERIE like '4_%')"
			+ " where  t.id =(select material_head_id from sale_item where id=?)";

	public void run() {
		// logger.info("【执行炸单定时器START】");
		// System.out.println("--enter--");
		RuntimeService runtimeService = SpringContextHolder.getBean("runtimeService");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		HistoryService historyService = SpringContextHolder.getBean("historyService");
		CommonManager commonmanager = SpringContextHolder.getBean("commonManager");
		List<Execution> executions = runtimeService.createExecutionQuery().activityId("receivetask2").list();
		for (Execution execution : executions) {
			Object variable = runtimeService.getVariable(execution.getId(), "bgordercode");
			String shIdSql = "SELECT SH.ID FROM SALE_HEADER SH WHERE SH.ORDER_CODE=?";
			List<Map<String, Object>> shIdList = jdbcTemplate.queryForList(shIdSql, new Object[] { variable });
			String shId = (String) shIdList.get(0).get("ID");
			String sql = "SELECT count(t.rowid) FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI "
					+ "ON SI.PID = SH.ID LEFT JOIN IMOS_IDBEXT T ON T.ORDERID = SI.ORDER_CODE_POSEX "
					+ "WHERE SH.ORDER_CODE = ? AND SI.IS_STANDARD = '0'group by si.order_code_posex having count(t.rowid)<=0";
			List<Map<String, Object>> queryForCountList = jdbcTemplate.queryForList(sql, new Object[] { variable });
			if (queryForCountList.size() <= 0) {
				String itemSql = "SELECT SI.ID FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SH.ID = SI.PID WHERE SI.PID='"
						+ shId + "' AND SI.IS_STANDARD='0'";
				List<Map<String, Object>> itemList = jdbcTemplate.queryForList(itemSql);
				for (Map<String, Object> itemMap : itemList) {
					String subuuid = (String) itemMap.get("ID");
					if (!"".equals(subuuid) && subuuid != null) {
						jdbcTemplate.update(sumAreaSql, new Object[] { subuuid, subuuid });
						jdbcTemplate.update(sumAreaXiSu, new Object[] { subuuid, subuuid });
						List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
								.processInstanceId(execution.getProcessInstanceId()).taskDefinitionKey("usertask3")
								.orderByTaskCreateTime().asc().list();
//						jdbcTemplate.update("insert into task_log(uuid,result_list) values( ? ,?)",new Object[]{subuuid,list.size()});
						if (list.size() > 0) {
							HistoricTaskInstance historicTaskInstance = list.get(0);
							if (!historicTaskInstance.getAssignee().startsWith("test")
									&& !historicTaskInstance.getAssignee().startsWith("admin"))
								runtimeService.setVariable(execution.getId(), "assignee",
										historicTaskInstance.getAssignee());
						} else {
							List<Map<String, Object>> queryForList = jdbcTemplate
									.queryForList("select * from act_ct_mapping where id=?", subuuid);
							if (queryForList.size() > 0) {
								Map<String, Object> map = queryForList.get(0);
								if (map.get("PROC_INST_ID_OLD") != null
										&& map.get("PROC_INST_ID_OLD").toString().trim().length() > 0) {
									list = historyService.createHistoricTaskInstanceQuery()
											.processInstanceId(map.get("PROC_INST_ID_OLD").toString())
											.taskDefinitionKey("usertask3").orderByTaskCreateTime().desc().list();
									if (list.size() > 0) {
										HistoricTaskInstance historicTaskInstance = list.get(0);
										// if(!historicTaskInstance.getAssignee().startsWith("test") &&
										// !historicTaskInstance.getAssignee().startsWith("admin"))
										runtimeService.setVariable(execution.getId(), "assignee",
												historicTaskInstance.getAssignee());
									}
								}
							}
						}
						/*
						 * String
						 * imosIdbexSql="select t1.grid,t1.WIDTH,t1.ID,t1.LENGTH,t1.BARCODE,t1.ORDERID from IMOS_IDBEXT t1,sale_item t2  where t2.order_code_posex=t1.orderid "
						 * +
						 * " and  t2.id='"+subuuid+"' AND T1.BARCODE IS NOT NULL AND T1.INFO1 LIKE 'A%'"
						 * ; List<Map<String, Object>> imos = jdbcTemplate.queryForList(imosIdbexSql);
						 * TechnologyFactory factory=new TechnologyFactory(); for (Map<String, Object>
						 * map : imos) { String orderid = String.valueOf(map.get("ORDERID")); String id
						 * = String.valueOf(map.get("ID")); String
						 * imosIdbwgSql="SELECT I.IP_X,I.IP_Y,I.OR_X,I.OR_Y,I.MACHINING,I.DE,I.DIA,I.EP_X,I.EP_Y,I.OR_Z,I.ORTYPE FROM IMOS_IDBWG I "
						 * + "WHERE I.ORDERID='" + orderid + "' " + "AND I.ID='" + id + "'";
						 * List<Map<String, Object>> imosIdbwg =
						 * jdbcTemplate.queryForList(imosIdbwgSql); if(imosIdbwg.size()>0){
						 * ProductFactory product=new ProductFactory(map,imosIdbwg); product.cabinet();
						 * String updateSql = factory.calculateCabinet(product); if(updateSql!=null){
						 * jdbcTemplate.update(updateSql); } }
						 * 
						 * }
						 */
						ProductionLineDataFactory dataFactory = new ProductionLineDataFactory();
						dataFactory.execute(String.valueOf(variable), jdbcTemplate);
						if ("ButoFBAVh2mEz1xjTEZEqY".equals((String) subuuid)) {
							System.out.println("enter");
						}
						// 针对只有barcode2的需要将barcode2的数据转移到barcode1中
						jdbcTemplate.update(
								"update imos_idbext t set t.cnc_barcode1=t.cnc_barcode2,t.cnc_barcode2=null where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and t.cnc_barcode2 is not null and t.cnc_barcode1 is null",
								new Object[] { subuuid });

						jdbcTemplate.update(
								"update imos_idbext set SURFBNAM='打孔' where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and ((cnc_barcode1 is not null and cnc_barcode2 is null) or (cnc_barcode1 is null and cnc_barcode2 is not null)) and barcode is not null and instr(id_serie,'11_')=1",
								new Object[] { subuuid });

						// 针对barcode2、barcode1为空的情况，把barcode的数据转移到barcode1
						jdbcTemplate.update(
								"update imos_idbext t set t.cnc_barcode1=t.barcode where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and t.cnc_barcode2 is null and t.cnc_barcode1 is null",
								new Object[] { variable });

						// 柜体体板件移至窄板件线
						// 20170524
						sql = "update imos_idbext ii" + "   set ii.id_serie = '11_20', ii.info1 = 'A23'"
								+ " where (ii.width <= 100 or ii.length<=100 )" + "   and instr(ii.id_serie, '1_') = 1"
								+ "   and instr(ii.info1, 'A') = 1"
								+ "   and ii.info1 not in ('A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69')"
								+ "   and ii.orderid = (select si.order_code_posex from sale_item si where si.id = ? and (select sh.order_type from sale_header sh where sh.id=si.pid)<>'OR9') ";
						jdbcTemplate.update(sql, new Object[] { subuuid });

//						sql="update imos_idbext ii set ii.id_serie='1_'||(substr(ii.id_serie,instr(ii.id_serie,'_')+1)) where instr(ii.id_serie,'11_')=1 and ii.info1 in ('A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69') and orderid = (select si.order_code_posex from sale_item si where si.id = ?) ";
//						jdbcTemplate.update(sql,new Object[]{subuuid});

						jdbcTemplate.update(
								"update imos_idbext t set t.cnc_barcode1=t.barcode,t.surfbnam='不打孔' where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and t.cnc_barcode2 is  null and t.cnc_barcode1 is null and t.barcode is not null and instr(id_serie,'11_')=1",
								new Object[] { subuuid });

						// 更新负载均衡数据
						SaleItem saleItem = commonmanager.getOne(subuuid, SaleItem.class);
						logger.info("【执行炸单定时器】UUID=" + subuuid + ",OK!");
						jdbcTemplate.update("update imos_load_balance set status=1 where order_code='"
								+ saleItem.getOrderCodePosex() + "'");
					}
				}
				runtimeService.signal(execution.getId());

			}

		}

		// logger.info("【执行炸单定时器END】");
	}
}
