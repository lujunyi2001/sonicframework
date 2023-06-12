package org.sonicframework.context.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.sonicframework.context.common.annotation.Match;

/**
 * @author lujunyi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ContentParam {
	boolean isRequest() default false;
	int index() default 0;
	String field() default "";
	Match[] match() default {};
	String defaultMatchVal() default "";
	
}
