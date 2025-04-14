package org.sonicframework.core.config;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
* @author lujunyi
*/
public class SonicMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

	private StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();

	@Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		if (object instanceof String && type == String.class) {
			stringConverter.write((String) object, MediaType.TEXT_HTML, outputMessage);
			return;
		}
		super.writeInternal(object, type, outputMessage);
	}
	

}
