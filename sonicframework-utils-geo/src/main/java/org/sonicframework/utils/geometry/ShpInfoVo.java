package org.sonicframework.utils.geometry;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ShpInfoVo implements Serializable{

	private static final long serialVersionUID = -2029548182854017830L;
	private List<ShpRecordVo> recordList;
	private String geojson;
	private String geoStr;
	private Geometry geo;
	private String sourceName;
	private CoordinateReferenceSystem coordinateReferenceSystem;
	private int dataIndex;
	
	public ShpInfoVo() {
	}
	public ShpInfoVo(List<ShpRecordVo> recordList, String geojson) {
		super();
		this.recordList = recordList;
		this.geojson = geojson;
	}
	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
	
	/**
	 * 获取dbf中一行的数据
	 * @return dbf中一行的数据
	 */
	public List<ShpRecordVo> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<ShpRecordVo> recordList) {
		this.recordList = recordList;
	}
	public String getGeojson() {
		return geojson;
	}
	public void setGeojson(String geojson) {
		this.geojson = geojson;
	}
	/**
	 * 获取feature中的图形字符串
	 * @return feature中的图形字符串
	 */
	public String getGeoStr() {
		return geoStr;
	}
	public void setGeoStr(String geoStr) {
		this.geoStr = geoStr;
	}
	/**
	 * 获取feature中的图形数据
	 * @return feature中的图形数据
	 */
	public Geometry getGeo() {
		return geo;
	}
	public void setGeo(Geometry geo) {
		this.geo = geo;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public int getDataIndex() {
		return dataIndex;
	}
	public void setDataIndex(int dataIndex) {
		this.dataIndex = dataIndex;
	}
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}
	public void setCoordinateReferenceSystem(CoordinateReferenceSystem coordinateReferenceSystem) {
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}
}
