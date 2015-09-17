package com.mofang.feed.mysql;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FeedStatisticsDao {

	public List<Object[]> forumNameList(int start, int end) throws Exception;
	
	public List<Object[]> forumNameList(Set<Long> forumIdSet) throws Exception;
	
	public Map<Long, Integer> forumThreadCount(Set<Long> forumIdSet, long startTime, long endTime) throws Exception;
	
	public Map<Long, Integer> forumPostCount(Set<Long> forumIdSet, long startTime, long endTime) throws Exception;
	
	public Map<Long, Integer> forumCommentCount(Set<Long> forumIdSet, long startTime, long endTime) throws Exception;
	
}
