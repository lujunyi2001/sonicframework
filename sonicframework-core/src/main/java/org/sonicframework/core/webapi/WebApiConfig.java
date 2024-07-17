package org.sonicframework.core.webapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("sonicWebApiConfig")
@ConfigurationProperties("sonicframework.webapi")
public class WebApiConfig {

	private boolean returnUnifiedResult = true;
	
	public WebApiConfig() {
	}

	public boolean isReturnUnifiedResult() {
		return returnUnifiedResult;
	}

	public void setReturnUnifiedResult(boolean returnUnifiedResult) {
		this.returnUnifiedResult = returnUnifiedResult;
	}

	
}
