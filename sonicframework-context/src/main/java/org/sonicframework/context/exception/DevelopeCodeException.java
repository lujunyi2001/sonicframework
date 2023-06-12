package org.sonicframework.context.exception;

public class DevelopeCodeException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = -1;
	public DevelopeCodeException(String message) {
		super(CODE, message);
	}
	public DevelopeCodeException(String message, Throwable t) {
		super(CODE, message, t);
	}

	
}
