package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public interface SensitizationSupport {

	/**
	 * 数据脱敏处理方法
	 * @param val 脱敏前的数据
	 * @param env 脱敏参数
	 * @return 脱敏后的数据
	 */
	String serializ(String val, Env env);
}
