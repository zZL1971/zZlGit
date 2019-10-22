package com.mw.framework.controller;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.main.dao.CustHeaderDao;
import com.main.dao.MaterialPriceConditionDao;
import com.main.dao.PriceConditionDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemPriceDao;
import com.main.dao.SaleOneCustDao;
import com.main.dao.TerminalClientDao;
import com.main.domain.cust.TerminalClient;
import com.main.domain.sale.SaleItem;
import com.main.manager.SaleManager;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysMesSendDao;
import com.mw.framework.domain.SysMesSend;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.StringUtil;

/**
 *
 */
@Controller
@RequestMapping("/core/sysMesSend/*")
public class SysMesSendController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(SysMesSendController.class);

	@Autowired
	private SysMesSendDao sysMesSendDao;

	/**
	 * 列出所有流程模板
	 */

	@RequestMapping(value = { "/query" }, method = RequestMethod.GET)
	public ModelAndView query(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "SysMesSendApp");
		return mav;
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForList(int page, int limit) {
		String msgTitle = this.getRequest().getParameter("msgTitle");
		String createUser = this.getRequest().getParameter("createUser");
		String createTime = this.getRequest().getParameter("createTime");
		String createTime2 = this.getRequest().getParameter("createTime2");
		String hasRead = this.getRequest().getParameter("hasRead");

		StringBuffer sb = new StringBuffer(
				"select * from SYS_MES_SEND where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		if (!StringUtils.isEmpty(msgTitle)) {
			sb.append(" and MSG_TITLE like ? ");
			params.add(StringHelper.like(String.valueOf(msgTitle)));
		}
		if (!StringUtils.isEmpty(createUser)) {
			sb.append(" and CREATE_USER = ? ");
			params.add(createUser);
		}
		if (!StringUtils.isEmpty(createTime)) {
			sb.append(" and CREATE_TIME >= ? ");
			params
					.add(DateTools.strToDate(createTime,
							DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(createTime2)) {
			sb.append(" and CREATE_TIME <= ? ");
			params.add(DateTools
					.strToDate(createTime2, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(hasRead)) {
			sb.append(" and HAS_READED = ? ");
			params.add(hasRead);
		}

		sb.append(" and receive_user = ? order by create_time desc ");
		params.add(this.getLoginUserId());

		// 获取总记录数
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(sb
				.toString(), params.toArray());

		StringBuffer pageSQL = new StringBuffer("select * from (");
		if (page - 1 == 0) {
			pageSQL.append(sb + " ) where rownum <= ?");
			params.add(limit);
		} else {
			pageSQL.append("select row_.*, rownum rownum_ from ( " + sb
					+ ") row_ where rownum <= ?) where rownum_ > ?");
			params.add(limit * page);
			params.add((page - 1) * limit);
		}

		// 总页数=总记录数/每页条数(系数加1)
		int totalPages = (totalElements.size() + limit - 1) / limit;

		// 多个时间字段转换
		// Map<String, SimpleDateFormat> formatMap = new HashMap<String,
		// SimpleDateFormat>();
		// formatMap.put("createTime", new SimpleDateFormat("yyyy-MM"));
		// formatMap.put("orderDate", new SimpleDateFormat("yyyy-MM-dd"));
		// formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));

		//System.out.println(pageSQL.toString());

		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
		formats.put("createTime", new SimpleDateFormat(DateTools.fullFormat));
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(), new MapRowMapper(true, formats));

		/*
		 * for (Map<String, Object> map : queryForList) { Object orderDate =
		 * map.get("orderDate"); if (orderDate != null) { map.put("orderDate",
		 * DateTools.formatDate((Date) orderDate, DateTools.defaultFormat)); } }
		 */
		//System.out.println(queryForList);
		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}

	@RequestMapping(value = "/findById", method = RequestMethod.GET)
	@ResponseBody
	public Message findById(String id) throws ParseException {
		Message msg = null;
		try {
			SysMesSend sysMesSend = sysMesSendDao.findOne(id);
			Map<String, Object> map = new HashMap<String, Object>();
			if (sysMesSend != null) {
				map.put("id", sysMesSend.getId());
				map.put("createTime", sysMesSend.getCreateTime() == null ? ""
						: DateTools.formatDate(sysMesSend.getCreateTime(),
								DateTools.fullFormat));
				map.put("updateTime", sysMesSend.getUpdateTime() == null ? ""
						: DateTools.formatDate(sysMesSend.getUpdateTime(),
								DateTools.fullFormat));
				map.put("createUser", sysMesSend.getCreateUser() == null ? ""
						: sysMesSend.getCreateUser());
				map.put("updateUser", sysMesSend.getUpdateUser() == null ? ""
						: sysMesSend.getUpdateUser());
				Field[] declaredFields = sysMesSend.getClass()
						.getDeclaredFields();
				for (int i = 0; i < declaredFields.length; i++) {
					Field field = declaredFields[i];
					String name = field.getName();
					Object property = BeanUtils.getValue(sysMesSend, name);
					if (property != null) {
						if (name.equals("sendTime")) {
							map.put(name, sysMesSend.getSendTime() == null ? ""
									: DateTools.formatDate(sysMesSend
											.getSendTime(),
											DateTools.fullFormat));
						} else {
							map.put(name, property);
						}
					}
				}
			}
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort" };
//			System.out.println(JSONObject.fromObject(map, super
//					.getJsonConfig(strings)));
			msg = new Message(JSONObject.fromObject(map, super
					.getJsonConfig(strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-GET-500", "数据加载失败!");
		}
		return msg;
	}

	@RequestMapping(value = "/updateSysMesSendByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message updateSysMesSendByIds(String[] ids) {
		Message msg = null;
		try {
			if (ids != null && ids.length > 0) {
				jdbcTemplate
						.update("update sys_mes_send set has_readed='1' where id in("
								+ StringUtil.arrayToString(ids) + ")");
			}
			msg = new Message("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}
	
}
