/**
 *
 */
package com.mw.framework.commons;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mw.framework.bean.Message;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.utils.BeanUtils;

/**
 * 默认CRUD操作类，可在此基础上面进行二次拓展
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.commons.GenericController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-12
 *
 */
public abstract class GenericController<T> extends BaseController{

	protected abstract String getAppName();
	protected abstract String[] resultJsonExcludeField();
	
	private Class<T> entityClass;
	
	@SuppressWarnings("unchecked")
	public GenericController() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments(); 
		entityClass = (Class<T>) params[0];
	}

	@Autowired
	protected CommonManager commonManager;
	
	private String[] getExcludeFields(){
		String[] excludeDefault = new String[]{"hibernateLazyInitializer", "handler", "fieldHandler", "children","parent","sort"};
		
		String[] excludeField = resultJsonExcludeField();
		String[] exclude = null;
		if(excludeField!=null){
			exclude =  new String[excludeDefault.length + excludeField.length];  
			System.arraycopy(excludeDefault, 0, exclude, 0, excludeDefault.length);  
	        System.arraycopy(excludeField, 0, exclude, excludeDefault.length, excludeField.length);
		}else{
			exclude = excludeDefault;
		}
		
		return exclude;
	}
	
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public ModelAndView index(ModelAndView modelAndView){
		modelAndView.setViewName("core/index");
		modelAndView.getModelMap().put("module",entityClass.getSimpleName()+"App");
		modelAndView.getModelMap().put("moduleTitle", getAppName());
		return modelAndView;
	}
	
	@RequestMapping(value = {"/save"}, method = RequestMethod.POST)
	@ResponseBody
	protected Message save(T object,BindingResult result) {
		//保存数据
		Message msg = null;
		if(result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				System.out.println(fieldError.getField()+"|"+fieldError.getDefaultMessage());
			}
			msg = new Message("V-500",result.toString());
		}  
		
		if(object!=null){
			try {
				String id = BeanUtils.getValue(object, "id");
				if(BeanUtils.isNotEmpty(id)){
					Object byId;
					try {
						byId = commonManager.getById(id, entityClass);
						BeanUtils.copyProperties(object, byId,false);
						commonManager.save(byId);
					} catch (Exception e) {
						commonManager.save(object);
					}
				}else{
					commonManager.save(object);
				}
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

	
	/**
	 * 删除
	 * @return
	 */
	@RequestMapping(value = {"/delete"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message delete(@RequestParam(value = "ids") String[] ids){
		Message msg = null;
		try {
			
			commonManager.delete(ids, entityClass);
			msg = new Message("ok");
		} catch (Exception e) {
			//e.printStackTrace();
			msg = new Message("TT-D-500", e.getLocalizedMessage());
		}
		return msg;
	}
	
	/**
	 * 根据ID查询
	 * @param id
	 * @param page
	 * @param limit
	 * @return
	 */
	
	@RequestMapping(value = {"/list"}, method = RequestMethod.GET)
	@ResponseBody
	public JSONObject list(int page,int limit,String sort){
		Page<Object> queryByRange = commonManager.queryByRange(entityClass,super.getCustomParameterMap(), page-1, limit);
		return JSONObject.fromObject(queryByRange, super.getJsonConfig(getExcludeFields()));
	}
	 
}
