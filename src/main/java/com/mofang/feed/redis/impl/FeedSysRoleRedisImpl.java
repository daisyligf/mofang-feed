package com.mofang.feed.redis.impl;

import java.util.Map;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.redis.FeedSysRoleRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.DeleteWorker;
import com.mofang.framework.data.redis.workers.IncrWorker;
import com.mofang.framework.data.redis.workers.SetWorker;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysRoleRedisImpl implements FeedSysRoleRedis
{
	private final static FeedSysRoleRedisImpl REDIS = new FeedSysRoleRedisImpl();
	
	private FeedSysRoleRedisImpl()
	{}
	
	public static FeedSysRoleRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public int makeUniqueId() throws Exception
	{
		String key = RedisKey.ROLE_INCREMENT_ID_KEY;
		RedisWorker<Long> worker = new IncrWorker(key);
		Long value = GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
		return value.intValue();
	}

	@Override
	public void initUniqueId(int roleId) throws Exception
	{
		String key = RedisKey.ROLE_INCREMENT_ID_KEY;
		RedisWorker<Boolean> worker = new SetWorker(key, String.valueOf(roleId));
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void save(final FeedSysRole model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.ROLE_INFO_KEY_PREFIX, model.getRoleId());
				jedis.hmset(key, model.toMap());
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(int roleId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.ROLE_INFO_KEY_PREFIX, roleId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public FeedSysRole getInfo(final int roleId) throws Exception
	{
		RedisWorker<FeedSysRole> worker = new RedisWorker<FeedSysRole>()
		{
			@Override
			public FeedSysRole execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.ROLE_INFO_KEY_PREFIX, roleId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map || map.size() == 0)
					return null;
				
				return new FeedSysRole(map);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
}