package org.sonicframework.core.webapi;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
//import org.apache.shiro.authz.AuthorizationException;
//import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.core.webapi.annotation.ResponseResult;
import org.sonicframework.core.webapi.service.WebApiResultApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import org.sonicframework.context.exception.BaseBizException;
import org.sonicframework.context.webapi.dto.ResultDto;

/**
 * @author lujunyi
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice
public class ResponseBodyAdviceHandler<T> implements ResponseBodyAdvice<Object> {

	private static final Logger logger = LoggerFactory.getLogger(ResponseBodyAdviceHandler.class);
	@Value("${spring.servlet.multipart.max-file-size:50MB}")
	private String uploadMax;
	@Autowired
	private WebApiResultApplyService<T> webApiResultApplyService;
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		ResponseResult responseResult = (ResponseResult) attributes.getRequest().getAttribute(ResponseResultHandlerInterceptor.LANDTOOL_FRAMEWORK_RESPONSE_RESULT_SUPPORT);
		return responseResult != null;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		if(body!= null && webApiResultApplyService.applyClass().isAssignableFrom(body.getClass())) {
			return body;
		}
		return webApiResultApplyService.applyBySuccess(body);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public T handlerException(HttpServletRequest req, Exception e){
		T result = webApiResultApplyService.apply();
		if(supportValidationException(e, result)) {
			return result;
		}
		result = webApiResultApplyService.applyByUncaughException(e);
		logger.error("uncatch exception:" + e.toString(), e);
		return result;
	}
	
	private boolean supportValidationException(Exception e, T result) {
		if(e instanceof BaseBizException) {
			BaseBizException bbe = (BaseBizException) e;
			webApiResultApplyService.fillResult(result, bbe.getCode(), bbe.getMessage());
			return true;
		}else if(e instanceof BindException) {
			String errorMsg = processObjectErrorList(((BindException)e).getBindingResult().getAllErrors());
			webApiResultApplyService.fillResult(result, ResultDto.RESULT_ARGUMENT_ERROR, errorMsg);
			return true;
		}else if(e instanceof MethodArgumentNotValidException) {
			String errorMsg = processObjectErrorList(((MethodArgumentNotValidException)e).getBindingResult().getAllErrors());
			webApiResultApplyService.fillResult(result, ResultDto.RESULT_ARGUMENT_ERROR, errorMsg);
			return true;
		}else if(e instanceof ConstraintViolationException) {
			String errorMsg = ((ConstraintViolationException)e).getConstraintViolations()
	                .stream()
	                .filter(item -> StringUtils.isNotBlank(item.getMessage()))
	                .map(item -> item.getMessage())
	                .collect(Collectors.joining(","));
			webApiResultApplyService.fillResult(result, ResultDto.RESULT_ARGUMENT_ERROR, errorMsg);
			return true;
		}else if(e instanceof MaxUploadSizeExceededException) {
			webApiResultApplyService.fillResult(result, ResultDto.RESULT_ARGUMENT_ERROR, "上传文件大小不能大于" + uploadMax);
			return true;
//		}else if(e instanceof UnauthorizedException) {
//			webApiResultApplyService.fillResult(result, ResultDto.RESULT_FORBIDDEN, "没有权限");
//			return true;
//		}else if(e instanceof AuthorizationException) {
//			webApiResultApplyService.fillResult(result, ResultDto.RESULT_FORBIDDEN, "没有权限");
//			return true;
		}
		return false;
	}
	
	private String processObjectErrorList(List<ObjectError> list) {
		return list.stream().filter(error -> StringUtils.isNotBlank(error.getDefaultMessage()))
				.map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.joining(","));
	}
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(new DateConverter().convert(text));
            }
        });

    }

}
