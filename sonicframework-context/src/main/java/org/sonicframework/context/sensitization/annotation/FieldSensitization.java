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
@Target({ElementType.FIELD})
@Inherited
public @interface FieldSensitization {

	String key() default "";
	Class<? extends SensitizationSupport>[] support() default {};
	SensitizationEnv[] env() default {};
	Class<?>[] groups() default {};
}
