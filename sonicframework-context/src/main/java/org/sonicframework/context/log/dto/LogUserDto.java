package org.sonicframework.context.log.dto;

import org.sonicframework.context.dto.BaseDto;

/**
* @author lujunyi
*/
public class LogUserDto extends BaseDto {

	private static final long serialVersionUID = -3127850967270864210L;

	private String id;//用户id
	private String name;//用户姓名
	private String account;//用户账户名
	private String unitId;//用户部门id
	private String unitCode;//用户部门编码
	private String unitName;//用户部门名称
	private String region;//用户区域
	
	public LogUserDto() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

}
