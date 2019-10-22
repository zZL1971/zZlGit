package com.mw.framework.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerGenerialExcel {

	private static Configuration configuration = null;
	
	public Configuration getConfig() {
		if(configuration!=null){
			return configuration;
		}
		configuration = new Configuration();
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
		String path = url.getPath();
		path = path.substring(0,path.lastIndexOf("classes/"))+"ftl";
		try {
			configuration.setDirectoryForTemplateLoading(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		configuration.setDefaultEncoding("UTF-8");
		configuration.setLocale(Locale.CHINESE);
		configuration.setClassicCompatible(true);//处理空值为空字符串
		return configuration;
	}
	
	public Message downFreemarkerExcel(String orderCodePosex, HttpServletResponse response, HttpServletRequest request) {
		Message msg = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			Map<String, Object> root = new HashMap<String, Object>();
			JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
			String sql="SELECT ROWNUM AS \"SIZE\", MP.AMOUNT,MP.AREA,MP.COLOR,MP.DEEP,MP.HIGH,MP.LINE,MP.NAME,MP.NET_PRICE as netPrice,MP.REBATE,MP.TOTAL_PRICE as totalPrice,MP.TYPE AS TYPEN,MP.UNIT,MP.UNIT_PRICE unitPrice,MP.WIDE FROM SALE_ITEM SI LEFT JOIN MATERIAL_PRICE MP ON SI.ID = MP.PID WHERE SI.ORDER_CODE_POSEX = '"+orderCodePosex+"' ORDER BY MP.TYPE";
			List<Map<String, Object>> bzBnajian = jdbcTemplate.queryForList(sql);
			String SQL_GROUP="SELECT MP.TYPE,COUNT(1) AS NU FROM SALE_ITEM SI LEFT JOIN MATERIAL_PRICE MP ON SI.ID = MP.PID WHERE SI.ORDER_CODE_POSEX = '"+orderCodePosex+"' GROUP BY MP.TYPE ORDER BY MP.TYPE";
			List<Map<String, Object>> group = jdbcTemplate.queryForList(SQL_GROUP);
			String SQL="SELECT SI.ORDER_CODE_POSEX AS orderCode,SH.ORDER_CODE AS CODE," + 
					"       CH.NAME1," + 
					"       (SELECT SD.DESC_ZH_CN FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE ST.KEY_VAL='SALE_FOR' AND SD.KEY_VAL=MH.SALE_FOR) AS SALE_FOR," + 
					"       (SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=DECODE(0,'0',(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='MATERIAL_MATKL'),'1',(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='MATERIAL_MATKL_CUP'),'3',(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='MATERIAL_MATKL_MM'),'') AND SD.KEY_VAL=MH.MATKL) AS MATKL," + 
					"        (SELECT SD.DESC_ZH_CN FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE ST.KEY_VAL=MH.TEXTURE_OF_MATERIAL AND SD.KEY_VAL=MH.COLOR AND ST.PID IN (SELECT S.ID FROM SYS_TRIE_TREE S WHERE S.KEY_VAL = DECODE(MH.SALE_FOR,'0','TEXTURE_OF_MATERIAL','1','TEXTURE_OF_MATERIAL_CUP','3','MM_TEXTURE',''))) AS COLOR," + 
					"       TC.NAME1 AS name1," + 
					"       SIF.ZZAZDR AS azAddress," + 
					"       MH.TEXTURE_OF_MATERIAL," + 
					"       SH.CHECK_PRICE_USER AS checkPrice" + 
					"  FROM SALE_HEADER SH" + 
					"  LEFT JOIN SALE_ITEM SI" + 
					"    ON SH.ID = SI.PID" + 
					"  LEFT JOIN MATERIAL_HEAD MH" + 
					"    ON SI.MATERIAL_HEAD_ID = MH.ID" + 
					"  LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG=CH.KUNNR" + 
					"  LEFT JOIN TERMINAL_CLIENT TC ON SH.ID=TC.SALE_ID" + 
					"  LEFT JOIN SALE_ITEM_FJ SIF ON SI.ID=SIF.SALE_ITEM_ID" + 
					" WHERE SI.ORDER_CODE_POSEX = '"+orderCodePosex+"'";
			List<Map<String, Object>> saleHeader = jdbcTemplate.queryForList(SQL);
			String kunnrName="";
			String fileName="";
			if(saleHeader.size()>0) {
				String order = String.valueOf(saleHeader.get(0).get("CODE"));
				order = order.substring((order.length()-3),order.length());
				String saleFor = String.valueOf(saleHeader.get(0).get("SALE_FOR"));
				String matkl = String.valueOf(saleHeader.get(0).get("MATKL"));
				String color = String.valueOf(saleHeader.get(0).get("COLOR"));
				String name = String.valueOf(saleHeader.get(0).get("NAME1"));
				fileName = order+"单/"+saleFor+".xls";
				kunnrName = name+order+"单/"+saleFor+"/"+matkl+"/"+color;
				saleHeader.get(0).put("kunnrName", kunnrName);
				saleHeader.get(0).put("priceDate", DateTools.getDateAndTime(new Date(),DateTools.defaultFormat));
			}
			int num=group.size();
			String format="";
			int su = 0;
			for (Map<String, Object> map : group) {
				su += map.get("NU")!=null?Integer.parseInt(String.valueOf(map.get("NU"))):0;
				int val= num*4-2+bzBnajian.size()-su;
				format += ",R[-"+val+"]C[22]";
				num --;
			}
			format = format.substring(1);
			Template tel = this.getConfig().getTemplate("test01.ftl");
			root.put("data", bzBnajian);
			root.put("format", format);
			root.put("baseData", "爱她就给她一个家");
			root.put("saleHeader", saleHeader.get(0));
			response.setCharacterEncoding("UTF-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition",
					"attachment;fileName="
							+ new String(fileName
									.replace(",", "及").getBytes("gbk"),
									"ISO8859-1"));
			File file=File.createTempFile(fileName, "xls");
			Writer w = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			tel.process(root, w);
			inputStream = new BufferedInputStream(new FileInputStream(file));
			outputStream = response.getOutputStream();
			byte[] b = new byte[2048];
			int length;
			while ((length = inputStream.read(b)) > 0) {
				outputStream.write(b, 0, length);
			}
			
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			msg = new Message("MM-fileDownload-500", "文件下载失败!");
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			msg = new Message("MM-fileDownload-500", "文件下载失败!");
		}finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return msg;
	}
	public File downFreemarkerExcel(String orderCodePosex) {
		File file =null;
		OutputStream outStream= null;
		try {
			Map<String, Object> root = new HashMap<String, Object>();
			JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
			String sql="SELECT ROWNUM AS \"SIZE\", MP.AMOUNT,MP.AREA,MP.COLOR,MP.DEEP,MP.HIGH,MP.LINE,MP.NAME,MP.NET_PRICE as netPrice,MP.REBATE,MP.TOTAL_PRICE as totalPrice,MP.TYPE AS TYPEN,MP.UNIT,MP.UNIT_PRICE unitPrice,MP.WIDE FROM SALE_ITEM SI LEFT JOIN MATERIAL_PRICE MP ON SI.ID = MP.PID WHERE SI.ORDER_CODE_POSEX = '"+orderCodePosex+"' ORDER BY MP.TYPE";
			List<Map<String, Object>> bzBnajian = jdbcTemplate.queryForList(sql);
			String SQL_GROUP="SELECT MP.TYPE,COUNT(1) AS NU FROM SALE_ITEM SI LEFT JOIN MATERIAL_PRICE MP ON SI.ID = MP.PID WHERE SI.ORDER_CODE_POSEX = '"+orderCodePosex+"' GROUP BY MP.TYPE ORDER BY MP.TYPE";
			List<Map<String, Object>> group = jdbcTemplate.queryForList(SQL_GROUP);
			String SQL="SELECT SI.ORDER_CODE_POSEX AS orderCode,SH.ORDER_CODE AS CODE," + 
					"       CH.NAME1," + 
					"       (SELECT SD.DESC_ZH_CN FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE ST.KEY_VAL='SALE_FOR' AND SD.KEY_VAL=MH.SALE_FOR) AS SALE_FOR," + 
					"       (SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID=DECODE(0,'0',(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='MATERIAL_MATKL'),'1',(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='MATERIAL_MATKL_CUP'),'3',(SELECT ST.ID FROM SYS_TRIE_TREE ST WHERE ST.KEY_VAL='MATERIAL_MATKL_MM'),'') AND SD.KEY_VAL=MH.MATKL) AS MATKL," + 
					"        (SELECT SD.DESC_ZH_CN FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE ST.KEY_VAL=MH.TEXTURE_OF_MATERIAL AND SD.KEY_VAL=MH.COLOR AND ST.PID IN (SELECT S.ID FROM SYS_TRIE_TREE S WHERE S.KEY_VAL = DECODE(MH.SALE_FOR,'0','TEXTURE_OF_MATERIAL','1','TEXTURE_OF_MATERIAL_CUP','3','MM_TEXTURE',''))) AS COLOR," + 
					"       TC.NAME1 AS name1," + 
					"       SIF.ZZAZDR AS azAddress," + 
					"       MH.TEXTURE_OF_MATERIAL," + 
					"       SH.CHECK_PRICE_USER AS checkPrice" + 
					"  FROM SALE_HEADER SH" + 
					"  LEFT JOIN SALE_ITEM SI" + 
					"    ON SH.ID = SI.PID" + 
					"  LEFT JOIN MATERIAL_HEAD MH" + 
					"    ON SI.MATERIAL_HEAD_ID = MH.ID" + 
					"  LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG=CH.KUNNR" + 
					"  LEFT JOIN TERMINAL_CLIENT TC ON SH.ID=TC.SALE_ID" + 
					"  LEFT JOIN SALE_ITEM_FJ SIF ON SI.ID=SIF.SALE_ITEM_ID" + 
					" WHERE SI.ORDER_CODE_POSEX = '"+orderCodePosex+"'";
			List<Map<String, Object>> saleHeader = jdbcTemplate.queryForList(SQL);
			String kunnrName="";
			String fileName="";
			if(saleHeader.size()>0) {
				String order = String.valueOf(saleHeader.get(0).get("CODE"));
				order = order.substring((order.length()-3),order.length());
				String saleFor = String.valueOf(saleHeader.get(0).get("SALE_FOR"));
				String matkl = String.valueOf(saleHeader.get(0).get("MATKL"));
				String color = String.valueOf(saleHeader.get(0).get("COLOR"));
				String name = String.valueOf(saleHeader.get(0).get("NAME1"));
				fileName = order+"单/"+saleFor;
				kunnrName = name+order+"单/"+saleFor+"/"+matkl+"/"+color;
				saleHeader.get(0).put("kunnrName", kunnrName);
				saleHeader.get(0).put("priceDate", DateTools.getDateAndTime(new Date(),DateTools.defaultFormat));
			}
			int num=group.size();
			String format="";
			int su = 0;
			for (Map<String, Object> map : group) {
				su += map.get("NU")!=null?Integer.parseInt(String.valueOf(map.get("NU"))):0;
				int val= num*4-2+bzBnajian.size()-su;
				format += ",R[-"+val+"]C[22]";
				num --;
			}
			format = format.substring(1);
			Template tel = this.getConfig().getTemplate("test01.ftl");
			root.put("data", bzBnajian);
			root.put("format", format);
			root.put("baseData", "爱她就给她一个家");
			root.put("saleHeader", saleHeader.get(0));
			file=File.createTempFile(fileName, ".xls");
			outStream=new FileOutputStream(file);
			Writer w = new OutputStreamWriter(outStream, "utf-8");
			tel.process(root, w);
			w.flush();
			w.close();
		}catch(TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(outStream!=null) {
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return file;
	}
}
