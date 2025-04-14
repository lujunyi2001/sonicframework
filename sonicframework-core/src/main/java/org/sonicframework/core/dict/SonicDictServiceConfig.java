package org.sonicframework.core.dict;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @author lujunyi
*/
@Configuration("sonicDictServiceConfig")
public class SonicDictServiceConfig {

	public SonicDictServiceConfig() {
	}
	
	@Bean("sonicDictService")
	@ConditionalOnMissingBean(DictService.class)
	public DictService dictService() {
		return new DictServiceImpl();
	}

}
