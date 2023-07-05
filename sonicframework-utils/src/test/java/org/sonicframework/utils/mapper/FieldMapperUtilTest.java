package org.sonicframework.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.utils.dto.TestDto;

/**
* @author lujunyi
*/
public class FieldMapperUtilTest {

	public FieldMapperUtilTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testImportMapper() {
		MapperContext<TestDto> context = MapperContext.newInstance(TestDto.class, ()->new TestDto(), type->getDictList(type));
		context = context.setValidEnable(true);
		Date date = new Date();
		Map<String, Object> data = new HashMap<>();
		data.put("STR", "名称3");
		data.put("DOU", 1D);
		data.put("DAT", date);
		data.put("EXT1", "ext1");
		data.put("EXT2", "ext2");
		TestDto dto = FieldMapperUtil.importMapper(data, context, (t, o)->t.setExt3("1"));
		assertEquals(dto.getStr(), "3");
		assertEquals(dto.getDou(), 1D);
		assertEquals(dto.getDat(), date);
		assertEquals(dto.getExt1(), "ext1");
		assertEquals(dto.getExt2(), "ext2");
		assertEquals(dto.getExt3(), "1");
		System.out.println(dto);
	}
	
	private List<DictCodeDto> getDictList(String type){
		List<DictCodeDto> list = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			DictCodeDto dto = new DictCodeDto();
			dto.setId(String.valueOf(i));
			dto.setCode(String.valueOf(i));
			dto.setValue("名称" + i);
			list.add(dto);
		}
		return list;
	}
	
	@Test
	public void getFromDescMap() {
		Map<String, List<MapperDescVo>> map = FieldMapperUtil.getFromDescMap(TestDto.class);
		for (Map.Entry<String, List<MapperDescVo>> entry : map.entrySet()) {
			System.out.println(entry.getKey());
		}
	}

}
