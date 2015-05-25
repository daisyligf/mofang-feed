package com.mofang.feed.redis;

public interface FeedTagRedis {

	public void set(int tagId, String name) throws Exception;
	
	public String get(int tagId) throws Exception;
	
	public void delete(int tagId) throws Exception;
}
