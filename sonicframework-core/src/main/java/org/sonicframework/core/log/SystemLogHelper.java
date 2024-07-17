package org.sonicframework.core.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sonicframework.context.common.annotation.Match;
import org.sonicframework.context.exception.BaseBizException;
import org.sonicframework.context.log.annotation.ContentParam;
import org.sonicframework.context.log.annotation.SystemLog;
import org.sonicframework.context.log.dto.LogUserDto;
import org.sonicframework.context.log.dto.SystemLogDto;
import org.sonicframework.utils.ClassUtil;
import org.sonicframework.utils.StreamUtil;
import org.sonicframework.utils.beans.BeanWrapperImpl;
import org.sonicframework.utils.http.ServletUtil;

/**
* @author lujunyi
*/
public class SystemLogHelper {
	
	private final static String HIDDEN_VALUE = "******";

	public static void fillInfo(SystemLogDto dto, SystemLog log, LogUserDto currentUser, Object[] args, HttpServletRequest request) {
		if(dto == null) {
			return ;
		}
		fillLogUserInfo(dto, currentUser);
		fillSystemLogInfo(dto, log);
		fillRequestInfo(dto, log, request);
		fillContentInfo(dto, log, args, request);
	}
	
	public static SystemLogDto fillAndValidResult(SystemLogDto dto, Object result, Throwable e, long startTime) {
    	if(dto == null) {
    		return null;
    	}
    	boolean fail = false;
    	String resultMsg = null;
    	if(e != null && e instanceof BaseBizException) {
    		return null;
    	}
    	if(e != null) {
    		fail = true;
    		resultMsg = getErrorInfoFromException(e);
    	}else {
    		resultMsg = toJson(result);
    	}
    	dto.setCostTime(System.currentTimeMillis() - startTime);
    	dto.setFail(fail);
    	if(dto.getResult() == null || e != null) {
    		dto.setResult(resultMsg);
    	}
    	return dto;
    }
	
	private static String getErrorInfoFromException(Throwable e) {
    	StringWriter sw = null;
    	PrintWriter pw = null;
    	try {
    		sw = new StringWriter();
    		pw = new PrintWriter(sw);
    		e.printStackTrace(pw);
    		return "\r\n" + sw.toString() + "\r\n";
    	} catch (Exception e2) {
    		return e.toString();
    	}finally {
    		StreamUtil.close(sw);
    		StreamUtil.close(pw);
    	}
    }
	
	public static void fillSystemLogInfo(SystemLogDto dto, SystemLog log) {
		if(dto == null) {
			return ;
		}
		dto.setModuleName(log.module());
		dto.setOptType(log.optType());
		dto.setOperateTime(new Date());
	}
	
	public static void fillContentInfo(SystemLogDto dto, SystemLog log, Object[] args, HttpServletRequest request) {
		if(dto == null) {
			return ;
		}
		String content = log.content();
    	if(StringUtils.isNotBlank(content)) {
    		content = parseContent(log, args, request);
    	}
    	if(content != null && content.length() > 300) {
    		content = content.substring(0, 300) + "...";
    	}
    	dto.setContent(content);
	}
	
	private static String parseContent(SystemLog log, Object[] args, HttpServletRequest request) {
    	String content = log.content();
    	ContentParam[] param = log.param();
    	if(param.length == 0 || args == null) {
    		return content;
    	}
    	String[] p = new String[param.length];
    	ContentParam paramDefinetion = null;
    	for (int i = 0; i < param.length; i++) {
    		paramDefinetion = param[i];
    		if(args.length > paramDefinetion.index() || paramDefinetion.isRequest()) {
    			p[i] = parseParam(paramDefinetion, args[paramDefinetion.index()], request);
    		}else {
    			p[i] = null;
    		}
		}
    	for (int i = 0; i < p.length; i++) {
    		content = content.replace("{" + i + "}", p[i] == null?"":p[i]);
		}
    	return content;
    }
	
