package org.sonicframework.core.webapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import org.sonicframework.context.common.constaints.SwitchType;

/**
 * @author lujunyi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Controller
@ResponseBody
@ResponseResult
public @interface WebApiController {
	
	@AliasFor(annotation = Controller.class)
	String value() default "";
	
	@AliasFor(annotation = ResponseResult.class, attribute = "switchType")
	SwitchType switchType() default SwitchType.DEFAULT;
}
