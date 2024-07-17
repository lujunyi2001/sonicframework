package org.sonicframework.core.encrypt;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonicframework.context.common.annotation.EncryptResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author lujunyi
 */
@Component("sonicEncryptResponseHandlerInterceptor")
public class EncryptResponseHandlerInterceptor<T> implements HandlerInterceptor {

	@Autowired
	private EncryptConfig config;
	
	private ConcurrentHashMap<Method, EncryptResponseBody> apiCacheMap = new ConcurrentHashMap<>(256);
	public final static String sonic_FRAMEWORK_RESPONSE_ENCRYPT_SUPPORT = "sonic_FRAMEWORK_RESPONSE_ENCRYPT_SUPPORT";
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler != null && handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Method method = handlerMethod.getMethod();
			boolean supportEncrypt = config.isEnable() && (config.isAlwaysEcryptResponsebody() || putResponseResultSupport(request, handlerMethod, method));
			if(supportEncrypt) {
				request.setAttribute(sonic_FRAMEWORK_RESPONSE_ENCRYPT_SUPPORT, true);
			}else {
				request.removeAttribute(sonic_FRAMEWORK_RESPONSE_ENCRYPT_SUPPORT);
			}
		}
		return true;
	}
	
	private boolean putResponseResultSupport(HttpServletRequest request, HandlerMethod handlerMethod, Method method) {
		EncryptResponseBody encryptResponseBody = null;
		boolean hasAnnotation = false;
		if(apiCacheMap.containsKey(method)) {
			hasAnnotation = true;
			encryptResponseBody = apiCacheMap.get(method);
		}else {
			Class<?> clazz = handlerMethod.getBeanType();
			
			if(method.isAnnotationPresent(EncryptResponseBody.class)) {
				encryptResponseBody = method.getAnnotation(EncryptResponseBody.class);
			}else if(clazz.isAnnotationPresent(EncryptResponseBody.class)) {
				encryptResponseBody = clazz.getAnnotation(EncryptResponseBody.class);
			}
			if(encryptResponseBody == null) {
				encryptResponseBody = AnnotatedElementUtils.findMergedAnnotation(clazz, EncryptResponseBody.class);
			}
			hasAnnotation = encryptResponseBody != null;
			if(hasAnnotation) {
				apiCacheMap.put(method, encryptResponseBody);
			}else {
				encryptResponseBody = null;
			}
		}
		return hasAnnotation;
	}
	

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
