package com.mw.framework.interceptor;

import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysAccessLogDao;
import com.mw.framework.domain.SysAccessLog;
import com.mw.framework.domain.SysRequestLog;
import com.mw.framework.domain.SysRole;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.util.PropertiesUtils;
import com.mw.framework.utils.IPAddress;
import com.mw.framework.utils.RSAUtils;

/**
 * 
 * @Project SpringMVC
 * @Copyright © 2008-2013 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.cf.core.interceptor.LogNDCInterceptor
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2013-6-2
 *
 */
public class LogNDCInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(LogNDCInterceptor.class);
	private long startTime = System.currentTimeMillis();
	private Date lastTime;
	
	@Autowired
	private CommonManager commonManager;
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {
		
		//System.out.println("LogNDCInterceptor After completion handle");
		/*String url = request.getRequestURI();
		//ex.printStackTrace();
		//System.out.println(System.currentTimeMillis()-startTime);
		
		Pattern p2 = Pattern.compile(".(jpg|css|js|gif|png|ico|bin|htm|woff|woff2|ttf|eot|swf)");
		Matcher matcher2 = p2.matcher(url.toLowerCase());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		String method = "";
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			method = handlerMethod.getBeanType().getSimpleName()+"."+handlerMethod.getMethod().getName();
		}
		
		if(!matcher2.find() && !url.startsWith("/webscoket/webSocketServer")
				&& !url.startsWith("/core/ext/grid/")
				&& !url.startsWith("/core/dd/list3/")
				&& !url.startsWith("/core/ext/base/dd/table/")
				){
				
				String user = "";
				String ua = request.getHeader("User-Agent");;
				String na = IPAddress.getIP(request);
				String params = request.getParameterMap().size()>0?JSONObject.fromObject(request.getParameterMap()).toString():"";
				Date requestLastTime = lastTime;
				Date responseTime = new Date();
				
				Date requestTime = new Date(startTime);
				
				String responseStatus = (ex==null?"200":"500");
				String responseObject = "";
				String ex_ = ex!=null?ex.getLocalizedMessage():"";
				
				StringBuffer sb = new StringBuffer();
				
				if(url.endsWith("/login")){
					//ia = request.getParameter("ipAddr");
					user = request.getParameter("username");
				}else{
					//ia = (String) request.getSession().getAttribute(Constants.CURR_USER_IA);
					user = (String) request.getSession().getAttribute(Constants.CURR_USER_ID);
					
					@SuppressWarnings("unchecked")
					Set<SysRole> roles = (Set<SysRole>) request.getSession().getAttribute(Constants.CURR_USER_ROLES);
					
					for (SysRole sysRole : roles) {
						if(!"root".equals(sysRole.getDescZhCn())){
							sb.append(sysRole.getDescZhCn());
						}
					}
				}
				
				
				SysRequestLog requestLog = new SysRequestLog(user, sb.toString(),ua, na, na, url, params, requestLastTime, requestTime, responseTime, responseStatus, responseObject, method, ex_,request.getSession().getId());
				commonManager.save(requestLog);
		 }
		
		System.out.println("-------------------REQUEST["+method+"]["+sdf.format(new Date())+"]["+(System.currentTimeMillis()-startTime)+"ms]------------------------");
		System.out.println("");*/
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {
		//System.out.println("LogNDCInterceptor Post-handle");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		
		String enterType=request.getParameter("enterType");
		String Account=request.getParameter("account");
		String password=request.getParameter("pwd");
		Date date_ = new Date();
		String referer = request.getHeader("Referer");
		String requestType = request.getHeader("X-Requested-With");
		String url = request.getRequestURI();
		//System.out.println(url);
		startTime = date_.getTime();
		Date requestLastTime = (Date)request.getSession().getAttribute("REQUEST_LAST_TIME");
		
		lastTime = requestLastTime;
		
		if(url.startsWith("/webscoket/webSocketServer") || url.endsWith("/login")){
			return true;
		}
		
		String na = "";
		 if (request.getHeader("x-forwarded-for") == null) {  
			 na = request.getRemoteAddr();
	     }else{
	        na = request.getHeader("x-forwarded-for");
	     }
		 
		 String[] exceptions = new String[]{"/extjs/4.2.1/resources/css/ext-all.css","/extjs/4.2.1/resources/ext-theme-classic/ext-theme-classic-all.css"};
		 
		 Pattern p2 = Pattern.compile(".(jpg|css|js|gif|png|ico|bin|htm|woff|woff2|ttf|eot|swf)");
		 Matcher matcher2 = p2.matcher(url.toLowerCase());
		 if(matcher2.find()){
			 return true;
		 }
		 
			
		//验证菜单权限
		//@SuppressWarnings("unchecked")
		//Map<String,String> menus = (Map<String, String>) request.getSession().getAttribute(Constants.CURR_ROLE_MENUS_MAP);
		
		//String menu = url.substring(1);
			
		 if(referer!=null){
			 for (String string : exceptions) {
			 	if(referer.endsWith(string)){
			 		return true;
			 	}
			 }
			 /*if(menus!=null && menu.startsWith("core/bpm/randomTask/")){
				if(requestLastTime!=null && startTime-requestLastTime.getTime()<100){
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json;charset=UTF-8");
					response.setHeader("Cache-Control", "no-cache");
					PrintWriter out = response.getWriter();
					ObjectMapper mapper =new ObjectMapper();
					mapper.writeValue(out, new Message("REQ_500","你的操作过快，已被系统拦截!"));
					
					out.flush();
					out.close();
					return false;
				}
				
				request.getSession().setAttribute("REQUEST_LAST_TIME",date_);
			}*/
		 }else if(referer==null && url.endsWith("/favicon.ico")){
			return true;
		}
		
		String ia = null;
		String username = null;
		if(url.endsWith("/login")){
			ia = request.getParameter("ipAddr");
			username = request.getParameter("username");
		}else{
			ia = (String) request.getSession().getAttribute(Constants.CURR_USER_IA);
			username = (String) request.getSession().getAttribute(Constants.CURR_USER_ID);
			//针对其他系统调用本系统页面
			if(username==null && enterType!=null && !StringUtils.isEmpty(enterType) && "281ffc0109b04d1eab1979c2bb9a461a".equals(enterType)){
				request.getSession().setAttribute("isSpecial", true);
				com.mw.framework.controller.IndexController indexController=SpringContextHolder.getBean("indexController");
				HashMap<String,Object> map=RSAUtils.getKeys();
				RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
				RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
				String modulus = publicKey.getModulus().toString();
				String public_exponent = publicKey.getPublicExponent().toString();
				String private_exponent = privateKey.getPrivateExponent().toString();
				RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
				RSAPrivateKey priKey = RSAUtils
						.getPrivateKey(modulus, private_exponent);
				if(StringUtils.isEmpty(password)){
					password="roco201610";
					Account="inner_user";
				}
				String pwd=RSAUtils.encryptByPublicKey(new StringBuilder(password).reverse().toString(), pubKey);
				request.getSession().setAttribute("privateKey",priKey);  
				indexController.login(Account, pwd, "",request);
				username = (String) request.getSession().getAttribute(Constants.CURR_USER_ID);
			}
		}
		String debugRequest = PropertiesUtils.getProperty("debug.console.request");
		
		if(debugRequest!=null && debugRequest.trim().length()>0 && debugRequest.equals("true")){
			try {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				if(username==null ){
					if(requestType==null){
						//throw new Exception("未知用户被禁止操作！");
						response.setCharacterEncoding("UTF-8");
						response.setContentType("text/html;charset=utf-8");
						response.setHeader("Cache-Control", "no-cache");
						response.sendRedirect("/");
						request.getSession().setAttribute("LAST_URL", url);
						PrintWriter out = response.getWriter();
						out.write("【UA-500】Undefined access path:"+url);
						out.flush();
						out.close();
					}else{
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/json;charset=UTF-8");
						response.setHeader("Cache-Control", "no-cache");
						PrintWriter out = response.getWriter();
						ObjectMapper mapper =new ObjectMapper();
						Message message = new Message("UT-403", "服务器不能受理该未知请求!");
						mapper.writeValue(out, message);
						
						out.flush();
						out.close();
					}
					
					//System.out.println("【用户验证失败】"+url);
					//logger.info("【非法请求】["+na+"]["+ia+"]["+username+"]【"+handlerMethod.getBeanType().getSimpleName()+"."+handlerMethod.getMethod().getName()+"】【"+url+"】");
					
					return false;
				}
				//logger.info("【Developer Tips】["+na+"]["+ia+"]["+username+"]【"+handlerMethod.getBeanType().getSimpleName()+"."+handlerMethod.getMethod().getName()+"】【"+url+"】");
			} catch (Exception e) {
				logger.error("【Developer Tips】["+na+"]["+ia+"]["+username+"]Undefined access path："+url);
				
				if(requestType==null){
					//throw new Exception("未知用户被禁止操作！");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html;charset=utf-8");
					response.setHeader("Cache-Control", "no-cache");
					//response.sendRedirect("/404.jsp");
					//request.getSession().setAttribute("LAST_URL", url);
					//System.out.println("【用户验证失败】"+url);
					PrintWriter out = response.getWriter();
					out.write("【UA-500】Undefined access path:"+url);
					out.flush();
					out.close();
				}else{
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json;charset=UTF-8");
					response.setHeader("Cache-Control", "no-cache");
					PrintWriter out = response.getWriter();
					ObjectMapper mapper =new ObjectMapper();
					Message message = new Message("UT-403", "服务器不能受理该未知请求!");
					mapper.writeValue(out, message);
					
					out.flush();
					out.close();
				}
				return false;
			}
		}
		
		String property = PropertiesUtils.getProperty("debug.access.log");
		
		if((property!=null && property.trim().length()>0 && property.equals("true"))||(property==null)){
				
			SysAccessLogDao accessLogDao = SpringContextHolder.getBean("sysAccessLogDao");
			accessLogDao.saveAndFlush(new SysAccessLog(username, request.getHeader("User-Agent"), na, ia, request.getRequestURI()));
		}else{
			//System.out.println("【"+username+"】【"+request.getHeader("User-Agent")+"】【"+request.getRequestURI()+"】");
		}
		
		
		
		//System.out.println("LogNDCInterceptor Pre-handle");
		/*if (1 == 2) {
			PrintWriter out = response.getWriter();
			StringBuilder builder = new StringBuilder();
			builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
			builder.append("alert(\"页面过期，请重新登录\");");
			builder.append("window.top.location.href=\"");
			builder.append("xxx");
			builder.append("/background/index.html\";</script>");
			out.print(builder.toString());
			out.close();
			return false;
		} else {
			// 记录日志
		}

		Map paramsMap = request.getParameterMap();
		for (Iterator<Map.Entry> it = paramsMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = it.next();
			Object[] values = (Object[]) entry.getValue();
			for (Object obj : values) {
				// if (!DataUtil.isValueSuccessed(obj)) {
				//throw new RuntimeException("有非法字符：" + obj);
				// }
			}
		}

		super.preHandle(request, response, handler);*/
		//System.out.println("LOG.interceptor="+mappingURL);
		
		return true;
	}

}
