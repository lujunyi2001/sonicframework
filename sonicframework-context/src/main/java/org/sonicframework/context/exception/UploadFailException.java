package org.sonicframework.context.exception;

public class UploadFailException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1106;
	public static final String MESSAGE = "上传失败";
	public UploadFailException() {
		super(CODE, MESSAGE);
	}
	public UploadFailException(String message) {
		super(CODE, message);
	}
	public UploadFailException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

	
}
