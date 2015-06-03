package com.mofang.feed.logic.app;

import java.util.Set;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumLogic
{
	public ResultValue getInfo(long forumId) throws Exception;
	
	public ResultValue getForumList(Set<Long> forumIds) throws Exception;
	
	public ResultValue search(String forumName, int pageNum, int pageSize) throws Exception;
}