package org.sonicframework.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration("sonicWebConfig")
@ConfigurationProperties("sonicframework.web")
public class WebConfig {

	private boolean argumentBlankToNull = false;
	private boolean argumentTrim = false;
	private boolean outNullStrToNull = false;
	
	public WebConfig() {
	}

	public boolean isArgumentTrim() {
		return argumentTrim;
	}

	public void setArgumentTrim(boolean argumentTrim) {
		this.argumentTrim = argumentTrim;
	}

	public boolean isArgumentBlankToNull() {
		return argumentBlankToNull;
	}

	public void setArgumentBlankToNull(boolean argumentBlankToNull) {
		this.argumentBlankToNull = argumentBlankToNull;
	}

	public boolean isOutNullStrToNull() {
		return outNullStrToNull;
	}

	public void setOutNullStrToNull(boolean outNullStrToNull) {
		this.outNullStrToNull = outNullStrToNull;
	}


}
