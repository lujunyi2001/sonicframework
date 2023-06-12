package org.sonicframework.core.sensitization.wrapper;

import org.springframework.stereotype.Service;

import org.sonicframework.context.sensitization.SensitizationResultWrapperSupport;
import org.sonicframework.context.webapi.dto.ResultDto;

/**
* @author lujunyi
*/
@SuppressWarnings("rawtypes")
@Service
public class ResultDtoSensitizationResultWrapperSupport implements SensitizationResultWrapperSupport {

	public ResultDtoSensitizationResultWrapperSupport() {
	}

	@Override
	public boolean isSupport(Object obj) {
		return obj != null && obj instanceof ResultDto;
	}

	@Override
	public Object get(Object obj) {
		return ((ResultDto)obj).getData();
	}

	@Override
	public Object set(Object obj, Object t) {
		((ResultDto)obj).setData(t);
		return obj;
	}

}
