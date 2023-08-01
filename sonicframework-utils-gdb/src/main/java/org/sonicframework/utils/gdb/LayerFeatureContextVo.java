package org.sonicframework.utils.gdb;

import java.io.Serializable;

import org.gdal.ogr.Feature;
import org.gdal.ogr.Layer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class LayerFeatureContextVo implements Serializable{

	private static final long serialVersionUID = -2029548182854017830L;
	private Layer layer;
	private Feature feature;
	private String sourceName;
	private int layerDataIndex;
	private int featureDataIndex;
	private CoordinateReferenceSystem coordinateReferenceSystem;
	public LayerFeatureContextVo(Layer layer, int layerDataIndex, Feature feature, int featureDataIndex,  String sourceName, CoordinateReferenceSystem coordinateReferenceSystem) {
		super();
		this.layer = layer;
		this.feature = feature;
		this.sourceName = sourceName;
		this.coordinateReferenceSystem = coordinateReferenceSystem;
		this.layerDataIndex = layerDataIndex;
		this.featureDataIndex = featureDataIndex;
	}
	public Layer getLayer() {
		return layer;
	}
	public String getSourceName() {
		return sourceName;
	}
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}
	public Feature getFeature() {
		return feature;
	}
	public int getLayerDataIndex() {
		return layerDataIndex;
	}
	public int getFeatureDataIndex() {
		return featureDataIndex;
	}
}
