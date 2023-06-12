package org.sonicframework.utils;

/**
* @author lujunyi
*/
@FunctionalInterface
public interface ConsumerImpEntity<T, R> {

	void execute(T t, R r);

}
