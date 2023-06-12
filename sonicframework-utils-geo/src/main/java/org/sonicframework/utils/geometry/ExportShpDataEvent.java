package org.sonicframework.utils.geometry;

import org.sonicframework.context.dto.BaseDto;

public class ExportShpDataEvent<T> extends BaseDto{

	private static final long serialVersionUID = -2029548182854017830L;
	private T data;
	private String name;
	private Object value;
	private Exception exception;
	
	public ExportShpDataEvent(T data, String name, Object value, Exception exception) {
		super();
		this.data = data;
		this.name = name;
		this.value = value;
		this.exception = exception;
	}

	public T getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public Exception getException() {
		return exception;
	}
}
