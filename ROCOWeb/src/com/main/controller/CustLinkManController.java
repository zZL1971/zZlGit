package com.main.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.main.domain.cust.CustContacts;
import com.main.domain.cust.CustHeader;
import com.main.model.cust.CustContactsModel;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.GenericController;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;

@Controller
@RequestMapping("/main/cust/lm/*")
public class CustLinkManController extends GenericController<CustContacts>{

	@Override
	protected String getAppName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] resultJsonExcludeField() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	protected Message save(CustContacts object, BindingResult result) {
		//保存数据
		Message msg = null;
		if(result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
//			for (FieldError fieldError : fieldErrors) {
//				System.out.println(fieldError.getField()+"|"+fieldError.getDefaultMessage());
//			}
			msg = new Message("V-500",result.toString());
		}  
		String kunnr=object.getKunnr();//客户编码
		Set<Object> set=new HashSet<Object>();
		set.add(kunnr);
		List<CustHeader> custHeaders=commonManager.createQueryByIn(CustHeader.class, "kunnr", set);
		if(custHeaders.size()>0){
			CustHeader custHeader=custHeaders.get(0);
			if(object!=null){
				try {
					String id = BeanUtils.getValue(object, "id");
					if(BeanUtils.isNotEmpty(id)){//id不为空则说明是更新
						CustContacts byId;
						try {
							byId = commonManager.getById(id, CustContacts.class);
							BeanUtils.copyProperties(object, byId,false);
							byId.setCustHeader(custHeader);
							commonManager.save(byId);
						} catch (Exception e) {
							object.setCustHeader(custHeader);
							commonManager.save(object,"parnr");
						}
					}else{//新增
						object.setCustHeader(custHeader);//设置外键
						commonManager.save(object,"parnr");
					}
					msg = new Message("ok");
				} catch (Exception e) {
					e.printStackTrace();
					msg = new Message("S-500",e.getLocalizedMessage());
				}
			}
		}else{
			msg = new Message("S-000","无法获取客户！");
		}
		return msg;
	}
	
	@RequestMapping(value = {"/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message get(@PathVariable String id){
		Message msg = null;
		try {
			
			Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
	        formats.put("createTime", new SimpleDateFormat(DateTools.fullFormat));
	        formats.put("updateTime", new SimpleDateFormat(DateTools.fullFormat));
			String sql="select * from CUST_CONTACTS where id='"+id+"'";
			List<Map<String, Object>> query = jdbcTemplate.query(sql,new MapRowMapper(true, formats));
			if(query.size()>0){
				Map<String, Object> map=query.get(0);
				CustContactsModel contacts = new CustContactsModel();
				//BeanUtils.tranMapToObj(map, contacts);
				
				contacts.setId(map.get("id").toString());
				contacts.setParnr(map.get("parnr")==null?"":map.get("parnr").toString());
				contacts.setKunnr(map.get("kunnr").toString());
				contacts.setName1(map.get("name1")==null?"":map.get("name1").toString());
				contacts.setNamev(map.get("namev")==null?"":map.get("namev").toString());
				contacts.setAbtnr(map.get("abtnr")==null?"":map.get("abtnr").toString());
				contacts.setTelf1(map.get("telf1")==null?"":map.get("telf1").toString());
				contacts.setQqNum(map.get("qqNum")==null?"":map.get("qqNum").toString());
				contacts.setVtext(map.get("vtext")==null?"":map.get("vtext").toString());
				contacts.setAnred(map.get("anred")==null?"":map.get("anred").toString());
				contacts.setStatus(map.get("status")==null?"":map.get("status").toString());
				contacts.setCreateUser(map.get("createUser").toString());
				contacts.setCreateTime(map.get("createTime").toString());
				contacts.setUpdateUser(map.get("updateUser").toString());
				contacts.setUpdateTime(map.get("updateTime").toString());
				msg = new Message(contacts);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("C-TT-500", "系统未找到对应属性,非法参数!");
		}
		return msg;
	}

}
