package org.sonicframework.utils.mapper;

/**
* @author lujunyi
*/
public class DictMapContext<T> {

	private T target;
	private String label;
	private String dictName;
	private String fieldName;
	private String mapValue;
	private String defaultValue;
	DictMapContext(T target, String label, String dictName, String fieldName, String mapValue) {
		super();
		this.target = target;
		this.label = label;
		this.dictName = dictName;
		this.fieldName = fieldName;
		this.mapValue = mapValue;
	}
	public T getTarget() {
		return target;
	}
	public String getLabel() {
		return label;
	}
	public String getDictName() {
		return dictName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getMapValue() {
		return mapValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	

}
