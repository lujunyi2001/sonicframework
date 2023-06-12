package org.geotools.data.shapefile.dbf;

import java.util.Optional;

public class DbfCharsetContextHolder {

	private static ThreadLocal<String> context = new ThreadLocal<>();
	
	public static void set(String charset) {
		context.set(charset);
	}
	public static String get() {
		return context.get();
	}
	public static void remove() {
		context.remove();
	}
	public static String getIfPresent() {
		return Optional.ofNullable(context.get()).orElseGet(()->"UTF-8");
	}
}
