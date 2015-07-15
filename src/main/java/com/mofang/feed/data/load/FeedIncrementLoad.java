package com.mofang.feed.data.load;

import com.mofang.feed.data.load.increment.FeedCommentLoad;
import com.mofang.feed.data.load.increment.FeedPostConflictLoad;
import com.mofang.feed.data.load.increment.FeedPostLoad;
import com.mofang.feed.data.load.increment.FeedThreadLoad;

public class FeedIncrementLoad
{
	public static void execute()
	{
		FeedLoad load = null;
		long start = System.currentTimeMillis();
		long itemStart = 0L;
		long itemEnd = 0L;
		
		System.out.println("thread data prepare load......");
		itemStart = System.currentTimeMillis();
		load = new FeedThreadLoad();
		load.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("thread data load completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		System.out.println("post data prepare load......");
		itemStart = System.currentTimeMillis();
		load = new FeedPostLoad();
		load.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("post data load completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		System.out.println("comment data prepare load......");
		itemStart = System.currentTimeMillis();
		load = new FeedCommentLoad();
		load.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("comment data load completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		System.out.println("post conflict data prepare load......");
		itemStart = System.currentTimeMillis();
		load = new FeedPostConflictLoad();
		load.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("post conflict data load completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		long end = System.currentTimeMillis();
		System.out.println("data load completed. cost time: " + (end - start) + " ms.");
	}
}