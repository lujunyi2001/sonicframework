package org.sonicframework.utils.fillup;

import org.sonicframework.context.fillup.DictCodeBindType;
import org.sonicframework.context.fillup.FillupNotMatch;

/**
* @author lujunyi
*/
public class FillupMapperDescVo {

	private String fieldName;
	private String dictName;
	private String target;
	private String split;
	private String outputSplit;
	private DictCodeBindType bindType;
	private FillupNotMatch fillupNotMatch;
	private String defaultVal;
	private String label;
	private Class<?>[] groups;
	
	public FillupMapperDescVo() {}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public DictCodeBindType getBindType() {
		return bindType;
	}

	public void setBindType(DictCodeBindType bindType) {
		this.bindType = bindType;
	}

	public Class<?>[] getGroups() {
		return groups;
	}

	public void setGroups(Class<?>[] groups) {
		this.groups = groups;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public String getOutputSplit() {
		return outputSplit;
	}

	public void setOutputSplit(String outputSplit) {
		this.outputSplit = outputSplit;
	}

	public FillupNotMatch getFillupNotMatch() {
		return fillupNotMatch;
	}

	public void setFillupNotMatch(FillupNotMatch fillupNotMatch) {
		this.fillupNotMatch = fillupNotMatch;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
