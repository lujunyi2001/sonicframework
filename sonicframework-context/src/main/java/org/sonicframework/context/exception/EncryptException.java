package org.sonicframework.context.exception;

public class EncryptException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1109;
	public static final String MESSAGE = "加密或解密失败";
	public EncryptException() {
		super(CODE, MESSAGE);
	}
	public EncryptException(String message) {
		super(CODE, message);
	}
	public EncryptException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

	
}
