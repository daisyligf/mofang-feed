package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.common.RequestFrom;
import com.mofang.feed.model.FeedPost;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostLogic
{
	public ResultValue add(FeedPost model) throws Exception;
	
	public ResultValue edit(FeedPost model, long operatorId) throws Exception;
	
	public ResultValue delete(long postId, long operatorId, String reason) throws Exception;
	
	public ResultValue restore(long postId, long operatorId) throws Exception;
	
	public ResultValue remove(long postId, long operatorId) throws Exception;
	
	public ResultValue recommend(long userId, long postId) throws Exception;
	
	public ResultValue getInfo(long postId) throws Exception;
	
	public ResultValue getPostList(long threadId, int status, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getThreadPostList(long threadId, int pageNum, int pageSize, long currentUserId, RequestFrom from) throws Exception;
	
	public ResultValue getHostPostList(long threadId, int pageNum, int pageSize, long currentUserId, RequestFrom from) throws Exception;
	
	public ResultValue getUserPostList(long userId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getUserReplyList(long userId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}