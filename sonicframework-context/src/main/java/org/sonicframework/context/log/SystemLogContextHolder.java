package org.sonicframework.context.log;

import java.util.Optional;

import org.sonicframework.context.log.dto.SystemLogDto;

public class SystemLogContextHolder {

	private static ThreadLocal<SystemLogDto> context = new ThreadLocal<>();
	
	public static void set(SystemLogDto vo) {
		context.set(vo);
	}
	public static SystemLogDto get() {
		return context.get();
	}
	public static void remove() {
		context.remove();
	}
	public static SystemLogDto getIfPresent() {
		return Optional.ofNullable(context.get()).orElseGet(()->new SystemLogDto());
	}
}
