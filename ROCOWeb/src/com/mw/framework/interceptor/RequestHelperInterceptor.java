package com.mw.framework.interceptor;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rubyeye.xmemcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.http.WebStatFilter.StatHttpServletResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysAuthorizedServersDao;
import com.mw.framework.domain.SysAuthorizedServers;
import com.mw.framework.domain.SysUser;
import com.mw.framework.model.OnLineUserModel;
import com.mw.framework.util.PropertiesUtils;
import com.mw.framework.utils.DateTools;

/**
 * 
 * @Project SpringMVC
 * @Copyright © 2008-2013 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.cf.core.interceptor.RequestHelperInterceptor
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2013-6-2
 *
 */
public class RequestHelperInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RequestHelperInterceptor.class);
 
	@Autowired
	SysAuthorizedServersDao authorizedServersDao;
	
	/** 
     * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行， 
     * 这个方法的主要作用是用于清理资源的，当然这个方法也只能在当前这个Interceptor的preHandle方法的返回值为true时才会执行。 
     */ 
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
			Exception arg3) throws Exception {
		//System.out.println("RequestHelperInterceptor After completion handle");  
	}
 
	 /** 
     * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之 
     * 后，也就是在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操 
     * 作。这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用，这跟Struts2里面的拦截器的执行过程有点像， 
     * 只是Struts2里面的intercept方法中要手动的调用ActionInvocation的invoke方法，Struts2中调用ActionInvocation的invoke方法就是调用下一个Interceptor 
     * 或者是调用action，然后要在Interceptor之前调用的内容都写在调用invoke之前，要在Interceptor之后调用的内容都写在调用invoke方法之后。 
     */ 
	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
			ModelAndView arg3) throws Exception {
		 //System.out.println("RequestHelperInterceptor Post-handle");  
	}
	
	/** 
     * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，SpringMVC中的Interceptor拦截器是链式的，可以同时存在 
     * 多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在 
     * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返 
     * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。 
     */  
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2)
			throws Exception {
		//System.out.println("RequestHelperInterceptor Pre-handle");  
		SysUser user1 = (SysUser)request.getSession().getAttribute(Constants.CURR_USER);
		//MemcachedClient memcachedClient = SpringContextHolder.getBean("memcachedClient");
		//LinkedHashMap<String, OnLineUserModel> userMap = memcachedClient.get(Constants.LOGIN_USER_LIST);
		String url = request.getRequestURI();
		
		
		if(user1!=null /*&& userMap.get(request.getSession().getId())!=null */){
			/*OnLineUserModel userModel = userMap.get(request.getSession().getId());
			userModel.setOptTime(DateTools.getDateAndTime(DateTools.fullFormat));
			userMap.put(request.getSession().getId(), userModel);
			memcachedClient.set(Constants.LOGIN_USER_LIST,0, userMap);*/
			return true;
		}
		
		if(url.startsWith("/core/report/t/") || url.startsWith("/core/report/d/")){
			return true;
		}
		
		/*如果是访问的登录页面，可以直接通过。*/
		String requestType = request.getHeader("X-Requested-With");
		String referer = request.getHeader("Referer");
		
		
		//System.out.println("【"+referer+"】【"+url+"】");
		
		Pattern p2 = Pattern.compile(".(jpg|css|js|gif|png|ico|bin|htm|woff|woff2|ttf|eot|swf)");
		Matcher matcher2 = p2.matcher(url.toLowerCase());
		if(matcher2.find()){
			return true;
		}
		
		//客户端语言
		//System.out.println("ddd=====accept======"+request.getHeader("accept"));
		//System.out.println("ddd=====accept-language======"+request.getHeader("accept-language"));
//		System.out.println();
//		System.out.println(request.getRemoteHost());
//		System.out.println(request.getRemoteAddr());
//		System.out.println(request.getRemotePort());
//		System.out.println(request.getRemoteUser());
//		System.out.println(request.getHeader("x-forwarded-for"));
//		System.out.println();
		
		String property = PropertiesUtils.getProperty("debug.authorized.servers");
		
		if((property!=null && property.trim().length()>0 && property.equals("true"))||(property==null)){
			List<SysAuthorizedServers> findAll = authorizedServersDao.findAll();
			String[] exceptions = new String[]{"/extjs/4.2.1/resources/css/ext-all.css","/extjs/4.2.1/resources/ext-theme-classic/ext-theme-classic-all.css"};
			
			if(findAll!=null){
				for (SysAuthorizedServers sysAuthorizedServers : findAll) {
					if(referer!=null){
						if(referer.equals(sysAuthorizedServers.getServer())){
							//System.out.println("【默认通过】【"+url+"】");
							return true;
						}
						
						for (String string : exceptions) {
							if(referer.startsWith(sysAuthorizedServers.getServer()) && referer.endsWith(string)){
								//System.out.println("【默认通过2】【"+url+"】");
								return true;
							}
						}
					}else if(referer==null && url.endsWith("/favicon.ico")){
						//System.out.println("【网站图标通过】【"+url+"】");
						return true;
					}
				}
			}else{
				System.out.println("【授权失败】"+referer);
				return false;
			}
		}/*else if(property!=null && property.trim().length()>0 && property.equals("false")){
			return true;
		}*/
		
		if(url.endsWith("/login")||url.endsWith("/getUserdata")||url.endsWith("/getActUserdata")|| url.endsWith("/relogin")||url.indexOf("/loginPort")!=-1){
			return true;
		}
		/*判断用户是否登录*/
		HttpSession session = request.getSession();
		
		//SysUser user = (SysUser)session.getAttribute(Constants.CURR_USER);
		String user = (String) session.getAttribute(Constants.CURR_USER_ID);
		
		if(user==null || String.valueOf(user).length()==0/* || userMap.get(request.getSession().getId())==null*/){
			if(requestType==null){
				//throw new Exception("未知用户被禁止操作！");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html;charset=utf-8");
				response.setHeader("Cache-Control", "no-cache");
				//response.sendRedirect("/");
				request.getSession().setAttribute("LAST_URL", url);
				System.out.println("【用户验证失败】"+url);
				PrintWriter out = response.getWriter();
				//out.write("【操作代码：UA-500】未授权操作，已被系统终止!");
				//out.write("<script>alert('系统登录超时，自动返回登录界面!');window.top.location='/';</script>");
				out.write("<script>var a_ = parent.top.Ext?parent.top.Ext.getCmp('relogin'):null; if(a_){window.location='/?relogin=true';}else{window.top.location='/'}</script>");
				out.flush();
				out.close();
			}else{
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json;charset=UTF-8");
				response.setHeader("Cache-Control", "no-cache");
				PrintWriter out = response.getWriter();
				ObjectMapper mapper =new ObjectMapper();
				Message message = new Message("UT-403", "未授权操作，读访问被禁止，已被系统拦截!");
				mapper.writeValue(out, message);
				
				out.flush();
				out.close();
			}
			return false;
		}else{
			
			Pattern p = Pattern.compile(".(js|jpg|css|gif|png|ico|bin|htm|woff|woff2|ttf|eot|swf)");
			Matcher matcher = p.matcher(url.toLowerCase());
			if(!matcher.find()){
				//System.out.println("【"+/*user.getLoginNo()*/user+"】"+url);
				logger.info("【"+/*user.getLoginNo()*/user+"】【"+url+"】");
				
			}
		}
		 
		/*禁止用户直接通过地址访问静态文件：如果启用cookie后，还是无法判断，浏览器会先寻找本地缓存
		//String userAgent = request.getHeader("User-Agent");
		
		//System.out.println("【客户端】【"+url+"】："+userAgent);
		boolean bool2 =true;
		if(bool2){
			return true;
		}
		String requestTypeDesc = (requestType==null?"标准请求":"异步请求");
		
		Pattern p = Pattern.compile(".(js|jpg|css|gif|png|ico)");
		Matcher matcher = p.matcher(url.toLowerCase());
		if(!matcher.find()){
			//logger.info("【"+requestType+"】访问路径："+url);
			System.out.println("【"+requestTypeDesc+"】："+url);
		}
		
		if(referer==null || referer.trim().length()==0 ){
			
			Matcher m = p.matcher(url);
			boolean rs = m.find();
			if(rs){
				//System.out.println("非法访问:"+request.getRequestURI());
				System.out.println("【非法请求】："+url);
				//logger.debug("非法访问:"+request.getRequestURI());
				// 设置编码
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html;charset=utf-8");
				response.setHeader("Cache-Control", "no-cache");
				
				PrintWriter out = response.getWriter();
				out.write("Unauthorized access!");
				out.flush();
				out.close();
				return false;
			}else{
				logger.debug("正常访问:"+request.getRequestURI());
			}
		}*/
		return true;
	}

}
