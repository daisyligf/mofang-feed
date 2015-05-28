package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysUserRoleDao
{
	public boolean exists(long forumId, long userId) throws Exception;
	
	public void add(FeedSysUserRole model) throws Exception;
	
	public void update(FeedSysUserRole model) throws Exception;
	
	public void delete(long forumId, long userId) throws Exception;
	
	public void deleteByRoleId(int roleId) throws Exception;
	
	public FeedSysUserRole getInfo(long forumId, long userId) throws Exception;
	
	public List<FeedSysUserRole> getList(Operand operand) throws Exception;
	
	public List<FeedSysUserRole> getListByRoleId(int roleId) throws Exception;
	
	public long getCountByForumId(long forumId) throws Exception;
	
	public List<Integer> getRoleIdListByForumId(long forumId) throws Exception;
	
	public List<FeedSysUserRole> getUserList(int start, int end) throws Exception;
	
	public long getUserCount() throws Exception;
	
	public List<FeedSysUserRole> getForumListByUserId(long userId) throws Exception;
}