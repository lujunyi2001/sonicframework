package org.sonicframework.context.log.annotation;

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
@Target(ElementType.METHOD)
@Inherited
public @interface SystemLog {
	String module();
	String optType();
	String content() default "";
	ContentParam[] param() default {};
	String[] skipRequestParam() default{};
	
}
