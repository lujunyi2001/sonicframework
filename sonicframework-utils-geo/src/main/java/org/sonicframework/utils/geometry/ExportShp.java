package org.sonicframework.utils.geometry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.dbf.DbfCharsetContextHolder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.utils.PageQuerySupport;
import org.sonicframework.utils.StreamUtil;
import org.sonicframework.utils.file.FileUtil;
import org.sonicframework.utils.file.ZipUtil;
import org.sonicframework.utils.geometry.mapper.GeoMapperContext;
import org.sonicframework.utils.mapper.FieldMapperUtil;
import org.sonicframework.utils.mapper.MapperColumnDesc;
import org.sonicframework.context.exception.DataCheckException;
import org.sonicframework.context.exception.DevelopeCodeException;
import org.sonicframework.context.exception.ExportFailException;

@SuppressWarnings("static-access")
public class ExportShp<T> {

	private File dicPath;
	private Map<String, MapperColumnDesc> fieldNameMap;
	private Map<Class<? extends Geometry>, ExportShp<T>> instanceMap = new HashMap<>();
	private FeatureWriter<SimpleFeatureType, SimpleFeature> writer;
	private String geoKey = null;
	private String fileNamePrefix;
	private File expFile;
	private String charset;
	private GeoMapperContext<T> mapperContext;
	private StringExceedLengthPolicy stringExceedLengthPolicy;
	private ExportErrorPolicy exportErrorPolicy;
	private Consumer<ExportShpDataEvent<T>> exportErrorListener;
	private boolean noDataEmptyShp;
	private boolean multiGeoMerge = true;
	
	private final static String SHAPE_GEO_KEY = "the_geom";
	private final static String SHAPE_FILE_EXTENDNAME = ".shp";
	
	public final static String CHARSET_GBK = "GBK";
	public final static String CHARSET_OEM = "oem";
	
	private static Logger logger = LoggerFactory.getLogger(ExportShp.class);

	static {
//		try {
//			Class.forName("org.geotools.data.shapefile.dbf.DbaseFileHeader");
//		} catch (Throwable e) {
//		}
		try {
			ExportShp.class.getClassLoader().getSystemClassLoader().loadClass("org.geotools.data.shapefile.ShapefileDataStore");
			ExportShp.class.getClassLoader().getSystemClassLoader().loadClass("org.geotools.data.shapefile.ShapefileFeatureSource");
			ExportShp.class.getClassLoader().getSystemClassLoader().loadClass("org.geotools.data.shapefile.dbf.DbaseFileHeader");
		} catch (Throwable e) {
		}
	}
	
	static <T>ExportShp<T> buildEmpty(String path, String fileName, GeoMapperContext<T> mapperContext, String geoKey) {
		return buildEmpty(path, fileName, mapperContext, geoKey, "utf-8");
	}
	static <T>ExportShp<T> buildEmpty(String path, String fileName, GeoMapperContext<T> mapperContext, String geoKey, String charset) {
		ExportShp<T> instance = new ExportShp<>(path, fileName, mapperContext, geoKey);
		instance.charset = charset;
		return instance;
	}
	static ExportShp<Map<String, Object>> buildEmpty(String path, String fileName, Map<String, MapperColumnDesc> fieldNameMap, String geoKey) {
		return buildEmpty(path, fileName, fieldNameMap, geoKey, "utf-8");
	}
	static ExportShp<Map<String, Object>> buildEmpty(String path, String fileName, Map<String, MapperColumnDesc> fieldNameMap, String geoKey, String charset) {
		ExportShp<Map<String, Object>> instance = new ExportShp<Map<String, Object>>(path, fileName, fieldNameMap, geoKey);
		instance.charset = charset;
		return instance;
	}

	private ExportShp() {

	}
	
