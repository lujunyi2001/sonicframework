package org.sonicframework.utils.file;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class UploadVo implements Serializable{

	private static final long serialVersionUID = -2029548182854017830L;
	private long size;
	private String originalFilename;
	private String extendName;
	private String remotePath;
	private String tmpPath;
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getOriginalFilename() {
		return originalFilename;
	}
	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}
	public String getExtendName() {
		return extendName;
	}
	public void setExtendName(String extendName) {
		this.extendName = extendName;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
	public String getTmpPath() {
		return tmpPath;
	}
	public void setTmpPath(String tmpPath) {
		this.tmpPath = tmpPath;
	}
	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
