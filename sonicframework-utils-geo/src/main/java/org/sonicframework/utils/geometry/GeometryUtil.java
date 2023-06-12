package org.sonicframework.utils.geometry;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonicframework.context.exception.DataCheckException;

/**
* @author lujunyi
*/
public class GeometryUtil {
	
	private static Logger logger  =LoggerFactory.getLogger(GeometryUtil.class);

	private GeometryUtil() {}

	public static String writeGeometry(Geometry geo) {
		if(geo == null) {
			return null;
		}
		WKTWriter wirter = new WKTWriter();
		String result = wirter.write(geo);
		if(geo instanceof MultiPoint) {
			result = result
					.replace("((", "(").replace("))", ")")
					.replace("),(", ",")
					.replace("), (", ",");
		}
		logger.trace("end writeGeometry");
		return result;
	}
	
	public static Geometry readGeometry(String str) {
		if(str == null || "EMPTY".equalsIgnoreCase(str)) {
			return null;
		}
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
        WKTReader reader = new WKTReader( geometryFactory );
        try {
			return reader.read(str);
		} catch (ParseException e) {
			throw new DataCheckException("shape数据不合法", e);
		}
	}
}
