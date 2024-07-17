package org.sonicframework.core.encrypt;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * @author lujunyi
 */
public class ResponseWrapper extends ContentCachingResponseWrapper {

	public ResponseWrapper(HttpServletResponse response) {
		super(response);
	}


}