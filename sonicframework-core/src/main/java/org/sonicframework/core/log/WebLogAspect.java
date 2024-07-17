package org.sonicframework.core.log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.sonicframework.context.exception.BaseBizException;
import org.sonicframework.context.log.SystemLogContextHolder;
import org.sonicframework.context.log.annotation.SkipWebLog;
import org.sonicframework.context.log.annotation.SystemLog;
import org.sonicframework.context.log.dto.LogUserDto;
import org.sonicframework.context.log.dto.SystemLogDto;
import org.sonicframework.context.log.service.LogUserService;
import org.sonicframework.context.log.service.SystemLogService;
import org.sonicframework.utils.http.ServletUtil;

/**
 * @author lujunyi
 */
@Aspect
@Component
@Order(Integer.MAX_VALUE)
public class WebLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    private static ExecutorService pool = Executors.newCachedThreadPool();
    
    @Autowired
    private WebLogConfig webLogConfig;
    @Autowired(required = false)
    private LogUserService logUserService;
    @Autowired(required = false)
    private List<SystemLogService> logServices;
    
//    @Pointcut("execution(public * com..*.controller.*.*(..)) || execution(public * org..*.controller.*.*(..)) || execution(public * test.controller.*.*(..))")
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || "
    		+ "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
    		+ "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
    		+ "@annotation(org.springframework.web.bind.annotation.GetMapping) || "
    		+ "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || "
    		+ "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    /** 所有controller切入点*/ 
    public void logPointCut() {
    	
    }

    private final static String LOG_BEGIN_FORMAT = "BEGIN requestId:{} url:{},  httpmethod:{},  clientIp:{}, user:{}, params:{}, requestbody:{}";
    private final static String LOG_RESULT_FORMAT = "END requestId:{} call:{}.{}({}) return:{}, cost:{}";
    private final static String LOG_ERROR_FORMAT = "END requestId:{} call:{}.{}({}) exeption:{}, cost:{}";
    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
    	ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
