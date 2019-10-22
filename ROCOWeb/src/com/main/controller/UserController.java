package com.main.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.ActExpiredLog;
import com.mw.framework.domain.UserGrumble;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.utils.StringUtil;

@Controller
@RequestMapping("/main/user/*")
public class UserController extends BaseController {
	private static final Logger logger = LoggerFactory
			.getLogger(CustController.class);
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	private CommonManager commonManager;

	/**
	 * 获取日绩效信息
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/getEfficiencyReport" }, method = RequestMethod.GET)
	@ResponseBody
	public Message getEfficiencyReport() {
		String _ActType = this.getRequest().getParameter("ActType");
		String _ActName = this.getRequest().getParameter("ActName");
		String _QueryDate = this.getRequest().getParameter("QueryDate");
		// 针对主流程查询的sql
		String _MainProsql = "select count(1) as sum_total,sum(case when to_char(max_end_time,'yyyy-mm-dd')= ?  then 1 else 0 end) as sum_really,sum(case when redo_time >1 then 1 else 0 end) as err_total from ( main_act_rp_view_sub )t2 where (t2.order_status is null and to_date(?,'yyyy-mm-dd') between to_date(to_char(t2.max_start_time, 'yyyy-mm-dd'), 'yyyy-mm-dd') and to_date(to_char(t2.max_end_time, 'yyyy-mm-dd'), 'yyyy-mm-dd')) and t2.act_name in ( ? ";
		// 确认报价需要将客户确认也加入
		if ("确认报价".equals(_ActName)) {
			_MainProsql += ", '客户确认' ";
		}
		_MainProsql += " ) ";

		// 如果是价格审核，那么需要排除取消的订单，即取消订单不算绩效
		if ("价格审核".equals(_ActName)) {
			_MainProsql += " and order_status is null";
		}
		// 针对子流程查询的sql
		String _SubProsql = "select act_name,count(1) as sum_Total,sum(case when to_char(max_end_time,'yyyy-mm-dd')=  ?  then 1 else 0 end) as sum_really,sum(case when redo_time >1 then 1 else 0 end ) as err_total from ( select * from sub_act_summary_view where to_date( ?  ,'yyyy-mm-dd') between to_date(to_char(max_start_time,'yyyy-mm-dd'),'yyyy-mm-dd') and to_date(to_char(max_end_time,'yyyy-mm-dd'),'yyyy-mm-dd') ) where act_name= ?  group by act_name";
		// 参数
		List<Object> params = new ArrayList<Object>();
		// 时间格式处理
		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
		// 添加参数
		params.add(_QueryDate);
		params.add(_QueryDate);
		params.add(_ActName);
		// 判定查询是主流程还是子流程
		String _sql = "main".equals(_ActType) ? _MainProsql : _SubProsql;
		// 执行查询
		List<Map<String, Object>> queryForList = jdbcTemplate.query(_sql,
				params.toArray(), new MapRowMapper(true, formats));
		// 处理结果
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map<String, Object> reMap : queryForList) {
			map.put("total", reMap.get("sumTotal"));
			map.put("really", reMap.get("sumReally"));
			map.put("error", reMap.get("errTotal"));
		}
		return new Message(map);
	}

	/**
	 * 获取日排行榜信息
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/getRankList" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean getRankList() {
		// 可以分页，可以用于排行榜
		String _page = this.getRequest().getParameter("page");
		String _limit = this.getRequest().getParameter("limit");

		String _ActType = this.getRequest().getParameter("ActType");
		String _orderType = this.getRequest().getParameter("OrderByType");
		String _ActName = this.getRequest().getParameter("ActName");
		String _QueryDate = this.getRequest().getParameter("QueryDate");
		String _ChartLimit = this.getRequest().getParameter("ChartLimit");

		// 查询主流程sql
		StringBuilder main_sb = new StringBuilder();
		// 查询子流程sql
		StringBuilder sub_sb = new StringBuilder();

		StringBuilder sb = new StringBuilder();
		// 参数列表
		List<Object> _params = new ArrayList<Object>();

		main_sb
				.append("select t_all.*, rownum from (select t3.* from (select assignee, sum(case when t.order_type not in ('OR3', 'OR4') then case when to_char(t.max_end_time,'yyyy-mm-dd')= ?  then 1 else 0 end else 0 end) as standard_total, sum(case when t.order_type = 'OR3' then case when to_char(t.max_end_time,'yyyy-mm-dd')= ?  then 1 else 0 end else 0 end) as OR3_total, sum(case when t.order_type = 'OR4' then case when to_char(t.max_end_time,'yyyy-mm-dd')= ?  then 1 else 0 end else 0 end) as OR4_total, sum(nvl(t.order_total, 0)) as price_total from (select t2.*, (select a.assignee_ from act_hi_actinst a where a.proc_inst_id_ = t2.proc_inst_id and a.act_id_ = t2.act_id and a.end_time_ = t2.max_end_time) as assignee from (main_act_rp_view_sub) t2 where t2.act_name in (? ");
		// 确认报价，需要将客户确认加入
		if ("确认报价".equals(_ActName)) {
			main_sb.append(", '客户确认' ");
		}
		main_sb.append(" ) ");
		// 价格审核，需要过滤取消订单，即取消订单不算绩效
		if ("价格审核".equals(_ActName)) {
			main_sb.append(" and t2.order_status is null ");
		}

		main_sb
				.append(" and to_char(max_end_time,'yyyy-mm-dd')= ? ) t group by act_name, assignee) t3 ");

		sub_sb
				.append(" select t_all.* ,rownum from ( select * from ( select assignee,count(1) as standard_total,0 as OR3_total,0 as OR4_total,sum(nvl(order_total,0)) as price_total from sub_act_summary_view where to_char(max_end_time,'yyyy-mm-dd')= ?  and act_name= ?  group by assignee)t3 ");
		_params.add(_QueryDate);
		if ("main".equals(_ActType)) {
			_params.add(_QueryDate);
			_params.add(_QueryDate);
		}
		_params.add(_ActName);
		if ("main".equals(_ActType)) {
			_params.add(_QueryDate);
		}
		sb = "main".equals(_ActType) ? main_sb : sub_sb;
		// 按照价格排序
		if ("price".equals(_orderType)) {
			sb.append("order by t3.price_total asc");
		} else if ("order".equals(_orderType)) { // 按照标准订单数排序
			sb.append("order by t3.standard_total asc");
		} else {// 默认不排序
			sb.append("");
		}
		sb.append(" )t_all ");

		Map<String, Object> _totalElements = jdbcTemplate.queryForMap(
				"select count(1) as total from ( " + sb.toString() + " )",
				_params.toArray());
		// 数据总数
		int totalSize = ((BigDecimal) _totalElements.get("TOTAL")).intValue();
		// 总页数
		int totalPages = 0; // (_totalElements.size() + _ChartLimit - 1) /
		// _ChartLimit;
		if (_ChartLimit != null) {
			// 排行榜取前几位
			sb.append(" where (rownum between 1 and ?)");
			_params.add(_ChartLimit);
			totalPages = (_totalElements.size() + Integer.parseInt(_ChartLimit) - 1)
					/ Integer.parseInt(_ChartLimit);
		} else if (_page != null && _limit != null) {
			// 分页处理
			sb.append("where (rownum between ? and ?)");
			_params.add(((Integer.parseInt(_page.trim()) - 1)
					* Integer.parseInt(_limit.trim()) + 1));
			_params.add((Integer.parseInt(_page.trim()) * Integer
					.parseInt(_limit.trim())));
		}
		Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
		// 查询结果
		List<Map<String, Object>> queryList = jdbcTemplate.query(sb.toString()
				.toString(), _params.toArray(), new MapRowMapper(true,
				formatMap));
		// 返回结果
		int _size = (_ChartLimit == null ? Integer.parseInt(_limit) : Integer
				.parseInt(_ChartLimit));
		return new JdbcExtGridBean(totalPages, totalSize, _size, queryList);
	}

	/**
	 * 获取订单的所有的当日状态
	 * 
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = { "/getOrderStatus" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean getOrderStatus(int page, int limit) {
		String _ActType = this.getRequest().getParameter("ActType");
		String _ActName = this.getRequest().getParameter("ActName");
		String _QueryDate = this.getRequest().getParameter("QueryDate");
		List<Object> _params = new ArrayList<Object>();
		// 针对主流程查询的sql
		StringBuilder _mainsql = new StringBuilder(
				"select t.proc_inst_id,t.act_id,t.act_name,(case when t.max_end_time=date'9999-12-31' then null else t.max_end_time end) as end_time,t.max_start_time as start_time,t.redo_time,t.order_code,t.order_total,t.assignee,t.order_status,t.td_fin_status,t.is_exist_err from ( select t3.*,rownum as rn,(case when max_end_time between to_date( ? , 'yyyy-mm-dd') and (to_date( ? , 'yyyy-mm-dd') + 1) then '已完成'  else  '未完成' end) as td_fin_status,(case when redo_time>1 then '是' else '否' end) as is_exist_err from ( select t2.*, (select a.assignee_ from act_hi_actinst a where a.proc_inst_id_ = t2.proc_inst_id and a.act_id_ = t2.act_id and a.end_time_ = t2.max_end_time) as assignee from (main_act_rp_view_sub) t2 where t2.act_name in (  ?  ");
		// 针对子流程查询的sql
		StringBuilder _subsql = new StringBuilder(
				"select t.proc_inst_id,t.act_id,t.act_name,(case when t.max_end_time=date'9999-12-31' then null else t.max_end_time end) as end_time,t.max_start_time as start_time,t.redo_time,t.order_code,t.order_total,t.assignee,t.order_status,t.td_fin_status,t.is_exist_err from (select t2.*, rownum as rn , (case  when to_char(max_end_time,'yyyy-mm-dd')= ?    then   '已完成'  else   '未完成' end) as td_fin_status, (case  when redo_time > 1 then  '是'  else   '否' end) as is_exist_err  from sub_act_summary_view  t2 where  act_name= ? ");

		// 主流程的确认报价，需要将客户确认加入
		if ("确认报价".equals(_ActName)) {
			_mainsql.append(", '客户确认' ");
		}
		_mainsql.append(" ) ");

		// 主流程的价格审核，需要将取消订单取出，即取消的订单不算绩效
		if ("价格审核".equals(_ActName)) {
			_mainsql.append(" and t2.order_status is null ");
		}
		// 根据查询类型去选择是查询主流程还是子流程
		StringBuilder _sql = ("main".equals(_ActType)) ? _mainsql : _subsql;

		// 加入查询日期
		_sql
				.append(" and (to_date( ? , 'yyyy-mm-dd') between to_date(to_char(t2.max_start_time, 'yyyy-mm-dd'), 'yyyy-mm-dd') and to_date(to_char(t2.max_end_time, 'yyyy-mm-dd'), 'yyyy-mm-dd') ) ");

		if ("main".equals(_ActType)) {
			_sql.append(" ) t3 ");
		}
		_sql.append(" )t where 1=1 ");

		_params.add(_QueryDate);
		if ("main".equals(_ActType)) {
			_params.add(_QueryDate);
		}
		_params.add(_ActName);
		_params.add(_QueryDate);

		// 查询结果总数
		Map<String, Object> _totalElements = jdbcTemplate.queryForMap(
				"select count(1) as total from ( " + _sql.toString() + " )",
				_params.toArray());
		// 数据总数
		int totalSize = ((BigDecimal) _totalElements.get("TOTAL")).intValue();
		// 总页数
		int totalPages = (_totalElements.size() + limit - 1) / limit;

		_params.add((page - 1) * limit + 1);
		_params.add(page * limit);
		Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
		// 查询结果
		List<Map<String, Object>> queryList = jdbcTemplate.query(_sql
				+ " and  (rn between ? and ? )", _params.toArray(),
				new MapRowMapper(true, formatMap));
		// 返回结果
		return new JdbcExtGridBean(totalPages, totalSize, limit, queryList);
	}

	/**
	 * 获取节点的月总结
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/getActSummary" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean getActSummary() {
		String _ActType = this.getRequest().getParameter("ActType");
		String _ActName = this.getRequest().getParameter("ActName");
		String _Year = this.getRequest().getParameter("Year");
		String _Month = this.getRequest().getParameter("Month");
		List<Object> _params = new ArrayList<Object>();
		// 针对主流程查询sql
		StringBuilder _mainsql = new StringBuilder(
				"select * from act_summary_view where 1=1 ");
		// 针对子流程查询sql
		StringBuilder _subsql = new StringBuilder(
				"select * from (select act_name,order_status ,to_char(max_end_time,'yyyy') as order_year,to_char(max_end_time,'mm') as order_month,to_char(max_end_time,'dd') as order_day,count(1) as total from sub_act_summary_view where 1=1 ");
		// 针对价格审核，应该过滤取消订单，即取消的订单不算绩效
		if ("价格审核".equals(_ActName)) {
			_mainsql.append(" and order_status is null");
		}
		_subsql
				.append(" group by act_name,order_status,to_char(max_end_time,'yyyy'),to_char(max_end_time,'mm'),to_char(max_end_time,'dd') ) where 1=1 ");
		StringBuilder _sql = ("main".equals(_ActType)) ? _mainsql : _subsql;
		_sql.append(" and act_name in ( ? ");

		// 确认报价需要加入客户确认
		if ("确认报价".equals(_ActName)) {
			_sql.append(", '客户确认' ");
		}
		_sql.append(" ) ");

		// 加入年月过滤
		_sql.append(" and order_year = ? and order_month = ? ");

		_params.add(_ActName);
		_params.add(_Year);
		_params.add(_Month);

		// 按日期排序
		_sql.append(" order by order_day ");

		// 时间格式处理
		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();

		// 查询结果
		List<Map<String, Object>> queryForList = jdbcTemplate
				.query(_sql.toString(), _params.toArray(), new MapRowMapper(
						true, formats));
		return new JdbcExtGridBean(0, 0, 0, queryForList);
	}

	/**
	 * 验证用户是否有权限使用工作台
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/checkUser" }, method = RequestMethod.GET)
	@ResponseBody
	public Message checkUser() {
		Map<String, Object> map = new HashMap<String, Object>();
		// System.out.println(this.getLoginUser().getUserName());
		// for(SysGroup group:this.getLoginUser().getGroups()){
		// if("gp_store".equals(group.getId())){
		// map.put("result", "fail");
		// return new Message(map);
		// }
		// }
		// 目前设置为系统管理员才有权限使用
		if ("系统管理员".equals(this.getLoginUser().getUserName())) {
			map.put("result", "success");
		} else {
			map.put("result", "fail");
		}
		return new Message(map);
	}

	/**
	 * 检测是否超期
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/checkActExpired" }, method = RequestMethod.GET)
	@ResponseBody
	public Message checkActExpired() {
		// 获取参数
		String _assignee = this.getRequest().getParameter("assignee");
		String _procinstId = this.getRequest().getParameter("procinstId");
		String _actId = this.getRequest().getParameter("actId");
		String _checkType = this.getRequest().getParameter("checkType");

		// 结果集
		List<Map<String, Object>> _resultList = new ArrayList<Map<String, Object>>();
		double _percent = 1;
		String _ahaSql = "";
		// 查询流程节点
		// if ("taskId".equals(_checkType)) {

		_ahaSql = "select * from (select aha.*,(select claim_time_ from act_hi_taskinst t where t.proc_inst_id_=aha.proc_inst_id_ and t.task_def_key_=aha.act_id_ and t.assignee_=aha.assignee_ and rownum=1) as claim_time, sac.*, decode((select count(1) as s from act_expired_log ael where ael.history_Id = (select c.id_ from act_hi_actinst c where proc_inst_id_ = aha.proc_inst_id_ and act_id_ = aha.act_id_ and act_name_ = aha.act_name_ and end_time_ is null)), 0, 0, 1) as has_expired_log, (select count(1) from SAP_ZST_PP_RL01 sap where to_date(to_char(sap.Werks_Date, 'yyyy-mm-dd'),  'yyyy-mm-dd') between to_date(to_char(aha.start_time_, 'yyyy-mm-dd'), 'yyyy-mm-dd') and to_date(to_char(nvl(aha.end_time_,sysdate), 'yyyy-mm-dd'), 'yyyy-mm-dd') and work is null) as nowork_days, nvl((select (Case when sh.order_status is null then 1 else case when sh.order_status = 'QX' then 0 else 1 end end) from sale_header sh, act_ct_mapping acm where sh.id = acm.id and acm.procinstid = aha.proc_inst_id_), (select si.status from sale_item si, act_ct_mapping acm where si.id = acm.id and acm.procinstid = aha.proc_inst_id_)) as order_status, nvl((select sh.order_code from sale_header sh, act_ct_mapping acm where sh.id = acm.id and acm.procinstid = aha.proc_inst_id_), (select si.order_code_posex from sale_item si, act_ct_mapping acm where si.id = acm.id and acm.procinstid = aha.proc_inst_id_)) as order_code  from  act_hi_actinst aha, sys_act_conf sac where aha.act_id_ = sac.act_id and aha.act_name_ = sac.act_name) where has_expired_log = 0 and proc_inst_id_='"
				+ _procinstId
				+ "' and act_id_='"
				+ _actId
				+ "' and assignee_='" + _assignee + "'";
		// _ahaSql="select aha.* from act_hi_actinst aha where aha.proc_inst_id_='"+_procinstId+"' and aha.act_id_='"+_actId+"' and aha.assignee_='"+_assignee+"'";
		// }
		// else if ("assignee".equals(_checkType)) {
		// _ahaSql =
		// "select aha.*,sac.*, (select count(1) from SAP_ZST_PP_RL01 sap where to_date(to_char(sap.Werks_Date,'yyyy-mm-dd'),'yyyy-mm-dd') between to_date(to_char(aha.start_time_,'yyyy-mm-dd'),'yyyy-mm-dd') and to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd')  and work is null ) as nowork_days, nvl((select (Case when sh.order_status is null then 1 else case when sh.order_status ='QX' then 0 else 1 end end ) from sale_header sh, act_ct_mapping acm where sh.id = acm.id and acm.procinstid = aha.proc_inst_id_), (select si.status from sale_item si, act_ct_mapping acm where si.id = acm.id and acm.procinstid = aha.proc_inst_id_)) as order_status, nvl((select sh.order_code from sale_header sh, act_ct_mapping acm where sh.id = acm.id and acm.procinstid = aha.proc_inst_id_), (select si.order_code_posex from sale_item si, act_ct_mapping acm where si.id = acm.id and acm.procinstid = aha.proc_inst_id_)) as order_code from act_hi_actinst aha, sys_act_conf sac where aha.act_id_ = sac.act_id and aha.act_name_ = sac.act_name and aha.end_time_ is null and aha.assignee_='"
		// + _assignee + "'";
		// _percent = 0.75;
		// }
		List<Map<String, Object>> actHistoryList = jdbcTemplate
				.queryForList(_ahaSql);
		// 查询结果
		for (Map<String, Object> pri : actHistoryList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("duration", pri.get("duration_time"));
			map.put("procinstid", pri.get("proc_inst_id_"));
			map.put("actid", pri.get("act_id_"));
			map.put("actname", pri.get("act_name_"));
			map.put("actstarttime", pri.get("start_time_"));
			map.put("actendtime", pri.get("end_time_") == null ? new Date()
					: pri.get("end_time_"));
			map.put("orderstatus", pri.get("order_status"));
			map.put("ordercode", pri.get("order_code"));
			map.put("mornendtime", pri.get("morn_shift_end_time"));// 上午下班时间
			map.put("mornstarttime", pri.get("morn_shift_start_time"));// 上午上班时间
			map.put("afternstarttime", pri.get("aftern_shift_start_time"));// 下午上班时间
			map.put("afternendtime", pri.get("aftern_shift_end_time"));// 下午下班时间
			map.put("overtime", pri.get("over_time"));// 超期时间
			map.put("noworkdays", pri.get("nowork_Days"));// 工作日期
			map.put("assignee", pri.get("assignee_"));// 工作日期
			_resultList.add(map);
		}
		Map<String, String> sbs = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		boolean result = getExpiredAct(_resultList, sbs, _percent);
		map.put("message", sbs.size() == 0 ? null : (sbs.get(_assignee + "|"
				+ _procinstId + "|" + _actId) == null) ? null : sbs.get(
				_assignee + "|" + _procinstId + "|" + _actId).toString());
		map.put("duration", sbs.size() == 0 ? null : (sbs.get(_assignee + "|"
				+ _procinstId + "|" + _actId + "_duration") == null) ? null
				: sbs.get(
						_assignee + "|" + _procinstId + "|" + _actId
								+ "_duration").toString());
		map.put("hasexpired", result);
		return new Message(map);
	}

	/**
	 * 计算是否超期
	 * 
	 * @param list
	 * @param sb
	 *            超期订单记录
	 * @param percent
	 *            百分比
	 * @return true 为超期，false为没有存在超期
	 */
	public Boolean getExpiredAct(List<Map<String, Object>> list,
			Map<String, String> sbs, double percent) {

		boolean flag = false;

		Map<String, Long> durationList = new HashMap<String, Long>();

		for (Map<String, Object> map : list) {
			Long duration = 0l;
			// 获取参数
			// BigDecimal _duration=(BigDecimal)map.get("duration");
			String _assignee = map.get("assignee") == null ? "" : map.get(
					"assignee").toString();
			String _orderCode = map.get("ordercode") == null ? "" : map.get(
					"ordercode").toString();
			String _procinstId = map.get("procinstid") == null ? "" : map.get(
					"procinstid").toString();
			String _actId = map.get("actid") == null ? "" : map.get("actid")
					.toString();
			String _actName = map.get("actname") == null ? "" : map.get(
					"actname").toString();

			Calendar _actStartTime = Calendar.getInstance();
			_actStartTime.setTimeInMillis(((Date) map.get("actstarttime"))
					.getTime());

			Calendar _actEndTime = Calendar.getInstance();
			_actEndTime.setTimeInMillis(((Date) map.get("actendtime"))
					.getTime());

			Calendar _mornEndTime = Calendar.getInstance();
			_mornEndTime.setTimeInMillis(((Date) map.get("mornendtime"))
					.getTime());

			Calendar _mornStartTime = Calendar.getInstance();
			_mornStartTime.setTimeInMillis(((Date) map.get("mornstarttime"))
					.getTime());

			Calendar _afternStartTime = Calendar.getInstance();
			_afternStartTime
					.setTimeInMillis(((Date) map.get("afternstarttime"))
							.getTime());

			Calendar _afternEndTime = Calendar.getInstance();
			_afternEndTime.setTimeInMillis(((Date) map.get("afternendtime"))
					.getTime());

			// 订单状态
			Integer _orderStatus = ((BigDecimal) map.get("orderstatus"))
					.intValue();
			// 超期规则规定时间
			Integer _overTime = ((BigDecimal) map.get("overtime")).intValue();
			// 非上班天数
			Integer _noworkDays = ((BigDecimal) map.get("noworkdays"))
					.intValue();

			// 超期判断时间
			long _overTimeMillis = (long) (percent * _overTime * 60 * 60 * 1000);
			// 开始时间和结束时间中间相差的天数
			int _costTimeDay = (int) ((_actEndTime.getTimeInMillis() - _actStartTime
					.getTimeInMillis()) / (24 * 60 * 60 * 1000));
			// 开始时间到结束时间，实在用了的时间
			long _remainTimeMIllis = 0;

			// 如果订单状态为取消，或者没有定义超期规则，或者是开始时间和结束时间相差的时间就比超期规则定义的时间少，那么跳过
			if (_orderStatus == 0 || _overTime == null) {
				continue;
			} else {
				// 将结束时间和开始时间放在同一天
				_actEndTime.setTime(new Date(_actEndTime.getTimeInMillis()
						- (24 * 60 * 60 * 1000) * _costTimeDay));

				_mornStartTime.set(_actStartTime.get(Calendar.YEAR),
						_actStartTime.get(Calendar.MONTH), _actStartTime
								.get(Calendar.DAY_OF_MONTH));
				_mornEndTime.set(_actStartTime.get(Calendar.YEAR),
						_actStartTime.get(Calendar.MONTH), _actStartTime
								.get(Calendar.DAY_OF_MONTH));
				_afternStartTime.set(_actStartTime.get(Calendar.YEAR),
						_actStartTime.get(Calendar.MONTH), _actStartTime
								.get(Calendar.DAY_OF_MONTH));
				_afternEndTime.set(_actStartTime.get(Calendar.YEAR),
						_actStartTime.get(Calendar.MONTH), _actStartTime
								.get(Calendar.DAY_OF_MONTH));

				// 如果开始时间和结束时间是同一天
				if ((_actEndTime.get(Calendar.DAY_OF_YEAR) - _actStartTime
						.get(Calendar.DAY_OF_YEAR)) == 0) {
					switch (getTimeStep(_actStartTime, _mornStartTime,
							_mornEndTime, _afternStartTime, _afternEndTime)) {
					case 1:
						switch (getTimeStep(_actEndTime, _mornStartTime,
								_mornEndTime, _afternStartTime, _afternEndTime)) {
						case 2:
							_remainTimeMIllis = _actEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis();
							break;
						case 3:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis();
							break;
						case 4:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis()
									+ _actEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						case 5:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis()
									+ _afternEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						default:
							break;
						}
						break;
					case 2:
						switch (getTimeStep(_actEndTime, _mornStartTime,
								_mornEndTime, _afternStartTime, _afternEndTime)) {
						case 2:
							_remainTimeMIllis = _actEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis();
							break;
						case 3:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis();
							break;
						case 4:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis()
									+ _actEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						case 5:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis()
									+ _afternEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						default:
							break;
						}
						break;
					case 3:
						switch (getTimeStep(_actEndTime, _mornStartTime,
								_mornEndTime, _afternStartTime, _afternEndTime)) {
						case 3:
							_remainTimeMIllis = 0;
							break;
						case 4:
							_remainTimeMIllis = _actEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						case 5:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						default:
							break;
						}
						break;
					case 4:
						switch (getTimeStep(_actEndTime, _mornStartTime,
								_mornEndTime, _afternStartTime, _afternEndTime)) {
						case 4:
							_remainTimeMIllis = _actEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis();
							// + _actStartTime.getTimeInMillis()
							// - _afternStartTime.getTimeInMillis();
							break;
						case 5:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _actStartTime.getTimeInMillis();
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}

				} else if ((_actEndTime.get(Calendar.DAY_OF_YEAR) - _actStartTime
						.get(Calendar.DAY_OF_YEAR)) != 0) {
					Calendar tempMornStart = Calendar.getInstance();
					Calendar tempMornEnd = Calendar.getInstance();
					Calendar tempAfternStart = Calendar.getInstance();
					Calendar tempAfternEnd = Calendar.getInstance();

					tempMornStart.set(_actEndTime.get(Calendar.YEAR),
							_actEndTime.get(Calendar.MONTH), _actEndTime
									.get(Calendar.DAY_OF_MONTH), _mornStartTime
									.get(Calendar.HOUR_OF_DAY), _mornStartTime
									.get(Calendar.MINUTE), _mornStartTime
									.get(Calendar.SECOND));
					tempMornEnd.set(_actEndTime.get(Calendar.YEAR), _actEndTime
							.get(Calendar.MONTH), _actEndTime
							.get(Calendar.DAY_OF_MONTH), _mornEndTime
							.get(Calendar.HOUR_OF_DAY), _mornEndTime
							.get(Calendar.MINUTE), _mornEndTime
							.get(Calendar.SECOND));
					tempAfternStart.set(_actEndTime.get(Calendar.YEAR),
							_actEndTime.get(Calendar.MONTH), _actEndTime
									.get(Calendar.DAY_OF_MONTH),
							_afternStartTime.get(Calendar.HOUR_OF_DAY),
							_afternStartTime.get(Calendar.MINUTE),
							_afternStartTime.get(Calendar.SECOND));
					tempAfternEnd.set(_actEndTime.get(Calendar.YEAR),
							_actEndTime.get(Calendar.MONTH), _actEndTime
									.get(Calendar.DAY_OF_MONTH), _afternEndTime
									.get(Calendar.HOUR_OF_DAY), _afternEndTime
									.get(Calendar.MINUTE), _afternEndTime
									.get(Calendar.SECOND));

					switch (getTimeStep(_actStartTime, _mornStartTime,
							_mornEndTime, _afternStartTime, _afternEndTime)) {
					case 1:
						if ((_actEndTime.getTimeInMillis() - _costTimeDay * 24
								* 60 * 60 * 1000) < _actStartTime
								.getTimeInMillis()) {
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis()
									+ _afternEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
						}
						break;
					case 2:
						switch (getTimeStep(_actEndTime, tempMornStart,
								tempMornEnd, tempAfternStart, tempAfternEnd)) {
						case 1:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis()
									+ _afternEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						case 2:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis()
									+ _afternEndTime.getTimeInMillis()
									- _afternStartTime.getTimeInMillis()
									+ _actEndTime.getTimeInMillis()
									- tempMornStart.getTimeInMillis();
							break;
						default:
							break;
						}
						break;
					case 3:
						switch (getTimeStep(_actEndTime, tempMornStart,
								tempMornEnd, tempAfternStart, tempAfternEnd)) {
						case 1:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _afternStartTime.getTimeInMillis();
							break;
						case 2:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _afternStartTime.getTimeInMillis()
									+ _actEndTime.getTimeInMillis()
									- tempMornStart.getTimeInMillis();
							break;
						case 3:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _afternStartTime.getTimeInMillis()
									+ _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis();
							break;
						default:
							break;
						}
						break;
					case 4:
						switch (getTimeStep(_actEndTime, tempMornStart,
								tempMornEnd, tempAfternStart, tempAfternEnd)) {
						case 1:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _actStartTime.getTimeInMillis();
							break;
						case 2:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _actStartTime.getTimeInMillis()
									+ _actEndTime.getTimeInMillis()
									- tempMornStart.getTimeInMillis();
							break;
						case 3:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _actStartTime.getTimeInMillis()
									+ _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis();
							break;
						case 4:
							_remainTimeMIllis = _actEndTime.getTimeInMillis()
									- tempAfternStart.getTimeInMillis()
									+ _afternEndTime.getTimeInMillis()
									- _actStartTime.getTimeInMillis()
									+ _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis();
						default:
							break;
						}
						break;
					case 5:
						switch (getTimeStep(_actEndTime, tempMornStart,
								tempMornEnd, tempAfternStart, tempAfternEnd)) {
						case 2:
							_remainTimeMIllis = _actEndTime.getTimeInMillis()
									- tempMornStart.getTimeInMillis();
							break;
						case 3:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis();
							break;
						case 4:
							_remainTimeMIllis = _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis()
									+ _actEndTime.getTimeInMillis()
									- tempAfternStart.getTimeInMillis();
							break;
						case 5:
							_remainTimeMIllis = _afternEndTime
									.getTimeInMillis()
									- _afternStartTime.getTimeInMillis()
									+ _mornEndTime.getTimeInMillis()
									- _mornStartTime.getTimeInMillis();
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
				}
				duration = (_remainTimeMIllis + 8 * 60 * 60 * 1000
						* (long) (_costTimeDay - _noworkDays));
				if (!durationList.containsKey(_procinstId + _actId)) {
					durationList.put(_procinstId + _actId, duration);
				} else {
					durationList.put(_procinstId + _actId, durationList
							.get(_procinstId + _actId)
							+ duration);
				}
				duration = durationList.get(_procinstId + _actId);
				if (duration > _overTimeMillis) {
					flag = true;
					sbs.put(_assignee + "|" + _procinstId + "|" + _actId,
							"订单编号：" + _orderCode + ",环节:" + _actName
									+ "，即将或者已经超期，请马上处理！\n");
					sbs.put(_assignee + "|" + _procinstId + "|" + _actId
							+ "_duration", duration.toString());
				}
			}
		}
		return flag;
	}

	private int getTimeStep(Calendar srcTime, Calendar timefrom1,
			Calendar timeto1, Calendar timefrom2, Calendar timeto2) {
		if (srcTime.getTimeInMillis() <= timefrom1.getTimeInMillis()) {
			return 1;
		} else if ((srcTime.getTimeInMillis() > timefrom1.getTimeInMillis())
				&& (srcTime.getTimeInMillis() <= timeto1.getTimeInMillis())) {
			return 2;
		} else if ((srcTime.getTimeInMillis() > timeto1.getTimeInMillis())
				&& (srcTime.getTimeInMillis() <= timefrom2.getTimeInMillis())) {
			return 3;
		} else if ((srcTime.getTimeInMillis() > timefrom2.getTimeInMillis())
				&& (srcTime.getTimeInMillis() <= timeto2.getTimeInMillis())) {
			return 4;
		} else if (srcTime.getTimeInMillis() > timeto2.getTimeInMillis()) {
			return 5;
		}
		return 0;
	}

	@RequestMapping(value = { "/saveExpired" }, method = RequestMethod.POST)
	@ResponseBody
	public Message saveExpired() {
		Message msg = null;
		Integer expiredType = Integer.parseInt(this.getRequest().getParameter(
				"expiredType"));
		String expiredReason = this.getRequest().getParameter("expiredReason");
		String assignee = this.getRequest().getParameter("assignee");
		String procinstId = this.getRequest().getParameter("procinstId");
		String actId = this.getRequest().getParameter("actId");
		String duration = this.getRequest().getParameter("duration");

		// String historySql="select * from "
		try {
			String sql = "select acm.id from act_ct_mapping acm where acm.procinstid='"
					+ procinstId + "'";
			// String acmId = jdbcTemplate.queryForObject(sql,
			// java.lang.String.class);
			sql = "select aha.id_ from act_hi_actinst aha where aha.proc_inst_id_='"
					+ procinstId
					+ "' and act_id_='"
					+ actId
					+ "' and end_time_ is null";
			String historyId = jdbcTemplate.queryForObject(sql,
					java.lang.String.class);
			ActExpiredLog log = new ActExpiredLog(expiredType, expiredReason,
					assignee, historyId, Long.parseLong(duration));
			// new ActExpiredLog(actId, acmId, actName,
			// expiredType, expiredReason, assignee,
			// procinstId,Long.parseLong(duration),historyId);
			commonManager.save(log);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("result", true);
			msg = new Message(map);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("DD-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	/**
	 * 保存用户的吐槽
	 * 
	 * @param userGrumble
	 * @return
	 */
	@RequestMapping(value = { "/saveGrumble" }, method = RequestMethod.POST)
	@ResponseBody
	public Message saveGrumble(UserGrumble userGrumble) {
		Message msg = null;
		try {
			UserGrumble saved = commonManager.save(userGrumble);
			if (saved != null && !StringUtil.isEmpty(saved.getId())) {
				msg = new Message("保存成功");
			} else {
				msg = new Message("保存失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("job-500", "获取数据失败！");
		}
		return msg;
	}
}
