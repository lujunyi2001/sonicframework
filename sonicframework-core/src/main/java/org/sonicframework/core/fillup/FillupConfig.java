package org.sonicframework.core.fillup;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("landtoolframework.fillup")
public class FillupConfig {

	private boolean enable = true;
	
	public FillupConfig() {
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}


}
