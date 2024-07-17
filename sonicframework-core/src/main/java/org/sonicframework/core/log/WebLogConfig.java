package org.sonicframework.core.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration("sonicWebLogConfig")
@ConfigurationProperties("sonicframework.weblog")
public class WebLogConfig {

	private boolean enableAroundLog = true;
	private boolean enableSystemLog = true;
	private boolean enableServiceLog = false;
	
	private List<Class<?>> excludeException;
	
	private List<String> skipRequestParam = new ArrayList<>(Arrays.asList("password", "confirmPassword"));
	
	public WebLogConfig() {
	}

	public boolean isEnableAroundLog() {
		return enableAroundLog;
	}

	public void setEnableAroundLog(boolean enableAroundLog) {
		this.enableAroundLog = enableAroundLog;
	}

	public boolean isEnableSystemLog() {
		return enableSystemLog;
	}

	public void setEnableSystemLog(boolean enableSystemLog) {
		this.enableSystemLog = enableSystemLog;
	}

	public List<String> getSkipRequestParam() {
		return skipRequestParam;
	}

	public void setSkipRequestParam(List<String> skipRequestParam) {
		this.skipRequestParam = skipRequestParam;
	}

	public boolean isEnableServiceLog() {
		return enableServiceLog;
	}

	public void setEnableServiceLog(boolean enableServiceLog) {
		this.enableServiceLog = enableServiceLog;
	}

	public List<Class<?>> getExcludeException() {
		return excludeException;
	}

	public void setExcludeException(List<Class<?>> excludeException) {
		this.excludeException = excludeException;
	}

}
