package org.sonicframework.utils;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

/**
* @author lujunyi
*/
public class ClassUtilTest {

	public ClassUtilTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void scanPackage() throws ClassNotFoundException, IOException {
		Set<Class<?>> set = ClassUtil.scanPackage("org.geotools");
		for (Class<?> class1 : set) {
			System.out.println(class1);
		}
	}

}
