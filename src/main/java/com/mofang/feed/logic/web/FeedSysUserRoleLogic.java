package com.mofang.feed.logic.web;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysUserRoleLogic
{
	public ResultValue getInfo(long forumId, long userId) throws Exception;
	
	public ResultValue getRoleList(long forumId) throws Exception;
	
	public ResultValue getUserRoleInfo(long userId) throws Exception;
}