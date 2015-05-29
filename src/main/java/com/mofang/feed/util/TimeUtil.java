package com.mofang.feed.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {

	private static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static long getInitDelay(int hour) {
		Calendar cl = Calendar.getInstance();
		cl.get(Calendar.HOUR_OF_DAY);
		cl.set(Calendar.HOUR_OF_DAY, hour);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.SECOND, 0);
		return cl.getTimeInMillis() - System.currentTimeMillis();
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

	public static void main(String[] args) {
		long time = getInitDelay(24);
		System.out.println(time);
		System.out.println(time / 1000 / 60 / 60);
	}

}
