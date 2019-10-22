/**
 *
 */
package com.mw.framework.commons;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.bean.Range;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.dao.SysUserDao;
import com.mw.framework.domain.SysRole;
import com.mw.framework.domain.SysUser;
import com.mw.framework.interceptor.LogNDCInterceptor;
import com.mw.framework.manager.SysMesSendManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.ExtDataField;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.support.dao.GenericRepository;
import com.mw.framework.util.JsonDateValueProcessor;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.BeanUtils;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.commons.BaseController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-3-10
 * 
 */
public class BaseController {
	private static final Logger logger = LoggerFactory.getLogger(LogNDCInterceptor.class);
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	private MemcachedClient memcachedClient;
	
	@Autowired
	protected SysMesSendManager mesSendManager;
	
	@Autowired
	private SysUserDao sysUserDao;
	
	@ExceptionHandler(Exception.class)
	public void handleIOException(Exception ex, HttpServletRequest request,HttpServletResponse response)throws Exception  {
		
		System.out.println("========="+getLoginUserId()+"==============START===EX===["+getSessionAttr(Constants.CURR_USER_IA)+"]["+getSessionAttr(Constants.CURR_USER_NA)+"]==========");
		PrintWriter out =null;
		try{
		ex.printStackTrace();
		String simpleName = ex.getClass().getSimpleName();
		String referer = request.getHeader("Referer");
		if(referer==null){
			System.out.println("【标准】异常统一处理！");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Cache-Control", "no-cache");
			System.out.println(simpleName+"："+ex.getLocalizedMessage());
			//response.sendRedirect("/");
		}else{
			System.out.println("【异步】异常统一处理！");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			out = response.getWriter();
			ObjectMapper mapper =new ObjectMapper();
			Message message = new Message(simpleName, ex.getLocalizedMessage());
			mapper.writeValue(out, message);
		}
		}catch(Exception e){
		}finally{
			if(out!=null){
				out.flush();
				out.close();
			}
		}
		
		System.out.println("=======================END===EX========"+this.getLoginUserId()+"===============");
		
	}
	
	public JdbcExtGridBean simpleQuery(String sql,boolean isPage,Map<String,SimpleDateFormat> formatMap,HttpServletRequest request){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//sql
		StringBuffer sb = new StringBuffer(sql);
		//sql params
		List<Object> params = new ArrayList<Object>();
		
		Map<String, String[]> parameterMap = request.getParameterMap();
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
									values[i] = sdf.parse(split[0]+" 00:00:00");
								}else if(split[0].length()==19){
									values[i] = sdf.parse(split[0]);
								}
								
								if(split.length==2){
									if(split[1].length()==10){
										high = sdf.parse(split[1]+" 23:59:59");
									}else if(split[1].length()==19){
										high = sdf.parse(split[1]);
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
		
		//System.out.println(sb);
		StringBuffer pageSQL = null;
		//获取总记录数
		List<Map<String, Object>> totalElements = isPage==true?jdbcTemplate.queryForList(sb.toString(),params.toArray()):jdbcTemplate.query(sb.toString(),params.toArray(),new MapRowMapper(true,formatMap));
		
		if(isPage){
			Integer page = Integer.valueOf(request.getParameter("page"));
			Integer limit = Integer.valueOf(request.getParameter("limit"));
			Integer start = Integer.valueOf(request.getParameter("start"));
			
			pageSQL = new StringBuffer("select * from (");
			if(page-1==0){
				pageSQL.append(sb+" ) where rownum <= ?");
				params.add(limit);
			}else{
				pageSQL.append("select row_.*, rownum rownum_ from ( "+sb+") row_ where rownum <= ?) where rownum_ > ?");
				params.add(limit*page);
				params.add(start);
			}
			
			logger.info(pageSQL.toString());
			System.out.println("basecontrolsql="+pageSQL.toString());
			
			//总页数=总记录数/每页条数(系数加1)
			int totalPages = (totalElements.size()+limit-1)/limit;
			
			//获取当前分页数据
			List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL.toString(),params.toArray(),new MapRowMapper(true,formatMap));
			
			return new JdbcExtGridBean(totalPages, totalElements.size(), limit, queryForList);
		}else{
			return new JdbcExtGridBean(totalElements);
		}
		
		
	}
	
