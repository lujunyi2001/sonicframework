package org.sonicframework.utils.gdb;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonicframework.context.exception.DataCheckException;
import org.sonicframework.context.exception.DataNotValidException;
import org.sonicframework.context.exception.DevelopeCodeException;
import org.sonicframework.utils.PageQuerySupport;
import org.sonicframework.utils.StreamUtil;
import org.sonicframework.utils.file.FileUtil;
import org.sonicframework.utils.file.ZipUtil;
import org.sonicframework.utils.geometry.ExportErrorPolicy;
import org.sonicframework.utils.geometry.ExportShpDataEvent;
import org.sonicframework.utils.geometry.GeometryUtil;
import org.sonicframework.utils.mapper.FieldMapperUtil;
import org.sonicframework.utils.mapper.MapperContext;

/**
 * @author lujunyi
 */
public class ExportGdb<T> {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(ExportGdb.class);

	
	private File dicPath;
	private int srid;
	private ExportGdb<?> top;
	@SuppressWarnings("unused")
	private String charset;
	private Map<String, ExportGdb<?>> instanceMap = new HashMap<>();
	private DataSource dataSourceGDB;
	
	static <T> ExportGdb<T> buildEmpty(String path, int srid) {
		return buildEmpty(path, srid, "utf-8");
	}

	static <T> ExportGdb<T> buildEmpty(String path, int srid, String charset) {
		ExportGdb<T> instance = new ExportGdb<>(path, srid);
		instance.charset = charset;
		return instance;
	}
	
	private ExportGdb(String path, int srid) {
		File basePath = new File(path);
		if (basePath.exists() && basePath.isFile()) {
			if (basePath.isFile()) {
				throw new IllegalArgumentException("导出shp文件目录必须为目录");
			}
		} else {
//			String uuid = UUID.randomUUID().toString().replace("-", "");
			dicPath = new File(path);
//			dicPath.mkdirs();
		}
		this.srid = srid;
	}
	
	private String layerName;
	private MapperContext<?> mapperContext;
	private Layer gdbLayer;
	private String geoKey = null;
	private Map<String, Class<?>> fieldNameMap;
	
	
	public <L>ExportGdb<L> createNewLayer(String layerName, MapperContext<L> mapperContext, String geoKey){
		return new ExportGdb<>(this, layerName, mapperContext, geoKey);
	}
	public <L>ExportGdb<L> createNewLayer(MapperContext<L> mapperContext, String geoKey){
		return new ExportGdb<>(this, mapperContext.getMapperName(), mapperContext, geoKey);
	}
	
	private <L>ExportGdb(ExportGdb<?> top, String layerName, MapperContext<L> mapperContext, String geoKey) {
		if(StringUtils.isBlank(layerName)) {
			throw new DevelopeCodeException("图层名不能为空");
		}
		this.top = top;
		this.layerName = layerName;
		this.top.instanceMap.put(layerName, this);
		this.mapperContext = mapperContext;
		this.geoKey = geoKey;
		this.fieldNameMap = mapperContext.getFieldClassMap();
	}
	
	
	

	
	private ExportErrorPolicy exportErrorPolicy;
	private Consumer<ExportShpDataEvent<?>> exportErrorListener;
	private boolean noDataEmptyGdb;


	private DataSource getDatasource() {
		ExportGdb<?> topInstance = getTopInstance();
		synchronized (topInstance) {
			if(topInstance.dataSourceGDB != null) {
				return topInstance.dataSourceGDB;
			}
			try {
				/******************* 输出gdb图层、表 ********************/
				Driver driverOut = ogr.GetDriverByName("FileGDB");
				topInstance.dataSourceGDB = driverOut.CreateDataSource(topInstance.dicPath.getPath());
				if(topInstance.dataSourceGDB == null) {
					throw new DataNotValidException("创建gdb数据源失败");
				}
				return topInstance.dataSourceGDB;
			} catch (Exception e) {
				throw new DevelopeCodeException("初始化gdb创建datasource出错", e);
			}
		}
	}
	
	private ExportGdb<?> getTopInstance(){
		ExportGdb<?> topInstance = this;
		if(this.top != null) {
			topInstance = this.top;
		}
		return topInstance;
	}
	
	private int getSrid() {
		return getTopInstance().srid;
	}

