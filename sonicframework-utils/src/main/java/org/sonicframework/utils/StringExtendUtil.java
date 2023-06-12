package org.sonicframework.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

/**
* @author lujunyi
*/
public class StringExtendUtil {

	private StringExtendUtil() {}

	
	public static String subtringByByteNum(String str, int byteNum) {
		return subtringByByteNum(str, null, byteNum);
	}
	public static String subtringByByteNum(String str, String charsetName, int byteNum) {
		if(StringUtils.isEmpty(str)) {
			return str;
		}
		byte[] sourceBytes = null;
		if(StringUtils.isNotBlank(charsetName)) {
			try {
				sourceBytes = str.getBytes(charsetName);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("未知字符集" + charsetName, e);
			}
		}else {
			sourceBytes = str.getBytes();
		}
		if(sourceBytes.length <= byteNum) {
			return str;
		}
		String result = null;
		byte[] tmpBytes = new byte[byteNum];
		System.arraycopy(sourceBytes, 0, tmpBytes, 0, byteNum);
		if(StringUtils.isNotBlank(charsetName)) {
			try {
				result = new String(tmpBytes, charsetName);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("未知字符集" + charsetName, e);
			}
		}else {
			result = new String(tmpBytes);
		}
		tmpBytes = null;
		
		int index = result.length() - 1;
		while(true && index > -1) {
			if(result.charAt(index) == str.charAt(index)) {
				result = result.substring(0, index + 1);
				break;
			}
			index--;
		}
		return result;
	}
}
