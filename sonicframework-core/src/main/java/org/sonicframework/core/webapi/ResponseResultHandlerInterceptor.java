package org.sonicframework.core.webapi;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonicframework.core.webapi.annotation.ExceptionHandlerStatus;
import org.sonicframework.core.webapi.annotation.ResponseResult;
import org.sonicframework.core.webapi.service.WebApiResultApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author lujunyi
 */
@Component
public class ResponseResultHandlerInterceptor<T> implements HandlerInterceptor {

	@Autowired
	private WebApiConfig webApiConfig;
	@Autowired
	private WebApiResultApplyService<T> resultApplyService;
	
	private ConcurrentHashMap<Method, ResponseResult> apiCacheMap = new ConcurrentHashMap<>(256);
	private ConcurrentHashMap<Method, HttpStatus> exceptionHttpStatusCacheMap = new ConcurrentHashMap<>(256);
	public final static String sonic_FRAMEWORK_RESPONSE_RESULT_SUPPORT = "sonic_FRAMEWORK_RESPONSE_RESULT_SUPPORT";
	public final static String sonic_EXCEPTION_HTTPSTATUS_SUPPORT = "sonic_EXCEPTION_HTTPSTATUS_SUPPORT";
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler != null && handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Method method = handlerMethod.getMethod();
			boolean hasAnnotation = putResponseResultSupport(request, handlerMethod, method);
			if(hasAnnotation) {
				putExceptionHandlerHttpStatusSupport(request, handlerMethod, method);
			}
		}
		return true;
	}
	
	private boolean putResponseResultSupport(HttpServletRequest request, HandlerMethod handlerMethod, Method method) {
		ResponseResult responseResult = null;
		boolean hasAnnotation = false;
		if(apiCacheMap.containsKey(method)) {
			hasAnnotation = true;
			responseResult = apiCacheMap.get(method);
		}else {
			Class<?> clazz = handlerMethod.getBeanType();
			
			if(method.isAnnotationPresent(ResponseResult.class)) {
				responseResult = method.getAnnotation(ResponseResult.class);
			}else if(clazz.isAnnotationPresent(ResponseResult.class)) {
				responseResult = clazz.getAnnotation(ResponseResult.class);
			}
			if(responseResult == null) {
				responseResult = AnnotatedElementUtils.findMergedAnnotation(clazz, ResponseResult.class);
			}
			hasAnnotation = responseResult != null;
			boolean support = responseResult != null && supportMethod(method, responseResult);
			if(support) {
				apiCacheMap.put(method, responseResult);
			}else {
				responseResult = null;
			}
		}
		
		if(responseResult != null) {
			request.setAttribute(sonic_FRAMEWORK_RESPONSE_RESULT_SUPPORT, responseResult);
		}else {
			request.removeAttribute(sonic_FRAMEWORK_RESPONSE_RESULT_SUPPORT);
		}
		return hasAnnotation;
	}
	
	private void putExceptionHandlerHttpStatusSupport(HttpServletRequest request, HandlerMethod handlerMethod, Method method) {
		HttpStatus httpStatus = null;
		if(exceptionHttpStatusCacheMap.containsKey(method)) {
			httpStatus = exceptionHttpStatusCacheMap.get(method);
		}else {
			Class<?> clazz = handlerMethod.getBeanType();
			
			if(method.isAnnotationPresent(ExceptionHandlerStatus.class)) {
				httpStatus = method.getAnnotation(ExceptionHandlerStatus.class).value();
			}else if(clazz.isAnnotationPresent(ExceptionHandlerStatus.class)) {
				httpStatus = clazz.getAnnotation(ExceptionHandlerStatus.class).value();
			}
			
			if(httpStatus == null) {
				ExceptionHandlerStatus annotation = AnnotatedElementUtils.findMergedAnnotation(clazz, ExceptionHandlerStatus.class);
				if(annotation != null) {
					httpStatus = annotation.value();
				}
			}
			
			if(httpStatus != null) {
				exceptionHttpStatusCacheMap.put(method, httpStatus);
			}
		}
		
		if(httpStatus != null) {
			request.setAttribute(sonic_EXCEPTION_HTTPSTATUS_SUPPORT, httpStatus);
		}else {
			request.removeAttribute(sonic_EXCEPTION_HTTPSTATUS_SUPPORT);
		}
	}
	
	private boolean supportMethod(Method method, ResponseResult responseResult) {
		return responseResult != null && responseResult.switchType() != null && 
				responseResult.switchType().isStatusOn(webApiConfig.isReturnUnifiedResult()) && 
				!resultApplyService.applyClass().isAssignableFrom(method.getReturnType());
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
