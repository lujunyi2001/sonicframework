package org.sonicframework.core.cors;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
* @author lujunyi
*/
@Configuration
public class CorsAppConfig {
	private static Logger logger = LoggerFactory.getLogger(CorsAppConfig.class);

	public CorsAppConfig() {
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "sonicframework.corss-origin", name = "enable", havingValue = "true", matchIfMissing = false)
    public CorsFilter corsFilter(CorssOriginConfig corssOriginConfig) {
		List<String> mapprings = corssOriginConfig.getMappring();
		List<String> allowedOrigins = corssOriginConfig.getAllowedOrigins();
		List<String> allowedMethods = corssOriginConfig.getAllowedMethods();
		List<String> allowedHeaders = corssOriginConfig.getAllowedHeaders();
		List<String> exposedHeaders = corssOriginConfig.getExposedHeaders();
		Boolean allowCredentials = corssOriginConfig.getAllowCredentials();
		Long maxAge = corssOriginConfig.getMaxAge();
		
		UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = null;
		if(CollectionUtils.isNotEmpty(mapprings)) {
			for (String mapping : mapprings) {
				configuration = new CorsConfiguration();
				if(CollectionUtils.isNotEmpty(allowedOrigins)) {
					configuration.setAllowedOrigins(allowedOrigins);
				}
				if(CollectionUtils.isNotEmpty(allowedMethods)) {
					configuration.setAllowedMethods(allowedMethods);
				}
				if(CollectionUtils.isNotEmpty(allowedHeaders)) {
					configuration.setAllowedHeaders(allowedHeaders);
				}
				if(CollectionUtils.isNotEmpty(exposedHeaders)) {
					configuration.setExposedHeaders(exposedHeaders);
				}
				if(maxAge != null) {
					configuration.setMaxAge(maxAge);
				}
				if(allowCredentials != null) {
					configuration.setAllowCredentials(allowCredentials);
				}
				corsSource.registerCorsConfiguration(mapping, configuration);
				logger.info("registerCorsConfiguration mapping:[{}], allowedOrigins:[{}], maxAge:[{}], allowCredentials:[{}], allowedMethods:[{}], allowedHeaders:[{}]"
						+ ", exposedHeaders:[{}]"
						, mapping, allowedOrigins, maxAge, allowCredentials, allowedMethods, allowedHeaders, exposedHeaders);
			}
		}
        return new CorsFilter(corsSource);
	}

}
