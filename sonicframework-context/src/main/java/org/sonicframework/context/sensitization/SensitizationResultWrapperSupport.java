package org.sonicframework.context.sensitization;

/**
* @author lujunyi
*/
public interface SensitizationResultWrapperSupport<T> {

	boolean isSupport(Object obj);
	
	T get(Object obj);
	Object set(Object obj, Object t);
}
