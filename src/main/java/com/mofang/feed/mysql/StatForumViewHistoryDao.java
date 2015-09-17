package com.mofang.feed.mysql;

import java.util.Map;
import java.util.Set;

import com.mofang.feed.model.StatForumViewHistory;
import com.mofang.feed.model.external.ForumCountByTime;

public interface StatForumViewHistoryDao {

	public void add(StatForumViewHistory model) throws Exception;
	
	public Map<Long, ForumCountByTime> getUV(Set<Long> forumIds, long startTime, long endTime) throws Exception;
}
