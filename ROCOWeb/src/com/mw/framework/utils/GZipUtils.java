package com.mw.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * @author tanml
 * 将一串数据按照gzip方式压缩和解压缩  
 */
public class GZipUtils {
	// 压缩
	public static byte[] compress(byte[] data) throws IOException {
		if (data == null || data.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data);
		gzip.close();
		return  out.toByteArray();
		// return out.toString("ISO-8859-1");
	}
	
	public static byte[] compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return null;
		}
		return compress(str.getBytes("ISO-8859-1"));
	}
	// 解压缩
	public static byte[] uncompress(byte[] data) throws IOException {
		if (data == null || data.length == 0) {
			return data;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[2048];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		gunzip.close();
		in.close();
		return out.toByteArray();
	}
	
	public static String uncompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		byte[] data = uncompress(str.getBytes("ISO-8859-1")); // ISO-8859-1
		return new String(data);
	}
	/**
     * @Title: unZip 
     * @Description: TODO(这里用一句话描述这个方法的作用) 
     * @param @param unZipfile
     * @param @param destFile 指定读取文件，需要从压缩文件中读取文件内容的文件名
     * @param @return 设定文件 
	 * @return String 返回类型 
     * @throws
 */
	public static String unZip(String unZipfile, String destFile) {// unZipfileName需要解压的zip文件名
		InputStream inputStream;
		String inData = null;
		try {
			// 生成一个zip的文件
			File f = new File(unZipfile);
			ZipFile zipFile = new ZipFile(f);
	
			// 遍历zipFile中所有的实体，并把他们解压出来
			ZipEntry entry = zipFile.getEntry(destFile);
			if (!entry.isDirectory()) {
				// 获取出该压缩实体的输入流
				inputStream = zipFile.getInputStream(entry);
	
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] bys = new byte[4096];
				for (int p = -1; (p = inputStream.read(bys)) != -1;) {
					out.write(bys, 0, p);
				}
				inData = out.toString();
				out.close();
				inputStream.close();
			}
			zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return inData;
	}
	public static void main(String[] args) throws Exception{
		
		try {
			File file = new File("C:\\Users\\RH\\Desktop\\上传文件\\44.kit");
			FileInputStream inputFile = new FileInputStream(file);
			
			BASE64Encoder encoder = new BASE64Encoder();
			BASE64Decoder decoder=new BASE64Decoder();
			//压缩 转码
			byte[] buffer = new byte[(int)file.length()];
			inputFile.read(buffer);
			inputFile.close();
			byte[] bufferCom= GZipUtils.compress(buffer);//压缩
			String base64Str=new String (encoder.encode(bufferCom));//base64转码
//——————————————————————————	
			String base64de=new String(decoder.decodeBuffer(base64Str));//base64解码
			byte[] bufferUn= GZipUtils.uncompress(bufferCom);//解压
			FileOutputStream fos = new FileOutputStream("G:/2.kit");
			fos.write(bufferUn);
			fos.close();
			System.out.println("成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
