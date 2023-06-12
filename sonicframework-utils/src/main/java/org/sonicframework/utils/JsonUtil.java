package org.sonicframework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sonicframework.context.exception.ConvertFailException;

/**
* @author lujunyi
*/
public class JsonUtil {

	private static ObjectMapper objectMapper;
	private JsonUtil() {}
	
	private static ObjectMapper getObjectMapper() {
		if(objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}
	
	public static String toJson(Object obj) {
        if(obj == null) {
        	return null;
        }
        try {
			return getObjectMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new ConvertFailException("json转换失败", e);
		}
    }

}
