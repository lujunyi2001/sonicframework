package org.sonicframework.context.exception;

public class ConvertFailException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1104;
	public ConvertFailException(String message) {
		super(CODE, message);
	}
	public ConvertFailException(String message, Throwable t) {
		super(CODE, message, t);
	}

	
}
