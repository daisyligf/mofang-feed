package com.mofang.feed.logic.web;

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
	
	public ResultValue getPostCommentList(long postId, int pageNum, int pageSize) throws Exception;
}