package org.sonicframework.utils.dto;

import java.util.Date;

import org.sonicframework.utils.excel.ExcelUtil;

import org.sonicframework.context.common.annotation.ClassFieldMapper;
import org.sonicframework.context.common.annotation.FieldMapper;
import org.sonicframework.context.dto.BaseDto;
import org.sonicframework.context.fillup.DictCodeBindType;
import org.sonicframework.context.fillup.annotation.DictFillupMapper;
import org.sonicframework.context.valid.annotation.DoubleValid;
import org.sonicframework.context.valid.annotation.StringType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ClassFieldMapper(local = "ext1", other = "EXT1", order = 1)
@ClassFieldMapper(local = "ext2", other = "EXT2", order = 100)
public class TestDto extends BaseDto {

	private static final long serialVersionUID = 3523538263638783679L;
	@StringType(nullable = false, max = 50, blankable = false, fieldLabel = "str", groups = {})
	@FieldMapper(field = "STR", dictName = "dict1", order = 5)
	@DictFillupMapper(dictName = "a", target = "str2", bindType = DictCodeBindType.VALUE)
	@DictFillupMapper(dictName = "a", target = "str3", bindType = DictCodeBindType.PARAM1)
	private String str;
	private String str2;
	private String str3;
	@DoubleValid(nullable = false, zeroable = false)
	@FieldMapper(field = "DOU", order = 2)
	private Double dou;
	@FieldMapper(field = "中文字", order = -1)
	private String chnVal;
	
	@FieldMapper(field = "DAT", order = 10, format = "yyyy-MM-dd")
	private Date dat;
	
	private String ext1;
	private String ext2;
	private String ext3;
	@FieldMapper(field = "splitStr", order = -1,dictName = "dict1", splitSep = ",", splitImpSep = ",", splitNoMatch2Null = true, groups = ExcelUtil.class)
	@DictFillupMapper(dictName = "a", target = "splitStr2", bindType = DictCodeBindType.VALUE, split = ",", outputSplit = "|")
	@DictFillupMapper(dictName = "a", target = "splitStr3", bindType = DictCodeBindType.PARAM1, split = ",")
	private String splitStr;
	private String splitStr2;
	private String splitStr3;
	@FieldMapper(field = "the_geom", groups = ExcelUtil.class)
	private String geoStr;
}
