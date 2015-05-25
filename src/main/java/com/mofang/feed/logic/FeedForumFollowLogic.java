package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumFollowLogic
{
	public ResultValue follow(long forumId, long userId) throws Exception;
	
	public ResultValue cancel(long forumId, long userId) throws Exception;
}