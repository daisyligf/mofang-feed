package com.mofang.feed.redis.impl;

import java.util.Set;

import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.HotForumListRedis;

/***
 * 
 * @author linjx
 *
 */
public class HotForumListRedisImpl implements HotForumListRedis {

	private static final HotForumListRedisImpl REDIS = new HotForumListRedisImpl();
	
	private HotForumListRedisImpl(){}
	
	public static HotForumListRedisImpl getInstance(){
		return REDIS;
	}
	
	@Override
	public void addHotForumList(String key, long forumId, long score)
			throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.HOT_FORUM_LIST_KEY_PREFIX, key);
		RedisFaster.zadd(key, score, forumId);
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

	@Override
	public void delete(String key, long forumId) throws Exception {
		key = RedisKey.buildRedisKey(RedisKey.HOT_FORUM_LIST_KEY_PREFIX, key);
		RedisFaster.zrem(key, forumId);
	}

}
