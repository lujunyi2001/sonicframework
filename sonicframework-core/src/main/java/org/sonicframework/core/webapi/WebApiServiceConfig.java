package org.sonicframework.core.webapi;

import org.sonicframework.core.webapi.provider.DefaultWebApiResultApplyServiceImpl;
import org.sonicframework.core.webapi.service.WebApiResultApplyService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.sonicframework.context.webapi.dto.ResultDto;

@Configuration
public class WebApiServiceConfig {

	@Bean("webApiResultApplyService")
	@ConditionalOnMissingBean(WebApiResultApplyService.class)
	public WebApiResultApplyService<ResultDto> webApiResultApplyService() {
		return new DefaultWebApiResultApplyServiceImpl();
	}

	
}
