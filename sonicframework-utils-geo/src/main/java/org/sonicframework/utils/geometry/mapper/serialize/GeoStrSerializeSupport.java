package org.sonicframework.utils.geometry.mapper.serialize;

import org.locationtech.jts.geom.Geometry;
import org.sonicframework.utils.geometry.GeometryUtil;

import org.sonicframework.context.common.annotation.SerializeSupport;

/**
* @author lujunyi
*/
public class GeoStrSerializeSupport implements SerializeSupport<Geometry, String> {

	public GeoStrSerializeSupport() {}

	@Override
	public String serialize(Geometry t) {
		return GeometryUtil.writeGeometry(t);
	}

	@Override
	public Geometry deserialize(String f) {
		return GeometryUtil.readGeometry(f);
	}

}
