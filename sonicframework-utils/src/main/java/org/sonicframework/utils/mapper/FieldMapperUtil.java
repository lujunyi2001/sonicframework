package org.sonicframework.utils.mapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.utils.ClassUtil;
import org.sonicframework.utils.ConvertFactory;
import org.sonicframework.utils.ValidationUtil;
import org.sonicframework.utils.beans.BeanWrapperImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.core.annotation.AnnotationUtils;

import org.sonicframework.context.common.annotation.ClassFieldMapper;
import org.sonicframework.context.common.annotation.ClassFieldMappers;
import org.sonicframework.context.common.annotation.FieldMapper;
import org.sonicframework.context.common.annotation.FieldMappers;
import org.sonicframework.context.common.annotation.Label;
import org.sonicframework.context.common.annotation.Match;
import org.sonicframework.context.common.annotation.SerializeSupport;
import org.sonicframework.context.common.constaints.FieldMapperConst;
import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.context.exception.DevelopeCodeException;


/**
* @author lujunyi
*/
public class FieldMapperUtil {

	private static Map<Class<?>, List<MapperDescVo>> cacheMapperDescVo = new ConcurrentHashMap<>();
	private static Logger log = LoggerFactory.getLogger(FieldMapperUtil.class);
	
	public final static String TITLE_GROUPS_SEP = "$$";
	
	private FieldMapperUtil() {}
	
	public static List<MapperDescVo> parseDesc(Class<?> clazz){
		if(cacheMapperDescVo.containsKey(clazz)) {
//			if(log.isTraceEnabled()) {
//				log.trace("parseDesc found from cache in class:[{}]", clazz);
//			}
			return cacheMapperDescVo.get(clazz);
		}
		List<MapperDescVo> result = new ArrayList<>();
		BeanWrapperImpl bean = new BeanWrapperImpl(clazz);
		MapperDescVo vo = null;
		String local = null;
		String other = null;
		String label = null;
		String dictName = null;
		Map<String, PropertyDescriptor> classDesc = ClassUtil.getClassDesc(clazz);
		Map<String, Label> labelMap = ClassUtil.findAnnotationByClassFields(clazz, Label.class);
		List<FieldMapper> fm = null;
		Map<String, List<FieldMapper>> fieldMapperMap = findAnnotationByFieldMapper(clazz);
		for (Map.Entry<String, List<FieldMapper>> entry : fieldMapperMap.entrySet()) {
			fm = entry.getValue();
			for (FieldMapper fieldMapper: fm) {
				local = entry.getKey();
				if(!classDesc.containsKey(local)) {
					continue;
				}
				other = fieldMapper.field();
				label = StringUtils.isBlank(fieldMapper.label())?null:fieldMapper.label();
				if(label == null && labelMap.containsKey(local)) {
					label = labelMap.get(local).value();
					if(StringUtils.isBlank(label)) {
						label = null;
					}
				}
				dictName = StringUtils.isBlank(fieldMapper.dictName())?null:fieldMapper.dictName().trim();
				if(StringUtils.isBlank(other)) {
					throw new DevelopeCodeException("没有配置" + local + "的映射字段");
				}
				vo = new MapperDescVo(local, other, label, classDesc.get(local).getPropertyType());
				vo.setDictName(dictName);
				vo.setFormat(fieldMapper.format());
				if(fieldMapper.targetClass().length > 0) {
					vo.setTargetClass(fieldMapper.targetClass()[0]);
				}
				vo.setTitleGroups(fieldMapper.titleGroups());
				vo.setMatch(fieldMapper.match());
				vo.setMatchContains(fieldMapper.matchContains());
				vo.setSplitSep(fieldMapper.splitSep());
				vo.setSplitImpSep(fieldMapper.splitImpSep());
				vo.setSplitExpSep(fieldMapper.splitExpSep());
				vo.setSplitNoMatch2Null(fieldMapper.splitNoMatch2Null());
				vo.setOrder(fieldMapper.order());
				vo.setAction(fieldMapper.action());
				if(fieldMapper.serialize().length > 0) {
					vo.setSerializeSupportClazz(fieldMapper.serialize()[0]);
				}
				vo.setGroups(fieldMapper.groups());
				vo.setLength(fieldMapper.length());
				vo.setTitleStyles(fieldMapper.titleStyle());
				vo.setContentStyle(ArrayUtils.isEmpty(fieldMapper.contentStyle())?null:fieldMapper.contentStyle()[0]);
				result.add(vo);
			}
			
			
		}
		List<ClassFieldMapper> classFieldMapperList = findAnnotationByClassFieldMapper(clazz);
		for (ClassFieldMapper mapper : classFieldMapperList) {
			local = mapper.local();
			other = mapper.other();
			label = StringUtils.isBlank(mapper.label())?null:mapper.label();
			dictName = StringUtils.isBlank(mapper.dictName())?null:mapper.dictName().trim();
			if(StringUtils.isBlank(local)) {
				throw new DevelopeCodeException("没有配置ClassFieldMapper的local映射字段");
			}
			if(StringUtils.isBlank(other)) {
				throw new DevelopeCodeException("没有配置ClassFieldMapper的other映射字段");
			}
			vo = new MapperDescVo(local, other, label, bean.getPropertyType(local));
			vo.setDictName(dictName);
			vo.setFormat(mapper.format());
			if(mapper.targetClass().length > 0) {
				vo.setTargetClass(mapper.targetClass()[0]);
			}
			vo.setTitleGroups(mapper.titleGroups());
			vo.setMatch(mapper.match());
			vo.setMatchContains(mapper.matchContains());
			vo.setSplitSep(mapper.splitSep());
			vo.setSplitImpSep(mapper.splitImpSep());
			vo.setSplitExpSep(mapper.splitExpSep());
			vo.setSplitNoMatch2Null(mapper.splitNoMatch2Null());
			vo.setOrder(mapper.order());
			vo.setAction(mapper.action());
			if(mapper.serialize().length > 0) {
				vo.setSerializeSupportClazz(mapper.serialize()[0]);
			}
			vo.setGroups(mapper.groups());
			vo.setLength(mapper.length());
			vo.setTitleStyles(mapper.titleStyle());
			vo.setContentStyle(ArrayUtils.isEmpty(mapper.contentStyle())?null:mapper.contentStyle()[0]);
			result.add(vo);
		}
		result.sort((o1, o2)->o1.getOrder() - o2.getOrder());
		cacheMapperDescVo.put(clazz, result);
		if(log.isTraceEnabled()) {
			log.trace("parseDesc parse in class:[{}], result:[{}]", clazz, result);
		}
		return result;
	}
	
