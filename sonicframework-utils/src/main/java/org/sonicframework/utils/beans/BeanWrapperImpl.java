package org.sonicframework.utils.beans;

import org.springframework.beans.BeansException;

/**
 * @author lujunyi
 */
public class BeanWrapperImpl extends org.springframework.beans.BeanWrapperImpl {

	private static final String SEP = ".";
	
	public BeanWrapperImpl() {
		super();
	}

	public BeanWrapperImpl(boolean registerDefaultEditors) {
		super(registerDefaultEditors);
	}

	public BeanWrapperImpl(Object object) {
		super(object);
	}

	public BeanWrapperImpl(Class<?> clazz) {
		super(clazz);
	}

	public BeanWrapperImpl(Object object, String nestedPath, Object rootObject) {
		super(object, nestedPath, rootObject);
	}

	@SuppressWarnings("unused")
	private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl parent) {
		super(object, nestedPath, parent);
	}

	
	
	@Override
	public Object getPropertyValue(String propertyName) throws BeansException {
		String key = "";
		String[] split = propertyName.split("\\.");
		Object value = null;
		for (int i = 0; i < split.length; i++) {
			if(i > 0) {
				key += SEP;
			}
			key += split[i];
			value = super.getPropertyValue(key);
			if(value == null) {
				break;
			}
		}
		return value;
	}
}
