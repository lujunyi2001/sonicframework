package org.sonicframework.utils.geometry;

/**
* @author lujunyi
*/
@FunctionalInterface
public interface ThreeParamFunction<T, U, I, R> {

	R apply(T t, U u, I i);
	
}
