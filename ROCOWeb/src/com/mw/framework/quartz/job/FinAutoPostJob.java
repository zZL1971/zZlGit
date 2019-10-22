/**
 *
 */
package com.mw.framework.quartz.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.dao.SysJobPoolDao;
import com.main.domain.sys.SysJobPool;
import com.main.manager.SysJobPoolManager;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.FlowManager;
import com.mw.framework.manager.SysMesSendManager;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 * 财务确认环节自动过账定时器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.quartz.job.FinAutoPostJob.java
 * @Version 3.1.4
 * @author Chaly
 * @time 2019-03-04
 */
public class FinAutoPostJob {
	
	private static final Logger logger = LoggerFactory.getLogger(FinanceReleaseQuartzJob.class);
	
	public void run() throws InterruptedException {
		long sysTime = System.currentTimeMillis();
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		SysJobPoolDao sysJobPoolDao = SpringContextHolder.getBean("sysJobPoolDao");
		CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		SysJobPoolManager cpMan =SpringContextHolder.getBean("sysJobPoolManagerImpl");
		SysMesSendManager sysMesSendManager = SpringContextHolder.getBean("sysMesSendManager");
		TaskService taskService=SpringContextHolder.getBean("taskService");
		FlowManager flowManager = SpringContextHolder.getBean("flowManager");
		List<Map<String, Object>> imosIdbextList = jdbcTemplate.queryForList("SELECT JP.ID FROM SYS_JOB_POOL JP LEFT JOIN ACT_RU_TASK AR ON JP.PROC_INST_ID_=AR.PROC_INST_ID_ LEFT JOIN SALE_HEADER SH ON JP.ZUONR=SH.ORDER_CODE  WHERE JP.JOB_TYPE='CREDIT_JOB' AND NVL(JP.SAP_STATUS,'N')='Y' AND NVL(JP.JOB_STATUS,'B')!='C' AND NVL(JP.IS_FREEZE,'1')!='1' AND AR.NAME_='财务确认' ORDER BY JP.BEGIN_DATE");
		for (Map<String, Object> map : imosIdbextList) {
			String sysJobPoolId = (String)map.get("ID");
			SysJobPool sysJobPool = sysJobPoolDao.findOne(sysJobPoolId);
			Date beginDate = sysJobPool.getBeginDate();
			//接下来开始判断 beginDate 要比当前系统时间小 30分钟 才执行下列语句 使用系统时间减去 beginDate 得到 +30
			long time = sysTime-beginDate.getTime();
			time  = time/1000/60;
			if (time < 30) {
				continue;
			}
			Message msg = null;
			String status = "D";
			String mStr="";
			logger.warn(String.format("订单：%s,开始处理………………", sysJobPool.getZuonr()));
			try {
				// 查询信贷
				String value = "0";
				JCoDestination connect = SAPConnect.getConnect();
				JCoFunction function2 = connect.getRepository().getFunction("ZRFC_SD_XD01");
				function2.getImportParameterList().setValue("P_KKBER", "3000");
				JCoTable sKunnrTable = function2.getTableParameterList().getTable("S_KUNNR");
				sKunnrTable.appendRow();
				sKunnrTable.setValue("SIGN", "I");
				sKunnrTable.setValue("OPTION", "EQ");
				sKunnrTable.setValue("CUSTOMER_VENDOR_LOW", sysJobPool.getKunnr());
				function2.execute(connect);
				JCoTable table4 = function2.getTableParameterList().getTable("IT_TAB1");
				if (table4.getNumRows() > 0) {
					table4.firstRow();
					for (int i = 0; i < table4.getNumRows(); i++, table4.nextRow()) {
						if (i == 0) {
							value = table4.getValue("OBLIG_S").toString() == "" ? "0"
									: table4.getValue("OBLIG_S").toString();// 剩余信贷额度
						}
					}
				}
				logger.warn(String.format("订单：%s,准备开始 释放 修改SAP 拒绝原因", sysJobPool.getZuonr()));
				msg = cpMan.checkCreditBYCust1(sysJobPool);
				//成功
				if(msg.getSuccess()){
					logger.warn(String.format("订单：%s,释放 修改SAP 拒绝原因 成功", sysJobPool.getZuonr()));
					mStr=msg.getMsg();
					//订单当前任务ID
					String taskSql="SELECT RT.ID_ TASK_ID FROM act_ru_task RT,act_ct_mapping M,sale_header sh WHERE M.PROCINSTID=RT.PROC_INST_ID_ AND M.ID=SH.ID AND SH.ORDER_CODE='"+sysJobPool.getZuonr()+"'";
					List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(taskSql);
					//只有当前节点在财务确认才能进行跳转到结束节点的操作
					if(queryForList!=null && queryForList.size()>0){
						List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(queryForList.get(0).get("TASK_ID").toString());
						if(identityLinksForTask==null || !"gp_finance".equals(identityLinksForTask.get(0).getGroupId())){
							return ;
						}
					}
					if(queryForList.size()>0){
						logger.warn(String.format("订单：%s 已由%s审批完成,任务ID：%s,准备结束此订单流程 ………………", sysJobPool.getZuonr(),"JOBCHAIN",queryForList.get(0).get("TASK_ID").toString()));
						msg=flowManager.jump(queryForList.get(0).get("TASK_ID").toString(), "endevent1","JOBCHAIN");
						if(msg.getSuccess()){
							status="C";
							mStr=mStr+" | "+msg.getMsg();
							//短信 发送
							if("C".equals(status)){
								logger.warn(String.format("订单：%s 已结束流程,准备发送订单审批完成短信………………", sysJobPool.getZuonr()));
								if(!"dev".equals(ZStringUtils.readProperties())) {
									sysMesSendManager.sendMsg2Cus(jdbcTemplate, sysJobPool,value);
								}
								logger.warn(String.format("订单：%s 已成功发送短信 ^_^", sysJobPool.getZuonr()));
								//冻结此任务 因为此任务已经完成 处理
								sysJobPool.setIsFreeze("1");
							}
						}else{
							status = "D";
							mStr=mStr+" | "+msg.getErrorMsg();
						}
					}
				}else{
					logger.warn(String.format("订单：%s,释放 修改SAP 拒绝原因 失败", sysJobPool.getZuonr()));
					status = "D";
					mStr=msg.getErrorMsg();
				}
			} catch (Exception e) {
				e.printStackTrace();
				mStr=e.getLocalizedMessage();
			}finally{
				sysJobPool.setJobStatus(status);
				sysJobPool.setMsg(mStr);
				if(status.equals("D")) {
					//自动任务只处理一次 多次处理浪费资源
					sysJobPool.setJobType("SALE_JOB");
				}
				commonManager.save(sysJobPool);
			}
		
		}
	}
}
