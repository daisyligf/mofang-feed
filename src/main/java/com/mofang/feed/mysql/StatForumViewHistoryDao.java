package com.mofang.feed.mysql;

import java.util.List;
import java.util.Set;

import com.mofang.feed.model.StatForumViewHistory;
import com.mofang.feed.model.external.ForumCount;

public interface StatForumViewHistoryDao {

	public void add(StatForumViewHistory model) throws Exception;
	
	public List<ForumCount> getUV(Set<Long> forumIds, long startTime, long endTime) throws Exception;
}
