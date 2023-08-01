package org.sonicframework.context.common.annotation;

/**
 * 序列化策略
 * @author lujunyi
 */
public interface SerializeSupport<F, T> {

	/**
	 * 导出时序列化
	 * 
	 * @param f
	 * @return
	 */
	T serialize(F f);

	/**
	 * 导入时序列化
	 * 
	 * @param t
	 * @return
	 */
	F deserialize(T t);

}
