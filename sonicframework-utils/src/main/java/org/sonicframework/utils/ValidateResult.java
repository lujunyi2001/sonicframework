package org.sonicframework.utils;

import java.util.List;

import org.sonicframework.context.dto.BaseDto;

/**
* @author lujunyi
*/
public class ValidateResult extends BaseDto {

	private static final long serialVersionUID = -819111818595706534L;
	
	private boolean validResult;
	private String validMessage;
	private List<String> errMsgList;

	public ValidateResult(boolean validResult, String validMessage) {
		super();
		this.validResult = validResult;
		this.validMessage = validMessage;
	}
	public ValidateResult(boolean validResult, String validMessage, List<String> errMsgList) {
		super();
		this.validResult = validResult;
		this.validMessage = validMessage;
		this.errMsgList = errMsgList;
	}

	/**
	 * 获取验证结果
	 * @return true:验证成功,false:验证失败
	 */
	public boolean isValidResult() {
		return validResult;
	}

	/**
	 * 获取验证信息
	 * @return 验证失败时返回错误信息
	 */
	public String getValidMessage() {
		return validMessage;
	}
	
	/**
	 * 获取验证失败信息信息列表
	 * @return 验证失败时返回错误信息列表
	 */
	public List<String> getErrMsgList() {
		return errMsgList;
	}


}
