package org.sonicframework.core.valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sonicframework.valid")
public class ValidConfig {

	private boolean failfast = false;
	
	public ValidConfig() {
	}

	public boolean isFailfast() {
		return failfast;
	}

	public void setFailfast(boolean failfast) {
		this.failfast = failfast;
	}

}
