package com.mofang.feed.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author zhaodx
 *
 */
public class HtmlTagFilter
{
	/**
	 * 过滤所有HTML标签
	 * @param text
	 * @return
	 */
	public static String filterHtmlTag(String text)
	{
		String strRegex = "<[^>]+>";
		Pattern pattern = Pattern.compile(strRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		text = matcher.replaceAll(""); // 过滤html标签
		return text;
	}

	/**
	 * 过滤指定HTML标签(只保留font,b,u,i,img,a)
	 * @param text
	 * @return
	 */
	public static String filterOptionHtmlTag(String text)
	{
		String strRegex = "<(?!font|/font|b\\W|/b\\W|i\\W|/i\\W|u\\W|/u\\W|p\\W|/p\\W|div\\W|/div\\W|embed\\W|/embed\\W|a\\W|/a\\W|img|/img|br\\W)[^>]+>";
		Pattern pattern = Pattern.compile(strRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		text = matcher.replaceAll(""); // 过滤html标签
		return text;
	}
	
	/**
	 * 是否有img标签
	 * @param text
	 * @return
	 */
	public static boolean findImg(String text) {
		return Pattern.compile("<img.*src\\s*=\\s*(.*?)[^>]*?>").matcher(text).find();
	}
	
}