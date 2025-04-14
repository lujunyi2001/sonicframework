package org.sonicframework.utils.mapper;

/**
* @author lujunyi
*/
public class MapperColumnDesc {

	private Class<?> type;
	private int length;
	private int scales;
	private String alias;
	
	public MapperColumnDesc(Class<?> type) {
		super();
		this.type = type;
	}
	public MapperColumnDesc(Class<?> type, int length) {
		super();
		this.type = type;
		this.length = length;
	}

	public MapperColumnDesc(Class<?> type, int length, int scales, String alias) {
		super();
		this.type = type;
		this.length = length;
		this.scales = scales;
		this.alias = alias;
	}
	public Class<?> getType() {
		return type;
	}



	public void setType(Class<?> type) {
		this.type = type;
	}



	public int getLength() {
		return length;
	}



	public void setLength(int length) {
		this.length = length;
	}
	public int getScales() {
		return scales;
	}
	public void setScales(int scales) {
		this.scales = scales;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	

}
