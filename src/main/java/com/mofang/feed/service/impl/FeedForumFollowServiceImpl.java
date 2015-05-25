package com.mofang.feed.service.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForumFollow;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedForumFollowDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedForumFollowDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.service.FeedForumFollowService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumFollowServiceImpl implements FeedForumFollowService
{
	private final static FeedForumFollowServiceImpl SERVICE = new FeedForumFollowServiceImpl();
	private FeedForumFollowDao followDao = FeedForumFollowDaoImpl.getInstance();
	private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	
	private FeedForumFollowServiceImpl()
	{}
	
	public static FeedForumFollowServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public boolean isFollow(long forumId, long userId) throws Exception
	{
		try
		{
			return followDao.isFollow(forumId, userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumFollowServiceImpl.isFollow throw an error.", e);
			throw e;
		}
	}
	
	@Override
	public void follow(long forumId, long userId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///更新版块关注总数
			forumRedis.incrFollows(forumId);
			
			/******************************数据库操作******************************/
			///更新版块关注总数
			forumDao.incrFollows(forumId);
			///保存对应关系
			FeedForumFollow model = new FeedForumFollow();
			model.setForumId(forumId);
			model.setUserId(userId);
			model.setIsFollow(true);
			if(followDao.exists(forumId, userId))
				followDao.edit(model);
			else
				followDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumFollowServiceImpl.follow throw an error.", e);
			throw e;
		}
	}

	@Override
	public void cancel(long forumId, long userId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///更新版块关注总数
			forumRedis.decrFollows(forumId);
			
			/******************************数据库操作******************************/
			///更新版块关注总数
			forumDao.decrFollows(forumId);
			///保存对应关系
			FeedForumFollow model = new FeedForumFollow();
			model.setForumId(forumId);
			model.setUserId(userId);
			model.setIsFollow(false);
			if(followDao.exists(forumId, userId))
				followDao.edit(model);
			else
				followDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumFollowServiceImpl.cancel throw an error.", e);
			throw e;
		}
	}
}