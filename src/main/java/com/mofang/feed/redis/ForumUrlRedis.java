package com.mofang.feed.redis;

import java.util.Map;

public interface ForumUrlRedis {

	/***
	 * 
	 * @param forumId
	 * @param urlMap
	 * 					{
	 * 					   "download_url":xx
	 *                    "gift_url":xx
	 *                    "prefecture_url":xx
	 *                  }
	 * @throws Exception
	 */
	public void setUrl(long forumId, Map<String, String> urlMap) throws Exception;
	
	public void setUrl(long forumId, String giftUrl) throws Exception;
	
	public void delete(long forumId) throws Exception;
	
	public Map<String, String> getUrl(long forumId) throws Exception;
	
}
