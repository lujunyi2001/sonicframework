package org.sonicframework.context.log.service;

import org.sonicframework.context.log.dto.SystemLogDto;

/**
* @author lujunyi
*/
public interface SystemLogService {

	/**
	 * 保存操作日志
	 * @param log 操作日志模型
	 */
	void saveLog(SystemLogDto log);
}
