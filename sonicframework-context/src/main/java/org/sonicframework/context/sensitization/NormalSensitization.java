package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public class NormalSensitization implements SensitizationSupport {

	public NormalSensitization() {
	}

	@Override
	public String serializ(String val, Env env) {
		if(val == null) {
			return null;
		}
		if(env.getEnd() != 0 && env.getStart() == env.getEnd()) {
			return val;
		}
		return getStarString(val, env.getStart(), env.getEnd(), env);
	}
	
	static String getStarString(String content, int begin, int end, Env env) {
		 
        if (begin >= content.length() || begin < 0) {
            return content;
        }
        if(end < 0 && end + content.length() > 0) {
        	end = end + content.length();
        }else if(end == 0) {
        	end = content.length();
        }
        if(end >= content.length()) {
        	end = content.length();
        }
        if (end < 0) {
            return content;
        }
        if (begin >= end) {
            return content;
        }
        String starStr = "";
        String maskStr = env.getMask();
        boolean hasMask = false;
        boolean repeat = env.isMaskRepeat();
        for (int i = begin; i < end; i++) {
        	if(repeat || !hasMask) {
        		starStr = starStr + maskStr;
        		hasMask = true;
        	}
            
        }
        return content.substring(0, begin) + starStr + content.substring(end, content.length());
    }

}
