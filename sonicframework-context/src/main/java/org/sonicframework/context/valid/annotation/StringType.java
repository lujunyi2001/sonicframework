package org.sonicframework.context.valid.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.sonicframework.context.valid.support.StringTypeValidSupport;

/**
 * @author lujunyi
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {StringTypeValidSupport.class})
public @interface StringType {

	String message() default "";

	String fieldLabel() default "";
	
	boolean nullable() default true;
	
	boolean blankable() default true;
	
	int min() default -1;
	
	int max() default -1;
	
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
