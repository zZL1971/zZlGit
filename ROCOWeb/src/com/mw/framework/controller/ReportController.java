package com.mw.framework.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.bean.Range;
import com.mw.framework.bean.SupcanJsonResult;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysReportViewsDao;
import com.mw.framework.domain.SysReportViews;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.util.ShortUrl;
import com.mw.framework.util.StringHelper;
import com.mw.framework.util.UUIDUtils;
import com.mw.framework.utils.ExcelUtil;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultElement;

/**
 * @Project SMSWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.sms.controller.ReportController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-9-15
 * 
 */
@Controller
@RequestMapping("/core/report/*")
public class ReportController extends BaseController {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportController.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	SysReportViewsDao reportViewsDao;

	@RequestMapping(value = { "/{name}" }, method = RequestMethod.GET)
	public ModelAndView index(@PathVariable String name,
			ModelAndView modelAndView) {
		modelAndView.setViewName("core/report");
		modelAndView.getModelMap().put("module", "ReportApp");// 对应app/index/trie.js
		modelAndView.getModelMap().put("moduleTitle", "Report");// 功能页面标题JSP
		modelAndView.getModelMap().put("reportNo", name);// 报表编号

		Document doc = getXML(name);
		if (doc != null) {
			Element node = (Element) doc
					.selectSingleNode("//TreeList/Properties");

			if (node != null) {
				Attribute attribute = node.attribute("Title");
				modelAndView.getModelMap().put("reportName",
						attribute.getText());// 报表名字
			}
		}

		return modelAndView;
	}

	@RequestMapping(value = { "/t/{name}" }, method = RequestMethod.GET)
	@ResponseBody
	public String getTable(@PathVariable String name) {
		Document doc = getXML(name);
		if (doc != null) {
			Element rootElement = doc.getRootElement();
			Node node = doc.selectSingleNode("//TreeList/sql");
			if (node != null) {
				rootElement.remove(node);
			}
			Node node2 = doc.selectSingleNode("//TreeList/Search");
			if (node2 != null) {
				rootElement.remove(node2);
			}

			return doc.asXML();
		}
		return "";
	}

	@RequestMapping(value = { "/s/{name}" }, method = RequestMethod.GET)
	@ResponseBody
	public String getSearchs(@PathVariable String name) {
		Document doc = getXML(name);
		if (doc != null) {
			Node node = doc.selectSingleNode("//TreeList/Search");
			if (node != null) {
				return node.getText();
			}
		}
		return "[]";
	}

