package org.sonicframework.core.fillup;

import java.lang.reflect.Method;
//import java.util.List;

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

import org.sonicframework.context.fillup.annotation.DictFillup;
//import org.sonicframework.context.sensitization.SensitizationResultWrapperSupport;

/**
 * @author lujunyi
 */
@Aspect
@Component
@Order(Integer.MAX_VALUE - 2)
public class FillupAspect {

    private static final Logger logger = LoggerFactory.getLogger(FillupAspect.class);
    
    @Autowired
    private FillupConfig webLogConfig;
//    @Autowired(required = false)
//    private List<SensitizationResultWrapperSupport<?>> wrapperSupportList;
    
    @Pointcut("@annotation(org.sonicframework.context.fillup.annotation.DictFillup)")
    /** 所有controller切入点*/ 
    public void logPointCut() {
    	
    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        
    	Object ob = pjp.proceed();
    	if(ob == null) {
    		return ob;
    	}
    	DictFillup dictFillup = findDictFillup(pjp);
    	if(dictFillup == null) {
    		return ob;
    	}
    	logger.info("sensitization result");
//    	SensitizationItemVo defaultSensitizationItem = SensitizationUtil.buildDefaultSensitizationItemVo(sensitization.defaultSupport().length == 0?null:sensitization.defaultSupport()[0], 
//    			sensitization.defaultEnv().length == 0?null:sensitization.defaultEnv()[0]);
//    	ob = SensitizationUtil.encrypt(ob, wrapperSupportList, defaultSensitizationItem, sensitization.map(), sensitization.groups());
    	
    	return ob;
    }
    
    private DictFillup findDictFillup(ProceedingJoinPoint pjp) {
    	if(!webLogConfig.isEnable()) {
    		return null;
    	}
    	DictFillup dictFillup = null;
        if(pjp.getSignature() instanceof MethodSignature) {
        	Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        	dictFillup = method.getAnnotation(DictFillup.class);
        }
        return dictFillup;
    }
    
}