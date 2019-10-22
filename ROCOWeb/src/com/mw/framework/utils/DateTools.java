package com.mw.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * 
 * 
 */
public class DateTools {
	public static final String defaultFormat = "yyyy-MM-dd";
	public static final String YMHFormat = "yyyy-MM-dd HH";
	public static final String YYMMDDFormat = "yyyyMMdd";
	public static final String YYMMFormat = "yyyyMM";
	public static final String MMDDFormat = "MMdd";
	public static final String fullFormat = "yyyy-MM-dd HH:mm:ss";
	public static final String fullFormat2 = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String YYMMDDHHMMSSFormat = "yyyyMMdd HH:mm:ss";
	public static final String HHMMSSFormat = "HHmmss";
	public static final String HH_MM_SSFormat = "HH:mm:ss";

	public DateTools() {
	}

	public static String getDateAndTime(String fomat) {
		SimpleDateFormat today = new SimpleDateFormat(fomat);
		return today.format(new Date());
	}

	public static String getSelfFomat(String fomat, Date date) {
		SimpleDateFormat today = new SimpleDateFormat(fomat);
		return today.format(date);
	}

	public static String getDateAndTime(Date date, String fomat) {
		SimpleDateFormat today = new SimpleDateFormat(fomat);
		return today.format(date);
	}

	public static String getDate() {
		return getDateAndTime("yyyy-MM-dd");
	}

	public static String getDateYYMMDD() {
		String currentDate = getDateAndTime("yyMMdd");
		return currentDate;
	}

