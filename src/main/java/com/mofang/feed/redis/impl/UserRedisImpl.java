package com.mofang.feed.redis.impl;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.external.User;
import com.mofang.feed.redis.UserRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class UserRedisImpl implements UserRedis
{
	private final static UserRedisImpl REDIS = new UserRedisImpl();
	
	private UserRedisImpl()
	{}
	
	public static UserRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public void save(final User model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.CACHE_USER_KEY_PREFIX, model.getUserId());
				JSONObject json = model.toJson();
				if(null == json)
					return false;
				
				jedis.set(key, json.toString());
				jedis.expire(key, GlobalConfig.USER_EXPIRE_SECONDS);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public User getInfo(final long userId) throws Exception
	{
		RedisWorker<User> worker = new RedisWorker<User>()
		{
			@Override
			public User execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.CACHE_USER_KEY_PREFIX, userId);
				String value = jedis.get(key);
				if(StringUtil.isNullOrEmpty(value))
					return null;
				
				JSONObject json = new JSONObject(value);
				return new User(json);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
}