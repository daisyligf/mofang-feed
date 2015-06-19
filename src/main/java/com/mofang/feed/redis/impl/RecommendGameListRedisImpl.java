package com.mofang.feed.redis.impl;

import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.RecommendGameListRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.DeleteWorker;

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
	public void delete(String key, long forumId) throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.RECOMMEND_GAME_LIST_KEY_PREFIX, key);
		RedisFaster.zrem(key, forumId);
	}

	@Override
	public void delete(String key) throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.RECOMMEND_GAME_LIST_KEY_PREFIX, key);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

}
