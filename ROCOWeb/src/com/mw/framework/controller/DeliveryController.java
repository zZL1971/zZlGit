package com.mw.framework.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.main.util.ExcellField;
import com.mw.framework.bean.Message;
import com.mw.framework.bean.OrderBy;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.ComponentConfigDao;
import com.mw.framework.dao.LineConfigDao;
import com.mw.framework.domain.ComponentConfig;
import com.mw.framework.domain.LineConfig;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.model.GenernalModel;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.utils.ExcelUtil;

@Controller
@RequestMapping("/delivery/*")
public class DeliveryController extends BaseController{

	@Autowired
	private LineConfigDao lineConfigDao;
	@Autowired
	private ComponentConfigDao componentConfigDao;
	@Autowired
	private CommonManager commonManager;
	
	@RequestMapping(value="/line/index",method=RequestMethod.GET)
	public @ResponseBody ModelAndView indexLinePage(ModelAndView mav){
		mav.setViewName("core/index");
		mav.addObject("module", "DeliveryApp");
		return mav;
	}
	@RequestMapping(value="/component/index",method=RequestMethod.GET)
	public @ResponseBody ModelAndView indexComponentPage(ModelAndView mav){
		mav.setViewName("core/index");
		mav.addObject("module", "ComponentApp");
		return mav;
	}
	@RequestMapping(value="/saveLine",method={RequestMethod.POST})
	@IgnoreProperties(value={@IgnoreProperty(name = {"createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = LineConfig.class)})
	@ResponseBody
	public Message lineSave(@RequestBody GenernalModel<LineConfig> lineConfig,BindingResult result){
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
		List<LineConfig> data = lineConfig.getData();
		if(data.size()>0) {
			commonManager.save(data);
		}
		msg = new Message("SAVE-SUCCESS-200","保存成功^_^");
		return msg;
	}
	@RequestMapping(value="/saveComponent",method={RequestMethod.POST})
	@IgnoreProperties(value={@IgnoreProperty(name = {"createUser","createTime","updateUser","updateTime","rowStatus"}, pojo = ComponentConfig.class)})
	@ResponseBody
	public Message componentSave(@RequestBody GenernalModel<ComponentConfig> componentConfig,BindingResult result){
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
		List<ComponentConfig> data = componentConfig.getData();
		if(data.size()>0) {
			commonManager.save(data);
		}
		msg = new Message("SAVE-SUCCESS-200","保存成功^_^");
		return msg;
	}
	
	@RequestMapping(value = { "/lineList" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean lineList(int page, int limit){
		String lineCode = this.getRequest().getParameter("lineCode");
		Map<String, String[]> params = new HashMap<String, String[]>();
		if(lineCode!=null&&!"".equals(lineCode)) {
			String[] val= {lineCode};
			params.put("ICEQlineCode", val);
		}
		Page<Object> list = commonManager.queryByRange(LineConfig.class, params, page, limit, new OrderBy("createTime","DESC"));
		List<Object> data = list.getContent();
		return new JdbcExtGridBean(list.getTotalPages(), (int)list.getTotalElements(), limit, data);
	}
	@RequestMapping(value = { "/componentList" }, method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean componentList(int page, int limit){
		String line = this.getRequest().getParameter("line");
		String identifyCode = this.getRequest().getParameter("identifyCode");
		String stat = this.getRequest().getParameter("stat");
		String componentName = this.getRequest().getParameter("componentName");
		String materialCode = this.getRequest().getParameter("materialCode");
		String outSourceIdentifyCode = this.getRequest().getParameter("outSourceIdentifyCode");
		Map<String, String[]> params = new HashMap<String, String[]>();
		if(line!=null&&!"".equals(line)) {
			String[] val= {line};
			params.put("ICEQline", val);
		}
		if(identifyCode!=null&&!"".equals(identifyCode)) {
			String[] val= {identifyCode};
			params.put("ICEQidentifyCode", val);
		}
		if(stat!=null&&!"".equals(stat)) {
			String[] val= {stat};
			params.put("ICEQstat", val);
		}
		if(componentName!=null&&!"".equals(componentName)) {
			String[] val= {componentName};
			params.put("ICCPcomponentName", val);
		}
		if(materialCode!=null&&!"".equals(materialCode)) {
			String[] val= {materialCode};
			params.put("ICEQmaterialCode", val);
		}
		if(outSourceIdentifyCode!=null&&!"".equals(outSourceIdentifyCode)) {
			String[] val= {outSourceIdentifyCode};
			params.put("ICEQoutSourceIdentifyCode", val);
		}
		Page<Object> list = commonManager.queryByRange(ComponentConfig.class, params, page, limit, new OrderBy("createTime","DESC"));
		List<Object> data = list.getContent();
		return new JdbcExtGridBean(list.getTotalPages(), (int)list.getTotalElements(), limit, data);
	}
	
	@RequestMapping(value="/deletesLine",method=RequestMethod.POST)
	@ResponseBody
	public Message deletesLine(@RequestParam(value = "ids") String[] ids) {
		Message msg=null;
		for (String id : ids) {
			commonManager.delete(id, LineConfig.class);
		}
		msg=new Message("XML-DELETE-200","删除成功");
		return msg;
	}
	
	@RequestMapping(value="/deletesComponent",method=RequestMethod.POST)
	@ResponseBody
	public Message deletesComponent(@RequestParam(value = "ids") String[] ids) {
		Message msg=null;
		for (String id : ids) {
			commonManager.delete(id, ComponentConfig.class);
		}
		msg=new Message("XML-DELETE-200","删除成功");
		return msg;
	}
	
	@RequestMapping(value="/updateLine",method=RequestMethod.POST)
	@ResponseBody
	public Message updateLine() {
		Message msg=null;
		String id = this.getRequest().getParameter("id");
		String val = this.getRequest().getParameter("val");
		String name = this.getRequest().getParameter("name");
		LineConfig xmlText=commonManager.getById(id, LineConfig.class);
		Class<? extends LineConfig> xml = xmlText.getClass();
		LineConfig controlText=new LineConfig();
		Class<? extends LineConfig> cls = controlText.getClass();
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = xml.getDeclaredMethods();
		if(name!=null&&!"".equals(name)) {
			for (Field field : fields) {
				String fieldName = field.getName();
				if(name.equals(fieldName)&&!fieldName.equals("serialVersionUID")) {
					String setMethod="set"+
							name.substring(0, 1).toUpperCase()+name.substring(1,name.length());
					try {
						Method method = cls.getMethod(setMethod, field.getType());
						if(field.getType().equals(Integer.class)) {
							method.invoke(controlText, Integer.parseInt(val));
						}else if(field.getType().equals(String.class)) {
							method.invoke(controlText, val);
						}
						for (Method meth : methods) {
							String methodName = meth.getName();
							if(methodName.startsWith("set")) {
								if(methodName.indexOf(setMethod)==0) {
									if(field.getType().equals(Integer.class)) {
										meth.invoke(xmlText, Integer.parseInt(val));
									}else if(field.getType().equals(String.class)) {
										meth.invoke(xmlText, val);
									}
									//meth.invoke(xmlText, val);
									break;
								}
							}
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
	
	@RequestMapping(value="/updateComponent",method=RequestMethod.POST)
	@ResponseBody
	public Message updateComponent() {
		Message msg=null;
		String id = this.getRequest().getParameter("id");
		String val = this.getRequest().getParameter("val");
		String name = this.getRequest().getParameter("name");
		ComponentConfig xmlText=commonManager.getById(id, ComponentConfig.class);
		Class<? extends ComponentConfig> xml = xmlText.getClass();
		ComponentConfig controlText=new ComponentConfig();
		Class<? extends ComponentConfig> cls = controlText.getClass();
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = xml.getDeclaredMethods();
		if(name!=null&&!"".equals(name)) {
			for (Field field : fields) {
				String fieldName = field.getName();
				if(name.equals(fieldName)&&!fieldName.equals("serialVersionUID")) {
					String setMethod="set"+
							name.substring(0, 1).toUpperCase()+name.substring(1,name.length());
					try {
						Method method = cls.getMethod(setMethod, field.getType());
						if(field.getType().equals(Integer.class)) {
							method.invoke(controlText, Integer.parseInt(val));
						}else if(field.getType().equals(String.class)) {
							method.invoke(controlText, val);
						}
						for (Method meth : methods) {
							String methodName = meth.getName();
							if(methodName.startsWith("set")) {
								if(methodName.indexOf(setMethod)==0) {
									if(field.getType().equals(Integer.class)) {
										meth.invoke(xmlText, Integer.parseInt(val));
									}else if(field.getType().equals(String.class)) {
										meth.invoke(xmlText, val);
									}
									break;
								}
							}
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
	/**
	 * @param file
	 * @return
	 */
	@RequestMapping(value="/uploadComponent",method= RequestMethod.POST)
	public ResponseEntity<String> uploadComponent(MultipartFile file) {
		String json = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		String fileName = file.getOriginalFilename();
		String fileType=null;
		if(fileName.indexOf(".")!=-1) {
			fileType = fileName.substring(fileName.indexOf(".")+1, fileName.length());
		}
		if(fileType==null||!(fileType.equals("xls")||fileType.equals("xlsx"))) {
			json = "{success: false,msg:'请以Excell 文件格式 传输'}";
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		}
		try {
			Workbook workbook = ExcelUtil.createWorkBook(file.getInputStream());
			Field[] fields = new ComponentConfig().getClass().getDeclaredFields();
			Map<String,Integer> fieldMap = new LinkedHashMap<String, Integer>();
			Map<String,Integer> sheetIndex=new HashMap<String, Integer>();
			//sheetIndex.put("A", 0);
			//sheetIndex.put("B", 1);
			//sheetIndex.put("C", 2);
			//sheetIndex.put("D", 3);
			//sheetIndex.put("E", 4);
			//sheetIndex.put("G", 5);
			//sheetIndex.put("N", 6);
			//sheetIndex.put("M", 7);
			sheetIndex.put("H", 8);
			//sheetIndex.put("W", 9);
			List<ComponentConfig> componentConfigList=new ArrayList<ComponentConfig>();
			Set<Entry<String, Integer>> entrySet = sheetIndex.entrySet();
			for (Entry<String, Integer> entry : entrySet) {
				if(entry.getKey()==null||"".equals(entry.getKey()))continue;
				Integer val = entry.getValue();
				fieldMap.clear();
				componentConfigList.clear();
				Sheet sheet = workbook.getSheetAt(val);
				Row row = sheet.getRow(0);
				for (int j = 0; j < fields.length; j++) {
					Cell cell = row.getCell(j);
					if(cell==null) continue;
					String value = cell.getStringCellValue().trim();
					for (int i = 0; i < fields.length; i++) {
						boolean flg = fields[i].isAnnotationPresent(ExcellField.class);
						if(flg) {
							ExcellField excellField = fields[i].getAnnotation(ExcellField.class);
							String cellVal = excellField.cellVal();
							if(cellVal!=null&&cellVal.equals(value)) {
								fieldMap.put(fields[i].getName(), j);//通过注解 解析得出 对应 Excell 索引
								break;
							}
						}
					}
				}
				for (int j = 1; j < sheet.getLastRowNum(); j++) {
					Row rowReal = sheet.getRow(j);
					if(rowReal==null) {
						continue;
					}
					ComponentConfig componentConfig=new ComponentConfig();
					componentConfig.setLine(entry.getKey());
					//componentConfig.setComponentName(fieldMap.get("componentName")!=null?rowReal.getCell(fieldMap.get("componentName")).getStringCellValue():"");
					String componentName = autoTypeValCheck(fieldMap, rowReal, "componentName", String.class);
					componentConfig.setComponentName(componentName);
					String identifyCode = autoTypeValCheck(fieldMap, rowReal, "identifyCode", String.class);
					//componentConfig.setIdentifyCode(fieldMap.get("identifyCode")!=null?rowReal.getCell(fieldMap.get("identifyCode")).getStringCellValue():"");
					componentConfig.setIdentifyCode(identifyCode);
					Integer dnDelivery = autoTypeValCheck(fieldMap, rowReal, "dnDelivery", Integer.class);
					//componentConfig.setDnDelivery(fieldMap.get("dnDelivery")!=null?(int) (rowReal.getCell(fieldMap.get("dnDelivery")).getNumericCellValue()):0);
					componentConfig.setDnDelivery(dnDelivery);
					Integer dwDelivery = autoTypeValCheck(fieldMap, rowReal, "dwDelivery", Integer.class);
					//componentConfig.setDwDelivery(fieldMap.get("dwDelivery")!=null?(int) (rowReal.getCell(fieldMap.get("dwDelivery")).getNumericCellValue()):0);
					componentConfig.setDwDelivery(dwDelivery);
					Integer repairOrderDelivery = autoTypeValCheck(fieldMap, rowReal, "repairOrderDelivery", Integer.class);
					//componentConfig.setRepairOrderDelivery(fieldMap.get("repairOrderDelivery")!=null?(int) (rowReal.getCell(fieldMap.get("repairOrderDelivery")).getNumericCellValue()):0);
					componentConfig.setRepairOrderDelivery(repairOrderDelivery);
					String outSourceIdentifyCode = autoTypeValCheck(fieldMap, rowReal, "outSourceIdentifyCode", String.class);
					//componentConfig.setOutSourceIdentifyCode(fieldMap.get("outSourceIdentifyCode")!=null?rowReal.getCell(fieldMap.get("outSourceIdentifyCode")).getStringCellValue():"");
					componentConfig.setOutSourceIdentifyCode(outSourceIdentifyCode);
					String materialCode = String.valueOf(autoTypeValCheck(fieldMap, rowReal, "materialCode", Long.class));
					//componentConfig.setMaterialCode(fieldMap.get("materialCode")!=null?String.valueOf(rowReal.getCell(fieldMap.get("materialCode")).getNumericCellValue()):"");
					componentConfig.setMaterialCode("null".equals(materialCode)?null:materialCode);
					componentConfig.setStat("1");
					componentConfig.setIsStandard("0");
					if(fieldMap.get("materialCode")!=null&&rowReal.getCell(fieldMap.get("materialCode"))!=null)
					componentConfig.setIsStandard("1");
					if(checkIsNotEmpty(componentConfig)) componentConfigList.add(componentConfig);
				}
				componentConfigDao.save(componentConfigList);
			}
			json = "{success: true,msg:'导入成功'}";
		} catch (IOException e) {
			e.printStackTrace();
			json = "{success: false,msg:'"+e.getLocalizedMessage()+"'}";
		}
		return new ResponseEntity<String>(json, responseHeaders,
				HttpStatus.OK);
	}
	public <T> T autoTypeValCheck(Map<String,Integer> fieldMap,Row row,String fieldName,Class<?> cls) {
		Object obj=null;
		Integer fieldInd = fieldMap.get(fieldName);
		if(fieldInd==null) return (T) obj;
		Cell cell = row.getCell(fieldInd.intValue());
		if(!ExcelUtil.checkCellIsNotNull(cell))return (T) obj;
		if(cls.getName().equals("java.lang.String")) {
			obj = cell.getStringCellValue();
		}else if(cls.getName().equals("java.lang.Integer")) {
			obj = (int)cell.getNumericCellValue();
		}else if(cls.getName().equals("java.lang.Long")) {
			obj = (long)cell.getNumericCellValue();
		}
		return (T) obj;
	}
	
	public boolean checkIsNotEmpty(ComponentConfig componentConfig) {
		boolean flag=false;
		if(componentConfig.getIdentifyCode()!=null&&!"".equals(componentConfig.getIdentifyCode())) {
			flag = true;
		}else if(componentConfig.getMaterialCode()!=null&&!"".equals(componentConfig.getMaterialCode())&&!"null".equals(componentConfig.getMaterialCode())) {
			flag = true;
		}else if(componentConfig.getOutSourceIdentifyCode()!=null&&!"".equals(componentConfig.getOutSourceIdentifyCode())&&!"null".equals(componentConfig.getOutSourceIdentifyCode())) {
			flag = true;
		}
		return flag;
	}
}
