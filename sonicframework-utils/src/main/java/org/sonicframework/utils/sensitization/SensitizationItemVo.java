package org.sonicframework.utils.sensitization;

import org.sonicframework.context.sensitization.Env;
import org.sonicframework.context.sensitization.SensitizationSupport;

/**
* @author lujunyi
*/
public class SensitizationItemVo {

	private String fieldName;
	private Env env;
	private Class<?>[] groups = new Class[0];
	private SensitizationSupport support;
	public SensitizationItemVo() {
	}
	
	public SensitizationItemVo(String fieldName, Env env, Class<?>[] groups) {
		super();
		this.fieldName = fieldName;
		this.env = env;
		this.groups = groups;
	}

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Env getEnv() {
		return env;
	}
	public void setEnv(Env env) {
		this.env = env;
	}
	public Class<?>[] getGroups() {
		return groups;
	}
	public void setGroups(Class<?>[] groups) {
		this.groups = groups;
	}

	public SensitizationSupport getSupport() {
		return support;
	}

	public void setSupport(SensitizationSupport support) {
		this.support = support;
	}

}
