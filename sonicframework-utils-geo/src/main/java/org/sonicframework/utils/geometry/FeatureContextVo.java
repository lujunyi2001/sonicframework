package org.sonicframework.utils.geometry;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class FeatureContextVo implements Serializable{

	private static final long serialVersionUID = -2029548182854017830L;
	private SimpleFeature feature;
	private String sourceName;
	private int dataIndex;
	private CoordinateReferenceSystem coordinateReferenceSystem;
	public FeatureContextVo(SimpleFeature feature, String sourceName, CoordinateReferenceSystem coordinateReferenceSystem, int dataIndex) {
		super();
		this.feature = feature;
		this.sourceName = sourceName;
		this.coordinateReferenceSystem = coordinateReferenceSystem;
		this.dataIndex = dataIndex;
	}
	public SimpleFeature getFeature() {
		return feature;
	}
	public String getSourceName() {
		return sourceName;
	}
	public int getDataIndex() {
		return dataIndex;
	}
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}
}
