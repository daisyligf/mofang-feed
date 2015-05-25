package com.mofang.feed.redis.impl;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.FeedTagRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.util.StringUtil;

public class FeedTagRedisImpl implements FeedTagRedis {
	
	private static final FeedTagRedisImpl REDIS = new FeedTagRedisImpl();
	
	private FeedTagRedisImpl(){}
	
	public static FeedTagRedisImpl getInstance(){
		return REDIS;
	}

	@Override
	public void set(final int tagId, final String name) throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.TAG_NAME_KEY_PREFIX, tagId);
				if (StringUtil.isNullOrEmpty(name))
					return false;
				jedis.set(key, name);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public String get(final int tagId) throws Exception {
		RedisWorker<String> worker = new RedisWorker<String>() {
			@Override
			public String execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.TAG_NAME_KEY_PREFIX, tagId);
				return jedis.get(key);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(final int tagId) throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.TAG_NAME_KEY_PREFIX, tagId);
				jedis.del(key);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

}
