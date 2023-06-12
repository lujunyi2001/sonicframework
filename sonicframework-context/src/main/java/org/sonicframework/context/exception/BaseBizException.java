package org.sonicframework.context.exception;

public class BaseBizException extends RuntimeException {

	private static final long serialVersionUID = -862522456173742109L;
	private int code;

	
	public BaseBizException(int code) {
		super();
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public BaseBizException(int code, String message) {
		super(message);
		this.code = code;
	}
	public BaseBizException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
	@Override
	public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return s + ": " + code + ":" + (message != null?message:"");
    }
}
