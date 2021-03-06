package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.model.Page;

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
	
	public List<FeedSysUserRole> getUserListByForumId(long forumId) throws Exception;
	
	public Page<FeedSysUserRole> getUserList(int pageNum, int pageSize) throws Exception;
	
	public List<FeedSysUserRole> getForumListByUserId(long userId) throws Exception;
	
	public Page<FeedSysUserRole> searchByUserId(long userId, int pageNum, int pageSize) throws Exception;
}