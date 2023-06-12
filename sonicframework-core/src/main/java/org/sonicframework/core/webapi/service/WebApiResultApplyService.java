package org.sonicframework.core.webapi.service;

import org.sonicframework.context.exception.BaseBizException;

/**
* @author lujunyi
*/
public interface WebApiResultApplyService<T> {

	Class<T> applyClass();
	T apply();
	T applyByResult(int result, String message);
	T applyBySuccess(Object data);
	T applyByBizException(BaseBizException exception);
	T applyByUncaughException(Exception exception);
	
	void fillResult(T resultDto, int result, String message);
	void fillResultBySuccess(T resultDto, Object data);
	void fillResultByBizException(T resultDto, BaseBizException exception);
	void fillResultByUncaughException(T resultDto, Exception exception);
}
