/**
 *
 */
package com.mw.framework.quartz.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
 * 财务确认环节手动过账定时器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.quartz.job.FinAutoPostJob.java
 * @Version 3.2.7
 * @author Chaly
 * @time 2019-03-04
 */
public class FinanceReleaseQuartzJob {

	private static final Logger logger = LoggerFactory.getLogger(FinanceReleaseQuartzJob.class);

	/**
	 * 定时执行释放销售订单，用于解决资源抢夺问题
	 */
	public void run() {
		long sysTime = System.currentTimeMillis();// 获取系统时间戳
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		FlowManager flowManager = SpringContextHolder.getBean("flowManager");
		SysJobPoolDao sysJobPoolDao = SpringContextHolder.getBean("sysJobPoolDao");
		CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		SysJobPoolManager cpMan = SpringContextHolder.getBean("sysJobPoolManagerImpl");
		SysMesSendManager sysMesSendManager = SpringContextHolder.getBean("sysMesSendManager");
		String sql = "SELECT JP.ID FROM SYS_JOB_POOL JP LEFT JOIN ACT_RU_TASK AR ON JP.PROC_INST_ID_=AR.PROC_INST_ID_ LEFT JOIN SALE_HEADER SH ON JP.ZUONR = SH.ORDER_CODE WHERE JP.JOB_TYPE='SALE_JOB' AND NVL(JP.SOURCE_TYPE,'A')='A' AND NVL(JP.SAP_STATUS, 'N') = 'Y' AND NVL(JP.JOB_STATUS, 'B') not in ('C','D') AND NVL(JP.IS_FREEZE, '1') != '1' AND AR.NAME_ = '财务确认' ORDER BY JP.BEGIN_DATE";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			String sysJobPoolId = (String) map.get("ID");
			SysJobPool sysJobPool = sysJobPoolDao.findOne(sysJobPoolId);
			Date beginDate = sysJobPool.getBeginDate();
			// 接下来开始判断 beginDate 要比当前系统时间小 30分钟 才执行下列语句
			long time = sysTime - beginDate.getTime();
			// 使用系统时间减去 beginDate 得到 +30
			time = time / 1000 / 60;
			if (time < 30) {
				continue;
			}
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
				Message msg = cpMan.checkCreditBYCust1(sysJobPool);
				if (msg.getSuccess()) {
					logger.warn(String.format("订单：%s,释放 修改SAP 拒绝原因 成功", sysJobPool.getZuonr()));
					sysJobPool.setJobStatus("C");
					sysJobPool.setIsFreeze("1");// 任务处理成功
					List<Map<String, Object>> taskList = jdbcTemplate.queryForList(
							"select id_ as id,assignee_ as assignee,ark.proc_def_id_,(select a.id from act_ct_mapping a where a.procinstid=ark.proc_inst_id_ ) as mappingid  from act_ru_task ark where ark.proc_inst_id_=( select acm.procinstid from act_ct_mapping acm where acm.id =( select sh.id from sale_header sh where sh.order_code= ? ) )",
							new Object[] { sysJobPool.getZuonr() });
					if (taskList != null && taskList.size() > 0) {
						String ID = taskList.get(0).get("ID").toString();
						String assignee = taskList.get(0).get("ASSIGNEE") == null ? "unclaimed"
								: taskList.get(0).get("ASSIGNEE").toString();
						logger.warn(String.format("订单：%s 已由%s审批完成,任务ID：%s,准备结束此订单流程 ………………", sysJobPool.getZuonr(),assignee,ID));
						flowManager.jump(ID, "endevent1", assignee);
						logger.warn(String.format("订单：%s 已结束流程,准备发送订单审批完成短信………………", sysJobPool.getZuonr()));
						if(!"dev".equals(ZStringUtils.readProperties())) {
							sysMesSendManager.sendMsg2Cus(jdbcTemplate, sysJobPool,value);
						}
						logger.warn(String.format("订单：%s 已成功发送短信 ^_^", sysJobPool.getZuonr()));
					}
				} else {
					logger.warn(String.format("订单：%s,释放 修改SAP 拒绝原因 失败", sysJobPool.getZuonr()));
					sysJobPool.setJobStatus("D");
					sysJobPool.setMsg(msg.getErrorMsg());
				}
			}catch(Exception e) {
				sysJobPool.setJobStatus("D");
				sysJobPool.setMsg(e.getLocalizedMessage());
			}
			commonManager.save(sysJobPool);
		}
	}
}
