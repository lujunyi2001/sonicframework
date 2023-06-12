package org.sonicframework.context.log.service;

import org.sonicframework.context.log.dto.LogUserDto;

/**
* @author lujunyi
*/
public interface LogUserService {

	/**
	 * 操作用户提供方法
	 * @return 当前用户
	 */
	LogUserDto getCurrentUser();
}
