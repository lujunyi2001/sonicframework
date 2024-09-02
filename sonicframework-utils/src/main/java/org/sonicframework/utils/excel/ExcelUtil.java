package org.sonicframework.utils.excel;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.utils.ConsumerImpEntity;
import org.sonicframework.utils.ConvertFactory;
import org.sonicframework.utils.PageQuerySupport;
import org.sonicframework.utils.ValidateResult;
import org.sonicframework.utils.ValidationUtil;
import org.sonicframework.utils.beans.BeanWrapperImpl;
import org.sonicframework.utils.mapper.FieldMapperUtil;
import org.sonicframework.utils.mapper.MapperContext;
import org.sonicframework.utils.mapper.MapperDescVo;
import org.sonicframework.utils.mapper.PostMapper;
import org.springframework.beans.BeanWrapper;

import org.sonicframework.context.common.annotation.Style;
import org.sonicframework.context.common.enums.Border;
import org.sonicframework.context.exception.DataNotValidException;
import org.sonicframework.context.exception.DevelopeCodeException;
import org.sonicframework.context.exception.ExportFailException;
import org.sonicframework.context.exception.FileCheckException;

/**
 * @author lujunyi
 */
public class ExcelUtil {

	private final static int WORKBOOK_BUFFER_MAX_ROWS = 200;

	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
	
	private static ThreadLocal<Boolean> AUTO_SIZE_COLUMN = new ThreadLocal<>();

	private ExcelUtil() {
	}

	public static SXSSFWorkbook createWorkbook() {
		return new SXSSFWorkbook(WORKBOOK_BUFFER_MAX_ROWS);
	}

	public static SXSSFSheet createEmptySheet() {
		return createEmptySheet(null);
	}

	public static SXSSFSheet createEmptySheet(SXSSFWorkbook workbook) {
		SXSSFWorkbook wb = workbook == null ? createWorkbook() : workbook;
		return wb.createSheet();
	}

