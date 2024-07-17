package org.sonicframework.context.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sonicframework.context.common.constaints.FieldMapperConst;

/**
* @author lujunyi
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ClassFieldMappers.class)
@Inherited
public @interface ClassFieldMapper {
	String local();
	String other();
	String label() default "";
	String dictName() default "";
	String format() default "";
	Class<?>[] targetClass() default {};
	String[] titleGroups() default {};
	Match[] match() default{};
	boolean matchContains() default false;
	String splitSep() default "";
	String splitImpSep() default "";
	String splitExpSep() default "";
	boolean splitNoMatch2Null() default false;
	int order() default 0;
	int action() default FieldMapperConst.MAPPER_BOTH;
	Class<? extends SerializeSupport<?, ?>>[] serialize() default{};
	Class<?>[] groups() default{};
	int length() default 0;
	Style[] titleStyle() default {};
	Style[] contentStyle() default {};
}

