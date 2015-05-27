package com.mofang.feed.redis.impl;

import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.ThreadReplyHighestListRedis;
import com.mofang.framework.data.redis.RedisWorker;

public class ThreadReplyHighestListRedisImpl implements
		ThreadReplyHighestListRedis {

	public static final ThreadReplyHighestListRedisImpl REDIS = new ThreadReplyHighestListRedisImpl();
	
	private ThreadReplyHighestListRedisImpl(){}
	
	public static ThreadReplyHighestListRedisImpl getInstance(){
		return REDIS;
	}
	
	@Override
	public void add(final long forumId, final long threadId) throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.REPLYHIGHEST_THREAD_KEY_PREFIX, forumId);
				jedis.sadd(key, String.valueOf(threadId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public Set<String> getThreadIdList(final long forumId) throws Exception {
		RedisWorker<Set<String>> worker = new RedisWorker<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.REPLYHIGHEST_THREAD_KEY_PREFIX, forumId);
				Set<String> set = jedis.zrevrange(key, 0, 6);
				return set;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

}
