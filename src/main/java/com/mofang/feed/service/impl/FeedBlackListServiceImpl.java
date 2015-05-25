package com.mofang.feed.service.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedBlackList;
import com.mofang.feed.mysql.FeedBlackListDao;
import com.mofang.feed.mysql.impl.FeedBlackListDaoImpl;
import com.mofang.feed.redis.FeedBlackListRedis;
import com.mofang.feed.redis.impl.FeedBlackListRedisImpl;
import com.mofang.feed.service.FeedBlackListService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedBlackListServiceImpl implements FeedBlackListService
{
	private final static FeedBlackListServiceImpl SERVICE = new FeedBlackListServiceImpl();
	private FeedBlackListRedis blackListRedis = FeedBlackListRedisImpl.getInstance();
	private FeedBlackListDao blackListDao = FeedBlackListDaoImpl.getInstance();
	
	private FeedBlackListServiceImpl()
	{}
	
	public static FeedBlackListServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(FeedBlackList model) throws Exception
	{
		try
		{
			///保存到redis中
			blackListRedis.save(model);
			///保存到mysql中
			blackListDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedBlackListServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long forumId, long userId) throws Exception
	{
		try
		{
			///从redis中删除
			blackListRedis.delete(forumId, userId);
			///从mysql中删除
			blackListDao.delete(forumId, userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedBlackListServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean exists(long forumId, long userId) throws Exception
	{
		try
		{
			return blackListRedis.exists(forumId, userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedBlackListServiceImpl.exists throw an error.", e);
			throw e;
		}
	}

	@Override
	public JSONObject getUserList(long forumId) throws Exception
	{
		try
		{
			JSONObject data = new JSONObject();
			List<Long> uidList = blackListRedis.getUserListByForumId(forumId);
			if(null == uidList || uidList.size() == 0)
			{
				data.put("total", 0);
				data.put("users", new JSONArray());
				return data;
			}
			
			JSONArray arrayUsers = new JSONArray();
			JSONObject jsonItem = null;
			for(long uid : uidList)
			{
				jsonItem = new JSONObject();
				arrayUsers.put(jsonItem);
			}
			data.put("users", arrayUsers);
			return data;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedBlackListServiceImpl.exists throw an error.", e);
			throw e;
		}
	}
}