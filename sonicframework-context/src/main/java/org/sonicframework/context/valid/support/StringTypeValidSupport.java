package org.sonicframework.context.valid.support;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.sonicframework.context.valid.annotation.StringType;

/**
 * @author lujunyi
 */
public class StringTypeValidSupport implements ConstraintValidator<StringType, String> {
	
	private String nullMsg = "%s不能为空";
	private String blankMsg = "%s不能为空格";
	private String minMsg = "%s长度不能小于%d";
	private String maxMsg = "%s长度不能大于%d";
	
	private String message;
	private String fieldLabel;
	private boolean nullable;
	private boolean blankable;
	private int min;
	private int max;
	
	private boolean noDefaultMsg = false;
	
	@Override
	public void initialize(StringType annotation) {
		this.message = annotation.message();
		this.fieldLabel = annotation.fieldLabel();
		this.blankable = annotation.blankable();
		this.min = annotation.min();
		this.max = annotation.max();
		
		this.nullMsg = String.format(nullMsg, this.fieldLabel);
		this.blankMsg = String.format(blankMsg, this.fieldLabel);
		this.minMsg = String.format(minMsg, this.fieldLabel, this.min);
		this.maxMsg = String.format(maxMsg, this.fieldLabel, this.max);
		nullable = annotation.nullable();
		if(StringUtils.isBlank(message)) {
			this.noDefaultMsg = true;
		}
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if( value == null ){
			if(!this.nullable) {
				setMsg(context, nullMsg);
				return false;
			}
            return true;
		}
		if(!this.blankable && StringUtils.isBlank(value)) {
			setMsg(context, this.blankMsg);
			return false;
		}
		int length = value.length();
		if(this.min >= 0 && length < this.min) {
			setMsg(context, this.minMsg);
			return false;
		}
		if(this.max >= 0 && length > this.max) {
			setMsg(context, this.maxMsg);
			return false;
		}
		return true;
	}
	
	private void setMsg(ConstraintValidatorContext context, String msg) {
		if(noDefaultMsg) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
		}
	}
}
