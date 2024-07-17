package org.sonicframework.core.sensitization;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("sonicSensitizationConfig")
@ConfigurationProperties("sonicframework.sensitization")
public class SensitizationConfig {

	private boolean enable = true;
	
	public SensitizationConfig() {
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}


}
