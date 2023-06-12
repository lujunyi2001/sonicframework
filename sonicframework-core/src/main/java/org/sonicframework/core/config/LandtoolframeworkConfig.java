package org.sonicframework.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("landtoolframework")
public class LandtoolframeworkConfig {

	private boolean debug = false;
	
	public LandtoolframeworkConfig() {
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}



}
