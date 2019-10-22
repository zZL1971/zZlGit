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

import com.main.dao.CustHeaderDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SysJobPoolDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sys.SysJobPool;
import com.main.manager.SysJobPoolManager;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysUserDao;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;

/**
 * 批量推单定时器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.quartz.job.OrdBatchJob.java
 * @Version 3.2.5
 * @author Chaly
 * @time 2016-4-7
 */
public class OrdBatchDelyJob {
	private static final Logger logger = LoggerFactory.getLogger(FinanceReleaseQuartzJob.class);
	public void run() {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		SysJobPoolManager cpMan = SpringContextHolder.getBean("sysJobPoolManagerImpl");
		SysJobPoolDao sysJobPoolDao = SpringContextHolder.getBean("sysJobPoolDao");
		SysUserDao sysUserDao = SpringContextHolder.getBean("sysUserDao");
	    CustHeaderDao custHeaderDao = SpringContextHolder.getBean("custHeaderDao");
		SaleHeaderDao saleHeaderDao = SpringContextHolder.getBean("saleHeaderDao");
		CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		String sapNullSql="SELECT JP.ID FROM SYS_JOB_POOL JP "
				+ "LEFT JOIN ACT_RU_TASK AR ON JP.PROC_INST_ID_=AR.PROC_INST_ID_ LEFT JOIN SALE_HEADER SH ON JP.ZUONR=SH.ORDER_CODE "
				+ "WHERE AR.NAME_='财务确认' AND JP.JOB_TYPE = 'CREDIT_JOB'"
				+ "AND JP.JOB_STATUS = 'A' AND JP.SOURCE_TYPE = 'B' "
				+ "AND JP.SAP_STATUS in ('N','P') AND NVL(JP.IS_FREEZE,'1') != '1'";
		List<Map<String, Object>> imosIdbextList = jdbcTemplate
				.queryForList(sapNullSql);
		for (Map<String, Object> map : imosIdbextList) {
			String sysJobPoolId = (String)map.get("ID");
			SysJobPool sysJobPool = sysJobPoolDao.findOne(sysJobPoolId);
			Message msg = null;
			String status = "D";
			String mStr = "";
			String sapId = "N";
			try {
				logger.info(String.format("订单：%s 准备推送订单至SAP ……………………", sysJobPool.getZuonr()));
				msg = cpMan.sendSaleByCode(sysJobPool.getZuonr(),
						sysJobPool.getCreateUser());
				// 成功
				if (msg.getSuccess()) {
					logger.info(String.format("订单：%s 推送订单SAP 成功 准备检测 是否全部推送成功………………", sysJobPool.getZuonr()));
					status = "B";
					mStr = msg.getMsg();
					List<SaleItem> saleItem = saleItemDao.findByItems(sysJobPool.getZuonr());//判断是否全部生成SAP号
					if(saleItem.size()<=0) {
						logger.info(String.format("订单：%s 推送订单SAP 成功 ^_^", sysJobPool.getZuonr()));
						sapId = "Y"; //SAP号 为 N 失败 Y 成功
						Integer num = sysJobPool.getNum();
						if(num.intValue()==1) {
							sysJobPool.setBeginDate(new Date());
						}
						sysJobPool.setNum(num+1);
						String uSql = "UPDATE SALE_HEADER H SET H.SAP_CREATE_DATE=SYSDATE WHERE H.order_code='" + sysJobPool.getZuonr() + "'";
						jdbcTemplate.update(uSql);
					}
					SaleHeader saleHeader = saleHeaderDao.findByCode(sysJobPool.getZuonr()).get(0);
					// 添加自动过账流水
					SysUser sysUser = sysUserDao.findOne(sysJobPool.getCreateUser());
					CustHeader custHeader = sysUser.getCustHeader();
					String bukrs = null;
					String kunnr = null;
					if("OR4".equals(saleHeader.getOrderType())||"OR3".equals(saleHeader.getOrderType())) {
						List<CustHeader> cust = custHeaderDao.findByCode(saleHeader.getShouDaFang());
						bukrs = cust.get(0).getBukrs();
						kunnr = cust.get(0).getKunnr();
					}else {
						 bukrs = custHeader.getBukrs();
						 kunnr = custHeader.getKunnr();
					}
					sysJobPool.setBukrs(bukrs);
					sysJobPool.setKunnr(kunnr);
					Double totalPrice = jdbcTemplate.queryForObject("SELECT SUM(SI.TOTAL_PRICE) AS TOTAL_PTICE FROM SALE_ITEM SI WHERE SI.PID=(SELECT SH.ID FROM SALE_HEADER SH WHERE SH.ORDER_CODE=?) AND NVL(SI.STATE_AUDIT,'E')<>'QX'",Double.class, sysJobPool.getZuonr());
					logger.info(String.format("订单：%s 更新总价至 任务池中", sysJobPool.getZuonr()));
					sysJobPool.setNetwr(totalPrice);
				} else {
					logger.info(String.format("订单：%s 推送订单SAP 失败 ~_~", sysJobPool.getZuonr()));
					status = "D";
					mStr = msg.getErrorMsg();
				}

			} catch (Exception e) {
				logger.info(String.format("订单：%s 推送订单SAP 失败 ~_~", sysJobPool.getZuonr()));
				e.printStackTrace();
				mStr = e.getLocalizedMessage();
			} finally {
				mStr = mStr.replace("'", " ");
				sysJobPool.setJobStatus(status);
				sysJobPool.setSapStatus(sapId);
				sysJobPool.setMsg(mStr);
				commonManager.save(sysJobPool);
			}

		}
	}
}
