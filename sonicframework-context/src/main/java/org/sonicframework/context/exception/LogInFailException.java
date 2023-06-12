package org.sonicframework.context.exception;

public class LogInFailException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1002;
	public static final String MESSAGE = "登录失败";
	public LogInFailException() {
		super(CODE, MESSAGE);
	}
	public LogInFailException(String message) {
		super(CODE, message);
	}

	
}
