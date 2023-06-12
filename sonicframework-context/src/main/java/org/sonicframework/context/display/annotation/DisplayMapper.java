package org.sonicframework.context.display.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * @author lujunyi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface DisplayMapper {
	boolean display() default true;
	String label() default "";
	String dictName() default "";
	int order() default 999;
	Class<?>[] groups() default {};
}
