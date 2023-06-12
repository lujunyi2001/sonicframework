package org.sonicframework.context.exception;

public class NoAuthException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1003;
	public static final String MESSAGE = "没有权限";
	public NoAuthException() {
		super(CODE, MESSAGE);
	}
	public NoAuthException(String message) {
		super(CODE, message);
	}

	
}
