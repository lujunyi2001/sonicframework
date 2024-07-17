package org.sonicframework.context.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.sonicframework.context.common.enums.Alignment;
import org.sonicframework.context.common.enums.Border;
import org.sonicframework.context.common.enums.VerticalAlignment;

/**
 * @author lujunyi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Inherited
public @interface Style {
    int width() default 0;
    int height() default 0;
    String bgColor() default "";
    String fgColor() default "";
    int fontHeight() default 0;
    String fontName() default "";
    short fontHeightInPoints() default 0;
    Alignment alignment() default Alignment.NONE;
    VerticalAlignment vAlignment() default VerticalAlignment.NONE;
    boolean bold() default false;
    boolean italic() default false;
    boolean underline() default false;
    boolean strikeout() default false;
    String fontColor() default "";
    Border borderTop() default Border.NONE;
    Border borderRight() default Border.NONE;
    Border borderBottom() default Border.NONE;
    Border borderLeft() default Border.NONE;
    String borderTopColor() default "";
    String borderRightColor() default "";
    String borderBottomColor() default "";
    String borderLeftColor() default "";
    boolean wrapText() default false;
    int level() default 0;

}
