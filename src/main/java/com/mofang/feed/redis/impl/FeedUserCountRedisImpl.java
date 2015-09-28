package com.mofang.feed.redis.impl;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.FeedUserCountRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.util.StringUtil;

public class FeedUserCountRedisImpl implements FeedUserCountRedis {

	private static final FeedUserCountRedisImpl REDIS = new FeedUserCountRedisImpl();
	
	private FeedUserCountRedisImpl(){}
	
	public static FeedUserCountRedisImpl getInstance() {
		return REDIS;
	}
	
	@Override
	public String userCountInfo(final long userId) throws Exception {
		RedisWorker<String> worker = new RedisWorker<String>() {
			@Override
			public String execute(Jedis jedis) throws Exception {
				String key = RedisKey.USER_COUNT_KEY_PREFIX.concat(String.valueOf(userId));
				String result = jedis.get(key);
				if(StringUtil.isNullOrEmpty(result))
					return null;
				return result;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public boolean saveAndExpire(final long userId, final long threadCount, final long postCount,
			final long commentCount, final long eliteThreadCount) throws Exception {
		
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.USER_COUNT_KEY_PREFIX.concat(String.valueOf(userId));
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("threads", threadCount);
				jsonObject.put("replies", postCount + commentCount);
				jsonObject.put("elite_threads", eliteThreadCount);
				//缓存2分钟
				jedis.setex(key, 2 * 60, jsonObject.toString());
				return true;
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

}
