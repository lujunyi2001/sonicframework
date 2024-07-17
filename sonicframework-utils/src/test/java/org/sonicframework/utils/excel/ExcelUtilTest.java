package org.sonicframework.utils.excel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.sonicframework.utils.PageQuerySupport;
import org.sonicframework.utils.mapper.MapperContext;

import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.utils.dto.TestDto;
import org.sonicframework.utils.dto.TestDto2;

/**
* @author lujunyi
*/
public class ExcelUtilTest {

	public ExcelUtilTest() {
	}
	
	@Test
	public void export() {
		MapperContext<TestDto> context = MapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context.setGroups(ExcelUtil.class);
		final List<TestDto> list = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			list.add(buildTestDto(i));
		}
		PageQuerySupport<TestDto> pageSupport = new PageQuerySupport<TestDto>() {
			
			@Override
			public int getPages() {
				return 1;
			}
			
			@Override
			public List<TestDto> getPageContent(int pageNum) {
				return list;
			}
		};
		Sheet sheet = ExcelUtil.export(context, pageSupport);
		try(OutputStream out = new FileOutputStream("E:/test-data/export/testexp.xlsx")) {
			sheet.getWorkbook().write(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testImport() {
		MapperContext<TestDto> context = MapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context.setGroups(ExcelUtil.class);
		context.setValidEnable(true);
		try(InputStream input = new FileInputStream("E:/test-data/export/testexp.xlsx")) {
			Workbook workbook = ExcelUtil.openExcel(input, "testexp.xlsx");
			Sheet sheet = workbook.getSheetAt(0);
			ExcelUtil.importForEntity(sheet, context, (t, r)->{
				System.out.println(r);
				System.out.println(t);
			}, (t, o)->{
				System.out.println(o.getSheetName() + "  " + o.getSheetIndex() + "  " + o.getRowIndex() + "  " + o.getDataMap());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	@Test
	public void testImport2() {
		MapperContext<TestDto2> context = MapperContext.newInstance(TestDto2.class, ()->new TestDto2(), type->getDictList(type));
		context.setValidEnable(true);
		context.setTitleIndex(1);
		context.setTitleEndIndex(7);
		try(InputStream input = new FileInputStream("E:/1.xlsx")) {
			Workbook workbook = ExcelUtil.openExcel(input, "1.xlsx");
			Sheet sheet = workbook.getSheetAt(0);
			ExcelUtil.importForEntity(sheet, context, (t, r)->{
				System.out.println(r);
				System.out.println(t);
			}, (t, o)->{
				System.out.println(o.getSheetName() + "  " + o.getSheetIndex() + "  " + o.getRowIndex() + "  " + o.getDataMap());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Test
	public void export2() {
		MapperContext<TestDto2> context = MapperContext.newInstance(TestDto2.class, ()->new TestDto2(), type->getDictList(type));
		context.setTitleIndex(1);
		final List<TestDto2> list = new ArrayList<>();
		TestDto2 dto = new TestDto2();
		dto.setStr("测试0");
		dto.setStr2("测试1");
		dto.setStr3("测试2");
		dto.setStr4("测试3");
		dto.setStr5("测试4");
		list.add(dto);
		PageQuerySupport<TestDto2> pageSupport = new PageQuerySupport<TestDto2>() {
			
			@Override
			public int getPages() {
				return 1;
			}
			
			@Override
			public List<TestDto2> getPageContent(int pageNum) {
				return list;
			}
		};
		ExcelUtil.setExportAutoSizeColumn(true);
		Sheet sheet = ExcelUtil.export(context, pageSupport);
		try(OutputStream out = new FileOutputStream("E:/2.xlsx")) {
			sheet.getWorkbook().write(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void importForMap() {
		try(InputStream input = new FileInputStream("E:/test-data/export/testexp.xlsx")) {
			Workbook workbook = ExcelUtil.openExcel(input, "testexp.xlsx");
			Sheet sheet = workbook.getSheetAt(0);
			List<Map<String, Object>> list = ExcelUtil.importForMap(sheet);
			for (Map<String, Object> map : list) {
				System.out.println(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private TestDto buildTestDto(int num) {
		TestDto dto = new TestDto();
		dto.setDat(new Date());
		dto.setDou(new Double(num));
		dto.setExt1("ext1" + num);
		dto.setExt2("ext2" + num);
		dto.setGeoStr("POINT(1 1)");
		dto.setStr("代码" + (num % 6));
		dto.setSplitStr("代码2,代码3,代码4");
		return dto;
	}
	
	private List<DictCodeDto> getDictList(String type){
		List<DictCodeDto> list = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			DictCodeDto dto = new DictCodeDto();
			dto.setId(String.valueOf(i));
			dto.setCode("代码" + i);
			dto.setValue("名称" + i);
			list.add(dto);
		}
		return list;
	}

}
