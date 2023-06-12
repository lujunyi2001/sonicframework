package org.sonicframework.core.log;

import java.util.Optional;

public class ServiceLogContextHolder {

	private static ThreadLocal<ServiceLogHelper> context = new ThreadLocal<>();
	
	public static void set(ServiceLogHelper vo) {
		context.set(vo);
	}
	public static ServiceLogHelper get() {
		return context.get();
	}
	public static void remove() {
		context.remove();
	}
	public static ServiceLogHelper getIfPresent() {
		return Optional.ofNullable(context.get()).orElseGet(()->new ServiceLogHelper());
	}
}
