package com.mw.framework.utils;

import java.util.Calendar;
import java.util.Date;

public class ControlTimeUtil {

	public static boolean checkTime(Date date) {
		//1.初始化 日历对象
		Calendar calendar = Calendar.getInstance();
		//2.设置当前系统时间
		calendar.setTime(date);
		//3.获取当前 时间的是否为星期天 根据需要 星期天 不可以操作
		return (isSunday(calendar.get(Calendar.DAY_OF_WEEK),calendar)&&isLockTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
	}
	
	public static boolean isSunday(int week,Calendar calendar) {
		if(isMouthLastDay(calendar)) {//判断月末是否为星期天
			return true;
		}
		if(1 == week) {//星期天
			return true;
		}
		return true;
	}
	
	public static boolean isLockTime(int hour , int minute) {
		/**
		 * 首先判断上午
		 */
		if(hour >= 8 && hour < 13) {
			if(hour >= 8  && hour < 13) {
				if(hour ==8 && minute < 30) {
					return false;
				}
			}
			if(hour >=9 &&hour < 13) {
				if(hour == 12 && minute > 30) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * 再次判断下午
		 */
		if(hour >= 13 && hour < 18) {
			if(hour >= 13 && hour < 18) {
				if(hour == 13 && minute < 30) {
					return false;
				}
			}
			if(hour >=14 && (hour < 19 )) {
				if(hour == 18 && minute > 00) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 获取 一个月中 最后一天 是不是星期天
	 * @param calendar
	 * @return
	 */
	private static boolean isMouthLastDay(Calendar calendar) {
		Calendar newCalendar = calendar;
		newCalendar.add(Calendar.DAY_OF_MONTH, 1);
		if(newCalendar.get(Calendar.DAY_OF_MONTH) ==1) {
			newCalendar.add(Calendar.DAY_OF_MONTH, -1);
			if(newCalendar.get(Calendar.DAY_OF_WEEK) == 1) {
				return true;
			}
		}
		return false;
	}
}