	@RequestMapping(value = { "/d/{name}" }, method = RequestMethod.GET)
	@ResponseBody
	public SupcanJsonResult getData(@PathVariable String name) {

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// 获取xml文件
		Document doc = getXML(name);
		if (doc != null) {
			// 找到sql节点
			Element node = (Element) doc.selectSingleNode("//TreeList/sql");
			if (node != null) {
				// SQL依据ORACLE特性，用jdbc写成视图并创建KEY
				String sql = node.getText().trim();
				String vkey = ShortUrl.ShortText(sql)[0];
				// System.out.println(vkey);
				String vname = node.attributeValue("vname");
				SysReportViews findByIdAndVkey = reportViewsDao
						.findByIdAndVkey(vname, vkey);
				if (findByIdAndVkey == null) {
					jdbcTemplate.execute("create or replace view " + vname
							+ " as " + sql);
					SysReportViews one = reportViewsDao.findOne(vname);
					if (one != null) {
						one.setVkey(vkey);
						one.setVsql(sql);
						reportViewsDao.saveAndFlush(one);
					} else {
						reportViewsDao.saveAndFlush(new SysReportViews(vname,
								vkey, sql));
					}
				}

				// sql
				StringBuffer sb = new StringBuffer("select * from " + vname
						+ " where 1=1 ");
				// sql params
				List<Object> params = new ArrayList<Object>();

				Map<String, String[]> parameterMap = super.getRequest()
						.getParameterMap();
				Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();

				// String xx = Range.Option.getValues();
				// System.out.println(xx);
				for (Entry<String, String[]> entry : entrySet) {
					String key = entry.getKey();
					// System.out.println(key);
					boolean result = Pattern.matches("^(I|E)(C|D|N)("
							+ Range.Option.getValues() + ")[A-Za-z0-9_.__]+$",
							key);
					if (result) {
						String sign = key.substring(0, 1), type = key
								.substring(1, 2), option = key.substring(2, 4), field = key
								.substring(4);
						String[] entryValues = entry.getValue();

						Object value = null;
						Object high = null;
						Object[] values = null;
						boolean single = true;
						if (entryValues != null) {
							values = new Object[entryValues.length];
							for (int i = 0; i < entryValues.length; i++) {
								switch (Range.Type.valueOf(type)) {
								case C:
									values[i] = entryValues[i];
									break;
								case D:
									try {
										String[] split = entryValues[i]
												.split(",");
										if (split[0].length() == 10) {
											values[i] = sdf2.parse(split[0]
													+ " 00:00:00");
										} else if (split[0].length() == 19) {
											values[i] = sdf2.parse(split[0]);
										}

										if (split.length == 2) {
											if (split[1].length() == 10) {
												high = sdf2.parse(split[1]
														+ " 23:59:59");
											} else if (split[1].length() == 19) {
												high = sdf2.parse(split[1]);
											}
										}
										// System.out.println("时间格式化后:"+values[i]);
									} catch (ParseException e) {
										e.printStackTrace();
									}
									break;
								case N:
									values[i] = entryValues[i];
									break;
								default:
									break;
								}
							}
							single = values.length == 1 ? true : false;
							value = values[0];

							switch (Range.Option.valueOf(option)) {
							case EQ:
								if (single) {
									switch (Range.Sign.valueOf(sign)) {
									case I:
										sb.append(" and " + field + "=? ");
										params.add(value);
										break;
									case E:
										sb.append(" and " + field + "<>? ");
										params.add(value);
										break;
									default:
										break;
									}

								} else {
									switch (Range.Sign.valueOf(sign)) {
									case I:
										StringBuffer sb2 = new StringBuffer();
										for (int i = 0; i < values.length; i++) {
											sb2.append("'" + values[i] + "'");
											if (i < values.length - 1) {
												sb2.append(",");
											}
										}
										sb.append(" and " + field + " in ? ");
										params.add(sb2.toString());
										break;
									case E:
										StringBuffer sb3 = new StringBuffer();
										for (int i = 0; i < values.length; i++) {
											sb3.append("'" + values[i] + "'");
											if (i < values.length - 1) {
												sb3.append(",");
											}
										}
										sb.append(" and " + field
												+ " not in ? ");
										params.add(sb3.toString());
										break;
									default:
										break;
									}
								}
								break;
							case GE:
								sb.append(" and " + field + " >= ? ");
								params.add(value);
								break;
							case LE:
								sb.append(" and " + field + " <= ? ");
								params.add(value);
								break;
							case GT:
								sb.append(" and " + field + " > ? ");
								params.add(value);
								break;
							case LT:
								sb.append(" and " + field + " < ? ");
								params.add(value);
								break;
							case NE:
								sb.append(" and " + field + " <> ? ");
								params.add(value);
								break;
							case BT:
								String[] splitValue = value.toString().split(
										",");
								switch (Range.Type.valueOf(type)) {
								case C:
									sb.append(" and " + field
											+ " between ?  and ? ");
									params.add(splitValue[0]);
									params.add(splitValue[1]);
								case D:
									sb.append(" and " + field
											+ " between ?  and ? ");
									params.add(value);
									params.add(high);
									break;
								case N:
									sb.append(" and " + field
											+ " between ?  and ? ");
									params.add(splitValue[0]);
									params.add(splitValue[1]);
									break;
								default:
									break;
								}

								break;
							case CP:
								if (single) {
									switch (Range.Sign.valueOf(sign)) {
									case I:
										sb.append(" and " + field + " like ? ");
										params.add(StringHelper.like(String
												.valueOf(value)));
										break;
									case E:
										sb.append(" and " + field
												+ " not like ? ");
										params.add(StringHelper.like(String
												.valueOf(value)));
										break;
									default:
										break;
									}
								} else {

									System.out.println("【高级查询】：【需向管理员申请权限!】");
									/*
									 * Predicate[] conditions =new
									 * Predicate[values.length]; switch
									 * (Range.Sign.valueOf(sign)) { case I: for
									 * (int i =0;i<values.length;i++) {
									 * conditions[i] =
									 * criteriaBuilder.like(path,
									 * StringHelper.like
									 * (String.valueOf(values[i]))); }
									 * 
									 * break; case E: for (int i
									 * =0;i<values.length;i++) { conditions[i] =
									 * criteriaBuilder.notLike(path,
									 * StringHelper
									 * .like(String.valueOf(values[i]))); }
									 * break; default: break; } condition =
									 * criteriaBuilder.or(conditions);
									 */
								}
								break;
							case IS:
								break;
							default:
								break;
							}
						}

					}
				}

				// System.out.println(sb.toString());
				// System.out.println(params);
				List<Map<String, Object>> queryForList = jdbcTemplate
						.queryForList(sb.toString(), params.toArray());
				// System.out.println(queryForList.size());
				return new SupcanJsonResult(queryForList);

			}
		}

		return new SupcanJsonResult();
	}

