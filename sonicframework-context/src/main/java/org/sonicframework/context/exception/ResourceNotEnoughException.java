package org.sonicframework.context.exception;

public class ResourceNotEnoughException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = 9001;
	public static final String MESSAGE = "系统资源不足";
	public ResourceNotEnoughException() {
		this(MESSAGE);
	}
	public ResourceNotEnoughException(String message) {
		super(CODE, message);
	}
	public ResourceNotEnoughException(Throwable t) {
		this(MESSAGE, t);
	}
	public ResourceNotEnoughException(String message, Throwable t) {
		super(CODE, message, t);
	}

	
}
