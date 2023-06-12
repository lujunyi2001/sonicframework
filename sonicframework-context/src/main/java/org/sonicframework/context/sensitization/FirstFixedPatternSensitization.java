package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public class FirstFixedPatternSensitization implements SensitizationSupport {

	public FirstFixedPatternSensitization() {
	}

	@Override
	public String serializ(String val, Env env) {
		if(val == null) {
			return null;
		}
		String prefix = "";
		int index = val.indexOf(env.getPattern());
		if(index > -1) {
			prefix = val.substring(0, index + 1);
			val = val.substring(index + 1);
		}
		return prefix + NormalSensitization.getStarString(val, env.getStart(), 0, env);
	}

	
}