	private ExportShp(String path, String fileNamePrefix, Map<String, MapperColumnDesc> fieldNameMap, String geoKey) {
		File basePath = new File(path);
		if (basePath.exists() && basePath.isFile()) {
			if (basePath.isFile()) {
				throw new IllegalArgumentException("导出shp文件目录必须为目录");
			}
		} else {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			dicPath = new File(path + "/" + uuid);
			dicPath.mkdirs();
			this.fileNamePrefix = fileNamePrefix;
		}
		this.fieldNameMap = fieldNameMap;
		this.geoKey = geoKey;
	}
	private ExportShp(String path, String fileNamePrefix, GeoMapperContext<T> mapperContext, String geoKey) {
		File basePath = new File(path);
		if (basePath.exists() && basePath.isFile()) {
			if (basePath.isFile()) {
				throw new IllegalArgumentException("导出shp文件目录必须为目录");
			}
		} else {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			dicPath = new File(path + "/" + uuid);
			dicPath.mkdirs();
			this.fileNamePrefix = fileNamePrefix;
		}
		this.mapperContext = mapperContext;
		this.geoKey = geoKey;
	}
	
	public void setStringExceedLengthPolicy(StringExceedLengthPolicy stringExceedLengthPolicy) {
		this.stringExceedLengthPolicy = stringExceedLengthPolicy;
	}
	
	public void setExportErrorPolicy(ExportErrorPolicy exportErrorPolicy) {
		this.exportErrorPolicy = exportErrorPolicy;
	}
	
	public void setExportErrorListener(Consumer<ExportShpDataEvent<T>> exportErrorListener) {
		this.exportErrorListener = exportErrorListener;
	}
	
	public void setNoDataEmptyShp(boolean noDataEmptyShp) {
		this.noDataEmptyShp = noDataEmptyShp;
	}
	public void setMultiGeoMerge(boolean multiGeoMerge) {
		this.multiGeoMerge = multiGeoMerge;
	}

	private synchronized ExportShp<T> buildShp(Class<? extends Geometry> geoClazz) {
		if(geoClazz != null) {
			if(multiGeoMerge) {
				if(geoClazz == Polygon.class) {
					geoClazz = MultiPolygon.class;
				}else if(geoClazz == LineString.class) {
					geoClazz = MultiLineString.class;
				}else  if(geoClazz == Point.class) {
					geoClazz = MultiPoint.class;
				}
			}
		}
		if (instanceMap.containsKey(geoClazz)) {
			return instanceMap.get(geoClazz);
		}
		ExportShp<T> instance = new ExportShp<>();
		instance.dicPath = this.dicPath;
		instance.stringExceedLengthPolicy = this.stringExceedLengthPolicy;
		instance.exportErrorPolicy = this.exportErrorPolicy;
		instance.exportErrorListener = this.exportErrorListener;
		if(this.fieldNameMap == null) {
			this.fieldNameMap = mapperContext.getFieldClassMap();
			instance.fieldNameMap = mapperContext.getFieldClassMap();
		}else {
			instance.fieldNameMap = new HashMap<>(this.fieldNameMap);
		}
		if(!instance.fieldNameMap.containsKey(this.geoKey)) {
			throw new DevelopeCodeException("ExportShp keys not contains '" + this.geoKey + "'");
		}
		instance.fieldNameMap.put(this.geoKey, new MapperColumnDesc(geoClazz));
		instance.fileNamePrefix = this.fileNamePrefix;
		instance.geoKey = this.geoKey;
		instance.charset = this.charset;
		instance.mapperContext = this.mapperContext;
		instance.init(instance.fieldNameMap);

		instanceMap.put(geoClazz, instance);
		return instance;
	}

