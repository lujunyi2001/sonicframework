package org.sonicframework.core.dict;

import java.util.List;

import org.sonicframework.context.dto.DictCodeDto;

/**
* @author lujunyi
*/
public interface DefaultDictService {

	List<DictCodeDto> getByType(String type);
}
