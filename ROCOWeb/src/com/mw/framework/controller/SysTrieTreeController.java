/**
 *
 */
package com.mw.framework.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.util.SortUtils;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.SysTrieTreeController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-4-12
 *
 */
@Controller
@RequestMapping("/core/trie/*")
public class SysTrieTreeController  extends BaseController{

	@Autowired
	private SysTrieTreeDao trieDao;
	
	@Autowired
	private SysDataDictDao dictDao;
	
	@Autowired
	private CommonManager commonManager;
	
	/**
	 * 跳转到数据字典索引对象维护界面
	 */
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public ModelAndView index(ModelAndView modelAndView){
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module", "trie");
		modelAndView.getModelMap().put("moduleTitle", "数据字典维护");
		return modelAndView;
	}
	
	/**
	 * 查询单个数据字典索引对象
	 */
	@RequestMapping(value = {"/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@IgnoreProperties(value={ @IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler","children","dataDicts","parent" }, pojo = SysTrieTree.class)})
	@ResponseBody
	public Message get(@PathVariable String id){
		Message msg = null;
		try {
			SysTrieTree one = trieDao.findOne(id);
			msg = new Message(one);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("C-TT-500", "系统未找到对应属性,非法参数!");
		}
		return msg;
	}
	
	/**
	 * 查询单个数据字典索引对象 for tree
	 */
	@RequestMapping(value = {"/tree/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonNode getForTree(@PathVariable String id){
		//ORACLE递归查询
		String sql = "select id,desc_en_us,desc_zh_cn,key_val,pid,icon from sys_trie_tree a start with a.id ='"+id+"' connect by prior a.id=a.pid order siblings by order_by";
		
		JsonNode queryForTreeNode = super.queryForTreeNode(sql);
		
		if(queryForTreeNode==null){
			super.jdbcTemplate.update("insert into sys_trie_tree(id,create_user,create_time,desc_zh_cn,key_val) values('root','admin',sysdate,'root','root')");
			super.jdbcTemplate.update("insert into sys_trie_tree(id,create_user,create_time,desc_zh_cn,key_val, pid) values('system','admin',sysdate,'数据字典','system','root')");
			queryForTreeNode = super.queryForTreeNode(sql);
		}
		
		return queryForTreeNode;
	}
	
	/**
	 * 查询单个数据字典索引子节点(单层)
	 */
	@RequestMapping(value = {"/nodes/{id}/{validField}/"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray getForNodes(@PathVariable String id,@PathVariable String validField){
		//ORACLE递归查询
		String sql = "select desc_zh_cn as text,"+validField+" as id from sys_trie_tree a where a.row_status=1 and a.pid=(select ID from sys_trie_tree WHERE KEY_VAL ='"+id+"') order by a.order_by";
		List<Map<String,Object>> queryForList = jdbcTemplate.query(sql,new MapRowMapper(true));
		return JSONArray.fromObject(queryForList);
	}
	
	/**
	 * 删除数据字典索引
	 * @return
	 */
	@RequestMapping(value = {"/delete"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message delete(@RequestParam("trieid")String trieid){
		Message msg = null;
		try {
			SysTrieTree trieTree = commonManager.getById(trieid, SysTrieTree.class);
			dictDao.delCache(trieTree.getKeyVal());
			commonManager.delete(trieTree);
			msg = new Message("ok");
		} catch (Exception e) {
			//e.printStackTrace();
			msg = new Message("TT-D-500", e.getLocalizedMessage());
		}
		return msg;
	}
	
	/**
	 * 保存数据字典索引
	 * @param trie
	 * @param result
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = {"/save"}, method = {RequestMethod.POST,RequestMethod.GET})
	@IgnoreProperties(value={ @IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler","parent","dataDicts","rowStatus","createUser","createTime","updateUser","updateTime" }, pojo = SysTrieTree.class)})
	@ResponseBody
	public Message save(@Valid SysTrieTree trie,BindingResult result/*,@RequestParam("myfiles") MultipartFile[] myfiles,HttpServletResponse response*/) throws IOException{
		/*//服务器存放图片文件夹路径
		String realPath = super.getSession().getServletContext().getRealPath("/upload");
		
		File file = new File(realPath);
		
		//判断文件夹路径是否存在
		if(!file.exists()){
			file.mkdirs();
		}
		
		//遍历所有的上传的文件列表
		for (MultipartFile multipartFile : myfiles) {
			if(multipartFile.getSize()>0){
				File file2 = new File(realPath, multipartFile.getOriginalFilename());
				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),file2);
				trie.setIcon("/upload/"+multipartFile.getOriginalFilename());
			}
		}*/
		
		//保存数据
		Message msg = null;
		if(result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
//			for (FieldError fieldError : fieldErrors) {
//				System.out.println(fieldError.getField()+"|"+fieldError.getDefaultMessage());
//			}
			msg = new Message("V-500",result.toString());
		}  
		
		if(trie!=null){
			try {
				commonManager.save(trie);
				msg = new Message("ok");
			} catch (Exception e) {
				//e.printStackTrace();
				//e.getCause()
				msg = new Message("TT-S-500",e.getLocalizedMessage());
			}
		}else{
			msg = new Message("TT-NVL-000","object is null");
		}
		
		//response.setContentType("text/html");
		return msg;
		
	}
	
	/**
	 * 保存拖拽树节点
	 * @return
	 */
	@RequestMapping(value = {"/save/drop/"}, method = RequestMethod.POST)
	@ResponseBody
	public Message drop(@RequestParam("trieid")String trieid,@RequestParam("overid")String overid){
		Message msg = null;
		
		try {
			SysTrieTree trieTree = commonManager.getOne(trieid, SysTrieTree.class);
			SysTrieTree overidTrieTree = commonManager.getOne(overid, SysTrieTree.class);
			
			if(trieTree!=null && overidTrieTree!=null){
				trieTree.setParent(overidTrieTree);
			}
			
			commonManager.save(trieTree);
			msg = new Message("ok");
		} catch (Exception e) {
			msg = new Message("TT-S-500", e.getLocalizedMessage());
		}
		
		return msg;
	}
	
	/**
	 * 根据ID查询数据字典索引对应的字典数据
	 * @param id
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = {"/list"}, method = RequestMethod.GET)
	@IgnoreProperties(value={ 
			@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler", "children" }, pojo = SysDataDict.class),
			@IgnoreProperty(name = {  "hibernateLazyInitializer", "handler", "fieldHandler","parent", "children" ,"dataDicts","rowStatus","createUser","createTime","updateUser","updateTime","orderBy","pid","type" }, pojo = SysTrieTree.class)})
	@ResponseBody
	public Page<SysDataDict> list(int page,int limit,String sort){
		return dictDao.queryByRange(super.getCustomParameterMap(), page-1, limit,SortUtils.getOrders(sort));
	}
	/**
	 * 四级联动菜单查询
	 * 
	 * @param cascadeMenu
	 *            传值key_val 数据库对应的节点值
	 * @return
	 */
	@RequestMapping(value = "/fourCascade/{cascadeMenu}/{mainQuery}/", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray getFourCascade(@PathVariable String cascadeMenu,
			@PathVariable String mainQuery,String code,String find) {
		String sql = "";
		List<Map<String, Object>> findListMap = null;
		if (Boolean.parseBoolean(mainQuery)) {
			sql = "select sd_.desc_zh_cn as text,sd_.key_val as id from sys_data_dict sd_ "
					+ "where sd_.trie_id=(select st_.id from sys_trie_tree st_ "
					+ "where st_.key_val='"
					+ cascadeMenu
					+ "') and sd_.stat <>0 order by sd_.order_by";
			findListMap = jdbcTemplate.query(sql, new MapRowMapper(true));
			return JSONArray.fromObject(findListMap);
		} else {
				sql = "select sd_2.type_key as text,sd_2.id as id from sys_data_dict sd_2 where sd_2.trie_id=( "
					+ "select st_2.id from sys_trie_tree st_2 where st_2.pid=( "
					+ "select st_1.id from sys_trie_tree st_1 where st_1.id=( "
					+ "select sd_1.trie_id from sys_data_dict sd_1 where sd_1.key_val='"
					+ code + "')))" + "order by sd_2.order_by";
			findListMap = jdbcTemplate.query(sql, new MapRowMapper(true));

		}
		if (!Boolean.parseBoolean(mainQuery)) {
			StringBuffer sbf = new StringBuffer();
			for (Map<String, Object> map : findListMap) {
				String text = (String) map.get("text");
				String[] result = text.split(",");
				for (int i = 0; i < result.length; i++) {
					if (code.equals(result[i])) {
						String id = (String) map.get("id");
						StringBuffer sb = new StringBuffer(id);
						sb.insert(0, '\'');
						sb.insert(id.length() + 1, '\'');
						sbf.append(sb.toString() + ",");

					}
				}
			}
			String sqlIf = "";
			if (sbf.length() > 3) {
				sqlIf = sbf.substring(0, sbf.length() - 1);
			}
			sbf.setLength(0);
			sbf.append("(");
			sbf.append(sqlIf);
			sbf.append(")");
			String findData = "";
			if (sbf.length() < 3) {
				findData = "select sd.desc_zh_cn as text,sd.key_val as id from sys_data_dict sd where sd.id in ('1') ";
			} else {
				findData = "select sd.desc_zh_cn as text,sd.key_val as id from sys_data_dict sd where sd.id in "
						+ sbf.toString() + " ";
			}
			List<Map<String, Object>> findList = jdbcTemplate.query(findData, new MapRowMapper(true));
			return JSONArray.fromObject(findList);
		}
		return null;
	}
}
