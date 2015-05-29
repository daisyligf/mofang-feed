package com.mofang.feed.redis.impl;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.FeedAdminUserRedis;
import com.mofang.framework.data.redis.RedisWorker;

public class FeedAdminUserRedisImpl implements FeedAdminUserRedis {

	private static final FeedAdminUserRedisImpl REDIS = new FeedAdminUserRedisImpl();
	
	private FeedAdminUserRedisImpl(){}
	
	public static FeedAdminUserRedisImpl getInstance(){
		return REDIS;
	}
	
	@Override
	public boolean exists(final long userId) throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.ADMIN_USER_LIST_KEY;
				return jedis.sismember(key, String.valueOf(userId));
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void add(final long userId) throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.ADMIN_USER_LIST_KEY;
				jedis.sadd(key, String.valueOf(userId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(long userId) throws Exception {
		String key = RedisKey.ADMIN_USER_LIST_KEY;
		RedisFaster.srem(key, String.valueOf(userId));
	}

}
