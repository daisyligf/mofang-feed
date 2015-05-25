package com.mofang.feed.service;

import java.util.Set;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumFollowService
{
	public boolean isFollow(long forumId, long userId) throws Exception;
	
	public void follow(long forumId, long userId) throws Exception;
	
	public void cancel(long forumId, long userId) throws Exception;
	
	public Set<Long> getForumIdList(long userId) throws Exception;
}