package org.sonicframework.context.sensitization.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sonicframework.context.sensitization.SensitizationSupport;

/**
* @author lujunyi
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Sensitization {

	Class<? extends SensitizationSupport>[] defaultSupport() default {};
	
	SensitizationEnv[] defaultEnv() default{};
	
	FieldSensitization[] map() default{};
	
	Class<?>[] groups() default {};
}
