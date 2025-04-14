package org.sonicframework.core.fillup;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
//import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.sonicframework.context.exception.DevelopeCodeException;
import org.sonicframework.context.fillup.annotation.DictFillup;
import org.sonicframework.core.dict.DictService;
import org.sonicframework.utils.fillup.FillupMapperContext;
import org.sonicframework.utils.fillup.FillupUtil;
//import org.sonicframework.context.sensitization.SensitizationResultWrapperSupport;

/**
 * @author lujunyi
 */
@Aspect
@Component("sonicFillupAspect")
@Order(Integer.MAX_VALUE - 2)
public class FillupAspect {

    private static final Logger log = LoggerFactory.getLogger(FillupAspect.class);
    
    @Autowired
    private FillupConfig webLogConfig;
    @Autowired(required = false)
    private DictService dictService;
    @Autowired(required = false)
    private List<FillupExpressionContextProvider> expressionContextProviders;
    
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final Map<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>();
    
    @Pointcut("@annotation(org.sonicframework.context.fillup.annotation.DictFillup)")
    /** 所有controller切入点*/ 
    public void dictFillup() {
    	
    }

    @Around("dictFillup()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        
    	Object ob = pjp.proceed();
    	if(ob == null) {
    		return ob;
    	}
    	DictFillup dictFillup = findDictFillup(pjp);
    	if(dictFillup == null) {
    		return ob;          
    	}else {
    		log.trace("fillup start");
    		doFillup(ob, dictFillup);
    		log.trace("fillup end");
    	}
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
    
    private void doFillup(Object ob, DictFillup dictFillup) {
    	if(ob == null) {
    		log.trace("fillup source is null");
    		return ;
    	}
    	if(dictService == null) {
			throw new DevelopeCodeException("使用DictFillup注解时未发现实现org.sonicframework.core.dict.DictService接口的bean");
		}
    	if(StringUtils.isNotBlank(dictFillup.expression())) {
    		log.trace("fillup expression:[{}]", dictFillup.expression());
    		Expression expression = getExpression(dictFillup.expression());
    		EvaluationContext context = new StandardEvaluationContext();
    		if(expressionContextProviders != null) {
    			expressionContextProviders.forEach(t->t.setExpressionContext(context));
    		}
    		context.setVariable("data", ob);
    		ob = expression.getValue(context);
    	}
    	if(ob == null) {
    		log.trace("fillup target is null");
    		return;
    	}
    	log.trace("fillup target type is [{}]", ob.getClass());
    	if(ob instanceof Map) {
    		Map<?, ?> map = (Map<?, ?>) ob;
    		Class<?> clazz = null;
    		for (Entry<?, ?> entry : map.entrySet()) {
    			if(entry.getValue() != null) {
    				clazz = entry.getValue().getClass();
    				FillupMapperContext<?> fillupMapperContext = FillupMapperContext.newInstance(clazz, type->dictService.getByType(type), dictFillup.groups());
    				FillupUtil.fillup(entry.getValue(), fillupMapperContext, dictFillup.groups());
    			}
			}
    	}else if(ob instanceof Collection) {
    		Collection<?> collection = (Collection<?>) ob;
    		FillupMapperContext<?> fillupMapperContext = null;
    		for (Object object : collection) {
				if(object != null) {
					if(fillupMapperContext == null) {
						fillupMapperContext = FillupMapperContext.newInstance(object.getClass(), type->dictService.getByType(type), dictFillup.groups());
					}
					FillupUtil.fillup(object, fillupMapperContext, dictFillup.groups());
					
				}
			}
    	}else {
    		Class<?> clazz = ob.getClass();
    		FillupMapperContext<?> fillupMapperContext = FillupMapperContext.newInstance(clazz, type->dictService.getByType(type), dictFillup.groups());
			FillupUtil.fillup(ob, fillupMapperContext, dictFillup.groups());
    	}
    }
    
    private static Expression getExpression(String expression) {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = expression.trim();
        return EXPRESSION_CACHE.computeIfAbsent(expression, EXPRESSION_PARSER::parseExpression);
    }
    
}