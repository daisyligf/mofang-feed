package com.mofang.feed.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.FeedBlackList;
import com.mofang.feed.redis.FeedBlackListRedis;
import com.mofang.framework.data.redis.RedisWorker;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedBlackListRedisImpl implements FeedBlackListRedis
{
	private final static FeedBlackListRedisImpl REDIS = new FeedBlackListRedisImpl();
	
	private FeedBlackListRedisImpl()
	{}
	
	public static FeedBlackListRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public void save(final FeedBlackList model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_BLACK_LIST_KEY_PREFIX, model.getForumId());
				jedis.sadd(key, String.valueOf(model.getUserId()));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public boolean exists(final long forumId, final long userId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_BLACK_LIST_KEY_PREFIX, forumId);
				return jedis.sismember(key, String.valueOf(userId));
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(final long forumId, final long userId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_BLACK_LIST_KEY_PREFIX, forumId);
				jedis.srem(key, String.valueOf(userId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public List<Long> getUserListByForumId(final long forumId) throws Exception
	{
		RedisWorker<List<Long>> worker = new RedisWorker<List<Long>>()
		{
			@Override
			public List<Long> execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_BLACK_LIST_KEY_PREFIX, forumId);
				Set<String> set = jedis.smembers(key);
				if(null == set || set.size() == 0)
					return null;
				
				List<Long> list = new ArrayList<Long>();
				for(String item : set)
					list.add(Long.parseLong(item));
				return list;
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
}