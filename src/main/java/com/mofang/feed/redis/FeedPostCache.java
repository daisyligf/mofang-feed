package com.mofang.feed.redis;

/**
 * 
 * @author milo
 *
 */
public interface FeedPostCache
{
	public String getThreadPostList(long threadId, int pageNum, int pageSize) throws Exception;
	
	public void setThreadPostList(long threadId, int pageNum, int pageSize, String cache) throws Exception;
	
	public String getHostPostList(long threadId, long userId, int pageNum, int pageSize) throws Exception;
	
	public void setHostPostList(long threadId, long userId, int pageNum, int pageSize, String cache) throws Exception;
	
	public String getPostListFromPostId(long threadId, long postId, int pageSize) throws Exception;
	
	public void setPostListFromPostId(long threadId, long postId, int pageSize, String cache) throws Exception;
}