package org.sonicframework.utils;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class ConvertUtil {
	public final static String[] DATE_FORMAT_ALL = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd", 
			"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyyMMddHHmmss", 
			"yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss.SSS", "yyyyMMddHHmmssSSS", 
			"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy/MM/dd'T'HH:mm:ss.SSS'Z'"};

	@SuppressWarnings("unchecked")
	public static<T> T convertToObject(String value, Class<T> clazz) {
		if(StringUtils.isBlank(value)) {
			return null;
		}
		T result = null;
		try {
			if(Date.class.isAssignableFrom(clazz)) {
				result = (T) DateUtils.parseDateStrictly(value, DATE_FORMAT_ALL);
			}else if(String.class == clazz) {
				result = (T) value;
			}else if(Byte.class == clazz || byte.class == clazz) {
				result = (T) new Byte(value);
			}else if(Short.class == clazz || short.class == clazz) {
				result = (T) new Integer(value);
			}else if(Integer.class == clazz || int.class == clazz) {
				result = (T) new Integer(value);
			}else if(Long.class == clazz || long.class == clazz) {
				result = (T) new Long(value);
			}else if(Double.class == clazz || double.class == clazz) {
				result = (T) new Double(value);
			}else if(Float.class == clazz || float.class == clazz) {
				result = (T) new Float(value);
			}else if(Boolean.class == clazz || boolean.class == clazz) {
				result = (T) new Boolean(value);
			}else if(Character.class == clazz || char.class == clazz) {
				result = (T) new Character(value.charAt(0));
			}
		} catch (Exception e) {
		}
		
		return result;
	}
}
