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
	public ResultValue apply(FeedModeratorApply model) throws Exception;
}