package com.mofang.feed.mysql;

import com.mofang.feed.model.FeedOperateHistory;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedOperateHistoryDao
{
	public void add(FeedOperateHistory model) throws Exception;
}