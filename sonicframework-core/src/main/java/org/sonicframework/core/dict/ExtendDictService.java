package org.sonicframework.core.dict;

import java.util.List;

import org.sonicframework.context.dto.DictCodeDto;

/**
* @author lujunyi
*/
public interface ExtendDictService {
	boolean support(String type);
	List<DictCodeDto> getAllCode(String type);
}
