package com.mofang.feed.redis;

public interface FeedTagRedis {

	/**
	 * 初始化主键ID
	 * @throws Exception
	 */
	public void initUniqueId(int tagId) throws Exception;
	
	public void set(int tagId, String name) throws Exception;
	
	public String get(int tagId) throws Exception;
	
	public void delete(int tagId) throws Exception;
}