	private void init(Map<String, Class<?>> fieldNameMap) {
		if(this.top == null) {
			return;
		}
		try {
			
			DataSource ds = getDatasource();
			/******************* 定义坐标系 ********************/
			SpatialReference sr = new SpatialReference();
			sr.ImportFromEPSG(this.getSrid());
			/******************* 输出gdb图层、表 ********************/
			// gdb图层
			int geoType = ogr.wkbPolygon;
			Class<?> geoClass = this.fieldNameMap.get(this.geoKey);
			if (Polygon.class.isAssignableFrom(geoClass) || MultiPolygon.class.isAssignableFrom(geoClass)) {
				geoType = ogr.wkbPolygon;
			} else if (LineString.class.isAssignableFrom(geoClass)
					|| MultiLineString.class.isAssignableFrom(geoClass)) {
				geoType = ogr.wkbLineString;
			} else if (Point.class.isAssignableFrom(geoClass) || MultiPoint.class.isAssignableFrom(geoClass)) {
				geoType = ogr.wkbPoint;
			} else {
				throw new DataCheckException("未知geometry类型" + geoClass);
			}
			String layName = this.layerName;
			if(StringUtils.isBlank(layName)) {
				layName = this.mapperContext.getMapperName();
			}
			if(StringUtils.isBlank(layName)) {
				layName = this.mapperContext.getClazz().getName();
			}
			Layer gdbLayer = ds.CreateLayer(layName, sr, geoType, null);

			for (Map.Entry<String, Class<?>> entry : fieldNameMap.entrySet()) {
				if (Objects.equals(this.geoKey, entry.getKey())) {
					continue;
				}
				FieldDefn fieldDefn = new FieldDefn();
				fieldDefn.SetName(entry.getKey());
//				fieldDefn.SetNullable(0);
				fieldDefn.SetType(getFieldType(entry.getValue()));
				gdbLayer.CreateField(fieldDefn);
				this.gdbLayer = gdbLayer;
			}

		} catch (Exception e) {
			throw new DevelopeCodeException("初始化gdb导出出错", e);
		} finally {

		}

	}

	private int getFieldType(Class<?> clazz) {

		if (clazz == String.class) {
			return ogr.OFTString;
		} else if (Date.class.isAssignableFrom(clazz)) {
			return ogr.OFTDateTime;
		} else if (clazz == Integer.class || clazz == int.class) {
			return ogr.OFTInteger;
		} else if (clazz == Long.class || clazz == long.class) {
			return ogr.OFTInteger64;
		} else if (clazz == Float.class || clazz == float.class) {
			return ogr.OFTReal;
		} else if (clazz == Double.class || clazz == double.class) {
			return ogr.OFTReal;
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			return ogr.OFSTBoolean;
		} else {
			throw new DevelopeCodeException("不能识别的字段类型" + clazz);
		}
	}

