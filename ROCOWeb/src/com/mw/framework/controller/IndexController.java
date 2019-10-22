/**
 *
 */
package com.mw.framework.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Constants.Language;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysUserDao;
import com.mw.framework.domain.SysAccessLog;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.model.OnLineUserModel;
import com.mw.framework.utils.IPAddress;
import com.mw.framework.utils.PwdUtil;
import com.mw.framework.utils.RSAUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.IndexController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-2-28
 *
 */
@Controller
@RequestMapping("/*")
public class IndexController extends BaseController{

	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private CommonManager commonManager;
	
	/**
	 * 跳转到主界面
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(){
		return null;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Message login(String username,String password,String checkcode,HttpServletRequest request){
		Message msg = null;
		//System.out.println("login控制器session:"+super.getSession().getId());
		/*if(username.startsWith("cl")){
			msg = new Message("LG-K-500", "登录异常");
			return msg;
		}*/
		if(username.startsWith("cl")||username.startsWith("kf")||username.startsWith("cw")){
			if(IPAddress.isOutside(request)){
				msg=new Message("ERROR-LOGIN","禁止登陆");
				return msg;
			}
		}
		//获取当前session验证码
		String validCode = super.getSessionAttr(Constants.VALID_CODE);
		/*if (true//validCode!=null
				) {
			
			
		}else{
			msg = new Message("LO-ER-105", "验证码过期,请重新输入验证码");
		}*/
		//String debug = super.getRequest().getParameter("debug");
		//if(!debug.isEmpty()){
			checkcode = validCode;
		//}
		if (//validCode.toUpperCase().equals(checkcode.toUpperCase())
				true
				) {
			
			RSAPrivateKey privateKey = (RSAPrivateKey)super.getSession().getAttribute("privateKey");  
			String descrypedPwd = null;
			try {
				descrypedPwd = new StringBuilder(RSAUtils.decryptByPrivateKey(password, privateKey)).reverse().toString(); //解密后的密
				descrypedPwd = PwdUtil.encrypt(descrypedPwd);
			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("LG-K-500", "登录异常");
				return msg;
			}
			
			//用户缓存列表
			//LinkedHashMap<String, OnLineUserModel> objectForCache = super.getObjectForCache(Constants.LOGIN_USER_LIST);
			StringBuffer sb1 = new StringBuffer();
			List<OnLineUserModel> usermodelList = new ArrayList<OnLineUserModel>();
			/*if(objectForCache!=null){
				//已登录该用户的信息
				Set<Entry<String,OnLineUserModel>> entrySet = objectForCache.entrySet();
				for (Entry<String, OnLineUserModel> entry : entrySet) {
					OnLineUserModel usermodel = entry.getValue();
					
					//判断session是否过期
					if(usermodel.getUserId().equals(username)){
						sb1.append(usermodel.getCreateTime()+" "+usermodel.getNa()+" "+usermodel.getIa()+" "+usermodel.getSession()+"<br/>");
						usermodelList.add(usermodel);
					}
				}
				 
			}*/
			
			Map<String,String[]> params = new HashMap<String,String[]>();
			params.put("ICEQid",new String[]{username});
			params.put("ICEQpassword",new String[]{descrypedPwd});
			
			List<SysUser> users = commonManager.queryByRange(SysUser.class,params);
			
			/*SysUser sysUser = sysUserDao.findByIdAndPassword(username, descrypedPwd);
			*/
			if (users.size()>0) {
				/*if(sb1.length()>0){
					msg = new Message("LO_VD_500","该账号已被其他客户端登录：<br/>"+sb1);
					msg.put("data", usermodelList);
				}else{
				}*/
				msg = new Message("成功!");
				Object attribute = super.getSession().getAttribute("LAST_URL");
				msg.put("lasturl", attribute);
				if(attribute!=null){
					super.getSession().removeAttribute("LAST_URL");
				}
				
				String na = IPAddress.getIP(super.getRequest());
				boolean ipCheck = Pattern.matches("(^172\\.16\\.[0-9]{1,3}\\.[0-9]{1,3}$)|(^192\\.168\\.10\\.[0-9]{1,3}$)|(^192\\.168\\.1\\.[0-9]{1,3}$)", na);
				SysUser sysUser = users.get(0);
				/*if(!ipCheck) {
					if(sysUser.getCustHeader()==null) {
						msg = new Message("LG-K-500", "禁止内部员工外网登录");
						return msg;
					}
				}*/
				
				int status = sysUser.getStatus();//1:正常,0:冻结
				if(status == 0){
					msg = new Message("LG-O-500", "用户已被禁用！");
					return msg;
				}
				
				
				super.getSession().removeAttribute(Constants.VALID_CODE);
				super.getSession().setAttribute(Constants.CURR_USER_ID, sysUser.getId());
				super.getSession().setAttribute(Constants.CURR_USER_LOGIN_NO, sysUser.getLoginNo());
				super.getSession().setAttribute(Constants.CURR_USER_NAME, sysUser.getUserName());
				super.getSession().setAttribute(Constants.CURR_USER, sysUser);
				if(sysUser.getCustHeader()!=null){
					super.getSession().setAttribute(Constants.CURR_USER_KUNNR, sysUser.getCustHeader().getKunnr());
					super.getSession().setAttribute(Constants.CURR_USER_KUNNR_CUP,sysUser.getCustHeader().getZzcupboard());
					super.getSession().setAttribute(Constants.CURR_USER_KUNNR_TIMBER,sysUser.getCustHeader().getZztimber());
				}
				super.getSession().setAttribute(Constants.CURR_USER_IA, na);
				super.getSession().setAttribute(Constants.CURR_USER_ROLES, sysUser.getRoles());
				super.getSession().setAttribute(Constants.CURR_SAP_CODE, sysUser.getKunnr());
				
				//设置菜单
				List<Map<String, Object>> loginUserMenus = super.getLoginUserMenus();
				super.getSession().setAttribute(Constants.CURR_ROLE_MENUS, loginUserMenus);
				super.getSession().setAttribute(Constants.CURR_ROLE_MENUS_MAP, super.getLoginUserMenuForMap(loginUserMenus));
				List<Map<String, Object>> rears = jdbcTemplate.queryForList("SELECT ST.REAR FROM SYS_TASK ST WHERE ST.ID='"+sysUser.getId()+"'");
				String rear="";
				if(rears.size()>0) {
					rear = (String) rears.get(0).get("REAR");
				}
				super.getSession().setAttribute("GROUPID", rear);
				//添加登录记录
				String userAgent = super.getRequest().getHeader("User-Agent");
				//String ia = super.getRequest().getParameter("ipaddr");
				String url = super.getRequest().getRequestURI();
				
				super.getSession().setAttribute(Constants.CURR_USER_NA,na);
				
				/*if(objectForCache!=null){
					Set<SysRole> roles = sysUser.getRoles();
					StringBuffer sb = new StringBuffer();
					for (SysRole sysRole : roles) {
						sb.append(sysRole.getDescZhCn()+";");
					}
					objectForCache.put(super.getSession().getId(), new OnLineUserModel(sysUser.getId(), sysUser.getUserName(),super.getLoginUserRolesId(), DateTools.getDateAndTime(DateTools.fullFormat), DateTools.getDateAndTime(DateTools.fullFormat),super.getSessionId(),na,ia));
					super.setObjectForCache(Constants.LOGIN_USER_LIST, objectForCache);
				}else{
					objectForCache = new LinkedHashMap<String, OnLineUserModel>();
					objectForCache.put(super.getSession().getId(), new OnLineUserModel(sysUser.getId(), sysUser.getUserName(),super.getLoginUserRolesId(), DateTools.getDateAndTime(DateTools.fullFormat), DateTools.getDateAndTime(DateTools.fullFormat),super.getSessionId(),na,ia));
					super.setObjectForCache(Constants.LOGIN_USER_LIST, objectForCache);
				}*/
				//设置最后访问时间
				super.getSession().setAttribute("REQUEST_LAST_TIME", new Date());
				commonManager.save(new SysAccessLog(username, userAgent, na, na, url));
				
				String acceptLanguage = super.getRequest().getHeader("accept-language");
				if(acceptLanguage!=null && acceptLanguage.length()>0){
					String primary =acceptLanguage.substring(0,3).replace("-", "_");
					String sub =acceptLanguage.substring(3,5).toUpperCase();
					Language language = Constants.Language.valueOf(primary+sub);
					super.getSession().setAttribute(Constants.language, language.name()/*.replace("_", "-")*/);
				}
				
				super.removeSessionAttr(Constants.VALID_CODE);
			} else {
				msg = new Message("LG-O-500", "用户名密码错误");
			}
		} else {
			msg = new Message("LG-O-500", "验证码输入错误");
		}
		return msg;
	}
	@RequestMapping(value = "/mpwd", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message mpwd(String oldpwd,String pwd){
		Message msg = null;
		
		if(oldpwd!=null && oldpwd.trim().length()>0 && pwd!=null && pwd.trim().length()>0){
			
			try {
				
				RSAPrivateKey privateKey = (RSAPrivateKey)super.getSession().getAttribute("privateKey");
				String username = super.getSessionAttr(Constants.CURR_USER_ID);
				
				String descrypedOldPwd = new StringBuilder(RSAUtils.decryptByPrivateKey(oldpwd, privateKey)).reverse().toString();
				
				SysUser sysUser = commonManager.getById(username, SysUser.class);
				if(sysUser!=null){
					if(sysUser.getPassword().equals(PwdUtil.encrypt(descrypedOldPwd))){
						String descrypedPwd = null;
						
						descrypedPwd = new StringBuilder(RSAUtils.decryptByPrivateKey(pwd, privateKey)).reverse().toString(); //解密后的密
						sysUser.setPassword(PwdUtil.encrypt(descrypedPwd));
						
						commonManager.save(sysUser);
						
						msg = new Message("密码修改成功!");
					}else{
						msg = new Message("SPD-502", "原密码不正确!");
					}
				}else{
					msg = new Message("SPD-501", "用户不存在，无法修改密码!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg = new Message("SPD-503", "存在重复登录客户端，无法更改密码!");
				return msg;
			}
		}else{
			msg = new Message("SPD-500", "密码不能为空!");
		}
		
		
		return msg;
	}
	
	//获取客户电话号码
	@RequestMapping(value = "/mgettelnum", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message mgettelnum(){
		Message msg = null;
		Object num = null;
		Map<String, Object> map = new HashMap<String, Object>();
		Object userId=this.getRequest().getSession().getAttribute(Constants.CURR_USER_ID);
		if(userId!=null){
			String sql = " select su.tel from sys_user su where su.id = ?";
			List<Map<String,Object>> list= jdbcTemplate.queryForList(sql,new Object[]{userId});
			if(list!=null && list.size()>0){
				num=list.get(0).get("TEL");
			}
			map.put("Tnum", num);
			msg = new Message(map);
		}else {
			msg = new Message("MM-querytelnum-500", "数据加载失败!");
		}
		
		return msg;
	}

	@RequestMapping(value = "/mtel", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message mtel(String kunnr,String telnum,String surenum){
		Message msg = null;
		Object userId=this.getRequest().getSession().getAttribute(Constants.CURR_USER_ID);
		//手机 号码不能为空
		if(telnum!=null && telnum.trim().length()>0 && surenum!=null && surenum.trim().length()>0){
			if(userId!=null){
				//新手机号码 与 确认手机号码必须一致,才能更新
				if(telnum.trim().equals(surenum.trim())){ 
					//更新客户手机号
					//String sql = "update cust_header cu set cu.telnum = "+telnum+"  where cu.kunnr = '"+ kunnr+"'";
					String sql = " update sys_user su set su.tel =  "+telnum+"   where su.kunnr = '"+ kunnr+"'";
					
					jdbcTemplate.update(sql);
					msg = new Message("手机号保存成功!");
				}else{
					msg = new Message("新手机号码 与 确认手机号码必须一致!");
				}
			}else{
				msg = new Message("无SAP编码客户不需要维护手机号!");
			}
		}else{
			msg = new Message("SPD-500", "手机号不能为空!");
		}
		
		return msg;
	}
	
	/**
	 * 退出系统
	 * @return
	 */
	@RequestMapping(value = "/loginout", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message loginout(){

		//用户缓存列表
		LinkedHashMap<String, Object> objectForCache = super.getObjectForCache(Constants.LOGIN_USER_LIST);
		
		if(objectForCache!=null){
			objectForCache.remove(super.getSessionId());
			super.setObjectForCache(Constants.LOGIN_USER_LIST, objectForCache);
		}
		
		//注销session
		super.getSession().invalidate();
		
		return new Message("ok");
	}
	
	/**
	 * 重新登录设置RSA
	 * @return
	 */
	@RequestMapping(value = "/relogin", method = {RequestMethod.POST})
	@ResponseBody
	public Message relogin(){
		Message msg = null;
		try {
			HashMap<String, Object> map = RSAUtils.getKeys();    
			//生成公钥和私钥    
			RSAPublicKey publicKey = (RSAPublicKey) map.get("public");    
			RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");  
			  
			super.getSession().setAttribute("privateKey", privateKey);//私钥保存在session中，用于解密  
			  
			//公钥信息保存在页面，用于加密  
			String publicKeyExponent = publicKey.getPublicExponent().toString(16);  
			String publicKeyModulus = publicKey.getModulus().toString(16);  
			
			msg = new Message("ok");
			msg.put("publicKeyExponent", publicKeyExponent);
			msg.put("publicKeyModulus", publicKeyModulus);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			msg = new Message("REA_LO_500", "初始化异常");
		}
		return msg;
	}
	@RequestMapping(value = "/loginPort", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String loginPort(String authToken,HttpServletRequest request,HttpServletResponse response){
		StringBuffer sbf=new StringBuffer("handleResponse(");
		BASE64Decoder decoder = new BASE64Decoder();
		String decode=null;
		System.out.println("aaaa");
		try {
			 decode= new String ( decoder.decodeBuffer(authToken));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String username=null;
		String time=null;
		if(decode!=null){
			JSONArray array=JSONArray.fromObject("["+decode+"]");
			JSONObject obj = array.getJSONObject(0);
			username = obj.getString("user_id");
			time = obj.getString("time");
		}else{
			sbf.append("{'msgCode':'系统出错，请联系管理员','msgStatus':'LG-O-500','success':'false'})");
			return sbf.toString();
		}	
		this.getSession().setMaxInactiveInterval(30);
		if (true) {
			if (true) {
				String descrypedPwd = null;
				List<Map<String, Object>> pwd = jdbcTemplate.queryForList("SELECT PASSWORD FROM SYS_USER SU WHERE SU.ID='"+username+"'");
				if(pwd.size()>0){
					descrypedPwd=(String) pwd.get(0).get("PASSWORD");
					StringBuffer sb1 = new StringBuffer();
					Map<String,String[]> params = new HashMap<String,String[]>();
					params.put("ICEQid",new String[]{username});
					params.put("ICEQpassword",new String[]{descrypedPwd});
					List<SysUser> users = commonManager.queryByRange(SysUser.class,params);
					if (users.size()>0) {
						if(sb1.length()>0){
							sbf.append("{'msgCode':'该账号已被其他客户端登录','msgStatus':'LO_VD_500','success':'false'})");
							return sbf.toString();
						}else{
							sbf.append("{'msgCode':'成功!','msgStatus':'LO_VD_500','success':'true'");
						}
						
						Object attribute = super.getSession().getAttribute("LAST_URL");
						if(attribute!=null){
							sbf.append(",'lasturl':'"+attribute.toString()+"'");
						}
						if(attribute!=null){
							super.getSession().removeAttribute("LAST_URL");
						}
						String na = IPAddress.getIP(super.getRequest());
						SysUser sysUser = users.get(0);
						int status = sysUser.getStatus();//1:正常,0:冻结
						if(status == 0){
							sbf.append("{'msgCode':'用户已被禁用！','msgStatus':'LG-O-500','success':'false'})");
							return sbf.toString();
						}
						super.getSession().removeAttribute(Constants.VALID_CODE);
						super.getSession().setAttribute(Constants.CURR_USER_ID, sysUser.getId());
						super.getSession().setAttribute(Constants.CURR_USER_LOGIN_NO, sysUser.getLoginNo());
						super.getSession().setAttribute(Constants.CURR_USER_NAME, sysUser.getUserName());
						super.getSession().setAttribute(Constants.CURR_USER, sysUser);
						if(sysUser.getCustHeader()!=null){
							super.getSession().setAttribute(Constants.CURR_USER_KUNNR, sysUser.getCustHeader().getKunnr());
							super.getSession().setAttribute(Constants.CURR_USER_KUNNR_CUP,sysUser.getCustHeader().getZzcupboard());
							super.getSession().setAttribute(Constants.CURR_USER_KUNNR_TIMBER,sysUser.getCustHeader().getZztimber());
						}
						super.getSession().setAttribute(Constants.CURR_USER_IA, na);
						super.getSession().setAttribute(Constants.CURR_USER_ROLES, sysUser.getRoles());						
						//设置菜单
						List<Map<String, Object>> loginUserMenus = super.getLoginUserMenus();
						super.getSession().setAttribute(Constants.CURR_ROLE_MENUS, loginUserMenus);
						super.getSession().setAttribute(Constants.CURR_ROLE_MENUS_MAP, super.getLoginUserMenuForMap(loginUserMenus));
						//添加登录记录
						String userAgent = super.getRequest().getHeader("User-Agent");
						String url = super.getRequest().getRequestURI();
						
						super.getSession().setAttribute(Constants.CURR_USER_NA,na);
						//设置最后访问时间
						super.getSession().setAttribute("REQUEST_LAST_TIME", new Date());
						commonManager.save(new SysAccessLog(username, userAgent, na, na, url));
						
						String acceptLanguage = super.getRequest().getHeader("accept-language");
						if(acceptLanguage!=null && acceptLanguage.length()>0){
							String primary =acceptLanguage.substring(0,3).replace("-", "_");
							String sub =acceptLanguage.substring(3,5).toUpperCase();
							Language language = Constants.Language.valueOf(primary+sub);
							super.getSession().setAttribute(Constants.language, language.name()/*.replace("_", "-")*/);
						}
						super.removeSessionAttr(Constants.VALID_CODE);
						
					}
				}else{
					sbf.append("{'msgCode':'系统出错，请联系管理员','msgStatus':'LG-O-500','success':'false'})");
					return sbf.toString();
				}
				
			}
		}
		response.setHeader("Access-Control-Allow-Origin", "*");  
        response.setHeader("Access-Control-Allow-Methods", "POST");  
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
		sbf.append("})");
		return sbf.toString();
		
	}
	@RequestMapping(value="/se/r/")
	@ResponseBody
	public Message getSeRsa(){
		Message msg = null;
		
		
		
		return msg;
	}
	
	public String navigation(){
		return null;
	}
}
