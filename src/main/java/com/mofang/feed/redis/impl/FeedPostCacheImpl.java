package com.mofang.feed.redis.impl;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.redis.FeedPostCache;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.GetWorker;

/**
 * 
 * @author milo
 *
 */
public class FeedPostCacheImpl implements FeedPostCache
{
	private final static int EXPIRE_SECONDS = 600;
	private final static FeedPostCacheImpl CACHE = new FeedPostCacheImpl();
	
	private FeedPostCacheImpl()
	{}
	
	public static FeedPostCacheImpl getInstance()
	{
		return CACHE;
	}

	@Override
	public String getThreadPostList(long threadId, int pageNum, int pageSize) throws Exception
	{
		/*
		String key = "cache_thread_post_list_" + threadId + "_" + pageNum + "_" + pageSize;
		RedisWorker<String> worker = new GetWorker(key);
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
		*/
		return null;
	}

	@Override
	public void setThreadPostList(long threadId, int pageNum, int pageSize, final String cache) throws Exception
	{
		/*
		final String key = "cache_thread_post_list_" + threadId + "_" + pageNum + "_" + pageSize;
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				jedis.set(key, cache);
				jedis.expire(key, EXPIRE_SECONDS);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
		*/
	}

	@Override
	public String getHostPostList(long threadId, long userId, int pageNum, int pageSize) throws Exception
	{
		String key = "cache_host_post_list_" + threadId + "_" + userId + "_" + pageNum + "_" + pageSize;
		RedisWorker<String> worker = new GetWorker(key);
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void setHostPostList(long threadId, long userId, int pageNum, int pageSize, final String cache) throws Exception
	{
		final String key = "cache_host_post_list_" + threadId + "_" + userId + "_" + pageNum + "_" + pageSize;
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				jedis.set(key, cache);
				jedis.expire(key, EXPIRE_SECONDS);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public String getPostListFromPostId(long threadId, long postId, int pageSize) throws Exception
	{
		String key = "cache_post_list_from_postid_" + threadId + "_" + postId + "_" + pageSize;
		RedisWorker<String> worker = new GetWorker(key);
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void setPostListFromPostId(long threadId, long postId, int pageSize, final String cache) throws Exception
	{
		final String key = "cache_post_list_from_postid_" + threadId + "_" + postId + "_" + pageSize;
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				jedis.set(key, cache);
				jedis.expire(key, EXPIRE_SECONDS);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
}