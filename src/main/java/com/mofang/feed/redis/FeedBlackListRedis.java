package com.mofang.feed.redis;

import java.util.List;

import com.mofang.feed.model.FeedBlackList;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedBlackListRedis
{
	/**
	 * 保存黑名单信息
	 * @param model 黑名单实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedBlackList model) throws Exception;
	
	/**
	 * 判断用户在指定版块是否为黑名单用户
	 * @param forumId 版块ID
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public boolean exists(long forumId, long userId) throws Exception;
	
	/**
	 * 删除黑名单
	 * @param forumId 版块ID
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public void delete(long forumId, long userId) throws Exception;
	
	/**
	 * 获取版块黑名单用户列表
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public List<Long> getUserListByForumId(long forumId) throws Exception;
}