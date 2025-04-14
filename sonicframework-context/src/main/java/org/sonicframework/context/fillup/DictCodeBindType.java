package org.sonicframework.context.fillup;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.sonicframework.context.dto.DictCodeDto;

/**
* @author lujunyi
*/
public enum DictCodeBindType {

	VALUE("value", t->t.getValue(), (t, s)->t.setValue(s)), 
	ID("id", t->t.getId(), (t, s)->t.setId(s)), 
	PID("pid", t->t.getPcode(), (t, s)->t.setPcode(s)), 
	TYPE("type", t->t.getType(), (t, s)->t.setType(s)), 
	CODE("code", t->t.getCode(), (t, s)->t.setCode(s)), 
	PARAM1("param1", t->t.getParam1(), (t, s)->t.setParam1(s)), 
	PARAM2("param2", t->t.getParam2(), (t, s)->t.setParam2(s)), 
	PARAM3("param3", t->t.getParam3(), (t, s)->t.setParam3(s)), 
	PARAM4("param4", t->t.getParam4(), (t, s)->t.setParam4(s)), 
	PARAM5("param5", t->t.getParam5(), (t, s)->t.setParam5(s)), 
	;
	private String bindType;
	private Function<DictCodeDto, String> bindTypeMapper;
	private BiConsumer<DictCodeDto, String> buildMockConsumer;
	private DictCodeBindType(String bindType, Function<DictCodeDto, String> bindTypeMapper, BiConsumer<DictCodeDto, String> buildMockConsumer) {
		this.bindType = bindType;
		this.bindTypeMapper = bindTypeMapper;
		this.buildMockConsumer = buildMockConsumer;
	}
	public String getBindType() {
		return bindType;
	}
	
	public String getBindValue(DictCodeDto dto) {
		if(dto == null) {
			return null;
		}
		return this.bindTypeMapper.apply(dto);
	}
	public DictCodeDto mock(String str) {
		DictCodeDto dto = new DictCodeDto();
		this.buildMockConsumer.accept(dto, str);
		return dto;
	}
}
