package org.sonicframework.context.valid.support;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.sonicframework.context.valid.annotation.IntegerValid;

/**
 * @author lujunyi
 */
public class IntegerValidSupport implements ConstraintValidator<IntegerValid, Integer> {

	private String nullMsg = "%s不能为空";
	private String zeroMsg = "%s必须大于0";
	private String intMsg = "%s整数位长度不能大于%s";
	private String maxMsg = "%s不能大于%s";
	private String minMsg = "%s不能小于%s";

	private String message;
	private String fieldLabel;
	private boolean nullable;
	private boolean zeroable;
	private int intLen;
	private String max;
	private String min;

	private boolean noDefaultMsg = false;

	@Override
	public void initialize(IntegerValid annotation) {
		this.message = annotation.message();
		this.fieldLabel = annotation.fieldLabel();
		this.nullMsg = String.format(nullMsg, this.fieldLabel);
		this.zeroMsg = String.format(zeroMsg, this.fieldLabel);
		this.intMsg = String.format(intMsg, this.fieldLabel, annotation.intLen());
		this.maxMsg = String.format(maxMsg, this.fieldLabel, annotation.max());
		this.minMsg = String.format(minMsg, this.fieldLabel, annotation.min());
		nullable = annotation.nullable();
		zeroable = annotation.zeroable();
		this.intLen = annotation.intLen();
		this.max = annotation.max();
		this.min = annotation.min();
		if (StringUtils.isBlank(message)) {
			this.noDefaultMsg = true;
		}
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (value == null) {
			if (!this.nullable) {
				setMsg(context, nullMsg);
				return false;
			}
			return true;
		}
		if (!this.zeroable && value == 0D) {
			setMsg(context, zeroMsg);
			return false;
		}
		if (NumberUtils.isCreatable(this.max) && new Double(max) < value) {
			setMsg(context, maxMsg);
			return false;
		}
		if (NumberUtils.isCreatable(this.min) && new Double(min) > value) {
			setMsg(context, minMsg);
			return false;
		}
		if (intLen > 0 && new Double(Math.pow(10, intLen)).longValue() <= value.longValue()) {
			setMsg(context, intMsg);
			return false;
		}
		return true;
	}

	private void setMsg(ConstraintValidatorContext context, String msg) {
		if (noDefaultMsg) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
		}
	}

	public static int getNumberDecimalDigits(double number) {
		if (number == (long) number) {
			return 0;
		}
		
		String str = String.valueOf(number);
		String[] split = str.split("\\.");
		return split.length > 1?split[1].trim().length():0;
	}

}
