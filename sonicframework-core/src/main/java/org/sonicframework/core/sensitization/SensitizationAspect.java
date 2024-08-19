package org.sonicframework.core.sensitization;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.sonicframework.context.sensitization.annotation.Sensitization;
import org.sonicframework.utils.http.ServletUtil;
import org.sonicframework.utils.sensitization.SensitiveRequestContext;

/**
 * @author lujunyi
 */
@Aspect
@Component("sonicSensitizationAspect")
@Order(Integer.MAX_VALUE - 1)
public class SensitizationAspect {

    private static final Logger logger = LoggerFactory.getLogger(SensitizationAspect.class);
    
    @Autowired
    private SensitizationConfig webLogConfig;
    
    @Pointcut("@annotation(org.sonicframework.context.sensitization.annotation.Sensitization)")
    /** 所有controller切入点*/ 
    public void logPointCut() {
    	
    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        
    	Object ob = pjp.proceed();
    	if(ob == null) {
    		return ob;
    	}
    	Sensitization sensitization = findSensitization(pjp);
    	if(sensitization == null) {
    		return ob;
    	}
    	logger.info("sensitization result");
    	SensitiveRequestContext context = new SensitiveRequestContext(sensitization.groups());
    	HttpServletRequest request = ServletUtil.getRequest();
    	request.setAttribute(SensitiveRequestContext.SENSITIVE_REQUEST_KEY, context);
    	
    	return ob;
    }
    
    private Sensitization findSensitization(ProceedingJoinPoint pjp) {
    	if(!webLogConfig.isEnable()) {
    		return null;
    	}
    	Sensitization sensitization = null;
        if(pjp.getSignature() instanceof MethodSignature) {
        	Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        	sensitization = method.getAnnotation(Sensitization.class);
        }
        return sensitization;
    }
    
}