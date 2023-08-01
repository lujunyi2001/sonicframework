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
	/** 模块名称 */
	String module();
	/** 操作名称 */
	String optType();
	/** 操作内容 */
	String content() default "";
	/** 操作内容自动填充 */
	ContentParam[] param() default {};
	/** 略过request中的某些值 */
	String[] skipRequestParam() default{};
	
}