	/**
	 * 
	 * @param days
	 *            当前时间前后天数。可以是负数
	 * @return
	 */
	public static String getNextDate(int days, String format) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, days);
		Date date1 = c.getTime();
		SimpleDateFormat today = new SimpleDateFormat(format);
		return today.format(date1);
	}

	/**
	 * 
	 * @param days
	 *            当前时间前后秒数。可以是负数
	 * @return
	 */
	public static String getNextSecond(int second, String format) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, second);
		Date date1 = c.getTime();
		SimpleDateFormat today = new SimpleDateFormat(format);
		return today.format(date1);
	}

	/**
	 * 
	 * @param days
	 *            当前时间前后秒数。可以是负数
	 * @return
	 */
	public static String getNextSecondYYMMDDHHMMSS(int second) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, second);
		Date date1 = c.getTime();
		SimpleDateFormat today = new SimpleDateFormat(YYMMDDHHMMSSFormat);
		return today.format(date1);
	}

	/**
	 * 
	 * @param days
	 *            当前时间前后秒数。可以是负数
	 * @return
	 */
	public static String getNextSecondHHMMSS(int second) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, second);
		Date date1 = c.getTime();
		SimpleDateFormat today = new SimpleDateFormat(HHMMSSFormat);
		return today.format(date1);
	}

	/**
	 * 
	 * @param days
	 *            当前时间前后天数。可以是负数
	 * @return
	 */
	public static String getNextDateYYMMDD(int days) {
		return getNextDate(days, DateTools.YYMMDDFormat);
	}

	/**
	 * 
	 * @param days
	 *            当前时间前后天数。可以是负数
	 * @return
	 */
	public static String getNextDateYY_MM_DD(int days) {
		return getNextDate(days, DateTools.defaultFormat);
	}

	public static String getDateYY() {
		String currentDate = getDateAndTime("yy");
		return currentDate;
	}

	public static String getDateYYMM() {
		String currentDate = getDateAndTime("yyMM");
		return currentDate;
	}

	public static String getDateYYYYMMDD() {
		String currentDate = getDateAndTime("yyyyMMdd");
		return currentDate;
	}

	public static String getDateYYYYMMDDHHMMSS() {
		String currentDate = getDateAndTime("yyyyMMdd_HH_mm_ss");
		return currentDate;
	}

	public static String getDateYYMMDD(Date date) {
		String currentDate = getDateAndTime(date, "yy-MM-dd");
		return currentDate;
	}

	public static String getDateYYYYMM() {
		String currentDate = getDateAndTime("yyyy-MM");
		currentDate = currentDate.replace("-", "");
		return currentDate;

	}

	public static String getDateYYYY() {
		String currentDate = getDateAndTime("yyyy");
		return currentDate;

	}

	public static String formatDate(Date pidate, String strFormat) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(strFormat);
		String str = simpledateformat.format(pidate);
		return str;
	}

	public static Date getCurrentDateAndTime(String strFormat) {
		return strToDate(getDateAndTime(strFormat), strFormat);

	}

	public static Date parseFormatDate(Date date, String fomat) {
		String curDate = formatDate(date, fomat);
		return (new Date(curDate));

	}

	public static Date strToDate(String str, String strFormat) {
		Date reDate = null;
		SimpleDateFormat simpledateformat = new SimpleDateFormat(strFormat);
		try {
			reDate = simpledateformat.parse(str);
		} catch (Exception exception) {
			simpledateformat = new SimpleDateFormat("yyyyMMdd");
			try {
				reDate = simpledateformat.parse(str);
			} catch (ParseException e) {
				simpledateformat = new SimpleDateFormat("yyyy.MM.dd");
				try {
					reDate = simpledateformat.parse(str);
				} catch (ParseException e2) {
				}
			}
		}
		return reDate;
	}

	public static Date stringToDate(String dateStr) {
		SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // String->Date
		return date;

	}

	public static Date addDay(Date date, long days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(5, (int) days);
		Date date1 = calendar.getTime();
		return java.sql.Date.valueOf(getDateAndTime(date1, "yyyy-MM-dd"));
	}

	public static Date addDay(Date date, int year, int month, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, year);// 日期减1年(-1)
		calendar.add(Calendar.MONTH, month);// 日期加3个月(3)
		calendar.add(Calendar.DAY_OF_YEAR, days);// 日期加10天(10)
		Date date1 = calendar.getTime();
		return java.sql.Date.valueOf(getDateAndTime(date1, "yyyy-MM-dd"));
	}

	public static String addDayString(Date date, long days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(5, (int) days);
		SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
		return today.format(calendar.getTime());
	}

	public static Date addMinute(Date pidate, Long minutes) {
		Long adate = pidate.getTime() + minutes * 60L * 1000L;
		return new Date(adate);
	}

	public static long dateDiff(Date pidate1, Date pidate2) {
		return pidate1.getTime() - pidate2.getTime();
	}

	/**
	 * 获得当前年份
	 * 
	 * @return
	 */
	public static String getNowYear() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		String now = dateformat.format(new Date());
		return now.substring(0, 4);
	}

	public static int getYear(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		// return cld.get(1);
		return cld.get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		// return cld.get(2) + 1;
		return cld.get(Calendar.MONTH) + 1;
	}

	public static int getDay(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获得当前月份
	 * 
	 * @return
	 */
	public static String getNowMonth() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		String now = dateformat.format(new Date());
		return now.substring(4, 6);
	}

	/**
	 * 获得当前日期
	 * 
	 * @return
	 */
	public static String K() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		String now = dateformat.format(new Date());
		return now.substring(6, 8);
	}

	/**
	 * 获得当前日期字符串
	 * 
	 * @return
	 */
	public static String getNowDateYYMMDDHHMMSS() {
		SimpleDateFormat dateformat = new SimpleDateFormat(YYMMDDHHMMSSFormat);
		return dateformat.format(new Date());
	}

	/**
	 * 获得当前日期字符串
	 * 
	 * @return
	 */
	public static String getNowDateYYMMDD() {
		SimpleDateFormat dateformat = new SimpleDateFormat(YYMMDDFormat);
		return dateformat.format(new Date());
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static Date getNextDay(Date nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			// Date d = strToDate(nowdate);
			long myTime = (nowdate.getTime() / 1000) + Integer.parseInt(delay)
					* 24 * 60 * 60;
			nowdate.setTime(myTime * 1000);
			return java.sql.Date.valueOf(format.format(nowdate));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 检验输入是否为正确的日期格式(不含秒的任何情况),严格要求日期正确性,格式:yyyy-MM-dd HH:mm
	 * 
	 * @param sourceDate
	 * @return
	 */
	public static boolean checkDate(String sourceDate) {
		if (sourceDate == null) {
			return false;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			dateFormat.setLenient(false);
			dateFormat.parse(sourceDate);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 检验输入是否为正确的日期格式(不含秒的任何情况),严格要求日期正确性,格式:yyyy-MM-dd HH:mm
	 * 
	 * @param sourceDate
	 * @return
	 */
	public static boolean checkDateYMD(String sourceDate) {
		if (sourceDate == null) {
			return false;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			dateFormat.parse(sourceDate);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 检验输入是否为正确的日期格式(不含秒的任何情况),严格要求日期正确性,格式:yyyy-MM-dd HH
	 * 
	 * @param sourceDate
	 * @return
	 */
	public static boolean checkDateYMDH(String sourceDate) {
		if (sourceDate == null) {
			return false;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
			dateFormat.setLenient(false);
			dateFormat.parse(sourceDate);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 检验输入是否为正确的日期格式(不含秒的任何情况),严格要求日期正确性,格式:yyyy-MM-dd HH
	 * 
	 * @param sourceDate
	 * @return
	 */
	public static boolean checkDateYMDHM(String sourceDate) {
		if (sourceDate == null) {
			return false;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			dateFormat.setLenient(false);
			dateFormat.parse(sourceDate);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 检验输入是否为正确的日期格式(不含秒的任何情况),严格要求日期正确性,格式:yyyy-MM-dd HH
	 * 
	 * @param sourceDate
	 * @return
	 */
	public static boolean checkDateYMDHMS(String sourceDate) {
		if (sourceDate == null) {
			return false;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			dateFormat.setLenient(false);
			dateFormat.parse(sourceDate);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 根据日期获得日期(不含秒的任何情况),严格要求日期正确性,格式:yyyy-MM-dd HH:mm
	 * 
	 * @param sourceDate
	 * @return
	 */
	public static Date getDate(String sourceDate) throws DataFormatException {
		if (sourceDate == null) {
			throw new DataFormatException("源数据为null");
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			dateFormat.setLenient(false);
			return dateFormat.parse(sourceDate);
		} catch (Exception e) {
			throw new DataFormatException("源数据格式错误");
		}
	}

	public static long dateDiff(String style, Date fromdate, Date todate) {
		byte byte1 = 0;
		int i = 1;
		Date date2;
		Date date3;
		if (fromdate.getTime() > todate.getTime()) {
			i = -1;
			date2 = todate;
			date3 = fromdate;
		} else {
			date2 = fromdate;
			date3 = todate;
		}
		byte byte0;
		if (style.equals("yyyy")) {
			byte0 = 1;
		} else if (style.equals("m")) {
			byte0 = 2;
		} else if (style.equals("d")) {
			byte0 = 5;
		} else if (style.equals("y")) {
			byte0 = 5;
		} else if (style.equals("w")) {
			byte0 = 4;
		} else if (style.equals("ww")) {
			byte0 = 3;
		} else if (style.equals("h")) {
			byte0 = 5;
			byte1 = 11;
		} else if (style.equals("n")) {
			byte0 = 5;
			byte1 = 12;
		} else if (style.equals("s")) {
			byte0 = 5;
			byte1 = 13;
		} else {
			return -1L;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date2);
		long l = 0L;
		calendar.add(byte0, 1);
		for (Date date4 = calendar.getTime(); date4.getTime() <= date3
				.getTime();) {
			calendar.add(byte0, 1);
			date4 = calendar.getTime();
			l++;
		}
		if ((byte1 == 11) || (byte1 == 12) || (byte1 == 13)) {
			calendar.setTime(date2);
			calendar.add(byte0, (int) l);
			@SuppressWarnings("unused")
			Date date5 = calendar.getTime();
			switch (byte1) {
			case 11: // '\013'
				l *= 24L;
				break;
			case 12: // '\f'
				l = l * 24L * 60L;
				break;
			case 13: // '\r'
				l = l * 24L * 60L * 60L;
				break;
			}
			calendar.add(byte1, 1);
			for (Date date6 = calendar.getTime(); date6.getTime() <= date3
					.getTime();) {
				calendar.add(byte1, 1);
				date6 = calendar.getTime();
				l++;
			}
		}
		return l * i;
	}

	public static int getWeekDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(7);
	}

	/**
	 * 取得两个日期之间的相差天数
	 * 
	 * @author jacky
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long getDaysByMinus(Date d1, Date d2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d1);
		long timethis = calendar.getTimeInMillis();
		calendar.setTime(d2);
		long timeend = calendar.getTimeInMillis();
		long theday = (timeend - timethis) / (1000 * 60 * 60 * 24);
		return theday;
	}

	/**
	 * 取得两个日期之间的相差秒数，结束日期减开始日期
	 * 
	 * @author james
	 * @param d1
	 *            开始日期
	 * @param d2
	 *            结束日期
	 * @return
	 */
	public static long getSecondBetweenTwoDay(Date d1, Date d2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d1);
		long timethis = calendar.getTimeInMillis();
		calendar.setTime(d2);
		long timeend = calendar.getTimeInMillis();
		long theMin = (timeend - timethis) / (1000);
		return theMin;
	}

	/**
	 * 取得本月最后一天的日期值
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getLastDayOfMonth(String year, String month) {
		int lastDay = 0;
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date buildDate1 = outFormat.parse(year + "-" + month + "-" + 1);// 本月月初
			Date buildDate3 = org.apache.commons.lang.time.DateUtils.addMonths(
					buildDate1, 1);// 下月月初
			Date buildDate4 = org.apache.commons.lang.time.DateUtils.addDays(
					buildDate3, -1);// 本月月末
			Calendar cld = Calendar.getInstance();
			cld.setTime(buildDate4);
			lastDay = cld.get(5);// 本月最后一天
		} catch (Exception e) {

		}
		return lastDay;
	}

	/**
	 * 获取i月前的第一天，可以是负数 如过是上月的，i为-1
	 * 
	 * @param i
	 * @return
	 */
	public static Date getFirstDateMoth(int i) {
		Calendar curCal = Calendar.getInstance();
		curCal.set(Calendar.DATE, 1);
		curCal.add(Calendar.MONTH, i);
		Date endTime = curCal.getTime();
		return endTime;

	}

	/**
	 * 获取财年。
	 * 
	 * @param i
	 * @return
	 */
	public static String getFiniaceYear(int i) {
		Date firstDateMoth = getFirstDateMoth(i);
		System.out
				.println(getDateAndTime(firstDateMoth, DateTools.YYMMDDFormat));
		return getDateAndTime(firstDateMoth, "yyyy");
	}

	/**
	 * 获取赫基财年。
	 * 
	 * @return
	 */
	public static String getHJFiniaceYear() {
		return getFiniaceYear(-3);
	}

	/**
	 * 获取i月前的最后一天，可以是负数 如过是上月的，i为-1
	 * 
	 * @param i
	 * @return
	 */
	public static Date getLastDateMoth(int i) {
		Calendar curCal = Calendar.getInstance();
		curCal.set(Calendar.DATE, 1);
		curCal.add(Calendar.MONTH, i + 1);
		curCal.add(Calendar.DATE, -1);
		Date endTime = curCal.getTime();
		return endTime;

	}

	/**
	 * 处理时间
	 * @param l
	 * @return
	 */
	public static String getProcessTime(long l) {
		if (l <= 0) {
			return "";
		}
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		// System.out.println("" + day + "天" + hour + "小时" + min + "分" + s +
		// "秒");
		return (day == 0 ? "" : day + "天") + (hour == 0 ? "" : hour + "小时")
				+ (min == 0 ? "" : min + "分") + s + "秒";
	}

	public static String toDateSql(Date date) {
		return "to_date( '" + DateTools.formatDate(date, DateTools.fullFormat)
				+ "','yyyy-MM-dd HH24:mi:ss' )";
	}

	public static boolean isMonthLastDay() {
		String nowDateYYMMDD = DateTools.getNowDateYYMMDD();
		String dateYYYYMM = DateTools.getDateYYYYMM();
		int lastDayOfMonth = DateTools.getLastDayOfMonth(dateYYYYMM.substring(
				0, 4), dateYYYYMM.substring(4));
		String nowDateYYMMDD2 = "";
		if (lastDayOfMonth < 10) {
			nowDateYYMMDD2 = dateYYYYMM + "0" + lastDayOfMonth;
		} else {
			nowDateYYMMDD2 = dateYYYYMM + lastDayOfMonth;
		}
		return nowDateYYMMDD.equals(nowDateYYMMDD2);
	}

	public static void main(String[] args) throws ParseException,
			CloneNotSupportedException {
		String finiaceYear = getFiniaceYear(-3);
		System.out.println(finiaceYear);
		// String d = "2011-07-10 15:57:54";
		//
		// // 491536016 2011-07-10 15:57:54---20110710155754_A
		// Date date = DateTools.strToDate(d, DateTools.fullFormat);
		// long l = DateTools.dateDiff(new Date(), date);
		// int lastDayOfMonth = DateTools.getLastDayOfMonth("2011", "10");
		// String dateYYYYMM = DateTools.getDateYYYYMM();
		// DateTools.getNowDateYYMMDD();
		// System.out.println(dateYYYYMM);
		// Date demandDate2 =
		// java.sql.Date.valueOf((getDateAndTime("yyyy-MM-dd")));
		// String nextDate="20100101";
		// for(int i =0;i<452;i++){
		// Date begin = DateTools.strToDate(nextDate,DateTools.YYMMDDFormat);
		// nextDate=DateTools.formatDate(DateTools.addDay(begin, 1),
		// DateTools.YYMMDDFormat);
		// System.out.println(nextDate);
		// }

		// Date date = DateTools.getFirstDateMoth(-1);
		// Date date2 = DateTools.getLastDateMoth(-1);
		// System.out.println(DateTools.toDateSql(date));
		// SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
		// String string = outFormat.format(date);
		// String string2 = outFormat.format(date2);

	}
}