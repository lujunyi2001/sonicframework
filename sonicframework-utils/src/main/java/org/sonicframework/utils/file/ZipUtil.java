package org.sonicframework.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.sonicframework.utils.StreamUtil;

import org.sonicframework.context.exception.FileCheckException;

import net.lingala.zip4j.model.FileHeader;

public class ZipUtil {

	/**
	 * 解压
	 * 
	 * @param zipPath
	 * @param descDir
	 * @return
	 */
	public static boolean decompressZip(String zipPath, String descDir) {
		File zipFile = new File(zipPath);
		boolean flag = false;
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = null;
		try {
			System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
			String encoding = getEncoding(zipPath);
			zip = new ZipFile(zipFile, Charset.forName(encoding));// 防止中文目录，乱码
			ZipEntry entry = null;
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
				entry = entries.nextElement();
				String zipEntryName = entry.getName();
				// 指定解压后的文件夹+当前zip文件的名称
				String outPath = (descDir + "/" + zipEntryName).replace("/", File.separator);
				// 判断路径是否存在,不存在则创建文件路径
				File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
				if (!file.exists()) {
					file.mkdirs();
				}
				// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
				if (new File(outPath).isDirectory()) {
					continue;
				}
				try (InputStream in = zip.getInputStream(entry);OutputStream out = new FileOutputStream(outPath);) {
					byte[] buf1 = new byte[2048];
					int len;
					while ((len = in.read(buf1)) > 0) {
						out.write(buf1, 0, len);
					}
				} finally {

				}
			}
			flag = true;
		} catch (Exception e) {
			throw new RuntimeException("解压" + zipPath + "时出错", e);
		} finally {
			StreamUtil.close(zip);
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	private static String getEncoding(String path) throws Exception {
		String encoding = "GBK";
		net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(path);
		zipFile.setFileNameCharset(encoding);
		List<FileHeader> list = zipFile.getFileHeaders();
		for (int i = 0; i < list.size(); i++) {
			FileHeader fileHeader = list.get(i);
			String fileName = fileHeader.getFileName();
			if (isMessyCode(fileName)) {
				encoding = "UTF-8";
				break;
			}
		}
		return encoding;
	}

	private static boolean isMessyCode(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
			// 从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
			if ((int) c == 0xfffd) {
				// 存在乱码
				return true;
			}
		}
		return false;
	}

	public static void doZip(File inFile, ZipOutputStream out, String entryName) throws IOException {
		ZipEntry entry = new ZipEntry(entryName);
		out.putNextEntry(entry);

		int len = 0;
		byte[] buffer = new byte[1024];
		FileInputStream fis = new FileInputStream(inFile);
		while ((len = fis.read(buffer)) > 0) {
			out.write(buffer, 0, len);
			out.flush();
		}
		fis.close();
		out.closeEntry();
	}

	/**
	 * 压缩文件列表中的文件
	 * 
	 * @param files
	 * @param outputStream
	 * @throws IOException
	 */
	public static void zipFile(List<File> files, List<String> names, ZipOutputStream outputStream)
			throws IOException, ServletException {
		if (files.size() != names.size()) {
			throw new RuntimeException("files size is not equals names size");
		}
		try {
			// 压缩列表中的文件
			File file = null;
			for (int i = 0, size = files.size(); i < size; i++) {
				file = files.get(i);
				zipFile(file, names.get(i).toString(), outputStream);
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public static void zipFile(List<File> files, ZipOutputStream outputStream) throws IOException {
		try {
			// 压缩列表中的文件
			for (int i = 0, size = files.size(); i < size; i++) {
				File file = (File) files.get(i);
				zipFile(file, file.getName(), outputStream);
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 将文件写入到zip文件中
	 * 
	 * @param inputFile
	 * @param outputstream
	 * @throws Exception
	 */
	public static void zipFile(File inputFile, String name, ZipOutputStream outputstream) throws IOException {
		try {
			if (inputFile.exists()) {
				if (inputFile.isFile()) {
					FileInputStream inStream = new FileInputStream(inputFile);
					BufferedInputStream bInStream = new BufferedInputStream(inStream);
					ZipEntry entry = new ZipEntry(name);
					outputstream.putNextEntry(entry);

					final int MAX_BYTE = 10 * 1024 * 1024; // 最大的流为10M
					long streamTotal = 0; // 接受流的容量
					int streamNum = 0; // 流需要分开的数量
					int leaveByte = 0; // 文件剩下的字符数
					byte[] inOutbyte; // byte数组接受文件的数据

					streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
					streamNum = (int) Math.floor(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
					leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量

					if (streamNum > 0) {
						for (int j = 0; j < streamNum; ++j) {
							inOutbyte = new byte[MAX_BYTE];
							// 读入流,保存在byte数组
							bInStream.read(inOutbyte, 0, MAX_BYTE);
							outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
						}
					}
					// 写出剩下的流数据
					inOutbyte = new byte[leaveByte];
					bInStream.read(inOutbyte, 0, leaveByte);
					outputstream.write(inOutbyte);
					outputstream.closeEntry(); // Closes the current ZIP entry and positions the stream for writing the
												// next entry
					bInStream.close(); // 关闭
					inStream.close();
				}
			} else {
				throw new RuntimeException("文件不存在！");
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public static void downloadZip(File file, HttpServletResponse response) {
		try {
			// 以流的形式下载文件。
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();

			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());

			// 设置response的Header
			String fileName = new String(file.getName().getBytes(), "ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.addHeader("Content-Length", "" + file.length());
			response.setContentType("application/octet-stream");

			toClient.write(buffer);
			toClient.flush();
			toClient.close();
			file.delete(); // 将生成的服务器端文件删除
		} catch (IOException ex) {
			throw new FileCheckException("未找到文件" + file, ex);
		} finally {

		}
	}

}
