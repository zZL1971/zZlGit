package com.mw.framework.interceptor;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.dao.SysAuthorizedServersDao;
import com.mw.framework.domain.SysAuthorizedServers;
import com.mw.framework.domain.SysUser;

/**
 * 
 * @Project SpringMVC
 * @Copyright © 2008-2013 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.cf.core.interceptor.UserInfoInterceptor
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2013-6-2
 *
 */
public class UserInfoInterceptor implements HandlerInterceptor {

	@Autowired
	SysAuthorizedServersDao authorizedServersDao;
 
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
			Exception arg3) throws Exception {
		//System.out.println("UserInfoInterceptor After completion handle");  
	}
 
	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
			ModelAndView arg3) throws Exception {
		 //System.out.println("UserInfoInterceptor Post-handle");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//System.out.println("UserInfoInterceptor Pre-handle");
		boolean bool2 =true;
		if(bool2){
			return true;
		}
		
		HttpSession session = request.getSession();
		String requestType = request.getHeader("X-Requested-With");
		String referer = request.getHeader("Referer");
		//String userAgent = request.getHeader("User-Agent");
		String url = request.getRequestURI();
		SysUser user = (SysUser)session.getAttribute(Constants.CURR_USER);
		String server = null;
		if(referer!=null){
			List<SysAuthorizedServers> findAll = authorizedServersDao.findAll();
			boolean bool = false;
			for (SysAuthorizedServers sysAuthorizedServers : findAll) {
				bool = referer.startsWith(sysAuthorizedServers.getServer());
				if(bool){
					server = sysAuthorizedServers.getServer();
					//System.out.println("服务器【"+sysAuthorizedServers.getServer()+"】授权验证通过!");
					break;
				}
			}
			
			if(bool){
				if((referer.equals(server))||url.startsWith("/extjs/4.2.1/")||
					url.startsWith("/jquery/")||
					url.startsWith("/resources/")||
					url.equals("/A0/app/app.js")||
					url.equals("/A0/app/view/Login.js")){
					return true;
				}
			}else{
				System.out.println("未授权服务器【"+referer+"】");
			}
		}/*else{
			if(userAgent.equals("BC")){
				System.out.println("【supcan】请求");
			}else{
				System.out.println("【页面跳转请求】"+url);
			}
		}*/
		
		if(user==null){
			//throw new Exception("未知用户被禁止操作！");
			if(requestType==null){
				//throw new Exception("未知用户被禁止操作！");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html;charset=utf-8");
				response.setHeader("Cache-Control", "no-cache");
				response.sendRedirect("/");
				request.getSession().setAttribute("LAST_URL", url);
				System.out.println("【用户验证失败】"+url);
				/*PrintWriter out = response.getWriter();
				out.write("【操作代码：UA-500】未授权操作，已被系统终止!");
				out.flush();
				out.close();*/
				return false;
			}else{
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json;charset=UTF-8");
				response.setHeader("Cache-Control", "no-cache");
				PrintWriter out = response.getWriter();
				
				//System.out.println(1/0);
				//response.sendError(403, "未授权操作，已被系统终止!");
				
				ObjectMapper mapper =new ObjectMapper();
				Message message = new Message("UT-403", "未登录或登录超时，请重新登录系统!");
				//message.put("error", "未授权操作，已被系统终止!");
				//message.put("message", "未授权操作，已被系统终止!");
				mapper.writeValue(out, message);
				
				
				out.flush();
				out.close();
				
				return false;
				//throw new Exception("未知用户被禁止操作！");
			}
			//return false;
		}
		return true;
	}

}