	private static Map<String, List<FieldMapper>> findAnnotationByFieldMapper(Class<?> clazz) {
		Map<String, FieldMappers> fieldMapperArrMap = ClassUtil.findAnnotationByClassFields(clazz, FieldMappers.class);
		Map<String, List<FieldMapper>> result = new LinkedHashMap<>();
		List<FieldMapper> list = new ArrayList<>();
		for (Map.Entry<String, FieldMappers> entry : fieldMapperArrMap.entrySet()) {
			if(result.containsKey(entry.getKey())) {
				list = result.get(entry.getKey());
			}else {
				list = new ArrayList<>();
				result.put(entry.getKey(), list);
			}
			list.addAll(Arrays.asList(entry.getValue().value()));
		}
		Map<String, FieldMapper> fieldMapperMap = ClassUtil.findAnnotationByClassFields(clazz, FieldMapper.class);
		for (Map.Entry<String, FieldMapper> entry : fieldMapperMap.entrySet()) {
			if(result.containsKey(entry.getKey())) {
				list = result.get(entry.getKey());
			}else {
				list = new ArrayList<>();
				result.put(entry.getKey(), list);
			}
			list.add(entry.getValue());
		}
		return result;
	}
	private static List<ClassFieldMapper> findAnnotationByClassFieldMapper(Class<?> clazz) {
		List<ClassFieldMapper> result = new ArrayList<>();
		ClassFieldMappers cfms = AnnotationUtils.findAnnotation(clazz, ClassFieldMappers.class);
		if(cfms != null) {
			result.addAll(Arrays.asList(cfms.value()));
		}
		ClassFieldMapper cfm = AnnotationUtils.findAnnotation(clazz, ClassFieldMapper.class);
		if(cfm != null) {
			result.add(cfm);
		}
		return result;
	}
	
