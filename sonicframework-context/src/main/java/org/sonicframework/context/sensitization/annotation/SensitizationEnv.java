package org.sonicframework.context.sensitization.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* @author lujunyi
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface SensitizationEnv {

	int start() default 0;
	int end() default 0;
	String pattern() default "@";
	String mask() default "*";
	boolean maskRepeat() default true;
}
