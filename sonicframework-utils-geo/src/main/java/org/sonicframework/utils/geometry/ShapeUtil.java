package org.sonicframework.utils.geometry;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.utils.ConsumerImpEntity;
import org.sonicframework.utils.JsonUtil;
import org.sonicframework.utils.StreamUtil;
import org.sonicframework.utils.ValidateResult;
import org.sonicframework.utils.ValidationUtil;
import org.sonicframework.utils.file.FileUtil;
import org.sonicframework.utils.geometry.mapper.GeoFieldMapperUtil;
import org.sonicframework.utils.geometry.mapper.GeoMapperContext;
import org.sonicframework.utils.mapper.MapperColumnDesc;
import org.sonicframework.utils.mapper.PostMapper;

import org.sonicframework.context.exception.BaseBizException;
import org.sonicframework.context.exception.DataCheckException;
import org.sonicframework.context.exception.DataNotValidException;
import org.sonicframework.context.exception.FileCheckException;

/**
* @author lujunyi
*/
@SuppressWarnings("static-access")
public class ShapeUtil {
	private final static String SHAPEFILE_EXTEND_NAME = ".shp";
	private final static String CPGFILE_EXTEND_NAME = ".cpg";
	
	public final static String DEFAULT_GEOKEY = "the_geom";
	
	private static Logger logger = LoggerFactory.getLogger(ShapeUtil.class);
	
	static {
		
		try {
			ExportShp.class.getClassLoader().getSystemClassLoader().loadClass("org.geotools.data.shapefile.dbf.DbaseFileHeader");
		} catch (Throwable e) {
		}
	}

	private ShapeUtil() {}
	
	public static ExportShp<Map<String, Object>> buildNewExport(String path, String fileName, Map<String, Class<?>> fieldNameMap) {
		Map<String, MapperColumnDesc> convertMapperColumns = convertMapperColumns(fieldNameMap);
		return ExportShp.buildEmpty(path, fileName, convertMapperColumns, DEFAULT_GEOKEY);
	}
	public static ExportShp<Map<String, Object>> buildNewExport(String path, String fileName, Map<String, Class<?>> fieldNameMap, String geoKey) {
		Map<String, MapperColumnDesc> convertMapperColumns = convertMapperColumns(fieldNameMap);
		return ExportShp.buildEmpty(path, fileName, convertMapperColumns, geoKey);
	}
	public static ExportShp<Map<String, Object>> buildNewExport(String path, String fileName, Map<String, Class<?>> fieldNameMap, String geoKey, String charset) {
		Map<String, MapperColumnDesc> convertMapperColumns = convertMapperColumns(fieldNameMap);
		return ExportShp.buildEmpty(path, fileName, convertMapperColumns, geoKey, charset);
	}
	public static <T>ExportShp<T> buildNewExport(String path, String fileName, GeoMapperContext<T> context) {
		return ExportShp.buildEmpty(path, fileName, context, DEFAULT_GEOKEY);
	}
	
	private static Map<String, MapperColumnDesc> convertMapperColumns(Map<String, Class<?>> fieldNameMap){
		if(fieldNameMap == null) {
			return null;
		}
		LinkedHashMap<String , MapperColumnDesc> result = new LinkedHashMap<>();
		for (Entry<String, Class<?>> entry : fieldNameMap.entrySet()) {
			result.put(entry.getKey(), new MapperColumnDesc(entry.getValue().getClass()));
		}
		return result;
	}
	/**
	 * 创建shape文件导出类
	 * @param path 生成目录
	 * @param fileName 生成文件名
	 * @param context 映射上下文
	 * @param geoKey 导出图形数据key
	 * @return
	 */
	public static <T>ExportShp<T> buildNewExport(String path, String fileName, GeoMapperContext<T> context, String geoKey) {
		return ExportShp.buildEmpty(path, fileName, context, geoKey);
	}
	/**
	 * 创建shape文件导出类
	 * @param path 生成目录
	 * @param fileName 生成文件名
	 * @param context 映射上下文
	 * @param geoKey 导出图形数据key
	 * @param charset 字符集,默认utf-8
	 * @return
	 */
	public static <T>ExportShp<T> buildNewExport(String path, String fileName, GeoMapperContext<T> context, String geoKey, String charset) {
		return ExportShp.buildEmpty(path, fileName, context, geoKey, charset);
	}
	
