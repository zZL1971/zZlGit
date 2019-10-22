package com.mw.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class ZipUtils {

	/**
	 * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
	 * 
	 * @param sourceFilePath
	 *            :待压缩的文件路径
	 * @param zipFilePath
	 *            :压缩后存放路径
	 * @param fileName
	 *            :压缩后文件的名称
	 * @return
	 */
	public static boolean fileToZip(String sourceFilePath, String zipFilePath,
			String fileName) {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		if (sourceFile.exists() == false) {
			System.out.println("待压缩的文件目录：" + sourceFilePath + "不存在.");
			sourceFile.mkdir();
		} else {
			try {
				File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
				if (zipFile.exists()) {
					System.out.println(zipFilePath + "目录下存在名字为:" + fileName
							+ ".zip" + "打包文件.");
				} else {
					File[] sourceFiles = sourceFile.listFiles();
					if (null == sourceFiles || sourceFiles.length < 1) {
						System.out.println("待压缩的文件目录：" + sourceFilePath
								+ "里面不存在文件，无需压缩.");
					} else {
						fos = new FileOutputStream(zipFile);
						zos = new ZipOutputStream(new BufferedOutputStream(fos));
						byte[] bufs = new byte[1024 * 10];
						for (int i = 0; i < sourceFiles.length; i++) {
							// 创建ZIP实体，并添加进压缩包
							ZipEntry zipEntry = new ZipEntry(
									sourceFiles[i].getName());
							zos.putNextEntry(zipEntry);
							// 读取待压缩的文件并写进压缩包里
							fis = new FileInputStream(sourceFiles[i]);
							bis = new BufferedInputStream(fis, 1024 * 10);
							int read = 0;
							while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
								zos.write(bufs, 0, read);
							}
						}
						flag = true;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				// 关闭流
				try {
					if (null != bis)
						bis.close();
					if (null != zos)
						zos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return flag;
	}

	/**
	 * 多个文件一起压缩，返回压缩的文件
	 * 
	 * @param files
	 *            文件集合
	 * @param zipFilePath
	 *            压缩文件保存路径
	 * @param fileName
	 *            压缩文件名称
	 * @return
	 */
	public static File filesToZip(List<Map<String,SmbFile>> smbFileMapList, String zipFilePath,
			String fileName) {
		SmbFileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		File zipFile = null;
		try {
			zipFile = new File(zipFilePath + "/" + fileName + ".zip");
			if (zipFile.exists()) {
			} else {
				fos = new FileOutputStream(zipFile);
				zos = new ZipOutputStream(new BufferedOutputStream(fos));
				byte[] bufs = new byte[1024 * 10];
				for (int i = 0; i < smbFileMapList.size(); i++) {
					Map<String,SmbFile> smbFileMap=smbFileMapList.get(i);
					Set<String> keySet = smbFileMap.keySet();
				    for (String key : keySet) {
				    	SmbFile file=smbFileMap.get(key);
				    	// 创建ZIP实体，并添加进压缩包
						ZipEntry zipEntry = new ZipEntry(key);
						zos.putNextEntry(zipEntry);
						// 读取待压缩的文件并写进压缩包里
						fis = new SmbFileInputStream(file);
						bis = new BufferedInputStream(fis, 1024 * 10);
						int read = 0;
						while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
							zos.write(bufs, 0, read);
						}
				    }
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// 关闭流
			try {
				if (null != bis)
					bis.close();
				if (null != zos)
					zos.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return zipFile;
	}

	/**
	 * 多个远程文件一起压缩并下载
	 * 
	 * @param files
	 *            文件集合
	 * @param response
	 * @return
	 */
	public static HttpServletResponse downloadZip(List<Map<String,SmbFile>> smbFileMapList,String fileName,
			HttpServletResponse response,HttpServletRequest request) {
		File file = null;
		OutputStream toClient=null;
		InputStream fis=null;
		try {
			
			String url=request.getSession().getServletContext().getRealPath("");
			System.out.println(url); 
			String zipFilePath = url+"/downloadzip";
			if(fileName==null){
				fileName = "ROCO" +DateTools.getDateYYYYMMDD()+"-"+ new Date().getTime();//默认保存文件名
			}
			file = filesToZip(smbFileMapList, zipFilePath, fileName);
			// 以流的形式下载文件。
			fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			
			// 清空response
			response.reset();

			toClient= new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ file.getName());
			toClient.write(buffer);
			toClient.flush();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try { 
				if(toClient!=null)
					toClient.close();
				if(fis!=null)
					fis.close();
				if(file!=null)
					file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void resolverZip(List<File> fileList,String fileName,
			HttpServletResponse response,HttpServletRequest request) {
		File file = null;
		OutputStream toClient=null;
		InputStream fis=null;
		try {
			
			String url=request.getSession().getServletContext().getRealPath("");
			System.out.println(url); 
			String zipFilePath = url+"/downloadzip";
			if(fileName==null){
				fileName = "ROCO" +DateTools.getDateYYYYMMDD()+"-"+ new Date().getTime();//默认保存文件名
			}
			file = excellToZip(fileList, zipFilePath, fileName);
			// 以流的形式下载文件。
			fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			
			// 清空response
			response.reset();

			toClient= new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ file.getName());
			toClient.write(buffer);
			toClient.flush();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try { 
				if(toClient!=null)
					toClient.close();
				if(fis!=null)
					fis.close();
				if(file!=null)
					file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static File excellToZip(List<File> fileList, String zipFilePath, String fileName) {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		File zipFile = null;
		try {
			zipFile = new File(zipFilePath + "/" + fileName + ".zip");
			if (zipFile.exists()) {
			} else {
				fos = new FileOutputStream(zipFile);
				zos = new ZipOutputStream(new BufferedOutputStream(fos));
				byte[] bufs = new byte[1024 * 10];
				for (int i = 0; i < fileList.size(); i++) {
					File excellFile = fileList.get(i);
					ZipEntry zipEntry = new ZipEntry(excellFile.getName());
					zos.putNextEntry(zipEntry);
					fis = new FileInputStream(excellFile);
					bis = new BufferedInputStream(fis, 1024 * 10);
					int read = 0;
					while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
						zos.write(bufs, 0, read);
					}
					excellFile.delete();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// 关闭流
			try {
				if (null != bis)
					bis.close();
				if (null != zos)
					zos.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return zipFile;
	}

	public static void main(String[] args) {
		try {
			String url=System.getProperty("user.dir"); 
			System.out.println(url);
			File file = new File("G:\\sproWork\\劳卡家具公司\\tmp");
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				System.out.println("//不存在");
				file.mkdir();
			} else {
				System.out.println("//目录存在");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sourceFilePath = "G:\\sproWork\\劳卡家具公司\\测试问题";
		String zipFilePath = "G:\\sproWork\\劳卡家具公司\\tmp";
		String fileName = "12700153file";
		boolean flag = ZipUtils
				.fileToZip(sourceFilePath, zipFilePath, fileName);
		if (flag) {
			System.out.println("文件打包成功!");
		} else {
			System.out.println("文件打包失败!");
		}
	}
}
