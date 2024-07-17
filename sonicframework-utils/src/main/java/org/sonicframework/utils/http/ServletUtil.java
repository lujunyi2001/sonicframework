package org.sonicframework.utils.http;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author lujunyi
 */
public class ServletUtil {

	private ServletUtil() {}
	
	public static boolean isAjax() {
		HttpServletRequest request = getRequest();
		return isAjax(request);
	}
	public static boolean isAjax(HttpServletRequest request) {
		if(request.getHeader("X-Requested-With") != null) {
			return true;
		}
		String accept = request.getHeader("accept");
		if (accept != null && accept.contains("application/json")) {
			return true;
		}
		return false;
	}
	public static ServletRequestAttributes getRequestAttributes() {

		return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	}

	public static HttpServletRequest getRequest() {
		return getRequestAttributes().getRequest();
	}

	public static HttpSession getSession() {
		return getRequest().getSession(true);
	}
	
	public static Map<String, String> getParameterMap(HttpServletRequest request) {
		return getParameterMap(request, new HashSet<>());
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> getParameterMap(HttpServletRequest request, Set<String> skipParam) {
		// 参数Map
		Map properties = request.getParameterMap();
		// 返回值Map
		Map returnMap = new HashMap();
		Iterator entries = properties.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		if(skipParam == null) {
			skipParam = new HashSet<>();
		}
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else if (valueObj instanceof String[]) {
				String[] values = (String[]) valueObj;
				value = StringUtils.join(values, ",");
			} else {
				value = valueObj.toString();
			}
			if(skipParam.contains(name)) {
				value = "******";
			}
			returnMap.put(name, value);
		}
		return returnMap;
	}
	
	public static Map<String, String> getHeaderMap(HttpServletRequest request) {
		Map<String, String> returnMap = new HashMap<>();
		Enumeration<String> names = request.getHeaderNames();
		String name = null;
		while (names.hasMoreElements()) {
			name = (String) names.nextElement();
			returnMap.put(name, request.getHeader(name));
		}
		return returnMap;
	}
	
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("X-Forwarded-For");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	    	ip = request.getHeader("X-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}
}
