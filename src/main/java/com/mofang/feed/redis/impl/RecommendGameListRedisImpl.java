package com.mofang.feed.redis.impl;

import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.global.common.ForumURLKey;
import com.mofang.feed.redis.RecommendGameListRedis;
import com.mofang.framework.data.redis.RedisWorker;

/***
 * 
 * @author linjx
 *
 */
public class RecommendGameListRedisImpl implements RecommendGameListRedis {

	private static final RecommendGameListRedisImpl REDIS = new RecommendGameListRedisImpl();
	
	private RecommendGameListRedisImpl(){}
	
	public static RecommendGameListRedisImpl getInstance(){
		return REDIS;
	}
	
	@Override
	public void addRecommendGameList(String key, long forumId, long score)
			throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.RECOMMEND_GAME_LIST_KEY_PREFIX, key);
		RedisFaster.zadd(key, score, forumId);
	}

	@Override
	public void setUrl(final long forumId, final Map<String, String> urlMap) throws Exception {
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
	public Set<String> getList(String key, int start, int end) throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.RECOMMEND_GAME_LIST_KEY_PREFIX, key);
		return RedisFaster.zrevrange(key, start, end) ;
	}

	@Override
	public long getForumCount(String key) throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.RECOMMEND_GAME_LIST_KEY_PREFIX, key);
		return RedisFaster.zcard(key);
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