package org.sonicframework.context.dto;

import java.util.List;

import org.sonicframework.context.exception.DataNotValidException;

/**
 * 数据字典模型
 * @author lujunyi
 */
public class DictCodeDto extends BaseDto implements Cloneable {

	private static final long serialVersionUID = -951174762682382675L;
	/**
	 * 字典id
	 */
	private String id;
	/**
	 * 字典父级code
	 */
	private String pcode;
	/**
	 * 字典类型
	 */
	private String type;
	/**
	 * 字典代码
	 */
	private String code;
	/**
	 * 字典名称
	 */
	private String value;
	/**
	 * 字典扩展类型1
	 */
	private String param1;
	/**
	 * 字典扩展类型2
	 */
	private String param2;
	/**
	 * 字典扩展类型3
	 */
	private String param3;
	/**
	 * 字典扩展类型4
	 */
	private String param4;
	/**
	 * 字典扩展类型5
	 */
	private String param5;
	/**
	 * 字典序号
	 */
	private int sort;
	/**
	 * 字典是否已删除
	 */
	private boolean deleted;
	/**
	 * 字典描述
	 */
	private String desc;
	/**
	 * 子节点
	 */
	private List<DictCodeDto> children;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public DictCodeDto cloneObj() {
		try {
			return (DictCodeDto) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new DataNotValidException("clone DictCodeDto 失败");
		}
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public List<DictCodeDto> getChildren() {
		return children;
	}

	public void setChildren(List<DictCodeDto> children) {
		this.children = children;
	}
}