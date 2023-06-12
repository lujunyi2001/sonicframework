package org.sonicframework.utils.geometry;

import java.io.UnsupportedEncodingException;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.sonicframework.utils.StringExtendUtil;

/**
* @author lujunyi
*/
public enum StringExceedLengthPolicy {

	DEFAULT((str, charsetName)->{
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
		if(sourceBytes.length > 253) {
			throw new IllegalArgumentException("value bytes length is grant than 253");
		}
		return str;
	}),
	NULL((str, charsetName)->{
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
		if(sourceBytes.length > 253) {
			return null;
		}
		return str;
	}),
	SUBSTRING((str, charsetName)->{
		return StringExtendUtil.subtringByByteNum(str, charsetName, 253);
	});
	
	
	private BiFunction<String, String, String> policy;
	private StringExceedLengthPolicy(BiFunction<String, String, String> policy) {
		this.policy = policy;
	}
	
	public String renderResult(String str, String charsetName) {
		return this.policy.apply(str, charsetName);
	}
}
