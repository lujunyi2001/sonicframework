package org.sonicframework.core.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author lujunyi
 */
//@Aspect
//@Component
//@Order(Integer.MAX_VALUE - 1)
public class ServiceLogAspect {


    @Autowired
    private WebLogConfig webLogConfig;
    
	private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

	@Pointcut("execution(public * com..*.*(..)) || execution(public * org..*.*(..)) || execution(public * test..*.*(..))")
	public void logPointCut() {

	}

	private final static String LOG_BEGIN_FORMAT = "execute service BEGIN requestId:{} call:{}.{} start";
	private final static String LOG_RESULT_FORMAT = "execute service END requestId:{} call:{}.{} end, cost:{}";

	@Around("logPointCut()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

		ServiceLogHelper logHelper = null;
		if(webLogConfig.isEnableServiceLog()) {
			logHelper = ServiceLogContextHolder.get();
		}
		String stepId = null;
		if (logHelper != null) {
			stepId = logHelper.getRequestId() + ":" + pjp.getSignature().getDeclaringTypeName() + ":" + pjp.getSignature().getName();
		}
		if (stepId != null) {
			logHelper.startStep(stepId);
			logger.info(LOG_BEGIN_FORMAT, 
					logHelper.getRequestId(), 
					pjp.getSignature().getDeclaringTypeName(),
					pjp.getSignature().getName());
		}

		try {
			return pjp.proceed();
		} finally {
			if (stepId != null) {
				long cost = -1;
				try {
					cost = logHelper.stopStep(stepId);
				} catch (Exception e) {
					logger.error("call logHelper.stopStep error", e);
				}
				logger.info(LOG_RESULT_FORMAT, 
						logHelper.getRequestId(), 
						pjp.getSignature().getDeclaringTypeName(),
						pjp.getSignature().getName(), cost);
			}
		}
	}
}