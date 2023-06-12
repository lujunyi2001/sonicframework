package org.sonicframework.utils.mapper;

/**
* @author lujunyi
*/
@FunctionalInterface
public interface PostMapper<T, O> {

	void execute(T t, O o);

}
