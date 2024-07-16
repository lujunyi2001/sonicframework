package org.sonicframework.core.cors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sonicframework.corss-origin")
public class CorssOriginConfig {

	private boolean enable = false;
	private List<String> mappring = new ArrayList<>(Arrays.asList("/**"));
	private List<String> allowedOrigins = new ArrayList<>(Arrays.asList("*"));
    private List<String> allowedMethods = new ArrayList<>(Arrays.asList("*"));
    private List<String> allowedHeaders = new ArrayList<>(Arrays.asList("*"));
    private List<String> exposedHeaders;
    private Boolean allowCredentials = Boolean.TRUE;
    private Long maxAge = 3600L;
	
	public CorssOriginConfig() {
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	public List<String> getAllowedMethods() {
		return allowedMethods;
	}

	public void setAllowedMethods(List<String> allowedMethods) {
		this.allowedMethods = allowedMethods;
	}

	public List<String> getAllowedHeaders() {
		return allowedHeaders;
	}

	public void setAllowedHeaders(List<String> allowedHeaders) {
		this.allowedHeaders = allowedHeaders;
	}

	public List<String> getExposedHeaders() {
		return exposedHeaders;
	}

	public void setExposedHeaders(List<String> exposedHeaders) {
		this.exposedHeaders = exposedHeaders;
	}

	public Boolean getAllowCredentials() {
		return allowCredentials;
	}

	public void setAllowCredentials(Boolean allowCredentials) {
		this.allowCredentials = allowCredentials;
	}

	public Long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}

	public List<String> getMappring() {
		return mappring;
	}

	public void setMappring(List<String> mappring) {
		this.mappring = mappring;
	}


	
}