	private void init(Map<String, MapperColumnDesc> fieldNameMap) {
		DbfCharsetContextHolder.set(charset);
		try {
			String className = this.fieldNameMap.get(this.geoKey) == null ? null
					: this.fieldNameMap.get(this.geoKey).getType().getSimpleName();
			String fileName = this.dicPath + "/" + this.fileNamePrefix + "_" + className + SHAPE_FILE_EXTENDNAME;
			this.expFile = new File(fileName);
			Map<String, Serializable> params = new HashMap<String, Serializable>();
			params.put(ShapefileDataStoreFactory.URLP.key, expFile.toURI().toURL());
			ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
			SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
			CoordinateReferenceSystem crs = this.mapperContext.getCrs();
			tb.setCRS(crs == null?DefaultGeographicCRS.WGS84:crs);
			tb.setName("shapefile");
			MapperColumnDesc desc = null;
			for (Map.Entry<String, MapperColumnDesc> entry : fieldNameMap.entrySet()) {
				desc = entry.getValue();
				if(desc.getType() == String.class && desc.getLength() > 0) {
					tb.length(desc.getLength());
				}else if(Number.class.isAssignableFrom(desc.getType()) || desc.getType() == int.class || 
						desc.getType() == long.class || desc.getType() == short.class || 
						desc.getType() == double.class || desc.getType() == float.class ) {
					if(desc.getLength() > 0) {
						tb.length(desc.getLength());
					}
					if(desc.getScales() > 0) {
						tb.userData(ShapefileDataStore.SONIC_SCALES, desc.getScales());
					}
				}
				tb.add(entry.getKey(), desc.getType());
			}
			ds.createSchema(tb.buildFeatureType());
			ds.setCharset(Charset.forName(charset == null?"UTF-8":charset));
//			ds.setCharset(Charset.forName("UTF-8"));
			writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
		} catch (Exception e) {
			throw new RuntimeException("初始化shp导出出错", e);
		}finally {
			DbfCharsetContextHolder.remove();
		}

	}

	@SuppressWarnings("unchecked")
	private void writeData(Map<String, Object> data, T originData) {
		Class<? extends Geometry> geoClazz;
		if (data.get(this.geoKey) == null) {
			geoClazz = null;
		} else {
			geoClazz = (Class<? extends Geometry>) data.get(this.geoKey).getClass();
		}
		ExportShp<T> shp = buildShp(geoClazz);
		String key = null;
		Object value = null;
		try {
			SimpleFeature feature = shp.writer.next();
			for (Map.Entry<String, MapperColumnDesc> entry : fieldNameMap.entrySet()) {
				key = entry.getKey();
				if (data.get(entry.getKey()) == null) {
					continue;
				}
				value = data.get(entry.getKey());
				if(stringExceedLengthPolicy != null && value instanceof String) {
					value = stringExceedLengthPolicy.renderResult((String) value, this.charset, entry.getValue().getLength());
				}
				if(Objects.equals(this.geoKey, entry.getKey())) {
					feature.setAttribute(SHAPE_GEO_KEY, value);
				}else {
					feature.setAttribute(entry.getKey(), value);
				}
				
			}
			shp.writer.write();
		} catch (Exception e) {
			if(this.exportErrorListener != null) {
				ExportShpDataEvent<T> event = new ExportShpDataEvent<>(originData, key, value, e);
				this.exportErrorListener.accept(event);
			}
			if(this.exportErrorPolicy == null || this.exportErrorPolicy == ExportErrorPolicy.DEFAULT) {
				throw new RuntimeException("写入shp数据出错:key=" + key + "value=" + value + ",value length=" + String.valueOf(value).length(), e);
			}else if(this.exportErrorPolicy == ExportErrorPolicy.SKIP) {
				try {
					shp.writer.remove();
				} catch (IOException e1) {
				}
				if(this.exportErrorListener == null) {
					logger.error("write shp data error,key:[" + key + "], value:[" + value + "], value length:[" + String.valueOf(value).length() + "]", e);
			
				}
			}
		}

	}
	
	/**
	 * 写入一条数据
	 * @param data 实体类
	 */
	@SuppressWarnings("unchecked")
	public void write(T data) {
		if(data == null) {
			throw new DataCheckException("write data is null");
		}
		Map<String, Object> dataMap = null;
		
		if(data instanceof Map) {
			dataMap = ((Map<String, Object>) data);
		}else {
			if(mapperContext == null) {
				throw new DevelopeCodeException("must build ExportShp with MapperContext ");
			}
			dataMap = FieldMapperUtil.buildToFieldDataMap(data, this.mapperContext);
		}
		writeData(dataMap, data);
	}
	
