package org.sonicframework.utils;

import org.junit.Test;
import org.sonicframework.utils.StringExtendUtil;

/**
* @author lujunyi
*/
public class StringExtendUtilTest {

	public StringExtendUtilTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void subtringByByteNum() {
		String str = "1234567890";
		int byteNum = 5;
		String result = null;
		result = StringExtendUtil.subtringByByteNum(str, byteNum);
		System.out.println(result);
		
		str = "一二三四五六七八九零";
		result = StringExtendUtil.subtringByByteNum(str, byteNum);
		System.out.println(result);
		
		str = "一2三4五六七八九零";
		result = StringExtendUtil.subtringByByteNum(str, byteNum);
		System.out.println(result);
		
		str = "一234五六七八九零";
		result = StringExtendUtil.subtringByByteNum(str, byteNum);
		System.out.println(result);
	}

}
