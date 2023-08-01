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
	/** 根据参数索引获取的参数是否是从request,为true时为request */
	boolean isRequest() default false;
	/** 获取参数的索引 */
	int index() default 0;
	/** 获取参数中的属性值,isRequest为true时从request中的parameter中获取 */
	String field() default "";
	/** 匹配值 */
	Match[] match() default {};
	/** 默认值,match中的值匹配不到时获取该值 */
	String defaultMatchVal() default "";
	
}
