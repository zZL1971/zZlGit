/**
 *
 */
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

import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleItem;
import com.main.factory.ProductFactory;
import com.main.factory.ProductionLineDataFactory;
import com.main.factory.TechnologyFactory;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.ExtjsGridController;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.manager.CommonManager;

/**
 * 炸单定时器
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.quartz.job.DismantleDrawingQuartzJob.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-15
 *
 */
public class DismantleDrawingQuartzJob {
	
	private static final Logger logger = LoggerFactory.getLogger(DismantleDrawingQuartzJob.class);
	
	private static String sumAreaSql="update material_head t set t.zzzkar=("+
		" select NVL(sum(t1.AREA),0)  from  imos_idbext t1,sale_item t2 "+
		" where t2.order_code_posex=t1.orderid and  typ =3 and t1.info1 is not  null and t2.id=? )"+		
		" where  t.id =(select material_head_id from sale_item where id=?)";
	private static String sumAreaXiSu="update material_head t set t.zzxsfs=("+
		" select NVL(sum(t1.AREA),0)  from  imos_idbext t1,sale_item t2 "+
		" where t2.order_code_posex=t1.orderid and  typ =3 and t1.info1 is not  null and t2.id=? "+
		" and t1.ID_SERIE like '4_%')"+
		" where  t.id =(select material_head_id from sale_item where id=?)";
 


