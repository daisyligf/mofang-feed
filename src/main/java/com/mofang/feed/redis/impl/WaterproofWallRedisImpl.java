package com.mofang.feed.redis.impl;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.GetWorker;
import com.mofang.framework.data.redis.workers.SetWorker;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class WaterproofWallRedisImpl implements WaterproofWallRedis
{
	private final static WaterproofWallRedisImpl REDIS = new WaterproofWallRedisImpl();
	
	private WaterproofWallRedisImpl()
	{}
	
	public static WaterproofWallRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public void updateUserLastPostTime(long userId, long lastPostTime) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_LAST_POST_TIME_KEY_PREFIX, userId);
		RedisWorker<Boolean> worker = new SetWorker(key, String.valueOf(lastPostTime));
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public boolean isSpam(long userId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_LAST_POST_TIME_KEY_PREFIX, userId);
		RedisWorker<String> worker = new GetWorker(key);
		String value = GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
		if(!StringUtil.isLong(value))
			return false;
		
		long interval = (System.currentTimeMillis() - Long.parseLong(value)) / 1000;
		return interval < GlobalConfig.POST_INTERVAL_SECONDS;
	}
}