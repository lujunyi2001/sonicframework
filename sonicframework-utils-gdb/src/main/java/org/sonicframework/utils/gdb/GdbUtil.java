package org.sonicframework.utils.gdb;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.MultiSurface;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonicframework.context.exception.BaseBizException;
import org.sonicframework.context.exception.DataCheckException;
import org.sonicframework.context.exception.DataNotValidException;
import org.sonicframework.context.exception.FileCheckException;
import org.sonicframework.utils.ConsumerImpEntity;
import org.sonicframework.utils.ValidateResult;
import org.sonicframework.utils.ValidationUtil;
import org.sonicframework.utils.geometry.GeometryUtil;
import org.sonicframework.utils.geometry.ParseGeoDataErrInfo;
import org.sonicframework.utils.geometry.ShpInfoVo;
import org.sonicframework.utils.geometry.ShpRecordVo;
import org.sonicframework.utils.geometry.mapper.GeoFieldMapperUtil;
import org.sonicframework.utils.geometry.mapper.GeoMapperContext;
import org.sonicframework.utils.mapper.MapperContext;
import org.sonicframework.utils.mapper.PostMapper;

/**
 * @author lujunyi
 */
public class GdbUtil {

	private static Logger logger = LoggerFactory.getLogger(GdbUtil.class);

	static {
		gdal.AllRegister();
		ogr.RegisterAll();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8","YES");

        gdal.SetConfigOption("SHAPE_ENCODING","CP936");
	}

	private GdbUtil() {
	}

	/**
	 * 解析shape文件成实体类
	 * 
	 * @param pathName 解析文件目录或文件名(传入目录解析目录下所有shp文件)
	 * @param context  字段映射上下文
	 * @param consumer 生成实体类消费者
	 */
	public static <T> void extractInfoEntity(String pathName, MapperContext<T> context,
			ConsumerImpEntity<T, ValidateResult> consumer) {
		extractInfoEntity(pathName, context, consumer, null);
	}

	/**
	 * 解析shape文件成实体类
	 * 
	 * @param pathName   解析文件目录或文件名(传入目录解析目录下所有shp文件)
	 * @param context    字段映射上下文
	 * @param consumer   生成实体类消费者
	 * @param postMapper 映射字段后置方法
	 */
	public static <T> void extractInfoEntity(String pathName, MapperContext<T> context,
			ConsumerImpEntity<T, ValidateResult> consumer, PostMapper<T, ShpInfoVo> postMapper) {
		boolean validEnable = context.isValidEnable();
		Class<?>[] validGroups = context.getValidGroups();
		context.setValidEnable(false, validGroups);
		String layerName = context.getMapperName();
		GeoMapperContext<T> geoMapperContext = null;
		if(context != null && context instanceof GeoMapperContext) {
			geoMapperContext = ((GeoMapperContext<T>)context);
		}
		extractInfo(pathName, layerName, t -> {
			T entity = GeoFieldMapperUtil.importMapper(t, context, postMapper);
			ValidateResult validateResult = null;
			if (validEnable) {
				try {
					ValidationUtil.checkValid(entity, validGroups);
					validateResult = new ValidateResult(true, null);
				} catch (DataNotValidException e) {
					validateResult = new ValidateResult(false, e.getMessage());
				}
			} else {
				validateResult = new ValidateResult(true, null);
			}
			consumer.execute(entity, validateResult);

		}, geoMapperContext);
		context.setValidEnable(validEnable, validGroups);
	}

	public static void extractInfo(String pathName, String layerName, Consumer<ShpInfoVo> consumer) {
		extractInfo(pathName, layerName, consumer, null);
	}
	public static <T>void extractInfo(String pathName, String layerName, Consumer<ShpInfoVo> consumer, GeoMapperContext<T> geoMapperContext) {
		File basePath = new File(pathName);
		checkFile(basePath);

		Consumer<LayerFeatureContextVo> featureConsumer = t -> {
			ShpInfoVo infoVo = buildShpInfoVo(t, geoMapperContext);
			consumer.accept(infoVo);
		};
		extractOneGdbFile(basePath, layerName, featureConsumer);
	}

	private static void checkFile(File file) {
		if (!file.exists()) {
			throw new FileCheckException("没有找到文件" + file.getPath());
		}
//		if(file.isFile() && !file.getName().toLowerCase().endsWith(SHAPEFILE_EXTEND_NAME)) {
//			throw new FileCheckException("不合法的shape文件扩展名" + file.getPath());
//		}
	}

