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
	@FieldMapper(field = "中文字a", alias = "中文字2", order = -1)
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
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public Double getDou() {
		return dou;
	}
	public void setDou(Double dou) {
		this.dou = dou;
	}
	public Double getDou2() {
		return dou2;
	}
	public void setDou2(Double dou2) {
		this.dou2 = dou2;
	}
	public String getChnVal() {
		return chnVal;
	}
	public void setChnVal(String chnVal) {
		this.chnVal = chnVal;
	}
	public Date getDat() {
		return dat;
	}
	public void setDat(Date dat) {
		this.dat = dat;
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
	public String getSplitStr() {
		return splitStr;
	}
	public void setSplitStr(String splitStr) {
		this.splitStr = splitStr;
	}
	public Geometry getGeo() {
		return geo;
	}
	public void setGeo(Geometry geo) {
		this.geo = geo;
	}
	public String getGeoStr() {
		return geoStr;
	}
	public void setGeoStr(String geoStr) {
		this.geoStr = geoStr;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
