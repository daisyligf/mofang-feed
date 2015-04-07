package com.mofang.feed.data.load;

import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class LoadManager
{
	public static void execute(String loadList)
	{
		if(StringUtil.isNullOrEmpty(loadList))
			return;
		
		String[] arrloads = loadList.split(",");
		FeedLoad load = null;
		long start = System.currentTimeMillis();
		long itemStart = 0L;
		long itemEnd = 0L;
		for(String loadItem : arrloads)
		{
			System.out.println(loadItem + " prepare load data......");
			itemStart = System.currentTimeMillis();
			load = LoadFactory.getInstance(loadItem);
			if(null == load)
				continue;
			
			load.exec();
			itemEnd = System.currentTimeMillis();
			System.out.println(loadItem + " data load completed. cost time: " + (itemEnd - itemStart) + " ms.");
		}
		long end = System.currentTimeMillis();
		System.out.println("data load completed. cost time: " + (end - start) + " ms.");
	}
}