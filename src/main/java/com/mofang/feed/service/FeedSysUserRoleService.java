package com.mofang.feed.service;

import com.mofang.feed.model.FeedSysUserRole;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysUserRoleService
{
	public boolean exists(long forumId, long userId) throws Exception;
	
	public boolean isFull(long forumId) throws Exception;
	
	public void save(FeedSysUserRole model) throws Exception;
	
	public void delete(long forumId, long userId) throws Exception;
	
	public int getRoleId(long forumId, long userId) throws Exception;
	
	public boolean hasPrivilege(long forumId, long userId, int privilegeId) throws Exception;
}