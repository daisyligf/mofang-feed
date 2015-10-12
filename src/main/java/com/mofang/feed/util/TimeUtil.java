package com.mofang.feed.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {

	public static long getInitDelay(int hour) {
		Calendar cl = Calendar.getInstance();
		cl.get(Calendar.HOUR_OF_DAY);
		cl.set(Calendar.HOUR_OF_DAY, hour);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.SECOND, 0);
		return cl.getTimeInMillis() - System.currentTimeMillis();
	}
	
	public static long getTodayStartTime(){
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.HOUR_OF_DAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTimeInMillis();
	}
	
	public static long getTodayEndTime(){
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.HOUR_OF_DAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTimeInMillis();
	}

	public static long getYesterdyStartTime() {
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.HOUR_OF_DAY);
		cal.add(Calendar.DATE,   -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTimeInMillis();
	}

	public static long getYesterdyEndTime() {
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.HOUR_OF_DAY);
		cal.add(Calendar.DATE,   -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTimeInMillis();
	}
	
	/***
	 * 7天前的开始时间
	 * @return
	 */
	public static long getLastSevenDayStartTime() {
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.HOUR_OF_DAY);
		cal.add(Calendar.DATE,   -7);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTimeInMillis();
	}
	
	public static boolean isSameDay(long timeA, long timeB) {
		Calendar calA = Calendar.getInstance();
		calA.setTimeInMillis(timeA);
		
		Calendar calB = Calendar.getInstance();
		calB.setTimeInMillis(timeB);
		
		 return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR)
		            && calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH)
		            &&  calA.get(Calendar.DAY_OF_MONTH) == calB.get(Calendar.DAY_OF_MONTH);
	}

	public static void main(String[] args) {
		SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		long startTime = getLastSevenDayStartTime();
		System.out.println(format.format(startTime));
		System.out.println(format.format(getTodayStartTime()));
	}

}
