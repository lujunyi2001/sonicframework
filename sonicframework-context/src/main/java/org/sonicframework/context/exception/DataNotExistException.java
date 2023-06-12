package org.sonicframework.context.exception;

public class DataNotExistException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1102;
	public static final String MESSAGE = "数据不存在或者已被删除";
	public DataNotExistException() {
		super(CODE, MESSAGE);
	}
	public DataNotExistException(String message) {
		super(CODE, message);
	}

	
}
