package org.sonicframework.utils.geometry;

/**
* @author lujunyi
*/
public class ParseGeoDataErrInfo {

	private ShpInfoVo info;
	private String geoStr;
	private Throwable cause;

	public ParseGeoDataErrInfo(Throwable cause, ShpInfoVo info, String geoStr) {
		this.cause = cause;
		this.info = info;
		this.geoStr = geoStr;
	}

	public ShpInfoVo getInfo() {
		return info;
	}

	public String getGeoStr() {
		return geoStr;
	}

	public Throwable getCause() {
		return cause;
	}

}
