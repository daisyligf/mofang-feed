package com.mofang.feed.mysql;

import com.mofang.feed.model.StatForumViewHistory;

public interface StatForumViewHistoryDao {

	public void add(StatForumViewHistory model) throws Exception;
	
	public long getUV(long startTime, long endTime) throws Exception;
}
