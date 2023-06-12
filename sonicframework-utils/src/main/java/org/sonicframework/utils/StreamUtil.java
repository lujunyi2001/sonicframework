package org.sonicframework.utils;

public class StreamUtil {

	private StreamUtil() {}
	
	public static void close(AutoCloseable stream) {
		if(stream != null) {
			try {
				stream.close();
			} catch (Exception e) {
			}
		}
	}
}
