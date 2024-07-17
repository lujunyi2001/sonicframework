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
	private String ext1;//扩展字段1
	private String ext2;//扩展字段2
	private String ext3;//扩展字段3
	private String ext4;//扩展字段4
	private String ext5;//扩展字段5
	
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

	public String getExt1() {
		return ext1;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	public String getExt2() {
		return ext2;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	public String getExt3() {
		return ext3;
	}

	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}

	public String getExt4() {
		return ext4;
	}

	public void setExt4(String ext4) {
		this.ext4 = ext4;
	}

	public String getExt5() {
		return ext5;
	}

	public void setExt5(String ext5) {
		this.ext5 = ext5;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
