package org.sonicframework.utils.fillup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.utils.dto.TestDto;

/**
* @author lujunyi
*/
public class FillupUtilTest {

	public FillupUtilTest() {
	}
	
	@Test
	public void export() {
		FillupMapperContext<TestDto> context = FillupMapperContext.newInstance(TestDto.class, type->getDictList(type));
		for (int i = 0; i < 100; i++) {
			TestDto buildTestDto = buildTestDto(i);
			FillupUtil.fillup(buildTestDto, context);
			System.out.println(buildTestDto);
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
			dto.setParam1("param1_" + i);
			list.add(dto);
		}
		return list;
	}

}
