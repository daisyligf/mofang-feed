package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedComment;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedCommentLogic
{
	public ResultValue add(FeedComment model) throws Exception;
	
	public ResultValue delete(long commentId, long operatorId, String reason) throws Exception;
	
	public ResultValue restore(long commentId, long operatorId) throws Exception;
	
	public ResultValue remove(long commentId, long operatorId) throws Exception;
	
	public ResultValue getCommentList(long postId, int status, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getPostCommentList(long postId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getUserCommentList(long userId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}