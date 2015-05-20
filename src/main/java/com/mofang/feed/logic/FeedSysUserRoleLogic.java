package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedSysUserRole;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysUserRoleLogic
{
	public ResultValue add(FeedSysUserRole model, long operatorId) throws Exception;
	
	public ResultValue edit(FeedSysUserRole model, long operatorId) throws Exception;
	
	public ResultValue delete(long forumId, long userId, long operatorId) throws Exception;
	
	public ResultValue getInfo(long forumId, long userId) throws Exception;
	
	public ResultValue getRoleInfoList(long forumId) throws Exception;
}