package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public class NullSensitization implements SensitizationSupport {

	public NullSensitization() {
	}

	@Override
	public String serializ(String val, Env env) {
		return null;
	}

}
