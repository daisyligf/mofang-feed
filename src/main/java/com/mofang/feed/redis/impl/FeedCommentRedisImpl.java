package com.mofang.feed.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.DeleteWorker;
import com.mofang.framework.data.redis.workers.IncrWorker;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedCommentRedisImpl implements FeedCommentRedis
{
	private final static FeedCommentRedisImpl REDIS = new FeedCommentRedisImpl();
	
	private FeedCommentRedisImpl()
	{}
	
	public static FeedCommentRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public long makeUniqueId() throws Exception
	{
		String key = RedisKey.COMMENT_INCREMENT_ID_KEY;
		RedisWorker<Long> worker = new IncrWorker(key);
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void save(final FeedComment model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.COMMENT_INFO_KEY_PREFIX, model.getCommentId());
				jedis.hmset(key, model.toMap());
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(long commentId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.COMMENT_INFO_KEY_PREFIX, commentId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public FeedComment getInfo(final long commentId) throws Exception
	{
		RedisWorker<FeedComment> worker = new RedisWorker<FeedComment>()
		{
			@Override
			public FeedComment execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.COMMENT_INFO_KEY_PREFIX, commentId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map || map.size() == 0)
					return null;
				
				return new FeedComment(map);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void addPostCommentList(long postId, long commentId, long score) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_COMMENT_LIST_KEY_PREFIX, postId);
		RedisFaster.zadd(key, score, commentId);
	}

	@Override
	public void deleteFromPostCommentList(long postId, long commentId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_COMMENT_LIST_KEY_PREFIX, postId);
		RedisFaster.zrem(key, commentId);
	}

	@Override
	public Set<String> getPostCommentList(long postId, int start, int end) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_COMMENT_LIST_KEY_PREFIX, postId);
		return RedisFaster.zrevrange(key, start, end);
	}

	@Override
	public long getPostCommentCount(long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_COMMENT_LIST_KEY_PREFIX, postId);
		return RedisFaster.zcard(key);
	}

	@Override
	public void deletePostCommentListByPostId(long postId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.POST_COMMENT_LIST_KEY_PREFIX, postId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public List<FeedComment> convertEntityList(final Set<String> idSet) throws Exception
	{
		RedisWorker<List<FeedComment>> worker = new RedisWorker<List<FeedComment>>()
		{
			@Override
			public List<FeedComment> execute(Jedis jedis) throws Exception
			{
				if(null == idSet || idSet.size() == 0)
					return null;
				
				List<FeedComment> list = new ArrayList<FeedComment>();
				FeedComment model = null;
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
	public List<FeedComment> convertEntityList(final List<Long> idList) throws Exception
	{
		RedisWorker<List<FeedComment>> worker = new RedisWorker<List<FeedComment>>()
		{
			@Override
			public List<FeedComment> execute(Jedis jedis) throws Exception
			{
				if(null == idList || idList.size() == 0)
					return null;
				
				List<FeedComment> list = new ArrayList<FeedComment>();
				FeedComment model = null;
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
	
	private FeedComment convertEntity(Jedis jedis, long commentId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.COMMENT_INFO_KEY_PREFIX, commentId);
		Map<String, String> map = jedis.hgetAll(key);
		if(null == map || map.size() == 0)
			return null;
		
		return new FeedComment(map);
	}
}