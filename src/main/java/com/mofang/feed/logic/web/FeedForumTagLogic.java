package com.mofang.feed.logic.web;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumTagLogic
{
	public ResultValue getTagList(long forumId) throws Exception;
}