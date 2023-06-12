package org.sonicframework.core.webapi;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class DateConverter implements Converter<String, Date> {

    private Logger logger = LoggerFactory.getLogger(DateConverter.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0000'";
    private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIMESTAMP_FORMAT = "^\\d+$";

    @Override
    public Date convert(String value) {
        logger.info("转换日期：" + value);

        if(value == null || value.trim().equals("") || value.equalsIgnoreCase("null")) {
            return null;
        }

        value = value.trim();

        try {
        	try {
				return DateUtils.parseDate(value, DATE_FULL_FORMAT, DATE_FORMAT, SHORT_DATE_FORMAT);
			} catch (Exception e) {
				if (value.matches(TIMESTAMP_FORMAT)) {
	                Long lDate = new Long(value);
	                return new Date(lDate);
	            }
			}
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", value));
        }
        throw new RuntimeException(String.format("parser %s to Date fail", value));
    }
}