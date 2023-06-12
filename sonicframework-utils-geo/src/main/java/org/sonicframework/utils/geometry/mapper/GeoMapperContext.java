package org.sonicframework.utils.geometry.mapper;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.utils.mapper.MapperContext;

/**
* @author lujunyi
*/
public class GeoMapperContext<T> extends MapperContext<T> {

	private CoordinateReferenceSystem crs;
	
	protected GeoMapperContext() {}
	
	/**
	 * 生成字段映射上下文
	 * @param clazz 映射类名
	 * @param dataSupplier 类名对应对象生产者
	 * @return 字段映射上下文
	 */
	public static <T>GeoMapperContext<T> newInstance(Class<T> clazz, Supplier<T> dataSupplier) {
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
	public static <T>GeoMapperContext<T> newInstance(Class<T> clazz, Supplier<T> dataSupplier, Function<String, List<DictCodeDto>> dictProvider) {
		GeoMapperContext<T> instance = new GeoMapperContext<T>();
		instance.clazz = clazz;
		instance.dataSupplier = dataSupplier;
		instance.dictProvider = dictProvider;
		instance.init();
		return instance;
	}

	public CoordinateReferenceSystem getCrs() {
		return crs;
	}

	public void setCrs(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}

}
