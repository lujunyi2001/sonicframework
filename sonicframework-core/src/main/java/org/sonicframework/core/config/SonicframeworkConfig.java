package org.sonicframework.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sonicframework")
public class SonicframeworkConfig {

	private boolean debug = false;
	
	public SonicframeworkConfig() {
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}



}
