package org.sonicframework.core.encrypt;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

/**
* @author lujunyi
*/
public class DecryptHttpInputMessage  implements HttpInputMessage{

	private InputStream inputStream;
	private HttpHeaders headers;

	public DecryptHttpInputMessage(InputStream inputStream, HttpHeaders headers) {
		this.inputStream = inputStream;
		this.headers = headers;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		return this.inputStream;
	}

}
