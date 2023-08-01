package org.sonicframework.utils.dto;

import java.util.Date;

import org.locationtech.jts.geom.Geometry;

import org.sonicframework.context.common.annotation.ClassFieldMapper;
import org.sonicframework.context.common.annotation.FieldMapper;
import org.sonicframework.context.common.constaints.FieldMapperConst;
import org.sonicframework.context.dto.BaseDto;
import org.sonicframework.context.valid.annotation.DoubleValid;
import org.sonicframework.context.valid.annotation.StringType;
import org.sonicframework.utils.excel.ExcelUtil;
import org.sonicframework.utils.geometry.ShapeUtil;
import org.sonicframework.utils.geometry.mapper.serialize.StrGeoSerializeSupport;

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
	private String str;
	@DoubleValid(nullable = false, zeroable = false)
	@FieldMapper(field = "DOU", order = 2)
	private Double dou;
	@FieldMapper(field = "SHAPE_Length", order = 2)
	private Double dou2;
	@FieldMapper(field = "中文字", order = -1)
	private String chnVal;
	
	@FieldMapper(field = "DAT", order = 10, format = "yyyy-MM-dd")
	private Date dat;
	
	private String ext1;
	private String ext2;
	private String ext3;
	@FieldMapper(field = "splitStr", order = -1,dictName = "dict1", splitSep = ",", splitImpSep = ",", splitNoMatch2Null = true, groups = ExcelUtil.class)
	private String splitStr;
	@FieldMapper(field = "the_geom", action = FieldMapperConst.MAPPER_IMPORT, groups = ShapeUtil.class)
	private Geometry geo;
	@FieldMapper(field = "the_geom", serialize = StrGeoSerializeSupport.class, groups = ShapeUtil.class)
	@FieldMapper(field = "the_geom")
	private String geoStr;
}