	private static <T>ShpInfoVo buildShpInfoVo(LayerFeatureContextVo layerContextVo, GeoMapperContext<T> geoMapperContext) {
		ShpInfoVo info = new ShpInfoVo();
		info.setSourceName(layerContextVo.getLayer().GetName());
		info.setDataIndex(layerContextVo.getFeatureDataIndex());
		info.setCoordinateReferenceSystem(layerContextVo.getCoordinateReferenceSystem());
		List<ShpRecordVo> recordList = new ArrayList<>();
		info.setRecordList(recordList);

		Feature feature = layerContextVo.getFeature();
		int fieldCount = feature.GetFieldCount();
		FieldDefn fieldDefnRef = null;
		ShpRecordVo record = null;
		String exportToWkt = null;
		try {
			for (int i = 0; i < fieldCount; i++) {
				fieldDefnRef = feature.GetFieldDefnRef(i);
				record = new ShpRecordVo(fieldDefnRef.GetName(), getProperty(feature, i), getPropertyType(feature, i));
				recordList.add(record);
			}
			exportToWkt = feature.GetGeometryRef().ExportToWkt();
			if(StringUtils.isNotBlank(exportToWkt)) {
				Geometry geo = GeometryUtil.readGeometry(exportToWkt);
				if(geoMapperContext != null && geoMapperContext.getCrs() != null) {
					if(info.getCoordinateReferenceSystem() == null) {
						throw new FileCheckException("没有找到坐标系文件");
					}
					if(!Objects.equals(info.getCoordinateReferenceSystem(), geoMapperContext.getCrs())) {
						try {
							MathTransform mt = CRS.findMathTransform(info.getCoordinateReferenceSystem(), geoMapperContext.getCrs(), true);
							if(geo instanceof MultiSurface) {
								String cloneStr = GeometryUtil.writeGeometry(geo);
								geo = GeometryUtil.readGeometry(cloneStr);
							}
							geo = JTS.transform(geo, mt);
				    	} catch (FactoryException e) {
							throw new DataCheckException("创建坐标系转换失败", e);
						} catch (MismatchedDimensionException | TransformException e) {
							throw new DataCheckException("创建坐标系转换失败", e);
				    	}
					}
					
				}
				info.setGeo(geo);
				info.setGeoStr(GeometryUtil.writeGeometry(geo));
			}
		} catch (Throwable e) {
			if(geoMapperContext != null && geoMapperContext.getParseGeoDataErrHandler() != null) {
				Throwable throwObj = null;
				if(e instanceof BaseBizException) {
					throwObj = e.getCause();
					if(throwObj == null) {
						throwObj = e;
					}
					ParseGeoDataErrInfo errInfo = new ParseGeoDataErrInfo(throwObj, info, exportToWkt);
					geoMapperContext.getParseGeoDataErrHandler().accept(errInfo);
				}
			}else {
				if(e instanceof RuntimeException) {
					throw (RuntimeException)e;
				}else {
					throw new FileCheckException("解析gdb失败", e);
				}
			}
		}
		
		
		return info;
	}

	private static void extractOneGdbFile(File file, String layerName, Consumer<LayerFeatureContextVo> consumer) {
		String sourceName = file.getName();
		DataSource dataSource = null;
		Driver driver = null;
		try {
			driver = ogr.GetDriverByName("OpenFileGDB");
			dataSource = driver.Open(file.getPath(), 0);
			int count = dataSource.GetLayerCount();
			Feature feature = null;
			Layer layer = null;
			LayerFeatureContextVo vo = null;
			CoordinateReferenceSystem coordinateReferenceSystem = null;
			for (int i = 0; i < count; i++) {
				layer = dataSource.GetLayer(i);
				if(StringUtils.isNotBlank(layerName) && !Objects.equals(layerName, layer.GetName())) {
					continue;
				}
				if(layer.GetSpatialRef() != null) {
					coordinateReferenceSystem = CRS.parseWKT(layer.GetSpatialRef().toString());
				}
				int dataIndex = 0;
				while ((feature = layer.GetNextFeature()) != null) {
					vo = new LayerFeatureContextVo(layer, i, feature, dataIndex, sourceName, coordinateReferenceSystem);
					logger.trace("extractInfo file:[{}] layer index:[{}] name:[{}], feature index:[{}]", file, i,
							layer.GetName(), dataIndex);
					consumer.accept(vo);
					dataIndex++;
				}

			}

		} catch (BaseBizException e) {
			throw e;
		} catch (Exception e) {
			throw new FileCheckException(FileCheckException.MESSAGE, e);
		} finally {
			if(dataSource != null){
                dataSource.delete();
            }
			if(driver != null){
				driver.delete();
			}
		}
	}

