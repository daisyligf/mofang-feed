package com.mofang.feed.redis.impl;

import java.util.Map;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.external.UserSignIn;
import com.mofang.feed.redis.FeedUserSignInRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mysql.jdbc.StringUtils;

public class FeedUserSignInRedisImpl implements FeedUserSignInRedis {

	private static final FeedUserSignInRedisImpl REDIS = new FeedUserSignInRedisImpl();
	
	private FeedUserSignInRedisImpl(){}
	
	public static FeedUserSignInRedisImpl getInstance(){
		return REDIS;
	}
	
	@Override
	public void update(final long userId, final long signInTime, final int days)
			throws Exception {
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.USER_SIGN_IN_KEY_PREFIX, userId);
				jedis.hset(key, "last_sign_in_time", String.valueOf(signInTime));
				jedis.hset(key, "days", String.valueOf(days));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public UserSignIn getInfo(final long userId) throws Exception {
		RedisWorker<UserSignIn> worker = new RedisWorker<UserSignIn>() {
			@Override
			public UserSignIn execute(Jedis jedis) throws Exception {
				String key = RedisKey.buildRedisKey(RedisKey.USER_SIGN_IN_KEY_PREFIX, userId);
				Map<String, String> map = jedis.hgetAll(key);
				if(map == null || map.size() == 0) {
					return null;
				}
				String strSignInTime = map.get("last_sign_in_time");
				String strDays = map.get("days");
				long signInTime = 0l;
				int days = 0;
				if(!StringUtils.isNullOrEmpty(strSignInTime))
					signInTime = Long.valueOf(strSignInTime);
				if(!StringUtils.isNullOrEmpty(strDays))
					days = Integer.valueOf(strDays);
				UserSignIn userSignIn = new UserSignIn();
				userSignIn.lastSignInTime = signInTime;
				userSignIn.days = days;
				return userSignIn;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}


}
