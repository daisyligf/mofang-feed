package com.mofang.feed.logic.admin;

import java.util.Set;

import com.mofang.feed.global.ResultValue;

public interface FeedStatisticsLogic {

	public ResultValue forumStatisticsInfos(Set<Long> forumIds, long startTime, long endTime) throws Exception;
}
