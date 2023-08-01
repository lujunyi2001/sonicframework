package org.sonicframework.utils.excel;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

import org.sonicframework.context.exception.DataNotValidException;
import org.sonicframework.context.exception.ExportFailException;
import org.sonicframework.context.exception.FileCheckException;

/**
 * @author lujunyi
 */
public class ExcelUtil {

	private final static int WORKBOOK_BUFFER_MAX_ROWS = 200;

	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

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
		if (sheet == null) {
			sheet = createEmptySheet(createWorkbook());
			if (sheet instanceof SXSSFSheet) {
				((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
			}
			sheet.autoSizeColumn(1);

			sheet.autoSizeColumn(1, true);
		}
		List<MapperDescVo> descList = FieldMapperUtil.parseDescByGroups(context.getClazz(), context.getGroups());
		List<String> titleList = descList.stream().map(t -> t.getOtherName()).collect(Collectors.toList());

		int rowNum = sheet.getPhysicalNumberOfRows();

		createSheetByTitle(titleList, rowNum, sheet);
		rowNum++;

		int pages = pageSupport.getPages();
		if (pages == 0) {
			return sheet;
		}

		List<T> list = null;
		List<String> data = null;
		for (int i = 0; i < pages; i++) {
			list = pageSupport.getPageContent(i + 1);
			if (CollectionUtils.isNotEmpty(list)) {
				for (T entity : list) {
					data = buildInsertData(entity, context, descList);
					insertData(sheet, rowNum, data);
					rowNum++;
					if (rowNum % WORKBOOK_BUFFER_MAX_ROWS == 0) {
						submitSheet(sheet);
					}
				}
			}
		}
		submitSheet(sheet);
		return sheet;
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

			insertData(sheet, rowNum, list);
			return sheet;
		} catch (Exception e) {
			throw new ExportFailException(ExportFailException.MESSAGE, e);
		}
	}

	private static void insertData(Sheet sheet, int rowNum, List<String> data) {
		try {
			Row row = sheet.createRow(rowNum);
			Cell cell = null;
			String val = null;
			for (int i = 0, num = data.size(); i < num; i++) {
				val = data.get(i);
				cell = row.createCell(i);
				cell.setCellValue(val);
			}
		} catch (Exception e) {
			throw new ExportFailException(ExportFailException.MESSAGE, e);
		}
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
			throw new FileCheckException("文件至少要包含" + (titleIndex + 1) + "两行数据");
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
		if (num < index + 1) {
			throw new FileCheckException("文件至少要包含" + (index + 1) + "两行数据");
		}
		List<String> titleList = parseTitleMap(sheet, index);

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

}