	private static Class<?> getPropertyType(Feature feature, int index) {
		int type = feature.GetFieldType(index);
		Class<?> propertyType;
		if (type < 0 || type >= propertyGetters.length) {
			propertyType = String.class;
		} else {
			propertyType = propertyTypeGetters[type];
		}
		return propertyType;
	}
	private static Object getProperty(Feature feature, int index) {
		int type = feature.GetFieldType(index);
		PropertyGetter propertyGetter;
		if (type < 0 || type >= propertyGetters.length) {
			propertyGetter = stringPropertyGetter;
		} else {
			propertyGetter = propertyGetters[type];
		}
		try {
			return propertyGetter.get(feature, index);
		} catch (Exception e) {
			throw new DataCheckException("获取gdb字段值index:" + index + "失败, type" + type, e);
		}
	}

	private static final PropertyGetter stringPropertyGetter = (feature, index) -> feature.GetFieldAsString(index);

	/**
	 * feature.GetFieldType(index)得到一个属性类型的int值,该值对应具体类型
	 */
	private static final PropertyGetter[] propertyGetters = new PropertyGetter[] {
			(feature, index) -> feature.GetFieldAsInteger(index), // 0 Integer
			(feature, index) -> feature.GetFieldAsIntegerList(index), // 1 IntegerList
			(feature, index) -> feature.GetFieldAsDouble(index), // 2 Real
			(feature, index) -> feature.GetFieldAsDoubleList(index), // 3 RealList
			stringPropertyGetter, // 4 String
			(feature, index) -> feature.GetFieldAsStringList(index), // 5 StringList
			stringPropertyGetter, // 6 (unknown)
			stringPropertyGetter, // 7 (unknown)
			(feature, index) -> feature.GetFieldAsBinary(index), // 8 Binary
			(feature, index) -> {
                int[] pnYear = new int[1];
                int[] pnMonth = new int[1];
                int[] pnDay = new int[1];
                int[] pnHour = new int[1];
                int[] pnMinute = new int[1];
                float[] pfSecond = new float[1];
                int[] pnTZFlag = new int[1];
                feature.GetFieldAsDateTime(index, pnYear, pnMonth, pnDay, pnHour, pnMinute, pfSecond, pnTZFlag);
                Date date = Date.valueOf(LocalDate.of(pnYear[0], pnMonth[0], pnDay[0]));
                return date;
            },//9	Date
            (feature, index) -> {
                int[] pnYear = new int[1];
                int[] pnMonth = new int[1];
                int[] pnDay = new int[1];
                int[] pnHour = new int[1];
                int[] pnMinute = new int[1];
                float[] pfSecond = new float[1];
                int[] pnTZFlag = new int[1];
                feature.GetFieldAsDateTime(index, pnYear, pnMonth, pnDay, pnHour, pnMinute, pfSecond, pnTZFlag);
                float fSecond = pfSecond[0];
                int s = (int) fSecond;
                int ns = (int) (1000000000 * fSecond - s);
                ns = 0;
                Time time = Time.valueOf(LocalTime.of(pnHour[0], pnMinute[0], s, ns));
                return time;
            },// 10	Time
            (feature, index) -> {
                int[] pnYear = new int[1];
                int[] pnMonth = new int[1];
                int[] pnDay = new int[1];
                int[] pnHour = new int[1];
                int[] pnMinute = new int[1];
                float[] pfSecond = new float[1];
                int[] pnTZFlag = new int[1];
                feature.GetFieldAsDateTime(index, pnYear, pnMonth, pnDay, pnHour, pnMinute, pfSecond, pnTZFlag);
                float fSecond = pfSecond[0];
                int s = (int) fSecond;
                int ns = (int) (1000000000 * fSecond - s);
                ns = 0;
                if(pnYear[0] != 0 && pnMonth[0] != 0 && pnDay[0] != 0){
                    LocalDateTime localDateTime = LocalDateTime.of(
                            LocalDate.of(pnYear[0], pnMonth[0], pnDay[0]),
                            LocalTime.of(pnHour[0], pnMinute[0], s, ns)
                    );
                    Timestamp timestamp = Timestamp.valueOf(localDateTime);
                    return timestamp;
                }
                return null;
            },//11	DateTime
			(feature, index) -> feature.GetFieldAsInteger64(index), // 12 Integer64
			(feature, index) -> feature.GetFieldAsIntegerList(index),// 13 Integer64List
			// >=14 (unknown)
	};
	
	private static final Class<?>[] propertyTypeGetters = new Class<?>[] {
			Integer.class, int[].class, Double.class, double[].class, String.class, 
			String[].class, String.class, String.class, byte[].class, Date.class, 
			Date.class, Timestamp.class, Long.class, long[].class
	};
	
	public static <T>ExportGdb<T> buildNewExport(String path, int srid, String charset) {
		return ExportGdb.buildEmpty(path, srid, charset);
	}
}