	/**
	 * 导出成geojson
	 * @param pathName 解析文件目录或文件名(传入目录解析目录下所有shp文件)
	 * @return geojson组成的List
	 */
	public static List<String> extractInfoGeoJson(String pathName){
		List<String> result = new ArrayList<>();
		extractInfoGeoJson(pathName, t->result.add(t));
//		return extractInfoGeoJson(file);
		return result;
	}
	
	/**
	 * 导出成geojson
	 * @param pathName 解析文件目录或文件名(传入目录解析目录下所有shp文件)
	 * @param consumer geojson的消费者Consumer<String>
	 */
	public static void extractInfoGeoJson(String pathName, Consumer<String> consumer){
		File basePath = new File(pathName);
		checkFile(basePath);
		
		Consumer<FeatureContextVo> featureConsumer = t->{
			String geojson = buildGeojson(t.getFeature());
			consumer.accept(geojson);
		};
		if(basePath.isFile()) {
			extractOneShpFile(basePath, featureConsumer);
		}else {
			File[] listFile = FileUtil.listFile(basePath.getPath(), SHAPEFILE_EXTEND_NAME);
			for (int i = 0; i < listFile.length; i++) {
				extractOneShpFile(listFile[i], featureConsumer);
			}
		}
	}
	
	/**
	 * 解析shape文件成实体类
	 * @param pathName 解析文件目录或文件名(传入目录解析目录下所有shp文件)
	 * @param context 字段映射上下文
	 * @param consumer 生成实体类消费者
	 */
	public static <T>void extractInfoEntity(String pathName, GeoMapperContext<T> context, ConsumerImpEntity<T, ValidateResult> consumer) {
		extractInfoEntity(pathName, context, consumer, null);
	}
	
	/**
	 * 解析shape文件成实体类
	 * @param pathName 解析文件目录或文件名(传入目录解析目录下所有shp文件)
	 * @param context 字段映射上下文
	 * @param consumer 生成实体类消费者
	 * @param postMapper 映射字段后置方法
	 */
	public static <T>void extractInfoEntity(String pathName, GeoMapperContext<T> context, ConsumerImpEntity<T, ValidateResult> consumer, PostMapper<T, ShpInfoVo> postMapper) {
		boolean validEnable = context.isValidEnable();
		Class<?>[] validGroups = context.getValidGroups();
		context.setValidEnable(false, validGroups);
		extractInfo(pathName, t->{
			T entity = GeoFieldMapperUtil.importMapper(t, context, postMapper);
			ValidateResult validateResult = null;
			if(validEnable) {
				/*try {
					ValidationUtil.checkValid(entity, validGroups);
					validateResult = new ValidateResult(true, null);
				} catch (DataNotValidException e) {
					validateResult = new ValidateResult(false, e.getMessage());
				}*/
				List<String> errMsgList = ValidationUtil.valid(entity, validGroups);
				if(errMsgList.isEmpty()) {
					validateResult = new ValidateResult(true, null);
				}else {
					validateResult = new ValidateResult(false, StringUtils.join(errMsgList, ","), errMsgList);
				}
			}else {
				validateResult = new ValidateResult(true, null);
			}
			consumer.execute(entity, validateResult);
			
		}, context);
		context.setValidEnable(validEnable, validGroups);
	}
	
	public static List<ShpInfoVo> extractInfo(String pathName) {
		List<ShpInfoVo> result = new ArrayList<>();
		extractInfo(pathName, t->result.add(t));
		return result;
	}
	
	public static void extractInfo(String pathName, Consumer<ShpInfoVo> consumer){
		extractInfo(pathName, consumer, null);
	}
	
	public static <T>void extractInfo(String pathName, Consumer<ShpInfoVo> consumer, GeoMapperContext<T> context){
		File basePath = new File(pathName);
		checkFile(basePath);
		
		Consumer<FeatureContextVo> featureConsumer = t->{
			ShpInfoVo infoVo = buildShpInfoVo(t, context);
			consumer.accept(infoVo);
		};
		if(basePath.isFile()) {
			extractOneShpFile(basePath, featureConsumer);
		}else {
			File[] listFile = FileUtil.listFile(basePath.getPath(), SHAPEFILE_EXTEND_NAME);
			for (int i = 0; i < listFile.length; i++) {
				extractOneShpFile(listFile[i], featureConsumer);
			}
		}
	}
	
