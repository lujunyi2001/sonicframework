package org.sonicframework.core.webapi.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lujunyi
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@ResponseBody
public @interface ExceptionHandlerStatus {
	HttpStatus value() default HttpStatus.OK;
}
