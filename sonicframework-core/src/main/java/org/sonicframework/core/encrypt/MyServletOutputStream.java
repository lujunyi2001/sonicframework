package org.sonicframework.core.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
* @author lujunyi
*/
public class MyServletOutputStream extends ServletOutputStream {

	private ByteArrayOutputStream bytes;

	public MyServletOutputStream(ByteArrayOutputStream bytes) {
		this.bytes = bytes;
	}
	
	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setWriteListener(WriteListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(int b) throws IOException {
		bytes.write(b);
	}

}
