package com.mw.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.type.TypeException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	/**
	 * 导出excle
	 * 
	 * @param excleName
	 *            名称
	 * @param headTitles
	 *            表格头列 LinkedHashMap 确保顺序不变
	 * @param datas
	 *            数据
	 * @return
	 * @throws IOException
	 */
	public static void createExcel(List<String> excelHeadContent,
			String excleName, LinkedHashMap<String, String> headTitles,
			List<Map<String, Object>> datas, HttpServletResponse response)
			throws IOException {

		response.reset();
		// 第一步，创建一个webbook，对应一个Excel文件
		SXSSFWorkbook wb = null;
		OutputStream outStream = null;
		try {
			wb = new SXSSFWorkbook(10);
			wb.setCompressTempFiles(true);
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(excleName);
			CellStyle dateStyle = wb.createCellStyle();
			CellStyle dateTimeStyle = wb.createCellStyle();
			CellStyle RocoMsgStyle = wb.createCellStyle();
			DataFormat dateFormat = wb.createDataFormat();
			DataFormat dateTimeFormat = wb.createDataFormat();
			dateStyle.setDataFormat(dateFormat.getFormat("yyyy-m-d"));
			dateTimeStyle.setDataFormat(dateTimeFormat
					.getFormat("yyyy-m-d hh:mm:ss"));
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 5));
			int excelHeadContentSize = excelHeadContent == null ? 0
					: excelHeadContent.size();
			sheet.addMergedRegion(new CellRangeAddress(
					excelHeadContentSize + 6, excelHeadContentSize + 7, 0,
					headTitles.size() - 1));
			sheet.addMergedRegion(new CellRangeAddress(0, 3, 0, headTitles
					.size() - 1));
			sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, headTitles
					.size() - 1));

			sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, headTitles
					.size() - 1));
			sheet.setDefaultColumnWidth(1000);
			XSSFFont font = (XSSFFont) wb.createFont();
			sheet.setDefaultColumnWidth(300);
			font.setFontHeightInPoints((short) 20); // 字体高度
			font.setColor(XSSFFont.COLOR_NORMAL); // 字体颜色
			font.setFontName("黑体"); // 字体
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 宽度
			font.setItalic(false); // 是否使用斜体

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			// 添加excel头信息 add by Mark
			// 加入ROCO的图标
			addPicture(wb, sheet,
					"C:/Users/Administrator/Desktop/rocologo.jpg",
					Workbook.PICTURE_TYPE_JPEG, 0, 0, 5);

			int index = 4;
			RocoMsgStyle
					.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			RocoMsgStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			Row rocoTel = sheet.createRow((int) index);
			Row rocoAddress = sheet.createRow((int) index + 1);
			Cell rocoTelCell = rocoTel.createCell((int) 0);
			Cell rocoAddressCell = rocoAddress.createCell((int) 0);

			rocoTelCell.setCellStyle(RocoMsgStyle);
			rocoAddressCell.setCellStyle(RocoMsgStyle);

			rocoTelCell.setCellValue("电话: 020-61802666       传真: 020-61802667");
			rocoAddressCell
					.setCellValue("地址: 广州市花都区红棉大道北九塘西路75733部队斜对面       		http://www.rocochina.com");

			if (excelHeadContent != null) {
				for (index = 6; index < (excelHeadContentSize + 6); index++) {
					Row headRow = sheet.createRow((int) index);
					Cell headCell = headRow.createCell((int) 0);
					headCell.setCellValue(excelHeadContent.get(index - 6));
				}
			}
			if (excelHeadContentSize <= 0) {
				index += 2;
			}
			Row excelTitle = sheet.createRow(index);
			Cell titleCell = excelTitle.createCell(0);
			CellStyle TitleStyle = wb.createCellStyle();
			TitleStyle.setFont(font);
			TitleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			titleCell.setCellStyle(TitleStyle);
			titleCell.setCellValue(excleName);

			Row row;
			index += 2;
			row = sheet.createRow((int) index);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			// HSSFCellStyle style = wb.createCellStyle();
			// style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

			// 设置表头
			Cell cell = row.createCell((short) 0);
			CellStyle generalCellStyle = wb.createCellStyle();
			generalCellStyle.setBorderBottom((short) 1);
			generalCellStyle.setBorderTop((short) 1);
			generalCellStyle.setBorderLeft((short) 1);
			generalCellStyle.setBorderRight((short) 1);
			short ind = 1;
			for (Iterator<String> i = headTitles.keySet().iterator(); i
					.hasNext();) {
				Object key = i.next();// 数据库字段
				System.out.println("表头---key=" + key + " value="
						+ headTitles.get(key));
				cell.setCellValue(headTitles.get(key));
				cell.setCellStyle(generalCellStyle);
				sheet.setColumnWidth(ind - 1,
						headTitles.get(key).length() * 540);
				cell = row.createCell(ind);
				ind++;
			}
			// 写表格数据
			for (int j = index; j < (datas.size() + index); j++) {
				Map<String, Object> data = datas.get(j - index);
				// 创建一行存放数据，第二行开始
				Row dataRow = sheet.createRow((int) j + 1);
				Cell dataccell = dataRow.createCell((short) 0);

				ind = 1;
				int valLength = 0;
				// 对应头表格写数据
				for (Iterator<String> i = headTitles.keySet().iterator(); i
						.hasNext();) {
					Object key = i.next();// 数据库字段
					Object val = data.get(key);// 数据值
					// System.out.println("数据---key=" + key + " value=" + val);
					if (!"".equals(val) && val != null) {
						if (val instanceof String) {
							valLength = (""+ val).length();
							dataccell.setCellValue((String) val);
						} else if (val instanceof Boolean) {
							valLength = (""+ val).length();
							dataccell.setCellValue((Boolean) val);
						} else if (val instanceof Integer) {
							valLength = (""+ val).length();
							dataccell.setCellValue((Integer) val);
						} else if (val instanceof Double
								|| val instanceof java.math.BigDecimal) {
							valLength = ("" + Double
									.parseDouble(val.toString())).length();
							dataccell.setCellValue(Double.parseDouble(val
									.toString()));
						} else if (val instanceof Long) {
							valLength = (""+ val).length();
							dataccell.setCellValue((Long) val);
						} else if (val instanceof Float) {
							valLength = (""+ val).length();
							dataccell.setCellValue((Float) val);
						} else if (val instanceof Character) {
							valLength = (""+ val).length();
							dataccell.setCellValue((Character) val);
						} else if (val instanceof Date) {
							valLength = 10;
							CellStyle style = "00:00:00"
									.equals(sdf.format(val)) ? dateStyle
									: dateTimeStyle;
							String format = "00:00:00".equals(sdf.format(val)) ? "yyyy-MM-dd"
									: "yyyy-MM-dd HH:mm:ss";
							System.out.println("行" + j + "列" + ind + format);
							SimpleDateFormat simp = new SimpleDateFormat(format);
							dataccell.setCellStyle(style);
							dataccell.setCellValue(simp.format(val));
						} else {
							dataccell.setCellValue(val.toString());
						}
					}
					// dataccell.setCellStyle(style);
//					int columnWidth = valLength * 360;
//					if (valLength > 4
//							&& (sheet.getColumnWidth(ind - 1) < columnWidth)) {
//						sheet.setColumnWidth(ind - 1, columnWidth);
//					}
					dataccell.setCellStyle(generalCellStyle);
					dataccell = dataRow.createCell(ind);
					ind++;// 列递增

				}
			}
			System.out.println(sheet.getDefaultColumnWidth());
			// 要加这句，否则中文名乱码
			response.setHeader("Content-Disposition", "attachment; filename="
					+ new String((excleName).getBytes("gbk"), "iso8859-1")
					+ ".xlsx");
			response.setContentType("application/ms-excel");
			outStream = response.getOutputStream();
			wb.write(outStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				outStream.flush();
				outStream.close();
				wb.dispose();
			}
		}

	}

	/**
	 * 生成Excle表格+内容
	 * 
	 * @param excleName
	 *            工作簿名称
	 * @param headTitles
	 *            列头集合 LinkedHashMap 确保顺序不变
	 * @param datas
	 *            数据集合
	 * @return
	 */
	@SuppressWarnings( { "unused" })
	public static Workbook createExcel(String excleName,
			LinkedHashMap<String, String> headTitles,
			List<HashMap<String, Object>> datas) {
		// 第一步，创建一个webbook，对应一个Excel文件
		Workbook wb = null;
		OutputStream outStream = null;
		try {
			wb = new SXSSFWorkbook();
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			Sheet sheet = wb.createSheet(excleName);
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			Row row = sheet.createRow((int) 0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			// HSSFCellStyle style = wb.createCellStyle();
			// style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

			// 设置表头
			Cell cell = row.createCell((short) 0);
			short ind = 1;
			for (Iterator<String> i = headTitles.keySet().iterator(); i
					.hasNext();) {
				Object key = i.next();// 数据库字段
				System.out.println("表头---key=" + key + " value="
						+ headTitles.get(key));
				cell.setCellValue(headTitles.get(key));
				// cell.setCellStyle(style);
				cell = row.createCell(ind);
				ind++;
			}
			// 写表格数据
			for (int j = 0; j < datas.size(); j++) {
				HashMap<String, Object> data = datas.get(j);
				// 创建一行存放数据，第二行开始
				Row dataRow = sheet.createRow((int) j + 1);
				Cell dataccell = dataRow.createCell((short) 0);
				ind = 1;
				// 对应头表格写数据
				for (Iterator<String> i = headTitles.keySet().iterator(); i
						.hasNext();) {
					Object key = i.next();// 数据库字段
					Object val = data.get(key);// 数据值
					// System.out.println("数据---key=" + key + " value=" + val);
					if (!"".equals(val) && val != null) {
						if (val instanceof String) {
							dataccell.setCellValue((String) val);
						} else if (val instanceof Boolean) {
							dataccell.setCellValue((Boolean) val);
						} else if (val instanceof Integer) {
							dataccell.setCellValue((Integer) val);
						} else if (val instanceof Double
								|| val instanceof java.math.BigDecimal) {
							dataccell.setCellValue(Double.parseDouble(val
									.toString()));
						} else if (val instanceof Long) {
							dataccell.setCellValue((Long) val);
						} else if (val instanceof Float) {
							dataccell.setCellValue((Float) val);
						} else if (val instanceof Character) {
							dataccell.setCellValue((Character) val);
						} else if (val instanceof Date) {
							CellStyle style = wb.createCellStyle();
							DataFormat format = wb.createDataFormat();
							SimpleDateFormat sdf = new SimpleDateFormat(
									"HH:mm:ss");
							style.setDataFormat(format.getFormat("yyyy-m-d"
									+ ("00:00:00".equals(sdf.format(val)) ? ""
											: " HH:mm:ss")));
							dataccell.setCellStyle(style);
							dataccell.setCellValue((Date) val);
						} else {
							dataccell.setCellValue(val.toString());
						}
					}
					// dataccell.setCellStyle(style);
					dataccell = dataRow.createCell(ind);
					ind++;// 列递增
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wb;
	}

	public static void createShipMentExcel(List<String> excelHeadContent,
			String excleName, LinkedHashMap<String, String> headTitles,
			Map<String, List<String>> orderHeadMsg,
			Map<String, List<Map<String, Object>>> contentMap,
			Map<String, Map<String, Integer>> countMsg,
			HttpServletResponse response) throws IOException {

		response.reset();
		SXSSFWorkbook wb = null;
		OutputStream outStream = null;
		try {
			wb = new SXSSFWorkbook(10);
			wb.setCompressTempFiles(true);
			SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(excleName);
			XSSFFont columnTitleFont = (XSSFFont) wb.createFont();
			XSSFFont font = (XSSFFont) wb.createFont();

			// 设置字体 --start
			{
				columnTitleFont.setFontHeightInPoints((short) 12); // 字体高度
				columnTitleFont.setColor(XSSFFont.COLOR_NORMAL); // 字体颜色
				columnTitleFont.setFontName("黑体"); // 字体
				columnTitleFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 宽度
				columnTitleFont.setItalic(false); // 是否使用斜体

				font.setFontHeightInPoints((short) 20); // 字体高度
				font.setColor(XSSFFont.COLOR_NORMAL); // 字体颜色
				font.setFontName("黑体"); // 字体
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 宽度
				font.setItalic(false); // 是否使用斜体
			}
			// 设置字体 --end

			CellStyle dateStyle = wb.createCellStyle();
			CellStyle dateTimeStyle = wb.createCellStyle();
			CellStyle RocoMsgStyle = wb.createCellStyle();//--roco公司信息格式
			CellStyle bottomLineStyle = wb.createCellStyle();//--ROCOlogo之后的横线
			CellStyle generalCellStyle = wb.createCellStyle();//--表格中的数据的格式
			CellStyle orderMsgStyle = wb.createCellStyle();//--每个订单的头信息
			CellStyle excelHeadStyle = wb.createCellStyle();//--订单信息中,经销商信息格式
			CellStyle titleStyle = wb.createCellStyle();//--标题格式
			CellStyle columnTitleStyle = wb.createCellStyle();//表格表头格式

			// 设置格式 --start
			{
				
				RocoMsgStyle.setFillForegroundColor(IndexedColors.ORANGE
						.getIndex());
				RocoMsgStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

				excelHeadStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
				// 自动换行
				excelHeadStyle.setWrapText(true);

				titleStyle.setFont(font);
				titleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
				titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

				orderMsgStyle.setFont(columnTitleFont);
				orderMsgStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
				// 自动换行
				orderMsgStyle.setWrapText(true);

				columnTitleStyle.setFont(columnTitleFont);
				columnTitleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
				columnTitleStyle
						.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
				columnTitleStyle.setBorderBottom((short) 1);
				columnTitleStyle.setBorderTop((short) 1);
				columnTitleStyle.setBorderLeft((short) 1);
				columnTitleStyle.setBorderRight((short) 1);

				generalCellStyle.setBorderBottom((short) 1);
				generalCellStyle.setBorderTop((short) 1);
				generalCellStyle.setBorderLeft((short) 1);
				generalCellStyle.setBorderRight((short) 1);
				generalCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
				generalCellStyle
						.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
				
				bottomLineStyle.setBorderBottom((short) 1);
			}

			// 设置格式 --end
			DataFormat dateFormat = wb.createDataFormat();
			DataFormat dateTimeFormat = wb.createDataFormat();
			dateStyle.setDataFormat(dateFormat.getFormat("yyyy-m-d"));
			dateTimeStyle.setDataFormat(dateTimeFormat
					.getFormat("yyyy-m-d hh:mm:ss"));
			
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 5));
			
			int excelHeadContentSize = excelHeadContent == null ? 0
					: excelHeadContent.size();
			
			sheet.addMergedRegion(new CellRangeAddress(
					excelHeadContentSize + 6, excelHeadContentSize + 7, 0,
					headTitles.size() - 1));
			sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, headTitles
					.size() - 1));
			sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, headTitles
					.size() - 1));

			sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, headTitles
					.size() - 1));
			sheet.setDefaultColumnWidth(1000);//设置默认列宽

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			// 添加excel头信息 
			// 加入ROCO的图标
			addPicture(wb, sheet,
					"C:/Users/Administrator/Desktop/rocologo.jpg",
					Workbook.PICTURE_TYPE_JPEG, 0, 0, 5);
			
			//logo之后的横线
			Row bottomLine = sheet.createRow(2);
			for(int i=0;i<headTitles.size();i++){
				bottomLine.createCell(i).setCellStyle(bottomLineStyle);
			}
			

			//填写公司信息  --start
			int index = 4;
			Row rocoTel = sheet.createRow((int) index);
			sheet.addMergedRegion(new CellRangeAddress(index, index, 0,
					headTitles.size() - 1));
			Row rocoAddress = sheet.createRow((int) index + 1);
			sheet.addMergedRegion(new CellRangeAddress(index + 1, index + 1, 0,
					headTitles.size() - 1));
			Cell rocoTelCell = rocoTel.createCell((int) 0);
			Cell rocoAddressCell = rocoAddress.createCell((int) 0);

			rocoTelCell.setCellStyle(RocoMsgStyle);
			rocoAddressCell.setCellStyle(RocoMsgStyle);

			rocoTelCell.setCellValue("电话: 020-61802666       传真: 020-61802667");
			rocoAddressCell
					.setCellValue("地址: 广州市花都区红棉大道北九塘西路75733部队斜对面       		http://www.rocochina.com");

			if (excelHeadContent != null) {
				for (index = 6; index < (excelHeadContentSize + 6); index++) {
					Row headRow = sheet.createRow((int) index);
					if (index == excelHeadContentSize + 5) {//最后一行包含地址 要考虑到自动换行的情况
						headRow.setHeight((short) 600);
						// 垂直置顶
						excelHeadStyle
								.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
					} else {
						headRow.setHeight((short) 400);
						// 垂直居中
						excelHeadStyle
								.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
					}
					sheet.addMergedRegion(new CellRangeAddress(index, index, 0,
							headTitles.size() - 1));
					Cell headCell = headRow.createCell((int) 0);
					headCell.setCellStyle(excelHeadStyle);
					headCell.setCellValue(excelHeadContent.get(index - 6));
				}
			}
			//填写公司信息  --end
			
			
			if (excelHeadContentSize <= 0) {
				index += 2;
			}
			Row excelTitle = sheet.createRow(index);
			Cell titleCell = excelTitle.createCell(0);
			titleCell.setCellStyle(titleStyle);
			titleCell.setCellValue(excleName);

			Row row;
			Cell cell;
			index += 1;
			// 写表格数据
			for (String name : orderHeadMsg.keySet()) {
				//第一步                 订单信息
				row = sheet.createRow(++index);
				sheet.addMergedRegion(new CellRangeAddress(index, index, 0,
						headTitles.size() - 1));
				cell = row.createCell((short) 0);
				row.setHeight((short) 600);
				StringBuilder sb = new StringBuilder();
				for (String msg : orderHeadMsg.get(name)) {
					sb.append(msg + "  ");
				}
				cell.setCellValue(sb.toString());
				cell.setCellStyle(orderMsgStyle);
				
				
				index++;
				row = sheet.createRow((int) index);
				row.setHeight((short) 400);

				//第二步  表头信息
				cell = row.createCell((short) 0);
				short ind = 1;
				for (Iterator<String> i = headTitles.keySet().iterator(); i
						.hasNext();) {
					Object key = i.next();// 数据库字段
					cell.setCellValue(headTitles.get(key));
					cell.setCellStyle(columnTitleStyle);
					sheet.setColumnWidth(ind - 1,
							headTitles.get(key).length() * 680);
					cell = row.createCell(ind);
					ind++;
				}
				index++;
				//第三步  表内容
				List<Map<String, Object>> list = contentMap.get(name);
				for (Map<String, Object> map : list) {
					Row contentRow = sheet.createRow(index++);
					contentRow.setHeight((short) 400);
					Cell dataccell = contentRow.createCell((short) 0);
					ind = 1;
					for (Iterator<String> i = headTitles.keySet().iterator(); i
							.hasNext();) {
						int valLength = 0;
						Object key = i.next();// 数据库字段
						Object val = map.get(key);// 数据值
						if (!"".equals(val) && val != null) {
							if (val instanceof String) {
								valLength = ((String) val).length();
								dataccell.setCellValue(((String) val)
										.equals("0") ? "" : ((String) val));
							} else if (val instanceof Boolean) {
								valLength = ((String) val).length();
								dataccell.setCellValue((Boolean) val);
							} else if (val instanceof Integer) {
								valLength = ((String) val).length();
								dataccell
										.setCellValue(((Integer) val) == 0 ? ""
												: ((Integer) val).toString());
							} else if (val instanceof Double
									|| val instanceof java.math.BigDecimal) {
								valLength = ("" + Double.parseDouble(val
										.toString())).length();
								dataccell.setCellValue(Double.parseDouble(val
										.toString()));
							} else if (val instanceof Long) {
								valLength = ((String) val).length();
								dataccell.setCellValue((Long) val);
							} else if (val instanceof Float) {
								valLength = ((String) val).length();
								dataccell.setCellValue((Float) val);
							} else if (val instanceof Character) {
								valLength = ((String) val).length();
								dataccell.setCellValue((Character) val);
							} else if (val instanceof Date) {
								valLength = 10;
								CellStyle style = "00:00:00".equals(sdf
										.format(val)) ? dateStyle
										: dateTimeStyle;
								String format = "00:00:00".equals(sdf
										.format(val)) ? "yyyy-MM-dd"
										: "yyyy-MM-dd HH:mm:ss";

								SimpleDateFormat simp = new SimpleDateFormat(
										format);
								dataccell.setCellStyle(style);
								dataccell.setCellValue(simp.format(val));
							} else {
								dataccell.setCellValue(val.toString());
							}
						}
						int columnWidth = valLength * 360;
						if (valLength > 4
								&& (sheet.getColumnWidth(ind - 1) < columnWidth)) {
							sheet.setColumnWidth(ind - 1, columnWidth);
						}
						dataccell.setCellStyle(generalCellStyle);
						dataccell = contentRow.createCell(ind);
						ind++;// 列递增

					}
				}
				//第四步  合计信息
				Map<String, Integer> countMap = countMsg.get(name);
				ind = 3;
				Cell countcell;
				Row countRow = sheet.createRow(index);
				sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 2));
				Cell totalCell = countRow.createCell((short) 0);
				totalCell.setCellValue("合计(包)");
				totalCell.setCellStyle(columnTitleStyle);
				countRow.createCell((short) 1).setCellStyle(columnTitleStyle);
				countRow.createCell((short) 2).setCellStyle(columnTitleStyle);
				for (String key : headTitles.keySet()) {
					countRow.setHeight((short) 400);
					countcell = countRow.createCell((short) ind);
					countcell.setCellStyle(columnTitleStyle);
					if (countMap.get(key.toUpperCase()) == null) {
						continue;
					}
					countcell
							.setCellValue("" + countMap.get(key.toUpperCase()));
					ind++;
				}
			}
			
			// 结尾
			index += 3;
			Row bottomRow = sheet.createRow(index);
			sheet.addMergedRegion(new CellRangeAddress(index, index, 0,
					headTitles.size() - 1));
			Cell bottomCell = bottomRow.createCell(0);
			bottomCell.setCellValue("注: 1.提取货物时，请务必按以上清单仔细核对，如有异常（包括订单号、收货人、产品类别、数量等）请及时致电我司客服人员。");

			index++;
			bottomRow = sheet.createRow(index);
			sheet.addMergedRegion(new CellRangeAddress(index, index, 0,
					headTitles.size() - 1));
			bottomCell = bottomRow.createCell(0);
			bottomCell
					.setCellValue("    2. 提取货物时，若发现包装上有损坏、破烂、受潮、请当场与货运公司协商，必要时可以要求索赔。");

			index++;
			bottomRow = sheet.createRow(index);
			sheet.addMergedRegion(new CellRangeAddress(index, index, 0,
					headTitles.size() - 1));
			bottomCell = bottomRow.createCell(0);
			bottomCell.setCellValue("    3、由于货运原因导致的货物损坏，我司将不承担任何责任。");
			// 要加这句，否则中文名乱码
			response.setHeader("Content-Disposition", "attachment; filename="
					+ new String((excleName).getBytes("gbk"), "iso8859-1")
					+ ".xlsx");
			response.setContentType("application/ms-excel");
			outStream = response.getOutputStream();
			wb.write(outStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				outStream.flush();
				outStream.close();
				wb.dispose();
			}
		}

	}

	private static void addPicture(Workbook wb, Sheet sheet,
			String picFileName, int picType, int row, int col, int col2) {
		InputStream is = null;
		try {
			// 读取图片
			is = new FileInputStream(picFileName);
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = wb.addPicture(bytes, picType);
			is.close();
			// 写图片
			CreationHelper helper = wb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			// 设置图片的位置
			anchor.setCol1(col);
			anchor.setRow1(0);
			anchor.setCol2(col2);
			anchor.setRow2(2);
			anchor.setDx1(0);
			anchor.setDx2(255);
			anchor.setDy1(0);
			anchor.setDy2(255);
			drawing.createPicture(anchor, pictureIdx);
		} catch (Exception e) {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	/**
	 * 兼容 所有 Excell 版本
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static Workbook createWorkBook(InputStream is) throws IOException {
		if(!is.markSupported()) {
			is  = new PushbackInputStream(is, 8);
		}
		if(POIFSFileSystem.hasPOIFSHeader(is)) {
			return new HSSFWorkbook(is);
		}
		if(POIXMLDocument.hasOOXMLHeader(is)) {
			return new XSSFWorkbook(is);
		}
		throw new TypeException("无法解析你当前的Excell 版本");
	}
	
	/**
	 * @desc 校验Cell 对象对应的 列 是否为空
	 * @param cell 行对象
	 * @return 返回布尔类型
	 */
	public static boolean checkCellIsNotNull(Cell cell) {
		if(cell==null) {
			return false;
		}
		return true;
	}
}
