package com.mofang.feed.redis.impl;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.redis.FeedSysUserRoleRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysUserRoleRedisImpl implements FeedSysUserRoleRedis
{
	private final static FeedSysUserRoleRedisImpl REDIS = new FeedSysUserRoleRedisImpl();
	
	private FeedSysUserRoleRedisImpl()
	{}
	
	public static FeedSysUserRoleRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public boolean exists(final long forumId, final long userId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_ROLE_LIST_KEY_PREFIX, forumId);
				return jedis.hexists(key, String.valueOf(userId));
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void save(final FeedSysUserRole model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_ROLE_LIST_KEY_PREFIX, model.getForumId());
				jedis.hset(key, String.valueOf(model.getUserId()), String.valueOf(model.getRoleId()));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(final long forumId, final long userId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_ROLE_LIST_KEY_PREFIX, forumId);
				jedis.hdel(key, String.valueOf(userId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public int getUserRole(final long forumId, final long userId) throws Exception
	{
		RedisWorker<Integer> worker = new RedisWorker<Integer>()
		{
			@Override
			public Integer execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_ROLE_LIST_KEY_PREFIX, forumId);
				String value = jedis.hget(key, String.valueOf(userId));
				if(!StringUtil.isInteger(value))
					return 0;
				return Integer.parseInt(value);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
}