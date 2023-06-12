package org.sonicframework.context.fillup;

import java.util.function.Function;

import org.sonicframework.context.dto.DictCodeDto;

/**
* @author lujunyi
*/
public enum DictCodeBindType {

	VALUE("value", t->t.getValue()), 
	ID("id", t->t.getId()), 
	PID("pid", t->t.getPcode()), 
	TYPE("type", t->t.getType()), 
	CODE("code", t->t.getCode()), 
	PARAM1("param1", t->t.getParam1()), 
	PARAM2("param2", t->t.getParam2()), 
	PARAM3("param3", t->t.getParam3()), 
	PARAM4("param4", t->t.getParam4()), 
	PARAM5("param5", t->t.getParam5()), 
	;
	private String bindType;
	private Function<DictCodeDto, String> bindTypeMapper;
	private DictCodeBindType(String bindType, Function<DictCodeDto, String> bindTypeMapper) {
		this.bindType = bindType;
		this.bindTypeMapper = bindTypeMapper;
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
}
