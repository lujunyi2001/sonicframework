package org.sonicframework.context.exception;

import org.sonicframework.context.webapi.dto.ResultDto;

public class NotLogInException extends BaseBizException {

	private static final long serialVersionUID = 7859356794319564824L;

	public static final int CODE = ResultDto.RESULT_NOT_LOGIN;
	public static final String MESSAGE = "未登录或登录已过期";
	public NotLogInException() {
		super(CODE, MESSAGE);
	}
	public NotLogInException(String message) {
		super(CODE, message);
	}

	
}
