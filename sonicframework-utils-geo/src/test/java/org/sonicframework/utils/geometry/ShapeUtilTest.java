 package org.sonicframework.utils.geometry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.sonicframework.utils.geometry.mapper.GeoMapperContext;
import org.sonicframework.utils.mapper.FieldMapperUtil;
import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.utils.dto.TestDto;

/**
* @author lujunyi
*/
public class ShapeUtilTest {

	public ShapeUtilTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testExtractInfo() {
		String path = "E:\\test-data\\test.shp";
		ShapeUtil.extractInfo(path, t->{
			System.out.println(t);
		});
	}
	@Test
	public void testExtractInfoGeoJson() {
		String path = "E:\\test-data\\test.shp";
		ShapeUtil.extractInfoGeoJson(path, t->{
			System.out.println(t);
		});
	}
	@Test
	public void testExport() {
		Map<String, Class<?>> fieldNameMap = new HashMap<>();
		fieldNameMap.put("geo", Geometry.class);
		fieldNameMap.put("STR", String.class);
		fieldNameMap.put("DOU", Double.class);
		fieldNameMap.put("DAT", Date.class);
		fieldNameMap.put("EXT1", String.class);
		fieldNameMap.put("EXT2", String.class);
		ExportShp<Map<String, Object>> exportShp = ShapeUtil.buildNewExport("e:/test-data/export", "测试导出", fieldNameMap, "geo", "utf-8");
		Map<String, Object> data = new HashMap<>();
		data.put("geo", GeometryUtil.readGeometry("POINT(1 1)"));
		data.put("a", 1);
		data.put("b", "aaaa");
		data.put("STR", "名称3");
		data.put("DOU", 1D);
		data.put("DAT", new Date());
		data.put("EXT1", "ext1");
		data.put("EXT2", "ext2");
		exportShp.write(data);
		exportShp.close();
//		exportShp.zip();
//		exportShp.clean();
		System.out.println(exportShp.getDicPath());
		;
	}
	@Test
	public void testExport2() {
		GeoMapperContext<TestDto> context = GeoMapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context.setGroups(ShapeUtil.class);
		ExportShp<TestDto> exportShp = ShapeUtil.buildNewExport("e:/test-data/export", "测试导出", context, "the_geom", "GBK");
//		exportShp.setStringExceedLengthPolicy(StringExceedLengthPolicy.DEFAULT);
		exportShp.setExportErrorPolicy(ExportErrorPolicy.SKIP);
		exportShp.setExportErrorListener(t->{
			System.out.println(t.getData());
			System.out.println(t.getName());
			System.out.println(t.getValue());
			t.getException().printStackTrace();
		});
		TestDto dto = new TestDto();
		dto.setDat(new Date());
		dto.setDou(1D);
		dto.setExt1("ext12345");
		String str = "";
		for (int i = 0; i < 100; i++) {
			str += "一二三四五六七八九十12345";
		}
		System.out.println(str.getBytes().length);
		dto.setExt1(str);
		dto.setExt2("ext2222");
		dto.setGeoStr("POINT(1 1)");
		dto.setStr("代码33333");
		dto.setChnVal("测试中文字段");
		exportShp.write(dto);
		dto.setExt1("ext222345");
		exportShp.write(dto);
		exportShp.close();
//		exportShp.zip();
//		exportShp.clean();
		System.out.println(exportShp.getDicPath());
		;
	}
	@Test
	public void testExport3() {
		GeoMapperContext<TestDto> context = GeoMapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context.setGroups(ShapeUtil.class);
		ExportShp<TestDto> exportShp = ShapeUtil.buildNewExport("e:/test-data/export", "测试导出", context, "the_geom", "GBK");
//		exportShp.setStringExceedLengthPolicy(StringExceedLengthPolicy.DEFAULT);
		exportShp.setNoDataEmptyShp(true);
		exportShp.setExportErrorPolicy(ExportErrorPolicy.SKIP);
		exportShp.setExportErrorListener(t->{
			System.out.println(t.getData());
			System.out.println(t.getName());
			System.out.println(t.getValue());
			t.getException().printStackTrace();
		});
		exportShp.close();
//		exportShp.zip();
//		exportShp.clean();
		System.out.println(exportShp.getDicPath());
		;
	}
	
	@Test
	public void testExtractInfoEntity() {
		String path = "E:\\test-data\\面-模板 (1)\\面";
//		List<String> geoJsonList = ShapeUtil.extractInfoGeoJson(path);
//		for (String string : geoJsonList) {
//			System.out.println(string);
//		}
		GeoMapperContext<TestDto> context = GeoMapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context = (GeoMapperContext<TestDto>) context.setValidEnable(false);
		context.setGroups(ShapeUtil.class);
		List<TestDto> list = new ArrayList<>();
		ShapeUtil.extractInfoEntity(path, context, (t, r)->{
			System.out.println(r);
			System.out.println(t);
			list.add(t);
		}, (t, o)->{
//			t.setGeo(o.getGeo());
//			t.setGeoStr(o.getGeoStr());
			System.out.println(o.getSourceName() + "  " + o.getDataIndex());
			System.out.println(o.getGeoStr());
			System.out.println(o.getGeoStr().length());
		});
		for (TestDto dto : list) {
			Map<String, Object> dataMap = FieldMapperUtil.buildToFieldDataMap(dto, context);
			System.out.println(dataMap);
		}
		
	}
	@Test
	public void testExtractInfoEntity2() {
		String path = "E:\\test-data\\面-模板 (1)";
//		List<String> geoJsonList = ShapeUtil.extractInfoGeoJson(path);
//		for (String string : geoJsonList) {
//			System.out.println(string);
//		}
		GeoMapperContext<TestDto> context = GeoMapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context = (GeoMapperContext<TestDto>) context.setValidEnable(false);
		context.setGroups(ShapeUtil.class);
		List<TestDto> list = new ArrayList<>();
		ShapeUtil.extractInfoEntity(path, context, (t, r)->{
			System.out.println(r);
			System.out.println(t);
			list.add(t);
		}, (t, o)->{
			System.out.println(GeometryUtil.writeGeometry(o.getGeo()));
//			t.setGeo(o.getGeo());
//			t.setGeoStr(o.getGeoStr());
		});
		
	}
	
	private List<DictCodeDto> getDictList(String type){
		List<DictCodeDto> list = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			DictCodeDto dto = new DictCodeDto();
			dto.setId(String.valueOf(i));
			dto.setCode("代码" + i);
			dto.setValue("名称" + i);
			list.add(dto);
		}
		return list;
	}
}
