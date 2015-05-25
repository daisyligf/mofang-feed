package com.mofang.feed.redis;

import com.mofang.feed.model.FeedSysUserRole;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysUserRoleRedis
{
	/**
	 * 判断角色是否存在于指定版块中
	 * @param forumId 版块ID
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public boolean exists(long forumId, long userId) throws Exception;
	
	/**
	 * 保存用户角色信息
	 * @param model 用户角色实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedSysUserRole model) throws Exception;
	
	/**
	 * 删除用户角色
	 * @param forumId 版块ID
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public void delete(long forumId, long userId) throws Exception;
	
	/**
	 * 获取用户角色
	 * @param forumId 版块ID
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public int getUserRole(long forumId, long userId) throws Exception;
}