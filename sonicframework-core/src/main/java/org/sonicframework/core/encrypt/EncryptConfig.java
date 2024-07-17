package org.sonicframework.core.encrypt;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sonicframework.encrypt")
public class EncryptConfig {

	private boolean enable = false;
	private String encryptKey = "__encryptKey";
    private Set<String> exclude = new HashSet<>();
    
    private boolean alwaysDecryptRequestbody = false;
    private boolean alwaysEcryptResponsebody = false;
	
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


	public Set<String> getExclude() {
		return exclude;
	}

	public void setExclude(Set<String> exclude) {
		this.exclude = exclude;
	}

	public boolean isAlwaysDecryptRequestbody() {
		return alwaysDecryptRequestbody;
	}

	public void setAlwaysDecryptRequestbody(boolean alwaysDecryptRequestbody) {
		this.alwaysDecryptRequestbody = alwaysDecryptRequestbody;
	}

	public boolean isAlwaysEcryptResponsebody() {
		return alwaysEcryptResponsebody;
	}

	public void setAlwaysEcryptResponsebody(boolean alwaysEcryptResponsebody) {
		this.alwaysEcryptResponsebody = alwaysEcryptResponsebody;
	}


}
