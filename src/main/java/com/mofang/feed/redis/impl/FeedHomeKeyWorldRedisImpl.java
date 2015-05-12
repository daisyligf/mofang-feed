package com.mofang.feed.redis.impl;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.FeedHomeKeyWordRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.util.StringUtil;

/***
 * 
 * @author linjx
 * 
 */
public class FeedHomeKeyWorldRedisImpl implements FeedHomeKeyWordRedis {

	private final static FeedHomeKeyWorldRedisImpl REDIS = new FeedHomeKeyWorldRedisImpl();

	private FeedHomeKeyWorldRedisImpl() {
	}

	public static FeedHomeKeyWorldRedisImpl getInstance() {
		return REDIS;
	}

	@Override
	public void setKeyWord(final String word) throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.HOME_DEFAULT_KEY_WORD_KEY;
				if (StringUtil.isNullOrEmpty(word))
					return false;
				jedis.set(key, word);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public String getKeyWord() throws Exception {
		RedisWorker<String> worker = new RedisWorker<String>() {
			@Override
			public String execute(Jedis jedis) throws Exception {
				String key = RedisKey.HOME_DEFAULT_KEY_WORD_KEY;
				return jedis.get(key);
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

}