	private static void checkFile(File file) {
		if(!file.exists()) {
			throw new FileCheckException("没有找到文件" + file.getPath());
		}
		if(file.isFile() && !file.getName().toLowerCase().endsWith(SHAPEFILE_EXTEND_NAME)) {
			throw new FileCheckException("不合法的shape文件扩展名" + file.getPath());
		}
	}
	
	private static void extractOneShpFile(File file, Consumer<FeatureContextVo> consumer) {
		String sourceName = file.getName();
		SimpleFeatureIterator featureIterator = null;
		try {
			CoordinateReferenceSystem coordinateReferenceSystem = getCoordinateReferenceSystem(file.getPath());
			FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
			String charset = getCharSet(file.getPath());
			if(ExportShp.CHARSET_OEM.equalsIgnoreCase(charset)) {
				charset = ExportShp.CHARSET_GBK;
			}
			((ShapefileDataStore) dataStore).setCharset(Charset.forName(charset));
			// 获取特征资源
			SimpleFeatureSource simpleFeatureSource = dataStore.getFeatureSource();
			// 要素集合
			SimpleFeatureCollection simpleFeatureCollection = simpleFeatureSource.getFeatures();
			// 要素数量
//			int featureSize = simpleFeatureCollection.size();
			// 获取要素迭代器
			featureIterator = simpleFeatureCollection.features();
			
			int index = 0;
			FeatureContextVo featureVo = null;
			while (featureIterator.hasNext()) {
				// 要素对象
				SimpleFeature feature = featureIterator.next();
				featureVo = new FeatureContextVo(feature, sourceName, coordinateReferenceSystem, index);
				logger.trace("extractInfo file:[{}] index:[{}]", file, index);
				consumer.accept(featureVo);
				index++;
			}
		} catch (BaseBizException e) {
			throw e;
		}  catch (Exception e) {
			throw new FileCheckException(FileCheckException.MESSAGE, e);
		} finally {
			StreamUtil.close(featureIterator);
		}
	}
	
	private static CoordinateReferenceSystem getCoordinateReferenceSystem(String path){
		CoordinateReferenceSystem result = null;
		int p = path.lastIndexOf(".");
		String prj = path.substring(0,p) + ".prj";
		String fileContent = getFileContent(prj);
		if(fileContent != null) {
			try {
				result = CRS.parseWKT(fileContent);
			} catch (FactoryException e) {
				logger.debug("解析{}文件出错{}", prj, e.toString());
			}
		}
		
		return result;
	}
	
	public static String getCharSet(String path){
        String charset = "GBK";

        int p = path.lastIndexOf(".");
        String cpg = path.substring(0, p) + CPGFILE_EXTEND_NAME;
        String fileContent = getFileContent(cpg);
        if(fileContent != null) {
        	charset = fileContent;
        }

        return charset;
    }
	
