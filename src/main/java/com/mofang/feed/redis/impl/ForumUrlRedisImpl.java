package com.mofang.feed.redis.impl;

import java.util.Map;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.global.common.ForumURLKey;
import com.mofang.feed.redis.ForumUrlRedis;
import com.mofang.framework.data.redis.RedisWorker;

public class ForumUrlRedisImpl implements ForumUrlRedis {

	private static final ForumUrlRedisImpl REDIS = new ForumUrlRedisImpl();
	
	private ForumUrlRedisImpl(){}
	
	public static ForumUrlRedisImpl getInstance(){
		return REDIS;
	}
	
	@Override
	public void setUrl(final long forumId, final Map<String, String> urlMap)
			throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>(){

			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_EXTEND_KEY_PREFIX, forumId);
				jedis.hset(key, ForumURLKey.DOWNLOAD_URL_KEY, urlMap.get(ForumURLKey.DOWNLOAD_URL_KEY));
				jedis.hset(key, ForumURLKey.GIFT_URL_KEY, urlMap.get(ForumURLKey.GIFT_URL_KEY));
				jedis.hset(key, ForumURLKey.PREFECTURE_URL_KEY, urlMap.get(ForumURLKey.PREFECTURE_URL_KEY));
				return true;
			}
			
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public Map<String, String> getUrl(final long forumId) throws Exception {
		RedisWorker<Map<String, String>> worker = new RedisWorker<Map<String,String>>() {
			@Override
			public Map<String, String> execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_EXTEND_KEY_PREFIX, forumId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map || map.size() == 0)
					return null;
				return map;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

}
