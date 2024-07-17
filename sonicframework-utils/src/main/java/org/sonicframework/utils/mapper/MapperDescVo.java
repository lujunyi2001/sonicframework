package org.sonicframework.utils.mapper;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.sonicframework.context.common.annotation.Match;
import org.sonicframework.context.common.annotation.SerializeSupport;
import org.sonicframework.context.common.annotation.Style;

/**
* @author lujunyi
*/
public class MapperDescVo implements Serializable{

	private static final long serialVersionUID = -2029548182854017830L;
	private static final String SEP = ";";
	
	
	private String localName;
	private String otherName;
	private String label;
	private String dictName;
	private Class<?> targetClass;
	private String[] titleGroups;
	private Match[] match;
	private boolean matchContains;
	private String splitSep;
	private String splitImpSep;
	private String splitExpSep;
	private boolean splitNoMatch2Null;
	private Class<?> localClass;
	private int order;
	private int dictType;
	private String[] arraySep;
	private boolean price;
	private String format;
	private double numberRate = 1;
	private int action;
	private Class<? extends SerializeSupport<?, ?>> serializeSupportClazz;
	private Class<?>[] groups = new Class<?>[0];
	private int length;
	private Style[] titleStyles;
	private Style contentStyle;
	
	public MapperDescVo(String localName, String otherName, String label) {
		super();
		this.localName = localName;
		this.otherName = otherName;
		this.label = label;
	}
	public MapperDescVo(String localName, String otherName, String label, Class<?> localClass) {
		super();
		this.localName = localName;
		this.otherName = otherName;
		this.label = label;
		this.localClass = localClass;
	}
	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
	
	@Override
	public int hashCode() {
		return buildUniqueStr().hashCode();
	}
	
	@Override
	public boolean equals(Object that) {
		if(this == that) {
			return true;
		}
		if(!(that instanceof MapperDescVo)) {
			return false;
		}
		return Objects.equals(buildUniqueStr(), ((MapperDescVo)that).buildUniqueStr());
	}
	
	private String buildUniqueStr() {
		return new StringBuilder("MapperDescVo:").append(localName).append(SEP).append(otherName)
			.append(SEP).append(label).append(SEP).append(StringUtils.join(groups, ",")).toString();
	}
	
	public String getLocalName() {
		return localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public String getOtherName() {
		return otherName;
	}
	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
	}
	public Class<?> getLocalClass() {
		return localClass;
	}
	public void setLocalClass(Class<?> localClass) {
		this.localClass = localClass;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getDictType() {
		return dictType;
	}
	public void setDictType(int dictType) {
		this.dictType = dictType;
	}
	public String[] getArraySep() {
		return arraySep;
	}
	public void setArraySep(String[] arraySep) {
		this.arraySep = arraySep;
	}
	public boolean isPrice() {
		return price;
	}
	public void setPrice(boolean price) {
		this.price = price;
	}
	public Match[] getMatch() {
		return match;
	}
	public void setMatch(Match[] match) {
		this.match = match;
	}
	public boolean isMatchContains() {
		return matchContains;
	}
	public void setMatchContains(boolean matchContains) {
		this.matchContains = matchContains;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public double getNumberRate() {
		return numberRate;
	}
	public void setNumberRate(double numberRate) {
		this.numberRate = numberRate;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public Class<? extends SerializeSupport<?, ?>> getSerializeSupportClazz() {
		return serializeSupportClazz;
	}
	public void setSerializeSupportClazz(Class<? extends SerializeSupport<?, ?>> serializeSupportClazz) {
		this.serializeSupportClazz = serializeSupportClazz;
	}
	public Class<?>[] getGroups() {
		return groups;
	}
	public void setGroups(Class<?>[] groups) {
		this.groups = groups;
	}
	public String getSplitSep() {
		return splitSep;
	}
	public void setSplitSep(String splitSep) {
		this.splitSep = splitSep;
	}
	public String getSplitExpSep() {
		return splitExpSep;
	}
	public void setSplitExpSep(String splitExpSep) {
		this.splitExpSep = splitExpSep;
	}
	public String getSplitImpSep() {
		return splitImpSep;
	}
	public void setSplitImpSep(String splitImpSep) {
		this.splitImpSep = splitImpSep;
	}
	public boolean isSplitNoMatch2Null() {
		return splitNoMatch2Null;
	}
	public void setSplitNoMatch2Null(boolean splitNoMatch2Null) {
		this.splitNoMatch2Null = splitNoMatch2Null;
	}
	public Class<?> getTargetClass() {
		return targetClass;
	}
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String[] getTitleGroups() {
		return titleGroups;
	}
	public void setTitleGroups(String[] titleGroups) {
		this.titleGroups = titleGroups;
	}
	public Style[] getTitleStyles() {
		return titleStyles;
	}
	public void setTitleStyles(Style[] titleStyles) {
		this.titleStyles = titleStyles;
	}
	public Style getContentStyle() {
		return contentStyle;
	}
	public void setContentStyle(Style contentStyle) {
		this.contentStyle = contentStyle;
	}
}
