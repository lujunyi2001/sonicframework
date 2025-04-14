package org.sonicframework.utils.geometry;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.sonicframework.context.dto.BaseDto;

public class ShpRecordVo extends BaseDto{

	private static final long serialVersionUID = -2029548182854017830L;
	private String name;
	private Object value;
	private Class<?> type;
	private String alias;
	
	public ShpRecordVo(String name, Object value, Class<?> type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
	}
	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
	/**
	 * 获取shape属性的名称
	 * @retur nshape属性的名称
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取shape属性的值
	 * @retur nshape属性的值
	 */
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	/**
	 * 获取shape属性的类型
	 * @retur nshape属性的类型
	 */
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
