package org.sonicframework.utils.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.util.CellRangeAddress;


/**
 * @author lujunyi
 */
public class ColumnVo {
	private String title;
	private String groupName;
	private String[] groupNameSplit;
	private int colspan = 1;
	private int rowspan = 1;
	private boolean cellRowspan = false;
	private ColumnVo rowspanParent = null;
	private String preVal;
	private Integer rowspanStart;
	private Integer rowspanEnd;
	private List<Long> regonList = new ArrayList<>();

	private int maxBytes;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		compareAndSetLen(this.title);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String[] getGroupNameSplit() {
		return groupNameSplit;
	}

	public void setGroupNameSplit(String[] groupNameSplit) {
		this.groupNameSplit = groupNameSplit;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}

	public boolean isCellRowspan() {
		return cellRowspan;
	}

	public void setCellRowspan(boolean cellRowspan) {
		this.cellRowspan = cellRowspan;
	}

	public ColumnVo getRowspanParent() {
		return rowspanParent;
	}

	public void setRowspanParent(ColumnVo rowspanParent) {
		this.rowspanParent = rowspanParent;
	}

	public List<Long> getRegonList() {
		return regonList;
	}

	public int getMaxBytes() {
		return maxBytes;
	}

//	public static ColumnVo build(ExportColumnDto dto) {
//		ColumnVo vo = new ColumnVo();
//		vo.setGroupName(dto.getGroupName());
//		vo.setGroupNameSplit(
//				StringUtils.isNotBlank(dto.getGroupName()) ? dto.getGroupName().split(",") : new String[0]);
//		vo.setTitle(dto.getTitle());
//		vo.setCellRowspan(dto.isRowspan());
//		return vo;
//	}

	public ColumnVo cloneVo() {
		ColumnVo vo = new ColumnVo();
		vo.setColspan(colspan);
		vo.setGroupName(groupName);
		vo.setGroupNameSplit(groupNameSplit);
		vo.setRowspan(rowspan);
		vo.setTitle(title);
		vo.setCellRowspan(cellRowspan);
		vo.setRowspanParent(rowspanParent);
		return vo;
	}

	private void compareAndSetLen(String text) {
		if (text != null) {
			int bytes = 0;
			try {
				bytes = text.getBytes("GBK").length;
			} catch (Exception e) {
				bytes = text.getBytes().length;
			}
			if (bytes > maxBytes) {
				this.maxBytes = bytes;
			}
		}
	}

	private void calcRowspanVal(String val, int rowNum) {
		if (!this.cellRowspan) {
			return;
		}
		if (preVal == null) {
			this.preVal = val;
			this.rowspanStart = rowNum;
			this.rowspanEnd = null;
		} else if (!Objects.equals(preVal, val)) {
			if (this.rowspanEnd != null) {
				addRegon();
			}
			this.preVal = val;
			this.rowspanStart = rowNum;
		} else {
			if (this.rowspanParent != null && this.rowspanParent.rowspanEnd == null && this.rowspanEnd != null) {
				addRegon();
			} else {
				this.rowspanEnd = rowNum;
			}
		}

	}

	public void end() {
		addRegon();
	}

	public List<CellRangeAddress> getRegon(int colNum) {
		List<CellRangeAddress> result = new ArrayList<>();
		for (Long rr : regonList) {
			result.add(buildRegon(rr, colNum));
		}
		return result;
	}

	private CellRangeAddress buildRegon(long rr, int colNum) {
		int mask = 0xFFFFFFFF;
		int rowEnd = (int) (rr & mask);
		int rowStart = (int) ((rr >> 32) & mask);
		return new CellRangeAddress(rowStart, rowEnd, colNum, colNum);
	}

	private void addRegon() {
		if (rowspanEnd == null) {
			return;
		}
		long v = ((long) this.rowspanStart << 32) | this.rowspanEnd;
		regonList.add(v);
		this.preVal = null;
		this.rowspanStart = null;
		this.rowspanEnd = null;
	}

	public void addCalcValue(String val, int rowNum) {
		compareAndSetLen(val);
		calcRowspanVal(val, rowNum);
	}

}