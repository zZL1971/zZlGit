package com.main.controller;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.main.bean.BgBean;
import com.main.dao.BgHeaderDao;
import com.main.dao.BgItemDao;
import com.main.dao.SaleHeaderDao;
import com.main.domain.bg.BgHeader;
import com.main.domain.bg.BgItem;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sys.SysBz;
import com.main.manager.BgManager;
import com.main.manager.SaleManager;
import com.main.manager.SalePrModManager;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.domain.SysMesSend;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.FlowManager;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.manager.SysMesSendManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.StringUtil;
import com.mw.framework.utils.ZStringUtils;

/**
 *
 */
@Controller
@RequestMapping("/main/bg/*")
public class BgController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(BgController.class);

	public static String BG_PRE = "BG";

	@Autowired
	private BgHeaderDao bgHeaderDao;

	@Autowired
	private BgItemDao bgItemDao;

	@Autowired
	private BgManager bgManager;

	@Autowired
	private FlowManager flowManager;

	@Autowired
	private SerialNumberManager serialNumberManager;

	@Autowired
	private CommonManager commonManager;
	
	@Autowired
	private SaleHeaderDao saleHeaderDao;
	
	@Autowired
	private SaleManager saleManager;
	
	@Autowired
	TaskService taskService;

	@Autowired
	private SysMesSendManager sysMesSendManager;

	/**
	 * 列出所有流程模板
	 */
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public ModelAndView list(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "BgApp");
		return mav;
	}

	@RequestMapping(value = { "/query" }, method = RequestMethod.GET)
	public ModelAndView query(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "BgApp");
		return mav;
	}

	@RequestMapping(value = { "/query/createUser" }, method = RequestMethod.GET)
	public ModelAndView queryCreateUser(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "BgCreateUserApp");
		return mav;
	}

	@RequestMapping(value = { "/query/kunnr" }, method = RequestMethod.GET)
	public ModelAndView queryKunnr(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "BgKunnrApp");
		return mav;
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForList(int page, int limit) {
		String bgCode = this.getRequest().getParameter("bgCode");
		String orderCode = this.getRequest().getParameter("orderCode");
		String clients = this.getRequest().getParameter("clients");
		String bgType = this.getRequest().getParameter("bgType");
		String contacts = this.getRequest().getParameter("contacts");
		String tel = this.getRequest().getParameter("tel");
		String orderStatus = this.getRequest().getParameter("orderStatus");
		String queryBgType = this.getRequest().getParameter("queryBgType");
		//创建人 
		String createUser=this.getRequest().getParameter("createUser");
		String orderType=this.getRequest().getParameter("orderType");
		
		/**
		 * 审批时间
		 */
		String updateTimeF=this.getRequest().getParameter("updateTimeF");
		String updateTimeT=this.getRequest().getParameter("updateTimeT");

		StringBuffer sb = new StringBuffer(
				"select t.* from bg_view t where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		if (!StringUtils.isEmpty(bgCode)) {
			sb.append(" and BG_CODE like ? ");
			params.add(StringHelper.like(String.valueOf(bgCode)));
		}
		if (!StringUtils.isEmpty(orderCode)) {
			sb.append(" and ORDER_CODE like ? ");
			params.add(StringHelper.like(String.valueOf(orderCode)));
		}
		if (!StringUtils.isEmpty(clients)) {
			sb.append(" and CLIENTS like ? ");
			params.add(StringHelper.like(String.valueOf(clients)));
		}
		if (!StringUtils.isEmpty(bgType)) {
			sb.append(" and BG_TYPE = ? ");
			params.add(bgType);
		}
		if (!StringUtils.isEmpty(contacts)) {
			sb.append(" and CONTACTS like ? ");
			params.add(StringHelper.like(String.valueOf(contacts)));
		}
		if (!StringUtils.isEmpty(tel)) {
			sb.append(" and TEL like ? ");
			params.add(StringHelper.like(String.valueOf(tel)));
		}
		if (!StringUtils.isEmpty(orderStatus)) {
			sb.append(" and ORDER_STATUS = ? ");
			params.add(orderStatus);
		}
		if (!StringUtils.isEmpty(orderType)) {
			sb.append(" and ORDER_TYPE = ? ");
			params.add(orderType);
		}
		if(!StringUtils.isEmpty(updateTimeF)){
			sb.append(" and UPDATE_TIME >= date'"+updateTimeF+"'");
		}
		if(!StringUtils.isEmpty(updateTimeT)){
			sb.append(" and UPDATE_TIME <= date'"+updateTimeT+"'");
		} 
		if(!StringUtils.isEmpty(createUser)){
			sb.append(" and create_user like '%"+createUser+"%'");
		}
		// 查询当前经销商的
		if ("2".equals(queryBgType)) {
			SysUser loginUser = this.getLoginUser();
			if (loginUser != null) {
				String kunnr = loginUser.getKunnr();
				if (ZStringUtils.isNotEmpty(kunnr)) {
					sb.append(" and SHOU_DA_FANG = '" + kunnr + "' ");
				} else {
					sb.append(" and SHOU_DA_FANG = 'ZZZZZZZZZZZZZZZZZZZZZZZ' ");
				}
			} else {
				sb.append(" and SHOU_DA_FANG = 'ZZZZZZZZZZZZZZZZZZZZZZZ' ");
			}
		}// 查询自己创建的
		else if ("3".equals(queryBgType)) {
			sb.append(" and CREATE_USER = '" + this.getLoginUserId() + "' ");
		}

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
		// Map<String,SimpleDateFormat> formatMap = new HashMap<String,
		// SimpleDateFormat>();
		// formatMap.put("createTime", new SimpleDateFormat("yyyy-MM"));
		// formatMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd"));
		// formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(), new MapRowMapper(true));

		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Message save(@Valid BgHeader bgHeader, BindingResult result,
			@RequestBody BgBean bgBean) {
		Message msg = null;
		// 判断前台输入的值类型是否跟后台的匹配
		if (result.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			// 显示错误信息
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getField() + "|"
						+ fieldError.getDefaultMessage());
			}
			msg = new Message("DD-V-500", sb.toString());
		} else {
			try {
				List<BgItem> bgItemList = bgBean.getBgItemList();
				for (BgItem bgItem : bgItemList) {
					String id = bgItem.getId();
					if (ZStringUtils.isEmpty(id)) {
						String saleItemId = bgItem.getSaleItemId();
						List<Map<String, Object>> queryForList = jdbcTemplate
								.queryForList("select t.id,t.posex,t2.bg_code,t2.order_code from bg_item t inner join bg_header t2 on t.pid=t2.id where t.SALE_ITEM_ID='"
										+ saleItemId + "'");
						if (queryForList != null && queryForList.size() > 0) {
							msg = new Message("BG-500", "订单("
									+ queryForList.get(0).get("ORDER_CODE")
									+ ")的 " + queryForList.get(0).get("POSEX")
									+ " 行项目已下变更单，<br/>变更单号为"
									+ queryForList.get(0).get("BG_CODE") + "！");
							return msg;
						}
					}
				}
				BgHeader obj = bgManager.save(bgHeader, bgBean);

				/************** 保存后，处理返回信息(start) ******************/
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", obj.getId());
				map.put("createTime", obj.getCreateTime() == null ? ""
						: DateTools.formatDate(obj.getCreateTime(),
								DateTools.fullFormat));
				map.put("updateTime", obj.getUpdateTime() == null ? ""
						: DateTools.formatDate(obj.getUpdateTime(),
								DateTools.fullFormat));
				map.put("createUser", obj.getCreateUser() == null ? "" : obj
						.getCreateUser());
				map.put("updateUser", obj.getUpdateUser() == null ? "" : obj
						.getUpdateUser());
				Field[] declaredFields = obj.getClass().getDeclaredFields();
				for (int i = 0; i < declaredFields.length; i++) {
					Field field = declaredFields[i];
					String name = field.getName();
					if (name == "bgItemSet") {
						continue;
					} else {
						Object property = BeanUtils.getValue(obj, name);
						if (property != null) {
							map.put(name, property);
						}
					}
				}
				String[] strings = new String[] { "hibernateLazyInitializer",
						"handler", "fieldHandler", "sort", "bgItemSet",
						"serialVersionUID" };
//				System.out.println(JSONObject.fromObject(map, super
//						.getJsonConfig(strings)));
				msg = new Message(JSONObject.fromObject(map, super
						.getJsonConfig(strings)));
				/************** 保存后，处理返回信息(end) ******************/
			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("DD-S-500", e.getLocalizedMessage());
			}
		}
		return msg;
	}

	@RequestMapping(value = "/findById", method = RequestMethod.GET)
	@ResponseBody
	public Message findById(String id) throws ParseException {
		Message msg = null;
		try {
			BgHeader bgHeader = bgHeaderDao.findOne(id);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", bgHeader.getId());
			map.put("createTime", bgHeader.getCreateTime() == null ? ""
					: DateTools.formatDate(bgHeader.getCreateTime(),
							DateTools.fullFormat));
			map.put("updateTime", bgHeader.getUpdateTime() == null ? ""
					: DateTools.formatDate(bgHeader.getUpdateTime(),
							DateTools.fullFormat));
			map.put("createUser", bgHeader.getCreateUser() == null ? ""
					: bgHeader.getCreateUser());
			map.put("updateUser", bgHeader.getUpdateUser() == null ? ""
					: bgHeader.getUpdateUser());
			Field[] declaredFields = bgHeader.getClass().getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				String name = field.getName();
				if (name == "bgItemSet") {
					continue;
				} else {
					Object property = BeanUtils.getValue(bgHeader, name);
					if (property != null) {
						map.put(name, property);
					}
				}
			}
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "bgItemSet",
					"serialVersionUID" };
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

	/**
	 * 根据SaleHeader.id查找对应SaleItem
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/findItemsByPid", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray findItemsByPid(String pid) {
		List<BgItem> findItemByPid = bgItemDao.findItemsByPid(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "bgHeader" };
//		System.out.println(JSONArray.fromObject(findItemByPid, super
//				.getJsonConfig(strings)));
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		return JSONArray
				.fromObject(findItemByPid, super.getJsonConfig(strings));
	}

	/**
	 * 根据ids查找custItem，(不做真删除)更新删除标志
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBgItemByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteBgItemByIds(String[] ids) {
		Message msg = null;
		try {
			bgManager.delete(ids, BgItem.class);
			// final List list = new ArrayList();
			// for (String id : ids) {
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("id", id);
			// map.put("update_user", this.getLoginUser().getLoginNo());
			// list.add(map);
			// }
			//
			// String sql =
			// "update bg_item set status='0',update_time=?,update_user=? where id=?";
			// jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
			// {
			// public int getBatchSize() {
			// return list.size();
			// // 这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();
			// }
			//
			// @Override
			// public void setValues(PreparedStatement ps, int i)
			// throws SQLException {
			// Map<String, Object> map = (Map<String, Object>) list.get(i);
			// ps.setTimestamp(1,
			// new Timestamp(System.currentTimeMillis()));
			// ps.setString(2, map.get("update_user") == null ? "" : map
			// .get("update_user").toString());
			// ps.setString(3, map.get("id") == null ? "" : map.get("id")
			// .toString());
			// }
			// });
			msg = new Message("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	@RequestMapping(value = "/submit", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message submit(String id, String status, String title, String step,
			String remarks) {
		JSONObject obj = new JSONObject();
		try {
			SysUser sysUser=this.getLoginUser();
			return bgManager.submit(id, status, title, step, remarks, sysUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("处理异常，请联系管理员:"+e.getLocalizedMessage());
			obj.put("msg", "处理异常，请联系管理员");
			Message msg = new Message(obj);
			return msg;
		}
	}
	
	@RequestMapping(value="existUnCheck",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message existUnCheck(String saleHeaderId){
		Message msg=null;
		try{
			StringBuilder sql= new StringBuilder("select * from bg_header bh where 1=1 and order_status='B' ");
			Map<String,Object> resultMap=new HashMap<String, Object>();
			List<Object> params=new ArrayList<Object>();
			if(!com.alibaba.druid.util.StringUtils.isEmpty(saleHeaderId)){
				sql.append(" and order_code=( select sh.order_code from sale_header sh where sh.id= ?)  ");
				params.add(saleHeaderId);
			}
//			if(!com.alibaba.druid.util.StringUtils.isEmpty(status)){
//				sql.append(" and order_status= ? ");
//				params.add(orderCode);
//			}
			List<Map<String,Object>> list=jdbcTemplate.queryForList(sql.toString(),params.toArray());
			if(list!=null && list.size()>0){
				resultMap.put("exist", true);
			}else{
				resultMap.put("exist", false);
			}
			msg=new Message(resultMap);
		}catch(Exception e){
			e.printStackTrace();
		}
		return msg;
	}
	
}
