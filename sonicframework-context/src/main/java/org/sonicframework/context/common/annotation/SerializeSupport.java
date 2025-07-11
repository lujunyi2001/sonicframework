package org.sonicframework.context.common.annotation;

/**
 * 序列化策略
 * @author lujunyi
 */
public interface SerializeSupport<F, T> {

	/**
	 * 导出时序列化
	 * 
	 * @param 序列化前的值
	 * @return 序列化后的值
	 */
	T serialize(F f);

	/**
	 * 导入时序列化
	 * 
	 * @param 序列化前的值
	 * @return 序列化后的值
	 */
	F deserialize(T t);

}
