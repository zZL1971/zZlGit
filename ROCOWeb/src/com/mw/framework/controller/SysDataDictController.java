package com.mw.framework.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustLogistics;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.bean.SSHBean;
import com.mw.framework.bean.SSHBeanModel;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.domain.SysUser;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;
import com.mw.framework.model.SysDataDictModel;
import com.mw.framework.support.DynamicDataSourceHolder;
import com.mw.framework.util.ValidateUtil;
import com.mw.framework.utils.BeanUtils;
import com.mysql.fabric.xmlrpc.base.Array;


@Controller
@RequestMapping("/core/dd/*")
public class SysDataDictController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(SysDataDictController.class);

	@Autowired
	public SysDataDictDao sysDataDictDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	//@Autowired
	//private Cache appCache;

	@RequestMapping(value = "/init", method = RequestMethod.GET)
	public String toDataDic(ModelAndView modelAndView){
		return "/core/toDataDic";
	}


	/**
	 * 根据PID获取数据字典数据(优先缓存)
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/list/{pid}", method = RequestMethod.GET)
	@ResponseBody
	public Page<SysDataDict> getDataDicList(@PathVariable String pid,int page, int limit) {
		DynamicDataSourceHolder.setCustomerType("DB01");
		List<Order> orders = new ArrayList<Sort.Order>();
		orders.add(new Order("id"));
		Sort sort = new Sort(orders);
		//		return sysDataDictDao.findByPid(pid,new PageRequest(page-1, limit, sort));
		return null;
	}

	/**
	 * 根据PID获取数据字典数据(优先缓存)
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/list2/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<SysDataDict> getDataDicList2(@PathVariable String id) {
		SysDataDict dataDic = sysDataDictDao.get(id);
		//System.out.println(dataDic);
		/*if(dataDic!=null){
			dataDic.setDescEnUs("woding");
			dataDicDao.saveForSSM(dataDic);
		}*/

		/*System.out.println("DATA:"+dataDic);

		try {
			Object object = appCache.get("goodscenter:DataDicDao:"+id, SerializationType.JAVA);
			System.out.println("object:"+object);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		}*/


		/*DataDic dataDic2 = dataDicDao.get(6);
		System.out.println("第二次查询获取缓存服务器数据是否:"+dataDic2);*/

		/*MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil
	              .getAddresses("127.0.0.1:11211"));
	    MemcachedClient memcachedClient = null;

	    try {
	    	memcachedClient = builder.build();
			//循环查看缓存内的数据	
			Iterator<Map<String, String>> iterSlabs = memcachedClient.getStatsByItem("items").values().iterator();
			Set<String> set = new HashSet<String>();
			while(iterSlabs.hasNext()) {
			    Map<String, String> slab = iterSlabs.next();
			    for(String key : slab.keySet()) {
			        String index = key.split(":")[1];
			        set.add(index);
			        System.out.println(key+"|"+index);
			    }
			}

			//统计
			List<String> list = new LinkedList<String>();
			for(String v : set) {
			    String commond = "cachedump ".concat(v).concat(" 0");
			    Iterator<Map<String, String>> iterItems = memcachedClient.getStatsByItem(commond).values().iterator();
			    while(iterItems.hasNext()) {
			        Map<String, String> items = iterItems.next();
			        list.addAll(items.keySet());
			        System.out.println(items.keySet());
			    }
			}
		} catch (MemcachedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return null;
	}

	/*	*//**
	 * 创建分页请求.
	 *//*
	private PageRequest buildPageRequest(int pageNumber, int pagzSize, String sortType) {
		Sort sort = null;
		if ("auto".equals(sortType)) {
			sort = new Sort(Direction.DESC, "id");
		} else if ("title".equals(sortType)) {
			sort = new Sort(Direction.ASC, "title");
		}

		return new PageRequest(pageNumber - 1, pagzSize, sort);
	}*/


	/**
	 * 根据ID获取数据字段详细信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/get/{id}", method = {RequestMethod.GET,RequestMethod.POST})
	@IgnoreProperties(value={ @IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler","parent","children","pid","orderBy","keyVal","descZhCn","descZhTw","descEnUs","dataDicts","rowStatus","createUser","createTime","updateUser","updateTime" }, pojo = SysTrieTree.class)})
	@ResponseBody
	public Message getDataDic(@PathVariable String id){
		if(!StringUtils.isEmpty(id)){
			SysDataDict dataDict = sysDataDictDao.findOne(id);
			Message message = new Message(dataDict);
			return message;
		}else{
			return new Message("DD-G-500", "未找到相关数据");
		}

	}

	/**
	 * 返回中文字符串的问题
	 * produces （返回json）:text/html;charset=utf-8
	 * produces （返回string）:text/html;charset=utf-8
	 * produces （返回string xml）:text/xml;charset=utf-8
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/get2", method = RequestMethod.GET,produces = {"text/json;charset=UTF-8"})
	@ResponseBody
	@Deprecated
	public String getDataDicResultString(){
		StringBuffer sb = new StringBuffer();
		//获取部门
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("select * from sys_organ LIMIT 3");

		sb.append("{id:'root',text:'中文哈哈',children:[");
		dgi(sb, queryForList, "root","");
		sb.append("]}");
		System.out.println(sb);
		return sb.toString();
	}


	/**
	 * 递归Tree
	 * @param sb
	 * @param list
	 * @param id
	 */
	private void dgi(StringBuffer sb, List<Map<String, Object>> list, String id,String regx) {
		String menuId = "";
		//sb.append("{children:[");
		for (int i = list.size() - 1; i >= 0; i--) {
			Map<String, Object> map = list.get(i);
			/*if(i==0){
				sb.append("],id:"+menuId+",text:"+map.get("dept_name")+"}");
			}*/
			if (id.equals(map.get("pid").toString().trim())) {
				//sb.append(",leaf:false");
				menuId = (String) map.get("id");
				//sb.append("<item id='" + menuId + "' text='" + map.get("dept_name") + "'>");
				sb.append("{id:"+menuId+",text:"+map.get("dept_name")+",children:[");
				dgi(sb, list, menuId,",");
				sb.append("]}");
				sb.append(regx);
			}


		}
		//sb.append("}");
	}

	/**
	 * spring MVC 传递json数组，可用于1对多等复杂数据传递
	 * @param dicts
	 * @return
	 */
	@RequestMapping(value = "/save2", method = RequestMethod.POST)
	@ResponseBody
	public Message save(@RequestBody SysDataDictModel dicts){
		Message msg = null;

		if(dicts!=null){
			try {

				StringBuffer sb = new StringBuffer();
				//手动验证请求提交的数据
				for (int i=0;i<dicts.getDicts().size();i++) {
					String validate = ValidateUtil.validate(dicts.getDicts().get(i));
					if(validate!=null){
						sb.append("<font color=blue>第"+(i+1)+"行数据验证结果:</font><br/>");
						sb.append(validate);
					}
				}

				if(sb.length()>0){
					msg = new Message("DD-V-500",sb.toString());
				}else{
					sysDataDictDao.saveForSSM(dicts.getDicts());
					msg = new Message("ok");
				}
			} catch (Exception e) {
				msg = new Message("DD-S-500",e.getLocalizedMessage());
			}
		}else{
			msg = new Message("DD-S-000","dataDic is null");
		}
		logger.debug(msg.toString());
		return msg;
	}

	/**
	 * spring MVC 传递json数组，可用于1对多等复杂数据传递
	 * @param dicts
	 * @return
	 */
	@RequestMapping(value = "/save3", method = RequestMethod.POST)
	@ResponseBody
	public Message save(@RequestBody List<SysDataDict> dicts){
		Message msg = null;

		if(dicts!=null){
			try {

				StringBuffer sb = new StringBuffer();
				//手动验证请求提交的数据
				for (int i=0;i<dicts.size();i++) {
					String validate = ValidateUtil.validate(dicts.get(i));
					if(validate!=null){
						sb.append("<font color=blue>第"+(i+1)+"行数据验证结果:</font><br/>");
						sb.append(validate);
					}
				}

				if(sb.length()>0){
					msg = new Message("DD-V-500",sb.toString());
				}else{
					sysDataDictDao.saveForSSM(dicts);
					msg = new Message("ok");
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("DD-S-500",e.getLocalizedMessage());
			}
		}else{
			msg = new Message("DD-S-000","dataDic is null");
		}
		logger.debug(msg.toString());
		return msg;
	}

	/**
	 * 保存数据字典(ajax提交)
	 * @param dataDic
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Message save(@Valid SysDataDict dataDict,BindingResult result){
		Message msg = null;
		if(result.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				//System.out.println(fieldError.getField()+"|"+fieldError.getDefaultMessage());
				sb.append(fieldError.getField()+"-->"+fieldError.getDefaultMessage());
			}
			msg = new Message("DD-V-500",sb.toString());
			return msg;
		}  

		if(dataDict!=null){
			try {
				sysDataDictDao.saveAndFlushForSSM(dataDict);
				msg = new Message(dataDict.getId());
			} catch (Exception e) {
				msg = new Message("DD-S-500",e.getLocalizedMessage());
			}
		}else{
			msg = new Message("DD-S-000","dataDic is null");
		}
		logger.debug(msg.toString());
		return msg;
	}

	/**
	 * 保存数据字典(传统表单提交)
	 * @param dataDic
	 * @return
	 */
	@RequestMapping(value = "/save2", method = RequestMethod.GET)
	public String save2(String holder, @Valid SysDataDict dataDic,BindingResult result){
		Message msg = null;
		if(result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				System.out.println(fieldError.getField()+"|"+fieldError.getDefaultMessage());
			}
			msg = new Message("DD-V-500",result.toString());
			return "error";
		}  

		if(dataDic!=null){
			try {
				logger.debug(dataDic.toString());
				DynamicDataSourceHolder.setCustomerType(holder);
				sysDataDictDao.saveForSSM(dataDic);
				msg = new Message("ok");
			} catch (Exception e) {
				msg = new Message("DD-S-500",e.getLocalizedMessage());
			}
		}else{
			msg = new Message("DD-S-000","dataDic is null");
		}
		logger.debug(msg.toString());
		//return msg;\
		return "success";
	}

	/**
	 * 根据ID删除数据字典
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Message delete(@PathVariable String id){
		Message msg = null;
		try {
			sysDataDictDao.deleteForSSM(id);
			msg = new Message(null,"ok");
		} catch (Exception e) {
			msg = new Message("DD-D-500",e.getLocalizedMessage());
		}
		return msg;
	}

	/**
	 * 根据ID删除数据字典
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message delete2(@RequestParam(value = "ids") String[] ids){
		Message msg = null;

		try {
			sysDataDictDao.deleteInBatchForSSM(sysDataDictDao.findAll(Arrays.asList(ids)));
			msg = new Message(null,"ok");
		} catch (Exception e) {
			msg = new Message("DD-D-500",e.getLocalizedMessage());
		}
		return msg;
	}

	/**
	 * SSM查询数据字典
	 * @return
	 */
	@RequestMapping(value = {"/list3/"}, method = RequestMethod.POST)
	@ResponseBody
	public Message list(@RequestBody SSHBeanModel model){
		List<SSHBean> result = new ArrayList<SSHBean>();
		List<SSHBean> sshs = model.getSshs();
		for (SSHBean sshBean : sshs) {
			List<SysDataDict> findByTrieTreeKeyVal = sysDataDictDao.findByTrieTreeKeyValForSSM(sshBean.getValue());
			List<Map<String, String>> dicts = new ArrayList<Map<String,String>>();
			for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {
				Map<String,String> map = new HashMap<String, String>();
				map.put("id", sysDataDict.getKeyVal());
				map.put("text", sysDataDict.getDescEnUs());//可以支持国际化
				dicts.add(map);
			}

			sshBean.setDicts(dicts);
			result.add(sshBean);
		}

		return new Message(result);
	}

	@RequestMapping(value = {"/list3/{code}"}, method = RequestMethod.GET)
	@ResponseBody
	public Message list(@PathVariable String code,String valType,String codeType){
		/*Object curr_user_timber = super.getSession().getAttribute("CURR_USER_KUNNR_TIMBER");
		Object curr_user_cup = super.getSession().getAttribute("CURR_USER_KUNNR_CUP");
		Object CURR_SAP_CODE = super.getSession().getAttribute("CURR_SAP_CODE");*/
		SysUser sysUser = (SysUser) super.getSession().getAttribute(Constants.CURR_USER);
		String newCodeType="";
		boolean boo=false;
		if(codeType!=null&&codeType.contains("##")){
			newCodeType=codeType.split("##")[0];
			codeType=codeType.split("##")[1];
			boo=code.equals("ERROR_ORD_TYPE")&&(newCodeType.equals("物料审核")||newCodeType.equals("孔位审核")||newCodeType.equals("移门审核")||newCodeType.equals("IMOS绘图")||newCodeType.equals("2020绘图"));
		}
		if(boo||(code.equals("ERROR_ORD_TYPE")&&newCodeType.equals("订单审绘"))){
			List<Map<String, Object>> dicts = new ArrayList<Map<String,Object>>();
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id","");
			map.put("text","");
			dicts.add(map);
			Map<String,Object> newmap1 = new HashMap<String, Object>();
			newmap1.put("id","0");
			newmap1.put("text","OK");
			dicts.add(newmap1);
			List<SysDataDict> findByTrieTreeKeyVal = sysDataDictDao.findByTrieTreeKeyValForSSM(code);
			for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {
				Map<String,Object> newmap = new HashMap<String, Object>();
					newmap.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
				if(boo&&sysDataDict.getType()!=null&&sysDataDict.getType().equals("son")){
					newmap.put("text", sysDataDict.getDescZhCn());
					dicts.add(newmap);
				}
				if(sysDataDict.getType()!=null&&sysDataDict.getType().equals("draw")){
					newmap.put("text", sysDataDict.getDescZhCn());
					dicts.add(newmap);
				}
				
			}
			return new Message(dicts);
		}else{
			CustHeader custHeader = sysUser.getCustHeader();
			List<String> sparts=null;
			if(custHeader!=null) {
				Set<CustLogistics> custLogisticsSet = custHeader.getCustLogisticsSet();
				sparts = new ArrayList<String>(custLogisticsSet.size());
				for (CustLogistics custLogistics : custLogisticsSet) {
					sparts.add(custLogistics.getSpart());
				}
			}
			String showDisabled=this.getRequest().getParameter("showDisabled");
			String judge = this.getRequest().getParameter("judge");
			List<SysDataDict> findByTrieTreeKeyVal=new ArrayList<SysDataDict>();
			if("JIAO_QI_TIAN_SHU".equals(code) || "SALE_FOR".equals(code)||"JIAO_QI_TIAN_SHU_B".equals(code)){			
				sysDataDictDao.delCache(code);
			}
			if(showDisabled==null || Boolean.parseBoolean(showDisabled)){
				findByTrieTreeKeyVal= sysDataDictDao.findByTrieTreeKeyValForSSM(code);
			}else{
				findByTrieTreeKeyVal= sysDataDictDao.findByTrieTreeKeyValAndStatForSSM(code);
			}
			List<Map<String, Object>> dicts = new ArrayList<Map<String,Object>>();
			Object language = super.getSession().getAttribute(Constants.language);//可以支持国际化
			//空的选项
			Map<String,Object> mapNvl = new HashMap<String, Object>();
			mapNvl.put("id","");
			mapNvl.put("text","");
			dicts.add(mapNvl);
			if(BeanUtils.isNotEmpty(codeType))
				for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {
					if(codeType.equals(sysDataDict.getTypeKey())){
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
						//可以支持国际化
						if(language.equals("zh_CN")){
							map.put("text", sysDataDict.getDescZhCn());
						}else if(language.equals("en_US")){
							map.put("text", sysDataDict.getDescEnUs());
						}else if(language.equals("zh_TW")){
							map.put("text", sysDataDict.getDescZhTw());
						}
						dicts.add(map);
					}
				}
			else
				for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {
					if("JIAO_QI_TIAN_SHU".equals(code)||"JIAO_QI_TIAN_SHU_B".equals(code)){
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
						//可以支持国际化
						if(language.equals("zh_CN")){
							if(Boolean.parseBoolean(judge)){
								map.put("text", sysDataDict.getDescZhTw());
							}else{
								map.put("text", sysDataDict.getDescZhCn());	
							}

						}else if(language.equals("en_US")){
							map.put("text", sysDataDict.getDescEnUs());
						}else if(language.equals("zh_TW")){
							map.put("text", sysDataDict.getDescZhTw());
						}

						dicts.add(map);

					}else{
						Map<String,Object> map = new HashMap<String, Object>();
						if("SALE_FOR".equals(code)&&custHeader!=null){
							if(sparts.contains(BeanUtils.getValueForType(sysDataDict.getTypeKey(), valType))){
								map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
							}/*else if(BeanUtils.getValueForType(sysDataDict.getTypeKey(), valType).equals("2")){
							map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
						}else if("X".equals(curr_user_cup)&&BeanUtils.getValueForType(sysDataDict.getTypeKey(), valType).equals("1")){
							map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
						}else if("X".equals(curr_user_timber)&&BeanUtils.getValueForType(sysDataDict.getTypeKey(), valType).equals("3")){
							map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
						}*/
						}else{
							map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
						}
						//可以支持国际化
						if(language.equals("zh_CN")){
							if("SALE_FOR".equals(code)&&custHeader!=null){
								if(sparts.contains(BeanUtils.getValueForType(sysDataDict.getTypeKey(), valType))){
									map.put("text", sysDataDict.getDescZhCn());
								}/*else if(BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType).equals("2")){
								map.put("text", sysDataDict.getDescZhCn());
							}else if("X".equals(curr_user_cup)&&BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType).equals("1")){
								map.put("text", sysDataDict.getDescZhCn());
							}else if("X".equals(curr_user_timber)&&BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType).equals("3")){
								map.put("text", sysDataDict.getDescZhCn());
							}*/
							}else{
								map.put("text", sysDataDict.getDescZhCn());
							}
						}else if(language.equals("en_US")){
							map.put("text", sysDataDict.getDescEnUs());
						}else if(language.equals("zh_TW")){
							map.put("text", sysDataDict.getDescZhTw());
						}

						dicts.add(map);
					}
				}
			if(dicts.size()==1){
				for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {

					if(sysDataDict.getTypeKey()!=null && sysDataDict.getTypeKey().trim().length()>0){

					}else{
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
						//可以支持国际化
						if(language.equals("zh_CN")){
							map.put("text", sysDataDict.getDescZhCn());
						}else if(language.equals("en_US")){
							map.put("text", sysDataDict.getDescEnUs());
						}else if(language.equals("zh_TW")){
							map.put("text", sysDataDict.getDescZhTw());
						}

						dicts.add(map);
					}

				}
			}
			return new Message(dicts);
		}
	}


	@RequestMapping(value = {"/list5/{code}"}, method = RequestMethod.GET)
	@ResponseBody
	public Message list5(@PathVariable String code,String valType,String codeType){
		String ccbm = this.getRequest().getParameter("ccbm");
		String cczb = this.getRequest().getParameter("cczb");
		String cclx = this.getRequest().getParameter("cclx");
		List<SysDataDict> findByTrieTreeKeyVal=new ArrayList<SysDataDict>();
		List<Map<String, Object>> codeList = jdbcTemplate.queryForList("select ST.ID from sys_trie_tree st where st.key_val='"+code+"'");
		if(codeList.size()>0){
			String codeId = (String) codeList.get(0).get("ID");
		
		if(ccbm!=null) {
			if(ccbm.equals("1")||ccbm.equals("9")||ccbm.equals("5")||ccbm.equals("4")||ccbm.equals("3")) {
				findByTrieTreeKeyVal = sysDataDictDao.findByType(codeId,ccbm);
			}else {
				findByTrieTreeKeyVal= sysDataDictDao.findByTrieTreeKeyValForSSM(code);
			}
		}else if(cczb!=null) {
			List<Map<String, Object>> cczbList = jdbcTemplate.queryForList("select sd.key_val from sys_data_dict sd where sd.trie_id=(select ST.ID from sys_trie_tree st where st.key_val='CCBM') and sd.desc_zh_cn='"+cczb+"'");
			if(cczbList.size()>0) {
				String cczbVal = (String) cczbList.get(0).get("key_val");
				findByTrieTreeKeyVal = sysDataDictDao.findByType(codeId,cczbVal);
				if(findByTrieTreeKeyVal.size()<=0) {
					findByTrieTreeKeyVal= sysDataDictDao.findByTrieTreeKeyValForSSM(code);
				}
			}
		}else if(cclx!=null) {
			findByTrieTreeKeyVal = sysDataDictDao.findByType(codeId,cclx);
		}
		}
		List<Map<String, Object>> dicts = new ArrayList<Map<String,Object>>();
		Object language = super.getSession().getAttribute(Constants.language);//可以支持国际化

		//空的选项
		Map<String,Object> mapNvl = new HashMap<String, Object>();
		mapNvl.put("id","");
		mapNvl.put("text","");
		for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
			map.put("text", sysDataDict.getDescZhCn());
			dicts.add(map);
		}

		if(dicts.size()==1){
			for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {
				if(sysDataDict.getTypeKey()!=null && sysDataDict.getTypeKey().trim().length()>0){

				}else{
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
					//可以支持国际化
					if(language.equals("zh_CN")){
						map.put("text", sysDataDict.getDescZhCn());
					}else if(language.equals("en_US")){
						map.put("text", sysDataDict.getDescEnUs());
					}else if(language.equals("zh_TW")){
						map.put("text", sysDataDict.getDescZhTw());
					}

					dicts.add(map);
				}

			}
		}

		return new Message(dicts);
	}

	@RequestMapping(value = {"/cascadelist"}, method = RequestMethod.GET)
	@ResponseBody
	public Message list4(String code,String trie,String valType){
		String showDisabled = this.getRequest().getParameter("showDisabled");
		//String showDisabled = null;
		List<SysDataDict> findByTrieTreeKeyVal = new ArrayList<SysDataDict>();
		String sql = "select id from sys_trie_tree where key_val='"+code+"' and pid=(select id from sys_trie_tree where key_val='"+trie+"')";
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
		String trieId="";
		if(queryForList.size()>0){
			trieId = (String) queryForList.get(0).get("ID");
		}

		if(showDisabled == null || Boolean.parseBoolean(showDisabled)){
			findByTrieTreeKeyVal = sysDataDictDao.findByTrieTreeIdForSSM(trieId);
		}else{
			//只针对颜色禁用
			//findByTrieTreeKeyVal = sysDataDictDao.findByTrieTreeKeyValAndStatForSSM("COLOR");
			findByTrieTreeKeyVal = sysDataDictDao.queryByNativeQuery("select * From sys_data_dict b where b.trie_id = '"+trieId+"'  and b.stat =1");
		}

		List<Map<String, Object>> dicts = new ArrayList<Map<String,Object>>();
		Object language = super.getSession().getAttribute(Constants.language);//可以支持国际化

		//空的选项
		Map<String,Object> mapNvl = new HashMap<String, Object>();
		mapNvl.put("id","");
		mapNvl.put("text","");
		dicts.add(mapNvl);

		for (SysDataDict sysDataDict : findByTrieTreeKeyVal) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", BeanUtils.getValueForType(sysDataDict.getKeyVal(), valType));
			//可以支持国际化
			if(language.equals("zh_CN")){
				map.put("text", sysDataDict.getDescZhCn());
			}else if(language.equals("en_US")){
				map.put("text", sysDataDict.getDescEnUs());
			}else if(language.equals("zh_TW")){
				map.put("text", sysDataDict.getDescZhTw());
			}

			dicts.add(map);
		}

		return new Message(dicts);
	}

}
