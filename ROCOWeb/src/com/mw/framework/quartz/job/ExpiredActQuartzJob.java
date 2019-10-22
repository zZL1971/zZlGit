/**
 *
 */
package com.mw.framework.quartz.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.TextMessage;

import com.google.code.yanf4j.core.SessionManager;
import com.main.controller.UserController;
import com.mw.framework.bean.Constants;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.ExtjsGridController;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.domain.SysMesSend;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.FlowManager;
import com.mw.framework.manager.SysMesSendManager;
import com.mw.framework.util.UUIDUtils;
import com.mw.framework.websocket.handler.SystemWebSocketHandler;

/**
 * 炸单定时器
 * 
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.quartz.job.DismantleDrawingQuartzJob.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-15
 * 
 */
public class ExpiredActQuartzJob {

	private static final Logger logger = LoggerFactory
			.getLogger(ExpiredActQuartzJob.class);

	public void run() {
		CommonManager commonManager = SpringContextHolder
				.getBean("commonManager");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		SystemWebSocketHandler systemWebSocketHandler = SpringContextHolder.getBean("systemWebSocketHandler");
		String sql = "select aha.proc_inst_id_ as procinstid, aha.act_name_ as actname, aha.act_id_ as actid, aha.start_time_ as actstarttime, nvl(aha.end_time_, sysdate) as actendtime, sac.morn_shift_start_time as mornstarttime, sac.morn_shift_end_time as mornendtime, sac.aftern_shift_start_time as afternstarttime, sac.aftern_shift_end_time as afternendtime, sac.over_time as overtime, (select assignee_ from act_hi_actinst where proc_inst_id_ = aha.proc_inst_id_ and act_id_ = aha.act_id_ and end_time_ is null) as assignee, (select count(1) from SAP_ZST_PP_RL01 sap where to_date(to_char(sap.Werks_Date, 'yyyy-mm-dd'), 'yyyy-mm-dd') between to_date(to_char(aha.start_time_, 'yyyy-mm-dd'), 'yyyy-mm-dd') and to_date(to_char(nvl(aha.end_time_, sysdate), 'yyyy-mm-dd'), 'yyyy-mm-dd') and work is null) as noworkdays, decode(si.state_audit, 'QX', 0, 1) as orderstatus, si.order_code_posex as ordercode from act_hi_actinst aha, sys_act_conf   sac, act_ct_mapping acm, sale_item      si where aha.act_id_ = sac.act_id and aha.act_name_ = sac.act_name and aha.proc_inst_id_ = acm.procinstid and acm.id = si.id and aha.id_ in (select id_ from act_hi_actinst where end_time_ is null) and si.id not in( select sale_item_id from bg_item) union all select aha.proc_inst_id_ as procinstid, aha.act_name_ as actname, aha.act_id_ as actid, aha.start_time_ as actstarttime, nvl(aha.end_time_, sysdate) as actendtime, sac.morn_shift_start_time as mornstarttime, sac.morn_shift_end_time as mornendtime, sac.aftern_shift_start_time as afternstarttime, sac.aftern_shift_end_time as afternendtime, sac.over_time as overtime, (select assignee_ from act_hi_actinst where proc_inst_id_ = aha.proc_inst_id_ and act_id_ = aha.act_id_ and end_time_ is null) as assignee, (select count(1) from SAP_ZST_PP_RL01 sap where to_date(to_char(sap.Werks_Date, 'yyyy-mm-dd'), 'yyyy-mm-dd') between to_date(to_char(aha.start_time_, 'yyyy-mm-dd'), 'yyyy-mm-dd') and to_date(to_char(nvl(aha.end_time_, sysdate), 'yyyy-mm-dd'), 'yyyy-mm-dd') and work is null) as noworkdays, decode(sh.order_status, 'QX', 0, 1) as orderstatus, sh.order_code as ordercode from act_hi_actinst aha, sys_act_conf   sac, act_ct_mapping acm, sale_header    sh where aha.act_id_ = sac.act_id and aha.act_name_ = sac.act_name and aha.proc_inst_id_ = acm.procinstid and acm.id = sh.id and aha.id_ in (select id_ from act_hi_actinst where end_time_ is null) and sh.order_status is null ";
		List<Map<String, Object>> queryList = jdbcTemplate.queryForList(sql);
		Map<String, String> sbs = new HashMap<String, String>();
		if (new UserController().getExpiredAct(queryList, sbs, 0.75)) {
			for (String key : sbs.keySet()) {
				sbs.get(key);
				if (!key.contains("_duration")) {
					String[] s = key.split("\\|");
					jdbcTemplate
							.execute(" insert into sys_mes_send(id,create_time,create_user,row_status,update_time,update_user,has_readed,msg_body,msg_title,msg_type,receive_user,SEND_TIME,SEND_USER) values('"
									+ UUIDUtils.base58Uuid()
									+ "',sysdate,'admin',1,sysdate,'admin',0,'"
									+ sbs.get(key)
									+ "','"
									+ "超期预警"
									+ "',1,'"
									+ s[0] + "',sysdate,'admin')");
					if (!"admin".equals(s[0])) {
						systemWebSocketHandler.sendMessageToUser(s[0],
								new TextMessage(sbs.get(key)));
					}

				}
			}
		}
	}
}
