package com.mw.framework.interceptor;

import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;

/**
 * 
 * @Project SpringMVC
 * @Copyright © 2008-2013 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.cf.core.interceptor.PermissionsInterceptor
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2013-6-2
 *
 */
public class PermissionsInterceptor implements HandlerInterceptor {

 
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
			Exception arg3) throws Exception {
		//System.out.println("PermissionsInterceptor After completion handle");  
	}
 
	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
			ModelAndView arg3) throws Exception {
		 //System.out.println("PermissionsInterceptor Post-handle");  
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2)
			throws Exception {
		//System.out.println(request.getSession().getId());
		//System.out.println("PermissionsInterceptor Pre-handle");  
		String url = request.getRequestURI();
		//String requestType = request.getHeader("X-Requested-With");
		String referer = request.getHeader("Referer");
	
		Pattern p2 = Pattern.compile(".(jpg|css|js|gif|png|ico|bin|htm|woff|woff2|ttf|eot|swf)");
		Matcher matcher2 = p2.matcher(url.toLowerCase());
		if(!matcher2.find()){
			
			//验证菜单权限
			@SuppressWarnings("unchecked")
			Map<String,String> menus = (Map<String, String>) request.getSession().getAttribute(Constants.CURR_ROLE_MENUS_MAP);
			String userName=(String)request.getSession().getAttribute(Constants.CURR_USER_NAME);
			String menu = null;
			if(referer==null){
				//标准请求
				
				menu = url.substring(1);
				
				if(url.indexOf("/loginPort")!=-1||menu.equals("core/main/")||url.endsWith("/getUserdata")||url.endsWith("/getActUserdata") || menu.startsWith("process") || menu.startsWith("core/report/") || menu.startsWith("webscoket/webSocketServer")|| menu.startsWith("core/ext/base/pic") || 
						//"inner_user".equals(userName)
						(request.getSession().getAttribute("isSpecial")!=null && (Boolean)request.getSession().getAttribute("isSpecial")==true)
						){
					return true;
				}else if(menus.get(menu)==null){
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html;charset=utf-8");
					response.setHeader("Cache-Control", "no-cache");
					System.out.println("未授权标准请求："+menu);
					response.sendRedirect("/");
					return false;
				}
			}else{
				//异步请求
				
				Pattern p=Pattern.compile("[a-zA-z]+://[0-9a-zA-z.]*(:[0-9]{2,4})*/");  
				Matcher m=p.matcher(referer);  
				while(m.find()){  
					menu = referer.substring(referer.indexOf(m.group())+m.group().length());
				}  
				
				Pattern p1=Pattern.compile("\\u003F([0-9a-zA-z.]*=[0-9a-zA-z.\\s]*(&)*)*");  
				Matcher m1=p1.matcher(menu);  
				while(m1.find()){
					menu = menu.substring(menu.indexOf(m1.group())+m1.group().length());
				}
				Object cc=request.getSession().getAttribute(Constants.CURR_USER_NAME);
				if("/loginPort".equals(url)||menu.equals("core/main/") || menu.startsWith("process") || menu.startsWith("core/report/") || menu.startsWith("core/ext/base/pic") || 
						//"inner_user".equals(request.getSession().getAttribute(Constants.CURR_USER_NAME))
						(request.getSession().getAttribute("isSpecial")!=null && (Boolean)request.getSession().getAttribute("isSpecial")==true)
						){
					return true;
				}else if(menus!=null && menus.get(menu)==null){
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json;charset=UTF-8");
					response.setHeader("Cache-Control", "no-cache");
					PrintWriter out = response.getWriter();
					ObjectMapper mapper =new ObjectMapper();
					System.out.println("未授权异步请求："+menu);
					Message message = new Message("UT-403", "菜单未授权,多次发现重复操作，管理员将会冻结IP!");
					mapper.writeValue(out, message);
					out.flush();
					out.close();
					return false;
				}
				
				
			}
			
			/*if(menus!=null && menus.get(menu)==null && !url.equals("/login")  && ! ){
				//未授权，弹回
				if(requestType==null){
					//throw new Exception("未知用户被禁止操作！");
					
				}else{
					
				}
			}*/
		}
		return true;
	}

}
