package org.sonicframework.utils.fillup;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.utils.ClassUtil;
import org.sonicframework.utils.beans.BeanWrapperImpl;
import org.springframework.beans.BeanWrapper;

import org.sonicframework.context.dto.DictCodeDto;
import org.sonicframework.context.fillup.FillupConst;
import org.sonicframework.context.fillup.annotation.DictFillupMapper;
import org.sonicframework.context.fillup.annotation.DictFillupMappers;

/**
* @author lujunyi
*/
public class FillupUtil {

	private static Map<Class<?>, List<FillupMapperDescVo>> cacheMapperDescVo = new ConcurrentHashMap<>();
	private static Logger log = LoggerFactory.getLogger(FillupUtil.class);
	
	private FillupUtil() {}
	
	public static List<FillupMapperDescVo> parseDesc(Class<?> clazz){
		if(cacheMapperDescVo.containsKey(clazz)) {
			return cacheMapperDescVo.get(clazz);
		}
		List<FillupMapperDescVo> result = new ArrayList<>();
		FillupMapperDescVo vo = null;
		String local = null;
		String dictName = null;
		Map<String, PropertyDescriptor> classDesc = ClassUtil.getClassDesc(clazz);
		List<DictFillupMapper> fm = null;
		Map<String, List<DictFillupMapper>> fieldMapperMap = findAnnotationByFieldMapper(clazz);
		for (Map.Entry<String, List<DictFillupMapper>> entry : fieldMapperMap.entrySet()) {
			fm = entry.getValue();
			for (DictFillupMapper fieldMapper: fm) {
				local = entry.getKey();
				if(!classDesc.containsKey(local)) {
					continue;
				}
				if(classDesc.get(local).getPropertyType() != String.class) {
					continue;
				}
				if(StringUtils.isBlank(fieldMapper.dictName())) {
					continue;
				}
				dictName = fieldMapper.dictName().trim();
				vo = new FillupMapperDescVo();
				vo.setFieldName(local);
				vo.setDictName(dictName);
				vo.setBindType(fieldMapper.bindType());
				vo.setTarget(fieldMapper.target());
				vo.setSplit(fieldMapper.split());
				vo.setOutputSplit(fieldMapper.outputSplit());
				vo.setGroups(fieldMapper.groups());
				result.add(vo);
			}
			
			
		}
		cacheMapperDescVo.put(clazz, result);
		if(log.isTraceEnabled()) {
			log.trace("parseDesc parse in class:[{}], result:[{}]", clazz, result);
		}
		return result;
	} 
	
	private static Map<String, List<DictFillupMapper>> findAnnotationByFieldMapper(Class<?> clazz) {
		Map<String, DictFillupMappers> fieldMapperArrMap = ClassUtil.findAnnotationByClassFields(clazz, DictFillupMappers.class);
		Map<String, List<DictFillupMapper>> result = new LinkedHashMap<>();
		List<DictFillupMapper> list = new ArrayList<>();
		for (Map.Entry<String, DictFillupMappers> entry : fieldMapperArrMap.entrySet()) {
			if(result.containsKey(entry.getKey())) {
				list = result.get(entry.getKey());
			}else {
				list = new ArrayList<>();
				result.put(entry.getKey(), list);
			}
			list.addAll(Arrays.asList(entry.getValue().value()));
		}
		Map<String, DictFillupMapper> fieldMapperMap = ClassUtil.findAnnotationByClassFields(clazz, DictFillupMapper.class);
		for (Map.Entry<String, DictFillupMapper> entry : fieldMapperMap.entrySet()) {
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
	
	public static List<FillupMapperDescVo> parseDescByGroups(Class<?> clazz, Class<?>...groups){
		List<FillupMapperDescVo> parseDesc = parseDesc(clazz);
		if(ArrayUtils.isEmpty(groups)) {
			parseDesc = parseDesc.stream().filter(t->ArrayUtils.isEmpty(t.getGroups())).collect(Collectors.toList());
		}else {
			Predicate<FillupMapperDescVo> predicate = t->{
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
	
	public static <T>void fillup(T obj, FillupMapperContext<T> context, Class<?>...groups){
		if(obj == null) {
			return ;
		}
		Class<?> clazz = obj.getClass();
		List<FillupMapperDescVo> descList = parseDescByGroups(clazz, groups);
		Map<String, DictCodeDto> valueDictCodeMap = new HashMap<>();
		Map<String, List<DictCodeDto>> valueDictCodeListMap = new HashMap<>();
		BeanWrapper bean = new BeanWrapperImpl(obj);
		String key = null;
//		Map<String, DictCodeDto> codeMap = null;
		String code = null;
		DictCodeDto dictCodeDto = null;
		List<DictCodeDto> dictCodeDtoList = null;
		for (FillupMapperDescVo desc : descList) {
			key = desc.getFieldName();
			if(valueDictCodeMap.containsKey(key) || valueDictCodeListMap.containsKey(key)) {
				continue;
			}
			code = (String) bean.getPropertyValue(key);
			if(code == null) {
				continue;
			}
			Map<String, DictCodeDto> codeMap = context.getDictMapByType(desc.getDictName());
			if(StringUtils.isEmpty(desc.getSplit())) {
				dictCodeDto = codeMap.get(code);
				if(dictCodeDto == null) {
					continue;
				}
				valueDictCodeMap.put(key, dictCodeDto);
			}else {
				dictCodeDtoList = Stream.of(code.split(desc.getSplit())).map(t->codeMap.containsKey(t)?codeMap.get(t):null).filter(t->t != null).collect(Collectors.toList());
				valueDictCodeListMap.put(key, dictCodeDtoList);
			}
			
		}
		
		String target = null;
		String bindVal = null;
		for (FillupMapperDescVo desc : descList) {
			if(Objects.equals(desc.getTarget(), FillupConst.TARGET_THIS)) {
				target = desc.getFieldName();
			}else {
				target = desc.getTarget();
			}
			
			if(StringUtils.isEmpty(desc.getSplit())) {
				dictCodeDto = valueDictCodeMap.get(desc.getFieldName());
				if(dictCodeDto == null) {
					continue;
				}
				bean.setPropertyValue(target, desc.getBindType().getBindValue(dictCodeDto));
			}else {
				dictCodeDtoList = valueDictCodeListMap.get(desc.getFieldName());
				if(CollectionUtils.isEmpty(dictCodeDtoList)) {
					continue;
				}
				bindVal = dictCodeDtoList.stream().map(t->desc.getBindType().getBindValue(t)).collect(Collectors.joining(StringUtils.isEmpty(desc.getOutputSplit())?desc.getSplit():desc.getOutputSplit()));
				bean.setPropertyValue(target, bindVal);
			}
		}
		
	}

}
