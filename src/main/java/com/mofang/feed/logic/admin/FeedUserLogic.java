package com.mofang.feed.logic.admin;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedUserLogic
{
	public ResultValue setProhibit(long forumId, long userId, long operatorId) throws Exception;
	
	public ResultValue cancelProhibit(long forumId, long userId, long operatorId) throws Exception;
	
	public ResultValue getInfo(long userId) throws Exception;
}