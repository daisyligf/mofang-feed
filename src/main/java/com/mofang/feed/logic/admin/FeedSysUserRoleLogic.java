package com.mofang.feed.logic.admin;

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
	
	public ResultValue delete(long forumId, long userId, long operatorId) throws Exception;
	
	public ResultValue addToAdmin(long userId, long operatorId) throws Exception;
	
	public ResultValue getModeratorList(int pageNum, int pageSize) throws Exception;
	
	public ResultValue searchByUserId(long userId, int pageNum, int pageSize) throws Exception;
}