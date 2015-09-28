package com.mofang.feed.redis;

public interface FeedUserCountRedis {

	public String userCountInfo(long userId) throws Exception;
	
	public boolean saveAndExpire(long userId, long threadCount, long postCount, long commentCount, long eliteThreadCount) throws Exception;
	
}
