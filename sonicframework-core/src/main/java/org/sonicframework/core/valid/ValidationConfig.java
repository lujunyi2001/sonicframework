package org.sonicframework.core.valid;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lujunyi
 */
@Configuration("sonicValidationConfig")
public class ValidationConfig {

	@Autowired
	private ValidConfig validConfig;

	@Bean("sonicValidator")
	public Validator validator() {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
				.failFast(validConfig.isFailfast()).buildValidatorFactory();
		return validatorFactory.getValidator();

	}


}