	/**
	 * 通过分页接口实现写入数据
	 * @param pageSupport 分页接口
	 */
	public void writePageData(PageQuerySupport<T> pageSupport) {
		int pages = pageSupport.getPages();
		if(pages == 0) {
			return ;
		}
		List<T> list = null;
		for (int i = 0; i < pages; i++) {
			list = pageSupport.getPageContent(i + 1);
			if(CollectionUtils.isNotEmpty(list)) {
				for (T data : list) {
					write(data);
				}
			}
		}
	}
	
//	public static String subString(String source, int length) {
//
//        StringBuffer buffer = new StringBuffer();
//        char[] chars = source.toCharArray();
//        char c;
//        for (int i = 0;; i++) {
//            if (length <= 0) {
//                break;
//            }
//            c = source.charAt(i);
//            buffer.append(c);
//            length -= String.valueOf(c).getBytes().length;
//            if (i + 1 < chars.length) {
//                if (String.valueOf(chars[i + 1]).getBytes().length > length) {
//                    break;
//                };
//            }
//        }
//        return buffer.toString();
//    }

	/**
	 * 文件写入完成关闭
	 */
	public void close() {
		Collection<ExportShp<T>> values = instanceMap.values();
		if(values.isEmpty() && noDataEmptyShp) {
			writeConstructor();
			values = instanceMap.values();
		}
		File cpgFile = null;
		DbfCharsetContextHolder.set(charset);
		for (ExportShp<T> exportShp : values) {
			try {
				exportShp.writer.close();
				if(StringUtils.isNotBlank(charset)) {
					String path = exportShp.expFile.getPath();
					path = path.substring(0, path.length() - SHAPE_FILE_EXTENDNAME.length()) + ".cpg";
					cpgFile = new File(path);
					try (OutputStream out = new FileOutputStream(cpgFile)){
						if(charset != null && CHARSET_GBK.equalsIgnoreCase(charset)) {
							out.write(CHARSET_OEM.getBytes());
						}else {
							out.write(charset.getBytes());
						}
						
					} catch (Exception e) {
						throw new ExportFailException("导出cpg文件" + path + "失败", e);
					}
				}
			} catch (Exception e) {
				DbfCharsetContextHolder.remove();
				throw new ExportFailException("导出shape文件" + exportShp + "失败", e);
			}
		}
		DbfCharsetContextHolder.remove();
		if(values.size() == 1) {
			ExportShp<T> shp = values.iterator().next();
			File shpDic = shp.expFile.getParentFile();
			File[] listFiles = shpDic.listFiles();
			String newFileName = null;
			File newFile = null;
			for (File file : listFiles) {
				if(file.getName().startsWith(this.fileNamePrefix)) {
					newFileName = shpDic.getPath() + "/" + this.fileNamePrefix + "." + FileUtil.getExtendName(file.getName());
					newFile = new File(newFileName);
					if(newFile.exists()) {
						newFile.delete();
						if(logger.isTraceEnabled()) {
							logger.trace("delete rename file:[{}]", newFile.getPath());
						}
					}
					file.renameTo(new File(newFileName));
				}
			}
		}

	}
	
	private void writeConstructor() {
		FieldMapperUtil.init(this.mapperContext);
		buildShp(Polygon.class);
	}
	
	/**
	 * 删除文件目录
	 */
	public void clean() {
		FileUtil.delete(dicPath);
	}
	
	/**
	 * 获取文件目录
	 * @return 文件目录file
	 */
	public File getDicPath() {
		return dicPath;
	}

	/**
	 * 打包下载文件并删除目录
	 * @param response HttpServletResponse
	 */
	public void downloadZipAndDelete(HttpServletResponse response) {
		try {
			File zip = zip();
			ZipUtil.downloadZip(zip, response);
		} finally{
			clean();
		}
		
	}

	public File zip() {
		FileOutputStream outStream = null;
		ZipOutputStream toClient = null;
		try {
			File[] shpFiles = dicPath.listFiles(t -> !t.getName().toLowerCase().endsWith(".zip"));
			String zipName = fileNamePrefix + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip";
			String outFilePath = dicPath.getAbsolutePath() + "/" + zipName;
			File file = new File(outFilePath);
			// 文件输出流
			outStream = new FileOutputStream(file);
			// 压缩流
			toClient = new ZipOutputStream(outStream);
			ZipUtil.zipFile(new ArrayList<>(Arrays.asList(shpFiles)), toClient);
			toClient.flush();
			return file;
		} catch (Exception e) {
			throw new RuntimeException("打包zip出错", e);
		}finally {
			StreamUtil.close(toClient);
			StreamUtil.close(outStream);
		}
		
		

	}
}


