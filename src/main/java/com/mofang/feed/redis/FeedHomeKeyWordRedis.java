package com.mofang.feed.redis;

public interface FeedHomeKeyWordRedis {

	public void setKeyWord(String word) throws Exception;
	
	public String getKeyWord() throws Exception;
}
