package org.sonicframework.utils.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.utils.StreamUtil;
import org.springframework.web.multipart.MultipartFile;

import org.sonicframework.context.exception.FileCheckException;
import org.sonicframework.context.exception.UploadFailException;

public class FileUtil {
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	public static final String SEP = "/";
	public static final String EXTEND_SEP = ".";
	private static String tmpDir;
	public static String getTmpDir() {
		if(tmpDir == null) {
			tmpDir = System.getProperty("java.io.tmpdir") + SEP;
		}
		return tmpDir;
	}
	
	public static String getTmpDirPath() {
		return getTmpDir() + getTmpDirRelativePath();
	}
	public static String getTmpDirRelativePath() {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateStr = format.format(new Date());
		return "landtool/" + dateStr + SEP;
	}
	
	public static File uploadTmpDir(MultipartFile file) {
		if(file.isEmpty()) {
			throw new UploadFailException("上传文件为空");
		}
		UploadVo vo = extractUploadVo(file);
		String realName = getTmpDirRelativePath() + UUID.randomUUID().toString() + SEP + vo.getOriginalFilename() + EXTEND_SEP + vo.getExtendName();
		String tmpFilePath = getTmpDir() + SEP + realName;
		File destFile = new File(tmpFilePath);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();// 新建文件夹
		}
		try {
            file.transferTo(destFile);
        } catch (IOException e) {
        	throw new UploadFailException("上传文件失败", e);
        }finally {
        	
        }
		return destFile;
	}
	
	public static UploadVo extractUploadVo(MultipartFile file) {
		UploadVo vo = new UploadVo();
		long size = file.getSize();
		String originalFilename = file.getOriginalFilename();
		int index = originalFilename.lastIndexOf("\\");
		if(index > -1) {
			originalFilename = originalFilename.substring(index + 1);
		}
		index = originalFilename.lastIndexOf("/");
		if(index > -1) {
			originalFilename = originalFilename.substring(index + 1);
		}
		String extendName = null;
		index = originalFilename.lastIndexOf(EXTEND_SEP);
		if(index > -1) {
			extendName = originalFilename.substring(index + 1);
			originalFilename = originalFilename.substring(0, index);
		}
		vo.setSize(size);
		vo.setOriginalFilename(originalFilename);
		vo.setExtendName(extendName);
		return vo;
	}
	
	public static String getExtendName(String name) {
		String extendName = null;
		int index = name.lastIndexOf(EXTEND_SEP);
		if(index > -1) {
			extendName = name.substring(index + 1);
		}
		return extendName;
	}
	
	public static void extractZip(File file, String outfilepath) {
		InputStream input = null;
        OutputStream output = null;
        ZipArchiveInputStream inputStream = null;
        try {
        	inputStream = getZipFile(file);
        	File outFile = null;
			if(outfilepath == null) {
				outFile = file.getParentFile();
			}else {
				outFile = new File(outfilepath);
			}
            if (!outFile.exists()) {
            	outFile.mkdirs();
            }
            ZipUtil.decompressZip(file.getAbsolutePath(), outFile.getAbsolutePath());
	    } catch (Exception e) {
	    	logger.error("解压文件失败", e);
	        throw new FileCheckException("解压文件失败", e);
	    }finally {
	    	StreamUtil.close(input);
	    	StreamUtil.close(output);
	    	StreamUtil.close(inputStream);
	    }
	}
	
	public static String byteToHex(byte[] bytes){
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }
	
	private static ZipArchiveInputStream getZipFile(File zipFile) throws Exception {
	    return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
	}
	
	public static File[] listFile(String dicPath, String extendName) {
		List<File> result = new ArrayList<>();
		addListFile(dicPath, extendName, result);
		return result.toArray(new File[0]);
//		File file = new File(dicPath);
//		final String end = "." + extendName;
//		File[] files = file.listFiles(new FilenameFilter() {
//			
//			@Override
//			public boolean accept(File dir, String name) {
//				return name.endsWith(end);
//			}
//		});
//		return files;
	}
	
	private static void addListFile(String dicPath, String extendName, List<File> result) {
		final String end = extendName.startsWith(EXTEND_SEP)?extendName.toLowerCase():(EXTEND_SEP + extendName.toLowerCase());
		File file = new File(dicPath);
		if(file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				if(listFiles[i].isDirectory()) {
					addListFile(listFiles[i].getAbsolutePath(), extendName, result);
				}else {
					if(listFiles[i].getName().toLowerCase().endsWith(end)) {
						result.add(listFiles[i]);
					}
				}
			}
		}else {
			if(file.getName().toLowerCase().endsWith(end)) {
				result.add(file);
			}
		}
		
	}
	
	public static void delete(String path) {
		delete(new File(path));
	}
	
	public static void delete(File file) {
		if(!file.exists()) {return;}
		
		if(file.isFile() || file.list()==null) {
			file.delete();
			logger.trace("删除了{}", file.getName());
		}else {
			File[] files = file.listFiles();
			for(File a:files) {
				delete(a);					
			}
			file.delete();
			logger.trace("删除了{}", file.getName());
		}
		
	}
	
	public static URL findInitCodeUrl(String fileName) throws MalformedURLException {
		String basePath = System.getProperty("landtool.initCode.path");
		if(StringUtils.isNotBlank(basePath)) {
			File dic = new File(basePath);
			if(dic.exists() && dic.isDirectory()) {
				File[] files = dic.listFiles((t, name)->Objects.equals(name, fileName));
				if(files.length > 0) {
					return files[0].toURI().toURL();
				}
			}
		}
		String resourceName = "/initCode/" + fileName;
		return FileUtil.class.getResource(resourceName);
	}
	
	public static File buildTempFile(String extendName) {
		File file = new File(getTmpDir() + "landtool/" + UUID.randomUUID().toString() + EXTEND_SEP + extendName);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new FileCheckException("创建文件" + file + "失败", e);
			}
		}
		return file;
	}
}