	private static String parseParam(ContentParam definetion, Object arg, HttpServletRequest request) {
    	String param = null;
    	if(definetion.isRequest()) {
    		if(StringUtils.isEmpty(definetion.field())) {
    			param = toJson(ServletUtil.getParameterMap(request));
    		}else {
    			param = request.getParameter(definetion.field());
    		}
    	}else if(arg != null && StringUtils.isEmpty(definetion.field())){
    		param = String.valueOf(arg);
    	}else if(arg != null){
    		if(ClassUtil.isNormalClass(arg.getClass())) {
    			param = String.valueOf(arg);
    		}else {
    			BeanWrapper bean = new BeanWrapperImpl(arg);
    			if(bean.isReadableProperty(definetion.field())) {
    				Object value = bean.getPropertyValue(definetion.field());
    				if(value != null && (value instanceof Collection<?> || value.getClass().isArray()) && definetion.match().length > 0) {
    					String [] arrVal = null;
    					if(value instanceof Collection<?>) {
    						arrVal = ((Collection<?>)value).stream().map(t->String.valueOf(t)).toArray(String[]::new);
    					}else if(value.getClass().isArray()) {
    						arrVal = toJsonArr(value);
    					}
    					if(arrVal != null) {
    						for (int i = 0; i < arrVal.length; i++) {
    							arrVal[i] = match(arrVal[i], definetion.match(), arrVal[i]);
							}
    						value = StringUtils.join(arrVal, ",");
    					}
    				}
    				param = convertParamValue(value);
    			}
    		}
    	}
    	param = match(param, definetion.match(), definetion.defaultMatchVal());
    	return param;
    }
	
	private static String match(String content, Match[] match, String defaultVal) {
		if(content == null) {
			content = StringUtils.EMPTY;
		}
    	for (int i = 0; i < match.length; i++) {
			if(Objects.equals(content, match[i].key())) {
				return match[i].val();
			}
		}
    	return StringUtils.isEmpty(defaultVal)?content:defaultVal;
    }
	
	private static String convertParamValue(Object val) {
    	if(val == null) {
    		return null;
    	}
    	if(ClassUtil.isNormalClass(val.getClass())) {
    		return String.valueOf(val);
    	}else {
    		return toJson(val);
    	}
    }
	
	public static void fillLogUserInfo(SystemLogDto dto, LogUserDto currentUser) {
    	if(currentUser == null) {
    		return ;
    	}
		if(dto == null || dto.getOperateId() != null) {
    		return;
    	}
		if(currentUser != null) {
			dto.setOperateId(currentUser.getId());
			dto.setOperateName(currentUser.getName());
			dto.setOperateAccount(currentUser.getAccount());
			dto.setOperateDeptId(currentUser.getUnitId());
			dto.setOperateDeptName(currentUser.getUnitName());
		}
    }
	
	public static void fillRequestInfo(SystemLogDto dto, SystemLog log, HttpServletRequest request) {
		if(dto == null) {
			return ;
		}
		if(request != null) {
			dto.setOperateIp(ServletUtil.getClientIp(request));
			dto.setAccessUrl(request.getRequestURL().toString());
    		Map<String, String> map = ServletUtil.getParameterMap(request);
    		String[] skipRequestParam = log.skipRequestParam();
    		for (int i = 0; i < skipRequestParam.length; i++) {
    			if(map.containsKey(skipRequestParam[i])) {
    				map.put(skipRequestParam[i], HIDDEN_VALUE);
    			}
    			
			}
    		dto.setParam(toJson(map));
    		dto.setRequestBody(getRequestBodyContent(request));

    		map = ServletUtil.getHeaderMap(request);
    		dto.setHeader(toJson(map));
    	}
	}
	
	public static String getRequestBodyContent(ServletRequest request) {
    	if(Objects.equals("application/json", request.getContentType())) {
    		if(request instanceof ContentCachingRequestWrapper) {
    			ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
    			String requestBody;
				try {
					requestBody = new String(requestWrapper.getContentAsByteArray(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					requestBody = new String(requestWrapper.getContentAsByteArray());
				}
        		return requestBody;
    		}else {
    			return "[UNKNOWM]";
    		}
    	}
    	return "";
    }
	
	private static String toJson(Object obj) {
    	if(obj == null) {
    		return null;
    	}
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			return "json序列化:" + obj;
		}
    }
	
	private static String[] toJsonArr(Object obj) {
    	if(obj == null) {
    		return null;
    	}
    	String str = toJson(obj);
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {
    		return objectMapper.readValue(str, String[].class);
    	} catch (Exception e) {
    		return new String[]{"json序列化:" + str};
    	}
    }

}
