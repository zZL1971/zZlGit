package com.main.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import org.springframework.util.StringUtils;

public class UrlUtil {
	/**
	 * 指定url访问外部方法
	 * 
	 * @param urlPath
	 * @param requestType
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String sendRequest(String urlPath, String requestType,
			String params, String encoding) throws IOException {
		String result = "";
		URL url = null;
		java.io.BufferedReader in = null;
		java.net.HttpURLConnection conn = null;
		PrintWriter printWriter = null;
		try {
			url = new URL(urlPath);
			// 建立连接
			conn = (java.net.HttpURLConnection) url.openConnection();
			// 设置输入
			conn.setDoOutput(true);
			// 设置输出
			conn.setDoInput(true);
			// 设置访问方式 POST /GET
			conn.setRequestMethod(requestType);
			// 输入参数
			if (!StringUtils.isEmpty(params)) {
				printWriter = new PrintWriter(conn.getOutputStream());
				printWriter.write(params);
				printWriter.flush();
			}
			// 读取输出值
			in = new java.io.BufferedReader(new java.io.InputStreamReader(
					conn.getInputStream(), encoding));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 最后要关闭所有流和连接
			if (conn != null) {
				conn.disconnect();
			}
			if (in != null) {
				in.close();
			}
			if (printWriter != null) {
				printWriter.close();
			}
		}
		return result;
	}
}