	public static List<MapperDescVo> parseDescByGroups(Class<?> clazz, Class<?>...groups){
		List<MapperDescVo> parseDesc = parseDesc(clazz);
		if(ArrayUtils.isEmpty(groups)) {
			parseDesc = parseDesc.stream().filter(t->ArrayUtils.isEmpty(t.getGroups())).collect(Collectors.toList());
		}else {
			Predicate<MapperDescVo> predicate = t->{
				Class<?>[] voGroups = t.getGroups();
				if(ArrayUtils.isEmpty(voGroups)) {
					return true;
				}
				for (int i = 0; i < groups.length; i++) {
					if(ArrayUtils.contains(voGroups, groups[i])) {
						return true;
					}
				}
				return false;
			};
			parseDesc = parseDesc.stream().filter(predicate).collect(Collectors.toList());
		}
		return parseDesc;
	}
	
	public static Map<String, List<MapperDescVo>> getFromDescMap(Class<?> clazz, Class<?>...groups){
		List<MapperDescVo> parseDesc = parseDescByGroups(clazz, groups);
		Map<String, List<MapperDescVo>> result = parseDesc.stream().collect(Collectors.groupingBy(t->{
			String[] titleGroups = Stream.of(t.getTitleGroups()).filter(titleGroup->StringUtils.isNotBlank(titleGroup)).toArray(String[]::new);
			if(ArrayUtils.isEmpty(titleGroups)) {
				return t.getOtherName();
			}
			
			return StringUtils.join(titleGroups, TITLE_GROUPS_SEP) + TITLE_GROUPS_SEP + t.getOtherName();
		}, LinkedHashMap::new, Collectors.toList()));
		return result;
	}
	
	private static boolean hasAction(MapperDescVo desc, int action) {
		return (desc.getAction() & action)== action;
	}
	