	private <D>void writeData(Map<String, Object> data, D originData) {
		if(this.top == null) {
			throw new DevelopeCodeException("不能在空ExportGdb中添加数据");
		}
		Class<? extends Geometry> geoClazz;
		Object geoValue = data.get(this.geoKey);
		String geoStr = null;
		Geometry geo = null;
		if (geoValue != null && geoValue instanceof Geometry) {
			geo = (Geometry) geoValue;
			geoStr = GeometryUtil.writeGeometry((Geometry) geoValue);
		} else if (geoValue != null && geoValue instanceof String) {
			geo = GeometryUtil.readGeometry((String) geoValue);
			geoStr = (String) geoValue;
		}
		if (geo == null) {
			throw new DataCheckException("未找到Geometry类型的值");
		}
		geoClazz = (Class<? extends Geometry>) geo.getClass();
		org.gdal.ogr.Geometry gdbGeo = org.gdal.ogr.Geometry.CreateFromWkt(geoStr);
		if (this.gdbLayer == null) {
			fieldNameMap.put(this.geoKey, geoClazz);
			init(fieldNameMap);
		}
		String key = null;
		Object value = null;
		try {
			FeatureDefn featureDefn = this.gdbLayer.GetLayerDefn();
			Feature feature = new Feature(featureDefn);
			for (Map.Entry<String, Class<?>> entry : fieldNameMap.entrySet()) {
				key = entry.getKey();
				if (data.get(entry.getKey()) == null) {
					continue;
				}
				value = data.get(entry.getKey());
				if (Objects.equals(this.geoKey, entry.getKey())) {
					feature.SetGeometry(gdbGeo);
				} else if (value == null) {
					feature.SetFieldNull(entry.getKey());
				} else {
					Class<? extends Object> valueClass = value.getClass();
					if (valueClass == String.class) {
						feature.SetField(entry.getKey(), (String) value);
					} else if (Date.class.isAssignableFrom(valueClass)) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime((Date) value);
						int year = calendar.get(Calendar.YEAR);
						int month = calendar.get(Calendar.MONTH) + 1;
						int day = calendar.get(Calendar.DATE);
						int hour = calendar.get(Calendar.HOUR_OF_DAY);
						int minute = calendar.get(Calendar.MINUTE);
						float second = calendar.get(Calendar.SECOND);
						feature.SetField(entry.getKey(), year, month, day, hour, minute, second, 0);
					} else if (valueClass == Integer.class || valueClass == int.class) {
						feature.SetField(entry.getKey(), new Integer((int) value).intValue());
					} else if (valueClass == Long.class || valueClass == long.class) {
						feature.SetField(entry.getKey(), new Long((long) value).longValue());
					} else if (valueClass == Float.class || valueClass == float.class) {
						feature.SetField(entry.getKey(), new Double((float) value).doubleValue());
					} else if (valueClass == Double.class || valueClass == double.class) {
						feature.SetField(entry.getKey(), new Double((double) value).doubleValue());
					} else if (valueClass == Boolean.class || valueClass == boolean.class) {
						feature.SetField(entry.getKey(), String.valueOf(value));
					} else {
						throw new DataCheckException("不能识别的字段类型" + valueClass);
					}
				}

			}
			gdbLayer.CreateFeature(feature);
		} catch (Exception e) {
			if (this.exportErrorListener != null) {
				ExportShpDataEvent<D> event = new ExportShpDataEvent<>(originData, key, value, e);
				this.exportErrorListener.accept(event);
			}
			if (this.exportErrorPolicy == null || this.exportErrorPolicy == ExportErrorPolicy.DEFAULT) {
				throw new RuntimeException(
						"写入shp数据出错:key=" + key + "value=" + value + ",value length=" + String.valueOf(value).length(),
						e);
			}
		}

	}

	/**
	 * 写入一条数据
	 * 
	 * @param data 实体类
	 */
	@SuppressWarnings("unchecked")
	public <D>void write(D data) {
		if (data == null) {
			throw new DataCheckException("write data is null");
		}
		Map<String, Object> dataMap = null;

		if (data instanceof Map) {
			dataMap = ((Map<String, Object>) data);
		} else {
			if (mapperContext == null) {
				throw new DevelopeCodeException("must build ExportShp with MapperContext ");
			}
			dataMap = FieldMapperUtil.buildToFieldDataMap(data, this.mapperContext);
			if(this.fieldNameMap == null) {
				this.fieldNameMap = mapperContext.getFieldClassMap();
			}
		}
		writeData(dataMap, data);
	}

	/**
	 * 通过分页接口实现写入数据
	 * 
	 * @param pageSupport 分页接口
	 */
	public <D>void writePageData(PageQuerySupport<D> pageSupport) {
		int pages = pageSupport.getPages();
		if (pages == 0) {
			return;
		}
		List<D> list = null;
		for (int i = 0; i < pages; i++) {
			list = pageSupport.getPageContent(i + 1);
			if (CollectionUtils.isNotEmpty(list)) {
				for (D data : list) {
					write(data);
				}
			}
		}
	}
	
	/**
	 * 文件写入完成关闭
	 */
	public void close() {
		ExportGdb<?> topInstance = getTopInstance();
		Set<Entry<String,ExportGdb<?>>> entrySet = topInstance.instanceMap.entrySet();
		ExportGdb<?> gdb = null;
		for (Entry<String, ExportGdb<?>> entry : entrySet) {
			gdb = entry.getValue();
			if(gdb.gdbLayer == null && topInstance.noDataEmptyGdb) {
				gdb.fieldNameMap.put(gdb.geoKey, Polygon.class);
				gdb.init(fieldNameMap);
			}
		}
		DataSource ds = getDatasource();
		ds.SyncToDisk();
		ds.FlushCache();
		ds.delete();
	}

	public <D>void setExportErrorListener(Consumer<ExportShpDataEvent<?>> exportErrorListener) {
		this.exportErrorListener = exportErrorListener;
	}

	public void setNoDataEmptyGdb(boolean noDataEmptyGdb) {
		ExportGdb<?> topInstance = getTopInstance();
		topInstance.noDataEmptyGdb = noDataEmptyGdb;
	}

	public void setExportErrorPolicy(ExportErrorPolicy exportErrorPolicy) {
		this.exportErrorPolicy = exportErrorPolicy;
	}
	
	/**
	 * 获取文件目录
	 * @return 文件目录file
	 */
	public File getDicPath() {
		ExportGdb<?> topInstance = getTopInstance();
		return topInstance.dicPath;
	}
	
	/**
	 * 打包下载文件并删除目录
	 * @param response HttpServletResponse
	 */
	public void downloadZipAndDelete(HttpServletResponse response) {
		File zip = null;
		try {
			zip = zip();
			ZipUtil.downloadZip(zip, response);
		} finally{
			clean();
			if(zip != null && zip.exists()){
				FileUtil.delete(zip);
			}
		}
		
	}

	public File zip() {
		ExportGdb<?> topInstance = getTopInstance();
		FileOutputStream outStream = null;
		ZipOutputStream toClient = null;
		try {
			String zipName = topInstance.dicPath.getName();
			if(zipName.lastIndexOf(".") > -1) {
				zipName = zipName.substring(0, zipName.lastIndexOf(".")) + ".zip";
			}else {
				zipName += ".zip";
			}
			String outFilePath = topInstance.dicPath.getParent() + "/" + zipName;
			File file = new File(outFilePath);
			// 文件输出流
			outStream = new FileOutputStream(file);
			// 压缩流
			toClient = new ZipOutputStream(outStream);
			ZipUtil.zipFile(new ArrayList<>(Arrays.asList(topInstance.dicPath)), toClient);
			toClient.flush();
			return file;
		} catch (Exception e) {
			throw new RuntimeException("打包zip出错", e);
		}finally {
			StreamUtil.close(toClient);
			StreamUtil.close(outStream);
		}
		
		

	}
	
	/**
	 * 删除文件目录
	 */
	public void clean() {
		FileUtil.delete(dicPath);
	}

}
