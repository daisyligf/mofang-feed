package com.mofang.feed.service;

import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.FeedUserFavorite;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedUserFavoriteService
{
	public void add(FeedUserFavorite model) throws Exception;
	
	public void delete(long userId, long threadId) throws Exception;
	
	public boolean exists(long userId, long threadId) throws Exception;
	
	public Page<FeedThread> getUserFavoriteThreadList(long userId, int pageNum, int pageSize) throws Exception;
}