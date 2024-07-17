package org.sonicframework.core.encrypt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.apache.commons.io.IOUtils;
import org.sonicframework.context.common.annotation.DecryptRequestBody;
import org.sonicframework.utils.encrypt.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
* @author lujunyi
*/
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {
	
	@Autowired
	private EncryptConfig config;
	@Autowired(required = false)
	private RsakeyProviderService rsakeyProvider;
	
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    	return config.isEnable() && rsakeyProvider != null && (config.isAlwaysDecryptRequestbody() || methodParameter.hasMethodAnnotation(DecryptRequestBody.class));
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
    	byte[] byteArray = IOUtils.toByteArray(inputMessage.getBody());
    	String requestBody = new String(byteArray);
    	byte[] decryptReturnBytes = RSAUtil.decryptReturnBytes(requestBody, rsakeyProvider.generatePrivateKey());
    	InputStream inputStream = new ByteArrayInputStream(decryptReturnBytes);
    	return new DecryptHttpInputMessage(inputStream, inputMessage.getHeaders());
    }

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

}