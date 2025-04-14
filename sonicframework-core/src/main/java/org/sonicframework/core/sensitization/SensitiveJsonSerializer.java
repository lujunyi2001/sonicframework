package org.sonicframework.core.sensitization;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.sonicframework.context.sensitization.annotation.FieldSensitization;
import org.sonicframework.utils.http.ServletUtil;
import org.sonicframework.utils.sensitization.SensitiveRequestContext;
import org.sonicframework.utils.sensitization.SensitizationItemVo;
import org.sonicframework.utils.sensitization.SensitizationUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
* @author lujunyi
*/
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

	private SensitizationItemVo itemVo;

	private FieldSensitization annotation;
	public SensitiveJsonSerializer() {
	}

	public SensitiveJsonSerializer(SensitizationItemVo itemVo, FieldSensitization annotation) {
		this.itemVo = itemVo;
		this.annotation = annotation;
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
			throws JsonMappingException {
		if(property == null) {
			return this;
		}
		AnnotatedMember member = property.getMember();
		String fieldName = property.getName();
		FieldSensitization annotation = member.getAnnotation(FieldSensitization.class);
    	if(annotation != null) {
    		SensitizationItemVo itemVo =  SensitizationUtil.buildSensitizationItemVo(fieldName, annotation);
			return new SensitiveJsonSerializer(itemVo, annotation);
		}
		return this;
	}
	
	private SensitizationItemVo enableSensitive(String fieldName, FieldSensitization annotation, SensitiveRequestContext context) {
		if(annotation == null || context == null) {
			return null;
		}
		SensitizationItemVo vo = this.itemVo;
		
		if(vo != null) {
			if (!ArrayUtils.isEmpty(context.getGroups()) && !ArrayUtils.isEmpty(vo.getGroups())) {
				Set<Class<?>> contextGroup = new HashSet<>(Arrays.asList(context.getGroups()));
				if(!Stream.of(vo.getGroups()).anyMatch(t->contextGroup.contains(t))) {
					vo = null;
				}
			}
		}
		return vo;
	}

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if(this.itemVo != null && value != null) {
			HttpServletRequest request = ServletUtil.getRequest();
			SensitiveRequestContext context = (SensitiveRequestContext) request.getAttribute(SensitiveRequestContext.SENSITIVE_REQUEST_KEY);
			SensitizationItemVo actualItemVo = enableSensitive(itemVo.getFieldName(), annotation, context);
			if(actualItemVo != null) {
				value = SensitizationUtil.encryptString(value, actualItemVo);
			}
		}

		gen.writeString(value);
	}

}
