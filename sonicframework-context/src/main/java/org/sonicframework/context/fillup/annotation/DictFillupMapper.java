package org.sonicframework.context.fillup.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sonicframework.context.fillup.DictCodeBindType;
import org.sonicframework.context.fillup.FillupConst;
import org.sonicframework.context.fillup.FillupNotMatch;

/**
* @author lujunyi
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(DictFillupMappers.class)
@Inherited
public @interface DictFillupMapper {
	String dictName();
	String target() default FillupConst.TARGET_THIS;
	DictCodeBindType bindType() default DictCodeBindType.VALUE;
	String split() default "";
	String outputSplit() default "";
	
	FillupNotMatch matchFail() default FillupNotMatch.SKIP;
	String defaultVal() default "";
	
	String label() default "";
	
	Class<?>[] groups() default{};
}

