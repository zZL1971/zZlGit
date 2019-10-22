package com.mw.framework.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.main.domain.sys.MethodExeductTime;

/**
 * 
 * @author Chaly
 *
 */
@Aspect
@Component
public class AspectMethodExeductTime {

	long time;
	MethodExeductTime exeductTime;
	@Pointcut(value="execution(* com..*.controller..*.*(..))")
	public void methodTime() {}
	
	@Before(value="methodTime() && @annotation(org.springframework.web.bind.annotation.RequestMapping) && !@annotation(com.mw.framework.json.filter.annotation.IgnoreProperties)")
	public void startTime(JoinPoint joinPoint) {
		//记录开始时间
		time = System.currentTimeMillis();
		exeductTime=new MethodExeductTime();
		Signature signature = joinPoint.getSignature();
		exeductTime.setClsName(signature.getDeclaringTypeName());
		exeductTime.setMethodName(signature.getName());
	}
	@After(value="methodTime() && @annotation(org.springframework.web.bind.annotation.RequestMapping) && !@annotation(com.mw.framework.json.filter.annotation.IgnoreProperties)")
	public void endTime(JoinPoint joinPoint) {
		long start = time;
		time = System.currentTimeMillis();
		
	}
}
