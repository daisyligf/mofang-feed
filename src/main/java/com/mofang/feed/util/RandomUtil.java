package com.mofang.feed.util;

public class RandomUtil {

	/**
	 * 在min,max固定范围内生成随机整型值
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(int min, int max) {
		if (min == max) {
			return min;
		}
		if (max < min) {
			int oldMax = max;
			max = min;
			min = oldMax;
		}
		return (int) (Math.random() * (max - min + 1)) + min;
	}
}
