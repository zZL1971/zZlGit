package com.main.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.main.domain.sap.SapZstPpRl01;
import com.main.manager.CustManager;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.FieldFunction;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 *
 */
@Controller
@RequestMapping("/main/sapZstPpRl01/*")
public class SapZstPpRl01Controller extends BaseController {

	@Autowired
	private CommonManager commonManager;
	
	@Autowired
    SysTrieTreeDao sysTrieTreeDao;

	private static final Logger logger = LoggerFactory
			.getLogger(SapZstPpRl01Controller.class);

	/**
	 * 列出所有流程模板
	 */

	@RequestMapping(value = { "/query" }, method = RequestMethod.GET)
	public ModelAndView query(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "SapZstPpRl01App");
		return mav;
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryForList(int page, int limit) {
		String werks = this.getRequest().getParameter("werks");
		String date = this.getRequest().getParameter("date");
		String date2 = this.getRequest().getParameter("date2");

		StringBuffer sb = new StringBuffer(
				"select * from SAP_ZST_PP_RL01 where 1=1 ");
		// sql params
		List<Object> params = new ArrayList<Object>();

		if (!StringUtils.isEmpty(werks)) {
			sb.append(" and WERKS like ? ");
			params.add(StringHelper.like(String.valueOf(werks)));
		}
		if (!StringUtils.isEmpty(date)) {
			sb.append(" and WERKS_DATE >= ? ");
			params.add(DateTools.strToDate(date, DateTools.defaultFormat));
		}
		if (!StringUtils.isEmpty(date2)) {
			sb.append(" and WERKS_DATE <= ? ");
			params.add(DateTools.strToDate(date2, DateTools.defaultFormat));
		}
		sb.append(" order by WERKS_DATE ");
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
		formats.put("werksDate", new SimpleDateFormat(DateTools.defaultFormat));
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(), new MapRowMapper(true, formats));

		// for (Map<String, Object> map : queryForList) {
		// Object orderDate = map.get("orderDate");
		// if (orderDate != null) {
		// map.put("orderDate", DateTools.formatDate((Date) orderDate,
		// DateTools.defaultFormat));
		// }
		// }
		//System.out.println(queryForList);
		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}

	@RequestMapping(value = { "/sync" }, method = RequestMethod.POST)
	@ResponseBody
	public Message sync() throws JCoException {
		
		String werks = this.getRequest().getParameter("werks");
		String zyear = this.getRequest().getParameter("zyear");
		
		if(zyear!=null && zyear.trim().length()>0){
			
		}else{
			return new Message("请输入同步工厂的年份");
		}
		
		int update = jdbcTemplate
				.update("delete SAP_ZST_PP_RL01 where substr(WEEK,1,4) = '"
						+ zyear + "' and werks='3110'");
		Message msg = null;
		JCoDestination connect = SAPConnect.getConnect();
		// 同步工厂日历
		JCoFunction function = connect.getRepository().getFunction(
				"ZRFC_PP_RL01");
		function.getImportParameterList().setValue("P_WERKS", werks);
		function.getImportParameterList().setValue("P_YEAR",zyear);
		function.execute(connect);
		// 工厂日历输出表
		JCoTable table = function.getTableParameterList().getTable("IT_TAB");
		List<SapZstPpRl01> sapZstPpRl01List = new ArrayList<SapZstPpRl01>();
		if (table.getNumRows() > 0) {
			table.firstRow();
			for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {
				SapZstPpRl01 sapZstPpRl01 = new SapZstPpRl01();
				for (JCoField jCoField : table) {
					Object value = table.getValue(jCoField.getName());
					try {
						BeanUtils.setValue(sapZstPpRl01, FieldFunction
								.dbField2BeanField(jCoField.getName()), value);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
				sapZstPpRl01List.add(sapZstPpRl01);
			}
		}
		if (sapZstPpRl01List != null && sapZstPpRl01List.size() > 0) {
			commonManager.save(sapZstPpRl01List);
		}
		msg = new Message("同步成功");
		return msg;
	}
	
	/*
	 * 同步板件识别码
	 */
	@RequestMapping(value = { "/syncJsmj" }, method = RequestMethod.POST)
	@ResponseBody
	public Message syncJsmj() throws JCoException {
		
		Message msg = null;
		try {
			int update = jdbcTemplate.update("DELETE SYS_DATA_DICT where trie_id =(select ID from SYS_TRIE_TREE where Key_Val='ZRFC_PP_JSMJ')");
			JCoDestination connect = SAPConnect.getConnect();
			// 同步吸塑标识码
			JCoFunction function = connect.getRepository().getFunction(
					"ZRFC_PP_JSMJ");
			function.execute(connect);

			//结果集
			JCoTable table = function.getTableParameterList().getTable("T_INFO1");
			SysTrieTree sysTrieTree = sysTrieTreeDao.findByKeyVal("ZRFC_PP_JSMJ");
			Set<SysDataDict> dataDicts=new HashSet<SysDataDict>();
			if (table.getNumRows() > 0) {
				table.firstRow();
				for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {
					SysDataDict sysDataDict =new SysDataDict();
					sysDataDict.setKeyVal(table.getString("INFO1"));
					//是否吸塑
					sysDataDict.setType(table.getString("ZSFXS"));
					//是否面积
					sysDataDict.setDescZhCn(table.getString("ZJSMJ"));
					//是否组件
					sysDataDict.setDescEnUs(table.getString("ZSFYX"));
					//是否组件
					sysDataDict.setDescZhTw(table.getString("ZSFSM"));
					sysDataDict.setTrieTree(sysTrieTree);
					dataDicts.add(sysDataDict);
				}
			}
			
			sysTrieTree.setDataDicts(dataDicts);
			commonManager.save(sysTrieTree);
			msg = new Message("同步成功");
		} catch (Exception e) {
			msg=new Message("同步失败");
			e.printStackTrace();
		}
		
		return msg;
	}
}
