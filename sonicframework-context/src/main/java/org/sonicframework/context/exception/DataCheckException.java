package org.sonicframework.context.exception;

public class DataCheckException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1103;
	public DataCheckException(String message) {
		super(CODE, message);
	}
	public DataCheckException(String message, Throwable t) {
		super(CODE, message, t);
	}

	
}
