package com.mofang.feed.redis.impl;

import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.UserReplyDiffThreadIdRedis;
import com.mofang.feed.util.TimeUtil;
import com.mofang.framework.data.redis.RedisWorker;

public class UserReplyDiffThreadIdRedisImpl implements UserReplyDiffThreadIdRedis{

	private static final UserReplyDiffThreadIdRedisImpl REDIS = new UserReplyDiffThreadIdRedisImpl();
	
	private UserReplyDiffThreadIdRedisImpl(){}
	
	public static UserReplyDiffThreadIdRedisImpl getInstance() {
		return REDIS;
	}
	
	@Override
	public void addDiffThreadId(final long userId, final long threadId) throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>(){
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.USER_DIFF_THREAD_REPLY_TASK_PREFIX.concat(String.valueOf(userId));
				jedis.sadd(key, String.valueOf(threadId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addDiffThreadIdAndExpire(final long userId, final long threadId)
			throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.USER_DIFF_THREAD_REPLY_TASK_PREFIX.concat(String.valueOf(userId));
				jedis.sadd(key, String.valueOf(threadId));
				jedis.expireAt(key, TimeUtil.getTodayEndTime()/1000);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public Set<String> getDiffThreadIds(final long userId)
			throws Exception {
		String key = RedisKey.USER_DIFF_THREAD_REPLY_TASK_PREFIX.concat(String.valueOf(userId));
		return RedisFaster.smembers(key);
	}

}
