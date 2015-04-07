package com.mofang.feed.service;

import org.json.JSONObject;

import com.mofang.feed.model.FeedBlackList;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedBlackListService
{
	/**
	 * 将用户添加到指定版块的黑名单中
	 * @param model 黑名单实体信息
	 * @throws Exception
	 */
	public void add(FeedBlackList model) throws Exception;
	
	/**
	 * 将用户从指定版块的黑名单中删除
	 * @param forumId 版块ID
	 * @param userId 用户ID
	 * @throws Exception
	 */
	public void delete(long forumId, long userId) throws Exception;
	
	/**
	 * 判断用户是否存在指定版块的黑名单中
	 * @param forumId 版块ID
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public boolean exists(long forumId, long userId) throws Exception;
	
	/**
	 * 获取指定版块的黑名单用户列表
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUserList(long forumId) throws Exception;
}