	/**
	 * Excel导出
	 * 
	 * @param resource
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/exportExcel/{resource}" }, method = {
			RequestMethod.GET, RequestMethod.POST })
	public void exportExcel(@PathVariable String resource,
			HttpServletRequest request, HttpServletResponse response) {
		Message msg = null;
		Document doc = getXMLForGrid(resource);
		LinkedHashMap<String, String> headMap = new LinkedHashMap<String, String>();
		List<DefaultElement> headNodes = doc.selectNodes("//grid/head/column");
		for (int i = 0; i < headNodes.size(); i++) {
			Element element = (Element) headNodes.get(i);
			String dataIndex = element.attributeValue("dataIndex");
			String text = element.attributeValue("text");
			if (dataIndex != null && text != null && !"id".equals(dataIndex)) {
				String fiedl = getFieldName(dataIndex);
				if("ORDER_TYPE".equals(fiedl)||"ZZEBMS".equals(fiedl)||"ZZEZXS".equals(fiedl)) {
					continue;
				}
				headMap.put(fiedl, text);
				System.out.println("表头====" + fiedl + "------------" + text);
			}
		}
		String excelName = request.getParameter("excelName");
		msg = getDataByExcel(resource, request);
		if (msg.getSuccess()) {
			List<Map<String, Object>> datas = (List<Map<String, Object>>) msg
					.getObj();
			try {

				ExcelUtil
						.createExcel(null, excelName, headMap, datas, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				PrintWriter out = response.getWriter();// 获取PrintWriter输出流
				response.setHeader("content-type", "text/html;charset=UTF-8");
				out.write("<meta http-equiv='content-type' content='text/html;charset=UTF-8'/>");
				String title = "<title>" + excelName + "Excel导出</title>";
				String str = "<br/><br/><center><H2>" + msg.getErrorMsg()
						+ "</H2></center>";
				out.write(title + str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据属性返回数据字段名 createDate REATE_DATE
	 * 
	 * @param dataIndex
	 * @return
	 */
	protected String getFieldName(String dataIndex) {
		String name = "";
		char[] cs = dataIndex.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			String str = String.valueOf(cs[i]);
			boolean bool = Character.isUpperCase(cs[i]);
			if (bool) {
				name = name + "_" + str;
			} else {
				name = name + str;
			}
		}
		return name.toUpperCase();
	}

	/**
	 * 查询数据数据
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Message getDataByExcel(String xmlName, HttpServletRequest request) {

		Object object = super.getObjectForCache(Constants.TM_GRID_SQL, xmlName);
		Element node = null;
		String vname = null;
		List<Element> formats = null;
		if (object != null) {
			Map<String, Object> map = (Map<String, Object>) object;
			node = (Element) map.get("node");
			vname = (String) map.get("vname");
		} else {
			Document doc = super.getXMLForGrid(xmlName);
			if (doc == null) {
				return new Message("找不到XML文件：" + xmlName);
			}
			node = (Element) doc.selectSingleNode("//grid/sql");
			formats = doc.selectNodes("//grid/sql/format");
			// SQL依据ORACLE特性，用jdbc写成视图并创建KEY
			String sql = node.getText().trim();

			String vkey = ShortUrl.ShortText(sql)[0];
			// System.out.println(vkey);
			vname = "xv_" + node.attributeValue("vname");
			SysReportViews findByIdAndVkey = reportViewsDao.findByIdAndVkey(
					vname, vkey);
			if (findByIdAndVkey == null) {
				jdbcTemplate.execute("create or replace view " + vname + " as "
						+ sql);
				SysReportViews one = reportViewsDao.findOne(vname);
				if (one != null) {
					one.setVkey(vkey);
					one.setVsql(sql);
					reportViewsDao.saveAndFlush(one);
				} else {
					reportViewsDao.saveAndFlush(new SysReportViews(vname, vkey,
							sql));
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("vname", vname);
			map.put("node", node);
			super.setObjectForCache(Constants.TM_GRID_SQL, xmlName, map);
		}

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if (node != null) {
			// sql
			StringBuffer sb = new StringBuffer("select * from " + vname
					+ " where 1=1 ");
			// sql params
			List<Object> params = new ArrayList<Object>();

			Map<String, String[]> parameterMap = request.getParameterMap();
			Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();

			// String xx = Range.Option.getValues();
			// System.out.println(xx);
			for (Entry<String, String[]> entry : entrySet) {
				String key = entry.getKey();
				// System.out.println(key);
				boolean result = Pattern.matches("^(I|E)(C|D|N)("
						+ Range.Option.getValues() + ")[A-Za-z0-9_.__]+$", key);
				if (result) {
					String sign = key.substring(0, 1), type = key.substring(1,
							2), option = key.substring(2, 4), field = key
							.substring(4);
					String[] entryValues = entry.getValue();

					// field转回数据库模式
					field = StringHelper.toColumnName(field);

					Object value = null;
					Object high = null;
					Object[] values = null;
					boolean single = true;
					if (entryValues != null) {
						values = new Object[entryValues.length];
						for (int i = 0; i < entryValues.length; i++) {
							switch (Range.Type.valueOf(type)) {
							case C:
								values[i] = entryValues[i];
								break;
							case D:
								try {
									String[] split = entryValues[i].split(",");
									if (split[0].length() == 10) {
										values[i] = sdf2.parse(split[0]
												+ " 00:00:00");
									} else if (split[0].length() == 19) {
										values[i] = sdf2.parse(split[0]);
									}

									if (split.length == 2) {
										if (split[1].length() == 10) {
											high = sdf2.parse(split[1]
													+ " 23:59:59");
										} else if (split[1].length() == 19) {
											high = sdf2.parse(split[1]);
										}
									}
									// System.out.println("时间格式化后:"+values[i]);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								break;
							case N:
								values[i] = entryValues[i];
								break;
							default:
								break;
							}
						}
						single = values.length == 1 ? true : false;
						value = values[0];

						switch (Range.Option.valueOf(option)) {
						case EQ:
							if (single) {
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and " + field + "=? ");
									params.add(value);
									break;
								case E:
									sb.append(" and " + field + "<>? ");
									params.add(value);
									break;
								default:
									break;
								}

							} else {
								switch (Range.Sign.valueOf(sign)) {
								case I:
									StringBuffer sb2 = new StringBuffer();
									for (int i = 0; i < values.length; i++) {
										sb2.append("'" + values[i] + "'");
										if (i < values.length - 1) {
											sb2.append(",");
										}
									}
									sb.append(" and " + field + " in ? ");
									params.add(sb2.toString());
									break;
								case E:
									StringBuffer sb3 = new StringBuffer();
									for (int i = 0; i < values.length; i++) {
										sb3.append("'" + values[i] + "'");
										if (i < values.length - 1) {
											sb3.append(",");
										}
									}
									sb.append(" and " + field + " not in ? ");
									params.add(sb3.toString());
									break;
								default:
									break;
								}
							}
							break;
						case GE:
							sb.append(" and " + field + " >= ? ");
							params.add(value);
							break;
						case LE:
							sb.append(" and " + field + " <= ? ");
							params.add(value);
							break;
						case GT:
							sb.append(" and " + field + " > ? ");
							params.add(value);
							break;
						case LT:
							sb.append(" and " + field + " < ? ");
							params.add(value);
							break;
						case NE:
							sb.append(" and " + field + " <> ? ");
							params.add(value);
							break;
						case BT:
							String[] splitValue = value.toString().split(",");
							switch (Range.Type.valueOf(type)) {
							case C:
								sb.append(" and " + field
										+ " between ?  and ? ");
								params.add(splitValue[0]);
								params.add(splitValue[1]);
							case D:
								sb.append(" and " + field
										+ " between ?  and ? ");
								params.add(value);
								params.add(high);
								break;
							case N:
								sb.append(" and " + field
										+ " between ?  and ? ");
								params.add(splitValue[0]);
								params.add(splitValue[1]);
								break;
							default:
								break;
							}

							break;
						case CP:
							if (single) {
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and " + field + " like ? ");
									params.add(StringHelper.like(String
											.valueOf(value)));
									break;
								case E:
									sb.append(" and " + field + " not like ? ");
									params.add(StringHelper.like(String
											.valueOf(value)));
									break;
								default:
									break;
								}
							} else {

								System.out.println("【高级查询】：【需向管理员申请权限!】");
								/*
								 * Predicate[] conditions =new
								 * Predicate[values.length]; switch
								 * (Range.Sign.valueOf(sign)) { case I: for (int
								 * i =0;i<values.length;i++) { conditions[i] =
								 * criteriaBuilder.like(path,
								 * StringHelper.like(String
								 * .valueOf(values[i]))); }
								 * 
								 * break; case E: for (int i
								 * =0;i<values.length;i++) { conditions[i] =
								 * criteriaBuilder.notLike(path,
								 * StringHelper.like
								 * (String.valueOf(values[i]))); } break;
								 * default: break; } condition =
								 * criteriaBuilder.or(conditions);
								 */
							}
							break;
						case IS:
							break;
						case MQ:
							if (single) {
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and (" + field + " is null or "
											+ field + "=?) ");
									params.add(value);
									break;
								case E:
									sb.append(" and (" + field + " is null or "
											+ field + "<>?) ");
									params.add(value);
									break;
								default:
									break;
								}
							} else {
								System.out
										.println("eg. and (a.assignee is null or a.assignee='admin') 暂只支持单个参数模式");
							}
							break;
						default:
							break;
						}
					}

				}
			}
			logger.info(sb.toString());
			/*
			 * String
			 * countSql="SELECT COUNT(1) DATA_SIZE FROM ( "+sb.toString()+" )";
			 * List<Map<String, Object>> dataSize =
			 * jdbcTemplate.queryForList(countSql.toString(),params.toArray());
			 * Integer
			 * count=Integer.parseInt(dataSize.get(0).get("DATA_SIZE").toString
			 * ()); if(count>50000){ return new
			 * Message("ROPORT-EXCEL-500","数据导出不能超过50000条,当前导出数据为："+count); }
			 */
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(
					sb.toString(), params.toArray());
			Message msg = new Message(queryForList);
			msg.setObj(queryForList);
			return msg;

		}
		return null;
	}

	/**
	 * Excel导出
	 * 
	 * @param resource
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/exportExcel2/{resource}" }, method = {
			RequestMethod.GET, RequestMethod.POST })
	public void exportSaleOutputExcel(@PathVariable String resource,
			HttpServletRequest request, HttpServletResponse response) {
		Message msg = null;
		LinkedHashMap<String, String> headMap = new LinkedHashMap<String, String>();
		Map<String, String[]> map = request.getParameterMap();
		String url = "";
		StringBuilder queryParmas = new StringBuilder();
		for (String key : map.keySet()) {
			String[] value = (String[]) map.get(key);
			String columnName;
			Pattern pattern = Pattern.compile("[A-Z]");
			//请求参数的获取
			if ("requestUrl".equals(key)) {
				url = value[0];
			} else if (key.startsWith("queryICCP") ||key.startsWith("queryIDEQ")) {
				if(key.startsWith("queryICCP")){
				columnName = key.split("queryICCP")[1];
				}else{
					columnName = key.split("queryIDEQ")[1];
				}
				if ("userKunnr".equals(columnName)) {
					//根据用户邦定的经销商代码去查询自己店面的出车情况
					if (!("admin".equals(this.getLoginUserId())) &&(value != null && value[0] != null
							&& !StringUtils.isEmpty(value[0]))) {
						queryParmas.append(" and " + "kunnr" + "='" + value[0]
								+ "' ");
					}
				} else {
					Matcher matcher = pattern.matcher(columnName);
					if (matcher.find()) {
						int index = columnName.indexOf(matcher.group());
						String before = columnName.substring(0, index);
						String after = columnName.substring(index);
						columnName=before+"_"+after;
					} 
					//sap编号和订单编号都可以用模糊查询
					if("sap_Code".equals(columnName) || "order_Code".equals(columnName)){
					String[] v=value[0].split(",");
					StringBuilder sb=new StringBuilder();
					for(int index=0;index<v.length;index++){
						if(index!=0){
							sb.append(" or ");
						}
						sb.append(columnName+" like '%"+v[index]+"%' ");
					}
					queryParmas.append(" and (" +sb.toString()+")");
					}else
					if("start_Date".equals(columnName) || "end_Date".equals(columnName)){
						if("start_Date".equals(columnName)){
							queryParmas.append("and output_time>=date'"+value[0]+"'");
						}else{
							queryParmas.append("and output_time<=date'"+value[0]+"'");
						}
					}
					else{
						queryParmas.append(" and "+columnName+" like '%"+value[0]+"%'");
					}

				}
			} else {
				Matcher matcher = pattern.matcher(key);
				if (matcher.find()) {
					int index = key.indexOf(matcher.group());
					String before = key.substring(0, index);
					String after = key.substring(index);
					columnName = before + "_" + after;
				} else {
					columnName = key;
				}
				headMap.put(columnName, value[0]);
			}
		}
		headMap.remove("1");
		headMap.remove("name");
		headMap.remove("order_Code");
		//Exceltable的内容
		List<Map<String, Object>> queryList = jdbcTemplate
				.queryForList("select t.*, (select desc_zh_cn from sys_data_dict sdd where sdd.trie_id='Dv81TqVx7qC4AaGJfCCAQp' and sdd.key_val=(select mh.matkl from material_head mh where mh.id=(select si.material_head_id from sale_item si where order_code_posex=(trim(t.order_code)||trim((case when length(t.posex)>5 then to_char(substr(t.posex,0,length(t.posex)-1),'0000') else to_char(t.posex,'0000') end)))))) as product_NAME, (select b.address from terminal_client b where sale_id = (select sh.id from sale_header sh where sh.order_code = t.order_code)) as cust_address, (t.parts_sum + t.shutter_sum + t.sliddoor_sum + t.plastic_sum + t.metals_sum + t.backboard_sum + t.carcase_sum + t.decoration_sum + t.general_sum + t.ARTIZAN_SUM + t.STAGEP_SUM+t.ELECTRIC_SUM+t.STONE_SUM) as total, rownum as rn from SALE_SHIPMENT_LOG t where 1 = 1"
						+ queryParmas.toString()+" order by order_code");
		//Exceltitle的内容
		List<Map<String, Object>> excelHeadContentMap = jdbcTemplate
				.queryForList("select cust_name, cust_phone, consignee, consignee_phone, (select ch.name1 from cust_header ch where kunnr = (select song_da_fang from sale_header sh where sh.order_code =  h.order_code and rownum = 1) and rownum = 1) as song_da_fang,(select cc.telf1 from cust_contacts cc where kunnr = (select song_da_fang from sale_header sh where sh.order_code =  h.order_code and rownum = 1) and rownum = 1) as song_da_fang_tel, (select ch.street from cust_header ch where kunnr = (select song_da_fang from sale_header sh where sh.order_code =  h.order_code and rownum = 1) and rownum = 1) as street from sale_shipment_log h where 1 = 1"
						+ queryParmas.toString() + " and rownum=1");
		List<String> excelHeadContent = new ArrayList<String>();
		
		Map<String,List<String>> orderHeadMsg=new HashMap<String,List<String>>();
		Map<String,List<Map<String,Object>>> contentMap=new HashMap<String, List<Map<String,Object>>>();
		Map<String,Map<String,Integer>> countMsg=new HashMap<String, Map<String,Integer>>();
		//从table 信息中获取到子title 信息
		for (Map<String, Object> m : queryList) {
			String name=m.get("name")==null?"":m.get("name").toString();
			if(!orderHeadMsg.containsKey(m.get("name").toString())){
				List<String> orderMsg=new ArrayList<String>();
				orderMsg.add(name);
				orderMsg.add(m.get("order_code")==null?"":"订单号:"+m.get("order_code").toString());
				orderMsg.add(m.get("cust_name")==null?"":"姓名:"+m.get("cust_name").toString());
				orderMsg.add(m.get("cust_phone")==null?"":"电话:"+m.get("cust_phone").toString());
				orderMsg.add(m.get("cust_address")==null?"":"安装地址:"+m.get("cust_address").toString());
				m.remove("name");
				m.remove("order_code");
				m.remove("cust_address");
				orderHeadMsg.put(name,orderMsg);
				
			}
			if(!contentMap.containsKey(name)){
				List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
				list.add(m);
				contentMap.put(name, list);
			}else{
				contentMap.get(name).add(m);
			}
			Map<String,Integer> countMap;
			if(!countMsg.containsKey(name)){
				 countMap=new HashMap<String, Integer>();
			}else{
				 countMap=countMsg.get(name);
			}
			for (String key : m.keySet()) {
				if(!"POSEX".equals(key.toUpperCase())){
				try{
					if(countMap.containsKey(key)){
						countMap.put(key, countMap.get(key)+Integer.parseInt(m.get(key).toString()));
					}else{
						countMap.put(key,Integer.parseInt(m.get(key).toString()));
					}
				}catch(NumberFormatException e){
					continue;
				}catch(NullPointerException npe){
					continue;
				}
				}
			}
			countMsg.put(name, countMap);
		}
		
		for (Map<String, Object> m : excelHeadContentMap) {
			//String custName=m.get("cust_name")==null?"":m.get("cust_name").toString();
			String consignee=m.get("consignee")==null?"":m.get("consignee").toString();
			//String custPhone=m.get("cust_phone")==null?"":m.get("cust_phone").toString();
			String consigneePhone=m.get("consignee_phone")==null?"":m.get("consignee_phone").toString();
			String songDaFang=m.get("song_da_fang")==null?"":m.get("song_da_fang").toString();
			String songDaFangTel=m.get("song_da_fang_tel")==null?"":m.get("song_da_fang_tel").toString();
			String street=m.get("street")==null?"":m.get("street").toString();
			//excelHeadContent.add("客户名字:" + custName
			//		+ "       " + "客户电话:" + custPhone);
			excelHeadContent.add("收件人名字:" + consignee
					+ "       " + "收件人电话:"
					+ consigneePhone);
			excelHeadContent.add("物流园:" + songDaFang
					+ "       " + "物流电话:" + songDaFangTel+"       " + "物流地址:" + street);
		}
		try {
			ExcelUtil.createShipMentExcel(excelHeadContent, "出货清单", headMap, orderHeadMsg,contentMap,countMsg,
					response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/syncShim",method = RequestMethod.POST)
	@ResponseBody
	public Message syncShim(Integer days) throws JCoException {
		Message msg = null;
		Date date=new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		JCoDestination connect = SAPConnect.getConnect();
		JCoFunction function = connect.getRepository().getFunction(
				"ZRFC_PP_CH01");
		Calendar firstCalendar=Calendar.getInstance();
		Calendar secondCalendar=Calendar.getInstance();
		int time=(days)*-1;
		firstCalendar.setTime(date);
		secondCalendar.setTime(date);
		secondCalendar.add(Calendar.DATE,time);
		//String firstDate = format.format(firstCalendar.getTime());
		//long secondTime = secondCalendar.getTime().getTime();
		long firstTime = firstCalendar.getTime().getTime();
		while(secondCalendar.getTime().getTime()<=firstTime) {
			String secondDate = format.format(secondCalendar.getTime());
			System.out.println(secondDate);
			JCoParameterList importParameterList = function
					.getImportParameterList();
			importParameterList.setValue("S_ERDAT",secondDate);
			function.execute(connect);
			JCoTable table = function.getTableParameterList().getTable("EX_T_CH01");
			for (int i = 0; i < table.getNumRows(); i++,table.nextRow()) {
				Object erdat = table.getValue("ERDAT");//记录建立日期
				Object vbeln = table.getValue("VBELN");//销售和分销凭证号
				Object posex = table.getValue("POSEX");//销售和分销凭证的项目号
				Object ztyis = table.getValue("ZTYIS");//通用码数量
				Object zguis = table.getValue("ZGUIS");//柜身数量
				Object zmens = table.getValue("ZMENS");//门板数量
				Object zbeis = table.getValue("ZBEIS");//背板数量
				Object zpeis = table.getValue("ZPEIS");//配件数量
				Object zwujs = table.getValue("ZWUJS");//五金数量
				Object zyims = table.getValue("ZYIMS");//移门数量
				Object zxiss = table.getValue("ZXISS");//吸塑门板数量
				Object zzsjs = table.getValue("ZZSJS");//装饰件数量
				Object name1 = table.getValue("NAME1");//名称 1
				Object zzname = table.getValue("ZZNAME");//客户姓名
				Object zzphon = table.getValue("ZZPHON");//客户电话
				Object zggjs = table.getValue("ZGGJS");//工匠线数量
				Object zdaos = table.getValue("ZDAOS");//道具数量
				Object appliances = table.getValue("ZAPPLIANCES");//电器
				Object zstonetab = table.getValue("ZSTONETAB");//石台
//				Object zbujs = table.getValue("ZTYIS");//补购
				Object zjian = table.getValue("ZJIAN");//装车单号
				logger.info((String)vbeln);
				if(erdat != null) {
					//获取收货人和联系方式
					List<Map<String, Object>> custContactsList = jdbcTemplate.queryForList("SELECT C1.NAMEV," + 
							"       C1.TELF1," + 
							"       C1.KUNNR," + 
							"       (SELECT CH.NAME1 FROM CUST_HEADER CH WHERE CH.KUNNR = C1.KUNNR) AS NAME1" + 
							"  FROM CUST_CONTACTS C1" + 
							" WHERE C1.ID = (SELECT NVL((SELECT C.ID" + 
							"                             FROM CUST_CONTACTS C" + 
							"                            WHERE C.ABTNR = '0008'" + 
							"                              AND C.KUNNR = SH.SHOU_DA_FANG" + 
							"                              AND ROWNUM = 1)," + 
							"                           (SELECT C.ID" + 
							"                              FROM CUST_CONTACTS C" + 
							"                             WHERE C.ABTNR = '0001'" + 
							"                               AND C.KUNNR = SH.SHOU_DA_FANG" + 
							"                               AND ROWNUM = 1)) AS ID" + 
							"                  FROM SALE_HEADER SH" + 
							"                  LEFT JOIN SALE_LOGISTICS SL" + 
							"                    ON SH.ID = SL.PID" + 
							"                 WHERE SL.SAP_CODE = ?)",vbeln);
					String namev = "";
					String telf1 = "";
					String kunnr = "";
					String kunnrName = "";
					for (Map<String, Object> custContacts : custContactsList) {
						namev = typeResolver(custContacts.get("NAMEV"));
						telf1 = typeResolver(custContacts.get("TELF1"));
						kunnr = typeResolver(custContacts.get("KUNNR"));
						kunnrName = typeResolver(custContacts.get("NAME1"));
					}
					StringBuffer insertSql= null;
					try {
						Integer repeatNum = jdbcTemplate.queryForObject("select COUNT(1) from SALE_SHIPMENT_LOG ssl where ssl.sap_code=? and ssl.posex=? AND SSL.ZJIAN=?", Integer.class,vbeln,posex,zjian);
						
						if(repeatNum<=0) {
							System.out.println(vbeln);
							String orderCode = jdbcTemplate.queryForObject("SELECT SH.ORDER_CODE FROM SALE_HEADER SH LEFT JOIN SALE_LOGISTICS SL ON SH.ID=SL.PID WHERE SL.SAP_CODE=?",String.class,vbeln);
							if(orderCode!=null) {
								insertSql=new StringBuffer(""
										+ "insert into SALE_SHIPMENT_LOG("
										+ "id,create_time,create_user,"
										+ "row_status,update_time,"
										+ "update_user,BACKBOARD_SUM,"
										+ "CARCASE_SUM,CUST_NAME,CUST_PHONE,"
										+ "DECORATION_SUM,GENERAL_SUM,METALS_SUM,"
										+ "NAME,ORDER_CODE,OUTPUT_TIME,PARTS_SUM,"
										+ "PLASTIC_SUM,POSEX,SAP_CODE,SHUTTER_SUM,"
										+ "SLIDDOOR_SUM,consignee,consignee_phone,"
										+ "kunnr,kunnr_name,ZJIAN,ARTIZAN_SUM,STAGEP_SUM,ELECTRIC_SUM,STONE_SUM) VALUES(");
								insertSql.append("'"+UUIDUtils.base58Uuid()+"',").append("SYSDATE,").append("'admin',");
								insertSql.append("1,").append("SYSDATE,");
								insertSql.append("'admin',").append(zbeis+",");
								insertSql.append(zguis+",").append("'"+zzname+"',").append("'"+zzphon+"',");
								insertSql.append(zzsjs+",").append(ztyis+",").append(zwujs+",");
								insertSql.append("'"+name1+"',").append("'"+orderCode+"',").append("to_date("+format.format(erdat)+",'yyyymmdd'),").append(zpeis+",");
								insertSql.append(zxiss+",").append("'"+posex+"',").append("'"+vbeln+"',").append(zmens+",");
								insertSql.append(zyims+",").append("'"+namev+"',").append("'"+telf1+"',");
								insertSql.append("'"+kunnr+"',").append("'"+kunnrName+"',").append("'"+zjian+"',");
								insertSql.append(zggjs+",").append(zdaos+",").append(appliances+",").append(zstonetab+")");
								
								//System.out.println(insertSql.toString());
								jdbcTemplate.update(insertSql.toString());
							}
						}
					}catch(EmptyResultDataAccessException e) {
						
						logger.error(e.getMessage(), e);
						if(insertSql != null)
							logger.error(insertSql.toString());
						continue;
					}
				}
			}
			secondCalendar.add(Calendar.DATE, 1);
		}
		return msg;
	}
	private static String typeResolver(Object obj) {
		if(obj!=null) {
			return String.valueOf(obj);
		}
		return null;
	}
}

