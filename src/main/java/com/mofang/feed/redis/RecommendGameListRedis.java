package com.mofang.feed.redis;

import java.util.Map;
import java.util.Set;

public interface RecommendGameListRedis {

	/***
	 * 
	 * @param key
	 *                字母分组=ABCDE
	 * @param forumId
	 * @param score
	 * @throws Exception
	 */
	public void addRecommendGameList(String key, long forumId, long score) throws Exception;
	
	/***
	 * 
	 * @param forumId
	 * @param urlJson
	 * 					{
	 * 					   "download_url":xx
	 *                    "gift_url":xx
	 *                    "prefecture_url":xx
	 *                  }
	 * @throws Exception
	 */
	public void setUrl(long forumId, Map<String, String> urlMap) throws Exception;
	
	/***
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Set<String> getList(String key, int start, int end) throws Exception;
	
	public long getForumCount(String key) throws Exception;
}
