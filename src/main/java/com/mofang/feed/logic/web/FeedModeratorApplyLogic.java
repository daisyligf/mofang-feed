package com.mofang.feed.logic.web;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedModeratorApply;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModeratorApplyLogic
{	
	public ResultValue check(long forumId, long userId) throws Exception;
	
	public ResultValue apply(FeedModeratorApply model) throws Exception;
}