	private static String getFileContent(String filePath) {
		File file = new File(filePath);
        if(file != null && file.exists()) {
            RandomAccessFile raf = null;
            try {
            	List<String> list = new ArrayList<>();
                raf = new RandomAccessFile(filePath, "r");
                String str = null;
                while ((str = raf.readLine()) != null) {
					if(StringUtils.isNotBlank(str)) {
						list.add(str.trim());
					}
					
				}
                return StringUtils.join(list, "");
                
            } catch (Exception e) {
                logger.debug("解析{}文件出错{}", filePath, e.toString());
            } finally {
            	StreamUtil.close(raf);
            }
        }
        return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T>ShpInfoVo buildShpInfoVo(FeatureContextVo featureVo, GeoMapperContext<T> context) {
		ShpInfoVo info = new ShpInfoVo();
		info.setSourceName(featureVo.getSourceName());
		info.setDataIndex(featureVo.getDataIndex());
		info.setCoordinateReferenceSystem(featureVo.getCoordinateReferenceSystem());
		List<ShpRecordVo> recordList = new ArrayList<>();
		info.setRecordList(recordList);
		
		ShpRecordVo record = null;
		SimpleFeature feature = featureVo.getFeature();
		
		// 要素属性信息，名称，值，类型
		List<Property> propertyList = (List<Property>) feature.getValue();
		String geoStr = null;
		try {
			for (Property property : propertyList) {
				if(property.getValue() != null) {
					record = new ShpRecordVo(property.getName().getLocalPart(), property.getValue(), property.getType().getBinding());
					recordList.add(record);
					logger.trace("buildShpInfoVo record:[{}]", record);
				}
				if(property.getValue() != null && Geometry.class.isAssignableFrom(property.getType().getBinding())){
					Geometry geo = (Geometry) property.getValue();
					if(context.getCrs() != null) {
						if(info.getCoordinateReferenceSystem() == null) {
							throw new FileCheckException("没有找到坐标系文件");
						}
						if(!Objects.equals(info.getCoordinateReferenceSystem(), context.getCrs())) {
							try {
								MathTransform mt = CRS.findMathTransform(info.getCoordinateReferenceSystem(), context.getCrs(), true);
								geo = JTS.transform(geo, mt);
					    	} catch (FactoryException e) {e.printStackTrace();
								throw new DataCheckException("创建坐标系转换失败", e);
							} catch (MismatchedDimensionException | TransformException e) {
								throw new DataCheckException("创建坐标系转换失败", e);
					    	}
						}
					}
					record.setValue(geo);
					info.setGeo(geo);
					geoStr = GeometryUtil.writeGeometry(geo);
					info.setGeoStr(geoStr);
					logger.trace("buildShpInfoVo geo:[{}]", (info.getGeo() == null?null:info.getGeo().getClass()));
				}
			}
		} catch (Throwable e) {
			if(context.getParseGeoDataErrHandler() != null) {
				Throwable throwObj = null;
				if(e instanceof BaseBizException) {
					throwObj = e.getCause();
					if(throwObj == null) {
						throwObj = e;
					}
					ParseGeoDataErrInfo errInfo = new ParseGeoDataErrInfo(throwObj, info, geoStr);
					context.getParseGeoDataErrHandler().accept(errInfo);
				}
			}else {
				if(e instanceof RuntimeException) {
					throw (RuntimeException)e;
				}else {
					throw new FileCheckException("解析shp失败", e);
				}
			}
			
		}
		
		return info;
	}
	
	@SuppressWarnings("unchecked")
	private static String buildGeojson(SimpleFeature feature) {
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		String val = null;
		String geometryPlaceHoder = "${geometry}";
		String geometryJsonStr = null;
		Map<String, Object> map = new LinkedHashMap<>();
		Map<String, Object> propertiesMap = new HashMap<>();
		map.put("type", "Feature");
		List<Property> propertyList = (List<Property>) feature.getValue();
		for (Property property : propertyList) {
			
			if(property.getValue() != null && !Geometry.class.isAssignableFrom(property.getType().getBinding())) {
				if(property.getValue() instanceof Date) {
					val = dateformat.format(property.getValue());
				}else {
					val = String.valueOf(property.getValue());
				}
				propertiesMap.put(property.getName().getLocalPart(), val);
				logger.trace("extractInfoGeoJson name:[{}], value:[{}]",property.getName().getLocalPart(), val);
			}else if(property.getValue() != null && Geometry.class.isAssignableFrom(property.getType().getBinding())){
				Geometry geo = ((Geometry)property.getValue());
				GeometryJSON geometryJson = new GeometryJSON(10);
				StringWriter writer = new StringWriter();
				try {
					geometryJson.write(geo, writer);
				} catch (IOException e) {
					throw new RuntimeException("写入geojson时发生错误", e);
				}
				geometryJsonStr = writer.toString();
				
			}else {
			}
		}
		map.put("geometry", geometryPlaceHoder);
		map.put("properties", propertiesMap);
		
		
		String geoJson = JsonUtil.toJson(map);
		geoJson = geoJson.replace("\"" + geometryPlaceHoder + "\"", geometryJsonStr);
        return geoJson;
	}

}
