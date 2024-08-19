package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public class LastFixedPatternSensitization implements SensitizationSupport {

	public LastFixedPatternSensitization() {
	}

	@Override
	public String serializ(String val, Env env) {
		if(val == null) {
			return null;
		}
		String suffix = "";
		int index = val.lastIndexOf(env.getPattern());
		if(index > -1) {
			suffix = val.substring(index);
			val = val.substring(0, index);
		}
		return NormalSensitization.getStarString(val, env.getStart(), env.getEnd(), env) + suffix;
	}

	
}
