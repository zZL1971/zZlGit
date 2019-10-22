/**
 *
 */
package com.mw.framework.aspect;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.main.domain.mm.MaterialFile;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.impl.BaseEntity;
import com.mw.framework.model.JdbcExtGridBean;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.aspect.AspectController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2016-4-8
 * 
 */
@Aspect
@Component
public class AspectController {
	private static final Logger logger = LoggerFactory.getLogger("monitor");

	@Pointcut("execution(* com.main.controller..*.*(..))")
	private void anyMethod() {
	}

	@Around("anyMethod()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Object ia = request.getSession().getAttribute(Constants.CURR_USER_IA);
		Object currUser = request.getSession().getAttribute(Constants.CURR_USER_ID);
		String na = "";
		if (request.getHeader("x-forwarded-for") == null) {  
			na = request.getRemoteAddr();
	    }else{
	        na = request.getHeader("x-forwarded-for");
	    }
		
		//System.out.println("-=-=-=-=-=-=Method["+pjp.getTarget().getClass()+"."+pjp.getSignature().getName()+"]["+request.getRequestURI()+"]["+currUser+"]["+na+"]["+ia+"]=-=-=-==-=-=-=-=--=-=-");
		
		Signature signature = pjp.getSignature(); 
		Class<?> returnType = ((MethodSignature) signature).getReturnType();
		 
		
		long begin = System.currentTimeMillis();
		
		/*if(request!=null){
			//获取传入参数
			if(request.getParameterMap().size()>0)
				System.out.println("|输入参数--------"+JSONObject.fromObject(request.getParameterMap()));
		}
		
		Object[] args = pjp.getArgs();
		for (int i = 0; i < args.length; i++) {
			System.out.println("|方法入参--------"+args[i]);
		}
		*/
		Object o = pjp.proceed();
		long end = System.currentTimeMillis();
		/*System.out.println("|返回类型--------"+returnType);
		if(o instanceof JdbcExtGridBean){
			JdbcExtGridBean gridBean = (JdbcExtGridBean) o;
			if(gridBean.getSize()<50){
				System.out.println("|输出参数--------"+JSONArray.fromObject(gridBean));
			}else{
				System.out.println("|输出参数--------"+gridBean.getSize());
			}
		}else if(o instanceof List){
			List list = (List) o;
			if(list.size()<50){
				System.out.println("|输出参数--------"+JSONArray.fromObject(o));
			}else{
				System.out.println("|输出参数--------"+list.size());
			}
		}else if(o instanceof String){
			System.out.println("|输出参数--------"+o);
		}else{
			//System.out.println("|输出参数--------"+JSONObject.fromObject(o));
		}*/
		
		//System.out.println("-=-=-=-=Method["+pjp.getTarget().getClass()+"."+pjp.getSignature().getName()+"]["+request.getRequestURI()+"]["+currUser+"]["+na+"]["+ia+"]["+(end - begin)+"ms]------------------------");
		return o;
	}
}
