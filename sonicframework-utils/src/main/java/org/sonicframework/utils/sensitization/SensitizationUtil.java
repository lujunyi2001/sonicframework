package org.sonicframework.utils.sensitization;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.sonicframework.utils.ClassUtil;
import org.sonicframework.utils.beans.BeanWrapperImpl;
import org.springframework.beans.BeanWrapper;

import org.sonicframework.context.exception.DevelopeCodeException;
import org.sonicframework.context.sensitization.Env;
import org.sonicframework.context.sensitization.NoneSensitization;
import org.sonicframework.context.sensitization.SensitizationResultWrapperSupport;
import org.sonicframework.context.sensitization.SensitizationSupport;
import org.sonicframework.context.sensitization.annotation.FieldSensitization;
import org.sonicframework.context.sensitization.annotation.SensitizationEnv;

/**
 * @author lujunyi
 */
public class SensitizationUtil {

	private static Map<Class<?>, List<SensitizationItemVo>> cacheMap = new ConcurrentHashMap<>();

	private SensitizationUtil() {
	}

	private static List<SensitizationItemVo> parseClass(Class<?> clazz) {
		if (cacheMap.containsKey(clazz)) {
			return cacheMap.get(clazz);
		}
		List<SensitizationItemVo> result = new ArrayList<>();
		Map<String, PropertyDescriptor> classDesc = ClassUtil.getClassDesc(clazz);
		Map<String, FieldSensitization> annoMap = ClassUtil.findAnnotationByClassFields(clazz,
				FieldSensitization.class);
		SensitizationItemVo vo = null;
		FieldSensitization anno = null;
		for (Map.Entry<String, FieldSensitization> entry : annoMap.entrySet()) {
			if (!classDesc.containsKey(entry.getKey())) {
				continue;
			}
			if (classDesc.get(entry.getKey()).getPropertyType() != String.class) {
				continue;
			}
			anno = entry.getValue();
			vo = buildSensitizationItemVo(entry.getKey(), anno);
			result.add(vo);
		}
		cacheMap.put(clazz, result);
		return result;

	}
	
