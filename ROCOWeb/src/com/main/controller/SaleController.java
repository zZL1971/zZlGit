package com.main.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.main.bean.SaleBean;
import com.main.dao.CustHeaderDao;
import com.main.dao.CustLogisticsDao;
import com.main.dao.MaterialHeadDao;
import com.main.dao.MaterialPriceDao;
import com.main.dao.MaterialSanjianHeadDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemPriceDao;
import com.main.dao.SaleLogisticsDao;
import com.main.dao.SaleOneCustDao;
import com.main.dao.SysJobPoolDao;
import com.main.dao.TerminalClientDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustItem;
import com.main.domain.cust.TerminalClient;
import com.main.domain.mm.MaterialFile;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemPrice;
import com.main.domain.sale.SaleLogistics;
import com.main.domain.sale.SaleOneCust;
import com.main.domain.sys.SysJobPool;
import com.main.manager.SaleManager;
import com.main.manager.SysJobPoolManager;
import com.main.util.MyFileUtil;
import com.mw.framework.activiti.JumpTaskCmd;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysActCTMappingDao;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.NumberUtils;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.webservice.RocoImos;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;


/**
 *
 */
@Controller
@RequestMapping("/main/sale/*")
public class SaleController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(SaleController.class);

	@Autowired
	CommonManager commonManager;

	@Autowired
	private SaleHeaderDao saleHeaderDao;

	@Autowired
	private SaleItemDao saleItemDao;

	@Autowired
	private SaleOneCustDao saleOneCustDao;
	
	@Autowired
	private SaleLogisticsDao saleLogisticsDao;
	
	@Autowired
	private MyGoodsController  myGoodsController;
	@Autowired
	private SysDataDictDao sysDataDictDao;

	@Autowired
	private SaleManager saleManager;
	
	@Autowired
	private MaterialPriceDao materialPriceDao;

	@Autowired
	private SaleItemPriceDao saleItemPriceDao;

	@Autowired
	private CustHeaderDao custHeaderDao;

	@Autowired
	private TerminalClientDao terminalClientDao;
	
	@Autowired
	private MaterialSanjianHeadDao materialSanjianHeadDao;

	@Autowired
	private MaterialHeadDao materialHeadDao;
	
	@Autowired
	private CustLogisticsDao custLogisticsDao;
	
	@Autowired
	TaskService taskService;
	
	@Autowired
	SysJobPoolDao sysJobPoolDao;
	
	@Autowired
	SysTrieTreeDao sysTrieTreeDao;
	/**
	 * jedis连接池
	 */
	@Autowired
	private JedisPool jedisPool;
	
	private static final String INSERT_XML_LIST="insert into xml_request_list(id,request_time,imos_path,file_name,frequency,status) values( ?, sysdate,?,?,1,2)";

	/**
	 * 列出所有流程模板
	 */
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public ModelAndView list(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "SaleApp");
		return mav;
	}

	@RequestMapping(value = { "/query" }, method = RequestMethod.GET)
	public ModelAndView query(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "Sale2App");
		return mav;
	}

	@RequestMapping(value = { "/query/createUser" }, method = RequestMethod.GET)
	public ModelAndView queryCreateUser(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "Sale2CreateUserApp");
		return mav;
	}

	@RequestMapping(value = { "/checksheet" }, method = RequestMethod.GET)
	public ModelAndView queryCheckSheet(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "SaleCheckSheetApp");
		return mav;
	}

	@RequestMapping(value = { "/query/kunnr" }, method = RequestMethod.GET)
	public ModelAndView queryKunnr(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "Sale2KunnrApp");
		return mav;
	}

	@RequestMapping(value = { "/query/bujian" }, method = RequestMethod.GET)
	public ModelAndView queryBujian(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "Sale2BujianApp");
		return mav;
	}

	@RequestMapping(value = { "/queryTc" }, method = RequestMethod.GET)
	public ModelAndView queryTc(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "Sale3App");
		return mav;
	}

	/**
	 * 从SAP查询客户对帐单信息
	 * 
	 * @param page
	 * @param limit
	 * @param kunnr
	 * @param startDate
	 * @param endDate
	 */
	@RequestMapping(value = { "/listchecksheet" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean listCheckSheet(int page, int limit) {
		// public Message listCheckSheet(int page, int limit) {
		// Message msg=null;
		int totalSize = 0;

		List<Map<String, Object>> queryForList = new ArrayList<Map<String, Object>>();
		try {
			// String kunnr = this.getRequest().getParameter("shouDaFang");
			// 通过登录用户ID，取得该用户在SAP的用户编码
			String id = this.getLoginUserId();
			SysUser findOne = commonManager.getOne(id, SysUser.class);
			String kunnr = findOne.getKunnr();

			// String queryType = this.getRequest().getParameter("queryType");

			Date date = null;
			Date dateC = null;

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			String startDate = this.getRequest().getParameter("startDate");
			date = df.parse(startDate);// 输入的日期

			dateC = df.parse("2016-01-01");

			// 输入日期在2016-01-01前
			if (date.before(dateC)) {
				startDate = "2016-01-01";
			}
			String endDate = this.getRequest().getParameter("endDate");

			JCoDestination connect = SAPConnect.getConnect();
			JCoFunction function = connect.getRepository().getFunction(
					"ZRFC_SD_BILL_INFO");// ZRFC_SD_BILL_INFO
			function.getImportParameterList().setValue("P_KUNNR", kunnr);
			if (!StringUtils.isEmpty(startDate)) {
				function.getImportParameterList()
						.setValue("S_BUDAT", startDate);// 查询的开始时间
			}

			if (!StringUtils.isEmpty(startDate)) {
				function.getImportParameterList().setValue("E_BUDAT", endDate);// 查询的结束时间
			}

			// function.getImportParameterList().setValue("E_BUDAT",endDate);
			function.execute(connect);

			com.sap.conn.jco.JCoParameterList list = function
					.getTableParameterList();
			com.sap.conn.jco.JCoTable table = list.getTable("ZSD_BILL_INFO");
			List<Map<String, Object>> lt = new ArrayList<Map<String, Object>>();
			Map<Integer, Map<String, Object>> map2 = new HashMap<Integer, Map<String, Object>>();
			if (table.getNumRows() > 0) {
				// table.firstRow();
				for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {
					Map<String, Object> map = new HashMap<String, Object>();
					// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String postDate = df.format(table.getValue("BUDAT"));// 凭证中的过帐日期
					String sapOrderCode = (String) table.getValue("BELNR");// 对账单凭证号/订单号
					String textOrder = (String) table.getValue("SGTXT");// 项目文本
					BigDecimal receiptAmount = (BigDecimal) table
							.getValue("DMBTR");// 收款金额
					BigDecimal placeAmount = (BigDecimal) table
							.getValue("NETWR");// 下单金额
					BigDecimal balanceAmount = (BigDecimal) table
							.getValue("YE");// 余额
					String orderReason = (String) table.getValue("AUGRU");// 订购原因(
																			// 业务原因
																			// )
					String beZei = (String) table.getValue("BEZEI");// 订购原因(
																	// 业务原因 )
					// String orderReason =
					// (String)table.getValue("AUG");//订购原因( 业务原因 )
					// Object orderReason = table.getValue("AUG");
					// System.out.println("sapOrderCode="+sapOrderCode+",orderReason="+orderReason);
					// Date releaseDate =
					// (Date)table.getValue("MAHDT");//与客户联系的最后日期
					String releaseDate = "";
					if (table.getValue("MAHDT") != null) {
						releaseDate = df.format(table.getValue("MAHDT"));// 凭证中的过帐日期
					}
					// String releaseDate =
					// df.format(table.getValue("MAHDT"));//凭证中的过帐日期
					// String postDate =
					// df.format(table.getValue("MAHDT"));//凭证中的过帐日期

					map.put("postdate", postDate);
					map.put("sapordercode", sapOrderCode);
					map.put("textorder", textOrder);
					map.put("receiptamount", receiptAmount);
					map.put("placeamount", placeAmount);
					map.put("balanceamount", balanceAmount);
					map.put("bezei", beZei);
					map.put("orderreason", orderReason);
					map.put("releasedate", releaseDate);
					// lt.add(map);
					map2.put(i, map);
				}
			}/*
			 * else{ Map<String,Object> map3=new HashMap<String, Object>();
			 * map3.put("postdate", 0); map3.put("sapordercode", 0);
			 * map3.put("textorder", 0); map3.put("receiptamount", 0);
			 * map3.put("placeamount", 0); map3.put("balanceamount", 0);
			 * map3.put("orderreason", 0); map3.put("releasedate", 0);
			 * //lt.add(map); map2.put(0, map3);
			 * 
			 * }
			 */

			totalSize = map2.size(); // 得到总记录数 179

			if (totalSize > 0) {

				int remain = totalSize % limit;
				int tempPage;// 总页数
				if (remain == 0) {
					// 没余数情况
					tempPage = totalSize / limit;// 得到总的页数
				} else {
					// 有余数情况
					tempPage = (totalSize / limit) + 1;
				}
				int startNum = (page - 1) * limit;// 25*6=150 7*25=175
				// int endNum = limit * page;//25*7=175 8*25=200
				int endNum = (page < tempPage) ? limit * page : totalSize;
				for (int i = startNum; i < endNum; i++) {

					Map<String, Object> aa = map2.get(i);
					// System.out.println("sapordercode-->"+aa.get("sapordercode"));
					queryForList.add(aa);
				}
				// msg=new Message(lt);
			} else {
				// 该用户在SAP无任何对帐信息

			}

		} catch (JCoException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new JdbcExtGridBean(1, totalSize, limit, queryForList);
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForList(int page, int limit) {
		String orderCode = this.getRequest().getParameter("orderCode");
		String orderType = this.getRequest().getParameter("orderType");
		String porderCode = this.getRequest().getParameter("serialNumber");
		String startDate = this.getRequest().getParameter("startDate");
		String endDate = this.getRequest().getParameter("endDate");
		String shouDaFang = this.getRequest().getParameter("shouDaFang");
		String dianMianTel = this.getRequest().getParameter("dianMianTel");
		String name1 = this.getRequest().getParameter("name1");
		String tel = this.getRequest().getParameter("tel");
		String queryType = this.getRequest().getParameter("queryType");
		String kunnrName1 = this.getRequest().getParameter("kunnrName1");
		String sapOrderCode = this.getRequest().getParameter("sapOrderCode");
		String orderHuanJie = this.getRequest().getParameter("orderHuanJie");
		String isYp = this.getRequest().getParameter("isYp");
		//地区
		String regio=this.getRequest().getParameter("regio");
		//大区
		String bzirk=this.getRequest().getParameter("bzirk");

		String address = this.getRequest().getParameter("address");
		//销售分类
		String saleFor=this.getRequest().getParameter("saleFor");
		/**
		 * 预计出货日期
		 */
		String yuJiDateF=this.getRequest().getParameter("yuJiDateF");
		String yuJiDateT=this.getRequest().getParameter("yuJiDateT");
		/*
		 * String address = ""; try { address = new
		 * String(address2.getBytes("GBK"),"UTF-8"); } catch
		 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		String queryView;
		if ("3".equals(queryType)) {
			queryView = " ord_info_rp_ps1 ";//sap
		} else if (!StringUtils.isEmpty(orderHuanJie)&&!"receivetask2".equals(orderHuanJie)) {
			queryView = " ORD_INFO_RP4 ";
		} else {
			queryView = " ORD_INFO_RP41 ";
		}
		String SQL_LIMIT="";
		String SQL_DATA="";
		if (!(StringUtils.isEmpty(saleFor))) {
			SQL_LIMIT= "select count(1) as TOTAL from (select distinct t.*  from " + queryView
					+ " t left join sale_header sh on sh.order_code = t.ORDER_CODE left join sale_item si on si.pid = sh.id left join material_head mh on mh.id = si.material_head_id";
			SQL_DATA= "select a.* from(select distinct t.* from " + queryView + " t left join sale_header sh on sh.order_code = t.ORDER_CODE left join sale_item si on si.pid = sh.id left join material_head mh on mh.id = si.material_head_id";
		}else{
			SQL_LIMIT= "select count(1) as TOTAL from " + queryView
					+ " t ";
			SQL_DATA= "select t.* from " + queryView + " t ";
		}
		
		StringBuffer sb = new StringBuffer();
		// sql params
		List<Object> params = new ArrayList<Object>();
		
		if (!(StringUtils.isEmpty(saleFor))) {
			sb.append(" left join sale_item si on t.SAP_ORDER_CODE=si.sap_code left join material_head mh on mh.id=si.material_head_id where 1=1 and mh.sale_for= ?");
			params.add(saleFor);
		}else{
			sb.append(" where 1=1 ");
		}
		if (!(StringUtils.isEmpty(sapOrderCode))) {
			sb.append(" and t.SAP_CODE like ?");
			params.add(StringHelper.like(String.valueOf(sapOrderCode)));
		}
		if (!(StringUtils.isEmpty(address))) {
			sb.append(" and t.address like ?");
			params.add(StringHelper.like(String.valueOf(address)));
		}
		if (!StringUtils.isEmpty(orderCode)) {
			sb.append(" and INSTR(t.ORDER_CODE,?)>0 ");
			params.add(String.valueOf(orderCode));
		}
		if (!StringUtils.isEmpty(orderType)) {
			sb.append(" and t.ORDER_TYPE = ? ");
			params.add(orderType);
		}
		if (!StringUtils.isEmpty(porderCode)) {
			sb.append(" and t.P_ORDER_CODE = ? ");
			params.add(porderCode);
		}
		if(!StringUtils.isEmpty(yuJiDateF)){
			sb.append(" and t.YU_JI_DATE>= ?");
			params.add(DateTools.strToDate(yuJiDateF, DateTools.defaultFormat));
		}
		if(!StringUtils.isEmpty(yuJiDateT)){
			sb.append(" and t.YU_JI_DATE<= ?");
			params.add(DateTools.strToDate(yuJiDateT, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(startDate)) {
			sb.append(" and t.ORDER_DATE >= ? ");
			params.add(DateTools.strToDate(startDate, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(endDate)) {
			sb.append(" and t.ORDER_DATE <= ? ");
			params.add(DateTools.strToDate(endDate, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(shouDaFang)) {
			sb.append(" and t.SHOU_DA_FANG like ? ");
			params.add(StringHelper.like(String.valueOf(shouDaFang)));
		}
		if (!StringUtils.isEmpty(dianMianTel)) {
			sb.append(" and t.DIAN_MIAN_TEL like ? ");
			params.add(StringHelper.like(String.valueOf(dianMianTel)));
		}
		if (!StringUtils.isEmpty(name1)) {
			sb.append(" and t.NAME1 like ? ");
			params.add(StringHelper.like(String.valueOf(name1)));
		}
		if (!StringUtils.isEmpty(tel)) {
			sb.append(" and t.TEL like ? ");
			params.add(StringHelper.like(String.valueOf(tel)));
		}
		if (!StringUtils.isEmpty(kunnrName1)) {
			sb.append(" and t.KUNNR_NAME1 like ? ");
			params.add(StringHelper.like(String.valueOf(kunnrName1)));
		}
//		if (!StringUtils.isEmpty(sapOrderCode)) {
//			sb.append(" and t.SAP_ORDER_CODE like ? ");
//			params.add(StringHelper.like(String.valueOf(sapOrderCode)));
//		}
		if (!StringUtils.isEmpty(orderHuanJie)) {
			if ("usertask_valuation".equals(orderHuanJie)) {// 价格审核
				sb.append(" and (ACT_ID = 'usertask_valuation' or  ACT_ID = 'usertask2') ");
			} else if ("usertask_finance".equals(orderHuanJie)) {// 财务确认
				sb.append(" and (ACT_ID = 'usertask_finance' or  ACT_ID = 'usertask4') ");
			} else if ("usertask_store_confirm".equals(orderHuanJie)) {// 客户确认
				sb.append(" and (ACT_ID = 'usertask_store_confirm' or  ACT_ID = 'usertask3') ");
			} else if ("usertask_store2".equals(orderHuanJie)) {
				sb.append(" and (ACT_ID = 'usertask_store' and ORDER_TYPE IN ('OR3','OR4') )");
			} else if ("usertask_store".equals(orderHuanJie)){
				sb.append(" and (ACT_ID = 'usertask_store' and ORDER_TYPE  NOT IN ('OR3','OR4') )");
			} else {
				sb.append(" and ACT_ID = ? ");
				params.add(orderHuanJie);
			}
		}
		if (!StringUtils.isEmpty(isYp)) {
			sb.append(" and t.yp = ? ");
			params.add(String.valueOf(isYp));
		}
		if(!StringUtils.isEmpty(regio)){
			sb.append(" and t.regio = ? ");
			params.add(String.valueOf(regio));
		}
		if(!StringUtils.isEmpty(bzirk)){
			sb.append(" and t.bzirk = ? ");
			params.add(String.valueOf(bzirk));
		}
		//销售分类
//		if(!StringUtils.isEmpty(saleFor)){
//			sb.append(" and sale = ? ");
//			params.add(String.valueOf(saleFor));
//		}

		// 查询当前经销商的
		if ("2".equals(queryType)) {
			SysUser loginUser = this.getLoginUser();
			if (loginUser != null) {
				String kunnr = loginUser.getKunnr();
				if (ZStringUtils.isNotEmpty(kunnr)) {
					sb.append(" and t.SHOU_DA_FANG = '" + kunnr
							+ "' order by t.ORDER_CODE desc ");
				} else {
					sb.append(" and t.SHOU_DA_FANG = 'ZZZZZZZZZZZZZZZZZZZZZZZ' order by t.ORDER_CODE desc ");
				}
			} else {
				sb.append(" and t.SHOU_DA_FANG = 'ZZZZZZZZZZZZZZZZZZZZZZZ' order by t.ORDER_CODE desc ");
			}
		}// 查询自己创建的
		else if ("3".equals(queryType)) {
			// sb.append(" and CREATE_USER = '" + this.getLoginUserId() + "' ");
			sb.append(" and t.ASSIGNEE = '" + this.getLoginUserId()
					+ "' order by t.ORDER_CODE desc ");
		}
		
		if(!(StringUtils.isEmpty(saleFor))){
			sb.append(") a");
		}
		// 获取总记录数
		Map<String, Object> totalElements = (Map<String, Object>) jdbcTemplate
				.queryForMap(SQL_LIMIT + sb.toString(), params.toArray());
		// System.out.println("获取总记录数:"+SQL_LIMIT + sb.toString());
		int totalSize = ((BigDecimal) totalElements.get("TOTAL")).intValue();
		StringBuffer pageSQL = new StringBuffer("select * from (");
		pageSQL.append("select row_.*, rownum rownum_ from ( " + SQL_DATA + sb
				+ ") row_ where rownum <= ?) where rownum_ > ?");
		params.add(limit * page);
		params.add((page - 1) * limit);
		// if (page - 1 == 0) {
		// pageSQL.append(SQL_DATA+sb + " ) where rownum <= ?");
		// params.add(limit);
		// } else {
		// pageSQL.append("select row_.*, rownum rownum_ from ( " + SQL_DATA+sb
		// + ") row_ where rownum <= ?) where rownum_ > ?");
		// params.add(limit * page);
		// params.add((page - 1) * limit);
		// }

		// 总页数=总记录数/每页条数(系数加1)
		int totalPages = (totalElements.size() + limit - 1) / limit;

		// 多个时间字段转换
		// Map<String, SimpleDateFormat> formatMap = new HashMap<String,
		// SimpleDateFormat>();
		// formatMap.put("createTime", new SimpleDateFormat("yyyy-MM"));
		// formatMap.put("orderDate", new SimpleDateFormat("yyyy-MM-dd"));
		// formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));

		// System.out.println(pageSQL.toString());

		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
		formats.put("orderDate", new SimpleDateFormat(DateTools.defaultFormat));
		/*
		 * formats .put("createTime", new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		 */
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(), new MapRowMapper(true, formats));

		// System.out.println("pageSQL="+pageSQL);
		SysUser sysUser = (SysUser) this.getRequest().getSession()
				.getAttribute("CURR_USER");
		boolean money = sysUser.isMoney();
		if (!money) {
			for (Iterator iterator = queryForList.iterator(); iterator
					.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				map.remove("orderTotal");
				map.remove("fuFuanCond");
				map.remove("fuFuanMoney");
				map.remove("payType");

			}
		}
		/*
		 * for (Map<String, Object> map : queryForList) { Object orderDate =
		 * map.get("orderDate"); if (orderDate != null) { map.put("orderDate",
		 * DateTools.formatDate((Date) orderDate, DateTools.defaultFormat)); } }
		 */
		// System.out.println(queryForList);
		return new JdbcExtGridBean(totalPages, totalSize, limit, queryForList);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Message save(@Valid SaleHeader saleHeader, BindingResult result,
			@RequestBody SaleBean saleBean) {
		Message msg = null;

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
				List<SaleItem> saleItemList = saleBean.getSaleItemList();
				if (saleItemList != null && saleItemList.size() > 0) {
					for (int i = 0; i < saleItemList.size(); i++) {
						SaleItem saleItem = saleItemList.get(i);
						if("1"!=saleItem.getIsStandard()) {
						}else {
							// 新增的行项目才要校验
							if (ZStringUtils.isEmpty(saleItem.getId())) {
								List<Map<String, Object>> queryForList = jdbcTemplate
										.queryForList("select MY_GOODS_ID from SALE_ITEM where MY_GOODS_ID ='"
												+ saleItem.getMyGoodsId() + "'");
								if (queryForList != null && queryForList.size() > 0) {
									msg = new Message("SALE-500", "商品("
											+ saleItem.getMaktx() + ")已被下单！");
									return msg;
								}
							}
						}
					}
				}

				SaleHeader obj = saleManager.save(saleHeader, saleBean);
				String pid = saleHeader.getId();

				/************** 保存后，处理返回信息(start) ******************/
				if("OR3".equals(saleHeader.getOrderType())||"OR4".equals(saleHeader.getOrderType())) {
			
	            	/**
	            	 * 修改我的商品状态 
	            	 */
	            	myGoodsController.saleAfterActivate(pid);
				}
				Map<String, Object> map = new HashMap<String, Object>();
				
				String shouDaFang = saleHeader.getShouDaFang();
				List<CustHeader> findByCode = custHeaderDao
						.findByCode(shouDaFang);
				if (findByCode != null && findByCode.size() > 0) {
					map.put("kunnrName1", ZStringUtils.isEmpty(findByCode
							.get(0).getName1()) ? "" : findByCode.get(0)
							.getName1());
				}

				map.put("id", obj.getId());
				map.put("orderCode", obj.getOrderCode());
				map.put("shouDaFang", obj.getShouDaFang());
				map.put("songDaFang", obj.getSongDaFang());
				map.put("createTime",
						obj.getCreateTime() == null ? "" : DateTools
								.formatDate(obj.getCreateTime(),
										DateTools.fullFormat));
				map.put("updateTime",
						obj.getUpdateTime() == null ? "" : DateTools
								.formatDate(obj.getUpdateTime(),
										DateTools.fullFormat));
				map.put("createUser",
						obj.getCreateUser() == null ? "" : obj.getCreateUser());
				map.put("updateUser",
						obj.getUpdateUser() == null ? "" : obj.getUpdateUser());
/*				Field[] declaredFields = obj.getClass().getDeclaredFields();
				for (int i = 0; i < declaredFields.length; i++) {
					Field field = declaredFields[i];
					String name = field.getName();
					if (name == "saleItemSet" || name == "terminalClient") {
						continue;
					} else {
						Object property = BeanUtils.getValue(obj, name);
						if (property != null) {
							if (name.equals("orderDate")
									|| name.equals("yuJiDate")) {
								map.put(name, DateTools.formatDate(
										(Date) property,
										DateTools.defaultFormat));
							} else {
								map.put(name, property);
							}
						}
					}
				}*/
				TerminalClient terminalClient2 = obj.getTerminalClient();
				map.put("tcId", terminalClient2.getId());
				Field[] declaredFields2 = terminalClient2.getClass()
						.getDeclaredFields();
				for (int i = 0; i < declaredFields2.length; i++) {
					Field field = declaredFields2[i];
					String name = field.getName();
					if (name == "saleHeader"||"_methods_"==name) {
						continue;
					} else {
						Object property = BeanUtils.getValue(terminalClient2,
								name);
						if (property != null) {
							if (name.equals("birthday")) {
								map.put(name, DateTools.formatDate(
										terminalClient2.getBirthday(),
										DateTools.defaultFormat));
							} else {
								map.put(name, property);
							}
						}
					}
				}
				String[] strings = new String[] { "hibernateLazyInitializer",
						"handler", "fieldHandler", "sort", "saleItemSet",
						"terminalClient", "saleOneCustSet", "serialVersionUID","saleLogisticsSet" };
				// System.out.println(JSONObject.fromObject(map,
				// super.getJsonConfig(strings)));
				msg = new Message(JSONObject.fromObject(map,
						super.getJsonConfig(strings)));
				/************** 保存后，处理返回信息(end) ******************/

			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("DD-S-500", e.getLocalizedMessage());
			}
		}

		return msg;

		/*
		 * // 判断前台输入的值类型是否跟后台的匹配 if (result.hasErrors()) { StringBuffer sb = new
		 * StringBuffer(); // 显示错误信息 List<FieldError> fieldErrors =
		 * result.getFieldErrors(); for (FieldError fieldError : fieldErrors) {
		 * sb.append(fieldError.getField() + "|" +
		 * fieldError.getDefaultMessage()); } msg = new Message("DD-V-500",
		 * sb.toString()); } else { try { String id = saleHeader.getId(); if
		 * (ZStringUtils.isEmpty(id)) { String orderCode =
		 * saleHeader.getOrderCode(); if (ZStringUtils.isEmpty(orderCode)) {
		 * String curSerialNumberFullYY = saleHeader .getShouDaFang() +
		 * serialNumberManager.curSerialNumberFullYY( "SALE", 4);
		 * saleHeader.setOrderCode(curSerialNumberFullYY); } } String saleDate =
		 * this.getRequest().getParameter("saleDate"); if
		 * (!StringUtils.isEmpty(saleDate)) {
		 * saleHeader.setOrderDate(DateTools.strToDate(saleDate,
		 * DateTools.defaultFormat)); } String createTime2 =
		 * this.getRequest().getParameter( "createTime2"); if
		 * (!StringUtils.isEmpty(createTime2)) {
		 * saleHeader.setCreateTime(DateTools.strToDate(createTime2,
		 * DateTools.fullFormat)); } Date createTime =
		 * saleHeader.getCreateTime();
		 * 
		 * TerminalClient terminalClient = saleBean.getTerminalClient();
		 * terminalClient.setSaleHeader(saleHeader); if (createTime != null) {
		 * terminalClient.setCreateTime(createTime);
		 * terminalClient.setCreateUser(saleHeader.getCreateUser()); }
		 * List<SaleItem> saleItemList = saleBean.getSaleItemList();
		 * Set<SaleItem> saleItemSet = new HashSet<SaleItem>(); for (SaleItem
		 * saleItem : saleItemList) { saleItem.setStatus("1");//
		 * 状态为1时正常，为0时表示已经删除 saleItem.setSaleHeader(saleHeader); if (createTime
		 * != null) { saleItem.setCreateTime(createTime);
		 * saleItem.setCreateUser(saleHeader.getCreateUser()); }
		 * saleItemSet.add(saleItem); } saleHeader.setSaleItemSet(saleItemSet);
		 * saleHeader.setTerminalClient(terminalClient); SaleHeader obj =
		 * saleManager.save(saleHeader);
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace(); msg = new
		 * Message("DD-S-500", e.getLocalizedMessage()); } }
		 */
	}

	@RequestMapping(value = "/addSave", method = RequestMethod.POST)
	@ResponseBody
	public Message addSave(@Valid SaleHeader saleHeader, BindingResult result,
			@RequestBody SaleBean saleBean) {
		Message msg = null;
		String custCode = null;
		Date createTime = null;
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
				if (!"".equals(saleHeader.getId())
						&& saleHeader.getId() != null) {
					List<Map<String, Object>> ql = jdbcTemplate
							.queryForList("select cur.act_name_ from act_ord_curr_node4 cur where cur.id='"
									+ saleHeader.getId() + "'");
					if (ql.size() > 0) {
						Object val = ql.get(0).get("ACT_NAME_");
						// createTime = (Date) ql.get(0).get("CREATE_TIME");
						if (!"起草".equals(val) && !"".equals(val)
								&& !"null".equals(val) && val != null) {
							if("OR3".equals(saleHeader.getOrderType())||"OR4".equals(saleHeader.getOrderType())) {
							}else {
							return msg = new Message("SALE-V-500",
									"该单不是起草状态，不能修改！");
							}
						}
					}
				}
				SysUser loginUser = this.getLoginUser();
				custCode = loginUser.getKunnr();
				if(custCode==null) {
					custCode = saleHeader.getShouDaFang();
				}
				List<CustHeader> findByCode = custHeaderDao
						.findByCode(custCode);
				// 设置订单默认信息
/*				if(findByCode.size()<=0) {
					msg = new Message("DD-S-500", "该订单不属于"+loginUser.getId());
					return msg;
				}*/
				CustHeader ch = findByCode.get(0);
				if ("".equals(saleHeader.getId()) || saleHeader.getId() == null) {
					saleHeader.setOrderDate(new Date());
					saleHeader.setPayType("C");
					saleHeader.setFuFuanMoney(0.00);
					if (findByCode.size() > 0) {

						saleHeader.setShouDaFang(custCode);
						//saleHeader.setSongDaFang(ch.getKunnrS());

						/*
						 * // 店面联系电话 List<Map<String, Object>> cs = jdbcTemplate
						 * .queryForList(
						 * "select c.TELF1 from Cust_Contacts c where c.abtnr='0001' and c.pid='"
						 * + ch.getId() + "'"); if (cs.size() > 0) {
						 * saleHeader.setDianMianTel(cs.get(0).get("TELF1")
						 * .toString()); }
						 */
					}
					/*
					 * if ("buDan".equals(saleHeader.getOrderType()) ||
					 * "OR3".equals(saleHeader.getOrderType()) ||
					 * "OR4".equals(saleHeader.getOrderType())) {
					 * saleHeader.setFuFuanCond("2"); } else {
					 * saleHeader.setFuFuanCond("1"); }
					 */
				} else {
					// saleHeader.setCreateTime(createTime);
				}
				// 计算客户年龄
				Date b = saleBean.getTerminalClient().getBirthday();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (b != null) {
					String bDate = sdf.format(b);
					SimpleDateFormat myFormatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date date = new Date();
					Date mydate = myFormatter.parse(bDate.toString());
					long day = (date.getTime() - mydate.getTime())
							/ (24 * 60 * 60 * 1000) + 1;
					Integer year = (int) (day / 365f);
					saleBean.getTerminalClient().setAge(year);
				}

				if (saleBean.getTerminalClient() != null
						&& saleBean.getSaleOneCustList() != null) {
					TerminalClient tc = saleBean.getTerminalClient();
					SaleOneCust sc = saleBean.getSaleOneCustList().get(0);
					sc.setKunnr(ch.getKunnrS());
					sc.setAnred(tc.getName1());
					sc.setSaleOneCustName1(tc.getName1());
					sc.setTelf1(tc.getTel());
					String ort = "16";// 运输区域默认= 周边区域
					if (ch.getOrt02() != null) {
						ort = ch.getOrt02();
					}
					sc.setOrt02(ort);
					sc.setSaleOneCustType("临时送达方");
				}
				SaleHeader obj = saleManager.saveFB(saleHeader, saleBean,
						super.getLoginUserId());


				String saleId = obj.getId();
				// 更新订单行项目编号
				// if(!"".equals(saleId)&&saleId!=null){
				// List<Map<String, Object>> sis =
				// jdbcTemplate.queryForList("select t.*,rownum*10 rn from ( select s.posex,s.id from sale_item s where s.pid ='"+saleId+"' order by to_number(s.posex) asc ) t");
				// for (int i = 0; i < sis.size(); i++) {
				// Map<String, Object> map=sis.get(i);
				// String
				// updateSql="update sale_item s set s.posex='"+map.get("RN")+"' where s.posex='"+map.get("POSEX")+"' and s.id='"+map.get("ID")+"'";
				// jdbcTemplate.update(updateSql);
				// }
				// }

				/************** 保存后，处理返回信息(start) ******************/
				Map<String, Object> map = new HashMap<String, Object>();

				if (findByCode != null && findByCode.size() > 0) {
					map.put("kunnrName1", ZStringUtils.isEmpty(findByCode
							.get(0).getName1()) ? "" : findByCode.get(0)
							.getName1());
				}

				map.put("id", obj.getId());

				String[] strings = new String[] { "hibernateLazyInitializer",
						"handler", "fieldHandler", "sort", "saleItemSet",
						"terminalClient", "saleOneCustSet", "serialVersionUID" };
				// System.out.println(JSONObject.fromObject(map,
				// super.getJsonConfig(strings)));
				msg = new Message(JSONObject.fromObject(map,
						super.getJsonConfig(strings)));
				/************** 保存后，处理返回信息(end) ******************/

			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("DD-S-500", e.getLocalizedMessage());
			}
		}

		return msg;
	}
	
	@RequestMapping(value = "/queryBgSaleById", method = RequestMethod.GET)
	@ResponseBody
	public Message queryBgSaleById(String id) {
		Message msg = null;
		try {
			if (id == null) {
				return new Message("DD-S-500", "ID不能为空！");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> list = jdbcTemplate
					.queryForList("select "
							+ "sh.sale_for,sh.shop,sh.shop_cls,sh.order_Type,sh.designer_Tel,sh.is_Yp,sh.remarks,sh.id shId,sh.check_Draw_User,sh.check_Price_User,sh.confirm_Finance_User,to_char(sh.create_time,'yyyy-mm-dd hh24:mi:ss') create_Time,sh.create_User,sh.dian_Mian_Tel,sh.fu_Fuan_Cond,sh.fu_Fuan_Money,sh.handle_Time,sh.huo_Dong_Type,sh.jiao_Qi_Tian_Shu,sh.order_Code,to_char(sh.order_Date,'yyyy-mm-dd') order_Date,sh.order_Status,sh.order_Total,sh.p_Order_Code,sh.pay_Type,sh.sap_Order_Code,to_char(sh.shi_Ji_Date,'yyyy-mm-dd') shi_Ji_Date,to_char(sh.shi_Ji_Date2,'yyyy-mm-dd') shi_Ji_Date2,sh.shou_Da_Fang,sh.song_Da_Fang,tc.name1,tc.tel,tc.sex,tc.id tcId,tc.tousucishu,to_char(tc.anzhuan_day, 'yyyy-mm-dd') anzhuanDay,tc.problem,nvl(soc.soc_address,tc.address) AZ_ADDRESS,soc.pstlz,soc.regio,soc.mcod3,soc.id socId"
							+ " from SALE_HEADER sh, SALE_BG_HEADER tc, SALE_ONE_CUST soc  where sh.id=tc.sale_id(+) and sh.id=soc.pid(+)  and sh.id='"
							+ id + "'");
			for (Map<String, Object> reMap : list) {
				map.put("saleFor", reMap.get("SALE_FOR"));
				map.put("designerTel", reMap.get("DESIGNER_TEL"));
				map.put("isYp", reMap.get("IS_YP"));
				map.put("tousucishu", reMap.get("tousucishu"));
				map.put("problem", reMap.get("problem"));
				map.put("anzhuanDay", reMap.get("anzhuanDay"));
				map.put("remarks", reMap.get("REMARKS"));
				map.put("ISYP", reMap.get("IS_YP"));
				map.put("shId", reMap.get("SHID"));
				map.put("checkDrawUser", reMap.get("CHECK_DRAW_USER"));
				map.put("checkPriceUser", reMap.get("CHECK_PRICE_USER"));
				map.put("confirmFinanceUser", reMap.get("CONFIRM_FINANCE_USER"));
				map.put("createTime", reMap.get("CREATE_TIME"));
				map.put("createUser", reMap.get("CREATE_USER"));
				map.put("dianMianTel", reMap.get("DIAN_MIAN_TEL"));
				map.put("fuFuanCond", reMap.get("FU_FUAN_COND"));
				map.put("fuFuanMoney", reMap.get("FU_FUAN_MONEY"));
				map.put("handleTime", reMap.get("HANDLE_TIME"));
				map.put("huoDongType", reMap.get("HUO_DONG_TYPE"));
				map.put("jiaoQiTianShu", reMap.get("JIAO_QI_TIAN_SHU"));
				map.put("orderCode", reMap.get("ORDER_CODE"));
				map.put("orderDate", reMap.get("ORDER_DATE"));
				map.put("orderStatus", reMap.get("ORDER_STATUS"));
				map.put("orderTotal", reMap.get("ORDER_TOTAL"));
				map.put("pOrderCode", reMap.get("P_ORDER_CODE"));
				map.put("payType", reMap.get("PAY_TYPE"));
				map.put("sapOrderCode", reMap.get("SAP_ORDER_CODE"));
				map.put("shiJiDate", reMap.get("SHI_JI_DATE"));
				map.put("shiJiDate2", reMap.get("SHI_JI_DATE2"));
				map.put("shouDaFang", reMap.get("SHOU_DA_FANG"));
				map.put("songDaFang", reMap.get("SONG_DA_FANG"));
				map.put("orderType", reMap.get("ORDER_TYPE"));

				map.put("name1", reMap.get("NAME1"));
				map.put("tel", reMap.get("TEL"));
				map.put("sex", reMap.get("SEX"));
				map.put("birthday", reMap.get("BIRTHDAY"));
				map.put("huXing", reMap.get("HU_XING"));
				map.put("isYangBan", reMap.get("IS_YANG_BAN"));
				map.put("orderPayFw", reMap.get("ORDER_PAY_FW"));
				map.put("tcId", reMap.get("TCID"));
				map.put("age", reMap.get("AGE"));
				map.put("jingShouRen", reMap.get("JING_SHOU_REN"));
				map.put("isYanBan", reMap.get("IS_YANG_BAN"));
				map.put("azAddress", reMap.get("AZ_ADDRESS"));

				map.put("pstlz", reMap.get("PSTLZ"));
				map.put("regio", reMap.get("REGIO"));
				map.put("mcod3", reMap.get("MCOD3"));
				map.put("address", reMap.get("AZ_ADDRESS"));
				map.put("socId", reMap.get("SOCID"));
				map.put("shop", reMap.get("SHOP"));
				map.put("shopCls", reMap.get("SHOP_CLS"));
				map.put("orderEvent", reMap.get("ORDER_EVENT"));

			}

			msg = new Message(map);
			/************** 保存后，处理返回信息(end) ******************/
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("DD-S-500", e.getLocalizedMessage());
		}

		return msg;
	}
	
	/**
	 * 查询订单信息--》新增界面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/querySaleById", method = RequestMethod.GET)
	@ResponseBody
	public Message querySaleById(String id) {
		Message msg = null;
		try {
			if (id == null) {
				return new Message("DD-S-500", "ID不能为空！");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> list = jdbcTemplate
					.queryForList("select "
							+ "sh.sale_for,sh.shop,sh.shop_cls,sh.order_Type,sh.designer_Tel,sh.is_Yp,sh.urgent_type,sh.is_kf,sh.is_mj,sh.is_qc,sh.remarks,sh.id shId,sh.check_Draw_User,sh.serial_number,sh.check_Price_User,sh.confirm_Finance_User,to_char(sh.create_time,'yyyy-mm-dd hh24:mi:ss') create_Time,sh.create_User,sh.dian_Mian_Tel,sh.fu_Fuan_Cond,sh.fu_Fuan_Money,sh.handle_Time,sh.huo_Dong_Type,sh.jiao_Qi_Tian_Shu,sh.order_Code,to_char(sh.order_Date,'yyyy-mm-dd') order_Date,sh.order_Status,sh.order_Total,sh.p_Order_Code,sh.pay_Type,sh.sap_Order_Code,to_char(sh.shi_Ji_Date,'yyyy-mm-dd') shi_Ji_Date,to_char(sh.shi_Ji_Date2,'yyyy-mm-dd') shi_Ji_Date2,sh.shou_Da_Fang,sh.song_Da_Fang,tc.name1,tc.tel,tc.sex,TO_CHAR(tc.birthday,'YYYY-MM-DD') birthday,tc.hu_Xing,tc.is_Yang_Ban,tc.order_Pay_Fw,tc.id tcId,tc.age,tc.address,tc.tousucishu,tc.problem,TO_CHAR(tc.anzhuan_day, 'YYYY-MM-DD') anzhuan_day,tc.jing_Shou_Ren,tc.is_yang_ban,nvl(soc.soc_address,tc.address) AZ_ADDRESS,soc.pstlz,soc.regio,soc.mcod3,soc.id socId"
							+ " from SALE_HEADER sh, TERMINAL_CLIENT tc, SALE_ONE_CUST soc  where sh.id=tc.sale_id(+) and sh.id=soc.pid(+)  and sh.id='"
							+ id + "'");
			for (Map<String, Object> reMap : list) {
				map.put("saleFor", reMap.get("SALE_FOR"));
				map.put("designerTel", reMap.get("DESIGNER_TEL"));
				map.put("serialNumber", reMap.get("SERIAL_NUMBER"));
				map.put("isYp", reMap.get("IS_YP"));
				map.put("remarks", reMap.get("REMARKS"));
				map.put("ISYP", reMap.get("IS_YP"));
				map.put("shId", reMap.get("SHID"));
				map.put("anzhuanDay", reMap.get("anzhuan_day"));
				map.put("tousucishu", reMap.get("tousucishu"));
				map.put("problem", reMap.get("problem"));
				map.put("checkDrawUser", reMap.get("CHECK_DRAW_USER"));
				map.put("checkPriceUser", reMap.get("CHECK_PRICE_USER"));
				map.put("confirmFinanceUser", reMap.get("CONFIRM_FINANCE_USER"));
				map.put("createTime", reMap.get("CREATE_TIME"));
				map.put("createUser", reMap.get("CREATE_USER"));
				map.put("dianMianTel", reMap.get("DIAN_MIAN_TEL"));
				map.put("fuFuanCond", reMap.get("FU_FUAN_COND"));
				map.put("fuFuanMoney", reMap.get("FU_FUAN_MONEY"));
				map.put("handleTime", reMap.get("HANDLE_TIME"));
				map.put("huoDongType", reMap.get("HUO_DONG_TYPE"));
				map.put("jiaoQiTianShu", reMap.get("JIAO_QI_TIAN_SHU"));
				map.put("orderCode", reMap.get("ORDER_CODE"));
				map.put("orderDate", reMap.get("ORDER_DATE"));
				map.put("orderStatus", reMap.get("ORDER_STATUS"));
				map.put("orderTotal", reMap.get("ORDER_TOTAL"));
				map.put("pOrderCode", reMap.get("P_ORDER_CODE"));
				map.put("payType", reMap.get("PAY_TYPE"));
				map.put("sapOrderCode", reMap.get("SAP_ORDER_CODE"));
				map.put("shiJiDate", reMap.get("SHI_JI_DATE"));
				map.put("shiJiDate2", reMap.get("SHI_JI_DATE2"));
				map.put("shouDaFang", reMap.get("SHOU_DA_FANG"));
				map.put("songDaFang", reMap.get("SONG_DA_FANG"));
				map.put("orderType", reMap.get("ORDER_TYPE"));

				map.put("name1", reMap.get("NAME1"));
				map.put("tel", reMap.get("TEL"));
				map.put("sex", reMap.get("SEX"));
				map.put("birthday", reMap.get("BIRTHDAY"));
				map.put("huXing", reMap.get("HU_XING"));
				map.put("isYangBan", reMap.get("IS_YANG_BAN"));
				map.put("orderPayFw", reMap.get("ORDER_PAY_FW"));
				map.put("tcId", reMap.get("TCID"));
				map.put("age", reMap.get("AGE"));
				map.put("jingShouRen", reMap.get("JING_SHOU_REN"));
				map.put("isYanBan", reMap.get("IS_YANG_BAN"));
				map.put("azAddress", reMap.get("AZ_ADDRESS"));

				map.put("pstlz", reMap.get("PSTLZ"));
				map.put("regio", reMap.get("REGIO"));
				map.put("mcod3", reMap.get("MCOD3"));
				map.put("address", reMap.get("AZ_ADDRESS"));
				map.put("socId", reMap.get("SOCID"));
				map.put("shop", reMap.get("SHOP"));
				map.put("shopCls", reMap.get("SHOP_CLS"));
				map.put("urgentType", reMap.get("URGENT_TYPE"));
				map.put("isMj", reMap.get("IS_MJ"));
				map.put("isQc", reMap.get("IS_QC"));
				map.put("isKf", reMap.get("IS_KF"));

			}

			msg = new Message(map);
			/************** 保存后，处理返回信息(end) ******************/
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("DD-S-500", e.getLocalizedMessage());
		}

		return msg;
	}

	@RequestMapping(value = "/tranSap", method = RequestMethod.POST)
	@ResponseBody
	public Message tranSap(String saleId) {
		Message msg = null;
		try {
			SysJobPoolManager cpMan = SpringContextHolder
					.getBean("sysJobPoolManagerImpl");
			String sql = "select h.ORDER_CODE from sale_header h where h.id='"
					+ saleId + "'";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			if (list.size() > 0) {
				Map<String, Object> map = list.get(0);
				String ordeCode = map.get("ORDER_CODE").toString();
				SysUser loginUser = this.getLoginUser();
				String user = loginUser.getId();
				// 记录次数和执行时间
				msg = cpMan.sendSaleByCode(ordeCode, user);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Message("SALE-500", e.getLocalizedMessage());
		}
		return msg;
		/*
		 * 2016-05-25 注销
		 * 
		 * JCoDestination connect = SAPConnect.getConnect(); JCoFunction
		 * function;// 销售单创建 JCoFunction functionMM;// 物料创建 JCoFunction
		 * functionXd;// 取客户信贷信息 try { SaleHeader saleHeader =
		 * saleHeaderDao.findOne(saleId); if
		 * (ZStringUtils.isNotEmpty(saleHeader.getSapOrderCode())) { msg = new
		 * Message("SALE-500", "传输失败：<br/>订单" + saleHeader.getOrderCode() +
		 * "，已传输SAP产生了销售单号" + saleHeader.getSapOrderCode() + "！"); return msg; }
		 * String bukrs = "";// 公司代码 String vkorg = "3100";// 销售组织 String vtweg
		 * = "01";// 分销渠道 String spart = "01";// 产品组 CustHeader custHeader =
		 * null; List<CustHeader> findByCode =
		 * custHeaderDao.findByCode(saleHeader.getShouDaFang()); if (findByCode
		 * != null && findByCode.size() > 0) { custHeader = findByCode.get(0);
		 * vkorg = ZStringUtils.isEmpty(custHeader.getVkorg()) ? "3100" :
		 * custHeader.getVkorg(); vtweg =
		 * ZStringUtils.isEmpty(custHeader.getVtweg()) ? "01" :
		 * custHeader.getVtweg(); spart =
		 * ZStringUtils.isEmpty(custHeader.getSpart()) ? "01" :
		 * custHeader.getSpart(); bukrs =
		 * ZStringUtils.isEmpty(custHeader.getBukrs()) ? "3000" :
		 * custHeader.getBukrs(); }
		 * 
		 * // 付款条件 String fuFuanCond = saleHeader.getFuFuanCond(); // int bfb =
		 * 0;// 百分比 // if ("2".equals(fuFuanCond)) {// 信贷付款 // bfb = 100; // }
		 * else if ("3".equals(fuFuanCond)) {// 预付款50% // bfb = 50; // } else if
		 * ("4".equals(fuFuanCond)) {// 预付款30% // bfb = 30; // } if
		 * (saleHeader.getOrderTotal() != null && saleHeader.getOrderTotal() >
		 * 0) { functionXd =
		 * connect.getRepository().getFunction("ZCREDIT_CHECKED_POST");//
		 * 前端接单系统信贷查询 functionXd.getImportParameterList().setValue("BUKRS",
		 * bukrs); functionXd.getImportParameterList().setValue("KUNNR",
		 * saleHeader.getShouDaFang());
		 * functionXd.getImportParameterList().setValue("ZUONR",
		 * saleHeader.getOrderCode());
		 * functionXd.getImportParameterList().setValue("NETWR",
		 * saleHeader.getOrderTotal()); //
		 * functionXd.getImportParameterList().setValue("KBVER", bfb); // String
		 * shouDaFang = saleHeader.getShouDaFang(); // JCoTable sKunnrTable = //
		 * functionXd.getTableParameterList().getTable( // "S_KUNNR"); //
		 * sKunnrTable.appendRow(); // sKunnrTable.setValue("SIGN", "I"); //
		 * sKunnrTable.setValue("OPTION", "EQ"); //
		 * sKunnrTable.setValue("CUSTOMER_VENDOR_LOW", shouDaFang);
		 * functionXd.execute(connect); Object POST_FLAG =
		 * functionXd.getExportParameterList().getValue("POST_FLAG"); if
		 * (ZStringUtils.isEmpty(POST_FLAG)) { msg = new Message("SALE-500",
		 * "客户(" + saleHeader.getShouDaFang() + ")的信贷额度不够！"); return msg; } }
		 *//*********************************************************************/
		/*
            *//*********************** SAP创建物料开始(start) ***********************/
		/*
            *//*********************************************************************/
		/*
		 * functionMM = connect.getRepository().getFunction("ZRFC_MM_MM01");//
		 * SAP创建物料接口 JCoTable itMatTable =
		 * functionMM.getTableParameterList().getTable("IT_MAT");//
		 * IMOS接口提供物料主数据结构 JCoTable itImos01Table =
		 * functionMM.getTableParameterList().getTable("IT_IMOS01");//
		 * IMOS对接数据-主表-物料产品层次用 List<SaleItem> saleItemList =
		 * saleItemDao.findItemsByPid(saleId); // 需要创建的物料 List<MaterialHead>
		 * createMaterialHeadList = new ArrayList<MaterialHead>(); if
		 * (saleItemList != null && saleItemList.size() > 0) { // 订单行项目需要创建的物料ID
		 * Set<String> createMmHeadIdSet = new HashSet<String>(); //
		 * 订单行项目的订单与行项目关联编号 Set<String> orderCodePosexSet = new
		 * HashSet<String>(); // key = orderCodePosex, value = 物料头的SerialNumber
		 * Map<String, Object> orderCodePosexMap_serialNumber = new
		 * HashMap<String, Object>();
		 * 
		 * for (Iterator iterator = saleItemList.iterator();
		 * iterator.hasNext();) { SaleItem saleItem = (SaleItem)
		 * iterator.next(); if ("0".equals(saleItem.getIsStandard()) &&
		 * !"QX".equals(saleItem.getStateAudit())) {// 非标物料
		 * createMmHeadIdSet.add(saleItem.getMaterialHeadId());
		 * orderCodePosexSet.add(saleItem.getOrderCodePosex()); } }
		 * createMaterialHeadList =
		 * saleManager.createQueryByIn(MaterialHead.class, "id",
		 * createMmHeadIdSet); for (MaterialHead materialHead :
		 * createMaterialHeadList) { for (SaleItem saleItem : saleItemList) { if
		 * (ZStringUtils.isNotEmpty(saleItem.getMaterialHeadId()) &&
		 * ZStringUtils.isNotEmpty(materialHead.getId()) &&
		 * saleItem.getMaterialHeadId().equals(materialHead.getId()) &&
		 * !"QX".equals(saleItem.getStateAudit())) {
		 * orderCodePosexMap_serialNumber.put(saleItem.getOrderCodePosex(),
		 * materialHead .getSerialNumber()); break; } } itMatTable.appendRow();
		 * // MATNR MATNR 物料号 CHAR 18 itMatTable.setValue("MATNR",
		 * materialHead.getMatnr()); // EXMAT ZEXMAT 外部物料号 CHAR 18
		 * itMatTable.setValue("EXMAT", materialHead.getSerialNumber()); //
		 * VKORG VKORG 销售组织 CHAR 4 itMatTable.setValue("VKORG", vkorg); // VTWEG
		 * VTWEG 分销渠道 CHAR 2 itMatTable.setValue("VTWEG", vtweg); // MTART MTART
		 * 物料类型 CHAR 4 itMatTable.setValue("MTART", materialHead.getMtart()); //
		 * MAKTX MAKTX 物料描述（短文本） CHAR 40 itMatTable.setValue("MAKTX",
		 * materialHead.getMaktx()); // MATKL MATKL 物料组 CHAR 9
		 * itMatTable.setValue("MATKL", materialHead.getMatkl()); // EXTWG EXTWG
		 * 外部物料组 CHAR 18 itMatTable.setValue("EXTWG", materialHead.getColor());
		 * // BRGEW BRGEW 毛重 QUAN 13 3 itMatTable.setValue("BRGEW",
		 * materialHead.getBrgew()); // NTGEW NTGEW 净重 QUAN 13 3
		 * itMatTable.setValue("NTGEW", materialHead.getNtgew()); // GEWEI GEWEI
		 * 重量单位 UNIT 3 itMatTable.setValue("GEWEI", materialHead.getGewei()); //
		 * VOLUM VOLUM 包数 QUAN 13 3 itMatTable.setValue("VOLUM",
		 * materialHead.getVolum()); // VOLEH VOLEH 体积单位 UNIT 3
		 * itMatTable.setValue("VOLEH", materialHead.getVoleh()); // GROES GROES
		 * 大小/量纲 CHAR 32 itMatTable.setValue("GROES", materialHead.getGroes());
		 * // ZXLCP ZXLCP 系列产品 NUMC 3 // ZCPLB ZCPLB 产品类别 NUMC 4 }
		 * 
		 * // List<ImosIdbext> imosIdbextList = //
		 * saleManager.createQueryByIn(ImosIdbext.class, "orderid", //
		 * createMmHeadIdSet);
		 * 
		 * if (orderCodePosexSet.size() > 0) { StringBuffer sb = new
		 * StringBuffer(); int i = 0; for (String str : orderCodePosexSet) {
		 * sb.append("'").append(str); if (i < orderCodePosexSet.size() - 1) {
		 * sb.append("',"); } else { sb.append("'"); } i++; } List<Map<String,
		 * Object>> imosIdbextList = jdbcTemplate
		 * .queryForList("select t.* from IMOS_IDBEXT t where t.orderid in (" +
		 * sb.toString() + ")"); for (Map<String, Object> map : imosIdbextList)
		 * { // BeanUtils.tranMapToObj(map, imosIdbext);
		 * itImos01Table.appendRow(); // POSEX POSEX 优先采购订单的项目号 CHAR 6
		 * itImos01Table.setValue("POSEX",
		 * Integer.parseInt(map.get("ORDERID").toString
		 * ().split(saleHeader.getOrderCode())[1])); // EXMAT ZEXMAT 外部物料号 CHAR
		 * 18 itImos01Table.setValue("EXMAT",
		 * orderCodePosexMap_serialNumber.get(map.get("ORDERID")) == null ? "" :
		 * orderCodePosexMap_serialNumber.get(map.get("ORDERID")).toString());
		 * // ID ZID ID From Imos CHAR 30 itImos01Table.setValue("ID",
		 * map.get("ID") == null ? "" : map.get("ID").toString()); // RENDERPMAT
		 * ZRENDERPMAT IMOS->对应SAP物料号 CHAR 18 // TYP ZTYP 类型 CHAR 1
		 * itImos01Table.setValue("TYP", map.get("TYP") == null ? "" :
		 * map.get("TYP").toString()); // PARTTYPE ZPARTTYPE 板件类型 INT4 10 if
		 * (ZStringUtils.isNotEmpty(map.get("PARTTYPE"))) {
		 * itImos01Table.setValue("PARTTYPE", new
		 * Integer(map.get("PARTTYPE").toString())); } // NAME ZNAME1 名称1 CHAR
		 * 32 itImos01Table.setValue("NAME", map.get("NAME") == null ? "" :
		 * map.get("NAME").toString()); // NAME2 ZNAME2 名称2 CHAR 30
		 * itImos01Table.setValue("NAME2", map.get("NAME2") == null ? "" :
		 * map.get("NAME2").toString()); // LENGTH ZLENGTH 长度 QUAN 13 3 if
		 * (ZStringUtils.isNotEmpty(map.get("LENGTH"))) {
		 * itImos01Table.setValue("LENGTH", new
		 * Double(map.get("LENGTH").toString())); } // WIDTH ZWIDTH 宽度 QUAN 13 3
		 * if (ZStringUtils.isNotEmpty(map.get("WIDTH"))) {
		 * itImos01Table.setValue("WIDTH", new
		 * Double(map.get("WIDTH").toString())); } // THICKNESS ZTHICKNESS 厚度
		 * QUAN 13 3 if (ZStringUtils.isNotEmpty(map.get("THICKNESS"))) {
		 * itImos01Table.setValue("THICKNESS", new
		 * Double(map.get("THICKNESS").toString())); } // PARENTID ZPARENTID 父ID
		 * CHAR 30 itImos01Table.setValue("PARENTID", map.get("PARENTID") ==
		 * null ? "" : map.get("PARENTID") .toString()); // ARTICLE_ID
		 * ZARTICLE_ID 产品_编号 来自ERP中的板件编号 CHAR 18
		 * itImos01Table.setValue("ARTICLE_ID", map.get("ARTICLE_ID") == null ?
		 * "" : map.get("ARTICLE_ID") .toString()); // INFO1 ZINFO1 信息1-识别码 CHAR
		 * 15 itImos01Table.setValue("INFO1", map.get("INFO1") == null ? "" :
		 * map.get("INFO1").toString()); // INFO2 ZINFO2 信息2 CHAR 80
		 * itImos01Table.setValue("INFO2", map.get("INFO2") == null ? "" :
		 * map.get("INFO2").toString()); // INFO3 ZINFO3 信息3 CHAR 80
		 * itImos01Table.setValue("INFO3", map.get("INFO3") == null ? "" :
		 * map.get("INFO3").toString()); // INFO4 ZINFO4 信息4 CHAR 80
		 * itImos01Table.setValue("INFO4", map.get("INFO4") == null ? "" :
		 * map.get("INFO4").toString()); // INFO5 ZINFO5 信息5 CHAR 80
		 * itImos01Table.setValue("INFO5", map.get("INFO5") == null ? "" :
		 * map.get("INFO5").toString()); // COLOR1 ZCOLOR1 颜色1 CHAR 64
		 * itImos01Table.setValue("COLOR1", map.get("COLOR1") == null ? "" :
		 * map.get("COLOR1").toString()); // COLOR2 ZCOLOR2 颜色2 CHAR 64
		 * itImos01Table.setValue("COLOR2", map.get("COLOR2") == null ? "" :
		 * map.get("COLOR2").toString()); } }
		 * 
		 * } int row = itMatTable.getNumRows(); if (row > 0) {
		 * functionMM.execute(connect); JCoTable itReturnTable =
		 * functionMM.getTableParameterList().getTable("IT_RETURN");
		 * StringBuffer errSb = new StringBuffer(); if
		 * (itReturnTable.getNumRows() > 0) { itReturnTable.firstRow(); //
		 * List<String> list = new ArrayList<String>(); for (int i = 0; i <
		 * itReturnTable.getNumRows(); i++, itReturnTable.nextRow()) { Object
		 * type = itReturnTable.getValue("TYPE"); Object message =
		 * itReturnTable.getValue("MESSAGE"); Object logNo =
		 * itReturnTable.getValue("LOG_NO"); Object messageV1 =
		 * itReturnTable.getValue("MESSAGE_V1"); if (type != null &&
		 * "S".equals(type.toString())) {// 成功创建的物料 if
		 * (ZStringUtils.isNotEmpty(logNo) &&
		 * ZStringUtils.isNotEmpty(messageV1)) { for (Iterator iterator =
		 * createMaterialHeadList.iterator(); iterator.hasNext();) {
		 * MaterialHead materialHead = (MaterialHead) iterator.next(); if
		 * (materialHead.getSerialNumber().equals(logNo.toString().trim())) { //
		 * 回写更新MATERIAL_HEAD的MATNR
		 * materialHead.setMatnr(messageV1.toString().trim()); for (Iterator it
		 * = saleItemList.iterator(); it.hasNext();) { SaleItem saleItem =
		 * (SaleItem) it.next(); if
		 * (saleItem.getMaterialHeadId().equals(materialHead.getId())) { //
		 * 回写更新sale_item的matnr saleItem.setMatnr(messageV1.toString().trim()); }
		 * } } } } // // 回写更新MATERIAL_HEAD的MATNR // String sql =
		 * "update MATERIAL_HEAD set MATNR='" // +
		 * (ZStringUtils.isEmpty(messageV1) ? "" // :
		 * messageV1.toString().trim()) // + "' where SERIAL_NUMBER='" // +
		 * (ZStringUtils.isEmpty(logNo) ? "" : logNo // .toString().trim()) +
		 * "'"; // // 回写更新sale_item的matnr // String sql2 =
		 * "update sale_item set MATNR='" // + (ZStringUtils.isEmpty(messageV1)
		 * ? "" // : messageV1.toString().trim()) // +
		 * "' where MATERIAL_HEAD_ID in (" // + //
		 * "select id from MATERIAL_HEAD where SERIAL_NUMBER='" // +
		 * (ZStringUtils.isEmpty(logNo) ? "" : logNo // .toString().trim()) +
		 * "')"; // list.add(sql); // list.add(sql2);
		 * 
		 * // // 回写更新MATERIAL_HEAD的MATNR // int update = jdbcTemplate //
		 * .update("update MATERIAL_HEAD set MATNR='" // +
		 * (ZStringUtils.isEmpty(messageV1) ? "" // : messageV1.toString() //
		 * .trim()) // + "' where SERIAL_NUMBER='" // +
		 * (ZStringUtils.isEmpty(logNo) ? "" // : logNo.toString().trim()) // +
		 * "'"); // // 回写更新sale_item的matnr // int update2 = jdbcTemplate //
		 * .update("update sale_item set MATNR='" // +
		 * (ZStringUtils.isEmpty(messageV1) ? "" // : messageV1.toString() //
		 * .trim()) // + "' where MATERIAL_HEAD_ID in (" // + //
		 * "select id from MATERIAL_HEAD where SERIAL_NUMBER='" // +
		 * (ZStringUtils.isEmpty(logNo) ? "" // : logNo.toString().trim()) // +
		 * "')"); // System.out.println("update=================>" + // update);
		 * // System.out.println("update2=================>" + // update2); }
		 * else {// 创建物料失败信息 errSb.append(message == null ? "" :
		 * message.toString()).append("<br/>"); } } // if (list.size() > 0) { //
		 * String[] array = list.toArray(new String[list.size()]); // int[]
		 * batchUpdate = jdbcTemplate.batchUpdate(array); // for (int i :
		 * batchUpdate) { // System.out.println("=================>" + i); // }
		 * // }
		 * 
		 * } else { errSb.append("接口异常，没有返回信息"); } // 有物料创建失败时返回页面，重新创建订单 if
		 * (!errSb.toString().equals("")) { msg = new Message("SALE-500",
		 * errSb.toString()); return msg; } }
		 *//*******************************************************************/
		/*
            *//*********************** SAP创建物料结束(end) ***********************/
		/*
            *//*******************************************************************/
		/*

            *//*********************************************************************/
		/*
            *//*********************** SAP创建销售单开始(start) *********************/
		/*
            *//*********************************************************************/
		/*
		 * // 当前日期 Date currDate = new Date(); int count = 0; String
		 * jiaoQiTianShu = saleHeader.getJiaoQiTianShu(); if
		 * (ZStringUtils.isNotEmpty(jiaoQiTianShu)) { count =
		 * Integer.parseInt(jiaoQiTianShu.trim()); } long secondBetweenTwoDay =
		 * DateTools
		 * .getSecondBetweenTwoDay(DateTools.strToDate(DateTools.getDate() +
		 * " 12:00:00", DateTools.fullFormat), currDate); if
		 * (secondBetweenTwoDay > 0) {// 中午12点后加1 count += 1; } StringBuffer sb
		 * = new StringBuffer(); sb.append("select * from (");
		 * sb.append("select row_.*, rownum rownum_ from ("); sb.append(
		 * "select t.* from SAP_ZST_PP_RL01 t where t.work='X' and t.werks_date>=to_date('"
		 * ).append( DateTools.formatDate(currDate,
		 * DateTools.defaultFormat)).append(
		 * "','yyyy-mm-dd') order by t.werks_date");
		 * sb.append(") row_ where rownum <= "
		 * ).append(count).append(") where rownum_ > 0 and rownum_ = ").append(
		 * count); List<Map<String, Object>> queryForList =
		 * jdbcTemplate.queryForList(sb.toString()); Object werksDate = null; if
		 * (queryForList != null && queryForList.size() > 0) { werksDate =
		 * queryForList.get(0).get("WERKS_DATE"); }
		 * 
		 * function =
		 * connect.getRepository().getFunction("ZRFC_SD_CREAT_SO_NEW");//
		 * SAP创建销售订单接口 ZRFC_SD_CREAT_SO 调用新接口 ZRFC_SD_CREAT_SO_NEW JCoStructure
		 * structure =
		 * function.getImportParameterList().getStructure("IM_S_HEAD");//
		 * 销售订单头结构 // BSTNK BSTNK 客户采购订单编号 CHAR 20 String orderCode =
		 * saleHeader.getOrderCode(); structure.setValue("BSTNK", orderCode); //
		 * BSTDK BSTDK 客户采购订单日期 DATS 8 structure.setValue("BSTDK",
		 * DateTools.formatDate(saleHeader.getOrderDate(),
		 * DateTools.YYMMDDFormat)); // AUART AUART 销售凭证类型 CHAR 4
		 * structure.setValue("AUART", saleHeader.getOrderType()); if
		 * (custHeader != null) { Set<CustItem> custItemSet =
		 * custHeader.getCustItemSet(); for (Iterator iterator =
		 * custItemSet.iterator(); iterator.hasNext();) { CustItem custItem =
		 * (CustItem) iterator.next(); if ("1".equals(custItem.getStatus())) {//
		 * 已经激活的 // ZZFDMC ZZFDMC 返点名称 CHAR 15 String
		 * fandian=sysDataDictDao.getDescForI18N("FAN_DIAN_NAME",
		 * custItem.getFanDianName(), Language.zh_CN);
		 * structure.setValue("ZZFDMC", fandian==null?"":fandian); break; } } }
		 * // VKORG VKORG 销售组织 CHAR 4 structure.setValue("VKORG", vkorg); //
		 * VTWEG VTWEG 分销渠道 CHAR 2 structure.setValue("VTWEG", vtweg); // SPART
		 * SPART 产品组 CHAR 2 structure.setValue("SPART", spart); // KUNNR KUNNR
		 * 客户编号 CHAR 10 structure.setValue("KUNNR", saleHeader.getShouDaFang());
		 * // KUSDF ZKUWE 送达方编号 CHAR 10 structure.setValue("KUSDF",
		 * saleHeader.getSongDaFang());
		 * 
		 * // VDATU EDATU_VBAK 请求交货日期 DATS 8 if
		 * (ZStringUtils.isNotEmpty(werksDate)) { structure.setValue("VDATU",
		 * DateTools.formatDate((Date) werksDate, DateTools.YYMMDDFormat)); }
		 * 
		 * TerminalClient terminalClient = saleHeader.getTerminalClient(); //
		 * ZZNAME ZZNAME 客户姓名 CHAR 15 structure.setValue("ZZNAME",
		 * terminalClient.getName1()); // ZZGEND ZZGEND 客户性别 CHAR 2 if
		 * (ZStringUtils.isNotEmpty(terminalClient.getSex())) {
		 * List<SysDataDict> sexSysDataDict =
		 * sysDataDictDao.findByTrieTreeKeyVal("SEX"); if (sexSysDataDict !=
		 * null && sexSysDataDict.size() > 0) { for (SysDataDict sysDataDict :
		 * sexSysDataDict) { if
		 * (sysDataDict.getKeyVal().equals(terminalClient.getSex())) {
		 * structure.setValue("ZZGEND", sysDataDict.getDescZhCn()); } } } } //
		 * ZZAGE ZZAGE 客户年龄 CHAR 3 structure.setValue("ZZAGE",
		 * terminalClient.getAge()); // ZZBITD ZZBITD 客户生日 DATS 8 if
		 * (terminalClient.getBirthday() != null) structure .setValue("ZZBITD",
		 * DateTools.formatDate(terminalClient.getBirthday(),
		 * DateTools.YYMMDDFormat)); // ZZPHON ZZPHON 客户电话 CHAR 16
		 * structure.setValue("ZZPHON", terminalClient.getTel()); // ZZADRS
		 * ZZADRS 客户地址 CHAR 80 structure.setValue("ZZADRS",
		 * terminalClient.getAddress()); // ZZJEFW ZZJEFW 金额范围 NUMC 3
		 * structure.setValue("ZZJEFW", terminalClient.getOrderPayFw()); //
		 * ZZHSTY ZZHSTY 客户户型 NUMC 3 structure.setValue("ZZHSTY",
		 * terminalClient.getHuXing()); // ZZREMK ZZREMK 备注 CHAR 80
		 * structure.setValue("ZZREMK", terminalClient.getCustRemarks());
		 * 
		 * // ZZSJHM ZZSJHM 审价员 NUMC 2 structure.setValue("ZZSJHM",
		 * saleHeader.getCheckPriceUser());
		 * 
		 * if
		 * (ZStringUtils.isNotEmpty(this.getRequest().getSession().getAttribute
		 * ("CURR_USER_ID"))) { // ZZCWHM ZZCWHM 财务确认人员 CHAR 12
		 * structure.setValue("ZZCWHM",
		 * this.getRequest().getSession().getAttribute
		 * ("CURR_USER_ID").toString()); }
		 * 
		 * // ZZYPFG ZZYPFG 是否样品 CHAR 1 if
		 * (ZStringUtils.isNotEmpty(saleHeader.getIsYp())) { if
		 * ("1".equals(saleHeader.getIsYp())) {// 是 structure.setValue("ZZYPFG",
		 * "X"); } else if ("0".equals(saleHeader.getIsYp())) {// 否
		 * structure.setValue("ZZYPFG", ""); } } // ZZAZFG ZZAZFG 是否需要安装 CHAR 1
		 * if (ZStringUtils.isNotEmpty(terminalClient.getIsAnZhuang())) { if
		 * ("1".equals(saleHeader.getIsYp())) {// 是 structure.setValue("ZZAZFG",
		 * "X"); } else if ("0".equals(saleHeader.getIsYp())) {// 否
		 * structure.setValue("ZZAZFG", ""); } }
		 * 
		 * // ZZHDLX ZZHDLX 活动类型 NUMC 3 structure.setValue("ZZHDLX",
		 * saleHeader.getHuoDongType()); // ZZYSFG ZZYSFG 是否预收款50% CHAR 1
		 * structure.setValue("ZZYSFG", fuFuanCond); // ZZDRMK ZZDRMK 备注 CHAR 80
		 * structure.setValue("ZZDRMK", saleHeader.getRemarks());
		 * 
		 * // ZZCJUN ZZCJUN 销售订单创建人 NUMC 2 structure.setValue("ZZCJUN",
		 * saleHeader.getCreateUser()); // ZZJQTS ZZJQTS 投诉日期 DATS 8
		 * structure.setValue("ZZJQTS", saleHeader.getJiaoQiTianShu());
		 * 
		 * // ZZTSDT ZZTSDT 投诉日期 DATS 8 structure.setValue("ZZTSDT",
		 * saleHeader.getCreateTime()); List<SaleOneCust> saleOneCustList =
		 * saleOneCustDao.findSaleOneCustsByPid(saleId); if (saleOneCustList !=
		 * null && saleOneCustList.size() > 0) { JCoTable imTAddrsTable =
		 * function.getTableParameterList().getTable("IM_T_ADDRS");// 客户地址信息 for
		 * (Iterator iterator = saleOneCustList.iterator(); iterator.hasNext();)
		 * { SaleOneCust saleOneCust = (SaleOneCust) iterator.next();
		 * imTAddrsTable.appendRow(); // KUNNR KUNNR 客户编号 CHAR 10
		 * imTAddrsTable.setValue("KUNNR", saleOneCust.getKunnr()); // ANRED
		 * ANRED 标题 CHAR 15 imTAddrsTable.setValue("ANRED",
		 * saleOneCust.getAnred()); // NAME1 NAME1_GP 名称 1 CHAR 35
		 * imTAddrsTable.setValue("NAME1", saleOneCust.getSaleOneCustName1());
		 * // STREET AD_STREET 街道 CHAR 60 imTAddrsTable.setValue("STREET",
		 * saleOneCust.getStreet()); // HOUSE_NUM1 AD_HSNM1 门牌号 CHAR 10 // PSTLZ
		 * PSTLZ 邮政编码 CHAR 10 imTAddrsTable.setValue("PSTLZ",
		 * saleOneCust.getPstlz()); // ORT01 ORT01 城市 CHAR 25
		 * imTAddrsTable.setValue("ORT01", saleOneCust.getMcod3()); // LAND1
		 * LAND1 国家键值 CHAR 3 imTAddrsTable.setValue("LAND1",
		 * saleOneCust.getLand1()); // REGIO REGIO 地区（省/自治区/直辖市、市、县） CHAR 3
		 * imTAddrsTable.setValue("REGIO", saleOneCust.getRegio()); // TELF1
		 * TELF1 第一个电话号 CHAR 16 imTAddrsTable.setValue("TELF1",
		 * saleOneCust.getTelf1()); // LZONE LZONE 发送货物的目的地运输区域 CHAR 10
		 * imTAddrsTable.setValue("LZONE", saleOneCust.getOrt02()); } } //
		 * saleItemList = saleItemDao.findItemsByPid(saleId); if (saleItemList
		 * != null && saleItemList.size() > 0) { // 订单行项目包含的散件ID Set<String>
		 * sjHeadIdSet = new HashSet<String>(); // 订单行项目包含的物料ID Set<String>
		 * mmHeadIdSet = new HashSet<String>(); // 订单行项目的订单与行项目关联编号 Set<String>
		 * orderCodePosexSet = new HashSet<String>(); // 订单行项目包含的补件ID
		 * Set<String> bjIdSet = new HashSet<String>(); // 订单行项目包含的我的商品ID
		 * Set<String> myGoodsIdSet = new HashSet<String>(); JCoTable imTText =
		 * function.getTableParameterList().getTable("IM_T_TEXT");// 销售订单文本 for
		 * (Iterator iterator = saleItemList.iterator(); iterator.hasNext();) {
		 * SaleItem saleItem = (SaleItem) iterator.next(); // 取消状态不传SAP if
		 * (!"QX".equals(saleItem.getStateAudit())) { // 物料说明(中文) String maktx =
		 * saleItem.getMaktx(); if (ZStringUtils.isNotEmpty(maktx)) { int
		 * totalLen = maktx.length(); int j = 1; for (int i = 0; i < totalLen; i
		 * = i + 132) { String currText = ""; if (maktx.length() > 132) {
		 * currText = maktx.substring(i, i + 132); maktx =
		 * maktx.substring(currText.length(), maktx.length()); } else { currText
		 * = maktx; } imTText.appendRow(); // POSEX POSEX 优先采购订单的项目号 CHAR 6
		 * imTText.setValue("POSEX", saleItem.getPosex()); // FORMAT_COL
		 * TDFORMAT 标记列 CHAR 2 imTText.setValue("FORMAT_COL", j); // TEXT_LINE
		 * TDLINE 文本行 CHAR 132 imTText.setValue("TEXT_LINE", currText); j++; } }
		 * sjHeadIdSet.add(saleItem.getSanjianHeadId());
		 * mmHeadIdSet.add(saleItem.getMaterialHeadId());
		 * orderCodePosexSet.add(saleItem.getOrderCodePosex());
		 * bjIdSet.add(saleItem.getBujianId());
		 * myGoodsIdSet.add(saleItem.getMyGoodsId()); } }
		 * 
		 * JCoTable imTImos01Table =
		 * function.getTableParameterList().getTable("IM_T_IMOS01");//
		 * IMOS对接数据-层级主表 JCoTable imTImos09Table =
		 * function.getTableParameterList().getTable("IM_T_IMOS09");//
		 * IMOS对接数据-孔位 if (orderCodePosexSet.size() > 0) { sb = new
		 * StringBuffer(); int i = 0; for (String str : orderCodePosexSet) {
		 * sb.append("'").append(str); if (i < orderCodePosexSet.size() - 1) {
		 * sb.append("',"); } else { sb.append("'"); } i++; } List<Map<String,
		 * Object>> imosIdbextList = jdbcTemplate
		 * .queryForList("select t.* from IMOS_IDBEXT t where t.orderid in (" +
		 * sb.toString() + ")"); if (imosIdbextList != null &&
		 * imosIdbextList.size() > 0) { Field[] declaredFields =
		 * ImosIdbext.class.getDeclaredFields(); Map<String, Class<?>> fieldMap
		 * = new HashMap<String, Class<?>>(); for (Field field : declaredFields)
		 * { String name = field.getName(); Class<?> type = field.getType();
		 * fieldMap.put(name, type); } for (Map<String, Object> map :
		 * imosIdbextList) { imTImos01Table.appendRow(); // 通过反射生成 for (JCoField
		 * jCoField : imTImos01Table) { String dbName = jCoField.getName();
		 * String beanName = FieldFunction.dbField2BeanField(dbName); if
		 * (!fieldMap.keySet().contains(beanName)) { continue; } Class<?> class1
		 * = fieldMap.get(beanName); Object obj = map.get(dbName); if
		 * (ZStringUtils.isEmpty(obj)) { continue; } Object obj2 = null; if
		 * (class1 == Timestamp.class || class1 == java.sql.Date.class || class1
		 * == java.util.Date.class) { obj2 = DateTools.strToDate(obj.toString(),
		 * DateTools.defaultFormat); } else if (class1 == Double.class || class1
		 * == double.class) { obj2 = new Double(obj.toString()); } else if
		 * (class1 == Integer.class || class1.toString().equals("int")) { obj2 =
		 * new Integer(obj.toString()); } else if (class1 == Long.class ||
		 * class1.toString().equals("long")) { obj2 = new Long(obj.toString());
		 * } else { obj2 = obj; } imTImos01Table.setValue(jCoField.getName(),
		 * obj2); } imTImos01Table.setValue("ID", map.get("ID"));
		 * imTImos01Table.setValue("POSEX",
		 * Integer.parseInt(map.get("ORDERID").toString().split(
		 * saleHeader.getOrderCode())[1])); } }
		 * 
		 * List<Map<String, Object>> imosIdbwgList = jdbcTemplate
		 * .queryForList("select t.* from IMOS_IDBWG t where t.orderid in (" +
		 * sb.toString() + ")"); if (imosIdbwgList != null &&
		 * imosIdbwgList.size() > 0) { Field[] declaredFields =
		 * ImosIdbwg.class.getDeclaredFields(); Map<String, Class<?>> fieldMap =
		 * new HashMap<String, Class<?>>(); for (Field field : declaredFields) {
		 * String name = field.getName(); Class<?> type = field.getType();
		 * fieldMap.put(name, type); } for (Map<String, Object> map :
		 * imosIdbwgList) { imTImos09Table.appendRow(); // 通过反射生成 for (JCoField
		 * jCoField : imTImos09Table) { String dbName = jCoField.getName();
		 * String beanName = FieldFunction.dbField2BeanField(dbName); if
		 * (!fieldMap.keySet().contains(beanName)) { continue; } Class<?> class1
		 * = fieldMap.get(beanName); Object obj = map.get(dbName); if
		 * (ZStringUtils.isEmpty(obj)) { continue; } Object obj2 = null; if
		 * (class1 == Timestamp.class || class1 == java.sql.Date.class || class1
		 * == java.util.Date.class) { obj2 = DateTools.strToDate(obj.toString(),
		 * DateTools.defaultFormat); } else if (class1 == Double.class || class1
		 * == double.class) { obj2 = new Double(obj.toString()); } else if
		 * (class1 == Integer.class || class1.toString().equals("int")) { obj2 =
		 * new Integer(obj.toString()); } else if (class1 == Long.class ||
		 * class1.toString().equals("long")) { obj2 = new Long(obj.toString());
		 * } else { obj2 = obj; } imTImos09Table.setValue(jCoField.getName(),
		 * obj2); } imTImos09Table.setValue("ID", map.get("ID"));
		 * imTImos09Table.setValue("POSEX",
		 * Integer.parseInt(map.get("ORDERID").toString().split(
		 * saleHeader.getOrderCode())[1])); } } }
		 * 
		 * JCoTable imTItemTable =
		 * function.getTableParameterList().getTable("IM_T_ITEM");// 行项目信息
		 * JCoTable imTPriceTable =
		 * function.getTableParameterList().getTable("IM_T_PRICE");// 价格信息
		 * JCoTable imTVcinfTable =
		 * function.getTableParameterList().getTable("IM_T_VCINF");// 销售订单物料配置信息
		 * List<MaterialHead> materialHeadList = saleManager
		 * .createQueryByIn(MaterialHead.class, "id", mmHeadIdSet);
		 * 
		 * // 物料补件信息List List<MaterialBujian> materialBujianList =
		 * saleManager.createQueryByIn(MaterialBujian.class, "id", bjIdSet); //
		 * 物料附加信息List List<SaleItemFj> saleItemFjList =
		 * saleManager.createQueryByIn(SaleItemFj.class, "myGoodsId",
		 * myGoodsIdSet);
		 * 
		 * for (Iterator iterator = saleItemList.iterator();
		 * iterator.hasNext();) { SaleItem saleItem = (SaleItem)
		 * iterator.next(); // 取消状态不传SAP if
		 * (!"QX".equals(saleItem.getStateAudit())) { imTItemTable.appendRow();
		 * 
		 * // POSEX POSEX 优先采购订单的项目号 CHAR 6 imTItemTable.setValue("POSEX",
		 * saleItem.getPosex()); // KZKFG KZKFG 可配置的物料 CHAR 1 // KWMENG KWMENG
		 * 以销售单位表示的累计订单数量 QUAN 15 3 imTItemTable.setValue("KWMENG",
		 * saleItem.getAmount()); // MATNR MATNR 物料号 CHAR 18
		 * imTItemTable.setValue("MATNR", saleItem.getMatnr());
		 * 
		 * for (SaleItemFj saleItemFj : saleItemFjList) { if
		 * (saleItemFj.getMyGoodsId().equals(saleItem.getMyGoodsId())) { //
		 * ZZAZDR ZZAZDR 安装位置 CHAR 20 imTItemTable.setValue("ZZAZDR",
		 * saleItemFj.getZzazdr()); break; } }
		 * 
		 * String kmein = ""; String kpein = ""; String konwa = "";
		 * 
		 * for (MaterialHead materialHead : materialHeadList) { if
		 * (saleItem.getMaterialHeadId().equals(materialHead.getId())) { if
		 * (ZStringUtils.isEmpty(saleItem.getMatnr())) { // MATNR MATNR 物料号 CHAR
		 * 18 imTItemTable.setValue("MATNR", materialHead.getMatnr()); } //
		 * VRKME VRKME 销售单位 UNIT 3 imTItemTable.setValue("VRKME",
		 * materialHead.getMeins()); kmein =
		 * ZStringUtils.isEmpty(materialHead.getKmein()) ? "EA" :
		 * materialHead.getKmein(); kpein =
		 * ZStringUtils.isEmpty(materialHead.getKpein()) ? "1" :
		 * materialHead.getKpein(); konwa =
		 * ZStringUtils.isEmpty(materialHead.getKonwa()) ? "CNY" :
		 * materialHead.getKonwa(); // ZZCOMT ZZCOMT 颜色及材质 NUMC 3 ------字段取消
		 * imTItemTable.setValue("ZZCOMT",
		 * materialHead.getTextureOfMaterial()==null
		 * ?"":materialHead.getTextureOfMaterial() +
		 * (materialHead.getColor()==null
		 * ?(materialHead.getExtwg()==null?"":materialHead
		 * .getExtwg()):materialHead.getColor())); // ZZWGFG ZZWGFG 是否外购物料 CHAR
		 * 1 imTItemTable.setValue("ZZWGFG",
		 * "1".equals(materialHead.getZzwgfg()) ? "X" : ""); // ZZTYAR ZZTYAR
		 * 投影面积平方数 QUAN 15 2 imTItemTable.setValue("ZZTYAR",
		 * materialHead.getZztyar()); // ZZZKAR ZZZKAR 板件展开面积平方数 QUAN 15 2
		 * imTItemTable.setValue("ZZZKAR", materialHead.getZzzkar()); // ZZCPYT
		 * ZZCPYT 产品用途区分 NUMC 3 imTItemTable.setValue("ZZCPYT",
		 * materialHead.getZzcpyt()==null?"":materialHead.getZzcpyt()); //
		 * ZZCPDJ ZZCPDJ 产品等级 CHAR 2 imTItemTable.setValue("ZZCPDJ",
		 * materialHead.getZzcpdj());
		 * 
		 * // ZZADHM ZZADHM 审单员 CHAR 10 imTItemTable.setValue("ZZADHM",
		 * saleHeader.getCheckDrawUser());
		 * 
		 * // ZZYMFS ZZYMFS 移门方数 QUAN 15 2 imTItemTable.setValue("ZZYMFS",
		 * materialHead.getZzymfs()); // ZZYMSS ZZYMSS 移门扇数 CHAR 6
		 * imTItemTable.setValue("ZZYMSS", materialHead.getZzymss()); // ZZXSFS
		 * ZZXSFS 吸塑方数 QUAN 15 2 imTItemTable.setValue("ZZXSFS",
		 * materialHead.getZzxsfs()); List<Map<String, Object>> materiaFileList
		 * = jdbcTemplate .queryForList(
		 * "select * from ( select t.id,t.upload_file_name,t.upload_file_path from material_file t where t.file_type='PDF' and t.status is null and t.pid='"
		 * + saleItem.getMaterialHeadId() +
		 * "' order by create_time desc ) where rownum<=1"); if (materiaFileList
		 * != null && materiaFileList.size() > 0) { Map<String, Object> fileObj
		 * = materiaFileList.get(0); // ZFLWAY 路径
		 * imTItemTable.setValue("ZFLWAY",
		 * fileObj.get("UPLOAD_FILE_PATH").toString()); // ZFLNAME 名称
		 * imTItemTable.setValue("ZFLNAME",
		 * fileObj.get("UPLOAD_FILE_NAME").toString()); } break; } }
		 * 
		 * if (materialBujianList != null && materialBujianList.size() > 0 &&
		 * ZStringUtils.isNotEmpty(saleItem.getBujianId())) { for
		 * (MaterialBujian materialBujian : materialBujianList) { if
		 * (materialBujian.getId().equals(saleItem.getBujianId())) { // TODO
		 * 修改为补件表 // ZZTSNR ZZTSNR 投诉内容描述 CHAR 80
		 * imTItemTable.setValue("ZZTSNR", materialBujian.getZztsnr()); //
		 * ZZEZX1 ZZEZX1 出错中心1 NUMC 2 imTItemTable.setValue("ZZEZX1",
		 * materialBujian.getZzezx1()); // ZZEZX2 ZZEZX2 出错中心2 NUMC 2
		 * imTItemTable.setValue("ZZEZX2", materialBujian.getZzezx2()); //
		 * ZZEBM1 ZZEBM1 出错部门1 NUMC 2 imTItemTable.setValue("ZZEBM1",
		 * materialBujian.getZzebm1()); // ZZEBM2 ZZEBM2 出错部门2 NUMC 2
		 * imTItemTable.setValue("ZZEBM2", materialBujian.getZzebm2()); //
		 * ZZECJ1 ZZECJ1 出错车间1 NUMC 2 imTItemTable.setValue("ZZECJ1",
		 * materialBujian.getZzecj1()); // ZZECJ2 ZZECJ2 出错车间2 NUMC 2
		 * imTItemTable.setValue("ZZECJ2", materialBujian.getZzecj2()); //
		 * ZZESCX1 ZZESCX1 出错生产线1 NUMC 2 imTItemTable.setValue("ZZESCX1",
		 * materialBujian.getZzescx1()); // ZZESCX2 ZZESCX2 出错生产线2 NUMC 2
		 * imTItemTable.setValue("ZZESCX2", materialBujian.getZzescx2()); //
		 * ZZRGX1 ZZRGX1 责任工序1 NUMC 2 imTItemTable.setValue("ZZRGX1",
		 * materialBujian.getZzrgx1()); // ZZRGX2 ZZRGX2 责任工序2 NUMC 2
		 * imTItemTable.setValue("ZZRGX2", materialBujian.getZzrgx2()); //
		 * ZZELB1 ZZELB1 出错类别1 NUMC 2 imTItemTable.setValue("ZZELB1",
		 * materialBujian.getZzelb1()); // ZZELB2 ZZELB2 出错类别2 NUMC 2
		 * imTItemTable.setValue("ZZELB2", materialBujian.getZzelb2()); break; }
		 * } }
		 * 
		 * kmein = ZStringUtils.isEmpty(kmein) ? "EA" : kmein; kpein =
		 * ZStringUtils.isEmpty(kpein) ? "1" : kpein; konwa =
		 * ZStringUtils.isEmpty(konwa) ? "CNY" : konwa;
		 * 
		 * Set<SaleItemPrice> saleItemPrices = saleItem.getSaleItemPrices(); if
		 * (saleItemPrices != null && saleItemPrices.size() > 0) { for
		 * (SaleItemPrice saleItemPrice : saleItemPrices) {
		 * imTPriceTable.appendRow(); // POSEX POSEX 优先采购订单的项目号 CHAR 6
		 * imTPriceTable.setValue("POSEX", saleItem.getPosex()); // KSCHL KSCHL
		 * 条件类型 CHAR 4 imTPriceTable.setValue("KSCHL", saleItemPrice.getType());
		 * 
		 * String plusOrMinus = saleItemPrice.getPlusOrMinus();
		 * 
		 * if ("0".equals(plusOrMinus)) {// 减 // KBETR KBETR 价格( 条件金额或百分数 ) CURR
		 * 11 2 imTPriceTable.setValue("KBETR",
		 * NumberUtils.round(NumberUtils.subtract(0,
		 * saleItemPrice.getSubtotal()), 2)); } else {// 1是加 // KBETR KBETR 价格(
		 * 条件金额或百分数 ) CURR 11 2 imTPriceTable.setValue("KBETR",
		 * NumberUtils.round(saleItemPrice.getSubtotal(), 2)); }
		 * 
		 * // WAERS WAERS 货币码 CUKY 5 imTPriceTable.setValue("WAERS", konwa); //
		 * KPEIN KPEIN 条件定价单位 DEC 5 imTPriceTable.setValue("KPEIN", kpein); //
		 * KMEIN KMEIN 条件单位 UNIT 3 imTPriceTable.setValue("KMEIN", kmein); } }
		 * 
		 * String materialPropertyItemInfo =
		 * saleItem.getMaterialPropertyItemInfo(); if
		 * (ZStringUtils.isNotEmpty(materialPropertyItemInfo)) { // KZKFG KZKFG
		 * 可配置的物料 CHAR 1 imTItemTable.setValue("KZKFG", "X");
		 * 
		 * String[] split = materialPropertyItemInfo.split(","); for (String str
		 * : split) { if (ZStringUtils.isNotEmpty(str)) {
		 * imTVcinfTable.appendRow(); String[] split2 = str.split(":"); // POSEX
		 * POSEX 优先采购订单的项目号 CHAR 6 imTVcinfTable.setValue("POSEX",
		 * saleItem.getPosex()); // ATINN ATINN 内部特性 NUMC 10 // ATNAM ATNAM 特征名称
		 * CHAR 30 imTVcinfTable.setValue("ATNAM", split2[1]); // ATBEZ ATBEZ
		 * 特性描述 CHAR 30 imTVcinfTable.setValue("ATBEZ", split2[2]); // ATWRT
		 * ATWRT 特性值 CHAR 30 imTVcinfTable.setValue("ATWRT", split2[3]); //
		 * ATWTB ATWTB 特性值文本 CHAR 30 imTVcinfTable.setValue("ATWTB", split2[4]);
		 * } } }
		 * 
		 * if(StringUtils.isEmpty(imTItemTable.getValue("VRKME"))) {
		 * imTItemTable.setValue("VRKME", "EA"); } }
		 * 
		 * } // System.out.println(imTItemTable); //
		 * System.out.println(imTPriceTable);
		 * 
		 * 
		 * List<ImosIdbext> imosIdbextList = saleManager.createQueryByIn(
		 * ImosIdbext.class, "orderid", orderCodePosexSet); List<ImosIdbwg>
		 * imosIdbwgList = saleManager.createQueryByIn( ImosIdbwg.class,
		 * "orderid", orderCodePosexSet); for (Iterator iterator =
		 * imosIdbextList.iterator(); iterator .hasNext();) { ImosIdbext
		 * imosIdbext = (ImosIdbext) iterator.next();
		 * imTImos01Table.appendRow(); // 通过反射生成 for (JCoField jCoField :
		 * imTImos01Table) { imTImos01Table .setValue(jCoField.getName(),
		 * BeanUtils .getValue(imosIdbext, FieldFunction
		 * .dbField2BeanField(jCoField .getName()))); } for (Iterator it2 =
		 * saleItemList.iterator(); it2.hasNext();) { SaleItem saleItem =
		 * (SaleItem) it2.next(); if (saleItem.getOrderCodePosex().equals(
		 * imosIdbext.getOrderid())) { imTImos01Table.setValue("POSEX", saleItem
		 * .getPosex()); break; } } }
		 * 
		 * for (Iterator iterator = imosIdbwgList.iterator(); iterator
		 * .hasNext();) { ImosIdbwg imosIdbwg = (ImosIdbwg) iterator.next();
		 * imTImos09Table.appendRow(); // 通过反射生成 for (JCoField jCoField :
		 * imTImos09Table) { imTImos09Table .setValue(jCoField.getName(),
		 * BeanUtils .getValue(imosIdbwg, FieldFunction
		 * .dbField2BeanField(jCoField .getName()))); } for (Iterator it2 =
		 * saleItemList.iterator(); it2.hasNext();) { SaleItem saleItem =
		 * (SaleItem) it2.next(); if (saleItem.getOrderCodePosex().equals(
		 * imosIdbwg.getOrderid())) { imTImos09Table.setValue("POSEX", saleItem
		 * .getPosex()); break; } } }
		 * 
		 * 
		 * // 散件bomb List<MaterialSanjianHead> mmSjHeadList =
		 * saleManager.createQueryByIn(MaterialSanjianHead.class, "id",
		 * sjHeadIdSet); if (mmSjHeadList != null && mmSjHeadList.size() > 0) {
		 * for (MaterialSanjianHead materialSanjianHead : mmSjHeadList) { String
		 * POSEX = ""; for (Iterator it2 = saleItemList.iterator();
		 * it2.hasNext();) { SaleItem saleItem = (SaleItem) it2.next(); if
		 * (ZStringUtils.isNotEmpty(saleItem.getSanjianHeadId()) &&
		 * saleItem.getSanjianHeadId().equals(materialSanjianHead.getId()) &&
		 * !"QX".equals(saleItem.getStateAudit())) { POSEX =
		 * saleItem.getPosex(); break; } } Set<MaterialSanjianItem>
		 * materialSanjianItemSet = materialSanjianHead
		 * .getMaterialSanjianItemSet(); int i = 0; for (MaterialSanjianItem
		 * materialSanjianItem : materialSanjianItemSet) {
		 * imTImos01Table.appendRow(); imTImos01Table.setValue("ID", ++i);
		 * imTImos01Table.setValue("INFO1", "Z01");
		 * imTImos01Table.setValue("ORDERID", saleHeader.getOrderCode());
		 * imTImos01Table.setValue("POSEX", POSEX);
		 * imTImos01Table.setValue("TYP", "9");
		 * imTImos01Table.setValue("PARENTID", "A"); // RENDER ZRENDER RENDER
		 * CHAR 18 imTImos01Table.setValue("RENDER",
		 * materialSanjianItem.getMatnr()); imTImos01Table.setValue("NAME",
		 * materialSanjianItem.getMiaoshu()); imTImos01Table.setValue("CNT",
		 * materialSanjianItem.getAmount()); } } }
		 * 
		 * } function.execute(connect); JCoTable reTable =
		 * function.getTableParameterList().getTable("EX_T_RETURN"); //
		 * System.out.println(reTable); StringBuffer errSb = new StringBuffer();
		 * if (reTable.getNumRows() > 0) { reTable.firstRow(); for (int i = 0; i
		 * < reTable.getNumRows(); i++, reTable.nextRow()) { Object type =
		 * reTable.getValue("TYPE"); Object id = reTable.getValue("ID"); Object
		 * message = reTable.getValue("MESSAGE"); if (i == 0) { if (type != null
		 * && "S".equals(type.toString())) { saleHeader =
		 * saleHeaderDao.findOne(saleId); saleHeader.setSapOrderCode(id == null
		 * ? "" : id.toString()); saleHeader.setYuJiDate((Date) werksDate); //
		 * saleHeaderDao.save(saleHeader); saleManager.save(saleHeader); msg =
		 * new Message(message == null ? "" : message.toString()); msg.put("id",
		 * id == null ? "" : id.toString()); msg.put("yuJiDate",
		 * DateTools.formatDate((Date) werksDate, DateTools.defaultFormat));
		 * return msg; } else { errSb.append(message == null ? "" :
		 * message.toString()).append("<br/>"); } } else { if (type != null &&
		 * "E".equals(type.toString())) { errSb.append(message == null ? "" :
		 * message.toString()).append("<br/>"); } } } } else {
		 * errSb.append("接口异常，没有返回信息"); } if (!errSb.toString().equals("")) {
		 * msg = new Message("SALE-500", errSb.toString()); }
		 *//*******************************************************************/
		/*
            *//*********************** SAP创建销售单结束(end) *********************/
		/*
            *//*******************************************************************/
		/*
		 * } catch (JCoException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); // msg = new Message("DD-S-500",
		 * e.getLocalizedMessage()); msg = new Message("SALE-500",
		 * e.getLocalizedMessage()); } return msg;
		 */}
	
	@RequestMapping(value = "/updateHoleInfo", method = RequestMethod.GET)
	@ResponseBody
	public Message updateHoleInfo(String id,String orderid,String cncbarcode1,String cncbarcode2) throws ParseException{
		System.out.println(id+""+orderid);
		Message msg=null;
		int row1=0;
		int row2=0;
		String groove ="";
		String side = "";
		String trough = "";
		String poge = "";
		String xlk = "";
		String nums = "";
		//修改barcode码
		String cncbarcode1Sql ="UPDATE IMOS_IDBEXT I SET I.CNC_BARCODE1='"+cncbarcode1+"' WHERE I.ORDERID='"+orderid+"' AND I.ID='"+id+"'";
		 row1 = jdbcTemplate.update(cncbarcode1Sql);

		String cncbarcode2Sql ="UPDATE IMOS_IDBEXT I SET I.CNC_BARCODE2='"+cncbarcode2+"' WHERE I.ORDERID='"+orderid+"' AND I.ID='"+id+"'";
		row2 = jdbcTemplate.update(cncbarcode2Sql);
		
		//修改工艺路线
		String barcodeSql ="";
		barcodeSql="select i.tec_groove,i.tec_side,i.tec_trough,i.tec_pore,i.tec_xlk,i.tec_pore_nums,i.cnc_barcode1,i.barcode from imos_idbext i  where i.barcode='"+cncbarcode1+"'"; 
		List<Map<String, Object>> tecList = jdbcTemplate.queryForList(barcodeSql);
		if(tecList.size()<=0) {
			barcodeSql="select i.tec_groove,i.tec_side,i.tec_trough,i.tec_pore,i.tec_xlk,i.tec_pore_nums,i.cnc_barcode1,i.barcode from imos_idbext i  where i.barcode='"+cncbarcode2+"'"; 
			tecList = jdbcTemplate.queryForList(barcodeSql);
		}
		if(tecList.size()>0) {
			Map<String, Object> tecMap = tecList.get(0);
			groove = (String) tecMap.get("TEC_GROOVE")==null?"":(String) tecMap.get("TEC_GROOVE");
			side= (String) tecMap.get("TEC_SIDE")==null?"":(String) tecMap.get("TEC_SIDE");
			trough = (String) tecMap.get("TEC_TROUGH")==null?"":(String) tecMap.get("TEC_TROUGH");
			poge = (String) tecMap.get("TEC_PORE")==null?"":(String) tecMap.get("TEC_PORE");
			xlk = (String) tecMap.get("TEC_XLK")==null?"":(String) tecMap.get("TEC_XLK");
			nums = (String) tecMap.get("TEC_PORE_NUMS")==null?"":(String) tecMap.get("TEC_PORE_NUMS");
		}	
		 StringBuffer tecSql = new StringBuffer();
		 tecSql.append("update imos_idbext i set i.tec_groove='"+groove+"',i.tec_side='"+side+"',i.tec_trough='"+trough+"',i.tec_pore='"+poge+"',i.tec_xlk='"+xlk+"',i.tec_pore_nums='"+nums+"' where i.orderid=? and I.CNC_BARCODE1=?");
		 jdbcTemplate.update(tecSql.toString(),orderid,cncbarcode1);
		 tecSql.setLength(0);
		 tecSql.append("update imos_idbext i set i.tec_groove='"+groove+"',i.tec_side='"+side+"',i.tec_trough='"+trough+"',i.tec_pore='"+poge+"',i.tec_xlk='"+xlk+"',i.tec_pore_nums='"+nums+"' where i.orderid=? and I.CNC_BARCODE1=?");
		 jdbcTemplate.update(tecSql.toString(),orderid,cncbarcode2);
		if(row1>0||row2>0) {
			 msg = new Message(true);
		}else {
			 msg = new Message(false);
		}
//		msg.put(key, value)
		return msg;
	}
	
	@RequestMapping(value = "/findById", method = RequestMethod.GET)
	@ResponseBody
	public Message findById(String id) throws ParseException {

		Message msg = null;

		try {
			SaleHeader saleHeader = saleHeaderDao.findOne(id);
			if (saleHeader == null) {
				List<SaleHeader> findByCode = saleHeaderDao.findByCode(id);
				if (findByCode != null && findByCode.size() > 0) {
					saleHeader = findByCode.get(0);
				}
			}
			String kunnr = saleHeader.getShouDaFang();
			String remarks2="";
			List<Map<String, Object>> list = jdbcTemplate.queryForList("select distinct ce.cust_level_zhekou from cust_event ce where ce.kunnr='"+kunnr+"'");
			if(list.size()>0){
				if(list.get(0).get("cust_level_zhekou")!=null){
					remarks2 = list.get(0).get("cust_level_zhekou").toString();
				}
			}
			// 调用SAP接口取订单：实 际出货日期shiJiDate/实际入库日期 shiJiDate2/计划完工日期YuJiDate2
			List<SaleItem> saleItems = saleItemDao.findByItems(saleHeader.getOrderCode());
			if (saleItems.size()<=0) {
				List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(id);
				for (SaleLogistics saleLogistics : saleLogisticsList) {
					if(saleLogistics.getSapCode()==null&&"".equals(saleLogistics.getSapCode())) {
						continue;
					}
					JCoDestination connect = SAPConnect.getConnect();
					JCoFunction functionXd = connect.getRepository()
							.getFunction("ZRFC_SD_DELIVERY_DATE");
					JCoParameterList importParameterList = functionXd
							.getImportParameterList();

					importParameterList.setValue("I_VBELN",
							saleLogistics.getSapCode());
					functionXd.execute(connect);
					JCoParameterList exportParameterList = functionXd
							.getExportParameterList();
					Object postFlag = exportParameterList.getValue("E_TYPE");
					Object message = exportParameterList.getValue("E_MSG");

					Object E_WADAT = exportParameterList.getValue("E_WADAT");
					Object E_GLTRP = exportParameterList.getValue("E_GLTRP");
					Object E_GLTRI = exportParameterList.getValue("E_LTRMI");
					/*
					 * E_WADAT实际出货日期 shiJiDate E_GLTRI实际入库日期 shiJiDate2
					 * E_GLTRP计划完工日期 YuJiDate2
					 */
					if ("S".equals(postFlag) || "W".equals(postFlag)) {
						if (E_WADAT != null) {
							saleLogistics.setPoDate((Date) E_WADAT);
							//如果实际出库日期有了，那么需要将订单的状态改为已发货
							saleHeader.setOrderStatus("4");
						}
						if (E_GLTRI != null)
							saleLogistics.setPbDate((Date) E_GLTRI);
						if (E_GLTRP != null)
							saleLogistics.setPcDate((Date) E_GLTRP);
						if (E_WADAT != null && E_GLTRP != null
								&& E_GLTRI != null) {
							commonManager.save(saleLogistics);
						}
						
					} else {
						System.out
								.println("失败-------》调用SAP接口取订单：实 际出货日期shiJiDate/实际入库日期 shiJiDate2/计划完工日期YuJiDate2  "
										+ message);
					}
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Map<String, Object> map = new HashMap<String, Object>();

			String shouDaFang = saleHeader.getShouDaFang();
			List<CustHeader> findByCode = custHeaderDao.findByCode(shouDaFang);
			if (findByCode != null && findByCode.size() > 0) {
				map.put("kunnrName1", ZStringUtils.isEmpty(findByCode.get(0)
						.getName1()) ? "" : findByCode.get(0).getName1());
			}

			map.put("id", saleHeader.getId());
			map.put("createTime",
					saleHeader.getCreateTime() == null ? "" : sdf
							.format(saleHeader.getCreateTime()));
			map.put("urgentType",
					saleHeader.getUrgentType() == null ? ""
							: saleHeader.getUrgentType());
			map.put("updateTime",
					saleHeader.getUpdateTime() == null ? "" : sdf
							.format(saleHeader.getUpdateTime()));
			map.put("createUser", saleHeader.getCreateUser() == null ? ""
					: saleHeader.getCreateUser());
			map.put("updateUser", saleHeader.getUpdateUser() == null ? ""
					: saleHeader.getUpdateUser());
			List<Map<String,Object>> _list=jdbcTemplate.queryForList("select cc.qq_num from cust_contacts cc, sale_header sh where cc.telf1=sh.designer_tel and sh.id='"+ saleHeader.getId() + "' and cc.qq_num is not null");
			if(_list!=null && _list.size()>0){
				map.put("qqNum", _list.get(0).get("QQ_NUM")==null?"":(String)  _list.get(0).get("QQ_NUM"));
			}
			List<CustHeader> custHeaders=custHeaderDao.findByCode(saleHeader.getShouDaFang());
			if(custHeaders!=null && custHeaders.size()>0){
				map.put("kunnrS",custHeaders.get(0).getKunnrS());
			}
			Field[] declaredFields = saleHeader.getClass().getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				String name = field.getName();
				if (name == "saleItemSet" || name == "terminalClient") {
					continue;
				} else {
					Object property = BeanUtils.getValue(saleHeader, name);
					if (property != null) {
						if (name.equals("orderDate") || name.equals("yuJiDate")
								|| name.equals("yuJiDate3")
								|| name.equals("yuJiDate2")
								|| name.equals("shiJiDate")
								|| name.equals("shiJiDate2")) {
							map.put(name, DateTools.formatDate((Date) property,
									DateTools.defaultFormat));
						} else {
							map.put(name, property);
						}
					}
				}
			}
			TerminalClient terminalClient = saleHeader.getTerminalClient();
			map.put("tcId", terminalClient.getId());
			Field[] declaredFields2 = terminalClient.getClass()
					.getDeclaredFields();
			for (int i = 0; i < declaredFields2.length; i++) {
				Field field = declaredFields2[i];
				String name = field.getName();
				if (name == "saleHeader") {
					continue;
				} else {
					Object property = BeanUtils.getValue(terminalClient, name);
					if (property != null) {
						if (name.equals("birthday")) {
							map.put(name, DateTools.formatDate(
									terminalClient.getBirthday(),
									DateTools.defaultFormat));
						} else {
							map.put(name, property);
						}
					}
				}
			}
			if(!"".equals(remarks2)){
				map.put("remarks2",remarks2);
			}
			
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "saleItemSet","saleLogisticsSet",
					"terminalClient", "saleOneCustSet", "serialVersionUID" };
			// System.out.println(JSONObject.fromObject(map,
			// super.getJsonConfig(strings)));
			msg = new Message(JSONObject.fromObject(map,
					super.getJsonConfig(strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-GET-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 根据ids查找saleHeader，(真删除)
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteSaleByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteSaleByIds(String[] ids) {
		Message msg = null;
		try {
			if (ids != null && ids.length > 0) {
				saleManager.deleteSaleByIds(ids);
			}
			msg = new Message("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	/**
	 * 客户删除起草订单使用，只能删除没有订单编号的订单
	 * 
	 * @param id
	 *            订单Id
	 * @return
	 */
	@RequestMapping(value = "/deleteSaleById4Customer", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteSaleById4Customer(String[] ids) {
		Message msg = null;
		try {
			if (ids != null && ids.length > 0) {
				StringBuilder sb = new StringBuilder();
				for (int index = 0; index < ids.length; index++) {
					SaleHeader saleHeader = saleHeaderDao.findOne(ids[index]);
					if (!StringUtils.isEmpty(saleHeader.getOrderCode())) {
						sb.append("有订号的订单不能删除" + saleHeader.getOrderCode());
					}
				}
				if (sb.length() == 0) {
					saleManager.deleteSaleByIds(ids);
					msg = new Message("删除成功");
				} else {
					msg = new Message("MM-S-500", sb.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	@RequestMapping(value = "/queryDicd", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryDicd() {
		String keyVal = this.getRequest().getParameter("keyVal");
		StringBuffer sb = new StringBuffer();
		sb.append("select t.* from sys_data_dict t inner join sys_trie_tree t2 on t.trie_id = t2.id  where 1=1 ");
		sb.append(" and t2.key_val='" + keyVal + "'");
		sb.append(" order by t.order_by");
		List<SysDataDict> queryByNativeQuery = sysDataDictDao
				.queryByNativeQuery(sb.toString());
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "trieTree" };
		// System.out.println(JSONArray.fromObject(queryByNativeQuery,
		// super.getJsonConfig(strings)));
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		return JSONArray.fromObject(queryByNativeQuery,
				super.getJsonConfig(strings));
	}

	/**
	 * 根据SaleHeader.id查找对应SaleItem
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/findItemsByPid", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean findItemsByPid(String pid) {
		// List<SaleItem> findItemByPid = saleItemDao.findItemsByPid(pid);
		// String[] strings = new String[] { "hibernateLazyInitializer",
		// "handler", "fieldHandler", "sort", "saleHeader",
		// "saleItemPrices" };
		// System.out.println(JSONArray.fromObject(findItemByPid, super
		// .getJsonConfig("yyyy-MM-dd HH:mm:ss", strings)));
		// // "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		// return JSONArray.fromObject(findItemByPid, super.getJsonConfig(
		// "yyyy-MM-dd HH:mm:ss", strings));
		
		StringBuffer sb = new StringBuffer(
				"select t.*,t4.ACT_NAME_ jd_name,t5.DRAW_TYPE,t5.zzwgfg,t5.file_type,"
						+ "(DECODE(T.IS_STANDARD, '0',( select mf.id from MATERIAL_FILE mf  where mf.file_type = 'PRICE' and nvl(mf.status, 'C') != 'X' and mf.pid =t5.id and rownum = 1), ( select mf.id from MATERIAL_FILE mf  where mf.file_type = 'BJ_PRICE' and nvl(mf.status, 'C') != 'X' and mf.MAPPING_ID =T.BUJIAN_ID and rownum = 1 ))) price_id,"
						+ "CASE WHEN T.ORTYPE='OR3' OR T.ORTYPE='OR4' THEN (select mf.id from MATERIAL_FILE mf  where mf.file_type = 'BJ' AND nvl(mf.status, 'C') != 'X' and mf.Mapping_Id =t.Bujian_Id and rownum = 1) ELSE (DECODE(T.IS_STANDARD, '0',( select mf.id from MATERIAL_FILE mf  where mf.file_type = 'PDF' and nvl(mf.status, 'C') != 'X' and mf.pid =t5.id and rownum = 1), (select mf.id from MATERIAL_FILE mf  where mf.file_type = 'PDF' and nvl(mf.status, 'C') != 'X' and mf.MAPPING_ID =t5.id and rownum = 1))) END pdf_id,"
						+ "(DECODE(T.IS_STANDARD, '0',(select mf.id from MATERIAL_FILE mf  where mf.file_type = 'KIT' and nvl(mf.status, 'C') != 'X' and mf.pid =t5.id and rownum = 1), (select mf.id from MATERIAL_FILE mf  where mf.file_type = 'KIT' and nvl(mf.status, 'C') != 'X' and mf.MAPPING_ID =t5.id and rownum = 1))) kit_id"
						+ " from sale_item t ");
		// sb.append(" left join act_ct_mapping t3 on t3.id = t.id ");
		sb.append("  left join act_ord_curr_node9 t4 on t4.id = t.id ");
		sb.append(" left join material_head t5 on t5.id = t.material_head_id ");
		sb.append(" where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();
		sb.append(" and t.pid = ? ");
		String sql = "select T.ID,"
	   +" MH.SALE_FOR,"
	   +" T.SAP_CODE,"
	   +" T.SAP_CODE_POSEX,"
       +" T.CREATE_TIME,"
       +" T.CREATE_USER,"
       +" T.ROW_STATUS,"
       +" T.UPDATE_TIME,"
       +" T.UPDATE_USER,"
       +" T.AMOUNT,"
       +" T.AREA,"
       +" T.COLOUR,"
       +" T.COLOUR_DESC,"
       +" T.IS_SALE,"
       +" T.ITEM_DESC,"
       +" T.NAME,"
       +" T.REMARK,"
       +" T.SERIAL_NUMBER,"
       +" T.SPEC,"
       +" T.STATUS,"
       +" T.TOTAL_PRICE,"
       +" T.TOU_YING_AREA,"
       +" T.TYPE,"
       +" T.UNIT,"
       +" T.UNIT_PRICE,"
       +" T.ZHE_KOU,"
       +" T.ZHE_KOU_JIA,"
       +" T.PID,"
       +" T.IS_STANDARD,"
       +" T.MATERIAL_HEAD_ID,"
       +" T.MATERIAL_PROPERTY_ITEM_INFO,"
       +" T.MY_GOODS_ID,"
       +" T.POSEX,"
       +" T.MAKTX,"
       +" T.MATNR,"
       +" T.MTART,"
       +" T.MATERIAL_PRICE,"
       +" T.MATERIAL_PROPERTY_ITEM_ID,"
       +" T.SANJIAN_HEAD_ID,"
       +" T.ORDER_CODE_POSEX,"
       +" T.BUJIAN_ID,"
       +" T.ORTYPE,"
       +" T.STATE_AUDIT,"
       +" T.ABGRU,"
       +" T.IS_CNC,"
       +" T.BOARD_THICKNESS,"
       +" T.PARENT_ID,"
       +" T.EXT_NAME,"
       +" T.EXT_THICKNESS,"
       +" T.EXT_WIDTH,"
       +" T.ERR_REA,"
       +" T.ERR_TYPE,"
       +" T.JIAO_QI_TIAN_SHU,"
       +" T.JIAO_QI_TIAN_SHU_INNER,"
       +" T.STYLE,"
       +" T.URGE_TYPE "
				+ ",(SELECT MH.SERIAL_NUMBER FROM MATERIAL_HEAD MH WHERE T.MATERIAL_HEAD_ID=MH.ID) as serial,(select mh.sale_for from material_head mh where mh.id=t.material_head_id)as sale_for, (select t4.act_name_ from ( act_ord_curr_nodek ) t4 where t4.proc_inst_id_=acm.procinstid) as jd_name, (select t5.draw_type from material_head t5 where t5.id=t.material_head_id) as draw_TYPE, (select t5.zzwgfg from material_head t5 where t5.id=t.material_head_id) as zzwgfg, (select t5.file_type from material_head t5 where t5.id=t.material_head_id) as file_type, (DECODE(T.IS_STANDARD, '0', (select mf.id from MATERIAL_FILE mf where mf.file_type = 'PRICE' and nvl(mf.status, 'C') != 'X' and mf.pid = t.material_head_id and rownum = 1), (select mf.id from MATERIAL_FILE mf where mf.file_type = 'BJ_PRICE' and nvl(mf.status, 'C') != 'X' and mf.MAPPING_ID = T.BUJIAN_ID and rownum = 1))) price_id, CASE WHEN T.ORTYPE = 'OR3' OR T.ORTYPE = 'OR4' THEN (select mf.id from MATERIAL_FILE mf where mf.file_type = 'BJ' AND nvl(mf.status, 'C') != 'X' and mf.Mapping_Id = t.Bujian_Id and rownum = 1) ELSE (DECODE(T.IS_STANDARD, '0', (select mf.id from MATERIAL_FILE mf where mf.file_type = 'PDF' and nvl(mf.status, 'C') != 'X' and mf.pid = t.material_head_id and rownum = 1), (select mf.id from MATERIAL_FILE mf where mf.file_type = 'PDF' and nvl(mf.status, 'C') != 'X' and mf.MAPPING_ID = t.material_head_id and rownum = 1))) END pdf_id, (DECODE(T.IS_STANDARD, '0', (select mf.id from MATERIAL_FILE mf where mf.file_type = 'KIT' and nvl(mf.status, 'C') != 'X' and mf.pid = t.material_head_id and rownum = 1), (select mf.id from MATERIAL_FILE mf where mf.file_type = 'KIT' and nvl(mf.status, 'C') != 'X' and mf.MAPPING_ID = t.material_head_id and rownum = 1))) kit_id from sale_item t left join material_head mh on t.material_head_id=mh.id left join act_ct_mapping acm on t.id=acm.id  where 1 = 1 and t.pid = ? order by to_number(t.posex)";
		params.add(pid);

		sb.append(" order by to_number(t.posex) ");

		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
		// formats.put("createTime", new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// System.out.println("jdbc-Sql：" + sb.toString());
		// System.out.println(sb.toString());
		List<Map<String, Object>> queryForList = jdbcTemplate.query(sql,
				params.toArray(), new MapRowMapper(true, formats));
		// System.out.println("======>" + queryForList);
		return new JdbcExtGridBean(1, queryForList.size(), queryForList.size(),
				queryForList);

	}

	// @RequestMapping(value = { "/queryDicd" }, method = RequestMethod.GET)
	// @ResponseBody
	// public JdbcExtGridBean queryDicd(int page, int limit) {
	// String keyVal = this.getRequest().getParameter("keyVal");
	//
	// StringBuffer sb = new StringBuffer(
	// "select t.* from sys_data_dict_view t where 1=1 ");
	// // sql params
	// List<Object> params = new ArrayList<Object>();
	//
	// if (!StringUtils.isEmpty(keyVal)) {
	// sb.append(" and t.key_val = ? ");
	// params.add(keyVal);
	// }
	// sb.append(" order by t.order_by ");
	// // 获取总记录数
	// List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(sb
	// .toString(), params.toArray());
	//
	// StringBuffer pageSQL = new StringBuffer("select * from (");
	// if (page - 1 == 0) {
	// pageSQL.append(sb + " ) where rownum <= ?");
	// params.add(limit);
	// } else {
	// pageSQL.append("select row_.*, rownum rownum_ from ( " + sb
	// + ") row_ where rownum <= ?) where rownum_ > ?");
	// params.add(limit);
	// params.add(page - 1);
	// }
	//
	// // 总页数=总记录数/每页条数(系数加1)
	// int totalPages = (totalElements.size() + limit - 1) / limit;
	//
	// // 多个时间字段转换
	// // Map<String,SimpleDateFormat> formatMap = new HashMap<String,
	// // SimpleDateFormat>();
	// // formatMap.put("createTime", new SimpleDateFormat("yyyy-MM"));
	// // formatMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd"));
	// // formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));
	//
	// System.out.println(pageSQL.toString());
	// // 获取当前分页数据
	// List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
	// .toString(), params.toArray(), new MapRowMapper(true));
	//
	// return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
	// queryForList);
	// }

	/**
	 * 根据ids查找saleItem，(不做真删除)更新删除标志
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteSaleItemByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteSaleItemByIds(String[] ids, String custCode) {
		Message msg = null;
		try {
			if (custCode == null) {

				SysUser loginUser = this.getLoginUser();
				custCode = loginUser.getKunnr();
			}

			if (ids != null && ids.length > 0) {
				saleManager.deleteSaleItemByIds(ids, custCode);
			}
			/*
			 * jdbcTemplate
			 * .execute("delete sale_item_price where SALE_ITEMID in(" +
			 * StringUtil.arrayToString(ids) + ")"); saleManager.delete(ids,
			 * SaleItem.class);
			 */
			/*
			 * final List list = new ArrayList(); for (String id : ids) {
			 * Map<String, Object> map = new HashMap<String, Object>();
			 * map.put("id", id); map.put("update_user",
			 * this.getLoginUser().getLoginNo()); list.add(map); } String sql =
			 * "update sale_item set status='0',update_time=?,update_user=? where id=?"
			 * ; jdbcTemplate.batchUpdate(sql, new
			 * BatchPreparedStatementSetter() { public int getBatchSize() {
			 * return list.size(); //
			 * 这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size(); }
			 * 
			 * @Override public void setValues(PreparedStatement ps, int i)
			 * throws SQLException { Map<String, Object> map = (Map<String,
			 * Object>) list.get(i); ps.setTimestamp(1, new
			 * Timestamp(System.currentTimeMillis())); ps.setString(2,
			 * map.get("update_user") == null ? "" : map
			 * .get("update_user").toString()); ps.setString(3, map.get("id") ==
			 * null ? "" : map.get("id") .toString()); } });
			 */
			msg = new Message("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	/**
	 * 根据ids查找saleItem，(不做真删除)更新删除标志 新下单界面删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteSaleItemById", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteSaleItemById(String[] ids, String custCode,
			String saleId) {
		Message msg = null;
		try {
			if (custCode == null) {

				SysUser loginUser = this.getLoginUser();
				custCode = loginUser.getKunnr();
			}
			// add by mark on 20170621 --start 已经审核过的行项目需要通过取消流程，不应该能被删除
			SaleItem saleItem = null;
			StringBuilder sb = new StringBuilder();
			if (ids != null && ids.length > 0) {
				for (int index = 0; index < ids.length; index++) {
					saleItem = saleItemDao.getOne(ids[index]);
					if ("B".equals(saleItem.getStateAudit())
							|| "D".equals(saleItem.getStateAudit())
							|| "E".equals(saleItem.getStateAudit())) {
						sb.append(saleItem.getPosex() + ",");
					}
				}
				if (sb.length() > 0) {
					msg = new Message("MM-S-500", "行项目" + sb.toString()
							+ "已经审核，如需删除请走取消流程");
					return msg;
				} else {
					// //add by mark on 20170621 --end
					saleManager.deleteSaleItemByIds(ids, custCode);
				}
			}
			// 更新订单行项目编号
			// if(!"".equals(saleId)&&saleId!=null){
			// List<Map<String, Object>> sis =
			// jdbcTemplate.queryForList("select t.*,rownum*10 rn from ( select s.posex,s.id from sale_item s where s.pid ='"+saleId+"' order by to_number(s.posex) asc ) t");
			// for (int i = 0; i < sis.size(); i++) {
			// Map<String, Object> map=sis.get(i);
			// jdbcTemplate.update("update sale_item s set s.posex='"+map.get("RN")+"' where s.posex='"+map.get("POSEX")+"' and s.id='"+map.get("ID")+"'");
			// }
			// }
			/*
			 * jdbcTemplate
			 * .execute("delete sale_item_price where SALE_ITEMID in(" +
			 * StringUtil.arrayToString(ids) + ")"); saleManager.delete(ids,
			 * SaleItem.class);
			 */
			/*
			 * final List list = new ArrayList(); for (String id : ids) {
			 * Map<String, Object> map = new HashMap<String, Object>();
			 * map.put("id", id); map.put("update_user",
			 * this.getLoginUser().getLoginNo()); list.add(map); } String sql =
			 * "update sale_item set status='0',update_time=?,update_user=? where id=?"
			 * ; jdbcTemplate.batchUpdate(sql, new
			 * BatchPreparedStatementSetter() { public int getBatchSize() {
			 * return list.size(); //
			 * 这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size(); }
			 * 
			 * @Override public void setValues(PreparedStatement ps, int i)
			 * throws SQLException { Map<String, Object> map = (Map<String,
			 * Object>) list.get(i); ps.setTimestamp(1, new
			 * Timestamp(System.currentTimeMillis())); ps.setString(2,
			 * map.get("update_user") == null ? "" : map
			 * .get("update_user").toString()); ps.setString(3, map.get("id") ==
			 * null ? "" : map.get("id") .toString()); } });
			 */
			msg = new Message("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	/**
	 * 根据ids删除SaleOneCust
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteSaleOneCustByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteSaleOneCustByIds(String[] ids) {
		Message msg = null;
		try {
			if (ids != null) {
				saleManager.delete(ids, SaleOneCust.class);
			}
			msg = new Message("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	@RequestMapping(value = { "/listForHoleInfo" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForListForHoleInfo(int page, int limit) {
		String pOrderCode = this.getRequest().getParameter("pOrderCode");
		String rowNum = this.getRequest().getParameter("rowNum");
		String info1 = this.getRequest().getParameter("info1");
		String name = this.getRequest().getParameter("name");
		String length = this.getRequest().getParameter("length");
		String width = this.getRequest().getParameter("width");
		String thickness = this.getRequest().getParameter("thickness");
		int r =  Integer.parseInt(rowNum); 
		String row = pOrderCode+String.format("%04d",r);
//		String orderId = pOrderCode+"00"+row;//拼接行号
		StringBuffer sb = new StringBuffer(
				"select * from xv_tree_material_bg where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		if (!StringUtils.isEmpty(row)) {
			sb.append(" and ORDERID = ? ");
			params.add(row);
		}

		if (!StringUtils.isEmpty(info1)) {
			sb.append(" and INFO1 like ? ");
			params.add(StringHelper.like(String.valueOf(info1)));
		}
		if (!StringUtils.isEmpty(name)) {
			sb.append(" and NAME like ? ");
			params.add(StringHelper.like(String.valueOf(name)));
		}
		if (!StringUtils.isEmpty(length)) {
			sb.append(" and LENGTH = ? ");
			params.add(length);
		}
		if (!StringUtils.isEmpty(thickness)) {
			sb.append(" and THICKNESS = ? ");
			params.add(thickness);
		}
		if (!StringUtils.isEmpty(width)) {
			sb.append(" and WIDTH = ? ");
			params.add(width);
		}
		// 获取总记录数
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(
				sb.toString(), params.toArray());

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
		Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
		formatMap.put("orderDate",
				new SimpleDateFormat(DateTools.defaultFormat));
		formatMap
				.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// formatMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd"));
		// formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));

		//System.out.println(pageSQL.toString());
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(),
				new MapRowMapper(true, formatMap));
		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}
	
	@RequestMapping(value = { "/listForWin" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForListForWin(int page, int limit) {
		String orderCode = this.getRequest().getParameter("orderCode");
		String orderType = this.getRequest().getParameter("orderType");
		String startDate = this.getRequest().getParameter("startDate");
		String endDate = this.getRequest().getParameter("endDate");
		String shouDaFang = this.getRequest().getParameter("shouDaFang");
		String dianMianTel = this.getRequest().getParameter("dianMianTel");
		String name1 = this.getRequest().getParameter("name1");
		String tel = this.getRequest().getParameter("tel");
		String kunnrName1 = this.getRequest().getParameter("kunnrName1");
		String sapOrderCode = this.getRequest().getParameter("sapOrderCode");

		String saleFlag = this.getRequest().getParameter("saleFlag");
		String queryBgType = this.getRequest().getParameter("queryBgType");

		StringBuffer sb = new StringBuffer(
				"select t.* from sale_view t where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		if (!StringUtils.isEmpty(orderCode)) {
			sb.append(" and ORDER_CODE like ? ");
			params.add(StringHelper.like(String.valueOf(orderCode)));
		}
		if (!StringUtils.isEmpty(orderType)) {
			sb.append(" and ORDER_TYPE = ? ");
			params.add(orderType);
		}

		if (!StringUtils.isEmpty(startDate)) {
			sb.append(" and ORDER_DATE >= ? ");
			params.add(DateTools.strToDate(startDate, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(endDate)) {
			sb.append(" and ORDER_DATE <= ? ");
			params.add(DateTools.strToDate(endDate, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(shouDaFang)) {
			sb.append(" and SHOU_DA_FANG like ? ");
			params.add(StringHelper.like(String.valueOf(shouDaFang)));
		}
		if (!StringUtils.isEmpty(dianMianTel)) {
			sb.append(" and DIAN_MIAN_TEL like ? ");
			params.add(StringHelper.like(String.valueOf(dianMianTel)));
		}
		if (!StringUtils.isEmpty(name1)) {
			sb.append(" and NAME1 like ? ");
			params.add(StringHelper.like(String.valueOf(name1)));
		}
		if (!StringUtils.isEmpty(tel)) {
			sb.append(" and TEL like ? ");
			params.add(StringHelper.like(String.valueOf(tel)));
		}
		if (!StringUtils.isEmpty(kunnrName1)) {
			sb.append(" and KUNNR_NAME1 like ? ");
			params.add(StringHelper.like(String.valueOf(kunnrName1)));
		}
		if (!StringUtils.isEmpty(sapOrderCode)) {
			sb.append(" and SAP_ORDER_CODE like ? ");
			params.add(StringHelper.like(String.valueOf(sapOrderCode)));
		}

		if (!StringUtils.isEmpty(saleFlag)) {
			// 变更订单窗口查询销售单
			if (saleFlag.equals("bgFlag")) {
				sb.append(" and JD_NAME <> '结束' and JD_NAME is not null ");
			}// 补单窗口查询销售单
			else if (saleFlag.equals("bdFlag")) {
				sb.append(" and JD_NAME = '结束' ");
			}
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
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(
				sb.toString(), params.toArray());

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
		Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
		formatMap.put("orderDate",
				new SimpleDateFormat(DateTools.defaultFormat));
		formatMap
				.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// formatMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd"));
		// formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));

		//System.out.println(pageSQL.toString());
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(),
				new MapRowMapper(true, formatMap));
		SysUser sysUser = (SysUser) this.getRequest().getSession()
				.getAttribute("CURR_USER");
		boolean money = sysUser.isMoney();
		if (!money) {
			for (Iterator iterator = queryForList.iterator(); iterator
					.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				map.remove("orderTotal");
				map.remove("fuFuanCond");
				map.remove("fuFuanMoney");
				map.remove("payType");

			}
		}
		// System.out.println(queryForList);
		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}

	/**
	 * 根据SaleHeader.id查找对应SaleOneCust
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/findSaleOneCustsByPid", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray findSaleOneCustsByPid(String pid) {
		List<SaleOneCust> findSaleOneCustsByPid = saleOneCustDao
				.findSaleOneCustsByPid(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "saleHeader" };
		// System.out.println(JSONArray.fromObject(findSaleOneCustsByPid,
		// super.getJsonConfig(strings)));
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		return JSONArray.fromObject(findSaleOneCustsByPid,
				super.getJsonConfig("yyyy-MM-dd HH:mm:ss", strings));
	}

	/**
	 * 根据SaleItem 计算总价
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/calculationPrice", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message calculationPrice(SaleItem saleItem, String shouDaFang) {

		List<SaleItemPrice> saleItemPrices = new ArrayList<SaleItemPrice>();
		if (saleItem.getId() == null || "".equals(saleItem.getId())) {
			// saleItemPrices = saleManager.getSaleItemPrice(saleItem);
		} else {
			saleItemPrices = saleItemPriceDao.querySaleItemPrice(saleItem
					.getId());
		}

		Double totalPrice = saleManager.calculationPrice(saleItemPrices,
				saleItem.getAmount(), false, shouDaFang);
		JSONObject obj = new JSONObject();
		obj.put("totalPrice", totalPrice);
		Message msg = new Message(obj);
		return msg;
	}

	/**
	 * 根据saleItemList 计算总价
	 * 
	 * @param saleItemList
	 * @param shouDaFang
	 * @return
	 */
	@RequestMapping(value = "/calculationTotalPrice", method = {
			RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Message calculationPrice(@RequestBody SaleBean saleBean,
			String shouDaFang) {
		List<SaleItem> saleItemList = saleBean.getSaleItemList();
		JSONObject obj = new JSONObject();
		double total = 0;
		for (SaleItem saleItem : saleItemList) {
			if (!"QX".equals(saleItem.getStateAudit())) {
				List<SaleItemPrice> saleItemPrices = new ArrayList<SaleItemPrice>();
				if (saleItem.getId() == null || "".equals(saleItem.getId())) {
					// saleItemPrices = saleManager.getSaleItemPrice(saleItem);

				} else {
					saleItemPrices = saleItemPriceDao
							.querySaleItemPrice(saleItem.getId());
				}
				Double totalPrice = saleManager
						.calculationPrice(saleItemPrices, saleItem.getAmount(),
								false, shouDaFang);
				saleItem.setTotalPrice(totalPrice);
				total = NumberUtils.add(total, totalPrice.doubleValue());
			}
		}

		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "saleHeader",
				"saleItemPrices" };
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		JSONArray fromObject = JSONArray.fromObject(saleItemList,
				super.getJsonConfig("yyyy-MM-dd HH:mm:ss", strings));
		obj.put("totalPrice", total);
		obj.put("saleItemList", fromObject);
		Message msg = new Message(obj);
		return msg;
	}

	/**
	 * 根据shouDaFang查找对应CustItem
	 * 
	 */
	@RequestMapping(value = "/getCustItem", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message getCustItem(String saleHeadId) {
		SaleHeader saleHeader = saleHeaderDao.findOne(saleHeadId);
		CustItem custItem = saleManager.getCustItem(saleHeader.getShouDaFang(),
				saleHeader.getOrderDate());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("shengYu",
				custItem.getShengYu() == null ? 0 : custItem.getShengYu());
		jsonObject.put("zheKou",
				custItem.getZheKou() == null ? 0 : custItem.getZheKou());
		Message msg = new Message(jsonObject);
		return msg;
	}

	/**
	 * 根据id查找对应pr04Total
	 * 
	 */
	@RequestMapping(value = "/getPR04", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message getPR04(String id) {
		SaleItemPrice saleItemPrice = saleManager.getSaleItemPrice(id);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("PR04", saleItemPrice.getTotal() == null ? 0
				: saleItemPrice.getTotal());
		Message msg = new Message(jsonObject);
		return msg;
	}

	/**
	 * 根据id查找对应pr04Total
	 * 
	 */
	@RequestMapping(value = "/getPR03", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message getPR03(String saleItemId) {

		String sql = "select sip.subtotal, sh.shou_da_fang, sh.create_time"
				+ "  from Sale_Item_Price sip, sale_item si, sale_header sh"
				+ " where sip.sale_itemid = si.id" + "   and sh.id = si.pid"
				+ "   and sip.type = 'PR04'  and sip.sale_itemid = '"
				+ saleItemId + "'";

		// Double zhekou = jdbcTemplate.queryForObject(sql, Double.class);
		BigDecimal subtotal = new BigDecimal("0.0000");
		String shou_da_fang = null;
		Date create_time = null;
		String tempdate = null;
		List<Map<String, Object>> queryforlist = jdbcTemplate.queryForList(sql);

		if (queryforlist != null && queryforlist.size() > 0) {

			Map<String, Object> map = queryforlist.get(0);
			subtotal = (BigDecimal) map.get("SUBTOTAL");
			shou_da_fang = (String) map.get("SHOU_DA_FANG");

			create_time = (Date) map.get("CREATE_TIME");
			tempdate = DateTools.formatDate(create_time,
					DateTools.defaultFormat);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("subtotal", subtotal);
		jsonObject.put("shou_da_fang", shou_da_fang);
		jsonObject.put("create_time", tempdate);
		Message msg = new Message(jsonObject);
		return msg;
	}

	@RequestMapping(value = { "/listSaleItemForWin" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean listSaleItemForWin(int page, int limit) {
		String orderCode = this.getRequest().getParameter("orderCode");
		String serialNumber = this.getRequest().getParameter("serialNumber");
		String type = this.getRequest().getParameter("type");
		String itemDesc = this.getRequest().getParameter("itemDesc");
		String amount = this.getRequest().getParameter("amount");

		StringBuffer sb = new StringBuffer(
				"select t.* from sale_item_view_for_win t where nvl(t.state_audit,'B')<>'QX' ");
		// sql params
		List<Object> params = new ArrayList<Object>();
		if (!StringUtils.isEmpty(orderCode)) {
			sb.append(" and ORDER_CODE = ? ");
			params.add(orderCode);
		}
		if (!StringUtils.isEmpty(serialNumber)) {
			sb.append(" and SERIAL_NUMBER like ? ");
			params.add(StringHelper.like(String.valueOf(serialNumber)));
		}
		if (!StringUtils.isEmpty(type)) {
			sb.append(" and TYPE = ? ");
			params.add(type);
		}

		if (!StringUtils.isEmpty(itemDesc)) {
			sb.append(" and ITEM_DESC like ? ");
			params.add(StringHelper.like(String.valueOf(itemDesc)));
		}
		if (!StringUtils.isEmpty(amount)) {
			sb.append(" and AMOUNT = ? ");
			params.add(Integer.parseInt(amount));
		}
		// 获取总记录数
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(
				sb.toString(), params.toArray());

		// StringBuffer pageSQL = new StringBuffer("select * from (");
		// if (page - 1 == 0) {
		// pageSQL.append(sb + " ) where rownum <= ?");
		// params.add(limit);
		// } else {
		// pageSQL
		// .append("select row_.*, rownum rownum_ from ( " + sb
		// + ") row_ where rownum <= ?) where rownum_ > ?");
		// params.add(limit * page);
		// params.add((page - 1) * limit);
		// }

		// 总页数=总记录数/每页条数(系数加1)
		int totalPages = (totalElements.size() + limit - 1) / limit;

		// 多个时间字段转换
		Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
		// formatMap.put("orderDate",
		// new SimpleDateFormat(DateTools.defaultFormat));
		// formatMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd"));
		// formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));

		// System.out.println(pageSQL.toString());
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(sb
				.toString(), params.toArray(),
				new MapRowMapper(true, formatMap));

		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}

	@RequestMapping(value = { "/listForTc" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForListForTc(int page, int limit) {
		String name1 = this.getRequest().getParameter("name1");
		String tel = this.getRequest().getParameter("tel");
		String sex = this.getRequest().getParameter("sex");
		String age = this.getRequest().getParameter("age");

		String birthday = this.getRequest().getParameter("birthday");
		String shenFenHao = this.getRequest().getParameter("shenFenHao");
		String code = this.getRequest().getParameter("code");
		String name = this.getRequest().getParameter("name");

		StringBuffer sb = new StringBuffer(
				"select t.* from terminal_client_view t where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		if (!StringUtils.isEmpty(name1)) {
			sb.append(" and NAME1 like ? ");
			params.add(StringHelper.like(String.valueOf(name1)));
		}
		if (!StringUtils.isEmpty(tel)) {
			sb.append(" and TEL like ? ");
			params.add(StringHelper.like(String.valueOf(tel)));
		}
		if (!StringUtils.isEmpty(sex)) {
			sb.append(" and SEX = ? ");
			params.add(sex);
		}
		if (!StringUtils.isEmpty(age)) {
			sb.append(" and AGE = ? ");
			params.add(age);
		}
		if (!StringUtils.isEmpty(birthday)) {
			sb.append(" and BIRTHDAY = ? ");
			params.add(DateTools.strToDate(birthday, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(shenFenHao)) {
			sb.append(" and SHENFENHAO like ? ");
			params.add(StringHelper.like(String.valueOf(shenFenHao)));
		}
		if (!StringUtils.isEmpty(code)) {
			sb.append(" and CODE like ? ");
			params.add(StringHelper.like(String.valueOf(code)));
		}
		if (!StringUtils.isEmpty(name)) {
			sb.append(" and NAME like ? ");
			params.add(StringHelper.like(String.valueOf(name)));
		}

		// 获取总记录数
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(
				sb.toString(), params.toArray());

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
		Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
		formatMap
				.put("birthday", new SimpleDateFormat(DateTools.defaultFormat));
		// formatMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd"));
		// formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));

		// System.out.println(pageSQL.toString());
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(),
				new MapRowMapper(true, formatMap));

		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}

	@RequestMapping(value = "/findTcById", method = RequestMethod.GET)
	@ResponseBody
	public Message findTcById(String id) throws ParseException {
		Message msg = null;
		try {
			TerminalClient terminalClient = terminalClientDao.findOne(id);
			Map<String, Object> map = new HashMap<String, Object>();
			if (terminalClient != null) {
				map.put("id", terminalClient.getId());
				map.put("createTime",
						terminalClient.getCreateTime() == null ? "" : DateTools
								.formatDate(terminalClient.getCreateTime(),
										DateTools.fullFormat));
				map.put("updateTime",
						terminalClient.getUpdateTime() == null ? "" : DateTools
								.formatDate(terminalClient.getUpdateTime(),
										DateTools.fullFormat));
				map.put("createUser",
						terminalClient.getCreateUser() == null ? ""
								: terminalClient.getCreateUser());
				map.put("updateUser",
						terminalClient.getUpdateUser() == null ? ""
								: terminalClient.getUpdateUser());
				Field[] declaredFields = terminalClient.getClass()
						.getDeclaredFields();
				for (int i = 0; i < declaredFields.length; i++) {
					Field field = declaredFields[i];
					String name = field.getName();
					if (name == "saleHeader") {
						continue;
					} else {
						Object property = BeanUtils.getValue(terminalClient,
								name);
						if (property != null) {
							if (name.equals("birthday")) {
								map.put(name,
										terminalClient.getBirthday() == null ? ""
												: DateTools.formatDate(
														terminalClient
																.getBirthday(),
														DateTools.defaultFormat));
							} else {
								map.put(name, property);
							}
						}
					}
				}
			}
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "saleHeader" };
			// System.out.println(JSONObject.fromObject(map,
			// super.getJsonConfig(strings)));
			msg = new Message(JSONObject.fromObject(map,
					super.getJsonConfig(strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-GET-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 客服起草环节提交下一环节时的校验
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/validateCurrKFQC", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message validateCurrKFQC(String saleHeadId) {
		Message msg = null;
		SaleHeader saleHeader = saleHeaderDao.findOne(saleHeadId);
		String jiaoQiTianShu = saleHeader.getJiaoQiTianShu();
/*		if (ZStringUtils.isEmpty(jiaoQiTianShu)) {
			msg = new Message("S-V-500", "当前环节提交下一环节前：请先选择交期天数，并保存！");
		} else {
		}*/
		msg = new Message("OK");
		return msg;
	}
	
	/**
	 * 客服审核提交下一环节的校验
	 * @param saleHeadId
	 * @return
	 */
	@RequestMapping(value = "/findBgInfoByid", method = {
			RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Message findBgInfoByid(String saleHeadId) {
		Message msg = null;
		String flagStr=this.getRequest().getParameter("flag");
		Boolean flag=StringUtils.isEmpty(flagStr)?false:Boolean.parseBoolean(flagStr);
		SaleHeader saleHeader = saleHeaderDao.findOne(saleHeadId);
		String orderType = saleHeader.getOrderType();
		StringBuffer sb = new StringBuffer();
		if("OR4".equals(orderType)) {
			String pOrderCode = saleHeader.getpOrderCode();
			TerminalClient terminalClient = saleHeader.getTerminalClient();
			String tousu = terminalClient.getTousucishu();
			if(ZStringUtils.isEmpty(pOrderCode)) {
				sb.append("免费订单请选择参考订单，<br/>");
			}
			if(ZStringUtils.isEmpty(tousu)) {
				sb.append("免费订单填写投诉次数，<br/>");
			}
		}
		if (!flag && ZStringUtils.isNotEmpty(sb.toString())) {
			msg = new Message("false", "当前环节提交下一环节前：<br/>" + sb.toString()
					+ "并保存！");
		} else {
			msg = new Message("true");
		}
		return msg;
	}
	/**
	 * 订单审价提交下一环节时的校验
	 * 
	 * @param saleHeadId
	 * @return
	 */
	@RequestMapping(value = "/validateCheckPrice", method = {
			RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Message validateCheckPrice(String saleHeadId) {
		Message msg = null;
		String flagStr=this.getRequest().getParameter("flag");
		Boolean flag=StringUtils.isEmpty(flagStr)?false:Boolean.parseBoolean(flagStr);
		SaleHeader saleHeader = saleHeaderDao.findOne(saleHeadId);
		String payType = saleHeader.getPayType();
		String fuFuanCond = saleHeader.getFuFuanCond();
		// String huoDongType = saleHeader.getHuoDongType();
		StringBuffer sb = new StringBuffer();
		if (ZStringUtils.isEmpty(payType)) {
			sb.append("请选择支付方式，<br/>");
		}
		if (ZStringUtils.isEmpty(fuFuanCond)) {
			sb.append("请选择付款条件，<br/>");
		}
		// if (ZStringUtils.isEmpty(huoDongType)) {
		// sb.append("请选择活动类型，<br/>");
		// }
		String orderType = saleHeader.getOrderType();
		// 免费单
		if ("OR4".equals(orderType)) {
/*			String sapOrderCode = saleHeader.getSapOrderCode();
			if (ZStringUtils.isEmpty(sapOrderCode)) {
				sb.append("免费补货单请先传输SAP，<br/>");
			}*/
		}
		if (!flag && ZStringUtils.isNotEmpty(sb.toString())) {
			msg = new Message("false", "当前环节提交下一环节前：<br/>" + sb.toString()
					+ "并保存！");
		} else {
			msg = new Message("true");
		}
		return msg;
	}

	/**
	 * 财务确认提交结束环节前，判断传输SAP成功没有
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/validateTranSap", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message validateTranSap(String saleHeadId, String flowtype) {
		if ("0".equals(flowtype)) {
			return new Message("true");
		}
		SaleHeader saleHeader = saleHeaderDao.findOne(saleHeadId);
		if(saleHeader==null) {
			//订单不存在
			return new Message("SD-CK-500", "没有找到有效订单");
		}
		SysJobPoolManager jobPoolManager = SpringContextHolder.getBean("sysJobPoolManagerImpl");
		SysActCTMappingDao sysActCTMappingDao = SpringContextHolder.getBean("sysActCTMappingDao");
		String orderCode=saleHeader.getOrderCode();
		SysJobPool sysJobPool = sysJobPoolDao.findByZuonr(orderCode);
		if(sysJobPool==null) {
			sysJobPool = new SysJobPool(orderCode, "A", "B",
					"CREDIT_JOB","N");//初始化 任务池
			sysJobPool.setIsFreeze("0");//在任务池 代表此任务 并未结束
			sysJobPool.setBeginDate(new Date());
			SysActCTMapping mapping = sysActCTMappingDao.findOne(saleHeadId);
			if(mapping!=null) {
				sysJobPool.setProcInstId(mapping.getProcinstid());
			}
			List<SaleItem> saleItemList = saleItemDao.findByItems(saleHeadId);
			if(saleItemList.size()<=0) {
				sysJobPool.setSapStatus("Y");//如果SAP 编号 为null 没有一个 则代表 已经生成SAP号了 此时的任务状态应该是 在处理中 且为手工处理
				sysJobPool.setJobStatus("B");
				sysJobPool.setJobType("SALE_JOB");
			}
		}
		if("N".equals(sysJobPool.getSapStatus())) {
			//N 表示 未生成 SAP 号 未生成SAP号 需要生成 不可以进行释放订单
			//如果 为N 保存 任务
			commonManager.save(sysJobPool);
			return new Message("SD-CK-500", orderCode + "未生成SAP号");
		}
		if(!"SALE_JOB".equals(sysJobPool.getJobType())) {
			// 非 则表示 还在自动过账任务中 不可进行 手工释放订单
			return new Message("SD-CK-500", orderCode + "还在自动过账任务中");
		}
		sysJobPool.setSourceType("A");
		return jobPoolManager.checkCreditBYCust(sysJobPool);
	}

	/**
	 * 更新免费单号
	 * 
	 * @param saleId
	 * @param updateCode
	 * @return
	 */
	@RequestMapping(value = "/updateOrderCode", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public Message updateOrderCode(String saleId, String updateCode) {
		Message msg = null;
		SaleHeader saleHeader = saleHeaderDao.findOne(saleId);

		List<Map<String, Object>> queryForList = jdbcTemplate
				.queryForList("select t.ORDER_CODE from SALE_HEADER t where t.ORDER_CODE ='"
						+ updateCode + "'");
		if (queryForList.size() == 0) {
			saleHeader.setOrderCode(updateCode);
			Set<SaleItem> saleItemSet = saleHeader.getSaleItemSet();
			for (SaleItem saleItem : saleItemSet) {
				saleItem.setOrderCodePosex(updateCode
						+ ZStringUtils.ZeroPer(saleItem.getPosex(), 4));
				saleItem.setSaleHeader(saleHeader);
			}
			saleHeader.setSaleItemSet(saleItemSet);
			saleManager.save(saleHeader);
			msg = new Message("true");
		} else {
			msg = new Message("SD-USC-500", "系统已存在这个单号!");
		}

		return msg;
	}

	@RequestMapping(value = "/findSaleById", method = RequestMethod.GET)
	@ResponseBody
	public Message findSaleById(String id) throws ParseException {
		Message msg = null;
		try {
			String sql = "SELECT * from SALE_HEADER_TA_V where id='" + id + "'";
			List<Map<String, Object>> queryForList = jdbcTemplate
					.queryForList(sql);
			if (queryForList.size() > 0) {
				return msg = new Message(queryForList.get(0));
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-GET-500", "数据加载失败!");
		}
		return msg;
	}
	
	/**
	 * 订单类型权限
	 * 
	 * @param shId
	 * @return
	 */
	@RequestMapping(value = { "/getOrderByCust/{shId}/{sourceType}" }, method = RequestMethod.GET)
	@ResponseBody
	public Message getOrderByCust(@PathVariable String shId,@PathVariable String sourceType) {
		String trieKeyVal=("buDan".equals(sourceType))?"ORDER_TYPE_BD":"ORDER_TYPE";//Chaly
		String sql = "select * from  Sys_Data_Dict_View v where v.TRIE_KEY_VAL='"+trieKeyVal+"' AND V.STAT='1'";
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		List<Map<String, Object>> dicts = new ArrayList<Map<String, Object>>();
		String orderType = "";
		if (!"null".equals(shId)) {
			String sqls = "select h.order_type from sale_header h where h.id='"
					+ shId + "'";
			List<Map<String, Object>> ss = jdbcTemplate.queryForList(sqls);
			orderType = ss.get(0).get("ORDER_TYPE").toString();
		}
		CustHeader cust = null;
		List<CustHeader> custHeaders = custHeaderDao.findByCode((String)super.getRequest().getSession().getAttribute(Constants.CURR_USER_KUNNR));
		String custType = "";
		if(custHeaders.size()>0) {
			cust = custHeaders.get(0);
			custType = cust.getKtokd();
		}

		for (Map<String, Object> queryMap : queryForList) {
			Map<String, Object> map = new HashMap<String, Object>();

			if (queryMap.get("KEY_VAL").toString().equals(orderType)) {
				map.put("id", queryMap.get("KEY_VAL"));
				map.put("text", queryMap.get("DESC_ZH_CN"));// 可以支持国际化
				dicts.add(map);
				continue;
			}
			if(sourceType!=null&&"buDan".equals(sourceType)) {
				map.put("id", queryMap.get("KEY_VAL"));
				map.put("text", queryMap.get("DESC_ZH_CN"));// 可以支持国际化
				dicts.add(map);
				continue;
			}
			map.put("id", queryMap.get("KEY_VAL"));
			map.put("text", queryMap.get("DESC_ZH_CN"));// 可以支持国际化
			if ("OR1".equals(queryMap.get("KEY_VAL"))
					|| "OR2".equals(queryMap.get("KEY_VAL"))
					) {
				if ("Z110".equals(custType) || "Z120".equals(custType)
						|| "Z210".equals(custType)|| "Z800".equals(custType)) {
					dicts.add(map);
				}

			} /*else if ("OR4".equals(queryMap.get("KEY_VAL"))) {

				if ("Z110".equals(custType) || "Z120".equals(custType)
						|| "Z210".equals(custType) || "Z310".equals(custType)||"Z900".equals(custType)) {
					dicts.add(map);
				}

			}*/
			if ("OR7".equals(queryMap.get("KEY_VAL"))) {
				if ("Z310".equals(custType)) {
					dicts.add(map);
				}

			}
			if ("OR8".equals(queryMap.get("KEY_VAL"))
					|| "OR9".equals(queryMap.get("KEY_VAL"))|| "OR1".equals(queryMap.get("KEY_VAL"))|| "OR2".equals(queryMap.get("KEY_VAL"))) {
				if ("Z900".equals(custType)) {
					dicts.add(map);
				}
			}
//			if ("admin".equals(loginUser.getId())) {
//				dicts.add(map);
//			}
			if(!super.getLoginUserId().contains("lj")){
				dicts.add(map);
			}
		}
		Message msg = new Message(dicts);
		msg.setObj(dicts);
		return msg;
	}

	/**
	 * 非标颜色 （权限）
	 */
	/*@RequestMapping(value = { "/getMMColor" }, method = RequestMethod.GET)
	@ResponseBody
	public Message getMMColor(HttpServletRequest request) {
		String pId = request.getParameter("pId");
		List<Map<String, Object>> dicts = new ArrayList<Map<String, Object>>();
		if (!"null".equals(pId) && pId != null) {
			String sql = "select * from  Sys_Data_Dict_View v where v.TRIE_KEY_VAL='"
					+ pId + "'";

			List<Map<String, Object>> queryForList = jdbcTemplate
					.queryForList(sql);

			Message msg = this.getOrderByCust("null");
			List<Map<String, Object>> otdicts = (List<Map<String, Object>>) msg
					.getObj();
			for (Map<String, Object> queryMap : queryForList) {
				String key = queryMap.get("KEY_VAL").toString();
				String name = queryMap.get("DESC_ZH_CN").toString();
				String ordTyp = String.valueOf(queryMap.get("ORDER_TYPES"));

				if (!"".equals(ordTyp) && !"null".equals(ordTyp)
						&& ordTyp != null) {
					String[] ordTyps = ordTyp.split(",");
					for (String ot : ordTyps) {
						for (Map<String, Object> map : otdicts) {
							if (ot.equals(map.get("KEY_VAL"))) {
								Map<String, Object> mapc = new HashMap<String, Object>();
								mapc.put("id", key);
								mapc.put("text", name);// 可以支持国际化
								dicts.add(mapc);
							}
						}
					}
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", key);
					map.put("text", name);// 可以支持国际化
					dicts.add(map);
				}

			}
		}
		return new Message(dicts);
	}*/

	/**
	 * 设计师下拉数据
	 * 
	 * @param shId
	 * @return
	 */
	@RequestMapping(value = { "/getDesignerTel/{shId}" }, method = RequestMethod.GET)
	@ResponseBody
	public Message getDesignerTel(@PathVariable String shId) {
		String sql = "";
		if ("null".equals(shId)) {
			String custName = super.getLoginUserKunnr();
			sql = "select c.namev,c.telf1 from CUST_CONTACTS c where c.status='1' and c.abtnr='0099' and c.kunnr='"
					+ custName + "'";
		} else {
			sql = "select c.namev,c.telf1 from CUST_CONTACTS c where c.status='1' and c.abtnr='0099' and c.kunnr=(select h.shou_da_fang from sale_header h where h.id='"
					+ shId + "')";
		}
		List<Map<String, Object>> dicts = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> queryMap : queryForList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", queryMap.get("TELF1"));
			map.put("text", queryMap.get("NAMEV"));// 可以支持国际化
			dicts.add(map);
		}
		return new Message(dicts);
	}

	/**
	 * 根据订单的id查询订单行项目的定价条件
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findSaleItemPrByPid", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean findSaleItemPrByPid(String id) {
		List<Map<String, Object>> saleItemPrList = new ArrayList<Map<String, Object>>();
		String sale_item_pri_sql = "select * from sale_prmod_item_AMOUNT_V  where  pid='"
				+ id
				+ "' and nvl(state_audit,'C')!='QX' order by to_number(posex)";
		List<Map<String, Object>> itemPriList = jdbcTemplate
				.queryForList(sale_item_pri_sql);
		for (Map<String, Object> pri : itemPriList) {
			Map<String, Object> saleItemPr = new HashMap<String, Object>();
			saleItemPr.put("id", pri.get("id"));
			saleItemPr.put("posnr", pri.get("posex"));// 行项目
			// saleItemPr.put("kwmeng", pri.get("kwmeng"));
			BigDecimal bd =(BigDecimal)pri.get("total_price");
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			saleItemPr.put("pr00", bd);// 付款金额(计算后的金额)
			saleItemPr.put("pr01", pri.get("pr01"));// 商品原价(含税)
			saleItemPr.put("pr02", pri.get("pr02"));// 赠送（活动）
			saleItemPr.put("pr03", pri.get("pr03"));// 产品折扣
			saleItemPr.put("pr04", pri.get("pr04"));// 活动折扣
			saleItemPr.put("pr05", pri.get("pr05"));// 产品免费（统计用）
			saleItemPr.put("zr01", pri.get("zr01"));// 运输费(含税)
			saleItemPr.put("zr02", pri.get("zr02"));// 返修费(含税)
			saleItemPr.put("zr03", pri.get("zr03"));// 安装服务费(含税)
			saleItemPr.put("zr04", pri.get("zr04"));// 设计费(含税)
			saleItemPr.put("zr05", pri.get("zr05"));// 订单变更管理费(含税)
			saleItemPr.put("zr06", pri.get("zr06"));// 客服支持
			saleItemPrList.add(saleItemPr);
		}
		return new JdbcExtGridBean(1, saleItemPrList.size(),
				saleItemPrList.size(), saleItemPrList);
		// return new Message(saleItemPrList);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));

	}

	/**
	 * 财务手动过账
	 * 
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = { "/finance/handing" }, method = RequestMethod.GET)
	public ModelAndView financeBatchIndex(ModelAndView modelAndView) {
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "FinanceHandingApp");
		modelAndView.getModelMap().put("moduleTitle", "财务手动过账");
		return modelAndView;
	}

	/**
	 * 查询财务未过账的
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/financeHanding/datalist")
	@ResponseBody
	public JdbcExtGridBean getFinanceList(HttpServletRequest request) {
		String userId = super.getLoginUserId();
		String sql = "Select * from (select P.*,"
				+ "(SELECT H.ID FROM SALE_HEADER H WHERE H.ORDER_CODE=P.ZUONR AND ROWNUM=1) SH_ID,"
				+ "(SELECT v.DESC_ZH_CN FROM Sys_Data_Dict_View v where v.TRIE_KEY_VAL='JOB_POOL_STA' and v.KEY_VAL=p.job_status) job_status_name,"
				+ "(SELECT v.DESC_ZH_CN FROM Sys_Data_Dict_View v where v.TRIE_KEY_VAL='CDT_POOL_TYPE' and v.KEY_VAL=p.source_Type) source_Type_name"
				+ " from SYS_JOB_POOL P WHERE P.JOB_TYPE='CREDIT_JOB' and nvl(p.job_status,'A')<>'C') where 1=1";
		return super.simpleQuery(sql, false, null, request);
	}

	@RequestMapping(value = { "/batch/validateTranSap/{saleHeadIds}" }, method = RequestMethod.GET)
	@ResponseBody
	public Message batchValidateTranSap(@PathVariable String saleHeadIds) {
		Message msg = null;
		String status = "D";
		String mStr = "";
		boolean flag = true;
		if (!"null".equals(saleHeadIds)) {
			String[] ids = saleHeadIds.split(":");
			for (String id : ids) {
				msg = this.validateTranSap(id, "");
				mStr = msg.getErrorMsg();
				if (msg.getSuccess()) {
					status = "C";
					mStr = msg.getMsg();
				} else {
					flag = false;
				}
				String sql = "UPDATE SYS_JOB_POOL P SET P.JOB_STATUS='"
						+ status
						+ "',P.End_Date=SYSDATE,p.msg='"
						+ mStr
						+ "' WHERE  p.zuonr in (select h.order_code from sale_header h where h.id='"
						+ id + "')";
				jdbcTemplate.update(sql);
			}
		}
		if (flag) {
			return new Message("释放成功");
		} else {
			return new Message("SAVE-batchValidateTranSap-500", "有单据释放失败");
		}

	}

	@RequestMapping(value = { "/getSaleOutputLogList" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean getSaleOutputLogList(int page, int limit) {
		String name = this.getRequest().getParameter("name");
		String userKunnr = this.getRequest().getParameter("userKunnr");
		String sapCode = this.getRequest().getParameter("sapCode");
		String OrderCode = this.getRequest().getParameter("orderCode");
		String Posex = this.getRequest().getParameter("posex");
		String StartDate = this.getRequest().getParameter("startDate");
		String EndDate = this.getRequest().getParameter("endDate");
		String kunnr = this.getRequest().getParameter("kunnr");
		String kunnrName = this.getRequest().getParameter("kunnrName");
		String custName = this.getRequest().getParameter("custName");
		String custPhone = this.getRequest().getParameter("custPhone");
		String regio=this.getRequest().getParameter("regio");
		String bzirk=this.getRequest().getParameter("bzirk");
		String[] v;

		// 查询语句
		StringBuilder sb = new StringBuilder("SELECT ROWNUM AS RN,ZS.* FROM Z_SALE_SHIPENT_VIEW ZS where 1=1 ");
		if (!"admin".equals(this.getLoginUserId()) && userKunnr != null
				&& !StringUtils.isEmpty(userKunnr)) {
			sb.append(" and kunnr= '" + userKunnr + "'");
		}
		if (name != null && !StringUtils.isEmpty(name)) {
			sb.append(" and name like '%" + name + "%'");
		}
		if (sapCode != null && !StringUtils.isEmpty(sapCode)) {
			sb.append(" and sap_code in ('1'");
			v = sapCode.split(",");
			for (String s : v) {
				sb.append(",'" + s + "'");
			}
			sb.append(" )");
		}
		if (OrderCode != null && !StringUtils.isEmpty(OrderCode)) {
			sb.append(" and (Order_code like (");
			v = OrderCode.split(",");
			int i = 0;
			for (String s : v) {
				if (i == 0) {
					sb.append("'%" + s + "%')");
				} else {
					sb.append(" or Order_code like '%" + s + "%'");
				}

				i = i + 1;
			}
			sb.append(" )");
		}
		if (Posex != null && !StringUtils.isEmpty(Posex)) {
			sb.append(" and Posex ='" + Posex + "'");
		}
		if (StartDate != null && !StringUtils.isEmpty(StartDate)) {
			sb.append(" and output_time>= to_date('" + StartDate
					+ "','yyyy-mm-dd')");
		}
		if (EndDate != null && !StringUtils.isEmpty(EndDate)) {
			sb.append(" and output_time<= to_date('" + EndDate
					+ "','yyyy-mm-dd')");
		}
		if (kunnr != null && !StringUtils.isEmpty(kunnr)) {
			sb.append(" and kunnr like '%" + kunnr + "%'");
		}
		if (kunnrName != null && !StringUtils.isEmpty(kunnrName)) {
			sb.append(" and kunnr_Name like '%" + kunnrName + "%'");
		}
		if (custName != null && !StringUtils.isEmpty(custName)) {
			sb.append(" and cust_name like '%" + custName + "%'");
		}
		if (custPhone != null && !StringUtils.isEmpty(custPhone)) {
			sb.append(" and cust_phone like '%" + custPhone + "%'");
		}
		if (regio != null && !StringUtils.isEmpty(regio)) {
			sb.append(" and regio='" + regio + "'");
		}
		if (bzirk != null && !StringUtils.isEmpty(bzirk)) {
			sb.append(" and bzirk='" + bzirk + "'");
		}
		BigDecimal totalElement = jdbcTemplate.queryForObject(
				"select count(1) from (" + sb.toString() + ")",
				BigDecimal.class);
		int totalPage = (totalElement.intValue() / limit);
		totalPage = (totalPage <= 0 ? 1 : totalPage);

		// 获取数据
		int startIndex = (page - 1) * limit + 1;
		int endIndex = page * limit;
		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
		formats.put("createTime", new SimpleDateFormat(DateTools.defaultFormat));
		formats.put("updateTime", new SimpleDateFormat(DateTools.defaultFormat));
		formats.put("outputTime", new SimpleDateFormat(DateTools.defaultFormat));
		String sql = "select * from (" + sb.toString() + ") where rn between "
				+ startIndex + " and " + endIndex + " ";
		List<Map<String, Object>> queryForList = jdbcTemplate.query(sql,
				new MapRowMapper(true, formats));
		return new JdbcExtGridBean(totalPage, totalElement.intValue(), limit,
				queryForList);
	}

	/**
	 * 财务手动过账
	 * 
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = { "/SaleOutput" }, method = RequestMethod.GET)
	public ModelAndView SaleOutput(ModelAndView modelAndView) {
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "SaleOutputApp");
		modelAndView.getModelMap().put("moduleTitle", "出库记录");
		return modelAndView;
	}

	@RequestMapping(value = { "/SaleItem/ImosFail/{resource}" }, method = RequestMethod.POST)
	@ResponseBody
	public Message findItemByOderCodePosex(@PathVariable String resource) {
		Message msg = null;
		try {
			String orderId = jdbcTemplate.queryForObject(
					"select orderid from mes_imos_fail where id='" + resource
							+ "'", String.class);
			String sql = "select mh.id ,mh.imos_path, ?  as order_code_posex from material_head mh where mh.id =(select material_head_id from sale_item where order_code_posex= ? )";
			List<Map<String, Object>> queryList = jdbcTemplate.queryForList(
					sql, new Object[] { orderId, orderId });
			if (queryList.size() > 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("materialHead.id", queryList.get(0).get("ID"));
				map.put("imos_path", queryList.get(0).get("IMOS_PATH"));
				map.put("order_code_posex",
						queryList.get(0).get("ORDER_CODE_POSEX"));
				msg = new Message(map);
			}
		} catch (Exception e) {
			msg = new Message("DD-V-500", "获取数据失败");
		}
		return msg;
	}

	@RequestMapping(value = { "/SaleItem/ImosFail/save" }, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> SaleItemImosFailSave(
			@Valid MaterialFile materialFile, BindingResult result,
			String order_code_posex, String imos_path) {
		MaterialController mc = SpringContextHolder
				.getBean("materialController");
		ResponseEntity<String> response = null;
		JSONObject obj = new JSONObject();
		Connection conn=null;
		PreparedStatement statement=null;
		PreparedStatement statement2=null;
		Transaction transaction=null;
		Jedis jedis=null;
		try {
			//初始化
			conn=jdbcTemplate.getDataSource().getConnection();
			statement=conn.prepareStatement(INSERT_XML_LIST);
			statement2=conn.prepareStatement("select 1 from dual");
			jedis=jedisPool.getResource();
			//开启redis事务
			transaction=jedis.multi();
			
			jdbcTemplate.update(" update material_head mh set mh.imos_path='"
					+ imos_path + "' where mh.id ='"
					+ materialFile.getMaterialHead().getId() + "'");
			if (materialFile.getFile().getSize() > 0) {
				response = mc.fileupload(materialFile, result,null);
				Map<String, Object> map = new HashMap<String, Object>();
				obj = JSONObject.fromObject(response.getBody());
				if (response.getStatusCode() != HttpStatus.OK) {
					obj.put("success", false);
					obj.put("errorMsg", obj.get("msg"));
					obj.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR);
					return response = new ResponseEntity<String>(
							obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			List<Map<String, Object>> queryForList = jdbcTemplate
					.queryForList(" select t.id,t.upload_file_name,t.upload_file_path from material_file t where t.file_type='XML' and t.status is null and t.pid='"
							+ materialFile.getMaterialHead().getId()
							+ "' order by create_time desc");
			if (queryForList == null || queryForList.size() == 0) {
				obj.put("success", false);
				obj.put("errorMsg", "读取文件失败");
				obj.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR);
				response = new ResponseEntity<String>(obj.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				// sb.append("");
				String orderId = order_code_posex;

				Map<String, Object> fileObj = queryForList.get(0);
				// MaterialFile obj =
				// materialManager.getOne(saleItem.getMaterialHeadId(),
				// MaterialFile.class);
				String uploadFilePath = fileObj.get("UPLOAD_FILE_PATH")
						.toString();
				String uploadFileName = fileObj.get("UPLOAD_FILE_NAME")
						.toString();
				String xmlPath = uploadFilePath + MyFileUtil.FILE_DIR
						+ uploadFileName;

				// 获取IMOS路径
				String imosPath = imos_path;
				SysDataDict sysDataDict = sysDataDictDao.findByKeyVal(imosPath);
				// SysDataDict sysDataDict=
				// dataDictDaoImpl.getSysDataDict("IMOS_PATH",
				// imosPath);

				// 推送IMOS服务器炸单
				RocoImos rocoImos = new RocoImos();
				if (!rocoImos.sendImosFile(statement,transaction,orderId,
						uploadFileName.split(".xml")[0], xmlPath,
						sysDataDict.getKeyVal())) {
					obj.put("success", false);
					obj.put("errorMsg", "推送炸单失败！");
					obj.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR);
					response = new ResponseEntity<String>(obj.toString(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				} else {
					// 插入一条记录，记录单子丢进哪个炸单服务器
					if (jdbcTemplate.queryForList(
							"select * from imos_load_balance ilb where ilb.order_code='"
									+ orderId + "'").size() == 0) {
						statement2.executeUpdate("inset into imos_load_balance(create_time,order_code,imos_path,status) values(sysdate,'"
										+ orderId + "','" + imosPath + "',0)");
					} else {
						statement2.executeUpdate("update imos_load_balance ilb set ilb.status=0 where ilb.order_code='"
										+ orderId + "'");
					}

					statement2.executeUpdate("update mes_imos_fail set orderstatus=2 where orderid='"
									+ orderId + "'");
					obj.put("success", true);
					response = new ResponseEntity<String>(obj.toString(),
							HttpStatus.OK);
					statement2.executeUpdate("delete imos_idbext  where  orderid='"
							+ orderId + "'");
					statement2.executeUpdate("delete imos_idbwg  where  orderid='"
							+ orderId + "'");
					
					//所有成功才会执行这条命令
					transaction.exec();
					statement.executeBatch();
					statement2.executeBatch();
					conn.commit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//关闭所有会话和事务
			try{
				//数据连接关闭
				if(conn!=null){
					conn.close();
				}
				//关闭事务
				if(statement!=null){
					statement.close();
				}
				//将redis连接返回到jedis连接池中
				if(jedis!=null){
					jedisPool.returnResource(jedis);
				}
				//关闭redis事务
				if(transaction!=null){
					transaction.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return response;

	}

	/**
	 * 检测内部订单号是否存在
	 * 
	 * @param zzaufnr
	 * @return
	 */
	@RequestMapping(value = { "/checkZzaufnr" }, method = RequestMethod.POST)
	@ResponseBody
	public Message checkZzaufnr(String zzaufnr) {
		Message msg = null;
		try {
			JCoDestination connect = SAPConnect.getConnect();
			JCoFunction function = connect.getRepository().getFunction(
					"ZRFC_SD_NB01");
			JCoParameterList ipList = function.getImportParameterList();
			ipList.setValue("S_AUFNR", zzaufnr);
			function.execute(connect);
			JCoParameterList outList = function.getExportParameterList();
			Object result = outList.getValue("S_ZAUFNR");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			if (result != null && zzaufnr.equals((String) result)) {
				resultMap.put("status", true);
				resultMap.put("resultDesc", "存在");
			} else {
				resultMap.put("status", false);
				resultMap.put("resultDesc", "不存在");
			}
			msg = new Message(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("result", "获取数据失败!");
		}
		return msg;
	}

	@RequestMapping(value = { "/getCustHeaderBySaleId" }, method = RequestMethod.POST)
	@ResponseBody
	public Message getCustHeaderBySaleId(String uuid) {
		Message msg = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			SaleHeader saleHeader = saleHeaderDao.getOne(uuid);
			CustHeader custHeader = custHeaderDao.findByCode(
					saleHeader.getShouDaFang()).get(0);
			String sql = "select sum(decode(sh.fu_fuan_cond,'2',sh.order_total,sh.fu_fuan_money)) as total_money from sale_header sh where sh.id in ( select acm.id from act_ct_mapping acm where acm.procinstid in ( select proc_inst_id_ from act_ru_task where name_ in('财务确认') ) ) and shou_da_fang= ? ";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,
					new Object[] { saleHeader.getShouDaFang() });
			if (list != null && list.size() > 0) {
				map.put("unProduction", list.get(0).get("TOTAL_MONEY"));
			}
			JCoDestination connect = SAPConnect.getConnect();
			JCoFunction function2 = connect.getRepository().getFunction(
					"ZRFC_SD_XD01");
			function2.getImportParameterList().setValue("P_KKBER", "3000");
			JCoTable sKunnrTable = function2.getTableParameterList().getTable(
					"S_KUNNR");
			sKunnrTable.appendRow();
			sKunnrTable.setValue("SIGN", "I");
			sKunnrTable.setValue("OPTION", "EQ");
			sKunnrTable.setValue("CUSTOMER_VENDOR_LOW", custHeader.getKunnr());
			function2.execute(connect);
			JCoTable table4 = function2.getTableParameterList().getTable(
					"IT_TAB1");
			if (table4.getNumRows() > 0) {
				table4.firstRow();
				for (int i = 0; i < table4.getNumRows(); i++, table4.nextRow()) {
					if (i == 0) {
						Object value = table4.getValue("OBLIG_S");// 剩余信贷额度
						if (ZStringUtils.isNotEmpty(value)) {
							custHeader.setXinDai(value.toString());
							map.put("credit", value.toString());
						}
					}
				}
			}
			msg = new Message(map);
		} catch (Exception e) {
			msg = new Message("result", "获取数据失败!");
			msg.setSuccess(false);
			e.printStackTrace();
		}
		return msg;
	}
	/**
	 * 行项目重新排列
	 * 
	 * @param uuid
	 */
	public void rearrangePosex(String uuid) {
		if(true){
			return ;
		}
		// step1.行项目按行号排序 asc
		String sql = "select * from sale_item where pid=? order by to_number(posex) asc";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,
				new Object[] { uuid });
		int currentPosex = 10;
		// 将 原订单行项目编号 和目标行项目编号 以 key -value的方式存入map中
		Map<String, String> map = new HashMap<String, String>();
		Map<String,Object> imosMap=new HashMap<String,Object>();
		List<Integer> sortList=new ArrayList<Integer>();
		String orderCode="";
		if (list != null && list.size() > 0) {
			orderCode=((String)list.get(0).get("ORDER_CODE_POSEX")).substring(0,13);
			for (int index = 0; index < list.size(); index++) {
				Map<String, Object> resultMap = list.get(index);
				String orderCodePosex = (String) resultMap
						.get("ORDER_CODE_POSEX");
				String posex=(String) resultMap.get("POSEX");
				String id=(String)resultMap.get("ID");
				map.put(id, orderCodePosex.substring(0, 13)
						+ String.format("%04d", currentPosex));
				imosMap.put(orderCodePosex, orderCodePosex.substring(0, 13)
						+ String.format("%04d", currentPosex));
				sortList.add(Integer.parseInt(posex));
				currentPosex += 10;
			}
		}
		// 批量修改
		// 修改saleItem 和imos表的数据
		sql = "update sale_item set order_code_posex=? ,posex=? where id=? ";
		String sql2 = "update imos_idbext set orderid=? where orderid=?";
		List<Object[]> parms = new ArrayList<Object[]>();
		for (String key : map.keySet()) {
			parms.add(new Object[] { map.get(key),Integer.parseInt(map.get(key).substring(13)), key });
		}
		jdbcTemplate.batchUpdate(sql, parms);
		for(int index=0;index<sortList.size();index++){
			String orderCodePosex=orderCode+String.format("%04d", sortList.get(index));
			String sql3="update material_file set upload_file_name_old='"+imosMap.get(orderCodePosex)+"'||substr(upload_file_name,instr(upload_file_name,'.')) where file_type='XML' and  pid=(select si.material_head_id from sale_item si where si.order_code_posex='"+orderCodePosex+"')";
			jdbcTemplate.update(sql2,new Object[]{imosMap.get(orderCodePosex),orderCodePosex});
			jdbcTemplate.update(sql3);
		}
	}
	/**
	 * ROCO 三期优化 数据 切换 
	 * @param file 前端传递 文件 使用此文件 导出 数据
	 * @return
	 */
	@RequestMapping(value="/changeData",method=RequestMethod.GET)
	@ResponseBody
	public Message changeData() {
		Message msg = null;
		//List<Map<String, Object>> taskProInfoOrderData = jdbcTemplate.queryForList("SELECT COUNT(1),AR.NAME_,SH.ORDER_CODE,AR.PROC_INST_ID_,SH.ORDER_TYPE,AR.ID_ FROM SALE_HEADER SH LEFT JOIN ACT_CT_MAPPING AC ON SH.ID = AC.ID LEFT JOIN ACT_RU_TASK AR ON AC.PROCINSTID = AR.PROC_INST_ID_ WHERE AR.PROC_INST_ID_ IS NOT NULL GROUP BY AR.NAME_,SH.ORDER_CODE,AR.PROC_INST_ID_,SH.ORDER_TYPE,AR.ID_ HAVING DECODE(SH.ORDER_TYPE,'OR3',1,'OR4',1,0)=0 AND DECODE(AR.NAME_,'起草',1，'订单审绘',1,0)=0");
		String userId="admin";
		String target="endevent1";
		//Map<String,String> errorMessage=new HashMap<String,String>();
		//Map<String,String> succMessage=new HashMap<String,String>();
		String[] orderCodes=new String[]{"R13070003190001"};
		for (String orderCode : orderCodes) {
			List<SaleHeader> saleHeaderList = saleHeaderDao.findByCode(orderCode);
			SaleHeader saleHeader = saleHeaderList.get(0);
			List<Map<String, Object>> info = jdbcTemplate.queryForList("SELECT AR.ID_ FROM ACT_CT_MAPPING AC LEFT JOIN ACT_RU_TASK AR ON AC.PROCINSTID=AR.PROC_INST_ID_ WHERE AC.ID=?",saleHeader.getId());
			if(info.size()<=0) {
				continue;
			}
			String taskId = ZStringUtils.resolverStr(info.get(0).get("ID_"));
			Task task = taskService.createTaskQuery().taskId(taskId)
					.singleResult();
			if(task==null) {
				continue;
			}
			taskService.addComment(task.getId(), null, userId);
			TaskServiceImpl taskServiceImpl=(TaskServiceImpl)taskService;
			taskServiceImpl.getCommandExecutor().execute(new JumpTaskCmd(task.getExecutionId(), target));
		}
		/*for (Map<String, Object> map : taskProInfoOrderData) {
			String taskId = ZStringUtils.resolverStr(map.get("ID_"));
			String orderCode = ZStringUtils.resolverStr(map.get("ORDER_CODE"));
			try {
				List<SaleHeader> saleHeaderList = saleHeaderDao.findByCode(orderCode);
				SaleHeader saleHeader = saleHeaderList.get(0);
				//原订单 的SAP 号 是存于 行项里面的 三期优化 将SAP号变更至 行号中
				if(saleHeader.getSapOrderCode()!=null&&saleHeader.getSapOrderCode().length()>0) {
					saleHeader.setSapOrderCode("");
					saleHeader.setSapCreateDate(null);
				}
				List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(saleHeader.getId());
				for (SaleLogistics saleLogistics : saleLogisticsList) {
					saleLogistics.setSapCode("");
				}
				Map<String,String> saleForMap=new HashMap<String, String>();
				Set<SaleItem> saleItemSet = saleHeader.getSaleItemSet();
				for (SaleItem saleItem : saleItemSet) {
					String sapCode = saleItem.getSapCode();
					if(sapCode!=null&&!"".equals(sapCode)) {
						JCoDestination connect = SAPConnect
								.getConnect();
						JCoFunction function = connect
								.getRepository().getFunction(
										"ZRFC_SD_CHANGE_SO_PO");
						JCoParameterList importParameterList = function
								.getImportParameterList();
						importParameterList.setValue("I_VBELN", sapCode);
						function.execute(connect);
						saleItem.setSapCode("");
						saleItem.setSapCodePosex("");
					}
					saleItem.setStateAudit("C");//将状态 改为 出错返回
					if (!"QX".equals(saleItem.getStateAudit())) {
						 MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					     if(materialHead==null) {
					    	 throw new TypeCastException("数据异常");
					     }
					     if("0".equals(materialHead.getIsStandard())) {
					    	 if(materialHead.getSaleFor()!=null&&!"".equals(materialHead.getSaleFor())) {
					    		 SysTrieTree saleForSysTrieTree = sysTrieTreeDao.findByKeyVal("SALE_FOR");
					    		 if(saleForSysTrieTree!=null) {
					    			 List<SysDataDict> saleForSysDataDictList = sysDataDictDao.findByTrieTreeId(saleForSysTrieTree.getId());
					    			 for (SysDataDict sysDataDict : saleForSysDataDictList) {
					    				 if(String.valueOf(materialHead.getSaleFor()).equals(sysDataDict.getKeyVal())) {
					    					 materialHead.setSpart(sysDataDict.getTypeKey());
					    					 break;
					    				 }
					    			 }
					    		 }
					    	 }
					    	 materialHeadDao.save(materialHead);
					     }
					     saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
					}
				}
				saleHeader.setSaleItemSet(saleItemSet);
				SysJobPool sysJobPool = sysJobPoolDao.findByZuonr(orderCode);
				if(sysJobPool!=null) {
					sysJobPoolDao.delete(sysJobPool);
				}
				if(taskId==null) {
					errorMessage.put(orderCode, "TASKID_NULL");
					continue;
				}
				Task task = taskService.createTaskQuery().taskId(taskId)
						.singleResult();
				if(task==null) {
					errorMessage.put(orderCode, "TASK_NULL");
					continue;
				}
				Set<SaleLogistics> saleLogisticsSet=new HashSet<SaleLogistics>();
				for (Entry<String, String> entry : saleForMap.entrySet()) {
					String saleHeaderId = saleHeader.getId();
					saleHeaderId = (saleHeaderId==null||saleHeaderId=="")?"":saleHeaderId;
					String saleFor=entry.getKey();
					String spart=entry.getValue();
					SaleLogistics saleLogistics = saleLogisticsDao.findBySaleHeaderIdAndSaleFor(saleHeaderId, saleFor);
					if(saleLogistics == null) {
						saleLogistics = new SaleLogistics();
					}
					saleLogistics.setSaleFor(saleFor);
					CustLogistics custLogistics = custLogisticsDao.findByKunnrAndSpartAndVkorg(saleHeader.getShouDaFang(), spart,"3110");
					saleLogistics.setKunnrS(custLogistics.getKunnrS());
					saleLogistics.setSaleHeader(saleHeader);
					saleLogisticsList.add(saleLogistics);
				}
				saleHeader.setSaleLogisticsSet(saleLogisticsSet);
				commonManager.save(saleHeader);
				commonManager.save(saleLogisticsList);
				taskService.addComment(task.getId(), null, userId);
				TaskServiceImpl taskServiceImpl=(TaskServiceImpl)taskService;
				taskServiceImpl.getCommandExecutor().execute(new JumpTaskCmd(task.getExecutionId(), target));
				succMessage.put(orderCode, "SUCCESS");
			}catch(Exception e) {
				errorMessage.put(orderCode, "Exception-Task-error");
				continue;
			}
		}*/
		msg = new  Message("change-data-success","切换数据成功");
		return msg;
	}
	
	
}