	public <T> T getObjectForCache(String space){
		T object =null;
		
		try {
			object = memcachedClient.get(space);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	public Object getObjectForCache(String space,String key){
		Object object =null;
		
		try {
			object = memcachedClient.get(space+key);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	public void setObjectForCache(String space,Object object){
		try {
			memcachedClient.set(space, 0, object);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
	
	public void setObjectForCache(String space,String key,Object object){
		try {
			memcachedClient.set(space+key, 0, object);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteObjectForCache(String space,String key){
		try {
			memcachedClient.delete(space+key);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

	public HttpSession getSession() {
		return getRequest().getSession();
	}
	
	public String getSessionId() {
		return getRequest().getSession().getId();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getSessionAttr(String name){
		return (T) getSession().getAttribute(name);
	}
	
	public void removeSessionAttr(String name){
		getSession().removeAttribute(name);
	}
 
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		return request;
	}
	
	public Map<String, String[]> getCustomParameterMap() {
		Map<String, String[]> map = new HashMap<String, String[]>(getRequest().getParameterMap());
		String[] fields = map.get("fields");
		if(fields!=null && fields.length>0){
			String field = StringHelper.getFields(fields);
			String string = map.get("query")[0];
			try {
				String decode = URLDecoder.decode(string, "UTF-8");
				map.put("ICOP"+field, new String[]{decode});
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	protected Document getXML(String name){
		return getXML("xml", name);
	}
	
	protected Document getXMLForGrid(String name){
		return getXML("grid", name);
	}
	
	private Document getXML(String path,String name){
		SAXReader sr = new SAXReader();
		URL resource = Thread.currentThread().getContextClassLoader().getResource("");
		try {
			Document doc = sr.read(resource.getPath()+"/"+path+"/"+name+".xml");
			return doc;
		} catch (DocumentException e) {
			System.out.println("未定义"+name+".xml");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置系统默认数据
	 * 
	 * @param entity
	 */
	public void setEntity(UUIDEntity entity) {
		if (entity.getId() != null && entity.getId().length() > 0) {
			entity.setUpdateUser((String) getSession().getAttribute(
					Constants.CURR_USER_ID));
			entity.setUpdateTime(new Date());
		} else {
			entity.setCreateUser((String) getSession().getAttribute(
					Constants.CURR_USER_ID));
			entity.setCreateTime(new Date());
		}
	}

	/**
	 * 
	 * @param strings
	 * @return
	 */
	protected JsonConfig getJsonConfig(String... strings) {
		return getJsonConfig("yyyy/MM/dd HH:mm:ss", strings);
	}

	/**
	 * jsonconfig
	 * 
	 * @param format
	 * @param strings
	 * @return
	 */
	protected JsonConfig getJsonConfig(String format, String... strings) {
		JsonConfig config = new JsonConfig();

		config.setIgnoreDefaultExcludes(false);
		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 只要设置这个数组，指定过滤哪些字段
		config.setExcludes(/* new String[]{"children","parent"} */strings);
		config.registerJsonValueProcessor(Date.class,
				new JsonDateValueProcessor(format));
		config.registerJsonValueProcessor(Timestamp.class,
				new JsonDateValueProcessor(format));
		
		config.registerJsonValueProcessor(Double.class, new JsonValueProcessor() {
            @Override
            public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
                if (value == null) {
                    return "";
                }
                return value;
            }
            @Override
            public Object processArrayValue(Object value, JsonConfig jsonConfig) {
                return value;
            }
        });
		
		config.registerJsonValueProcessor(Integer.class, new JsonValueProcessor() {
            @Override
            public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
                if (value == null) {
                    return "";
                }
                return value;
            }
            @Override
            public Object processArrayValue(Object value, JsonConfig jsonConfig) {
                return value;
            }
        });
		
		return config;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Message save(GenericRepository repository,Object obj,BindingResult result){
		Message msg = null;
		if(result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				System.out.println(fieldError.getField()+"|"+fieldError.getDefaultMessage());
			}
			msg = new Message("V-500",result.toString());
		}  
		
		if(obj!=null){
			try {
				repository.save(obj);
				msg = new Message("ok");
			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("S-500",e.getLocalizedMessage());
			}
		}else{
			msg = new Message("S-000","object is null");
		}
		return msg;
	}
	
	protected JsonNode queryForTreeNode(String sql){
		return queryForTreeNode(sql, false, false,false);
	}
	
	protected List<Map<String,Object>> getLoginUserMenus(){
		//递归取数
		String sql = "select id,desc_en_us,desc_zh_cn,url,pid from sys_menu a start with a.id ='root' connect by prior a.id=a.pid order siblings by order_by";
		
		if(getLoginUserId().equals("admin")){
			return jdbcTemplate.query(sql, new MapRowMapper(true));
		}
		
		String sesql = "select id,desc_en_us,desc_zh_cn,url,pid,user_id from view_user_menu a start with a.id='root' and a.user_id='"+getLoginUserId()+"'  connect by prior a.id=a.pid and a.user_id='"+getLoginUserId()+"' order siblings by a.order_by";
		return jdbcTemplate.query(sesql, new MapRowMapper(true));
	}
	
	protected Map<String,String> getLoginUserMenuForMap(List<Map<String,Object>> list){
		Map<String,String> menus = new HashMap<String, String>();
		for (Map<String, Object> map : list) {
			menus.put(String.valueOf(map.get("url")),String.valueOf(map.get("descZhCn")));
		}
		return menus;
	}
	
	/**
	 * oracle 递归查询模式获取树形结构数据(必须包含ID跟PID字段)
	 */
	protected JsonNode queryForTreeNode(List<Map<String,Object>> queryForList,boolean checked,boolean checkedRoot,boolean variation){
		ObjectMapper mapper = new ObjectMapper();
		
		//ORACLE递归查询
		//String sql = "select id,desc_en_us,desc_zh_cn,key_val,pid from sys_trie_tree a start with a.id ='"+id+"' connect by prior a.id=a.pid order siblings by order_by";
		//logger.info("【queryForTreeNode】["+sql+"]");
		/*默认格式*/
		//List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
		/*将字段名转换成属性名*/
		//List<Map<String,Object>> queryForList = jdbcTemplate.query(sql,objects, new MapRowMapper(true));
		
		if(queryForList.size()>0){
			Map<String,Object> rootMap = new LinkedHashMap<String, Object>();
			rootMap.put("id", "root");
			rootMap.put("name", "根节点");
 			
			ObjectNode rootNode = mapper.valueToTree((variation?rootMap:queryForList.get(0)));
			if(checkedRoot)
				rootNode.put("checked", false);
			
			Map<String,List<JsonNode>> nodesMap = new LinkedHashMap<String, List<JsonNode>>();
			
			for(int i=variation?0:1;i<queryForList.size();i++){
				Map<String, Object> map = queryForList.get(i);
				
				if(!map.get("id").equals("root")){
					//System.out.println(map.get("descZhCn"));
					String key = (String) map.get("pid");
					map.put("leaf", true);
					if(checked){
						map.put("checked", false);
					}
					
					List<JsonNode> list = nodesMap.get(key);
					if(list==null){
						List<JsonNode> nodes = new ArrayList<JsonNode>();
						nodes.add(mapper.valueToTree(map));
						nodesMap.put(key, nodes);
					}else{
						list.add(mapper.valueToTree(map));
					}
				}
			}
			
			setChildrenForTree(mapper, rootNode, nodesMap,checked);
			return rootNode;
		}
		
		return null;
	}
	
	/**
	 * oracle 递归查询模式获取树形结构数据(必须包含ID跟PID字段)
	 */
	protected JsonNode queryForTreeNode(String sql,boolean checked,boolean checkedRoot,boolean variation,Object...objects){
		ObjectMapper mapper = new ObjectMapper();
		
		//ORACLE递归查询
		//String sql = "select id,desc_en_us,desc_zh_cn,key_val,pid from sys_trie_tree a start with a.id ='"+id+"' connect by prior a.id=a.pid order siblings by order_by";
		logger.info("【queryForTreeNode】["+sql+"]");
		/*默认格式*/
		//List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
		/*将字段名转换成属性名*/
		List<Map<String,Object>> queryForList = jdbcTemplate.query(sql,objects, new MapRowMapper(true));
		
		if(queryForList.size()>0){
			Map<String,Object> rootMap = new LinkedHashMap<String, Object>();
			rootMap.put("id", "root");
			rootMap.put("name", "根节点");
 			
			ObjectNode rootNode = mapper.valueToTree((variation?rootMap:queryForList.get(0)));
			if(checkedRoot)
				rootNode.put("checked", false);
			
			Map<String,List<JsonNode>> nodesMap = new LinkedHashMap<String, List<JsonNode>>();
			
			for(int i=variation?0:1;i<queryForList.size();i++){
				Map<String, Object> map = queryForList.get(i);
				
				if(!map.get("id").equals("root")){
					//System.out.println(map.get("descZhCn"));
					String key = (String) map.get("pid");
					map.put("leaf", true);
					map.put("expanded", true);
					if(checked){
						map.put("checked", false);
					}
					
					List<JsonNode> list = nodesMap.get(key);
					if(list==null){
						List<JsonNode> nodes = new ArrayList<JsonNode>();
						nodes.add(mapper.valueToTree(map));
						nodesMap.put(key, nodes);
					}else{
						list.add(mapper.valueToTree(map));
					}
				}
			}
			
			setChildrenForTree(mapper, rootNode, nodesMap,checked);
			return rootNode;
		}
		
		return null;
	}
	
	/**
	 * Oracle 递归查询获取的数据转换成Extjs所需的Tree结构
	 * @param mapper
	 * @param rootNode
	 * @param nodesMap
	 */
	@SuppressWarnings("deprecation")
	private void setChildrenForTree(ObjectMapper mapper,ObjectNode rootNode,Map<String,List<JsonNode>> nodesMap,boolean checked){
		Set<Entry<String,List<JsonNode>>> entrySet = nodesMap.entrySet();
		
		for (Entry<String, List<JsonNode>> entry : entrySet) {
			String str = entry.getKey();
			JsonNode node = dg(rootNode, str);
			if(node!=null){
				ArrayNode arrayNode = mapper.createArrayNode();
				((ObjectNode)node).put("leaf",false);
				if(checked)
				((ObjectNode)node).put("checked",false);
				((ObjectNode)node).put("children", arrayNode.addAll(entry.getValue()));
			}
		}
	}
	
	/**
	 * 递归查找子节点对应的父节点元素 for Extjs
	 * @param rootNode
	 * @param str
	 * @return
	 */
	private JsonNode dg(JsonNode rootNode,String str){
		JsonNode jsonNode = rootNode.get("id");
		if(jsonNode.asText().equals(str)){
			return rootNode;
		}else{
			JsonNode node = null;
			if(rootNode.has("children")){
				JsonNode jsonNode2 = rootNode.get("children");
				for (JsonNode jsonNode3 : jsonNode2) {
					node = dg(jsonNode3, str);
					if(node!=null){
						break;
					}
				}
			}
			return node;
		}
	}
	
	/**
	 * 基于现有数据集合整理树形结构数据(必须包含ID跟PID字段)
	 */
	@Deprecated
	protected JsonNode queryForTreeNode(List<?> queryForList,String pid,boolean checkedRoot,boolean checked,String... fields){
		ObjectMapper mapper = new ObjectMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//ORACLE递归查询
		//String sql = "select id,desc_en_us,desc_zh_cn,key_val,pid from sys_trie_tree a start with a.id ='"+id+"' connect by prior a.id=a.pid order siblings by order_by";
		 
		/*默认格式*/
		//List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
		/*将字段名转换成属性名*/
		
		if(queryForList.size()>0){
			Map<String, Object> rootMap = BeanUtils.getValues(queryForList.get(0), fields);
			Set<Entry<String,Object>> entrySet = rootMap.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				if(entry.getValue()!=null && entry.getValue().getClass().equals("java.util.Date")){
					rootMap.put(entry.getKey(), sdf.format(entry.getValue()));
				}
			}
			rootMap.put("expanded",true);
			ObjectNode rootNode = mapper.valueToTree(rootMap);
			if(checkedRoot)
				rootNode.put("checked", false);
			
			Map<String,List<JsonNode>> nodesMap = new LinkedHashMap<String, List<JsonNode>>();
			
			for(int i=1;i<queryForList.size();i++){
				Map<String, Object> map = BeanUtils.getValues(queryForList.get(i), fields);
				Set<Entry<String,Object>> entrySetForeach = map.entrySet();
				for (Entry<String, Object> entry : entrySetForeach) {
					if(entry.getValue()!=null && entry.getValue().getClass().getName().equals("java.util.Date")){
						map.put(entry.getKey(), sdf.format(entry.getValue()));
					}
				}
				String key = (String) map.get("pid");
				map.put("leaf", true);
				map.put("expanded", true);
				if(checked){
					map.put("checked", false);
				}
				
				List<JsonNode> list = nodesMap.get(key);
				if(list==null){
					List<JsonNode> nodes = new ArrayList<JsonNode>();
					nodes.add(mapper.valueToTree(map));
					nodesMap.put(key, nodes);
				}else{
					list.add(mapper.valueToTree(map));
				}
			}
			
			setChildrenForTree(mapper, rootNode, nodesMap,checked);
			return rootNode;
		}
		
		return null;
	}
	protected Map<String,ExtDataField> getTableColumns(String tableName){
		return getTableColumns(tableName,true);
	}
	
	protected Map<String,ExtDataField> getTableColumns(String tableName,boolean bool){
		Message tableColumnTypes = getTableColumnTypes(tableName);
		Map<String,ExtDataField> tableFieldMap = null;
		if(tableColumnTypes.getSuccess()){
			tableFieldMap = new HashMap<String, ExtDataField>();
			@SuppressWarnings("unchecked")
			List<ExtDataField> dataFields = (List<ExtDataField>) tableColumnTypes.get("data");
			for (ExtDataField extDataField : dataFields) {
				tableFieldMap.put(bool?extDataField.getName():StringHelper.toColumnName(extDataField.getName()), extDataField);
			}
		}else{
			new Exception(tableColumnTypes.getErrorMsg());
		}
		
		return tableFieldMap;
	}
	
	protected Message getTableColumnTypes(String tableName){
		String vxcsql = "select column_name,data_type from user_tab_cols where table_name ='"+tableName.toUpperCase()+"'";
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(vxcsql);
		Message msg = null;
		List<ExtDataField> dataFields = new ArrayList<ExtDataField>();
		try {
			for (Map<String,Object> field : queryForList) {
				ExtDataField dataField = new ExtDataField();
				
				String fieldName = StringHelper.toFieldName(String.valueOf(field.get("COLUMN_NAME")));
				dataField.setColumname(String.valueOf(field.get("COLUMN_NAME")));
				dataField.setName(fieldName);
				if(fieldName.equals("leaf") || fieldName.equals("expanded")){//叶子节点、展开节点特殊处理
					dataField.setType("boolean");
				}else{
					String dataType = String.valueOf(field.get("DATA_TYPE"));
					
					dataField.setType(Constants.OracleColType.valueOf(dataType.substring(0,dataType.indexOf("(")!=-1?dataType.indexOf("("):dataType.length())).toString()/*String.valueOf(field.get("DATA_TYPE"))*/);
					
					if(dataType.toUpperCase().equals("TIMESTAMP(6)")){
						dataField.setDateFormat("Y-m-d H:i:s");
					}else if(dataType.toUpperCase().equals("DATE")){
						dataField.setDateFormat("Y-m-d H:i:s");
					}else if(dataType.toUpperCase().equals("NUMBER")){
						dataField.setType("auto");
					}
				}
				dataFields.add(dataField);
			}
			msg = new Message(dataFields);
			//super.setObjectForCache(Constants.TM_GRID_MODEL, type+":"+className, dataFields);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("Z-ERROR-100","数据列类型转换异常!");
		}
		return msg;
	}
	
	protected String getLoginUserRolesId(){
		Set<SysRole> roles = getLoginUserRoles();
		StringBuffer sb = new StringBuffer();
		for (SysRole sysRole : roles) {
			sb.append(sysRole.getDescZhCn()+";");
		}
		return sb.toString();
	}
	
	protected String getLoginUserId(){
		Object userId=this.getSession().getAttribute(Constants.CURR_USER_ID);
		if(userId!=null){
			return userId.toString();
		}
		return null;
	}
	
	protected Set<SysRole> getLoginUserRoles(){
		return this.getSessionAttr(Constants.CURR_USER_ROLES);
	}
	
	public SysUser getLoginUser(){
		SysUser sysUser = sysUserDao.findOne(this.getLoginUserId());
		return sysUser;
	}
	
	protected String getLoginUserKunnr(){
		return this.getSessionAttr(Constants.CURR_USER_KUNNR);
	}
}
