package org.sonicframework.context.webapi.dto;

import java.util.List;

/**
 * @author 卢俊毅
 */
public class ExceptionStackTraceResultDto extends ResultDto {

	
	private static final long serialVersionUID = 3103017371037987995L;
	
	private List<String> stackTrace;

	public List<String> getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(List<String> stackTrace) {
		this.stackTrace = stackTrace;
	}


}
