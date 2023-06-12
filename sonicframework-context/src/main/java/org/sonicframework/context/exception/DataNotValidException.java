package org.sonicframework.context.exception;

public class DataNotValidException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1101;
	public static final String MESSAGE = "数据不合法";
	public DataNotValidException() {
		super(CODE, MESSAGE);
	}
	public DataNotValidException(String message) {
		super(CODE, message);
	}

	
}
