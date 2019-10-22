/**
 *
 */
package com.mw.framework.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.bean.Range;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysReportViewsDao;
import com.mw.framework.domain.OperationLog;
import com.mw.framework.domain.SysReportViews;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.DataModel;
import com.mw.framework.model.ExtDataField;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.util.ShortUrl;
import com.mw.framework.util.StringHelper;
import com.mw.framework.util.annotation.FieldMeta;
import com.mw.framework.util.annotation.SortableField;

import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.ExtjsGridController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-13
 *
 */
@Controller
@RequestMapping("/core/ext/grid/*")
public class ExtjsGridController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(ExtjsGridController.class);
	
	@Autowired
	SysReportViewsDao reportViewsDao;
	
	@Autowired
	CommonManager commonManager;
	
	private Configuration config;
	
	/**
	 * 获取配置xml
	 * @param xmlName
	 * @param model
	 * @return 
	 */
	protected NodeModel getParse(String xmlName){
		try {
			URL resource = Thread.currentThread().getContextClassLoader().getResource("");
			File file1 = new File(resource.getPath()+"/grid/"+xmlName+".xml");
			return NodeModel.parse(file1);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取配置xml
	 * @param xmlName
	 * @param model
	 */
	protected void getParse(String xmlName,ModelMap model){
		try {
			//Object file = super.getObjectForCache(Constants.TM_GRID_PARSE, xmlName);
			NodeModel parse =null;
			//if(file==null){
				//System.out.println("****加载xml文件*****");
				URL resource = Thread.currentThread().getContextClassLoader().getResource("");
				File file1 = new File(resource.getPath()+"/grid/"+xmlName+".xml");
				parse = NodeModel.parse(file1);
				//super.setObjectForCache(Constants.TM_GRID_PARSE,xmlName, file1);
			//}else{
			//	parse = NodeModel.parse((File)file);
			//}
			model.put("doc",parse);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public Configuration getConfig() throws IOException{
		
		if(config!=null){
			return config;
		}
		config = new Configuration();
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
		String path = url.getPath();
		path = path.substring(0,path.lastIndexOf("classes/"))+"ftl";
		config.setDirectoryForTemplateLoading(new File(path));
		config.setDefaultEncoding("UTF-8");
		config.setLocale(Locale.CHINESE);
		config.setClassicCompatible(true);//处理空值为空字符串
		
		return config;
	}
	
	public String getTemplateForCache(String space,String xmlName,String ftl) throws IOException, TemplateException{
		/*String objectForCache = (String) super.getObjectForCache(space, xmlName);
		if(objectForCache!=null){
			return objectForCache;
		}else{*/
			
			Map<String, Object> root = new HashMap<String, Object>();
			Template template = this.getConfig().getTemplate(ftl+".ftl");
			NodeModel parse = getParse(xmlName);
			root.put("doc", parse);
			Writer out = new StringWriter(2048);
			template.process(root, out);
			
			super.setObjectForCache(space,xmlName, out.toString());
			
			return out.toString();
		/*}*/
	}
	
	@RequestMapping(value = {"/execModuleData/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message execModuleData(@PathVariable String xmlName) throws IOException, TemplateException{
		Map<String, Object> root = new HashMap<String, Object>();
		Template template = this.getConfig().getTemplate("dataModule.ftl");
		
		//删除缓存数据
		super.deleteObjectForCache(Constants.TM_GRID_SEARCHFORM, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_COLUMN, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_FORM, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_CONFIG, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_MODEL, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_SQL, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_GANTMODULE, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_TREESQL, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_TREEMODEL, xmlName);
		super.deleteObjectForCache(Constants.TM_GRID_VALIDATION, xmlName);
		
//		super.deleteObjectForCache(Constants.CACHE_LIST_TRIE_DICT, xmlName);
//		super.deleteObjectForCache(Constants.TRIE_NAMESPACE, xmlName);
		
		//search
		String search = getTemplateForCache(Constants.TM_GRID_SEARCHFORM, xmlName,"gridSearch");
		//columns
		String columns = getTemplateForCache(Constants.TM_GRID_COLUMN, xmlName,"grid");
		//form
		String form = getTemplateForCache(Constants.TM_GRID_FORM, xmlName,"gridForm");
		//config
		JSONObject config = getConfigForXML(xmlName);
		
		String modelType = config.get("modelType")==null?"view":config.get("modelType").toString();
		//model
		Message modelForClassName = getModelForClassName(modelType,xmlName);
		
		//validations
		String validations = getTemplateForCache(Constants.TM_GRID_VALIDATION, xmlName,"gridValid");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		String model = mapper.writeValueAsString(modelForClassName.get("data")); 
		 
		root.put("search", search);
		root.put("columns", columns);
		root.put("form", form);
		root.put("config", config.toString());
		root.put("model", model);
		root.put("xmlName", xmlName);
		root.put("validations", validations);
		
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
		String path = url.getPath();
		
		path = path.substring(1,path.lastIndexOf("WEB-INF/"))+"/app/data/"+xmlName+".js";
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");//转换流可以指定编码表
		template.process(root, out);
		out.flush();
		out.close();
		return new Message("ok！");
	}
	
	/**
	 * 获取vaild层
	 * @param xmlName
	 * @param model
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	@RequestMapping(value = {"/vaild/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String getValids(@PathVariable String xmlName) throws IOException, TemplateException{
		
		return getTemplateForCache(Constants.TM_GRID_VALIDATION, xmlName,"gridValid");
		
	}
	
	/**
	 * 获取columns层
	 * @param xmlName
	 * @param model
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	@RequestMapping(value = {"/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String getFields(@PathVariable String xmlName) throws IOException, TemplateException{
		return getTemplateForCache(Constants.TM_GRID_COLUMN, xmlName,"grid");
	}
	
	/**
	 * 获取treemodel层
	 * @param xmlName
	 * @param model
	 * @return
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	@RequestMapping(value = {"/treemodel/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String getFieldsForTreeGrid(@PathVariable String xmlName) throws IOException, TemplateException{
		return getTemplateForCache(Constants.TM_GRID_TREEMODEL, xmlName,"treemodel");
	}
	
	/**
	 * 获取查询字段集合
	 * @param xmlName
	 * @param model
	 * @return
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	@RequestMapping(value = {"/search/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String getSearchs(@PathVariable String xmlName) throws IOException, TemplateException{
		return getTemplateForCache(Constants.TM_GRID_SEARCHFORM, xmlName,"gridSearch");
	}
	
	/**
	 * 获取表单
	 * @param xmlName
	 * @param model
	 * @return
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	@RequestMapping(value = {"/form/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String getForm(@PathVariable String xmlName) throws IOException, TemplateException{
		return getTemplateForCache(Constants.TM_GRID_FORM, xmlName,"gridForm");
	}
	
	/**
	 * 获取授权集合
	 * @param xmlName
	 * @param model
	 * @return
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	@RequestMapping(value = {"/gantmodule/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String getGantmodule(@PathVariable String xmlName) throws IOException, TemplateException{
		return getTemplateForCache(Constants.TM_GRID_GANTMODULE, xmlName,"gantmodule");
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getConfigForXML(String xmlName){
		Document doc = super.getXMLForGrid(xmlName);
		Element element = (Element) doc.selectSingleNode("//grid/config");
		JSONObject json = new JSONObject();
		if(element!=null){
			List<DefaultAttribute> attributes = element.attributes();
			for (DefaultAttribute defaultAttribute : attributes) {
				json.put(defaultAttribute.getName(), defaultAttribute.getText());
			}
		}
		super.setObjectForCache(Constants.TM_GRID_CONFIG,xmlName, json);
		
		return json;
	}
	
	/**
	 * 获取配置对象
	 * @param xmlName
	 * @return
	 */
	@RequestMapping(value = {"/config/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message getConfig(@PathVariable String xmlName){
		Message msg = null;
		
		Object object = super.getObjectForCache(Constants.TM_GRID_CONFIG,xmlName);
		if(object!=null){
			msg = new Message(object);
			return msg;
		}
		
		JSONObject json = getConfigForXML(xmlName);
		msg = new Message(json);
		return msg;
	}
	
	private Message getModelForClassName(String type,String className){
		Message msg = null;
		try {
			
			//Object object = super.getObjectForCache(Constants.TM_GRID_MODEL, type+":"+className);
			/*if(object!=null){
				msg = new Message(object);
				return msg;
			}*/
			
			if(type.equals("view")){
				Document doc = super.getXMLForGrid(className);
				Element node = (Element)doc.selectSingleNode("//grid/sql");
				if(node!=null){
					String vname = "xv_"+node.attributeValue("vname");
					
					
					String sql = node.getText().trim();
					String vkey = ShortUrl.ShortText(sql)[0];
					
					SysReportViews findByIdAndVkey = reportViewsDao.findByIdAndVkey(vname, vkey);
					if(findByIdAndVkey==null){
						try {
							jdbcTemplate.execute("create or replace view "+vname+" as "+sql);
						} catch (DataAccessException e) {
							msg = new Message("EXT-GRID-MOD-500",e.getLocalizedMessage());
							return msg;
						}
						SysReportViews one = reportViewsDao.findOne(vname);
						if(one!=null){
							one.setVkey(vkey);
							one.setVsql(sql);
							reportViewsDao.saveAndFlush(one);
						}else{
							reportViewsDao.saveAndFlush(new SysReportViews(vname, vkey, sql));
						}
					}
					
					msg = getTableColumnTypes(vname);
					
				}
				
			}else{
				String clazz = "com."+(type.equals("sys")?"mw.framework.domain.":(type.equals("model")?"mw.framework.model.":"main.daomain."))+className;
				Class<?> claszz = Class.forName(clazz);
				
				Field[] declaredFields = claszz.getDeclaredFields();
				List<ExtDataField> dataFields = new ArrayList<ExtDataField>();
				for (Field field : declaredFields) {
					String fieldName = field.getName();
					if(!field.getType().getName().equals("java.util.HashSet")&&
							!field.getType().getName().equals("java.util.ArrayList") &&
							!field.getType().getName().equals("java.util.Set") &&
							!field.getType().getName().equals("java.util.LinkedHashMap") && !fieldName.equals("serialVersionUID")){
						FieldMeta meta = field.getAnnotation(FieldMeta.class);
						ExtDataField dataField = new ExtDataField();
						dataField.setName(field.getName());
						if (meta != null) {
							SortableField sf = new SortableField(meta, field);
							dataField.setDateFormat(sf.getMeta().format());
							dataField.setDefaultValue(sf.getMeta().defaultValue());
							dataField.setMapping(sf.getMeta().mapping());
							dataField.setType(sf.getMeta().type());
						}else{
							dataField.setType(field.getType().getSimpleName().toLowerCase());
						}
						dataFields.add(dataField);
					}
				}
				
				Class<?> superclass = claszz.getSuperclass();
				if(superclass!=null){
					Field[] superclassFields = superclass.getDeclaredFields();
					for (Field field : superclassFields) {
						String fieldName = field.getName();
						if(!field.getType().getName().equals("java.util.HashSet")&&
								!field.getType().getName().equals("java.util.ArrayList") &&
								!field.getType().getName().equals("java.util.Set") &&
								!field.getType().getName().equals("java.util.LinkedHashMap") && !fieldName.equals("serialVersionUID")){
							FieldMeta meta = field.getAnnotation(FieldMeta.class);
							ExtDataField dataField = new ExtDataField();
							dataField.setName(field.getName());
							if (meta != null) {
								SortableField sf = new SortableField(meta, field);
								dataField.setDateFormat(sf.getMeta().format());
								dataField.setDefaultValue(sf.getMeta().defaultValue());
								dataField.setMapping(sf.getMeta().mapping());
								dataField.setType(sf.getMeta().type());
							}else{
								dataField.setType(field.getType().getSimpleName().toLowerCase());
							}
							dataFields.add(dataField);
						}
					}
				}
				
				Class<?> class1 = superclass.getSuperclass();
				if(class1!=null){
					Field[] superclassFields = class1.getDeclaredFields();
					for (Field field : superclassFields) {
						String fieldName = field.getName();
						if(!field.getType().getName().equals("java.util.HashSet")&&
								!field.getType().getName().equals("java.util.ArrayList") &&
								!field.getType().getName().equals("java.util.Set") &&
								!field.getType().getName().equals("java.util.LinkedHashMap") && !fieldName.equals("serialVersionUID")){
							FieldMeta meta = field.getAnnotation(FieldMeta.class);
							ExtDataField dataField = new ExtDataField();
							dataField.setName(field.getName());
							if (meta != null) {
								SortableField sf = new SortableField(meta, field);
								dataField.setDateFormat(sf.getMeta().format());
								dataField.setDefaultValue(sf.getMeta().defaultValue());
								dataField.setMapping(sf.getMeta().mapping());
								dataField.setType(sf.getMeta().type());
							}else{
								dataField.setType(field.getType().getSimpleName().toLowerCase());
							}
							dataFields.add(dataField);
						}
					}
				}
				msg = new Message(dataFields);
				//super.setObjectForCache(Constants.TM_GRID_MODEL, type+":"+className, dataFields);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			msg = new Message("EXT-GRID-MOD-500","未找到对应实体对象");
		}
		return msg;
	}
	
	
	@RequestMapping(value = {"/model/{type}/{className}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message getModel(@PathVariable String type,@PathVariable String className){
		return getModelForClassName(type, className);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = {"/page/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JdbcExtGridBean getList(@PathVariable String xmlName,int page,int limit,int start,HttpServletRequest request){
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//是否分页
		Object config = super.getObjectForCache(Constants.TM_GRID_CONFIG, xmlName);
		boolean isPage = true;
		if(config!=null){
			JSONObject json = (JSONObject) config;
			Object object = json.get("isPage");
			if(object!=null){
				isPage = Boolean.valueOf(String.valueOf(json.get("isPage")));
			}
		}
		
		Object object = super.getObjectForCache(Constants.TM_GRID_SQL, xmlName);
		Element node = null;
		String vname =null;
		Element conf=null;
		List<Element> formats =null;
		boolean isDateNec=false;
		Integer num=0;
		Integer kunNum=0;
		if(object!=null){
			Map<String,Object> map = (Map<String, Object>) object;
			node = (Element) map.get("node");
			vname = (String) map.get("vname");
			conf=(Element)map.get("conf");

			/**
			 * 初始化ispasge参数,避免分页问题 
			 */
			isPage=conf.attribute("isPage")==null?true:Boolean.parseBoolean(conf.attribute("isPage").toString());
		}else{
			Document doc = super.getXMLForGrid(xmlName);
			node = (Element)doc.selectSingleNode("//grid/sql");
			formats = doc.selectNodes("//grid/sql/format");
			conf=(Element)doc.selectSingleNode("//grid/config");
			
			/**
			 * 初始化ispage参数,避免分页问题 
			 */
			isPage=conf.attribute("isPage")==null?true:Boolean.parseBoolean(conf.attribute("isPage").toString());
			
			//SQL依据ORACLE特性，用jdbc写成视图并创建KEY
			String sql = node.getText().trim();
			
			String vkey = ShortUrl.ShortText(sql)[0];
			//System.out.println(vkey);
			vname = "xv_"+node.attributeValue("vname");
			SysReportViews findByIdAndVkey = reportViewsDao.findByIdAndVkey(vname, vkey);
			if(findByIdAndVkey==null){
				jdbcTemplate.execute("create or replace view "+vname+" as "+sql);
				SysReportViews one = reportViewsDao.findOne(vname);
				if(one!=null){
					one.setVkey(vkey);
					one.setVsql(sql);
					reportViewsDao.saveAndFlush(one);
				}else{
					reportViewsDao.saveAndFlush(new SysReportViews(vname, vkey, sql));
				}
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("vname", vname);
			map.put("node", node);
			map.put("conf",conf);
			super.setObjectForCache(Constants.TM_GRID_SQL, xmlName, map);
		}
		
		
		if (node != null) {
			
			//sql
			StringBuffer sb = new StringBuffer("select * from "+vname+" where 1=1 ");
			if(conf!=null){
			Attribute gequeryType = conf.attribute("gequeryType");//add by hzm  2016.12.09
			
			if(gequeryType !=null){
				if("3".equals(conf.attribute("gequeryType").getValue().toString())){
					sb.append(" and ASSIGNEE = '" + this.getLoginUserId() + "' ");
				}
			}
		}
		
/*			String queryType = request.getParameter("queryType");
			if(queryType!=null && queryType.equals('3')){
				sb.append(" and ASSIGNEE = '" + this.getLoginUserId() + "' ");
			}*/
			
			//sql params
			List<Object> params = new ArrayList<Object>();
			
			Map<String, String[]> parameterMap = super.getRequest().getParameterMap();
			Set<Entry<String,String[]>> entrySet = parameterMap.entrySet();
			
			if(conf!=null &&conf.attribute("isDateNec")!=null && !com.alibaba.druid.util.StringUtils.isEmpty(conf.attribute("isDateNec").getValue()) ){
				isDateNec=Boolean.parseBoolean(conf.attribute("isDateNec").getValue());
			}
			//String xx = Range.Option.getValues();
			//System.out.println(xx);
			boolean hasDateParams=false;
			for (Entry<String, String[]> entry : entrySet) {
				String key = entry.getKey();

				//System.out.println(key);
				boolean result = Pattern.matches("^(I|E)(C|D|N)("+Range.Option.getValues()+")[A-Za-z0-9_.__]+$",key);
				if(result){
					String sign=key.substring(0,1),type=key.substring(1,2),option=key.substring(2, 4),field=key.substring(4);
					String[] entryValues = entry.getValue();
					
					//P_ORDER_CODE无法转回数据库模式
					if(!"P_ORDER_CODE".equals(field)) {
						//field转回数据库模式
						field = StringHelper.toColumnName(field);
					}
					
					Object value=null;
					Object high = null;
					Object[] values = null;
					boolean single = true;
					if(entryValues!=null){
						values = new Object[entryValues.length];
						for (int i=0;i<entryValues.length;i++) {
							switch (Range.Type.valueOf(type)) {
							case C:
								kunNum++;
								values[i] = entryValues[i];
								break;
							case D:
								try {
									hasDateParams=true;
									String[] split = entryValues[i].split(",");
									if(split[0].length()==10){
										num++;
										values[i] = sdf2.parse(split[0]+" 00:00:00");
									}else if(split[0].length()==19){
										values[i] = sdf2.parse(split[0]);
									}
									
									if(split.length==2){
										if(split[1].length()==10){
											num++;
											high = sdf2.parse(split[1]+" 23:59:59");
										}else if(split[1].length()==19){
											high = sdf2.parse(split[1]);
										}
									}
									//System.out.println("时间格式化后:"+values[i]);
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
						single = values.length==1?true:false;
						value = values[0];
						
						
			        	switch (Range.Option.valueOf(option)) {
						case EQ:
							if(single){
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and "+field+"=? ");
									params.add(value);
									break;
								case E:
									sb.append(" and "+field+"<>? ");
									params.add(value);
									break;
								default:
									break;
								}
								
							}else{
								switch (Range.Sign.valueOf(sign)) {
									case I:
										StringBuffer sb2 = new StringBuffer();
										for(int i=0;i<values.length;i++){
											sb2.append("'"+values[i]+"'");
											if(i<values.length-1){
												sb2.append(",");
											}
										}
										sb.append(" and "+field+" in ? ");
										params.add(sb2.toString());
										break;
									case E:
										StringBuffer sb3 = new StringBuffer();
										for(int i=0;i<values.length;i++){
											sb3.append("'"+values[i]+"'");
											if(i<values.length-1){
												sb3.append(",");
											}
										}
										sb.append(" and "+field+" not in ? ");
										params.add(sb3.toString());
										break;
									default:
										break;
								}
							}
							break;
						case GE:
							sb.append(" and "+field+" >= ? ");
							params.add(value);
							break;
						case LE:
							sb.append(" and "+field+" <= ? ");
							params.add(value);
							break;
						case GT:
							sb.append(" and "+field+" > ? ");
							params.add(value);
							break;
						case LT:
							sb.append(" and "+field+" < ? ");
							params.add(value);
							break;
						case NE:
							sb.append(" and "+field+" <> ? ");
							params.add(value);
							break;
						case BT:
							String[] splitValue = value.toString().split(",");
							switch (Range.Type.valueOf(type)) {
							case C:
								sb.append(" and "+field+" between ?  and ? ");
								params.add(splitValue[0]);
								params.add(splitValue[1]);
							case D:
								sb.append(" and "+field+" between ?  and ? ");
								params.add(value);
								params.add(high);
								break;
							case N:
								sb.append(" and "+field+" between ?  and ? ");
								params.add(splitValue[0]);
								params.add(splitValue[1]);
								break;
							default:
								break;
							}
							
							break;
						case CP:
							if(single){
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and "+field+" like ? ");
									params.add(StringHelper.like(String.valueOf(value)));
									break;
								case E:
									sb.append(" and "+field+" not like ? ");
									params.add(StringHelper.like(String.valueOf(value)));
									break;
								default:
									break;
								}
							}else{
								
								System.out.println("【高级查询】：【需向管理员申请权限!】");
								/*Predicate[] conditions =new Predicate[values.length];
								switch (Range.Sign.valueOf(sign)) {
								case I:
									for (int i =0;i<values.length;i++) {
										conditions[i] = criteriaBuilder.like(path, StringHelper.like(String.valueOf(values[i])));
									}
									
									break;
								case E:
									for (int i =0;i<values.length;i++) {
										conditions[i] = criteriaBuilder.notLike(path, StringHelper.like(String.valueOf(values[i])));
									}
									break;
								default:
									break;
								}
								condition = criteriaBuilder.or(conditions);*/
							}
							break;
						case IS:
							break;
						case MQ:
							if(single){
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and ("+field+" is null or "+field+"=?) ");
									params.add(value);
									break;
								case E:
									sb.append(" and ("+field+" is null or "+field+"<>?) ");
									params.add(value);
									break;
								default:
									break;
								}
							}else{
								System.out.println("eg. and (a.assignee is null or a.assignee='admin') 暂只支持单个参数模式");
							}
							break;
						default:
							break;
						}
					}
					
				}
			}
			
			Map<String,SimpleDateFormat> formatMap = null;
			
			if(formats!=null){
				formatMap = new HashMap<String, SimpleDateFormat>();
				for (Element element : formats) {
					formatMap.put(element.attributeValue("name"), new SimpleDateFormat(element.attributeValue("dateFormat")));
				}
			}
			
			logger.info(sb.toString());
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				logger.info(mapper.writeValueAsString(params));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			StringBuffer pageSQL = null;
			//获取总记录数
			if(!hasDateParams && isDateNec){
				return null;
			}
			if(!isPage){
				logger.info("isPage is false");
				List<Map<String, Object>> totalElements = jdbcTemplate.query(sb.toString(),params.toArray(),new MapRowMapper(true,formatMap));
				return new JdbcExtGridBean(1, totalElements.size(), totalElements.size(), totalElements);
			}
			//优化查询总记录数
			String sqlCount="";
			String kun=kunNum==1?"AND KUNNR like ? ":"";//params:[Thu Aug 02 00:00:00 CST 2018]
			String where=num==2?"and ORDER_DATE between ? and ?":num==1?"and ORDER_DATE >= ?":"";
			if(xmlName.equals("RP_ORD_COUNT")){
				sb.setLength(0);
				sb.append("select * from xv_RP_ORD_COUNT");
				sqlCount="select count(1) DATA_SIZE FROM ( SELECT REGIO,KUNNR,BZIRK,SUM(ORDER_NUM)AS ORDER_NUM, SUM(CAIWU) AS CAIWU,SUM(QUEREN) AS QUEREN,SUM(jine) AS jine,SUM(PRICE_TOTAL) AS PRICE_TOTAL FROM ( "+sb.toString()+") where 1 = 1 "+where+" "+kun+" GROUP BY REGIO,KUNNR,BZIRK )";
			}else{
				sqlCount="select count(1) DATA_SIZE FROM ( "+sb.toString()+" )";
			}
			List<Map<String, Object>> totalElements = jdbcTemplate.query(sqlCount,params.toArray(),new MapRowMapper(true,formatMap));
			Integer count=Integer.parseInt(totalElements.get(0).get("dataSize").toString());
			pageSQL = new StringBuffer("select * from (");
			String SQL_FIRST=xmlName.equals("RP_ORD_COUNT")?"SELECT REGIO,KUNNR,BZIRK,SUM(ORDER_NUM)AS ORDER_NUM, SUM(CAIWU) AS CAIWU,SUM(QUEREN) AS QUEREN,SUM(jine) AS jine,SUM(PRICE_TOTAL) AS PRICE_TOTAL FROM (":"";
			String SQL_LAST=xmlName.equals("RP_ORD_COUNT")?")where 1 = 1 "+where+" "+kun+" GROUP BY REGIO,KUNNR,BZIRK":"";
			if(page-1==0){
				pageSQL.append(SQL_FIRST + sb+ SQL_LAST +" ) where rownum <= ?");
				params.add(limit);
			}else{
				pageSQL.append("select row_.*, rownum rownum_ from ( "+SQL_FIRST+" "+sb+" "+SQL_LAST+") row_ where rownum <= ?) where rownum_ > ?");
				params.add(limit*page);
				params.add(start);
			}
			//总页数=总记录数/每页条数(系数加1)
			int totalPages = (count+limit-1)/limit;
			
			//多个时间字段转换
			//Map<String,SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
			//formatMap.put("createTime", new SimpleDateFormat("yyyy-MM-dd"));
			//formatMap.put("updateTime", new SimpleDateFormat("yyyy-MM-dd"));
			
			//System.out.println(pageSQL.toString());
			//System.out.println(params);
			
			//获取当前分页数据
			List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL.toString(),params.toArray(),new MapRowMapper(true,formatMap));
			
 			return new JdbcExtGridBean(totalPages, count, limit, queryForList);
				
			/*}else{
				List<Map<String, Object>> queryForList = jdbcTemplate.query(sb.toString(),params.toArray(),new MapRowMapper());
				return new JdbcExtGridBean(0,0,0,queryForList);
			}*/
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = {"/tree/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonNode getTreeList(@PathVariable String xmlName){
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		Object object = super.getObjectForCache(Constants.TM_GRID_TREESQL, xmlName);
		Element node = null;
		String vname =null;
		if(object!=null){
			Map<String,Object> map = (Map<String, Object>) object;
			node = (Element) map.get("node");
			vname = (String) map.get("vname");
		}else{
			Document doc = super.getXMLForGrid(xmlName);
			node = (Element)doc.selectSingleNode("//grid/sql");
			//SQL依据ORACLE特性，用jdbc写成视图并创建KEY
			String sql = node.getText().trim();
			
			String vkey = ShortUrl.ShortText(sql)[0];
			//System.out.println(vkey);
			vname = "xv_"+node.attributeValue("vname");
			SysReportViews findByIdAndVkey = reportViewsDao.findByIdAndVkey(vname, vkey);
			if(findByIdAndVkey==null){
				jdbcTemplate.execute("create or replace view "+vname+" as "+sql);
				SysReportViews one = reportViewsDao.findOne(vname);
				if(one!=null){
					one.setVkey(vkey);
					one.setVsql(sql);
					reportViewsDao.saveAndFlush(one);
				}else{
					reportViewsDao.saveAndFlush(new SysReportViews(vname, vkey, sql));
				}
			}
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("vname", vname);
			map.put("node", node);
			super.setObjectForCache(Constants.TM_GRID_TREESQL, xmlName, map);
		}
		
		if (node != null) {
			
			//sql
			StringBuffer sb = new StringBuffer("select * from "+vname+" where 1=1 ");
			//sql params
			List<Object> params = new ArrayList<Object>();
			
			Map<String, String[]> parameterMap = super.getRequest().getParameterMap();
			Set<Entry<String,String[]>> entrySet = parameterMap.entrySet();
			
			//String xx = Range.Option.getValues();
			//System.out.println(xx);
			for (Entry<String, String[]> entry : entrySet) {
				String key = entry.getKey();
				//System.out.println(key);
				boolean result = Pattern.matches("^(I|E)(C|D|N)("+Range.Option.getValues()+")[A-Za-z0-9_.__]+$",key);
				if(result){
					String sign=key.substring(0,1),type=key.substring(1,2),option=key.substring(2, 4),field=key.substring(4);
					String[] entryValues = entry.getValue();
					
					//field转回数据库模式
					field = StringHelper.toColumnName(field);
					
					Object value=null;
					Object high = null;
					Object[] values = null;
					boolean single = true;
					if(entryValues!=null){
						values = new Object[entryValues.length];
						for (int i=0;i<entryValues.length;i++) {
							switch (Range.Type.valueOf(type)) {
							case C:
								values[i] = entryValues[i];
								break;
							case D:
								try {
									String[] split = entryValues[i].split(",");
									if(split[0].length()==10){
										values[i] = sdf2.parse(split[0]+" 00:00:00");
									}else if(split[0].length()==19){
										values[i] = sdf2.parse(split[0]);
									}
									
									if(split.length==2){
										if(split[1].length()==10){
											high = sdf2.parse(split[1]+" 23:59:59");
										}else if(split[1].length()==19){
											high = sdf2.parse(split[1]);
										}
									}
									//System.out.println("时间格式化后:"+values[i]);
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
						single = values.length==1?true:false;
						value = values[0];
						
						
			        	switch (Range.Option.valueOf(option)) {
						case EQ:
							if(single){
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and "+field+"=? ");
									params.add(value);
									break;
								case E:
									sb.append(" and "+field+"<>? ");
									params.add(value);
									break;
								default:
									break;
								}
								
							}else{
								switch (Range.Sign.valueOf(sign)) {
									case I:
										StringBuffer sb2 = new StringBuffer();
										for(int i=0;i<values.length;i++){
											sb2.append("'"+values[i]+"'");
											if(i<values.length-1){
												sb2.append(",");
											}
										}
										sb.append(" and "+field+" in ? ");
										params.add(sb2.toString());
										break;
									case E:
										StringBuffer sb3 = new StringBuffer();
										for(int i=0;i<values.length;i++){
											sb3.append("'"+values[i]+"'");
											if(i<values.length-1){
												sb3.append(",");
											}
										}
										sb.append(" and "+field+" not in ? ");
										params.add(sb3.toString());
										break;
									default:
										break;
								}
							}
							break;
						case GE:
							sb.append(" and "+field+" >= ? ");
							params.add(value);
							break;
						case LE:
							sb.append(" and "+field+" <= ? ");
							params.add(value);
							break;
						case GT:
							sb.append(" and "+field+" > ? ");
							params.add(value);
							break;
						case LT:
							sb.append(" and "+field+" < ? ");
							params.add(value);
							break;
						case NE:
							sb.append(" and "+field+" <> ? ");
							params.add(value);
							break;
						case BT:
							String[] splitValue = value.toString().split(",");
							switch (Range.Type.valueOf(type)) {
							case C:
								sb.append(" and "+field+" between ?  and ? ");
								params.add(splitValue[0]);
								params.add(splitValue[1]);
							case D:
								sb.append(" and "+field+" between ?  and ? ");
								params.add(value);
								params.add(high);
								break;
							case N:
								sb.append(" and "+field+" between ?  and ? ");
								params.add(splitValue[0]);
								params.add(splitValue[1]);
								break;
							default:
								break;
							}
							
							break;
						case CP:
							if(single){
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and "+field+" like ? ");
									params.add(StringHelper.like(String.valueOf(value)));
									break;
								case E:
									sb.append(" and "+field+" not like ? ");
									params.add(StringHelper.like(String.valueOf(value)));
									break;
								default:
									break;
								}
							}else{
								
								System.out.println("【高级查询】：【需向管理员申请权限!】");
								/*Predicate[] conditions =new Predicate[values.length];
								switch (Range.Sign.valueOf(sign)) {
								case I:
									for (int i =0;i<values.length;i++) {
										conditions[i] = criteriaBuilder.like(path, StringHelper.like(String.valueOf(values[i])));
									}
									
									break;
								case E:
									for (int i =0;i<values.length;i++) {
										conditions[i] = criteriaBuilder.notLike(path, StringHelper.like(String.valueOf(values[i])));
									}
									break;
								default:
									break;
								}
								condition = criteriaBuilder.or(conditions);*/
							}
							break;
						case IS:
							break;
						case MQ:
							if(single){
								switch (Range.Sign.valueOf(sign)) {
								case I:
									sb.append(" and ("+field+" is null or "+field+"=?) ");
									params.add(value);
									break;
								case E:
									sb.append(" and ("+field+" is null or "+field+"<>?) ");
									params.add(value);
									break;
								default:
									break;
								}
							}else{
								System.out.println("eg. and (a.assignee is null or a.assignee='admin') 暂只支持单个参数模式");
							}
							break;
						default:
							break;
						}
					}
					
				}
			}
			long start = System.currentTimeMillis();
			JsonNode queryForTreeNode = super.queryForTreeNode(sb.toString(), xmlName.startsWith("TMW")?true:false, xmlName.startsWith("TMW")?true:false, true,params.toArray());
			long end = System.currentTimeMillis();
			//System.out.println(end-start);
			return queryForTreeNode;
		}
		return null;
	}
	
	@RequestMapping(value = {"/delete/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message deleteCurdTable(@PathVariable String xmlName,@RequestBody DataModel data){
		Message msg = null;
		Document doc = super.getXMLForGrid(xmlName);
		Element element = (Element)doc.selectSingleNode("//grid/config");
		Attribute tname = element.attribute("curdtname");
		Attribute updatedesc = element.attribute("updatedesc");
		
		if(tname!=null && tname.getText().length()>0 && updatedesc!=null && updatedesc.getText().length()>0){
			
			StringBuffer delsql = new StringBuffer("delete "+tname.getText()+" where ");
			List<Object[]> params = new ArrayList<Object[]>();
			
			String[] split = updatedesc.getText().split(",");
			List<Map<String,Object>> listmap = data.getListmap();
			
			for (int i=0;i<listmap.size();i++) {
				Map<String, Object> map  = listmap.get(i);
				List<Object> child = new ArrayList<Object>();
				
				for (int j=0;j< split.length;j++) {
					if(i==0){
						delsql.append((j>0?" and ":"")+(split[j].equals("pkid")?"id":split[j])+"= ?");
					}
					child.add(map.get(split[j]));
				}
//				System.out.println(child);
				params.add(child.toArray());
				
			}
			
			logger.info(delsql.toString());
			
			jdbcTemplate.batchUpdate(delsql.toString(), params);
			
			msg = new Message("OK!");
		}else{
			msg = new Message("EG_S_500", "后台curd配置不完整!");
		}
		
		
		return msg;
	}
	
	@RequestMapping(value = {"/save/{xmlName}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message saveCurdTable(@PathVariable String xmlName,@RequestBody DataModel data){
		Message msg = null;
		
		Document doc = super.getXMLForGrid(xmlName);
		Element element = (Element) doc.selectSingleNode("//grid/config");
		Attribute tname = element.attribute("curdtname");
		Attribute updatedesc = element.attribute("updatedesc");

		// 是否保存修改记录
		Attribute needLog = element.attribute("needLog");

		// 数据列
		@SuppressWarnings("unchecked")
		List<DefaultElement> selectNodes = doc
				.selectNodes("//grid/head/column[@dataIndex]");

		if (tname != null && tname.getText().length() > 0 && updatedesc != null
				&& updatedesc.getText().length() > 0) {
			String[] split = updatedesc.getText().split(",");

			// 查询数据表对应字段信息
			Message tableColumnTypes = getTableColumnTypes(tname.getText());
			Map<String, ExtDataField> tableFieldMap = new HashMap<String, ExtDataField>();
			if (tableColumnTypes.getSuccess()) {
				@SuppressWarnings("unchecked")
				List<ExtDataField> dataFields = (List<ExtDataField>) tableColumnTypes
						.get("data");
				for (ExtDataField extDataField : dataFields) {
					tableFieldMap.put(extDataField.getName(), extDataField);
					logger.info("extDataField.getName()="+extDataField.getName());
				}
			} else {
				msg = tableColumnTypes;
				return msg;
			}

			// 添加查询条件
			List<Map<String, Object>> listmap = data.getListmap();
			for (Map<String, Object> map : listmap) {

				StringBuffer update = new StringBuffer(
						"select count(1) as TOTAL from " + tname.getText()
								+ " where 1=1 ");
				
				List<Object> params = new ArrayList<Object>();
				StringBuffer updateCond = new StringBuffer();
				Map<String, String> pkey = new HashMap<String, String>();
				StringBuilder keyString=new StringBuilder();
				for (String string : split) {
					updateCond.append(" and "
							+ (string.equals("pkid") ? "id" : string) + "=?");
					logger.info("updateCond1="+updateCond.toString());
					pkey.put(string, string);
					params.add(map.get(string));
					logger.info("map.get(string)=数值:"+map.get(string));
					//存入到opration_log中，当做“主键”
					keyString.append(string+"="+map.get(string)+";");
				}
				
				
				update.append(updateCond);
				logger.info("update==="+update.toString());
				
				Map<String, Object> totalMap = jdbcTemplate.queryForMap(
						update.toString(), params.toArray());
				List<Map<String, Object>> beanList = jdbcTemplate.queryForList(
						"select * from " + tname.getText() + " where 1=1 "
								+ updateCond, params.toArray());
				
				
				if (Integer.valueOf(totalMap.get("TOTAL").toString()) == 1) {
					StringBuffer updatesql = new StringBuffer("update "
							+ tname.getText() + " set ");

					StringBuffer fields = new StringBuffer();
					List<Object> upparams = new ArrayList<Object>();
					Map<String, Object> beanMap = beanList.get(0);
					for (DefaultElement defaultElement : selectNodes) {
						String key = defaultElement.attributeValue("dataIndex");//old
						String columnName = StringHelper.toColumnName(key)
								.toUpperCase();//new
						
						logger.info("key=="+key);
						logger.info("columnName=="+columnName);
						
						Object columnValueOld = beanMap.get(columnName);
						Object columnValueNew = map.get(key);
						ExtDataField extDataField = tableFieldMap.get(key);
						if("TM_CHILD_MATERIAL04".equals(xmlName)){
							if("info3".equals(key)||"info2".equals(key)){
								if(columnValueNew.equals("1")){
									columnValueNew="外切";
								}
								if(columnValueNew.equals("2")){
									columnValueNew="内切";
								}
							}
						}
						Object valueNew = null;
						if (pkey.get(key) == null) {
							if (columnValueNew != null
									&& columnValueNew.toString().length() > 0) {
								fields.append(columnName + "=?,");
								if (extDataField.getType().equals("float")) {
									valueNew = Double.valueOf(columnValueNew
											.toString());
								} else if (extDataField.getType().equals("int")) {
									valueNew = Integer.valueOf(columnValueNew
											.toString());
								} else {
									valueNew = columnValueNew;
								}
								upparams.add(valueNew);
							}
							
							//如果XML文档中表明了需要记录则将修改字段，修改表明，主键，存入operation_log中
							if (needLog != null
									&& !Boolean
											.parseBoolean(needLog.toString())) {
								String _valueOld = columnValueOld == null ? ""
										: columnValueOld.toString();
								String _valueNew = valueNew == null ? ""
										: valueNew.toString();
								if (!_valueOld.equals(_valueNew)) {
									OperationLog log = new OperationLog(
											tname.getText(), columnName,
											_valueOld, _valueNew,
											keyString.toString());
									commonManager.save(log);
								}
							}
						}
					}

					updatesql.append(fields.length() > 0 ? fields
							.deleteCharAt(fields.length() - 1) : "");
					updatesql.append(" where 1=1 " + updateCond);

					logger.info(updatesql.toString());
					upparams.addAll(params);
					jdbcTemplate.update(updatesql.toString(),
							upparams.toArray());

				} else {

					// 添加数据
					params = new ArrayList<Object>();
					StringBuffer fields = new StringBuffer();
					StringBuffer fieldIndexs = new StringBuffer();

					for (DefaultElement defaultElement : selectNodes) {
						String key = defaultElement.attributeValue("dataIndex");
						Object object = map.get(key);
						if (object != null && object.toString().length() > 0) {
							fields.append(StringHelper.toColumnName(key
									.equals("pkid") ? "id" : key) + ",");
							fieldIndexs.append("?,");
							ExtDataField extDataField = tableFieldMap.get(key
									.equals("pkid") ? "id" : key);
							if (extDataField.getType().equals("float")) {
								params.add(Double.valueOf(object.toString()));
							} else if (extDataField.getType().equals("int")) {
								params.add(Integer.valueOf(object.toString()));
							} else {
								params.add(object);
							}
						}
					}

					String insertsql = "insert into "
							+ tname.getText()
							+ "("
							+ (fields.length() > 0 ? fields.deleteCharAt(fields
									.length() - 1) : "")
							+ ") values("
							+ (fieldIndexs.length() > 0 ? fieldIndexs
									.deleteCharAt(fieldIndexs.length() - 1)
									: "") + ")";
					logger.info(insertsql);
					jdbcTemplate.update(insertsql, params.toArray());
				}
			}

			msg = new Message("OK!");
		} else {
			msg = new Message("EG_S_500", "后台curd配置不完整!");
		}

		return msg;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private List<Map<String,Object>> getElementForJson(List<Element> elements){
		List<Map<String,Object>> arrayNode = new ArrayList<Map<String,Object>>();
		for (Element element : elements) {
			//获取属性节点转Map
			List<Attribute> attributes = element.attributes();
			
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			
			for (Attribute attribute : attributes) {
				map.put(attribute.getName(), attribute.getValue());
				
				//第二层
				List<Element> elements2 = element.elements();
				if(elements2.size()>0){
					for (Element element2 : elements2) {
						
						List<Attribute> attributes2 = element2.attributes();
						Map<String,Object> map2 = new LinkedHashMap<String, Object>();
						for (Attribute attribute2 : attributes2) {
							map2.put(attribute2.getName(), attribute2.getValue());
							
							
						}
						
						//第三层
						List<Element> elements3 = element.elements();
						if(elements3.size()>0){
							for (Element element3 : elements3) {
								List<Attribute> attributes3 = element3.attributes();
								Map<String,Object> map3 = new LinkedHashMap<String, Object>();
								for (Attribute attribute3 : attributes3) {
									map3.put(attribute3.getName(), attribute3.getValue());
								}
								map2.put(element3.getName(), map3);
							}
						}
						
						
						map.put(element2.getName(), map2);
					}
				}
			}
			arrayNode.add(map);
		}
		return arrayNode;
	}
}
