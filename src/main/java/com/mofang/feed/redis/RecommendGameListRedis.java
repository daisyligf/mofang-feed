package com.mofang.feed.redis;

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
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Set<String> getList(String key, int start, int end) throws Exception;
	
	public long getForumCount(String key) throws Exception;
	
	public void delete(String key, long forumId) throws Exception;
	
	public void delete(String key) throws Exception;
}
