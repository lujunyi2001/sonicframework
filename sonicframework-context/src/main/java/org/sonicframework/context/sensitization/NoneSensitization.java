package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public class NoneSensitization implements SensitizationSupport {

	public NoneSensitization() {
	}

	@Override
	public String serializ(String val, Env env) {
		return val;
	}

}
