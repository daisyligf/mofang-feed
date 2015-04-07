package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedUserFavorite;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedUserFavoriteLogic
{
	public ResultValue add(FeedUserFavorite model) throws Exception;
	
	public ResultValue delete(long userId, long threadId) throws Exception;
}