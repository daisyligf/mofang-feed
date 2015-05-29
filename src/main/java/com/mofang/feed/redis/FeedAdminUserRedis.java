package com.mofang.feed.redis;

public interface FeedAdminUserRedis {

	public boolean exists(long userId) throws Exception;
	
	public void add(long userId) throws Exception;
	
	public void delete(long userId) throws Exception;
}
