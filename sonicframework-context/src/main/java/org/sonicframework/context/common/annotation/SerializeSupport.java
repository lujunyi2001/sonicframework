package org.sonicframework.context.common.annotation;

/**
* @author lujunyi
*/
public interface SerializeSupport<F, T> {
	
	T serialize(F f);
	
	F deserialize(T t);

}
