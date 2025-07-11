package org.sonicframework.utils.gdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.context.exception.DevelopeCodeException;
import org.sonicframework.utils.dto.TestDto;
import org.sonicframework.utils.geometry.ExportErrorPolicy;
import org.sonicframework.utils.geometry.ShapeUtil;
import org.sonicframework.utils.geometry.mapper.GeoMapperContext;
import org.sonicframework.utils.mapper.FieldMapperUtil;
import org.sonicframework.utils.mapper.MapperContext;

/**
* @author lujunyi
*/
public class GdbUtilTest {

	public static final CoordinateReferenceSystem CGCS2000;
	static {
		String wktCgcs2000 = "GEOGCS[\"GCS_China_Geodetic_Coordinate_System_2000\",DATUM[\"D_China_2000\",SPHEROID[\"CGCS2000\",6378137.0,298.257222101]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]";
		try {
			CGCS2000 = CRS.parseWKT(wktCgcs2000);
		} catch (FactoryException e) {
			throw new DevelopeCodeException("不合法的CGCS2000代码定义");
		}
	}
	
	public GdbUtilTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testExtractInfo() {
		String path = "E:\\test-data\\export\\2801b55a-e491-4870-ac45-c4906419f9aa.gdb";
		
//		MapperContext<TestDto> context = MapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		GeoMapperContext<TestDto> context = GeoMapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context.setValidEnable(false);
		context.setGroups(ShapeUtil.class);
//		context.setMapperName("test2");
		context.setCrs(CGCS2000);
		context.setParseGeoDataErrHandler(t->{
			System.out.println(t.getInfo());
			System.out.println(t.getGeoStr());
			t.getCause().printStackTrace();
		});
		List<TestDto> list = new ArrayList<>();
		GdbUtil.extractInfoEntity(path, context, (t, r)->{
			System.out.println(r);
			System.out.println(t);
			list.add(t);
		}, (t, o)->{
			t.setGeo(o.getGeo());
			t.setGeoStr(o.getGeoStr());
			System.out.println(o.getSourceName() + "  " + o.getDataIndex());
		});
		for (TestDto dto : list) {
			Map<String, Object> dataMap = FieldMapperUtil.buildToFieldDataMap(dto, context);
			System.out.println(dataMap);
		}
	}
	
	@Test
	public void testExtractInfoGeoJson() {
		String path = "E:\\test-data\\zcqc\\hyyqq\\海域权属数据.shp";
		ShapeUtil.extractInfoGeoJson(path, t->{
			System.out.println(t);
		});
	}
	
	@Test
	public void testExport1() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			pool.execute(()->{
				testExport();
				try {
					Thread.sleep(10000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		pool.shutdown();
		pool.awaitTermination(10, TimeUnit.MINUTES);
	}
	@Test
	public void testExport() {
		MapperContext<TestDto> context = MapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context.setGroups(ShapeUtil.class);
		ExportGdb<TestDto> top = GdbUtil.buildNewExport("e:/test-data/export/" + UUID.randomUUID() + ".gdb", 4326, "GBK");
		ExportGdb<TestDto> exportGdb = top.createNewLayer("test1", context, "the_geom");
		ExportGdb<TestDto> exportGdb2 = top.createNewLayer("test2", context, "the_geom");
		//		exportGdb.setStringExceedLengthPolicy(StringExceedLengthPolicy.DEFAULT);
		exportGdb.setExportErrorPolicy(ExportErrorPolicy.SKIP);
		exportGdb.setExportErrorListener(t->{
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
		exportGdb.write(dto);
		dto.setExt1("ext222345");
		exportGdb.write(dto);
		exportGdb2.write(dto);
		dto.setExt1("ext2223aaaa");
		exportGdb2.write(dto);
		exportGdb.close();
//		exportShp.zip();
//		exportShp.clean();
//		System.out.println(exportGdb.getDicPath());
		;
		exportGdb.zip();
		System.out.println();
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
