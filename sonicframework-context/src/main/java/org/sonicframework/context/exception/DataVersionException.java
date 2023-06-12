package org.sonicframework.context.exception;

public class DataVersionException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1105;
	public static final String MESSAGE = "数据发生变化";
	public DataVersionException() {
		this(CODE, MESSAGE);
	}
	public DataVersionException(int code, String message) {
		super(code, message);
	}

	
}
