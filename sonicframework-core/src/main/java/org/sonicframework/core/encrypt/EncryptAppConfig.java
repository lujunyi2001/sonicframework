package org.sonicframework.core.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.core.webapi.service.WebApiResultApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
* @author lujunyi
*/
@Configuration
public class EncryptAppConfig<T> {
	
	@Autowired
	private EncryptConfig encryptConfig;
	@Autowired
	private RsakeyProviderService rsakeyProvider;
	@Autowired
	private WebApiResultApplyService<T> resultApplyService;
	
	private static Logger logger = LoggerFactory.getLogger(EncryptAppConfig.class);

	public EncryptAppConfig() {
	}

	@Bean
	@ConditionalOnMissingBean
	public RsakeyProviderService rsakeyProviderService() {
		logger.info("load RsakeyProviderService use default bean DefaultRsakeyProviderService");
		return new DefaultRsakeyProviderService();
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "landtoolframework.encrypt", name = "enable", havingValue = "true", matchIfMissing = false)
    public FilterRegistrationBean<DecryptFilter<T>> decryptFilter(){
    	FilterRegistrationBean<DecryptFilter<T>> filterRegBean = new FilterRegistrationBean<>();
    	DecryptFilter<T> decryptFilter = new DecryptFilter<>(this.encryptConfig, this.rsakeyProvider);
    	decryptFilter.setResultApplyService(resultApplyService);
    	filterRegBean.setFilter(decryptFilter);
    	filterRegBean.addUrlPatterns("/*");
    	filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
    	return filterRegBean;
    }
}
