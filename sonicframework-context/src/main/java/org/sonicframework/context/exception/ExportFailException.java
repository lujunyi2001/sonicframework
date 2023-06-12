package org.sonicframework.context.exception;

public class ExportFailException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 1107;
	public static final String MESSAGE = "导出失败";
	public ExportFailException() {
		super(CODE, MESSAGE);
	}
	public ExportFailException(String message) {
		super(CODE, message);
	}
	public ExportFailException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

	
}
