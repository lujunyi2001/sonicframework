package org.sonicframework.utils.fillup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.context.exception.DevelopeCodeException;

/**
* @author lujunyi
*/
public class FillupMapperContext<T> {

	private Map<String, List<DictCodeDto>> dictCodeCache = new ConcurrentHashMap<>();
	private Map<String, Map<String, DictCodeDto>> dictCodeCacheMap = new ConcurrentHashMap<>();
	private Class<T> clazz;
	private Function<String, List<DictCodeDto>> dictProvider;
	private Class<?>[] groups = new Class<?>[0];
	
	private FillupMapperContext() {}
	
	/** 
	 * 
	 * 生成字段映射上下文
	 * @param clazz 映射类名
	 * @param dataSupplier 类名对应对象生产者
	 * @param dictProvider 获取字典数据方法
	 * @return 字段映射上下文
	 */
	public static <T>FillupMapperContext<T> newInstance(Class<T> clazz, Function<String, List<DictCodeDto>> dictProvider, Class<?>... groups) {
		FillupMapperContext<T> instance = new FillupMapperContext<T>();
		instance.clazz = clazz;
		instance.dictProvider = dictProvider;
		instance.groups = groups;
		instance.init();
		return instance;
	}
	
	private void init() {
		if(dictProvider == null) {
			throw new DevelopeCodeException("FillupMapperContext must has dictProvider");
		}
	}
	
	
	List<DictCodeDto> getDictByType(String type) {
		if(dictCodeCache.containsKey(type)) {
			return dictCodeCache.get(type);
		}
		if(dictProvider == null) {
			throw new DevelopeCodeException("ImportMapperContext not found dictProvider");
		}
		List<DictCodeDto> list = dictProvider.apply(type);
		dictCodeCache.put(type, list);
		return list;
	}
	Map<String, DictCodeDto> getDictMapByType(String type) {
		if(dictCodeCacheMap.containsKey(type)) {
			return dictCodeCacheMap.get(type);
		}
		List<DictCodeDto> list = getDictByType(type);
		Map<String, DictCodeDto> map = new HashMap<>();
		for (DictCodeDto dto : list) {
			map.put(dto.getCode(), dto);
			map.put(dto.getValue(), dto);
		}
		dictCodeCacheMap.put(type, map);
		return map;
	}

	Class<T> getClazz() {
		return clazz;
	}

	void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	Class<?>[] getGroups() {
		return groups;
	}

	/**
	 * 设置上下文映射分组
	 * @param groups 分组数据
	 */
	public void setGroups(Class<?>... groups) {
		this.groups = groups;
	}



}
