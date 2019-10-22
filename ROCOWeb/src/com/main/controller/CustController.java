package com.main.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.Valid;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Encoder;

import com.google.gson.Gson;
import com.main.bean.CustBean;
import com.main.dao.CustAguasPassadasDao;
import com.main.dao.CustContactsDao;
import com.main.dao.CustEventDao;
import com.main.dao.CustHeaderDao;
import com.main.dao.CustItemDao;
import com.main.domain.cust.CustContacts;
import com.main.domain.cust.CustEvent;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustItem;
import com.main.domain.cust.CustLogistics;
import com.main.manager.CustManager;
import com.main.util.CustABankSync;
import com.main.util.MakeOrderNumUtil;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.Cust;
import com.mw.framework.domain.CustAguasPassadas;
import com.mw.framework.domain.CustInfo;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.FieldFunction;
import com.mw.framework.utils.TypeCaseHelper;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 *
 */
@Controller
@RequestMapping("/main/cust/*")
public class CustController extends BaseController {
	final static String CUST_ID="GD44000009178969501";
	final static String USER_ID="WLPT01";
	final static String PASSWORD="111111";
	final static String PAY_ACCNO="44050155150700000084";

	@Autowired
	private CustHeaderDao custHeaderDao;

	@Autowired
	private CustItemDao custItemDao;

	@Autowired
	private CustContactsDao custContactsDao;

	@Autowired
	private CustManager custManager;

	@Autowired
	private CommonManager commonManager;

	@Autowired
	private CustAguasPassadasDao custAguasPassadasDao;
	
	@Autowired
	private CustEventDao custEventDao;

