package com.mofang.feed.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.DeleteWorker;
import com.mofang.framework.data.redis.workers.IncrWorker;
import com.mofang.framework.data.redis.workers.SetWorker;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostRedisImpl implements FeedPostRedis
{
	private final static FeedPostRedisImpl REDIS = new FeedPostRedisImpl();
	
	private FeedPostRedisImpl()
	{}
	
	public static FeedPostRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public long makeUniqueId() throws Exception
	{
		String key = RedisKey.POST_INCREMENT_ID_KEY;
		RedisWorker<Long> worker = new IncrWorker(key);
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void initUniqueId(long postId) throws Exception
	{
		String key = RedisKey.POST_INCREMENT_ID_KEY;
		RedisWorker<Boolean> worker = new SetWorker(key, String.valueOf(postId));
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void save(final FeedPost model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, model.getPostId());
				jedis.hmset(key, model.toMap());
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, postId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public FeedPost getInfo(final long postId) throws Exception
	{
		RedisWorker<FeedPost> worker = new RedisWorker<FeedPost>()
		{
			@Override
			public FeedPost execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, postId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map || map.size() == 0)
					return null;
				
				return new FeedPost(map);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public int incrPosition(long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_POSITION_KEY_PREFIX, threadId);
		RedisWorker<Long> worker = new IncrWorker(key);
		Long value = GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
		return value.intValue();
	}

	@Override
	public void incrComments(long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, postId);
		RedisFaster.hincrBy(key, "comments", 1);
	}

	@Override
	public void decrComments(long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, postId);
		RedisFaster.hincrBy(key, "comments", -1);
	}

	@Override
	public void incrRecommends(long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, postId);
		RedisFaster.hincrBy(key, "recommends", 1);
	}

	@Override
	public FeedPost getStartPost(long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
		Set<String> set = RedisFaster.zrange(key, 0, 0);
		if(null == set || set.size() == 0)
			return null;
		
		FeedPost model = null;
		for(String postId : set)
		{
			model = getInfo(Long.parseLong(postId));
			if(null != model)
				return model;
		}
		return null;
	}

	@Override
	public int getRank(final long threadId, final long postId) throws Exception
	{
		RedisWorker<Integer> worker = new RedisWorker<Integer>()
		{
			@Override
			public Integer execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
				Long rank = jedis.zrank(key, String.valueOf(postId));
				return null == rank ? 0 : rank.intValue();
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void addThreadPostList(long threadId, long postId, long score) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
		RedisFaster.zadd(key, score, postId);
	}

	@Override
	public void deleteFromThreadPostList(long threadId, long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
		RedisFaster.zrem(key, postId);
	}

	@Override
	public Set<String> getThreadPostList(long threadId, int start, int end) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
		return RedisFaster.zrange(key, start, end);
	}
	
	@Override
	public long getThreadPostCount(long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
		return RedisFaster.zcard(key);
	}
	
	@Override
	public void deleteThreadPostListByThreadId(long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addHostPostList(long threadId, long postId, long score) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.HOST_POST_LIST_KEY_PREFIX, threadId);
		RedisFaster.zadd(key, score, postId);
	}

	@Override
	public void deleteFromHostPostList(long threadId, long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.HOST_POST_LIST_KEY_PREFIX, threadId);
		RedisFaster.zrem(key, postId);
	}

	@Override
	public Set<String> getHostPostList(long threadId, int start, int end) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.HOST_POST_LIST_KEY_PREFIX, threadId);
		return RedisFaster.zrange(key, start, end);
	}
	
	@Override
	public long getHostPostCount(long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.HOST_POST_LIST_KEY_PREFIX, threadId);
		return RedisFaster.zcard(key);
	}
	
	@Override
	public void deleteHostPostListByThreadId(long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.HOST_POST_LIST_KEY_PREFIX, threadId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addUserRecommendPostList(long userId, long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_RECOMMEND_POST_LIST_KEY_PREFIX, userId);
		RedisFaster.sadd(key, postId);
	}

	@Override
	public boolean existsUserRecommendPost(long userId, long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_RECOMMEND_POST_LIST_KEY_PREFIX, userId);
		return RedisFaster.sismember(key, postId);
	}

	@Override
	public Set<String> getUserRecommendPostSet(long userId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_RECOMMEND_POST_LIST_KEY_PREFIX, userId);
		return RedisFaster.smembers(key);
	}

	@Override
	public List<FeedPost> convertEntityList(final Set<String> idSet) throws Exception
	{
		RedisWorker<List<FeedPost>> worker = new RedisWorker<List<FeedPost>>()
		{
			@Override
			public List<FeedPost> execute(Jedis jedis) throws Exception
			{
				if(null == idSet || idSet.size() == 0)
					return null;
				
				List<FeedPost> list = new ArrayList<FeedPost>();
				FeedPost model = null;
				for(String id : idSet)
				{
					model = convertEntity(jedis, Long.parseLong(id));
					if(null == model)
						continue;
					
					list.add(model);
				}
				return list;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public List<FeedPost> convertEntityList(final List<Long> idList) throws Exception
	{
		RedisWorker<List<FeedPost>> worker = new RedisWorker<List<FeedPost>>()
		{
			@Override
			public List<FeedPost> execute(Jedis jedis) throws Exception
			{
				if(null == idList || idList.size() == 0)
					return null;
				
				List<FeedPost> list = new ArrayList<FeedPost>();
				FeedPost model = null;
				for(long id : idList)
				{
					model = convertEntity(jedis, id);
					if(null == model)
						continue;
					
					list.add(model);
				}
				return list;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
	
	private FeedPost convertEntity(Jedis jedis, long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, postId);
		Map<String, String> map = jedis.hgetAll(key);
		if(null == map || map.size() == 0)
			return null;
		
		return new FeedPost(map);
	}
}