//		String url = request.getRequestURL().toString();
		boolean isLog = true;
		boolean printResult = false;
		String requestId = UUID.randomUUID().toString();

		long startTime = System.currentTimeMillis();
		
		SkipWebLog skipWebLog = findSkipWebLog(pjp);
		if(skipWebLog != null && ArrayUtils.isEmpty(skipWebLog.value())) {
			isLog = false;
		}
        
        LogUserDto user = getCurrentUser();
        
        if(webLogConfig.isEnableAroundLog() && isLog && logger.isInfoEnabled()) {
        	List<String> skipRequestParam = webLogConfig.getSkipRequestParam();
        	Set<String> skipParamSet = skipRequestParam == null?new HashSet<>():new HashSet<>(skipRequestParam);
        	logger.info(LOG_BEGIN_FORMAT, 
        			requestId, 
        			request.getRequestURL(),
        			request.getMethod(), 
        			ServletUtil.getClientIp(request), 
        			user, 
        			ServletUtil.getParameterMap(request, skipParamSet), 
        			SystemLogHelper.getRequestBodyContent(request));
        }
        
        SystemLog systemLog = findSystemLog(pjp);
        SystemLogDto systemLogDto = null;
        if(systemLog != null) {
        	systemLogDto = new SystemLogDto();
        	SystemLogHelper.fillInfo(systemLogDto, systemLog, user, pjp.getArgs(), request);
        }
        if(systemLogDto != null) {
        	SystemLogContextHolder.set(systemLogDto);
        }
        if(webLogConfig.isEnableServiceLog()) {
        	ServiceLogHelper serviceLogHelper = new ServiceLogHelper(requestId);
        	ServiceLogContextHolder.set(serviceLogHelper);
        }
        Object ob = null;
		try {
			ob = pjp.proceed();
			if(webLogConfig.isEnableAroundLog() && logger.isInfoEnabled()) {
				Object resultOb = ob;
				if(!printResult) {
					if(ob == null) {
						resultOb = null;
					}else if(ob instanceof Collection<?>) {
						resultOb = "[result collection size:" + ((Collection<?>)ob).size() + "]";
					}
				}
				SystemLogHelper.fillAndValidResult(systemLogDto, resultOb, null, startTime);
				if(user == null) {
					user = getCurrentUser();
				}
				SystemLogHelper.fillLogUserInfo(systemLogDto, user);
				if(isLog) {
					logger.info(LOG_RESULT_FORMAT, 
							requestId, 
		        			pjp.getSignature().getDeclaringTypeName(),
		        			pjp.getSignature().getName(), 
		        			Arrays.toString(getParamValues(pjp, skipWebLog)), 
		        			resultOb, 
		        			(System.currentTimeMillis() - startTime));
				}
	        	
	        }
			
		} catch (Throwable e) {
			systemLogDto = SystemLogHelper.fillAndValidResult(systemLogDto, null, e, startTime);
			SystemLogHelper.fillLogUserInfo(systemLogDto, user);
			boolean ignoreException = ignoreException(e);
			if(ignoreException) {
				systemLogDto = null;
			}
			if(webLogConfig.isEnableAroundLog() && logger.isErrorEnabled()) {
	        	logger.error(LOG_ERROR_FORMAT, 
	        			requestId, 
	        			pjp.getSignature().getDeclaringTypeName(),
	        			pjp.getSignature().getName(), 
	        			Arrays.toString(getParamValues(pjp, skipWebLog)), 
	        			e, 
	        			(System.currentTimeMillis() - startTime));
	        	if(!(e instanceof BaseBizException) && !ignoreException) {
		        	logger.error(e.toString(), e);
	        	}
	        }
			throw e;
		}finally {
			SystemLogContextHolder.remove();
			ServiceLogContextHolder.remove();
			saveSystemLog(systemLogDto);
		}
        return ob;
    }
    
    private boolean ignoreException(Throwable e) {
    	if(e instanceof BaseBizException) {
    		return true;
    	}
    	if(webLogConfig.getExcludeException() != null) {
    		boolean anyMatch = webLogConfig.getExcludeException().stream().anyMatch(t->t.isAssignableFrom(e.getClass()));
    		if(anyMatch) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private SkipWebLog findSkipWebLog(ProceedingJoinPoint pjp) {
    	if(pjp.getSignature() instanceof MethodSignature) {
        	Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        	SkipWebLog skipWebLog = method.getAnnotation(SkipWebLog.class);
        	return skipWebLog;
    	}
    	return null;
    }
    
    private Object[] getParamValues(ProceedingJoinPoint pjp, SkipWebLog skipWebLog) {
    	if(skipWebLog != null && pjp.getSignature() instanceof MethodSignature) {
        	if(skipWebLog != null && ArrayUtils.isNotEmpty(skipWebLog.value())) {
        		int[] value = skipWebLog.value();
        		Object[] result = new Object[pjp.getArgs().length];
        		for (int i = 0; i < result.length; i++) {
					if(ArrayUtils.contains(value, i)) {
						result[i] = "******";
					}else {
						result[i] = pjp.getArgs()[i];
					}
				}
        		return result;
        	}
    	}
    	return pjp.getArgs();
    }
    
    private void saveSystemLog(SystemLogDto systemLogDto) {
    	if(systemLogDto == null) {
    		return;
    	}
    	if(CollectionUtils.isEmpty(logServices)) {
    		return;
    	}
    	pool.execute(new Thread("log") {
    		@Override
    		public void run() {
    			for (SystemLogService service : logServices) {
    				try {
    					service.saveLog(systemLogDto);
					} catch (Exception e) {
						logger.error("save system log error:[" + systemLogDto + "]", e);
					}
				}
    		}
    	});
    }
    
    private LogUserDto getCurrentUser() {
    	if(logUserService == null) {
    		return null;
    	}
    	try {
    		return logUserService.getCurrentUser();
		} catch (Exception e) {
			logger.error("execute method logUserService.getCurrentUser() error", e);
			return null;
		}
    }
    
    private SystemLog findSystemLog(ProceedingJoinPoint pjp) {
    	if(!webLogConfig.isEnableSystemLog()) {
    		return null;
    	}
    	if(CollectionUtils.isEmpty(logServices)) {
    		return null;
    	}
    	SystemLog systemLog = null;
        if(pjp.getSignature() instanceof MethodSignature) {
        	Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        	systemLog = method.getAnnotation(SystemLog.class);
        }
        return systemLog;
    }
    
}