	/**
	 * 列出所有流程模板
	 */
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public ModelAndView list(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "Cust2App");
		return mav;
	}

	@RequestMapping(value = { "/query" }, method = RequestMethod.GET)
	public ModelAndView query(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "CustApp");
		return mav;
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForList(int page, int limit) {
		String kunnr = this.getRequest().getParameter("kunnr1");
		String name1 = this.getRequest().getParameter("name1");
		String ktokd = this.getRequest().getParameter("ktokd");
		String tel = this.getRequest().getParameter("telNumber");
		String bzirk = this.getRequest().getParameter("bzirk");
		String xinDai = this.getRequest().getParameter("xinDai");
		String startDate = this.getRequest().getParameter("startDate");
		String endDate = this.getRequest().getParameter("endDate");
		//String tradeAccno = this.getRequest().getParameter("tradeAccNo");
		// 标志是查询售达方，还是送达方
		String custFlag = this.getRequest().getParameter("custFlag");	
		StringBuffer sb = new StringBuffer(
				"select t.*,(select cb.bank_id from cust_bank cb where cb.cust_id = t.id and cb.bank_status = 'NH') AS NH, "
						+ "(select cb.bank_id from cust_bank cb  where cb.cust_id = t.id and cb.bank_status = 'JH') AS JH "
						+ "from cust_view t where 1=1");
		//"select t.*,CASE WHEN CB.BANK_STATUS = 'NH' THEN CB.BANK_ID END AS NH,CASE WHEN CB.BANK_STATUS = 'JH' THEN CB.BANK_ID END AS JH from cust_view t left join cust_header ch on ch.kunnr=t.kunnr left join cust_bank cb on ch.id=cb.cust_id where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		if (!StringUtils.isEmpty(kunnr)) {
			sb.append(" and t.KUNNR like ? ");
			params.add(StringHelper.like(String.valueOf(kunnr)));
		}
		if (!StringUtils.isEmpty(name1)) {
			sb.append(" and t.NAME1 like ? ");
			params.add(StringHelper.like(String.valueOf(name1)));
		}
		if (!StringUtils.isEmpty(ktokd)) {
			sb.append(" and t.KTOKD = ? ");
			params.add(ktokd.trim());
		}
		if (!StringUtils.isEmpty(tel)) {
			sb.append(" and t.TEL like ? ");
			params.add(StringHelper.like(String.valueOf(tel)));
		}
		if (!StringUtils.isEmpty(xinDai)) {
			sb.append(" and t.XIN_DAI like ? ");
			params.add(StringHelper.like(String.valueOf(xinDai)));
		}
		if (!StringUtils.isEmpty(bzirk)) {
			sb.append(" and t.BZIRK like ? ");
			params.add(StringHelper.like(String.valueOf(bzirk)));
		}
		if (!StringUtils.isEmpty(startDate)) {
			sb.append(" and START_DATE >= ? ");
			params.add(DateTools.strToDate(startDate, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(endDate)) {
			sb.append(" and END_DATE <= ? ");
			params.add(DateTools.strToDate(endDate, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(custFlag)) {
			if ("songDaFang".equals(custFlag)) {
				sb.append("and t.row_status='1' and t.KTOKD in ('Z710','Z720') ");//鍚敤鐘舵�鐨�
			} else if ("shouDaFang".equals(custFlag)) {
				sb.append("and t.row_status='1' and t.KTOKD not in ('Z710','Z720') ");//鍚敤鐘舵�鐨�
			}
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

	@SuppressWarnings("unused")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	// @IgnoreProperties(value = { @IgnoreProperty(name = {
	// "hibernateLazyInitializer", "handler", "fieldHandler", "sort",
	// "custItemSet" }, pojo = CustHeader.class) })
	@ResponseBody
	public Message save(@Valid CustHeader custHeader, BindingResult result,
			@RequestBody CustBean custBean) {
		Message msg = null;

		String nh = this.getRequest().getParameter("nh");
		String jh = this.getRequest().getParameter("jh");
		String kunnr = this.getRequest().getParameter("kunnr");
		String name1 = this.getRequest().getParameter("name1");
		String sql ="select ch.id from cust_header ch where ch.kunnr='"+kunnr+"'";
		Map<String, Object> map1 = jdbcTemplate.queryForMap(sql);
		String cust_id=(String)map1.get("ID");
		//绑定农行账号
		if(!"未绑定".equals(nh)){
			if("".equals(nh)||nh.isEmpty()){
				//如果为空则清空绑定账号信息
				jdbcTemplate.execute("delete cust_bank cb where cb.cust_id='"+cust_id+"'");
			}else{
				List<Map<String, Object>> custBank = jdbcTemplate.queryForList("select ch.kunnr from cust_bank cb left join cust_header ch on ch.id=cb.cust_id where cb.bank_id='"+nh+"' AND BANK_STATUS='NH'");
				String kun=null;
				if(custBank.size()>0){
					kun=(String) custBank.get(0).get("KUNNR");
					if(!kunnr.equals(kun)){
						//如果此账号被绑定则，提示不做处理
						return new Message("SAVE-BINDING","此账号已被"+kun+"绑定，请核实后再操作");
					}
				}else{
					if(kunnr.equals(kun)){
						//如果对应的客户有绑定账号，则更改绑定账号
						jdbcTemplate.execute("update cust_bank cb set cb.bank_id='"+nh+"'where cb.cust_id='"+cust_id+"' and cb.bank_status='NH'");
					}else{
						//如果对应的客户没有绑定账号，则直接插入
						jdbcTemplate.execute("insert into cust_bank values('"+cust_id+"','"+nh+"','NH')");
					}
					//更新付款人信息
					jdbcTemplate.execute("update cust_trade_header th set th.payer='"+name1+"' where th.trade_acc_no='"+cust_id+"'");

				}
			}
		}
		//绑定建行账号44050155150700000084
		if(!"未绑定".equals(jh)){
			if("".equals(jh)||jh.isEmpty()){
				//如果为空则清空绑定账号信息
				jdbcTemplate.execute("delete cust_bank cb where cb.cust_id='"+cust_id+"'");
			}else{
				List<Map<String, Object>> custBank = jdbcTemplate.queryForList("select ch.kunnr from cust_bank cb left join cust_header ch on ch.id=cb.cust_id where cb.bank_id='"+jh+"' AND BANK_STATUS='JH'");
				String kun=null;
				if(custBank.size()>0){
					kun=(String) custBank.get(0).get("KUNNR");
					if(!kunnr.equals(kun)){
						//如果此账号被绑定则，提示不做处理
						return new Message("SAVE-BINDING","此账号已被"+kun+"绑定，请核实后再操作");
					}
				}else{
					if(kunnr.equals(kun)){
						//如果对应的客户有绑定账号，则更改绑定账号
						jdbcTemplate.execute("update cust_bank cb set cb.bank_id='"+jh+"'where cb.cust_id='"+cust_id+"' and cb.bank_status='JH'");
					}else{
						//如果对应的客户没有绑定账号，则直接插入
						jdbcTemplate.execute("insert into cust_bank values('"+cust_id+"','"+jh+"','JH')");
					}
					//更新付款人信息
					jdbcTemplate.execute("update cust_trade_header th set th.payer='"+name1+"' where th.trade_acc_no='"+cust_id+"'");
				}
			}
		}

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
				String kunnrS = custHeader.getKunnrS();
				if (ZStringUtils.isEmpty(kunnrS)) {
					custHeader.setKunnrS(custHeader.getKunnr());
				}
				List<CustItem> custItemList = custBean.getCustItemList();
				Set<CustItem> custItemSet = new HashSet<CustItem>();
				for (CustItem custItem : custItemList) {
					//					custItem.setStatus("1");// 状态为1时正常，为0时表示已经删除
					custItem.setKunnr(custHeader.getKunnr());
					custItem.setCustHeader(custHeader);
					custItemSet.add(custItem);
				}

				List<CustContacts> custContactsList = custBean
						.getCustContactsList();
				Set<CustContacts> custContactsSet = new HashSet<CustContacts>();
				for (CustContacts custContacts : custContactsList) {
					custContacts.setStatus("1");// 状态为1时正常，为0时表示已经删除
					custContacts.setKunnr(custHeader.getKunnr());
					custContacts.setCustHeader(custHeader);
					custContactsSet.add(custContacts);
				}

				custHeader.setCustItemSet(custItemSet);
				custHeader.setCustContactsSet(custContactsSet);
				CustHeader obj = custManager.save(custHeader);

				/************** 保存后，处理返回信息(start) ******************/
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", obj.getId());
				map.put("createTime", DateTools.formatDate(obj.getCreateTime(),
						DateTools.fullFormat));
				map.put("updateTime", DateTools.formatDate(obj.getUpdateTime(),
						DateTools.fullFormat));
				map.put("createUser", obj.getCreateUser());
				map.put("updateUser", obj.getUpdateUser());
				Field[] declaredFields = obj.getClass().getDeclaredFields();
				for (int i = 0; i < declaredFields.length; i++) {
					Field field = declaredFields[i];
					String name = field.getName();
					if (name == "custItemSet" || name == "custContactsSet") {
						continue;
					} else {
						Object property = BeanUtils.getValue(obj, name);
						if (property != null) {
							map.put(name, property);
						}
					}
				}
				String[] strings = new String[] { "hibernateLazyInitializer",
						"handler", "fieldHandler", "sort", "custItemSet",
				"custContactsSet" };
				msg = new Message(JSONObject.fromObject(map, super
						.getJsonConfig(strings)));
				/************** 保存后，处理返回信息(end) ******************/

			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("DD-S-500","保存失败");
			}
		}
		return msg;
	}

	@RequestMapping(value = "/findById", method = RequestMethod.GET)
	@ResponseBody
	public Message findById(String id) throws ParseException {
		Message msg = null;
		try {
			CustHeader custHeader = custHeaderDao.findOne(id);
			if (custHeader == null) {
				List<CustHeader> findByCode = custHeaderDao.findByCode(id);
				custHeader = findByCode.get(0);
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
						// Object KKBER = table4.getValue("KKBER");// 公司代码
						// Object KUNNR = table4.getValue("KUNNR");// 客户编号
						if (ZStringUtils.isNotEmpty(value)) {
							custHeader.setXinDai(value.toString());
							// System.out.println("=============>"
							// + value.toString());
						}
					}
				}
			}

			Map<String, Object> map = new HashMap<String, Object>();
			if (custHeader != null) {

				map.put("id", custHeader.getId());
				map.put("createTime", DateTools.formatDate(custHeader
						.getCreateTime(), DateTools.fullFormat));
				map.put("updateTime", DateTools.formatDate(custHeader
						.getUpdateTime(), DateTools.fullFormat));
				map.put("createUser", (custHeader.getCreateUser()!=null&&!"".equals(custHeader.getCreateUser())?custHeader.getCreateUser():""));
				map.put("updateUser", (custHeader.getUpdateUser()!=null&&!"".equals(custHeader.getUpdateUser())?custHeader.getUpdateUser():""));
				String sql1="select distinct (select cb.bank_id from cust_bank cb where cb.bank_status = 'NH' "
						+ " and cb.cust_id=ch.id) AS NH, (select cb.bank_id from cust_bank cb "
						+ " where cb.bank_status = 'JH'and cb.cust_id=ch.id) AS JH"
						+ " from cust_header ch where ch.kunnr='"+custHeader.getKunnr()+"'";
				Map<String, Object> map1 = jdbcTemplate.queryForMap(sql1);
				String nh=(String) map1.get("NH");
				String jh=(String) map1.get("JH");
				if(nh!=null){
					map.put("nh", nh);
				}else{
					map.put("nh", "未绑定");
				}
				if(jh!=null){
					map.put("jh", jh);
				}else{
					map.put("jh", "未绑定");
				}
				Field[] declaredFields = custHeader.getClass()
						.getDeclaredFields();
				for (int i = 0; i < declaredFields.length; i++) {
					Field field = declaredFields[i];
					String name = field.getName();
					if (name == "custItemSet" || name == "custContactsSet") {
						continue;
					} else {
						Object property = BeanUtils.getValue(custHeader, name);
						if (property != null) {
							map.put(name, property);
						}
					}
				}
			}
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "custItemSet",
			"custContactsSet","custLogisticsSet" };
			msg = new Message(JSONObject.fromObject(map, super
					.getJsonConfig(strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-GET-500", "数据加载失败!");
		}
		return msg;
	}

	//根据客户初始化客户的信贷金额
	@RequestMapping(value = "/findxdById", method = RequestMethod.GET)
	@ResponseBody
	public Message findxdById(String id) throws ParseException {
		Message msg = null;
		try {
			CustHeader custHeader = custHeaderDao.findOne(id);
			if (custHeader == null) {
				List<CustHeader> findByCode = custHeaderDao.findByCode(id);
				custHeader = findByCode.get(0);
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
			//function2.getImportParameterList().setValue("S_KUNNR", "LJ31705");

			function2.execute(connect);
			JCoTable table4 = function2.getTableParameterList().getTable(
					"IT_TAB1");

			if (table4.getNumRows() > 0) {
				table4.firstRow();
				for (int i = 0; i < table4.getNumRows(); i++, table4.nextRow()) {
					if (i == 0) {
						Object value = table4.getValue("OBLIG_S");// 剩余信贷额度
						// Object KKBER = table4.getValue("KKBER");// 公司代码
						// Object KUNNR = table4.getValue("KUNNR");// 客户编号
						if (ZStringUtils.isNotEmpty(value)) {
							custHeader.setXinDai(value.toString());
							// System.out.println("=============>"
							// + value.toString());

						}
					}
				}
			}

			Map<String, Object> map = new HashMap<String, Object>();
			if (custHeader != null) {
				map.put("xinDai", custHeader.getXinDai());
				map.put("id", custHeader.getId());
				map.put("createTime", DateTools.formatDate(custHeader
						.getCreateTime(), DateTools.fullFormat));
				map.put("updateTime", DateTools.formatDate(custHeader
						.getUpdateTime(), DateTools.fullFormat));
				map.put("createUser", custHeader.getCreateUser());
				map.put("updateUser", custHeader.getUpdateUser());

			}
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "custItemSet",
			"custContactsSet" };
			msg = new Message(JSONObject.fromObject(map, super
					.getJsonConfig(strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-GET-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 通过code查询某个CustHeader
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/findByCode", method = RequestMethod.GET)
	@IgnoreProperties(value = { @IgnoreProperty(name = {
			"hibernateLazyInitializer", "handler", "fieldHandler", "sort",
	"custItemSet" }, pojo = CustHeader.class) })
	@ResponseBody
	public Message findByCode(String code) {
		Message msg = null;
		try {
			List<CustHeader> findByCode = custHeaderDao.findByCode(code);
			if (findByCode != null && findByCode.size() > 0) {
				msg = new Message(findByCode.get(0));
			} else {
				msg = new Message("MM-GET-500", "当前客户不存在!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-GET-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 根据CustHeader.code查找对应CustItem
	 * 
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/findItemsByCode", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray findItemsByCode(String code) {
		List<CustItem> findItemByCode = custItemDao.findItemsByCode(code);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "custHeader" };
		// System.out.println(JSONArray.fromObject(findItemByCode, super
		// .getJsonConfig(strings)));
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		return JSONArray.fromObject(findItemByCode, super
				.getJsonConfig(strings));
	}

	/**
	 * 根据CustHeader.id查找对应CustItem
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/findItemsByPid", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray findItemsByPid(String pid) {
		List<CustItem> findItemByPid = custItemDao.findItemsByPid(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "custHeader" };
		//		System.out.println(JSONArray.fromObject(findItemByPid, super
		//				.getJsonConfig(strings)));
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		return JSONArray
				.fromObject(findItemByPid, super.getJsonConfig("yyyy-MM-dd",strings));
	}

	/**
	 * 根据CustHeader.id查找对应CustContacts
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/findContactsByPid", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray findContactsByPid(String pid) {
		List<CustContacts> findContactsByPid = custContactsDao
				.findContactsByPid(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "custHeader" };
		//System.out.println(JSONArray.fromObject(findContactsByPid, super
		//		.getJsonConfig(strings)));
		// "yyyy/MM/dd HH:mm:ss" 标配后台一定要这样在前台正常显示
		return JSONArray.fromObject(findContactsByPid, super
				.getJsonConfig(strings));
	}

	/**
	 * 根据ids查找custItem，真正删除数据<注释的代码是(不做真删除)更新删除标志>
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteCustItemByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteCustItemByIds(String[] ids) {
		Message msg = null;
		try {
			custManager.delete(ids, CustItem.class);
			/*
			 * final List list = new ArrayList(); for (String id : ids) {
			 * Map<String, Object> map = new HashMap<String, Object>();
			 * map.put("id", id); map.put("update_user",
			 * this.getLoginUser().getLoginNo()); list.add(map); }
			 * 
			 * String sql =
			 * "update cust_item set status='0',update_time=?,update_user=? where id=?"
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
	 * 根据ids查找custContacts，真正删除数据<注释的代码是(不做真删除)更新删除标志>
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteCustContactsByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteCustContactsByIds(String[] ids) {
		Message msg = null;
		try {
			custManager.delete(ids, CustContacts.class);
			/*
			 * final List list = new ArrayList(); for (String id : ids) {
			 * Map<String, Object> map = new HashMap<String, Object>();
			 * map.put("id", id); map.put("update_user",
			 * this.getLoginUser().getLoginNo()); list.add(map); }
			 * 
			 * String sql =
			 * "update cust_contacts set status='0',update_time=?,update_user=? where id=?"
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
	 * 同步农行数据
	 * @return
	 * @throws JCoException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/syncBank",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message syncABank(){
		long startTime = System.currentTimeMillis();
		Message msg=null;
		List<Map<String, Object>> listData = CustABankSync.syncABank();
		//遍历农行返回集合
		if(listData.size()>0){
			for (Map<String, Object> obj : listData) {
				if(obj.size()>0){
					Set<Entry<String, Object>> entrys1 = obj.entrySet();
					for (Entry<String, Object> entry1 : entrys1) {
						Map<String,Object> map1 = (Map<String, Object>) entry1.getValue();
						Map<String,Object> val1 = (Map<String, Object>) map1.get("Value");
						Integer code1 = (Integer) map1.get("Code");
						//判断是否连接成功
						if(code1==0){
							//code==0连接接口成功
							List<CustAguasPassadas> list = (List<CustAguasPassadas>) val1.get("List");
							for (int i = 0; i < list.size(); i++) {
								Map<String, Object> m=(Map<String, Object>) list.get(i);
								String jrnNo = (String) m.get("JrnNo");
								//判断交易交易流水是否已更新
								String sql="SELECT COUNT(*) FROM CUST_TRADE_HEADER TH WHERE TH.JRN_NO='"+jrnNo+"'";
								int count = jdbcTemplate.queryForObject(sql, Integer.class);
								if(count>0){
									//已更新
								}else{
									//没有更新，更新并保存到数据库
									String tradeCompanyId = (String) m.get("TradeCompanyId");
									String tradeAccNo = (String) m.get("TradeAccNo");
									jrnNo=(String) m.get("JrnNo");
									String tradeDate= (String) m.get("TradeDate");
									String dealerNum = (String) m.get("DealerNum");
									String timer = String.valueOf(m.get("TradeTime"));
									timer=timer.substring(timer.indexOf("(")+1, timer.lastIndexOf(")"));
									Date tradeTime= new Date(Long.parseLong(timer));
									String tradeRemark= (String) m.get("TradeRemark");
									String tradePostcript= (String) m.get("TradePostcript");
									String tradeDescription= (String) m.get("TradeDescription");
									String payer=(String)m.get("Payer");
									Double tradeAmount= Double.parseDouble(String.valueOf(m.get("TradeAmount")));
									CustAguasPassadas aguasPassadas=new CustAguasPassadas(tradeCompanyId, tradeAccNo, jrnNo, tradeDate, dealerNum,
											tradeTime,tradeRemark, tradePostcript, tradeDescription,tradeAmount,payer, null, "NH");
									commonManager.save(aguasPassadas);//保存到数据库
								}
							}
							msg=new Message("syncBank-200-100","同步成功");
						}else{
							msg=new Message("syncBank-500-101","连接农行失败");
						}
					}
				}
			}
		}else{
			msg=new Message("syncBank-500-101","同步0条数据");
		}
		long endTime = System.currentTimeMillis();
		System.out.println("同步AB所用时间："+((endTime-startTime)/1000));

		return msg;
	}
	/**
	 * 同步建行数据
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws DocumentException
	 * @throws ParseException
	 */
	@RequestMapping(value="/syncJHBank",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message syncJHBank() throws UnknownHostException, IOException, DocumentException, ParseException{
		Message msg=null;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		String date = df.format(new Date());
		// 拼接xml报文
		int count=Integer.MAX_VALUE;
		for (int i = 1; i <= count; i++) {
			if(!(i>count)){
				StringBuilder sb = new StringBuilder();
				String num = MakeOrderNumUtil.makeOrderNum(true);
				sb.append("<?xml version='1.0' encoding='GB2312' standalone='yes' ?>");
				sb.append("<TX>");
				sb.append("<REQUEST_SN>" + num + "</REQUEST_SN>");// 请求序列码
				sb.append("<CUST_ID>" + CUST_ID + "</CUST_ID>");// 客户号
				sb.append("<USER_ID>" + USER_ID + "</USER_ID>");// 操作员号
				sb.append("<PASSWORD>" + PASSWORD + "</PASSWORD>");// 密码
				sb.append("<TX_CODE>6WY101</TX_CODE>");// 交易请求码
				sb.append("<LANGUAGE>CN</LANGUAGE>");// 语言
				sb.append("<TX_INFO>");
				sb.append("<STARTDATE>20180926</STARTDATE>");// 开始时间
				sb.append("<ENDDATE>20180926</ENDDATE>");//结束时间
				sb.append("<ACCNO1>"+PAY_ACCNO+"</ACCNO1>");//账号
				sb.append("<PAGE>"+i+"</PAGE>");
				sb.append("<TOTAL_RECORD>200</TOTAL_RECORD>");//每页记录数
				sb.append("</TX_INFO>");
				sb.append("</TX>");
				// 向服务器端发送请求，服务器IP地址和服务器监听的端口号//172.16.9.2
				Socket client = new Socket("172.16.3.217", 8282);
				//此处添加连接时长，超时结束
				//client.setSoTimeout(500000);
				// 通过printWriter 来向服务器发送消息
				PrintWriter printWriter = new PrintWriter(client.getOutputStream());
				System.out.println("连接已建立...");
				// 发送消息
				printWriter.println(sb);
				printWriter.flush();
				// InputStreamReader是低层和高层串流之间的桥梁
				// client.getInputStream()从Socket取得输入串流
				InputStreamReader streamReader = new InputStreamReader(client.getInputStream(),"GBK");
				// 链接数据串流，建立BufferedReader来读取，将BufferReader链接到InputStreamReder
				BufferedReader reader = new BufferedReader(streamReader);
				StringBuffer content = new StringBuffer();
				String ch=null;
				while ((ch = reader.readLine()).length()>0) {
					content.append( ch);
				}
				reader.close();
				String advice=content.toString();
				System.out.println(advice);
//				String utf8 = new String(advice.getBytes("GB18030")); 
//				String unicode = new String(utf8.getBytes(), "GB2312"); 
//				String gbk = new String(unicode.getBytes("GB2312"));
				org.dom4j.Document doc=DocumentHelper.parseText(advice);
				Element rootElt = doc.getRootElement(); // 获取根节点
				Iterator<Element> TX_INFO = rootElt.elementIterator("TX_INFO"); // 获取根节点下的子节点TX_INFO
				while (TX_INFO.hasNext()) {
					Element recordEle = TX_INFO.next();
					if(!(count==Integer.parseInt(recordEle.elementTextTrim("TOTAL_PAGE")))){
						count = Integer.parseInt(recordEle.elementTextTrim("TOTAL_PAGE")); // 拿到TX_INFO节点下的子节点TOTAL_PAGE值
					}
					Iterator<Element> DETAILLIST = recordEle.elementIterator("DETAILLIST"); // 获取子节点TX_INFO下的子节点DETAILLIST
					// 遍历TX_INFO节点下的DETAILLIST节点
					while (DETAILLIST.hasNext()) {
						Element recordEle1 = DETAILLIST.next();
						Iterator<Element> DETAILINFO = recordEle1.elementIterator("DETAILINFO"); // 获取子节点DETAILLIST下的子节点DETAILINFO
						while(DETAILINFO.hasNext()){
							Element recordEle2 = (Element) DETAILINFO.next();
							String FLAG1=recordEle2.elementTextTrim("FLAG1");//借贷标志
							if(FLAG1.equals("1")){
								//如果借贷标志=1则为转入流水
								String tradeDate1 = recordEle2.elementTextTrim("TRANDATE");//交易日期
								String tradeDate=tradeDate1.replaceAll("/", "");
								Double tradeAmount =Double.parseDouble(recordEle2.elementTextTrim("AMT")); //金额 
								String timer = tradeDate1+" "+recordEle2.elementTextTrim("TRANTIME");//交易时间
								timer=timer.replace("/", "-");
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date tradeTime= sdf.parse(timer);
								String jrnNo = recordEle2.elementTextTrim("TRAN_FLOW");//交易流水号
								String tradeAccNo = recordEle2.elementTextTrim("RLTV_ACCNO");//关联账号
								String accName1 = recordEle2.elementTextTrim("ACC_NAME1");//打款人
								String accno2 = recordEle2.elementTextTrim("ACCNO2");//打款帐号
								//绑定付款人信息
								String payer="";
								String sql1="select distinct ch.payer from cust_trade_header ch where ch.trade_acc_no='"+tradeAccNo+"'";
								List<Map<String,Object>> list=jdbcTemplate.queryForList(sql1);
								if(list.size()>0){
									payer =(String)list.get(0).get("PAYER");
								}
								//保存到数据库
								String sql="SELECT COUNT(1) FROM CUST_TRADE_HEADER TH WHERE TH.JRN_NO= '"+jrnNo+"'";
								int count1 = jdbcTemplate.queryForObject(sql, Integer.class);
								if(count1==0){
									CustAguasPassadas aguasPassadas=new CustAguasPassadas(tradeAccNo, jrnNo, tradeDate, tradeTime, tradeAmount, "JH",payer,accName1,accno2);
									commonManager.save(aguasPassadas);

									msg=new Message("同步成功");
								}
							}else{
								msg=new Message("同步0条数据");
							}
						}
					}
				}
			}
		}
		return msg;
	}
	
	/**
	 * 批量绑定加密锁
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value="/softDogUpload",method=RequestMethod.POST)
	public ResponseEntity<String> softDogUpload(@RequestParam("file") CommonsMultipartFile file) throws IOException{
		List<String> list=new ArrayList<String>();
		HttpHeaders responseHeaders=new HttpHeaders();
		InputStream input =file.getFileItem().getInputStream();
		String json ="";
		String str="";
		String str1="";
		//判断excel版本
		boolean is2003Excel=file.getFileItem().getName().matches("^.+\\.(?i)(xls)$");
		if(is2003Excel){
			HSSFWorkbook workbook=new HSSFWorkbook(input);
			HSSFSheet sheet = workbook.getSheet("Sheet1");
			int num = sheet.getPhysicalNumberOfRows();
			for(int i=1;i<num;i++){
				HSSFRow rows = sheet.getRow(i);
				HSSFCell cell1 = rows.getCell(0);
				HSSFCell cell2 = rows.getCell(1);
				if(cell1!=null&&cell2!=null){
					String id=cell1.getStringCellValue();
					String softDog=cell2.getStringCellValue();
					int count = jdbcTemplate.queryForObject("select count(*) from sys_user su where su.softdog_id='"+softDog+"'",Integer.class);
					if(count==0){
						int count1 = jdbcTemplate.queryForObject("select count(*) from sys_user su where su.id='"+id+"'",Integer.class);
						if(count1>0){
							jdbcTemplate.update("update sys_user su set su.softdog_id='"+softDog+"' where su.id='"+id+"'");
						}else{
							//此登陆账号未维护
							str1+=id+",";
						}
					}else{
						//此加密锁已绑定
						str+=softDog+",";
					}
				}else{
					json="{success :false,msg:'表格不允许未空'}";
				}
			}
			workbook.close();
		}else{
			json="{success :false,msg:'上传文件格式错误'}";
		}
		if("".equals(json)){
			json="{success :true,msg:'绑定完成'}";
		}
		StringBuffer msg=new StringBuffer();
		if(!"".equals(str)){
			msg.append(str+"此加密锁已绑定");
		}
		if(!"".equals(str1)){
			msg.append(str1+"此登陆账号未维护");
		}
		if(!"".equals(msg.toString())){
			json="{success :false,msg:'"+msg+"'}";
		}
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<String>(json, responseHeaders,
				HttpStatus.OK);
	}
	
	/**
	 * 批量绑定客户等级折扣，只限年终冲量活动政策折扣
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/custZhekouUpdateFile",method=RequestMethod.POST)
	public ResponseEntity<String> uploadCustZhekou(@RequestParam("file") CommonsMultipartFile file) throws IOException{
		HttpHeaders responseHeaders = new HttpHeaders();
		FileItem fileItem = file.getFileItem();
		InputStream input = fileItem.getInputStream();
		String json="";
		//判断excel版本
		boolean is2003Excel=fileItem.getName().matches("^.+\\.(?i)(xls)$");
		if(is2003Excel){
			HSSFWorkbook workbook=new HSSFWorkbook(input);
			HSSFSheet sheet = workbook.getSheet("Sheet1");
			int num = sheet.getPhysicalNumberOfRows();
			if(num>1){
				jdbcTemplate.update("update cust_event ce set ce.cust_level_zhekou=''");
				String message="";
				for (int i = 0; i <num; i++) {
					HSSFRow rows = sheet.getRow(i);
					HSSFCell cell1 = rows.getCell(0);
					HSSFCell cell2 = rows.getCell(1);
					if(cell1!=null){
						int iskunnr = jdbcTemplate.queryForObject("select count(*) from cust_event ce where ce.kunnr='"+cell1+"'", Integer.class);
						if(iskunnr>0){
							if(cell2!=null){
								jdbcTemplate.execute("update cust_event ce set ce.cust_level_zhekou='"+cell2.toString()+"'where ce.kunnr='"+cell1.toString().toUpperCase()+"'");
							}
						}else{
							message=cell1+",";
						}
					}
				}
					json="{success :true,msg:'绑定完成'}";
			}
		}else{
			json="{success :false,msg:'上传文件格式错误'}";
		}
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<String>(json, responseHeaders,
				HttpStatus.OK);
	}
	/**
	 * 批量绑定，导入银行账号客户Excel表格
	 * @return
	 */
	@RequestMapping(value="/BankUpdateFile",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<String> enterExc(@RequestParam("file") CommonsMultipartFile file) throws IOException{
		List<String> list=new ArrayList<String>();
		HttpHeaders responseHeaders = new HttpHeaders();
		FileItem fileItem = file.getFileItem();
		InputStream input = fileItem.getInputStream();
		String json="";
		//判断excel版本
		boolean is2003Excel=fileItem.getName().matches("^.+\\.(?i)(xls)$");
		if(is2003Excel){
			HSSFWorkbook workbook=new HSSFWorkbook(input);
			HSSFSheet sheet = workbook.getSheet("Sheet1");
			int num = sheet.getPhysicalNumberOfRows();
			String str="";
			for(int i=1;i<num;i++){
				HSSFRow rows = sheet.getRow(i);
				HSSFCell cell1 = rows.getCell(0);
				HSSFCell cell2 = rows.getCell(1);
				HSSFCell cell3 = rows.getCell(2);
				if(cell1!=null&&cell2!=null&&cell3!=null){
					String bank_status=cell1.getStringCellValue();
					String bank_id=cell2.getStringCellValue();
					String kunnr=cell3.getStringCellValue();
					List<Map<String, Object>> list2 = jdbcTemplate.queryForList("select ch.name1 from cust_header ch where ch.kunnr='"+kunnr+"'");
					List<Map<String, Object>> list3 = jdbcTemplate.queryForList("select ch.id from cust_header ch where ch.kunnr='"+kunnr+"'");
					String name1="";
					String id="";
					if(list2.size()<1){
						str+=kunnr+",";
					}
					try{
						name1=(String)list2.get(0).get("NAME1");
						id=(String)list3.get(0).get("ID");
					}catch(Exception e){
						System.out.println(name1);
					}
					
					//更新付款人信息
					jdbcTemplate.execute("update cust_trade_header th set th.payer='"+name1+"' where th.trade_acc_no='"+bank_id+"'");
					//查询此账号是否绑定
					int count=jdbcTemplate.queryForObject("select count(1) from cust_bank cb where cb.bank_id='"+bank_id+"'", Integer.class);
					if(count==0){
						if("农行".equals(bank_status)){
							String checkNH=bank_id.substring(0,8);
							//判断农行账号的格式
							if("62284000".equals(checkNH)){
								//判断客户name1是否绑定农行账号
								int count1=jdbcTemplate.queryForObject("select count(cb.bank_id) from cust_bank cb where cb.cust_id='"+id+"' and cb.bank_status='NH'", Integer.class);
								if(count1==0){
									jdbcTemplate.execute("insert into cust_bank cb values('"+id+"','"+bank_id+"','NH')");
								}else{
									jdbcTemplate.execute("update cust_bank cb set cb.bank_id='"+bank_id+"' where cb.cust_id='"+id+"' and cb.bank_status='NH'");
								}
							}else{
								//农行账号的格式错误
								list.add(name1);
							}
						}else{
							if("建行".equals(bank_status)){
								String checkJH=bank_id.substring(0,20);
								//判断建行账号的格式
								if("44050155150700000084".equals(checkJH)){
									//判断客户name1是否绑定建行账号
									int count2=jdbcTemplate.queryForObject("select count(cb.bank_id) from cust_bank cb where cb.cust_id='"+id+"' and cb.bank_status='JH'", Integer.class);
									if(count2==0){
										jdbcTemplate.execute("insert into cust_bank cb values('"+id+"','"+bank_id+"','JH')");
									}else{
										jdbcTemplate.execute("update cust_bank cb set cb.bank_id='"+bank_id+"' where cb.cust_id='"+id+"' and cb.bank_status='JH'");
									}
								}else{
									//建行账号的格式错误
									list.add(name1);
								}
							}
						}
					}
				}
			}
			if(!"".equals(str)){
				json ="{success:false,msg:'"+str+"绑定失败,其他绑定成功"+"'}";
			}else{
				if(list.size()==0){
					json="{success :true,msg:'绑定完成'}";
				}else{
					String message="";
					for(int i =0;i<list.size();i++){
						message += list.get(i)+"；";
						json ="{success:false,msg:'"+message+"绑定失败"+"'}";
					}
				}
			}
			workbook.close();
		}else{
			json="{success :false,msg:'上传文件格式错误'}";
		}
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<String>(json, responseHeaders,
				HttpStatus.OK);
	}

	/**
	 * 同步sap,获取凭证信息		
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value="/add_doco",method=RequestMethod.POST)
	@ResponseBody
	public Message findCustDoco() throws ParseException{
		Message msg=null;
		JCoDestination connect = SAPConnect.getConnect();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String today=df.format(new Date());
		Calendar   cal   =   Calendar.getInstance();  
		cal.add(Calendar.DATE,   1); 
		//yesterday此为前天
		String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime()); 
		//连接sap
		JCoFunction function=null;
		String kunnr2=null;
		String tradeAmount2=null;
		String name2=null;
		String jrnNo=null;
		String BKTXT=null;
		String sql1 ="select ch.kunnr,th.acc_name1,th.trade_date,th.trade_amount,ch.name1,th.jrn_no,cb.bank_status from cust_header ch "
			+"left join cust_bank cb on ch.id=cb.cust_id left join cust_trade_header th on th.trade_acc_no=cb.bank_id where cb.bank_id is not null "
			+ " and th.trade_time between date '2018-11-20' and date '2018-12-30'";
		//		String sql1 ="select ch.kunnr,th.trade_date,th.trade_amount,ch.name1,th.jrn_no from cust_header ch "
		//				+"left join cust_trade_header th on th.trade_acc_no=ch.trade_accno where th.jrn_no='421027695'";
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(sql1);
		for (Map<String, Object> map : totalElements) {
			kunnr2=(String) map.get("KUNNR");
			String accName1=(String) map.get("ACC_NAME1");
			String tradeDate2=(String)map.get("TRADE_DATE");
			String year=tradeDate2.substring(0, 4);
			String month=tradeDate2.substring(4, 6);
			tradeAmount2=map.get("TRADE_AMOUNT").toString();
			name2=(String) map.get("NAME1");
			jrnNo=(String)map.get("JRN_NO");
			String bankStatus=(String)map.get("BANK_STATUS");
			if("NH".equals(bankStatus)){
				BKTXT="预收款:"+name2+"预付款";
			}else{
				BKTXT="预收"+name2+"款,"+jrnNo+"-"+accName1;
			}
			try {
				function = connect.getRepository().getFunction("ZRFC_FI_U801");
				function.getImportParameterList().setValue("I_AD_TYPE", "4");
				JCoStructure structure = function.getImportParameterList().getStructure("S_AD_BKPF");
				structure.setValue("BLDAT", tradeDate2);//交易日期
				structure.setValue("BLART", "DZ");
				structure.setValue("BUKRS", "3100");
				structure.setValue("BUDAT", tradeDate2);//交易日期
				structure.setValue("GJAHR", year);//交易年
				structure.setValue("MONAT", month);//交易月
				structure.setValue("USNAM", "abc");//打款人姓名
				structure.setValue("WAERS", "CNY");
				structure.setValue("BKTXT", BKTXT);//信息摘要
				structure.setValue("PPNAM", "admin");//操作人姓名
				structure.setValue("ZVOUCHER",jrnNo);//交易流水号，唯一标识
				//连接sap
				JCoTable table2 = function.getTableParameterList().getTable("T_AD_BSEG");
				table2.appendRow();
				table2.setValue("BUZEI", "10");           
				table2.setValue("SHKZG", "S");            
				table2.setValue("BSCHL", "40");
				if(bankStatus.equals("NH")){
					table2.setValue("HKONT", "1002010101");
				}else{
					table2.setValue("HKONT", "1002010201");
				}
				table2.setValue("USNAM", "abc");//打款人姓名
				table2.setValue("BUKRS", "3100");   
				table2.setValue("SGTXT", BKTXT);//信息摘要     
				table2.setValue("WRBTR", tradeAmount2);//金额         
				table2.setValue("ZECFCD", "103");//现金流量代码         
				table2.setValue("PRCTR", "3100SC01");
				table2.setValue("WAERS", "CNY");
				table2.appendRow();
				table2.setValue("BUZEI", "20");           
				table2.setValue("SHKZG", "H");            
				table2.setValue("BSCHL", "19");           
				table2.setValue("UMSKZ", "S");            
				table2.setValue("HKONT", "2203010100");   
				table2.setValue("USNAM", "abc");//打款人姓名
				table2.setValue("BUKRS", "3100");   
				table2.setValue("SGTXT", BKTXT);//信息摘要         
				table2.setValue("WRBTR", tradeAmount2);//金额         
				table2.setValue("WAERS", "CNY");
				table2.setValue("CUSTOMER", kunnr2);
				function.execute(connect);
				//凭证信息
				String belnr=function.getExportParameterList().getString("E_BELNR");
				//标识
				String status=function.getExportParameterList().getString("E_STATUS");
				//返回信息
				String message=function.getExportParameterList().getString("E_MESSAGE");
				System.out.println(jrnNo+"-----"+status+belnr+message);
				if(belnr!=null&&!belnr.isEmpty()){
					msg=new Message("add_doco-200-100","同步成功");
					String sql2="update cust_trade_header th set th.resumo= '" +belnr+"' where th.jrn_no= '" +jrnNo+"'";
					jdbcTemplate.execute(sql2);

				}else{
					msg=new Message("add_doco-200-300","同步0条信息");
				}
			} catch (JCoException e) {
				e.printStackTrace();
			}
		}
		return msg;
	}
	/**
	 * 查询银行数据
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/findCustBankData", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean findCustBankData(int limit,int page) {
		String kunnr=this.getRequest().getParameter("kunnr");
		String startDate = this.getRequest().getParameter("startDate");
		String endDate = this.getRequest().getParameter("endDate");
		String statusNum=this.getRequest().getParameter("status_num");
		//SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Date date=null;
		Date date2=null;
		if(startDate!=""){
			date =java.sql.Date.valueOf(startDate);
		}
		if(endDate!=""){
			date2=java.sql.Date.valueOf(endDate);
		}
		List<Object> params = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(
				"select distinct th.trade_company_id,th.trade_acc_no,th.jrn_no,th.trade_time,th.dealer_num,th.trade_amount,th.status_num,"
				+"th.payer from CUST_TRADE_HEADER th left join cust_bank cb on th.trade_acc_no=cb.bank_id left join cust_header ch on ch.id=cb.cust_id where 1=1 and ch.kunnr='"+kunnr+"'");
		if (!StringUtils.isEmpty(startDate)) {
			sql.append(" and th.TRADE_DATE >= ?");
			params.add(DateTools.formatDate(date, DateTools.YYMMDDFormat));
		}
		if (!StringUtils.isEmpty(endDate)) {
			sql.append(" and th.TRADE_DATE <= ?");
			params.add(DateTools.formatDate(date2, DateTools.YYMMDDFormat));
		}

		if (!StringUtils.isEmpty(statusNum)) {
			if(statusNum.equals("农行")){
				sql.append(" and th.STATUS_NUM ='NH'");
			}
			if(statusNum.equals("建行")){
				sql.append(" and th.STATUS_NUM ='JH'");
			}

		}


		// 获取总记录数
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		StringBuffer pageSQL = new StringBuffer("select * from (");
		if (page - 1 == 0) {
			pageSQL.append(sql + " ) where rownum <= ?");
			params.add(limit);
		} else {
			pageSQL.append("select row_.*, rownum rownum_ from ( " + sql
					+ ") row_ where rownum <= ?) where rownum_ > ?");
			params.add(limit * page);
			params.add((page - 1) * limit);
		}

		// 总页数=总记录数/每页条数(系数加1)
		int totalPages = (totalElements.size() + limit - 1) / limit;

		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(), new MapRowMapper(true));
		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}
	/**
	 * 同步sap查询信贷明细
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/findCustXindaiData", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean findCustXindaiDate(int limit,int page)throws JCoException{
		Message msg=null;
		String kunnr=this.getRequest().getParameter("kunnr");
		String startDate = this.getRequest().getParameter("startDate");
		String endDate = this.getRequest().getParameter("endDate");
		JCoDestination connect = SAPConnect.getConnect();
		JCoFunction function = connect.getRepository().getFunction("ZRFC_FI_CUSTOMER_RECONCILIATIO");
		function.getImportParameterList().setValue("L_BUDAT", startDate);
		function.getImportParameterList().setValue("H_BUDAT", endDate);
		function.getImportParameterList().setValue("P_KUNNR", kunnr);
		function.execute(connect);
		JCoTable table=function.getTableParameterList().getTable("T_AD_DZD");
		List<Map<String, Object>> queryForList=new ArrayList<Map<String,Object>>();
		Map<String, Object> map= null;
		if (table.getNumRows() > 0) {
			for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {
				//table.appendRows(i);
				map=new HashMap<String, Object>();
				String skDate=table.getValue("BUDAT").toString();		//凭证中的过账日期
				skDate = skDate.replace("CST", "").replaceAll("\\(.*\\)", "");  
				SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy", Locale.ENGLISH);  
				Date date;
				try {
					date = format.parse(skDate);
					skDate=new SimpleDateFormat("yyyyMMdd").format(date); 
					map.put("skDate", skDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String orderNum=table.getValue("BELNR").toString();		//凭证号/订单号
				String xmText=table.getValue("SGTXT").toString();		//项目文本
				String skMoney=table.getValue("DMBTR").toString();		//收款金额
				String xdMoney=table.getValue("NETWR").toString();		//下单金额
				String yeMoney=table.getValue("YE").toString();		//余额
				String bukrs=table.getValue("BUKRS").toString();		//公司代码
				//				String buyCause=table.getValue("AUGRU").toString();		//订购原因( 业务原因 )
				//				String orderCause=table.getValue("BEZEI").toString();	//订单原因
				//				String sfDate=table.getValue("MAHDT").toString();		//财务释放日期
				map.put("orderNum", orderNum);
				map.put("xmText", xmText);
				map.put("skMoney", skMoney);
				map.put("xdMoney", xdMoney);
				map.put("yeMoney", yeMoney);
				queryForList.add(map);
			}
		}else{
			return null;
		}
		// 获取总记录数
		String belnr=function.getExportParameterList().getString("E_MESSAGE");
		System.out.println(belnr);
		// 总页数=总记录数/每页条数(系数加1)
		int totalPages = (queryForList.size() + limit - 1) / limit;
		// 获取当前分页数据
		return new JdbcExtGridBean(totalPages, queryForList.size(), limit,
				queryForList);
		//}

	}
	//	/**
	//	 * 绑定银行账号
	//	 * @return
	//	 */
	//	@RequestMapping(value="/saveForBankId",method=RequestMethod.POST)
	//	@ResponseBody
	//	public Message saveForBankId(){
	//		Message msg=null;
	//		String kunnr=this.getRequest().getParameter("kunnr");
	//		String cust_tradeId01=this.getRequest().getParameter("cust_tradeId01");
	//		Map<String,String[]> params = new HashMap<String,String[]>();
	//		if(cust_tradeId01!=null&&!"".equals(cust_tradeId01)){
	//			params.put("ICEQkunnr",new String[]{kunnr});
	//			List<CustHeader> cust = commonManager.queryByRange(CustHeader.class,params);
	//			if(cust.size()>0){
	//				CustHeader custHeader=cust.get(0);
	//				String custId = custHeader.getId();
	//				if(cust_tradeId01.length()>16&&cust_tradeId01.length()<30){
	//					String sql1="select count(*) from cust_bank cb where cb.bank_id='"+cust_tradeId01+"'";
	//					int count1 = jdbcTemplate.queryForObject(sql1, Integer.class);
	//					if(count1==0){
	//						int result = jdbcTemplate.update("insert into cust_bank cb values('"+custId+"','"+cust_tradeId01+"')");
	//						//成功
	//						msg=new Message("成功绑定");
	//					}else{
	//						msg=new Message("绑定失败，此账号已绑定");
	//					}
	//				}else{
	//					msg=new Message("请输入正确的银行卡格式");
	//				}
	//			}
	//		}else{
	//			msg=new Message("请输入账号后在绑定");
	//		}
	//		return msg;
	//	}

	@RequestMapping(value = { "/syncCust" }, method = RequestMethod.POST)
	@ResponseBody
	public Message syncCust() throws JCoException {
		long startTime=System.currentTimeMillis();   //获取开始时间
		System.out.println("同步SAP客户开始时间="+startTime);

		Message msg = null;
		JCoDestination connect = SAPConnect.getConnect();
		JCoFunction function = connect.getRepository().getFunction("ZRFC_SD_KH01");
		function.execute(connect);
		// 客户主数据
		JCoTable table = function.getTableParameterList().getTable("IT_TAB1");
		// 联系人
		JCoTable table2 = function.getTableParameterList().getTable("IT_TAB2");
		// 物流信息
		JCoTable table3 = function.getTableParameterList().getTable("IT_TAB3");
		// SAP查询过来的custHeader
		List<CustHeader> custHeaderList = new ArrayList<CustHeader>();
		//		if(custHeaderList != null && custHeaderList.size()>0){
		//			
		//		}
		Set<String> set = new HashSet<String>();
		if (table.getNumRows() > 0) {
			table.firstRow();
			for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {
				CustHeader custHeader = new CustHeader();
				for (JCoField jCoField : table) {
					Object value = table.getValue(jCoField.getName());
					try {
						BeanUtils.setValue(custHeader, FieldFunction
								.dbField2BeanField(jCoField.getName()), value);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
				if(custEventDao.findByKunnr(custHeader.getKunnr())==null){
					CustEvent custEvent=new CustEvent(custHeader.getKunnr(), custHeader.getName1());
					commonManager.save(custEvent);
				};
				set.add(custHeader.getKunnr());
				custHeaderList.add(custHeader);
			}
		}
		jdbcTemplate.update(" update CUST_HEADER set row_status='0' ");//冻结所有的客户抬头

		// SAP查询过来的CustContacts
		List<CustContacts> custContactsList = new ArrayList<CustContacts>();
		if (table2.getNumRows() > 0) {
			table2.firstRow();
			for (int i = 0; i < table2.getNumRows(); i++, table2.nextRow()) {
				CustContacts custContacts = new CustContacts();
				for (JCoField jCoField : table2) {
					Object value = table2.getValue(jCoField.getName());
					try {
						BeanUtils.setValue(custContacts, FieldFunction
								.dbField2BeanField(jCoField.getName()), value);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
				custContacts.setStatus("1");
				custContactsList.add(custContacts);
			}
		}

		List<CustLogistics> custLogisticsList=new ArrayList<CustLogistics>();
		if(table3.getNumRows()>0) {
			table3.firstRow();
			for (int i = 0; i < table3.getNumRows(); i++,table3.nextRow()) {
				CustLogistics custLogistics=new CustLogistics();
				for (JCoField jcoField : table3) {
					Object value = table3.getValue(jcoField.getName());
					try {
						BeanUtils.setValue(custLogistics, FieldFunction.dbField2BeanField(jcoField.getName()), value);
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				custLogisticsList.add(custLogistics);
			}
		}
		//设置客户的信贷额度
		/*JCoFunction function2 = connect.getRepository().getFunction(
				"ZRFC_SD_XD01");
		function2.getImportParameterList().setValue("P_KKBER", "3000");*/
		/*
		 * JCoTable table3 =
		 * function2.getTableParameterList().getTable("S_KUNNR");
		 * table3.appendRow(); table3.setValue("SIGN", "I");
		 * table3.setValue("OPTION", "EQ");
		 * table3.setValue("CUSTOMER_VENDOR_LOW", "LJ57903");
		 */
		/*function2.execute(connect);
		JCoTable table4 = function2.getTableParameterList().getTable("IT_TAB1");
		if (table4.getNumRows() > 0) {
			table4.firstRow();
			for (int i = 0; i < table4.getNumRows(); i++, table4.nextRow()) {
				Object value = table4.getValue("OBLIG_S");// 剩余信贷额度
				Object KKBER = table4.getValue("KKBER");// 公司代码
				Object KUNNR = table4.getValue("KUNNR");// 客户编号
				for (CustHeader custHeader : custHeaderList) {
					if (KKBER != null
							&& custHeader.getBukrs().equals(KKBER.toString())
							&& KUNNR != null
							&& custHeader.getKunnr().equals(KUNNR.toString())
							&& value != null) {
						custHeader.setXinDai(value.toString());
					}
				}
			}
		}*/

		List<CustHeader> oldCustHeaderList = new ArrayList<CustHeader>();
		//Map<String,Set<CustContacts>> designMap=new HashMap<String, Set<CustContacts>>();//存放客户对应的设计师
		if (set != null && set.size() > 0) {
			//StringBuffer kunnrs = new StringBuffer();

			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				Set<CustContacts> designSet=new HashSet<CustContacts>();
				String kunnr = (String) iterator.next();
				//查询出设计师
				/*String design_sql="select * from cust_contacts where kunnr ='"+ kunnr + "' and abtnr='0099'";
				List<Map<String,Object>> designMapList=jdbcTemplate.queryForList(design_sql);
				for(Map<String,Object> design:designMapList){
					CustContacts des=new CustContacts();
					des.setParnr(design.get("PARNR")==null?"":design.get("PARNR").toString());
					des.setKunnr(design.get("KUNNR")==null?"":design.get("KUNNR").toString());
					des.setName1(design.get("NAME1")==null?"":design.get("NAME1").toString());
					des.setNamev(design.get("NAMEV")==null?"":design.get("NAMEV").toString());
					des.setAbtnr(design.get("ABTNR")==null?"":design.get("ABTNR").toString());
					des.setVtext(design.get("VTEXT")==null?"":design.get("VTEXT").toString());
					des.setTelf1(design.get("TELF1")==null?"":design.get("TELF1").toString());
					des.setAnred(design.get("ANRED")==null?"":design.get("ANRED").toString());
					des.setStatus(design.get("STATUS")==null?"":design.get("STATUS").toString());
					des.setCreateUser(design.get("CREATE_USER")==null?"":design.get("CREATE_USER").toString());
					String createTime=design.get("CREATE_TIME")==null?"":design.get("CREATE_TIME").toString();
					des.setCreateTime(DateTools.stringToDate(createTime));
					des.setUpdateUser(design.get("UPDATE_USER")==null?"":design.get("UPDATE_USER").toString());
					String updateTime=design.get("UPDATE_TIME")==null?"":design.get("UPDATE_TIME").toString();
					des.setUpdateTime(DateTools.stringToDate(updateTime));
					des.setRowStatus(design.get("ROW_STATUS")==null?"":design.get("ROW_STATUS").toString());
					designSet.add(des);
				}
				designMap.put(kunnr, designSet);//存放客户对应的设计师*/

				// 删除已经存在的客户联系人  //排除设计师
				jdbcTemplate.update("delete cust_contacts where kunnr ='"+ kunnr + "' and abtnr<>'0099'");
				jdbcTemplate.update("delete cust_logistics where kunnr ='"+ kunnr + "'");
				//kunnrs.append("'").append(kunnr).append("',");
				Set<String> kunnrSet = new HashSet<String>();
				kunnrSet.add(kunnr);
				List<CustHeader> list=custManager.createQueryByIn(CustHeader.class, "kunnr", kunnrSet);
				oldCustHeaderList.addAll(list);
			}
			//kunnrs.append("''");
			// System.out.println("=====================>" + update);
		}

		//更新
		Set<String> fieldNameSet = new HashSet<String>();//复制时需要排除的字段
		fieldNameSet.add("id");
		fieldNameSet.add("createUser");
		fieldNameSet.add("createTime");
		fieldNameSet.add("updateUser");
		fieldNameSet.add("updateTime");
		fieldNameSet.add("rowStatus");
		for (Iterator iterator = oldCustHeaderList.iterator(); iterator
				.hasNext();) {
			CustHeader oldCustHeader = (CustHeader) iterator.next();
			for (Iterator it2 = custHeaderList.iterator(); it2.hasNext();) {
				CustHeader newCustHeader = (CustHeader) it2.next();
				/*&& newCustHeader.getKtokd().equals(
						oldCustHeader.getKtokd())
						// && newCustHeader.getBukrs().equals(
						// oldCustHeader.getBukrs())
						&& newCustHeader.getVkorg().equals(
								oldCustHeader.getVkorg())
								&& newCustHeader.getVtweg().equals(
										oldCustHeader.getVtweg())*/
				if (newCustHeader.getKunnr().equals(oldCustHeader.getKunnr())) {// 同时存在（更改）&& newCustHeader.getSpart().equals(oldCustHeader.getSpart())
					FieldFunction.copyValue(oldCustHeader, newCustHeader,
							fieldNameSet);
					it2.remove();
					break;
				}
			}
			Set<CustContacts> custContactsSet = oldCustHeader
					.getCustContactsSet();//数据库的联系人
			if (custContactsSet == null) {
				custContactsSet = new HashSet<CustContacts>();
			}
			for (Iterator it2 = custContactsList.iterator(); it2.hasNext();) {
				CustContacts custContacts = (CustContacts) it2.next();//sap同步下来的联系人
				if (custContacts.getKunnr().equals(oldCustHeader.getKunnr())) {
					custContacts.setCustHeader(oldCustHeader);//关联联系人和客户
					custContactsSet.add(custContacts);
					it2.remove();
				}	
			}
			Set<CustLogistics> custLogisticsSet=oldCustHeader.getCustLogisticsSet();
			if(custLogisticsSet == null) {
				custLogisticsSet = new HashSet<CustLogistics>();
			}
			for (Iterator it3 = custLogisticsList.iterator();it3.hasNext();) {
				CustLogistics custLogistics = (CustLogistics) it3.next();
				if(custLogistics.getKunnr().equals(oldCustHeader.getKunnr())) {
					custLogistics.setCustHeader(oldCustHeader);
					custLogisticsSet.add(custLogistics);
					it3.remove();
				}
			}
			/*Set<CustContacts> designerSet=designMap.get(oldCustHeader.getKunnr());//数据库的设计师
			for (Iterator desIt = designerSet.iterator(); desIt.hasNext();) {
				CustContacts custContacts = (CustContacts) desIt.next();//数据库的设计师
				custContacts.setCustHeader(oldCustHeader);//关联联系人和客户
			}
			custContactsSet.addAll(designerSet);*/

			oldCustHeader.setCustContactsSet(custContactsSet);
			oldCustHeader.setCustLogisticsSet(custLogisticsSet);
			//修改内容
			oldCustHeader.setRowStatus("1");//设置为启用状态  SAP里查出来的数据，更新到前端表
			custManager.save(oldCustHeader);// 更新
		}
		/*if (oldCustHeaderList != null && oldCustHeaderList.size() > 0) {
			for (Iterator oldCustIt = oldCustHeaderList.iterator(); oldCustIt.hasNext();) {
				CustHeader custHeader=(CustHeader) oldCustIt.next();
				custHeader.setRowStatus("1");//设置为启用状态
				custManager.save(custHeader);// 更新
			}
		}*/

		//新增
		for (Iterator iterator = custHeaderList.iterator(); iterator.hasNext();) {
			CustHeader custHeader = (CustHeader) iterator.next();//SAP查询过来的custHeader
			for (Iterator it2 = custContactsList.iterator(); it2.hasNext();) {
				CustContacts custContacts = (CustContacts) it2.next();//sap同步下来的联系人
				if (custContacts.getKunnr().equals(custHeader.getKunnr())) {
					custContacts.setCustHeader(custHeader);//设置客户
					if (custHeader.getCustContactsSet() == null) {
						Set<CustContacts> custContactsSet = new HashSet<CustContacts>();
						custContactsSet.add(custContacts);
						custHeader.setCustContactsSet(custContactsSet);
					} else {
						custHeader.getCustContactsSet().add(custContacts);//设置联系人
					}
					it2.remove();
				}
			}
			for (Iterator it3 = custLogisticsList.iterator();it3.hasNext();) {
				CustLogistics custLogistics=(CustLogistics) it3.next();
				if(custLogistics.getKunnr().equals(custHeader.getKunnr())) {
					custLogistics.setCustHeader(custHeader);
					if(custHeader.getCustLogisticsSet() == null) {
						Set<CustLogistics> custLogisticsSet = new HashSet<CustLogistics>();
						custLogisticsSet.add(custLogistics);
						custHeader.setCustLogisticsSet(custLogisticsSet);
					}else {
						custHeader.getCustLogisticsSet().add(custLogistics);
					}
					it3.remove();
				}
			}
		}
		if (custHeaderList != null && custHeaderList.size() > 0) {
			for (Iterator custHeaderIt = custHeaderList.iterator(); custHeaderIt.hasNext();) {
				CustHeader custHeader=(CustHeader) custHeaderIt.next();
				custHeader.setRowStatus("1");//设置为启用状态
				//客户住数据同步优化，同步统一加公司代码3000
				custHeader.setBukrs("3100");
				custManager.save(custHeader);// 新增
			}
		}
		long endTime=System.currentTimeMillis(); //获取结束时间
		System.out.println("同步SAP客户结束时间="+endTime);
		System.out.println("同步运行时间： "+(endTime-startTime)+"ms");

		msg = new Message("同步成功");
		return msg;
	}


	@RequestMapping(value = { "/syncCust2" }, method = RequestMethod.POST)
	@ResponseBody
	public Message syncCust2() throws JCoException {
		Message msg = null;
		JCoDestination connect = SAPConnect.getConnect();
		JCoFunction function = connect.getRepository().getFunction(
				"ZRFC_SD_KH01");
		function.execute(connect);
		// 客户主数据
		JCoTable table = function.getTableParameterList().getTable("IT_TAB1");
		// 联系人
		JCoTable table2 = function.getTableParameterList().getTable("IT_TAB2");
		// SAP查询过来的custHeader
		List<CustHeader> custHeaderList = new ArrayList<CustHeader>();
		Set<String> set = new HashSet<String>();
		if (table.getNumRows() > 0) {
			table.firstRow();
			for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {
				CustHeader custHeader = new CustHeader();
				for (JCoField jCoField : table) {
					Object value = table.getValue(jCoField.getName());
					try {
						BeanUtils.setValue(custHeader, FieldFunction
								.dbField2BeanField(jCoField.getName()), value);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
				set.add(custHeader.getKunnr());
				custHeaderList.add(custHeader);
			}
		}
		// SAP查询过来的CustContacts
		List<CustContacts> custContactsList = new ArrayList<CustContacts>();
		if (table2.getNumRows() > 0) {
			table2.firstRow();
			for (int i = 0; i < table2.getNumRows(); i++, table2.nextRow()) {
				CustContacts custContacts = new CustContacts();
				for (JCoField jCoField : table2) {
					Object value = table2.getValue(jCoField.getName());
					try {
						BeanUtils.setValue(custContacts, FieldFunction
								.dbField2BeanField(jCoField.getName()), value);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
				custContacts.setStatus("1");
				custContactsList.add(custContacts);
			}
		}

		//设置客户的信贷额度
		JCoFunction function2 = connect.getRepository().getFunction(
				"ZRFC_SD_XD01");
		function2.getImportParameterList().setValue("P_KKBER", "3000");
		/*
		 * JCoTable table3 =
		 * function2.getTableParameterList().getTable("S_KUNNR");
		 * table3.appendRow(); table3.setValue("SIGN", "I");
		 * table3.setValue("OPTION", "EQ");
		 * table3.setValue("CUSTOMER_VENDOR_LOW", "LJ57903");
		 */
		function2.execute(connect);
		JCoTable table4 = function2.getTableParameterList().getTable("IT_TAB1");
		if (table4.getNumRows() > 0) {
			table4.firstRow();
			for (int i = 0; i < table4.getNumRows(); i++, table4.nextRow()) {
				Object value = table4.getValue("OBLIG_S");// 剩余信贷额度
				Object KKBER = table4.getValue("KKBER");// 公司代码
				Object KUNNR = table4.getValue("KUNNR");// 客户编号
				for (CustHeader custHeader : custHeaderList) {
					if (KKBER != null
							&& custHeader.getBukrs().equals(KKBER.toString())
							&& KUNNR != null
							&& custHeader.getKunnr().equals(KUNNR.toString())
							&& value != null) {
						custHeader.setXinDai(value.toString());
					}
				}
			}
		}

		List<CustHeader> oldCustHeaderList = new ArrayList<CustHeader>();
		if (set != null && set.size() > 0) {
			//StringBuffer kunnrs = new StringBuffer();
			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				String kunnr = (String) iterator.next();
				// 删除已经存在的客户联系人
				int update = jdbcTemplate
						.update("delete cust_contacts where kunnr ='"+ kunnr + "'");
				//kunnrs.append("'").append(kunnr).append("',");
				Set<String> kunnrSet = new HashSet<String>();
				kunnrSet.add(kunnr);
				List<CustHeader> list=custManager.createQueryByIn(CustHeader.class, "kunnr", kunnrSet);
				oldCustHeaderList.addAll(list);
			}
			//kunnrs.append("''");
			// System.out.println("=====================>" + update);
		}

		//更新
		Set<String> fieldNameSet = new HashSet<String>();//复制时需要排除的字段
		fieldNameSet.add("id");
		fieldNameSet.add("createUser");
		fieldNameSet.add("createTime");
		fieldNameSet.add("updateUser");
		fieldNameSet.add("updateTime");
		fieldNameSet.add("rowStatus");
		for (Iterator iterator = oldCustHeaderList.iterator(); iterator
				.hasNext();) {
			CustHeader oldCustHeader = (CustHeader) iterator.next();
			for (Iterator it2 = custHeaderList.iterator(); it2.hasNext();) {
				CustHeader newCustHeader = (CustHeader) it2.next();
				if (newCustHeader.getKunnr().equals(oldCustHeader.getKunnr())
						&& newCustHeader.getKtokd().equals(
								oldCustHeader.getKtokd())
								// && newCustHeader.getBukrs().equals(
								// oldCustHeader.getBukrs())
								&& newCustHeader.getVkorg().equals(
										oldCustHeader.getVkorg())
										&& newCustHeader.getVtweg().equals(
												oldCustHeader.getVtweg())
												&& newCustHeader.getSpart().equals(
														oldCustHeader.getSpart())) {// 同时存在（更改）
					FieldFunction.copyValue(oldCustHeader, newCustHeader,
							fieldNameSet);
					it2.remove();
				}
			}
			Set<CustContacts> custContactsSet = oldCustHeader
					.getCustContactsSet();//数据库的联系人
			if (custContactsSet == null) {
				custContactsSet = new HashSet<CustContacts>();
			}
			for (Iterator it2 = custContactsList.iterator(); it2.hasNext();) {
				CustContacts custContacts = (CustContacts) it2.next();//sap同步下来的联系人
				if (custContacts.getKunnr().equals(oldCustHeader.getKunnr())) {
					custContacts.setCustHeader(oldCustHeader);//关联联系人和客户
					custContactsSet.add(custContacts);
					it2.remove();
				}
			}
			oldCustHeader.setCustContactsSet(custContactsSet);
		}
		if (oldCustHeaderList != null && oldCustHeaderList.size() > 0) {
			for (Iterator oldCustIt = oldCustHeaderList.iterator(); oldCustIt.hasNext();) {
				CustHeader custHeader=(CustHeader) oldCustIt.next();
				custManager.save(custHeader);// 更新
			}
		}

		//新增
		for (Iterator iterator = custHeaderList.iterator(); iterator.hasNext();) {
			CustHeader custHeader = (CustHeader) iterator.next();//SAP查询过来的custHeader
			for (Iterator it2 = custContactsList.iterator(); it2.hasNext();) {
				CustContacts custContacts = (CustContacts) it2.next();//sap同步下来的联系人
				if (custContacts.getKunnr().equals(custHeader.getKunnr())) {
					custContacts.setCustHeader(custHeader);//设置客户
					if (custHeader.getCustContactsSet() == null) {
						Set<CustContacts> custContactsSet = new HashSet<CustContacts>();
						custContactsSet.add(custContacts);
						custHeader.setCustContactsSet(custContactsSet);
					} else {
						custHeader.getCustContactsSet().add(custContacts);//设置联系人
					}
					it2.remove();
				}
			}
		}
		if (custHeaderList != null && custHeaderList.size() > 0) {
			for (Iterator custHeaderIt = custHeaderList.iterator(); custHeaderIt.hasNext();) {
				CustHeader custHeader=(CustHeader) custHeaderIt.next();
				custManager.save(custHeader);// 新增
			}
			//custManager.save(custHeaderList);// 新增
		}
		msg = new Message("同步成功");
		return msg;
	}

	/**
	 * 根据SaleHeader.id查找对应SaleItem
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/queryUser", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryUser(String pid) {
		StringBuffer sb = new StringBuffer(
				"select t.id || '-' || t.user_name text,t.id from sys_user t inner join Sys_User_Group t2 on t.id=t2.user_id where t2.group_id='gp_drawing' ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
		// formats.put("createTime", new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		List<Map<String, Object>> queryForList = jdbcTemplate.query(sb
				.toString(), params.toArray(), new MapRowMapper(true, formats));
		//System.out.println(queryForList);
		return new JdbcExtGridBean(1, queryForList.size(), queryForList.size(),
				queryForList);

	}
	@RequestMapping(value = "/gainCustInfo", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean gainCustInfo(String user_id) {
		String requestUrl="http://120.77.170.255:8888/Admin/Base/getClientInfo";
		String requestMethod="POST";
		Map<String, String> map=new HashMap<String, String>();
		map.put("user_id", getLoginUserId());
		map.put("cust_stutas", "50");
		List<Map<String, Object>> list = gainCustInfo(requestUrl, requestMethod,map);
		if(list!=null){
			return new JdbcExtGridBean(1, list.size(), list.size(),
					list);
		}
		return null;

	}
	/**
	 *   
	 * @param requestUrl "http://roco_roco.honray.cc/Admin/Base/getClientInfo"
	 * @param requestMethod POST
	 * @param params 
	 * @return
	 */
	public  List<Map<String, Object>> gainCustInfo(String requestUrl,String requestMethod,Map<String,String> params){
		try {
			List<Map<String, Object>> list=null;
			URL url=new URL(requestUrl);
			URLConnection connection = url.openConnection();
			HttpURLConnection urlConnection=(HttpURLConnection) connection;
			String param="";
			String userId="";
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String key = entry.getKey();
				String val = entry.getValue();
				if(key.equals("user_id")){
					userId=val;
				}
				param+=key+"="+val+"&";
			}
			Date date=new Date();
			String token="{\"user_id\":\""+userId+"\",\"time\":"+date.getTime()+"}";
			BASE64Encoder encoder = new BASE64Encoder();
			token=encoder.encode(token.toString().getBytes());
			urlConnection.setRequestProperty("authtoken", token);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestMethod(requestMethod);
			urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
			urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			urlConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			urlConnection.setRequestProperty("Charset", "UTF-8");
			DataOutputStream dos=new DataOutputStream(urlConnection.getOutputStream());
			if(param.length()>0){
				dos.writeBytes(param.substring(0,param.length()-1));
			}
			dos.flush();
			dos.close();
			int resultCode=urlConnection.getResponseCode();
			if(HttpURLConnection.HTTP_OK==resultCode){
				StringBuffer sb=new StringBuffer();
				String readLine=new String();
				BufferedReader responseReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
				while((readLine=responseReader.readLine())!=null){
					sb.append(readLine).append("\n");
				}
				responseReader.close();
				Gson gson=new Gson();
				CustInfo json = gson.fromJson(sb.toString(), CustInfo.class);
				if("SUCCESS".equals(json.getReturn_code())){
					list=new ArrayList<Map<String,Object>>();
					List<Cust> cust = json.getReturn_data();
					Map<String, Object> map=null;
					for (Cust cu : cust) {//第一个list 就是第一个map
						map=new HashMap<String, Object>();
						map.put("cust_id", cu.getCust_id());
						map.put("name", cu.getName());
						map.put("sex", cu.getSex());
						map.put("tel", cu.getTel());
						map.put("address", cu.getAddress());
						map.put("jingShouRen", cu.getJingShouRen());
						String huXin="";
						if(cu.getHuXing()!=null&&!"".equals(cu.getHuXing())){
							huXin=jdbcTemplate.queryForObject("SELECT SD.KEY_VAL FROM SYS_DATA_DICT SD WHERE SD.DESC_ZH_CN='"+cu.getHuXing()+"'", String.class);
						}
						map.put("huXing", huXin);
						map.put("isYangBan", cu.getIsYangBan());
						map.put("isAnZhuang", cu.getIsAnZhuang());
						map.put("floor", cu.getFloor());
						map.put("orderPayFw", cu.getOrderPayFw());
						map.put("custRemarks", cu.getCustRemarks());
						map.put("birthday", cu.getBirthday());
						list.add(map);
					}
				}
				return list;
			}else{

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void processMapObj2(Map<String, Object> m, Object obj) {
		Class class1 = obj.getClass();
		Method[] methods = class1.getMethods();
		Map<String, Method> methodNameMethMap = new HashMap<String, Method>();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String name = method.getName();
			if (name.startsWith("set")) {
				methodNameMethMap.put(ZStringUtils.uppperCase(name.substring(3,
						name.length())), method);
			}
		}
		Set<String> set = methodNameMethMap.keySet();
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			Method method = methodNameMethMap.get(name);
			if (ZStringUtils.isEmpty(m.get(name))) {
				continue;
			}
			if (method != null) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length > 1)
					continue;
				Class<?> name2 = parameterTypes[0];
				String name3 = name2.getName();
				Object value = null;
				if (name3.contains("Double") || name3.contains("double")) {
					value = TypeCaseHelper.convert2Double(m.get(name));
					// value = Double.parseDouble(m.get(name));
				} else if (name3.contains("Integer") || name3.contains("int")) {
					// value = Integer.parseInt(m.get(name));
					value = TypeCaseHelper.convert2Integer(m.get(name));
				} else if (name3.contains("Date")) {
					value = DateTools.strToDate(m.get(name).toString(),
							DateTools.defaultFormat);
				} else if (name3.contains("Long") || name3.contains("long")) {
					value = TypeCaseHelper.convert2Long(m.get(name));
					// value = Long.parseLong(m.get(name));
				} else {
					value = m.get(name);
				}
				try {
					method.invoke(obj, value);
				} catch (Exception e) {
					//System.out.println(m.get(name));
					e.printStackTrace();
				}
			}
		}
	}


}
