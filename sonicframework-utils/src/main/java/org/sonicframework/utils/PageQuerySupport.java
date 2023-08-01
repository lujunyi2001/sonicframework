package org.sonicframework.utils;

import java.util.List;

/**
 * 分页查询生产者
 * 
 * @author lujunyi
 */
public interface PageQuerySupport<T> {

	/**
	 * 根据页数获取数据
	 * 
	 * @param pageNum 页码，从1开始
	 * @return 当前页数据
	 */
	List<T> getPageContent(int pageNum);

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	int getPages();
}
