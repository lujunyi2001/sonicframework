package org.sonicframework.core.webapi.provider;

import org.sonicframework.core.config.LandtoolframeworkConfig;
import org.sonicframework.core.webapi.service.WebApiResultApplyService;
import org.springframework.beans.factory.annotation.Autowired;

import org.sonicframework.context.exception.BaseBizException;
import org.sonicframework.context.webapi.dto.ExceptionStackTraceResultDto;
import org.sonicframework.context.webapi.dto.ResultDto;
import org.sonicframework.utils.ClassUtil;

/**
* @author lujunyi
*/
public class DefaultWebApiResultApplyServiceImpl implements WebApiResultApplyService<ResultDto> {

	@Autowired
	private LandtoolframeworkConfig frameworkConfig;
	
	public DefaultWebApiResultApplyServiceImpl() {
	}

	@Override
	public Class<ResultDto> applyClass() {
		return ResultDto.class;
	}
	
	@Override
	public ResultDto apply() {
		return new ResultDto(ResultDto.RESULT_SUCCESS, null);
	}

	@Override
	public ResultDto applyByResult(int result, String message) {
		ResultDto resultDto = apply();
		resultDto.setResult(result);
		resultDto.setMessage(message);
		return resultDto;
	}

	@Override
	public ResultDto applyBySuccess(Object data) {
		ResultDto resultDto = apply();
		resultDto.setData(data);
		return resultDto;
	}

	@Override
	public ResultDto applyByBizException(BaseBizException exception) {
		ResultDto resultDto = apply();
		resultDto.setResult(exception.getCode());
		resultDto.setMessage(exception.getMessage());
		return resultDto;
	}

	@Override
	public ResultDto applyByUncaughException(Exception exception) {
		ResultDto resultDto = apply();
		if(frameworkConfig.isDebug()) {
			ExceptionStackTraceResultDto traceResult = new ExceptionStackTraceResultDto();
			traceResult.setStackTrace(ClassUtil.parseStackTrace(exception));
			resultDto = traceResult;
		}
		resultDto.setResult(ResultDto.RESULT_EXCEPTION);
		resultDto.setMessage(exception.toString());
		return resultDto;
	}

	@Override
	public void fillResult(ResultDto resultDto, int result, String message) {
		resultDto.setResult(result);
		resultDto.setMessage(message);
	}

	@Override
	public void fillResultBySuccess(ResultDto resultDto, Object data) {
		resultDto.setData(data);
	}

	@Override
	public void fillResultByBizException(ResultDto resultDto, BaseBizException exception) {
		resultDto.setResult(exception.getCode());
		resultDto.setMessage(exception.getMessage());
	}

	@Override
	public void fillResultByUncaughException(ResultDto resultDto, Exception exception) {
		if(frameworkConfig.isDebug() && resultDto instanceof ExceptionStackTraceResultDto) {
			ExceptionStackTraceResultDto traceResult = (ExceptionStackTraceResultDto) resultDto;
			traceResult.setStackTrace(ClassUtil.parseStackTrace(exception));
			resultDto = traceResult;
		}
		resultDto.setResult(ResultDto.RESULT_EXCEPTION);
		resultDto.setMessage(exception.toString());
	}

}
