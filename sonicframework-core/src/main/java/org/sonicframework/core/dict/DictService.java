package org.sonicframework.core.dict;

import java.util.List;

import org.sonicframework.context.dto.DictCodeDto;

/**
* @author lujunyi
*/
public interface DictService {

	List<DictCodeDto> getByType(String type);
	void clearCache(String type);
	void clearAllCache();
}
