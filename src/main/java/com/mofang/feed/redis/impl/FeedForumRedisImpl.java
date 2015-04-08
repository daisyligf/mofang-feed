package com.mofang.feed.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.DeleteWorker;
import com.mofang.framework.data.redis.workers.IncrWorker;
import com.mofang.framework.data.redis.workers.SetWorker;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumRedisImpl implements FeedForumRedis
{
	private final static FeedForumRedisImpl REDIS = new FeedForumRedisImpl();
	
	private FeedForumRedisImpl()
	{}
	
	public static FeedForumRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public long makeUniqueId() throws Exception
	{
		String key = RedisKey.FORUM_INCREMENT_ID_KEY;
		RedisWorker<Long> worker = new IncrWorker(key);
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void initUniqueId(long forumId) throws Exception
	{
		String key = RedisKey.FORUM_INCREMENT_ID_KEY;
		RedisWorker<Boolean> worker = new SetWorker(key, String.valueOf(forumId));
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void save(final FeedForum model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, model.getForumId());
				jedis.hmset(key, model.toMap());
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
	
	@Override
	public void delete(long forumId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, forumId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public FeedForum getInfo(final long forumId) throws Exception
	{
		RedisWorker<FeedForum> worker = new RedisWorker<FeedForum>()
		{
			@Override
			public FeedForum execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, forumId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map || map.size() == 0)
					return null;
				
				return new FeedForum(map);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void incrThreads(final long forumId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, forumId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hincrBy(key, "threads", 1);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void decrThreads(final long forumId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, forumId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hincrBy(key, "threads", -1);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void incrTodayThreads(final long forumId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, forumId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hincrBy(key, "today_threads", 1);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void clearTodayThreads(final long forumId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, forumId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "today_threads", "0");
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addRecommendForumList(final long forumId, final int position) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.RECOMMEND_FORUM_LIST_KEY;
				jedis.zadd(key, position, String.valueOf(forumId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void deleteFromRecommendForumList(final long forumId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.RECOMMEND_FORUM_LIST_KEY;
				jedis.zrem(key, String.valueOf(forumId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public Set<String> getRecommendForumList() throws Exception
	{
		RedisWorker<Set<String>> worker = new RedisWorker<Set<String>>()
		{
			@Override
			public Set<String> execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.RECOMMEND_FORUM_LIST_KEY;
				return jedis.zrange(key, 0, Short.MAX_VALUE);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void clearRecommendForumList() throws Exception
	{
		String key = RedisKey.RECOMMEND_FORUM_LIST_KEY;
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addHotForumList(final long forumId, final int threads) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.HOT_FORUM_LIST_KEY;
				jedis.zadd(key, threads, String.valueOf(forumId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void deleteFromHotForumList(final long forumId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.HOT_FORUM_LIST_KEY;
				jedis.zrem(key, String.valueOf(forumId));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public Set<String> getHotForumList(final int size) throws Exception
	{
		RedisWorker<Set<String>> worker = new RedisWorker<Set<String>>()
		{
			@Override
			public Set<String> execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.HOT_FORUM_LIST_KEY;
				return jedis.zrevrange(key, 0, size - 1);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void clearHotForumList() throws Exception
	{
		String key = RedisKey.HOT_FORUM_LIST_KEY;
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public List<FeedForum> convertEntityList(final Set<String> idSet) throws Exception
	{
		RedisWorker<List<FeedForum>> worker = new RedisWorker<List<FeedForum>>()
		{
			@Override
			public List<FeedForum> execute(Jedis jedis) throws Exception
			{
				if(null == idSet || idSet.size() == 0)
					return null;
				
				List<FeedForum> list = new ArrayList<FeedForum>();
				FeedForum forumInfo = null;
				for(String strForumId : idSet)
				{
					forumInfo = convertEntity(jedis, Long.parseLong(strForumId));
					if(null == forumInfo)
						continue;
					list.add(forumInfo);
				}
				return list;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
	
	private FeedForum convertEntity(Jedis jedis, long forumId) throws Exception
	{
		String forumInfoKey = RedisKey.buildRedisKey(RedisKey.FORUM_INFO_KEY_PREFIX, forumId);
		Map<String, String> forumMap = jedis.hgetAll(forumInfoKey);
		if(null == forumMap || forumMap.size() == 0)
			return null;
		
		return new FeedForum(forumMap);
	}
}