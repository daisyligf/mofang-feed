package com.mofang.feed.mysql;

import java.util.Map;
import java.util.Set;

import com.mofang.feed.model.FeedForumFollow;
import com.mofang.feed.model.external.ForumCount;

public interface FeedForumFollowDao {

	public boolean isFollow(long forumId, long userId) throws Exception;
	
	public boolean exists(long forumId, long userId) throws Exception;
	
	public void add(FeedForumFollow model) throws Exception;
	
	public void edit(FeedForumFollow model) throws Exception;
	
	public long getFollowTime(long forumId, long userId) throws Exception;
	
	public Map<Long, ForumCount> getFollowCount(Set<Long> forumIds, long startTime, long endTime) throws Exception;
}
