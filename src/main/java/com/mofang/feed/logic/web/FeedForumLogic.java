package com.mofang.feed.logic.web;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumLogic
{
	public ResultValue getInfo(long forumId) throws Exception;
	
	public ResultValue getForumList(int type, int pageNum, int pageSize) throws Exception;
	
	public ResultValue search(String forumName, int pageNum, int pageSize) throws Exception;
}