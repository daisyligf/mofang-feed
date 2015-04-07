package com.mofang.feed.global.common;

/**
 * 主题类型
 * @author zhaodx
 *
 */
public class ThreadType 
{
	/**
	 * 普通Feed主题
	 */
	public final static int NORMAL = 1;
	
	/**
	 * 活动主题(根据link_url判断)
	 */
	public final static int ACTIVITY = 2;
	
	/**
	 * 提问主题
	 */
	public final static int QUESTION = 3;
}