package org.sonicframework.utils;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.utils.beans.BeanWrapperImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.util.ClassUtils;

/**
 * @author lujunyi
 */
public class ClassUtil {
	
	private static final String CLASS_SUFFIX = ".class";
	
	private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);

	private ClassUtil() {
	}
	
	public static Map<String, PropertyDescriptor> getClassDesc(Class<?> clazz) {
		BeanWrapperImpl wrapper = new BeanWrapperImpl(clazz);
		PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
		Map<String , PropertyDescriptor> map = new HashMap<>();
		for (int i = 0; i < descriptors.length; i++) {
			map.put(descriptors[i].getName(), descriptors[i]);
		}
		return map;
	}
	public static Field[] getClassAllField(Class<?> clazz) {
		LinkedHashMap<String, Field>map = new LinkedHashMap<>();
		Class<?> parentClass = clazz;
		Field[] fields = null;
		while (parentClass != null) {
			fields = parentClass.getDeclaredFields();
			for (Field field : fields) {
				if(!map.containsKey(field.getName())) {
					map.put(field.getName(), field);
				}
			}
			parentClass = parentClass.getSuperclass();
		}
		return map.values().toArray(new Field[0]);
	}
	
	public static boolean isSimpleValueType(Class<?> clazz) {
		return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() ||
				CharSequence.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) ||
				clazz.equals(URI.class) || clazz.equals(URL.class) ||
				clazz.equals(Locale.class) || clazz.equals(Class.class);
	}
	
	public static List<String> getFieldNameByAnnotation(Class<?> clazz, Class<? extends Annotation> annotation){
		List<String> result = new ArrayList<>();
		Field[] fields = getClassAllField(clazz);
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isAnnotationPresent(annotation)) {
				result.add(fields[i].getName());
			}
		}
		return result;
	}
	public static void mapFieldByAnnotation(Object source, Object dest, Class<?> clazz, Class<? extends Annotation> annotation){
		List<String> fields = getFieldNameByAnnotation(clazz, annotation);
		BeanWrapper sourceBean = new BeanWrapperImpl(source);
		BeanWrapper destBean = new BeanWrapperImpl(dest);
		for (String field : fields) {
			if(destBean.isWritableProperty(field)){
				destBean.setPropertyValue(field, sourceBean.getPropertyValue(field));
			}
		}
	}
	public static void mapFieldByNoAnnotation(Object source, Object dest, Class<?> clazz, Class<? extends Annotation> annotation){
		List<String> fields = getFieldNameByAnnotation(clazz, annotation);
		BeanWrapper sourceBean = new BeanWrapperImpl(source);
		BeanWrapper destBean = new BeanWrapperImpl(dest);
		Set<String> fieldSet = new HashSet<>(fields);
		PropertyDescriptor[] descriptors = sourceBean.getPropertyDescriptors();
		String fieldName = null;
		for (int i = 0; i < descriptors.length; i++) {
			fieldName = descriptors[i].getName();
			if(Objects.equals("class", fieldName)) {
				continue;
			}
			if(!fieldSet.contains(fieldName) && destBean.isWritableProperty(fieldName)) {
				destBean.setPropertyValue(fieldName, sourceBean.getPropertyValue(fieldName));
			}
		}
	}
	
	@SuppressWarnings("unused")
	public static boolean compareClassPropertiesEquals(Class<?> clazz1, Class<?> clazz2) {
		Map<String, PropertyDescriptor> descMap1 = getClassDesc(clazz1);
		Map<String, PropertyDescriptor> descMap2 = getClassDesc(clazz2);
		Set<String> set = new HashSet<>();
		PropertyDescriptor desc = null;
		for (Map.Entry<String, PropertyDescriptor> entry : descMap1.entrySet()) {
			if(descMap2.containsKey(entry.getKey())) {
				desc = descMap2.get(entry.getKey());
				if(entry.getValue().getPropertyType() == descMap2.get(entry.getKey()).getPropertyType()) {
					set.add(entry.getKey());
				}
			}
		}
		for (String string : set) {
			descMap1.remove(string);
			descMap2.remove(string);
		}
		return descMap1.isEmpty() && descMap2.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public static <T>Map<String, T> findAnnotationByClassFields(Class<?> clazz, Class<T> supportClass){
		Map<String, T> result = new HashMap<>();
		Field[] fields = getClassAllField(clazz);
		Annotation obj = null;
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isAnnotationPresent((Class<? extends Annotation>) supportClass)) {
				obj = fields[i].getAnnotation((Class<? extends Annotation>) supportClass);
				result.put(fields[i].getName(), (T) obj);
			}
		}
		return result;
	}
	
	public static boolean isWrapClass(Class<?> clz) {
	    try {
	        return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static boolean isCommonDataType(Class<?> clazz){
	    return clazz.isPrimitive();
	}
	public static boolean isPrimitive(Class<?> clazz){
		return clazz.isPrimitive() || isWrapClass(clazz);
	}
	public static boolean isNormalClass(Class<?> clazz){
		return clazz.isPrimitive() || isWrapClass(clazz) || clazz == String.class || Date.class.isAssignableFrom(clazz);
	}

	public static List<String> parseStackTrace(Throwable t) {
		List<String> result = new ArrayList<>();
		if(t == null) {
			return result;
		}
		Throwable e = t;
		boolean isfirst = true;
		while(e != null) {
			result.add((isfirst?"":"Cause by:") + e.toString());
			result.addAll(Arrays.stream(e.getStackTrace()).map(trace->" at " + trace.toString()).collect(Collectors.toList()));
			e = e.getCause();
			isfirst = false;
		}
		return result;
	}
	
	public static Set<Class<?>> scanPackage(String packageName) throws ClassNotFoundException, IOException {
		return scanPackage(packageName, null);
	}
	public static Set<Class<?>> scanPackage(String packageName, Predicate<Class<?>> filter) throws ClassNotFoundException, IOException {
		Set<Class<?>> classSet = new TreeSet<>((o1, o2)->o1.getName().compareTo(o2.getName()));
		Set<String> scanJarSet = new HashSet<>();
		scanPackage(packageName, classSet, scanJarSet, filter);
		return classSet;
	}
	
	private static void scanPackage(String packageName, Set<Class<?>> classSet, Set<String> scanJarSet, Predicate<Class<?>> filter) throws IOException, ClassNotFoundException {
        logger.trace("start to scanPackage, packageName:{}", packageName);
        //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> classes = ClassLoader.getSystemResources(packageName.replace(".", "/"));
        Class<?> clazz = null;
        String jarName = null;
        while (classes.hasMoreElements()) {
            URL url = classes.nextElement();
            if(url.getPath().contains("!")) {
            	jarName = url.getPath().substring(0, url.getPath().lastIndexOf("!"));
            	if(!scanJarSet.contains(jarName)) {
            		scanJarPackage(jarName, packageName, classSet, filter);
            		scanJarSet.add(jarName);
            	}
            	continue;
            }
            File packagePath = new File(url.getPath());
            if (packagePath.isDirectory()) {
                File[] files = packagePath.listFiles();
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isDirectory()) {
                        String newPackageName = StringUtils.isEmpty(packageName)?fileName:String.format("%s.%s", packageName, fileName);
                        scanPackage(newPackageName, classSet, scanJarSet, filter);
                    } else {
                    	if(!fileName.endsWith(CLASS_SUFFIX)) {
                    		continue;
                    	}
                        String className = fileName.substring(0, fileName.lastIndexOf("."));
                        String fullClassName = StringUtils.isEmpty(packageName)?className:String.format("%s.%s", packageName, className);
                        clazz = Class.forName(fullClassName);
                        if(filter == null || filter.test(clazz)) {
                        	classSet.add(clazz);
                        }
                    }
                }
            } else {
                String className = url.getPath().substring(0, url.getPath().lastIndexOf("."));
                String fullClassName = StringUtils.isEmpty(packageName)?className:String.format("%s.%s", packageName, className);
                clazz = Class.forName(fullClassName);
                if(filter == null || filter.test(clazz)) {
                	classSet.add(clazz);
                }
            }
        }
    }
	
	@SuppressWarnings("resource")
	private static void scanJarPackage(String jarPath, String packageName, Set<Class<?>> classSet, Predicate<Class<?>> filter) throws IOException, ClassNotFoundException {
		packageName = packageName.replace(".", "/");
		String packageNamePrefix = packageName + "/";
		URL url = new URL(jarPath);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, ClassUtil.class.getClassLoader());
        JarFile jarFile = new JarFile(url.getPath());
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarName = jarEntry.getName();
            if(!Objects.equals(jarName, packageName) && !jarName.startsWith(packageNamePrefix)) {
            	continue;
            }
            if(logger.isTraceEnabled()) {
            	logger.trace("start to scanPackage, packageName:{}", jarPath + "!/" + packageName);
            }
            if (!jarEntry.isDirectory() && jarName.endsWith(CLASS_SUFFIX)) {
                // 将文件路径名转换为包名称的形式
                String className = jarName.replace(CLASS_SUFFIX, "");
                className = className.replace("/", ".");
                Class<?> clazz = classLoader.loadClass(className);
                if((filter == null || filter.test(clazz)) && !classSet.contains(clazz)) {
                	classSet.add(clazz);
                }
            }
        }
	}
	
	

}
