package com.mw.framework.controller;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mw.framework.bean.Message;
import com.mw.framework.bean.OrderBy;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysXmlControlTextDao;
import com.mw.framework.domain.SysXmlControlText;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.model.GenernalModel;
import com.mw.framework.model.JdbcExtGridBean;

@Controller
@RequestMapping("/xmlcontrol/*")
public class XMLController extends BaseController{

	@Autowired
	private SysXmlControlTextDao sysXmlControlDao;
	@Autowired
	private CommonManager commonManager;
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public @ResponseBody ModelAndView indexPage(ModelAndView mav){
		mav.setViewName("core/index");
		mav.addObject("module", "XMLApp");
		return mav;
	}
	
	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean list(int page, int limit){
		String textCode = this.getRequest().getParameter("textCode");
		String type = this.getRequest().getParameter("type");
		String saleFor = this.getRequest().getParameter("saleFor");
		String textDesc = this.getRequest().getParameter("textDesc");
		Map<String, String[]> params = new HashMap<String, String[]>();
		if(textCode!=null&&!"".equals(textCode)) {
			String[] val= {textCode};
			params.put("ICEQtextCode", val);
		}
		if(type!=null&&!"".equals(type)) {
			String[] val= {type};
			params.put("ICEQtype", val);
		}
		if(saleFor!=null&&!"".equals(saleFor)) {
			String[] val= {saleFor};
			params.put("ICEQsaleFor", val);
		}
		if(textDesc!=null&&!"".equals(textDesc)) {
			String[] val= {textDesc};
			params.put("ICEQtextDesc", val);
		}
		Page<Object> list = commonManager.queryByRange(SysXmlControlText.class, params, page, limit, new OrderBy("textCode","DESC"));
		List<Object> data = list.getContent();
		return new JdbcExtGridBean(list.getTotalPages(), (int)list.getTotalElements(), limit, data);
	}
	
	
	@RequestMapping(value="/save",method={RequestMethod.POST})
	@IgnoreProperties(value={@IgnoreProperty(name = {"createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = SysXmlControlText.class)})
	@ResponseBody
	public Message xmlTextSave(@RequestBody GenernalModel<SysXmlControlText> xmlText,BindingResult result){
		Message msg=null;
		if(result.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			// 显示错误信息
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getField() + "|"
						+ fieldError.getDefaultMessage());
			}
			msg = new Message("XML-SAVE-500", sb.toString());
			return msg;
		}
		List<SysXmlControlText> data = xmlText.getData();
		for (SysXmlControlText text : data) {
			Integer textCode = sysXmlControlDao.findByTextCode();
			if(textCode!=null) {
				int code = textCode;
				code+=10;
				textCode=code;
			}else {
				textCode=10;
			}
			text.setTextCode(textCode);
			commonManager.save(text);
		}
		msg = new Message("SAVE-SUCCESS-200","保存成功^_^");
		return msg;
	}
	@RequestMapping(value="/deletes",method=RequestMethod.POST)
	@ResponseBody
	public Message deletes(@RequestParam(value = "ids") String[] ids) {
		Message msg=null;
		for (String id : ids) {
			commonManager.delete(id, SysXmlControlText.class);
		}
		msg=new Message("XML-DELETE-200","删除成功");
		return msg;
	}
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public Message update() {
		Message msg=null;
		String id = this.getRequest().getParameter("id");
		String val = this.getRequest().getParameter("val");
		String name = this.getRequest().getParameter("name");
		SysXmlControlText xmlText=commonManager.getById(id, SysXmlControlText.class);
		Class<? extends SysXmlControlText> xml = xmlText.getClass();
		SysXmlControlText controlText=new SysXmlControlText();
		Class<? extends SysXmlControlText> cls = controlText.getClass();
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = xml.getDeclaredMethods();
		if(name!=null&&!"".equals(name)) {
			for (Field field : fields) {
				String fieldName = field.getName();
				System.out.println(field.getName());
				if(name.equals(fieldName)&&!fieldName.equals("serialVersionUID")) {
					String setMethod="set"+
							name.substring(0, 1).toUpperCase()+name.substring(1,name.length());
					try {
						Method method = cls.getMethod(setMethod, field.getType());
						method.invoke(controlText, val);
						for (Method meth : methods) {
							String methodName = meth.getName();
							if(methodName.startsWith("set")) {
								if(methodName.indexOf(setMethod)==0) {
									meth.invoke(xmlText, val);
									break;
								}
							}
							System.out.println(methodName);
						}
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else {
			
		}
		commonManager.save(xmlText);
		msg=new Message("XML-UPDATE-200","修改成功");
		return msg;
	}
}