	public void run(){
		//logger.info("【执行炸单定时器START】");
		//System.out.println("--enter--");
		RuntimeService runtimeService = SpringContextHolder.getBean("runtimeService");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		HistoryService historyService = SpringContextHolder.getBean("historyService");
		CommonManager commonmanager=SpringContextHolder.getBean("commonManager");
		List<Execution> executions = runtimeService.createExecutionQuery().activityId("receivetask1").list();
		for (Execution execution : executions) {
			Object variable = runtimeService.getVariable(execution.getId(),"subuuid");
			String sql="select count(t1.rowid) as total from IMOS_IDBEXT t1,sale_item t2  where t2.order_code_posex=t1.orderid "+
			" and  t2.id=?";
			Map<String, Object> queryForMap = jdbcTemplate.queryForMap(sql,new Object[]{variable});
			Integer total = Integer.valueOf(String.valueOf(queryForMap.get("total")));
			if(total>0){
				jdbcTemplate.update(sumAreaSql,new Object[]{variable,variable});
				jdbcTemplate.update(sumAreaXiSu,new Object[]{variable,variable});
				List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(execution.getProcessInstanceId()).taskDefinitionKey("usertask3").orderByTaskCreateTime().asc().list();
				//jdbcTemplate.update("insert into task_log(uuid,result_list) values( ? ,?)",new Object[]{variable,list.size()});
				 if(list.size()>0){
					 HistoricTaskInstance historicTaskInstance=list.get(0);
					 if(!historicTaskInstance.getAssignee().startsWith("test") && !historicTaskInstance.getAssignee().startsWith("admin"))
					 runtimeService.setVariable(execution.getId(), "assignee", historicTaskInstance.getAssignee());
				 }else{
					 List<Map<String,Object>> queryForList = jdbcTemplate.queryForList("select * from act_ct_mapping where id=?",variable);
					 if(queryForList.size()>0){
						 Map<String, Object> map = queryForList.get(0);
						 if(map.get("PROC_INST_ID_OLD")!=null && map.get("PROC_INST_ID_OLD").toString().trim().length()>0){
							 list = historyService.createHistoricTaskInstanceQuery().processInstanceId(map.get("PROC_INST_ID_OLD").toString()).taskDefinitionKey("usertask3").orderByTaskCreateTime().desc().list();
							 if(list.size()>0){
								 HistoricTaskInstance historicTaskInstance=list.get(0);
								 //if(!historicTaskInstance.getAssignee().startsWith("test") && !historicTaskInstance.getAssignee().startsWith("admin"))
								 runtimeService.setVariable(execution.getId(), "assignee", historicTaskInstance.getAssignee());
							 }
						 }
					 }
				 }
				/* String imosIdbexSql="select t1.grid,t1.WIDTH,t1.ID,t1.LENGTH,t1.BARCODE,t1.ORDERID from IMOS_IDBEXT t1,sale_item t2  where t2.order_code_posex=t1.orderid "+
					" and  t2.id='"+variable+"' AND T1.BARCODE IS NOT NULL AND T1.INFO1 LIKE 'A%'";
				List<Map<String, Object>> imos = jdbcTemplate.queryForList(imosIdbexSql);
				TechnologyFactory factory=new TechnologyFactory();
				for (Map<String, Object> map : imos) {
					String orderid = String.valueOf(map.get("ORDERID"));
					String id = String.valueOf(map.get("ID"));
					String imosIdbwgSql="SELECT I.IP_X,I.IP_Y,I.OR_X,I.OR_Y,I.MACHINING,I.DE,I.DIA,I.EP_X,I.EP_Y,I.OR_Z,I.ORTYPE FROM IMOS_IDBWG I "
						+ "WHERE I.ORDERID='"
						+ orderid
						+ "' "
						+ "AND I.ID='" + id + "'";
					List<Map<String, Object>> imosIdbwg = jdbcTemplate.queryForList(imosIdbwgSql);
					if(imosIdbwg.size()>0){
						ProductFactory product=new ProductFactory(map,imosIdbwg);
						product.cabinet();
						String updateSql = factory.calculateCabinet(product);
						if(updateSql!=null){
							jdbcTemplate.update(updateSql);
						}
					}
					
				}*/
				ProductionLineDataFactory dataFactory=new ProductionLineDataFactory();
				dataFactory.execute(String.valueOf(variable), jdbcTemplate);
				 if("ButoFBAVh2mEz1xjTEZEqY".equals((String)variable)){
					 System.out.println("enter");
				 }
				 //针对只有barcode2的需要将barcode2的数据转移到barcode1中
				jdbcTemplate.update("update imos_idbext t set t.cnc_barcode1=t.cnc_barcode2,t.cnc_barcode2=null where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and t.cnc_barcode2 is not null and t.cnc_barcode1 is null",new Object[]{variable});
				
				
				jdbcTemplate.update("update imos_idbext set SURFBNAM='打孔' where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and ((cnc_barcode1 is not null and cnc_barcode2 is null) or (cnc_barcode1 is null and cnc_barcode2 is not null)) and barcode is not null and instr(id_serie,'11_')=1",new Object[]{variable});
				//针对barcode2、barcode1为空的情况，把barcode的数据转移到barcode1
				jdbcTemplate.update("update imos_idbext t set t.cnc_barcode1=t.barcode where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and t.cnc_barcode2 is null and t.cnc_barcode1 is null",new Object[]{variable});
				
				//柜体体板件移至窄板件线
				//20170524
				sql = 
					 "update imos_idbext ii" +
					 "   set ii.id_serie = '11_20', ii.info1 = 'A23'" + 
					 " where (ii.width <= 100 or ii.length<=100 )" + 
					 "   and instr(ii.id_serie, '1_') = 1" + 
					 "   and instr(ii.info1, 'A') = 1" +
					 "   and ii.info1 not in ('A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69')" + 
					 "   and ii.orderid = (select si.order_code_posex from sale_item si where si.id = ? and (select sh.order_type from sale_header sh where sh.id=si.pid)<>'OR7') ";
				jdbcTemplate.update(sql,new Object[]{variable});
				
//				sql="update imos_idbext ii set ii.id_serie='1_'||(substr(ii.id_serie,instr(ii.id_serie,'_')+1)) where instr(ii.id_serie,'11_')=1 and ii.info1 in ('A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69') and orderid = (select si.order_code_posex from sale_item si where si.id = ?) ";
//				jdbcTemplate.update(sql,new Object[]{variable});
				
				jdbcTemplate.update("update imos_idbext t set t.cnc_barcode1=t.barcode,t.surfbnam='不打孔' where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and t.cnc_barcode2 is  null and t.cnc_barcode1 is null and t.barcode is not null and instr(id_serie,'11_')=1",new Object[]{variable});
				
				

				
				//窄板件线移到柜体体板件
				//20171214
				sql = 
					 " update imos_idbext ii set ii.info1='A03' ,ii.id_serie='1_17' WHERE instr(id_serie,'11_')=1 and ii.width>=300 and ii.length>=300 " +
					 "   and ii.info1 not in ('A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69')" + 
					 "   and ii.orderid = (select si.order_code_posex from sale_item si where si.id = ? and (select sh.order_type from sale_header sh where sh.id=si.pid)<>'OR7') ";
				jdbcTemplate.update(sql,new Object[]{variable});
				jdbcTemplate.update("update imos_idbext set SURFBNAM='打孔' where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and ((cnc_barcode1 is not null and cnc_barcode2 is null) or (cnc_barcode1 is null and cnc_barcode2 is not null)) and barcode is not null and info1='A03' and id_serie='1_17' ",new Object[]{variable});
				jdbcTemplate.update("update imos_idbext t set t.cnc_barcode1=t.barcode,t.surfbnam='不打孔' where orderid=(select si.order_code_posex from sale_item si where si.id= ? ) and t.cnc_barcode2 is  null and t.cnc_barcode1 is null and t.barcode is not null and info1='A03' and id_serie='1_17'",new Object[]{variable});
				
				
				//更新负载均衡数据
				SaleItem saleItem=commonmanager.getOne(variable.toString(), SaleItem.class);
				jdbcTemplate.update("update imos_load_balance set status=1 where order_code='"+saleItem.getOrderCodePosex()+"'");
				runtimeService.signal(execution.getId());
				logger.info("【执行炸单定时器】UUID="+variable+",OK!");
				
				//针对重炸过的订单需要将其原来的打孔程序删除
//				List<Map<String,Object>> historyList=jdbcTemplate.queryForList("select * from act_hi_actinst aha where aha.act_id_='receivetask1' and end_time_ is not null and aha.proc_inst_id_ =( select acm.procinstid from act_ct_mapping acm where acm.id = ? )",new Object[]{variable});
//				if(historyList!=null && historyList.size()>0){   //说明是重炸得
//					
//				}
			}
			
			//异型板件
			if(variable!=null){
			SaleItem saleItem = commonmanager.getOne(variable.toString(), SaleItem.class);
			MaterialHead findOne=commonmanager.getOne(saleItem.getMaterialHeadId(), MaterialHead.class);
			List<Map<String,Object>> list=jdbcTemplate.queryForList("select * from imos_idbext where info1 in ('A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69')  and orderid=?",new Object[]{saleItem.getOrderCodePosex()});
			String hasPec="0";
			if(list!=null && list.size()>0){
				hasPec="1";
			}else{
				hasPec="0";
			}
			
			jdbcTemplate.update("update material_head mh set has_pec= ? where mh.id=?",new Object[]{hasPec,findOne.getId()});
			
			//jdbcTemplate.update("update imos_idbext ii set ii.ispec='1' where ii.info1 in ( 'A55','A06','A07','A08','A09','A10','A11','A12','A13','A17','A18','A21','A22','A30','A31','A34','A42','A45','A46','A60','A64','A65','A66','A67','A68','A69') and orderid= ?",new Object[]{saleItem.getOrderCodePosex()});
			//行项目 板件厚度信息的添加
			sql="select cthickness,(select key_val from sys_data_dict where trie_id='8ZMA82S2G5fwCbvW3oegKD' and desc_zh_cn=cthickness) as board_code from imos_idbext where (typ = '3' and (info1 like 'A%'))  and orderid= ? group by cthickness";
			list=jdbcTemplate.queryForList(sql,new Object[]{saleItem.getOrderCodePosex()});
			long boardThickness=0;
			for (Map<String, Object> map : list) {
				long code=map.get("BOARD_CODE")==null?0:Long.parseLong(map.get("BOARD_CODE").toString());
				boardThickness|=code;
			}
			
			jdbcTemplate.update("update material_head mh set board_thickness= ? where mh.id=?",new Object[]{boardThickness,findOne.getId()});
			
			//ispec =4的全部改成ispec=0
			//jdbcTemplate.update("update imos_idbext set ispec='0' where ispec='4' and orderid=?",new Object[]{saleItem.getOrderCodePosex()});
			}
		}
		
		
		//logger.info("【执行炸单定时器END】");
	}
}
