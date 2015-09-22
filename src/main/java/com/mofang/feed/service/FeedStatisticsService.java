package com.mofang.feed.service;

import java.util.Map;
import java.util.Set;

import com.mofang.feed.model.external.ForumStatisticsInfo;

public interface FeedStatisticsService {

	public Map<Long, ForumStatisticsInfo> forumStatisticsInfos(Set<Long> forumIdSet, long startTime, long endTime, int type) throws Exception;
}
