package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;

public interface FeedUserLogic {

	public ResultValue getInfo(long userId) throws Exception;
	
	public ResultValue setProhibit(long forumId, long userId, long operatorId) throws Exception;
	
	public ResultValue cancelProhibit(long forumId, long userId, long operatorId) throws Exception;
}
