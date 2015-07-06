package com.mofang.feed.logic.app;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedPost;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostLogic
{
	public ResultValue add(FeedPost model) throws Exception;
	
	public ResultValue delete(long postId, long operatorId, String reason) throws Exception;
	
	public ResultValue recommend(long userId, long postId) throws Exception;
	
	public ResultValue getInfo(long postId) throws Exception;
	
	public ResultValue getThreadPostList(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception;
	
	public ResultValue getHostPostList(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception;
	
	public ResultValue getUserReplyList(long userId, int pageNum, int pageSize) throws Exception;
}