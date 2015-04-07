package com.mofang.feed.redis;

import com.mofang.feed.model.FeedSysRole;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysRoleRedis
{
	/**
	 * 生成主键ID
	 * @return
	 * @throws Exception
	 */
	public int makeUniqueId() throws Exception;
	
	/**
	 * 保存角色信息
	 * @param model 角色实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedSysRole model) throws Exception;
	
	/**
	 * 删除角色
	 * @param roleId 角色ID
	 * @return
	 * @throws Exception
	 */
	public void delete(int roleId) throws Exception;
	
	/**
	 * 获取角色信息
	 * @param roleId 角色ID
	 * @return
	 * @throws Exception
	 */
	public FeedSysRole getInfo(int roleId) throws Exception;
}