	private static <T>void refreshContext(MapperContext<T> context) {
		if(!context.hasSerializeSupport() && context.getClazz() != null) {
			List<MapperDescVo> list = parseDescByGroups(context.getClazz(), context.getGroups());
			for (MapperDescVo vo : list) {
				if(vo.getSerializeSupportClazz() == null) {
					continue;
				}
				try {
					context.addSerializeSupport(vo, vo.getSerializeSupportClazz().newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					throw new DevelopeCodeException("can not instance " + vo.getSerializeSupportClazz() + ", it must has no argument contructor");
				}
			}
		}
	}
	
	public static <T>T importMapper(Map<String, Object> data, MapperContext<T> context) {
		return importMapper(data, context, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T importMapper(Map<String, Object> data, MapperContext<T> context, PostMapper<T, Map<String, Object>> postMapper) {
		T entity = context.getDataSupplier().get();
		if(context.getClazz() == null) {
			context.setClazz((Class<T>)entity.getClass());
		}
		refreshContext(context);
		Map<String, List<MapperDescVo>> descMap = getFromDescMap(context.getClazz(), context.getGroups());
		
		DictCodeDto code = null;
		BeanWrapper bean = new BeanWrapperImpl(entity);
		Object val = null;
		Object oringinVal = null;
		SerializeSupport<Object, Object> serializeSupport = null;
		Map<String, DictCodeDto> codeMap = null;
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			if(entry.getValue() == null) {
				continue;
			}
			oringinVal = entry.getValue();
			if(!descMap.containsKey(entry.getKey())) {
				continue;
			}
			if(oringinVal instanceof String && StringUtils.isBlank((String)oringinVal)) {
				continue;
			}
			List<MapperDescVo> descList = descMap.get(entry.getKey());
			Match[] matches = null;
			String[] tmpKeys = null;
			for (MapperDescVo desc : descList) {
				if(!hasAction(desc, FieldMapperConst.MAPPER_IMPORT)) {
					continue;
				}
				val = oringinVal;
				serializeSupport = (SerializeSupport<Object, Object>) context.getSerializeSupport(desc);
				if(serializeSupport == null) {
					if(desc.getLocalClass().isAssignableFrom(val.getClass())) {
						
					}else if(val instanceof Date && desc.getLocalClass() == String.class){
						val = DateFormatUtils.format((Date)val, StringUtils.isNotBlank(desc.getFormat())?desc.getFormat():"yyyy-MM-dd");
					}else if(val instanceof String && desc.getLocalClass().isAssignableFrom(Date.class)) {
						val = ConvertFactory.convertToObject((String)val, Date.class);
					}else if(val instanceof Number){
						NumberFormat nf = NumberFormat.getInstance();
						nf.setGroupingUsed(false);
						nf.setMaximumFractionDigits(10);
						val = nf.format(val);
					}else {
						val = String.valueOf(val);
					}
					
					if(val != null && StringUtils.isNotBlank(desc.getDictName())) {
						code = null;
						codeMap = context.getDictMapByType(desc.getDictName());
						if(StringUtils.isEmpty(desc.getSplitSep()) && StringUtils.isEmpty(desc.getSplitImpSep())) {
							code = codeMap.get(val);
							val = code == null?null:code.getCode();
						}else {
							final Map<String, DictCodeDto> finalCodeMap = codeMap;
							tmpKeys = String.valueOf(val).split(StringUtils.isNotEmpty(desc.getSplitImpSep())?desc.getSplitImpSep():desc.getSplitSep());
							List<String> valList = Stream.of(tmpKeys).map(t->finalCodeMap.containsKey(t)?finalCodeMap.get(t).getCode():null).collect(Collectors.toList());
							if(desc.isSplitNoMatch2Null() && valList.stream().anyMatch(t->t == null)) {
								val = null;
//								valList.forEach(t->System.out.println(t));
							}else {
								val = valList.stream().filter(t->t != null)
										.collect(Collectors.joining(StringUtils.isNotEmpty(desc.getSplitSep())?desc.getSplitSep():desc.getSplitImpSep()));
							}
							
							if(val != null && (val instanceof String) && StringUtils.isEmpty((String)val)) {
								val = null;
							}
							
						}
						
					}
				}else {
					Object param = val;
					val = serializeSupport.deserialize(param);
				}
				matches = desc.getMatch();
				if(ArrayUtils.isNotEmpty(matches)) {
					String matchVal = String.valueOf(val);
					for (int i = 0; i < matches.length; i++) {
						if(Objects.equals(matches[i].val(), matchVal)) {
							val = matches[i].key();
							break;
						}
					}
				}
				bean.setPropertyValue(desc.getLocalName(), val);
			}
			
		}
		entity = (T) bean.getWrappedInstance();
		if(postMapper != null) {
			postMapper.execute(entity, data);
		}
		if(context.isValidEnable()) {
			ValidationUtil.checkValid(entity, context.getValidGroups());
		}
		return entity;
	}
	
	public static Map<String, Class<?>> buildToFieldClassMap(Class<?> clazz, Class<?>...groups){
		Map<String, Class<?>> result = new LinkedHashMap<>();
		Map<String, List<MapperDescVo>> descMap = getFromDescMap(clazz, groups);
		List<MapperDescVo> list = null;
		for (Map.Entry<String, List<MapperDescVo>> entry : descMap.entrySet()) {
			list = entry.getValue();
			list = list.stream().filter(t->hasAction(t, FieldMapperConst.MAPPER_EXPORT)).collect(Collectors.toList());
			for (MapperDescVo vo : list) {
				if(vo.getSerializeSupportClazz() == null) {
					if(vo.getTargetClass() != null) {
						result.put(entry.getKey(), vo.getTargetClass());
					}else{
						result.put(entry.getKey(), vo.getLocalClass());
					}
				}else {
					Class<?> returnType = null;
					Class<? extends SerializeSupport<?, ?>> serializeSupportClazz = vo.getSerializeSupportClazz();
					Method[] methods = serializeSupportClazz.getDeclaredMethods();
					for (int i = 0; i < methods.length; i++) {
						if(Modifier.isPublic(methods[i].getModifiers()) && Objects.equals(methods[i].getName(), "serialize") 
								&& methods[i].getParameterCount() == 1 && methods[i].getReturnType() != void.class) {
							if(returnType == null) {
								returnType = methods[i].getReturnType();
							}else{
								Class<?> type = methods[i].getReturnType();
								Type paramType = methods[i].getParameters()[0].getParameterizedType();
								if(type != Object.class && paramType != Object.class) {
									returnType = methods[i].getReturnType();
								}
							}
							
						}
					}
					if(returnType == null) {
						throw new DevelopeCodeException("can not find the method name 'serialize' in class:" + serializeSupportClazz);
					}
					result.put(entry.getKey(), returnType);
				}
			}
		}
		return result;
	}
	
	public static <T> void init(MapperContext<T> context) {
		refreshContext(context);
		Map<String, Class<?>> fieldClassMap = context.getActualFieldClassMap();
		if(fieldClassMap == null) {
			fieldClassMap = buildToFieldClassMap(context.getClazz(), context.getGroups());
			context.setFieldClassMap(fieldClassMap);
		}
	}
	
	public static <T>Map<String, Object> buildToFieldDataMap(Object data, MapperContext<T> context){
		init(context);
		Map<String, Class<?>> fieldClassMap = context.getActualFieldClassMap();
		Map<String, Object> result = new HashMap<>();
		Map<String, List<MapperDescVo>> fromDescMap = getFromDescMap(context.getClazz(), context.getGroups());
		List<MapperDescVo> list = null;
		MapperDescVo desc = null;
		BeanWrapper bean = new BeanWrapperImpl(data);
		for (Map.Entry<String, List<MapperDescVo>> entry : fromDescMap.entrySet()) {
			list = entry.getValue();
			list = list.stream().filter(t->hasAction(t, FieldMapperConst.MAPPER_EXPORT)).collect(Collectors.toList());
			if(list.isEmpty()) {
				continue;
//				throw new DevelopeCodeException("can not found FieldMapper or ClassFieldMapper with field '" + entry.getKey() + "'");
			}
			if(!fieldClassMap.containsKey(entry.getKey())) {
				throw new DevelopeCodeException("can not found FieldMapper or ClassFieldMapper with field '" + entry.getKey() + "'");
			}
			if(list.size() == 1) {
				desc = list.get(0);
			}else {
				Class<?> returnType = fieldClassMap.get(entry.getKey());
				Optional<MapperDescVo> findFirst = list.stream().filter(t->returnType.isAssignableFrom(t.getLocalClass())).findFirst();
				if(!findFirst.isPresent()) {
					throw new DevelopeCodeException("can not found FieldMapper or ClassFieldMapper with field '" + entry.getKey() + "'" );
				}
				desc = findFirst.get();
			}
			result.put(entry.getKey(), getValue(bean, context, desc));
			
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>Object getValue(BeanWrapper bean, MapperContext<T> context, MapperDescVo desc){
		Object value = bean.getPropertyValue(desc.getLocalName());
		SerializeSupport<Object, Object> support = (SerializeSupport<Object, Object>) context.getSerializeSupport(desc);
		if(value == null) {
			return null;
		}
		if(support != null) {
			return support.serialize(value);
		}
		
		if(StringUtils.isNotBlank(desc.getDictName())) {
			Map<String, DictCodeDto> codeMap = context.getDictMapByType(desc.getDictName());
			if(StringUtils.isEmpty(desc.getSplitSep()) && StringUtils.isEmpty(desc.getSplitExpSep())) {
				if(codeMap.containsKey(value)) {
					value = codeMap.get(value).getValue();
				}
			}else {
				String[] tmpKeys = String.valueOf(value).split(StringUtils.isNotEmpty(desc.getSplitSep())?desc.getSplitSep():desc.getSplitExpSep());
				value = Stream.of(tmpKeys).map(t->codeMap.containsKey(t)?codeMap.get(t).getValue():null)
					.filter(t->StringUtils.isNotEmpty(t))
					.collect(Collectors.joining(StringUtils.isNotEmpty(desc.getSplitExpSep())?desc.getSplitExpSep():desc.getSplitSep()));
				if((value instanceof String) && StringUtils.isEmpty((String)value)) {
					value = null;
				}
			}
			
			if(value == null) {
				return null;
			}
		}
		Match[] matches = desc.getMatch();
		if(ArrayUtils.isNotEmpty(matches)) {
			String matchVal = String.valueOf(value);
			for (int i = 0; i < matches.length; i++) {
				if(Objects.equals(matches[i].val(), matchVal)) {
					value = matches[i].key();
					break;
				}
			}
		}
		
		if(desc.getTargetClass() != null && desc.getLocalClass() != desc.getTargetClass()) {
			value = ConvertFactory.convertToObject(String.valueOf(value), desc.getTargetClass());
		}
		return value;
	}

}
