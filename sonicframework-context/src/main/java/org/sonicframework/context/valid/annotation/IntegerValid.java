package org.sonicframework.context.valid.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.sonicframework.context.valid.support.IntegerValidSupport;


/**
 * @author lujunyi
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {IntegerValidSupport.class})
public @interface IntegerValid {

	String message() default "";

	String fieldLabel() default "";
	
	boolean nullable() default true;
	
	boolean zeroable() default true;
	
	int intLen() default -1;
	
	String max() default "";
	String min() default "";
	
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
