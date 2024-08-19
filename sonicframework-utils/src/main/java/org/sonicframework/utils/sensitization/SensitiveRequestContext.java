package org.sonicframework.utils.sensitization;

/**
 * @author lujunyi
 */
public class SensitiveRequestContext {
	
	public final static String SENSITIVE_REQUEST_KEY = "sonicSensitiveRequest";

	private Class<?>[] groups;

	public SensitiveRequestContext() {
	}

	public SensitiveRequestContext(Class<?>[] groups) {
		super();
		this.groups = groups;
	}

	public Class<?>[] getGroups() {
		return groups;
	}

	public void setGroups(Class<?>[] groups) {
		this.groups = groups;
	}

}
