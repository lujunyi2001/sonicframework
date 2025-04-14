package org.sonicframework.utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;

import org.sonicframework.context.exception.DataNotValidException;

/**
* @author lujunyi
*/
public class ValidationUtil {

	private ValidationUtil() {}
	
	public static void checkValid(Object obj, Class<?>... group) {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败模式
                .failFast(false)
                // .addProperty( "hibernate.validator.fail_fast", "true" )
                .buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Object>> validate = validator.validate(obj, group);
		List<String> msgList = validate.stream().map(v -> v.getMessage()).collect(Collectors.toList());
		if(msgList.isEmpty()) {
		}else {
			throw new DataNotValidException(StringUtils.join(msgList, ","));
		}
	}
	public static List<String> valid(Object obj, Class<?>... group) {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
				.configure()
				// 快速失败模式
				.failFast(false)
				// .addProperty( "hibernate.validator.fail_fast", "true" )
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Object>> validate = validator.validate(obj, group);
		List<String> msgList = validate.stream().map(v -> v.getMessage()).collect(Collectors.toList());
		return msgList;
	}

}
