package org.sonicframework.utils.geometry.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonicframework.utils.ValidationUtil;
import org.sonicframework.utils.geometry.ShpInfoVo;
import org.sonicframework.utils.geometry.ShpRecordVo;
import org.sonicframework.utils.mapper.FieldMapperUtil;
import org.sonicframework.utils.mapper.MapperContext;
import org.sonicframework.utils.mapper.PostMapper;

/**
* @author lujunyi
*/
public class GeoFieldMapperUtil {

	private GeoFieldMapperUtil() {}
	
	public static <T>T importMapper(ShpInfoVo vo, MapperContext<T> context){
		return importMapper(vo, context, null);
	}
	
	public static <T>T importMapper(ShpInfoVo vo, MapperContext<T> context, PostMapper<T, ShpInfoVo> postMapper) {
		List<ShpRecordVo> list = vo.getRecordList();
		Map<String, Object> data = new HashMap<>();
		for (ShpRecordVo record : list) {
			data.put(record.getName(), record.getValue());
		}
		boolean validEnable = context.isValidEnable();
		Class<?>[] validGroups = context.getValidGroups();
		context = context.setValidEnable(false, validGroups);
		T entity = FieldMapperUtil.importMapper(data, context, null);
		if(postMapper != null) {
			postMapper.execute(entity, vo);
		}
		if(validEnable) {
			context = context.setValidEnable(validEnable, validGroups);
			ValidationUtil.checkValid(entity, validGroups);
		}
		return entity;
		
	}

}