	public static Object getJavaValue(Cell cell) {
		Object o = null;
		final CellType cellType = cell.getCellTypeEnum();
		switch (cellType) {
		case BLANK:
			o = "";
			break;
		case BOOLEAN:
			o = cell.getBooleanCellValue();
			break;
		case ERROR:
			o = null;
			break;
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				// 获取日期类型的单元格的值
				return cell.getDateCellValue();
			}
			double d = cell.getNumericCellValue();
			NumberFormat nf = NumberFormat.getInstance();
			// 设置保留多少位小数
			nf.setMaximumFractionDigits(20);
			// 取消科学计数法
			nf.setGroupingUsed(false);
			String v = nf.format(d);
			if (v.endsWith(".0")) {
				v = v.substring(0, v.length() - 2);
			}
			o = v;
			break;
		case FORMULA:
			try {
				o = String.valueOf(cell.getNumericCellValue());
			} catch (IllegalStateException e) {
				o = cell.getRichStringCellValue().toString();
			}
			break;
		default:
			o = cell.getRichStringCellValue().getString();
		}
		return o;
	}
	
	public static void setExportAutoSizeColumn(boolean autoSize) {
		AUTO_SIZE_COLUMN.set(autoSize);
	}

	/**
	 * excel导出
	 * @param context 字段映射上下文
	 * @param pageSupport 分析接口
	 * @return
	 */
	public static <T> Sheet export(MapperContext<T> context, PageQuerySupport<T> pageSupport) {
		return export(null, context, pageSupport);
	}

	/**
	 * excel导出
	 * @param <T>
	 * @param sheet sheet页
	 * @param context 字段映射上下文
	 * @param pageSupport 分析接口
	 * @return
	 */
	public static <T> Sheet export(Sheet sheet, MapperContext<T> context, PageQuerySupport<T> pageSupport) {
		Boolean autoSize = AUTO_SIZE_COLUMN.get();
		AUTO_SIZE_COLUMN.remove();
		if(autoSize == null) {
			autoSize = false;
		}
		
		if (sheet == null) {
			sheet = createEmptySheet(createWorkbook());
			if (sheet instanceof SXSSFSheet) {
				((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
			}
			sheet.autoSizeColumn(1);

			sheet.autoSizeColumn(1, true);
		}
		List<MapperDescVo> descList = FieldMapperUtil.parseDescByGroups(context.getClazz(), context.getGroups());
		
		int max = descList.stream().mapToInt(t->t.getTitleGroups() == null?0:(t.getTitleGroups().length + 1)).max().orElse(1);
		String[][] titleMartix = new String[max][descList.size()];
		MapperDescVo vo = null;
		String tmpStr = null;
		int tmpLen = 0;
		int listSize = descList.size();
		for (int i = 0; i < listSize; i++) {
			vo = descList.get(i);
			tmpLen = ArrayUtils.isEmpty(vo.getTitleGroups())?1:(vo.getTitleGroups().length + 1);
			for (int j = 0; j < max; j++) {
				if(j >= tmpLen) {
					titleMartix[j][i] = null;
				}else if(j == tmpLen - 1) {
					titleMartix[j][i] = vo.getOtherName();
				}else {
					tmpStr = vo.getTitleGroups()[j];
					titleMartix[j][i] = StringUtils.isBlank(tmpStr)?null:tmpStr.trim();
				}
			}
		}
		
		CellStyle[][] titleStyleMartix = buildTitleStyleMartix(titleMartix, descList, sheet.getWorkbook());
		
		/*for (int i = 0; i < max; i++) {
			titleRow = titleMartix[i];
			for (int j = 0; j < titleRow.length; j++) {
				vo = descList.get(j);
				
			}
		}
		
		
		List<String> titleList = descList.stream().map(t -> t.getOtherName()).collect(Collectors.toList());*/

		int rowNum = sheet.getPhysicalNumberOfRows();
		int titleIndex = context.getTitleIndex();
		if(titleIndex > rowNum) {
			for (int i = rowNum; i < titleIndex; i++) {
				sheet.createRow(i);
			}
			rowNum = titleIndex;
		}

//		createSheetByTitle(titleList, rowNum, sheet);
		int[] columnSize = null;
		if(autoSize) {
			columnSize = new int[descList.size()];
		}
		createSheetByTitle(titleMartix, rowNum, sheet, titleStyleMartix, columnSize);
		rowNum += titleMartix.length;

		int pages = pageSupport.getPages();
		if (pages == 0) {
			return sheet;
		}

		Sheet finalSheet = sheet;
		CellStyle[] cellStyles = descList.stream().map(t->t.getContentStyle() == null?null:buildStyle(t.getContentStyle(), finalSheet.getWorkbook())).toArray(CellStyle[]::new);
		List<T> list = null;
		List<String> data = null;
		for (int i = 0; i < pages; i++) {
			list = pageSupport.getPageContent(i + 1);
			if (CollectionUtils.isNotEmpty(list)) {
				for (T entity : list) {
					data = buildInsertData(entity, context, descList);
					insertData(sheet, rowNum, data, cellStyles, columnSize);
					rowNum++;
					if (rowNum % WORKBOOK_BUFFER_MAX_ROWS == 0) {
						submitSheet(sheet);
					}
				}
			}
		}
		submitSheet(sheet);
		for (int i = 0; i < listSize; i++) {
			vo = descList.get(i);
			if(vo.getLength() > 0) {
				sheet.setColumnWidth(i, vo.getLength());
			}else if(columnSize != null && i < columnSize.length) {
				if(columnSize[i] > 0) {
					sheet.setColumnWidth(i, columnSize[i]);
				}
			}
		}
		
		return sheet;
	}
	
	private static CellStyle[][] buildTitleStyleMartix(String[][] titleMartix, List<MapperDescVo> descList, Workbook workbook) {
		int length = titleMartix.length;
		CellStyle[][] result = new CellStyle[length][descList.size()];
		int size = descList.size();
		MapperDescVo vo = null;
		Style[] styles = null;
		for (int i = 0; i < size; i++) {
			vo = descList.get(i);
			if(vo.getTitleStyles() != null) {
				styles = vo.getTitleStyles();
				for (int j = 0; j < styles.length; j++) {
					if(length - styles[j].level() - 1 < length) {
						result[length - styles[j].level() - 1][i] = buildStyle(styles[j], workbook);
					}
				}
				
			}
		}
		CellStyle style = null;
		for (int i = 0; i < size; i++) {
			style = null;
			for (int j = result.length - 1; j >= 0 ; j--) {
				if(style == null) {
					style = result[j][i];
				}else if(result[j][i] == null) {
					result[j][i] = style;
				}else {
					style = result[j][i];
				}
			}
		}
		return result;
	}

	private static void submitSheet(Sheet sheet) {
		if (sheet instanceof SXSSFSheet) {
			try {
				((SXSSFSheet) sheet).flushRows(WORKBOOK_BUFFER_MAX_ROWS);
			} catch (IOException e) {
				throw new ExportFailException(ExportFailException.MESSAGE, e);
			}
		}
	}

	private static <T> List<String> buildInsertData(T data, MapperContext<T> context, List<MapperDescVo> descList) {
		List<String> result = new ArrayList<>(descList.size() < 10 ? 10 : descList.size());
		BeanWrapper bean = new BeanWrapperImpl(data);
		for (MapperDescVo desc : descList) {
			result.add(buildInsertDataItem(bean, context, desc));
		}
		return result;
	}

	private static <T> String buildInsertDataItem(BeanWrapper bean, MapperContext<T> context, MapperDescVo desc) {
		Object value = FieldMapperUtil.getValue(bean, context, desc);
		if (value == null) {
			return "";
		}
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Date) {
			String format = StringUtils.isNotBlank(desc.getFormat()) ? desc.getFormat() : "yyyy-MM-dd HH:mm:ss";
			return DateFormatUtils.format((Date) value, format);
		} else if (value instanceof Number) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumFractionDigits(10);
			String result = nf.format(value);
			return result;
		} else {
			return String.valueOf(value);
		}
	}

	@SuppressWarnings("unused")
	private static Sheet createSheetByTitle(List<String> list, int rowNum, Sheet sheet) {
		try {
			if (sheet == null) {
				sheet = createEmptySheet();
			}
			if (sheet instanceof SXSSFSheet) {
				((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
			}
			sheet.autoSizeColumn(1);

			sheet.autoSizeColumn(1, true);

			insertData(sheet, rowNum, list, new CellStyle[list.size()], null);
			return sheet;
		} catch (Exception e) {
			throw new ExportFailException(ExportFailException.MESSAGE, e);
		}
	}
	@SuppressWarnings("unchecked")
	private static Sheet createSheetByTitle(String[][] titleMartix, int rowNum, Sheet sheet, CellStyle[][] titleStyleMartix, int[] columnSize) {
		int startRowNum = rowNum;
		try {
			if (sheet == null) {
				sheet = createEmptySheet();
			}
			if (sheet instanceof SXSSFSheet) {
				((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
			}
			sheet.autoSizeColumn(1);
			
			sheet.autoSizeColumn(1, true);
//			new CellRangeAddress(firstRow, lastRow, firstCol, lastCol)
			List<CellRangeAddress> rangeList = new ArrayList<>();
			String[] titleRow = null;
			String lastStr = null;
			int firstRow;
			int lastRow;
			int firstCol = 0;
			int lastCol = 0;
			for (int i = 0; i < titleMartix.length; i++) {
				lastStr = null;
				titleRow = titleMartix[i];
				firstRow = i;
				lastRow = i;
				for (int j = 0; j < titleRow.length; j++) {
					if(containInRange(rangeList, i + rowNum, j)) {
						titleStyleMartix[i][j] = null;
						continue;
					}
					if(lastStr == null) {
						lastStr = titleRow[j];
						firstCol = j;
						lastCol = j;
						for (int k = i + 1; k < titleMartix.length; k++) {
							if(StringUtils.isBlank(titleMartix[k][j])) {
								titleMartix[k][j] = null;
								lastRow = k;
							}else {
								break;
							}
						}
					}else if(StringUtils.isBlank(titleRow[j]) || Objects.equals(lastStr, titleRow[j])){
						titleRow[j] = null;
						lastCol = j;
					}else {
						lastStr = titleRow[j];
						if(firstRow != lastRow || firstCol != lastCol) {
							rangeList.add(new CellRangeAddress(rowNum + firstRow, rowNum + lastRow, firstCol, lastCol));
						}
						firstCol = j;
						lastCol = j;
						lastRow = i;
						
						for (int k = i + 1; k < titleMartix.length; k++) {
							if(StringUtils.isBlank(titleMartix[k][j])) {
								titleMartix[k][j] = null;
								lastRow = k;
							}else {
								break;
							}
						}
						
						
					}
					if(j == titleRow.length - 1) {
						lastStr = titleRow[j];
						if(firstRow != lastRow || firstCol != lastCol) {
							rangeList.add(new CellRangeAddress(rowNum + firstRow, rowNum + lastRow, firstCol, lastCol));
						}
						firstCol = j;
						lastCol = j;
						lastRow = i;
					}
					
				}
//				if(firstRow != lastRow || firstCol != lastCol) {
//					rangeList.add(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
//				}
			}
			
//			for (int i = 0; i < titleMartix.length; i++) {
//				titleRow = titleMartix[i];
//				for (int j = 0; j < titleRow.length; j++) {
//					if(titleRow[j] == null) {
//						titleStyleMartix[i][j] = null;
//					}
//				}
//			}
			for (int i = 0; i < titleMartix.length; i++) {
				insertData(sheet, rowNum++, new ArrayList<>(Arrays.asList(titleMartix[i])), titleStyleMartix[i], columnSize);
			}
			CellStyle cellStyle = null;
			Row row = null;
			Cell cell = null;
			for (CellRangeAddress range : rangeList) {
				if(range.getFirstRow() - startRowNum < titleStyleMartix.length) {
					cellStyle = titleStyleMartix[range.getFirstRow() - startRowNum][range.getFirstColumn()];
					if(cellStyle != null) {
						for (int i = range.getFirstRow(); i <= range.getLastRow(); i++) {
							row = sheet.getRow(i);
							for (int j = range.getFirstColumn(); j <= range.getLastColumn(); j++) {
								cell = row.getCell(j);
								if(cell != null) {
									cell.setCellStyle(cellStyle);
								}
							}
						}
					}
				}
				
				sheet.addMergedRegion(range);
			}
			return sheet;
		} catch (Exception e) {
			throw new ExportFailException(ExportFailException.MESSAGE, e);
		}
	}
	
	private static boolean containInRange(List<CellRangeAddress> rangeList, int rowNum, int colNum) {
		for (CellRangeAddress range : rangeList) {
			if(range.containsColumn(colNum) && range.containsRow(rowNum)) {
				return true;
			}
		}
		return false;
	}

	private static void insertData(Sheet sheet, int rowNum, List<String> data, CellStyle[] cellStyles, int[] columnSize) {
		try {
			Row row = sheet.createRow(rowNum);
			Cell cell = null;
			String val = null;
			int width = 0;
			for (int i = 0, num = data.size(); i < num; i++) {
				val = data.get(i);
				if(val != null && columnSize != null && i < columnSize.length) {
					width = calcWidth(val);
					if(width > columnSize[i]) {
						columnSize[i] = width;
					}
				}
				cell = row.createCell(i);
				cell.setCellValue(val);
				if(i < cellStyles.length && cellStyles[i] != null) {
					cell.setCellStyle(cellStyles[i]);
				}
			}
		} catch (Exception e) {
			throw new ExportFailException(ExportFailException.MESSAGE, e);
		}
	}
	
	private static int calcWidth(String val) {
		if(val == null) {
			return 0;
		}
		int result = val.length() * 256;
		result += (val.getBytes().length - val.length()) / 2 * 256;
		return result;
	}

	/**
	 * 打开excel文件
	 * @param is 输入流
	 * @param fileName 文件名
	 * @return
	 */
	public static Workbook openExcel(InputStream is, String fileName) {
		String extString = fileName.substring(fileName.lastIndexOf("."));
		try {
			if (".xls".equalsIgnoreCase(extString)) {
				return new HSSFWorkbook(is);
			} else if (".xlsx".equalsIgnoreCase(extString)) {
				return new XSSFWorkbook(is);
			}
		} catch (Exception e) {
			throw new FileCheckException("文件打开失败");
		}
		return null;
	}

	public static List<Map<String, Object>> importForMap(Sheet sheet) {
		return importForMap(sheet, 0);
	}
	public static List<Map<String, Object>> importForMap(Sheet sheet, int titleIndex) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		importForMap(sheet, titleIndex, t->result.add(t));
		return result;
	}
	public static void importForMap(Sheet sheet, Consumer<Map<String, Object>> consumer) {
		importForMap(sheet, 0, consumer);
	}
	public static void importForMap(Sheet sheet, int titleIndex, Consumer<Map<String, Object>> consumer) {
		int num = sheet.getPhysicalNumberOfRows();
		if (num < titleIndex + 1) {
			throw new FileCheckException("文件至少要包含" + (titleIndex + 1) + "行数据");
		}
		List<String> titleList = parseTitleMap(sheet, titleIndex);
		
		PostMapper<Map<String, Object>, ExcelRowContextVo> parseConsumer = (t, r) -> {
			consumer.accept(t);
			;
		};
		parseSheetRow(sheet, titleIndex + 1, titleList, parseConsumer);
	}

	/**
	 * 通过字段映射上下文获取sheet数据
	 * @param sheet Sheet页
	 * @param context 字段映射上下文
	 * @param consumer 导入模型消费者
	 */
	public static <T> void importForEntity(Sheet sheet, MapperContext<T> context,
			ConsumerImpEntity<T, ValidateResult> consumer) {
		importForEntity(sheet, context, consumer, null);
	}

	/**
	 * 通过字段映射上下文获取sheet数据
	 * @param sheet Sheet页
	 * @param context 字段映射上下文
	 * @param consumer 导入模型消费者
	 * @param postMapper 实体类映射后置方法
	 */
	public static <T> void importForEntity(Sheet sheet, MapperContext<T> context,
			ConsumerImpEntity<T, ValidateResult> consumer, PostMapper<T, ExcelRowContextVo> postMapper) {
		int num = sheet.getPhysicalNumberOfRows();
		int index = context.getTitleIndex();
		int titleEndIndex = context.getTitleEndIndex();
		if(titleEndIndex < 0) {
			titleEndIndex = index;
		}
		if (num < titleEndIndex + 1) {
			throw new FileCheckException("文件至少要包含" + (index + 1) + "行数据");
		}
		List<String> titleList = parseTitleMap(sheet, index, titleEndIndex);

		boolean validEnable = context.isValidEnable();
		Class<?>[] validGroups = context.getValidGroups();
		context.setValidEnable(false, validGroups);

		PostMapper<Map<String, Object>, ExcelRowContextVo> parseConsumer = (t, r) -> {

			T entity = FieldMapperUtil.importMapper(t, context, null);

			if (postMapper != null) {
				postMapper.execute(entity, r);
			}

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
		};

		parseSheetRow(sheet, index + 1, titleList, parseConsumer);

		context.setValidEnable(validEnable, validGroups);
	}

	private static void parseSheetRow(Sheet sheet, int startRowIndex, List<String> titleList,
			PostMapper<Map<String, Object>, ExcelRowContextVo> consumer) {
		int num = sheet.getPhysicalNumberOfRows();
		Row row = null;
		Map<String, Object> dataMap = null;
		ExcelRowContextVo rowContext = null;
		for (int i = startRowIndex; i < num; i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			dataMap = parseRowDataMap(row, titleList);

			rowContext = new ExcelRowContextVo();
			rowContext.setDataMap(dataMap);
			rowContext.setRowIndex(i);
			rowContext.setSheetIndex(sheet.getWorkbook().getSheetIndex(sheet));
			rowContext.setSheetName(sheet.getSheetName());

			if (logger.isTraceEnabled()) {
				logger.trace("parseSheet:[{}], rowIndex:[{}], dataMap:[{}]", sheet.getSheetName(), i, dataMap);
			}
			consumer.execute(dataMap, rowContext);
		}
	}

	private static Map<String, Object> parseRowDataMap(Row row, List<String> titleList) {
		String title = null;
		Map<String, Object> dataMap = new HashMap<>();
		Cell cell = null;
		Object val = null;
		for (int i = 0, titleNum = titleList.size(); i < titleNum; i++) {
			title = titleList.get(i);
			if (title == null) {
				continue;
			}
			cell = row.getCell(i);
			if (cell == null) {
				continue;
			}
			val = getValFromCell(cell, null);
			if (val != null) {
				dataMap.put(title, val);
			}
		}
		return dataMap;
	}

	private static <T> List<String> parseTitleMap(Sheet sheet, int index) {
		List<String> descList = new ArrayList<>();
		Row row = sheet.getRow(index);
		String cellVal = null;
		Cell cell = null;
		for (int i = 0;; i++) {
			cellVal = null;
			cell = row.getCell(i);
			if (cell != null) {
				try {
					cellVal = cell.getStringCellValue();
				} catch (Exception e) {
					Object str = getValFromCell(cell, null);
					cellVal = String.valueOf(str);
				}

			}
			if (cellVal != null) {
				descList.add(cellVal);
			} else {
				break;
			}
		}
		return descList;
	}
	
	private static <T> List<String> parseTitleMap(Sheet sheet, int index, int titleEndIndex) {
		String[] lastTitleRows = new String[titleEndIndex - index + 1];
		Row[] rows = new Row[lastTitleRows.length];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = sheet.getRow(index + i);
		}
		List<String> descList = new ArrayList<>();
		Row row = rows[rows.length - 1];
		String cellVal = null;
		int cellNum = row.getPhysicalNumberOfCells();
		String[] tempTitles = new String[lastTitleRows.length];
		for (int i = 0;i < cellNum; i++) {
			for (int j = 0; j < tempTitles.length; j++) {
				tempTitles[j] = null;
			}
			for (int j = 0; j < rows.length; j++) {
				cellVal = getCellStringValue(rows[j].getCell(i));
				tempTitles[j] = cellVal;
				if(StringUtils.isNotBlank(cellVal)) {
					for (int k = j + 1; k < lastTitleRows.length; k++) {
						lastTitleRows[k] = null;
					}
					if(j < lastTitleRows.length) {
						lastTitleRows[j] = cellVal;
					}
				}
			}
			for (int j = 0; j < tempTitles.length; j++) {
				if(StringUtils.isBlank(tempTitles[j])) {
					tempTitles[j] = lastTitleRows[j];
				}
			}
			descList.add(Stream.of(tempTitles).filter(t->StringUtils.isNotBlank(t)).map(t->StringUtils.trim(t)).collect(Collectors.joining(FieldMapperUtil.TITLE_GROUPS_SEP)));
		}
		return descList;
	}
	
	private static String getCellStringValue(Cell cell) {
		String cellVal = null;
		if (cell != null) {
			try {
				cellVal = cell.getStringCellValue();
			} catch (Exception e) {
				Object str = getValFromCell(cell, null);
				cellVal = String.valueOf(str);
			}

		}
		return cellVal;
	}

	private static Object getValFromCell(Cell cell, MapperDescVo desc) {
		Object val = getJavaValue(cell);
		if (val == null || StringUtils.isBlank(String.valueOf(val))) {
			return null;
		}
		if (desc != null && val instanceof String && desc.getLocalClass().isAssignableFrom(Date.class)) {
			return ConvertFactory.convertToObject((String) val, Date.class);
		}
		return val;
	}
	
	private static CellStyle buildStyle(Style style, Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		XSSFCellStyle xssfCellStyle = null;
		if(cellStyle instanceof XSSFCellStyle) {
			xssfCellStyle = (XSSFCellStyle) cellStyle;
		}
		if(xssfCellStyle != null && StringUtils.isNotEmpty(style.bgColor())) {
			xssfCellStyle.setFillBackgroundColor(buildColor(style.bgColor()));
			
		}
		if(xssfCellStyle != null && StringUtils.isNotEmpty(style.fgColor())) {
			xssfCellStyle.setFillForegroundColor(buildColor(style.fgColor()));
			xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		
		xssfCellStyle.setFont(buildFont(style, workbook));
		
		switch (style.alignment()) {
		case LEFT:
			xssfCellStyle.setAlignment(HorizontalAlignment.LEFT);
			break;
		case RIGHT:
			xssfCellStyle.setAlignment(HorizontalAlignment.RIGHT);
			break;
		case CENTER:
			xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
			break;

		default:
			break;
		}
		switch (style.vAlignment()) {
		case TOP:
			xssfCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
			break;
		case MIDDLE:
			xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			break;
		case BOTTOM:
			xssfCellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
			break;
			
		default:
			break;
		}
		
		xssfCellStyle.setBorderBottom(convertBorder(style.borderBottom()));
		xssfCellStyle.setBorderTop(convertBorder(style.borderTop()));
		xssfCellStyle.setBorderLeft(convertBorder(style.borderLeft()));
		xssfCellStyle.setBorderRight(convertBorder(style.borderRight()));
		
		if(xssfCellStyle != null && StringUtils.isNotEmpty(style.borderBottomColor())) {
			xssfCellStyle.setBottomBorderColor(buildColor(style.borderBottomColor()));
		}
		if(xssfCellStyle != null && StringUtils.isNotEmpty(style.borderTopColor())) {
			xssfCellStyle.setTopBorderColor(buildColor(style.borderTopColor()));
		}
		if(xssfCellStyle != null && StringUtils.isNotEmpty(style.borderLeftColor())) {
			xssfCellStyle.setLeftBorderColor(buildColor(style.borderLeftColor()));
		}
		if(xssfCellStyle != null && StringUtils.isNotEmpty(style.borderRightColor())) {
			xssfCellStyle.setRightBorderColor(buildColor(style.borderRightColor()));
		}
		
		if(xssfCellStyle != null) {
			xssfCellStyle.setWrapText(style.wrapText());
		}
		
		return cellStyle;
		
	}
	
	private static BorderStyle convertBorder(Border border) {
		BorderStyle borderStyle = BorderStyle.valueOf(border.toString());
		if(borderStyle == null) {
			throw new DevelopeCodeException("不合法的border样式" + border.toString());
		}
		return borderStyle;
	}
	
	private static XSSFColor buildColor(String rgb) {
		try {
			java.awt.Color awtColor = new java.awt.Color(Integer.parseInt(rgb, 16));
			XSSFColor color = new XSSFColor();
			color.setRGB(new byte[] {(byte)awtColor.getRed(), (byte)awtColor.getGreen(), (byte)awtColor.getBlue()});
			return color;
		} catch (NumberFormatException e) {
			throw new DevelopeCodeException("不合法的rgb颜色" + rgb, e);
		}
	}
	
	private static Font buildFont(Style style, Workbook workbook) {
		Font font = workbook.createFont();
		if(style.fontHeight() > 0) {
			font.setFontHeight((short) style.fontHeight());
		}
		if(StringUtils.isNotEmpty(style.fontName())) {
			font.setFontName(style.fontName());
		}
		if(style.fontHeightInPoints() > 0) {
			font.setFontHeightInPoints(style.fontHeightInPoints());
		}
		if(style.bold()) {
			font.setBold(style.bold());
		}
		if(style.italic()) {
			font.setItalic(style.italic());
		}
		if(style.underline()) {
			font.setUnderline(Font.U_SINGLE);
		}
		if(style.strikeout()) {
			font.setStrikeout(style.underline());
		}
		if(StringUtils.isNotBlank(style.fontColor())) {
			if(font instanceof XSSFFont) {
				((XSSFFont)font).setColor(buildColor(style.fontColor()));
			}
		}
		
		return font;
	}

}
