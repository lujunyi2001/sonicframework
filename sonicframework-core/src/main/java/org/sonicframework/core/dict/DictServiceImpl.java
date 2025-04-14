package org.sonicframework.core.dict;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.context.exception.DataNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
* @author lujunyi
*/
public class DictServiceImpl implements DictService {
	
	private static Logger log = LoggerFactory.getLogger(DictServiceImpl.class);
	
	@Autowired(required = false)
	private List<ExtendDictService> extendCodeServiceList;
	@Autowired(required = false)
	private DefaultDictService defaultDictService;
	
	public DictServiceImpl() {
	}
	
	@Override
	@Cacheable(value = "DictCodeDto", key = "'getByType_' + #type")
	public List<DictCodeDto> getByType(String type) {
		if(type == null) {
			throw new DataNotValidException("字典类型不能为空");
		}
		if(extendCodeServiceList != null) {
			for (ExtendDictService service : extendCodeServiceList) {
				if(service.support(type)) {
					return service.getAllCode(type);
				}
			}
		}
		if(defaultDictService != null) {
			return defaultDictService.getByType(type);
		}
		throw new DataNotValidException("没有找到类型为" + type + "的字典");
	}

	@Override
	@CacheEvict(value="DictCodeDto",allEntries=true)
	public void clearAllCache() {
		log.info("clean cache");
	}

	@Override
	@CacheEvict(value="DictCodeDto", key = "'getByType_' + #type")
	public void clearCache(String type) {
		log.info("clean cache [{}]", type);
	}

}
