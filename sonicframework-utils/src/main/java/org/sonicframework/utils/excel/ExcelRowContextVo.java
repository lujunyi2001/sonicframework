package org.sonicframework.utils.excel;

import java.util.Map;

/**
* @author lujunyi
*/
public class ExcelRowContextVo {

	/**
	 * 行数据
	 */
	private Map<String, Object> dataMap;
	/**
	 * 页码
	 */
	private int sheetIndex;
	/**
	 * 页名
	 */
	private String sheetName;
	/**
	 * 行号
	 */
	private int rowIndex;
	public ExcelRowContextVo() {
		// TODO Auto-generated constructor stub
	}
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
	public int getSheetIndex() {
		return sheetIndex;
	}
	void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}
	public String getSheetName() {
		return sheetName;
	}
	void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public int getRowIndex() {
		return rowIndex;
	}
	void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

}
