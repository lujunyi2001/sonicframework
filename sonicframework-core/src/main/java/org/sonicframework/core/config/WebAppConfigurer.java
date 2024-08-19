package org.sonicframework.core.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.sonicframework.core.encrypt.EncryptResponseHandlerInterceptor;
import org.sonicframework.core.sensitization.SensitiveJsonSerializer;
import org.sonicframework.core.sensitization.SensitizationConfig;
import org.sonicframework.core.webapi.ResponseResultHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author lujunyi
 */
@Configuration("sonicWebAppConfigurer")
public class WebAppConfigurer implements WebMvcConfigurer {

	@Autowired
	private ResponseResultHandlerInterceptor<?> responseResultHandlerInterceptor;
	@Autowired
	private EncryptResponseHandlerInterceptor<?> encryptResponseHandlerInterceptor;
	@Autowired
	private WebConfig webConfig;
	@Autowired
	private SensitizationConfig sensitizationConfig;
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatterForFieldType(String.class, new Formatter<String>() {

			@Override
			public String print(String object, Locale locale) {
				return object;
			}

			@Override
			public String parse(String text, Locale locale) throws ParseException {
				if(webConfig.isArgumentBlankToNull() && StringUtils.isBlank(text)) {
					return null;
				}
				if(webConfig.isArgumentTrim() && text != null) {
					return StringUtils.trim(text);
				}
				return text;
			}
		});
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(responseResultHandlerInterceptor).addPathPatterns("/**");
		registry.addInterceptor(encryptResponseHandlerInterceptor).addPathPatterns("/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter() {
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
		};
		
		if(sensitizationConfig.isEnable()) {
			ObjectMapper objectMapper = converter.getObjectMapper();
			SimpleModule simpleModule = new SimpleModule();
			simpleModule.addSerializer(String.class, new SensitiveJsonSerializer());
			objectMapper.registerModule(simpleModule);
		}
		
		
		converter.setSupportedMediaTypes(new ArrayList<>(Arrays.asList(MediaType.APPLICATION_JSON,
				new MediaType("application", "*+json"), MediaType.TEXT_HTML)));
		converters.add(0, converter);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = converter.getObjectMapper();
		// 生成JSON时,将所有Long转换成String
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		
		if(webConfig.isArgumentBlankToNull() || webConfig.isArgumentTrim()) {
			simpleModule.addDeserializer(String.class, new StdDeserializer<String>(String.class) {
				private static final long serialVersionUID = 1602849816560031153L;

				@Override
				public String deserialize(JsonParser p, DeserializationContext ctxt)
						throws IOException, JsonProcessingException {
					String text = StringDeserializer.instance.deserialize(p, ctxt);
					if(webConfig.isArgumentBlankToNull() && StringUtils.isBlank(text)) {
						return null;
					}
					if(webConfig.isArgumentTrim() && text != null) {
						return StringUtils.trim(text);
					}
					return text;
				}
			});
		}
		
		
		objectMapper.registerModule(simpleModule);
		// 时间格式化
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// 设置格式化内容
		converter.setObjectMapper(objectMapper);
		converters.add(0, converter);
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	@Override
	public Validator getValidator() {
		return null;
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		return null;
	}
	
	@Bean("sonicLoggingFilterRegistration")
	public FilterRegistrationBean<Filter> loggingFilterRegistration() {
	    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
	    RequestBodyFilter filter = new RequestBodyFilter();
	    registration.setFilter(filter);
	    registration.setUrlPatterns(Collections.singleton("/*"));
	    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
	    return registration;
	}

}
