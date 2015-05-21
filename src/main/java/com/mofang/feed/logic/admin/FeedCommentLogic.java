package com.mofang.feed.logic.admin;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedCommentLogic
{
	public ResultValue delete(long commentId, long operatorId, String reason) throws Exception;
	
	public ResultValue restore(long commentId, long operatorId) throws Exception;
	
	public ResultValue remove(long commentId, long operatorId) throws Exception;
	
	public ResultValue getCommentList(long postId, int status, int pageNum, int pageSize) throws Exception;
	
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}