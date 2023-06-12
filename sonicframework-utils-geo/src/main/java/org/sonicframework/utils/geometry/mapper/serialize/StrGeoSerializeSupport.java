package org.sonicframework.utils.geometry.mapper.serialize;

import org.locationtech.jts.geom.Geometry;
import org.sonicframework.utils.geometry.GeometryUtil;

import org.sonicframework.context.common.annotation.SerializeSupport;

/**
* @author lujunyi
*/
public class StrGeoSerializeSupport implements SerializeSupport<String, Geometry> {

	public StrGeoSerializeSupport() {}

	@Override
	public Geometry serialize(String f) {
		return GeometryUtil.readGeometry(f);
	}

	@Override
	public String deserialize(Geometry t) {
		return GeometryUtil.writeGeometry(t);
	}


}
