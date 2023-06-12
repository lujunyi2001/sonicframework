package org.sonicframework.context.exception;

public class FileCheckException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1108;
	public static final String MESSAGE = "文件解析失败";
	public FileCheckException() {
		super(CODE, MESSAGE);
	}
	public FileCheckException(String message) {
		super(CODE, message);
	}
	public FileCheckException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

	
}
