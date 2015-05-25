package com.mofang.feed.logic.admin;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostLogic
{
	public ResultValue delete(long postId, long operatorId, String reason) throws Exception;
	
	public ResultValue restore(long postId, long operatorId) throws Exception;
	
	public ResultValue remove(long postId, long operatorId) throws Exception;
	
	public ResultValue getPostList(long threadId, int status, int pageNum, int pageSize) throws Exception;
	
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}