	private static SensitizationItemVo buildSensitizationItemVo(String key, FieldSensitization anno) {
		SensitizationItemVo vo = new SensitizationItemVo();
		vo.setFieldName(key);
		vo.setGroups(anno.groups());
		try {
			vo.setSupport(anno.support().newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new DevelopeCodeException("can not instance class:" + anno.support(), e);
		}
		SensitizationEnv[] envs = anno.env();
//		if (ArrayUtils.isEmpty(envs)) {
//			throw new DevelopeCodeException("there is no env in annotation FieldSensitization where parse field: "
//					+ key + " in class:" + anno.support());
//		}
		Env env = new Env();
		if(ArrayUtils.isNotEmpty(envs)) {
			env.setEnd(envs[0].end());
			env.setPattern(envs[0].pattern());
			env.setStart(envs[0].start());
			env.setMask(envs[0].mask());
			env.setMaskRepeat(envs[0].maskRepeat());
		}
		vo.setEnv(env);
		return vo;
	}
	public static SensitizationItemVo buildDefaultSensitizationItemVo(Class<?extends SensitizationSupport> clazz, SensitizationEnv envAnno) {
		if(clazz == null) {
			clazz = NoneSensitization.class;
		}
		SensitizationItemVo vo = new SensitizationItemVo();
		try {
			vo.setSupport(clazz.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new DevelopeCodeException("can not instance class:" + clazz, e);
		}
		Env env = new Env();
		if(envAnno != null) {
			env.setEnd(envAnno.end());
			env.setPattern(envAnno.pattern());
			env.setStart(envAnno.start());
			env.setMask(envAnno.mask());
			env.setMaskRepeat(envAnno.maskRepeat());
		}
		vo.setEnv(env);
		return vo;
	}

	private static List<SensitizationItemVo> parseClassByGroups(Class<?> clazz, Class<?>... groups) {
		List<SensitizationItemVo> list = parseClass(clazz);
		if (ArrayUtils.isEmpty(groups)) {
			list = list.stream().filter(t -> ArrayUtils.isEmpty(t.getGroups())).collect(Collectors.toList());
		} else {
			Predicate<SensitizationItemVo> predicate = t -> {
				Class<?>[] voGroups = t.getGroups();
				if (ArrayUtils.isEmpty(voGroups)) {
					return true;
				}
				for (int i = 0; i < groups.length; i++) {
					if (ArrayUtils.contains(voGroups, groups[i])) {
						return true;
					}
				}
				return false;
			};
			list = list.stream().filter(predicate).collect(Collectors.toList());
		}
		return list;
	}

	public static <T> Object encrypt(Object obj, List<SensitizationResultWrapperSupport<?>> wrapperSupportList,
			SensitizationItemVo defaultSupport, 
			FieldSensitization[] extend, Class<?>... groups) {
		if (obj == null || obj instanceof Map<?, ?>) {
			return obj;
		}
		
		Map<String, SensitizationItemVo> extendMap = new HashMap<>();
		if(ArrayUtils.isNotEmpty(extend)) {
			for (int i = 0; i < extend.length; i++) {
				if(StringUtils.isNotBlank(extend[i].key())) {
					extendMap.put(extend[i].key(), buildSensitizationItemVo(extend[i].key(), extend[i]));
				}
			}
		}
		
		Map<String, SensitizationItemVo> descMap = new HashMap<>();
		Object t = null;
		SensitizationResultWrapperSupport<?> actualSupport = null;
		if (CollectionUtils.isNotEmpty(wrapperSupportList)) {
			for (SensitizationResultWrapperSupport<?> wrapperSupport : wrapperSupportList) {
				if (wrapperSupport.isSupport(obj)) {
					actualSupport = wrapperSupport;
					t = wrapperSupport.get(obj);
					break;
				}
			}
		}
		if (actualSupport != null) {
			if (t == null) {
				return obj;
			}
			t = executeEncrypt(t, descMap, defaultSupport, extendMap, groups);
			actualSupport.set(obj, t);
		} else {
			obj = executeEncrypt(obj, descMap, defaultSupport, extendMap, groups);
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T executeEncrypt(T obj, Map<String, SensitizationItemVo> descMap, SensitizationItemVo defaultSupport, 
			Map<String, SensitizationItemVo> extendMap, Class<?>[] groups) {
		if (obj instanceof List) {
			return (T) convertList((List<?>) obj, descMap, defaultSupport, extendMap, groups);
		} else if (obj instanceof Set) {
			return (T) convertSet((Set<?>) obj, descMap, defaultSupport, extendMap, groups);
		} else {
			return encryptItem(obj, descMap, defaultSupport, extendMap, groups);
		}
	}

	private static <E> List<E> convertList(List<E> list, Map<String, SensitizationItemVo> descMap, SensitizationItemVo defaultSupport, 
			Map<String, SensitizationItemVo> extendMap, Class<?>[] groups) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}

		List<E> result = new ArrayList<>();
		for (E object : list) {
			result.add(encryptItem(object, descMap, defaultSupport, extendMap, groups));
		}
		return result;
	}

	private static <T> Set<T> convertSet(Set<T> set, Map<String, SensitizationItemVo> descMap, SensitizationItemVo defaultSupport, 
			Map<String, SensitizationItemVo> extendMap, Class<?>[] groups) {
		if (CollectionUtils.isEmpty(set)) {
			return set;
		}
		Set<T> result = new LinkedHashSet<>();
		for (T object : set) {
			result.add(encryptItem(object, descMap, defaultSupport, extendMap, groups));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T> T encryptItem(T obj, Map<String, SensitizationItemVo> descMap, SensitizationItemVo defaultSupport, Map<String, SensitizationItemVo> extendMap, Class<?>[] groups) {
		if (obj instanceof Map) {
			return (T) encryptItemMap((Map<Object, Object>) obj, extendMap);
		}else {
			if (descMap.isEmpty()) {
				List<SensitizationItemVo> descs = parseClassByGroups(obj.getClass(), groups);
				Map<String, SensitizationItemVo> tmpMap = descs.stream().collect(Collectors.toMap(SensitizationItemVo::getFieldName, t->t, (t1, t2)->t2));
				descMap.putAll(tmpMap);
			}
			
			BeanWrapper bean = new BeanWrapperImpl(obj);
			PropertyDescriptor[] descriptors = bean.getPropertyDescriptors();
			for (int i = 0; i < descriptors.length; i++) {
				if(descriptors[i].getPropertyType() != String.class){
					continue;
				}
				if(descriptors[i].getPropertyType() != String.class){
					continue;
				}
				putVal(bean, descriptors[i].getName(), descMap, defaultSupport, extendMap);
			}
			return (T) bean.getWrappedInstance();
		}
	}
	
	private static void putVal(BeanWrapper bean, String fieldName, Map<String, SensitizationItemVo> descMap, 
			SensitizationItemVo defaultSupport, Map<String, SensitizationItemVo> extendMap) {
		Object val = bean.getPropertyValue(fieldName);
		if(val == null || !(val instanceof String)) {
			return;
		}
		SensitizationItemVo actualVo = null;
		if(descMap.containsKey(fieldName)) {
			actualVo = descMap.get(fieldName);
		}else if(extendMap.containsKey(fieldName)) {
			actualVo = extendMap.get(fieldName);
		}else if(defaultSupport != null) {
			actualVo = defaultSupport;
		}
		if(actualVo != null) {
			val = actualVo.getSupport().serializ((String) val, actualVo.getEnv());
			bean.setPropertyValue(fieldName, val);
		}
		
	}
	
	private static Map<Object, Object> encryptItemMap(Map<Object, Object> map, Map<String, SensitizationItemVo> extendMap) {
		if(map == null) {
			return map;
		}
		Object val = null;
		SensitizationItemVo value = null;
		Object resultVal = null;
		for (Map.Entry<String, SensitizationItemVo> entry : extendMap.entrySet()) {
			val = map.get(entry.getKey());
			if(val == null || !(val instanceof String)) {
				continue;
			}
			value = entry.getValue();
			resultVal = value.getSupport().serializ((String) val, value.getEnv());
			map.put(entry.getKey(), resultVal);
		}
		return map;
	}

}
