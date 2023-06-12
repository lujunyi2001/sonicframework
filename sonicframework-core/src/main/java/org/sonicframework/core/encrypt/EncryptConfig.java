package org.sonicframework.core.encrypt;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("landtoolframework.encrypt")
public class EncryptConfig {

	private boolean enable = false;
	private String encryptKey = "__encryptKey";
	private Set<String> include = new HashSet<>();
    private Set<String> exclude = new HashSet<>();
	
	public EncryptConfig() {
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getEncryptKey() {
		return encryptKey;
	}

	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}

	public Set<String> getInclude() {
		return include;
	}

	public void setInclude(Set<String> include) {
		this.include = include;
	}

	public Set<String> getExclude() {
		return exclude;
	}

	public void setExclude(Set<String> exclude) {
		this.exclude = exclude;
	}


}
