package com.mofang.feed.redis.impl;

import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.HotForumListRedis;
import com.mofang.framework.data.redis.RedisWorker;

/***
 * 
 * @author linjx
 *
 */
public class HotForumListRedisImpl implements HotForumListRedis {

	@Override
	public void addHotForumList(String key, long forumId, long score)
			throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.HOT_FORUM_LIST_KEY_PREFIX, key);
		RedisFaster.zadd(key, score, forumId);
	}

	@Override
	public void setUrl(final long forumId, final Map<String, String> urlMap)
			throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>(){

			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_EXTEND_KEY_PREFIX, forumId);
				jedis.hset(key, "download_url", urlMap.get("download_url"));
				jedis.hset(key, "gift_url", urlMap.get("gift_url"));
				jedis.hset(key, "prefecture_url", urlMap.get("prefecture_url"));
				return true;
			}
			
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
   }

	@Override
	public Set<String> getList(String key, int start, int end) throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.HOT_FORUM_LIST_KEY_PREFIX, key);
		return RedisFaster.zrevrange(key, start, end) ;
	}

	@Override
	public long getForumCount(String key) throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.HOT_FORUM_LIST_KEY_PREFIX, key);
		return RedisFaster.zcard(key);
	}

}
