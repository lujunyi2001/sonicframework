package org.sonicframework.utils.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.sonicframework.context.common.annotation.SerializeSupport;
import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.context.exception.DevelopeCodeException;

/**
* @author lujunyi
*/
public class MapperContext<T> {

	protected Map<String, List<DictCodeDto>> dictCodeCache = new ConcurrentHashMap<>();
	protected Map<String, Map<String, DictCodeDto>> dictCodeCacheMap = new ConcurrentHashMap<>();
	protected Class<T> clazz;
	protected Supplier<T> dataSupplier;
	protected Function<String, List<DictCodeDto>> dictProvider;
	protected boolean validEnable;
	protected Class<?>[] validGroups = new Class[0];
	protected Map<MapperDescVo, SerializeSupport<? extends Object, ? extends Object>> serializeSupportMap = new ConcurrentHashMap<>();
	protected Class<?>[] groups = new Class<?>[0];
	protected Map<String, Class<?>> fieldClassMap = null;
	protected int titleIndex = 0;
	protected int titleEndIndex = -1;
	protected String mapperName;
	
	protected MapperContext() {}
	
	/**
	 * 生成字段映射上下文
	 * @param clazz 映射类名
	 * @param dataSupplier 类名对应对象生产者
	 * @return 字段映射上下文
	 */
	public static <T>MapperContext<T> newInstance(Class<T> clazz, Supplier<T> dataSupplier) {
		return newInstance(clazz, dataSupplier, null);
	}
	
	/** 
	 * 
	 * 生成字段映射上下文
	 * @param clazz 映射类名
	 * @param dataSupplier 类名对应对象生产者
	 * @param dictProvider 获取字典数据方法
	 * @return 字段映射上下文
	 */
	public static <T>MapperContext<T> newInstance(Class<T> clazz, Supplier<T> dataSupplier, Function<String, List<DictCodeDto>> dictProvider) {
		MapperContext<T> instance = new MapperContext<T>();
		instance.clazz = clazz;
		instance.dataSupplier = dataSupplier;
		instance.dictProvider = dictProvider;
		instance.init();
		return instance;
	}
	
	protected void init() {
		if(clazz == null && dataSupplier == null) {
			throw new DevelopeCodeException("ImportMapperContext has at least one of clazz and dataSupplier");
		}
		if(dataSupplier == null) {
			dataSupplier = ()->{
				try {
					return clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new DevelopeCodeException("can not new instance " + clazz, e);
				}
			};
		}
	}
	
	/**
	 * 设置是否验证
	 * @param validEnable 是否验证,true:是,false:否
	 * @param validGroups 验证分组
	 * @return 返回映射上下文
	 */
	public MapperContext<T> setValidEnable(boolean validEnable, Class<?>...validGroups){
		this.validEnable = validEnable;
		this.validGroups = validGroups;
		return this;
	}
	
	public List<DictCodeDto> getDictByType(String type) {
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
	public Map<String, DictCodeDto> getDictMapByType(String type) {
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

	public Class<T> getClazz() {
		return clazz;
	}

	public Supplier<T> getDataSupplier() {
		return dataSupplier;
	}

	void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public boolean isValidEnable() {
		return validEnable;
	}

	public Class<?>[] getValidGroups() {
		return validGroups;
	}

	SerializeSupport<? extends Object, ? extends Object> getSerializeSupport(MapperDescVo vo) {
		return serializeSupportMap.get(vo);
	}

	void addSerializeSupport(MapperDescVo vo, SerializeSupport<?, ?> support) {
		this.serializeSupportMap.put(vo, support);
	}
	public boolean hasSerializeSupport() {
		return !this.serializeSupportMap.isEmpty();
	}

	public Class<?>[] getGroups() {
		return groups;
	}

	/**
	 * 设置上下文映射分组
	 * @param groups 分组数据
	 */
	public void setGroups(Class<?>... groups) {
		this.groups = groups;
	}

	Map<String, Class<?>> getActualFieldClassMap() {
		return fieldClassMap;
	}
	public Map<String, Class<?>> getFieldClassMap() {
		return fieldClassMap == null?null:new LinkedHashMap<>(fieldClassMap);
	}

	void setFieldClassMap(Map<String, Class<?>> fieldClassMap) {
		this.fieldClassMap = fieldClassMap;
	}

	public int getTitleIndex() {
		return titleIndex;
	}

	public MapperContext<T> setTitleIndex(int titleIndex) {
		this.titleIndex = titleIndex;
		return this;
	}

	public String getMapperName() {
		return mapperName;
	}

	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}

	public int getTitleEndIndex() {
		return titleEndIndex;
	}

	public void setTitleEndIndex(int titleEndIndex) {
		this.titleEndIndex = titleEndIndex;
	}

}
