package org.sonicframework.context.webapi.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author 卢俊毅
 */
public class ResultDto implements Serializable {

	
	private static final long serialVersionUID = 3103017371037987995L;
	
	public final static int RESULT_SUCCESS = 0;
	public final static int RESULT_ARGUMENT_ERROR = 1000;
	public final static int RESULT_SIGN_ERROR = 2000;
	public final static int RESULT_NOT_LOGIN = 3000;
	public final static int RESULT_LOGIN_TOKEN_VALID_FAIL = 3001;
	public final static int RESULT_LOGIN_EXPIRE = 3002;
	public final static int RESULT_LOGIN_IP_CHANGE = 3003;
	public final static int RESULT_LOGIN_USER_CHANGE = 3004;
	public final static int RESULT_FORBIDDEN = 403;
	public final static int RESULT_EXCEPTION = 500;
	private int result = RESULT_SUCCESS;
	private String message;
	private Object data;
	
	public ResultDto() {
	}
	public ResultDto(int result, String message) {
		this.result = result;
		this.message = message;
	}
	public ResultDto(int result, String message, Object data) {
		super();
		this.result = result;
		this.message = message;
		this.data = data;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
