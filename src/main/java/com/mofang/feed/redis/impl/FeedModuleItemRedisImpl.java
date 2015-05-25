package com.mofang.feed.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.model.FeedModuleItem;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.redis.FeedModuleItemRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.DeleteWorker;
import com.mofang.framework.data.redis.workers.IncrWorker;
import com.mofang.framework.data.redis.workers.SetWorker;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModuleItemRedisImpl implements FeedModuleItemRedis
{
	private final static FeedModuleItemRedisImpl REDIS = new FeedModuleItemRedisImpl();
	
	private FeedModuleItemRedisImpl()
	{}
	
	public static FeedModuleItemRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public long makeUniqueId() throws Exception
	{
		String key = RedisKey.MODULE_ITEM_INCREMENT_ID_KEY;
		RedisWorker<Long> worker = new IncrWorker(key);
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void initUniqueId(long itemId) throws Exception
	{
		String key = RedisKey.MODULE_ITEM_INCREMENT_ID_KEY;
		RedisWorker<Boolean> worker = new SetWorker(key, String.valueOf(itemId));
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void save(final FeedModuleItem model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.MODULE_ITEM_INFO_KEY_PREFIX, model.getItemId());
				jedis.hmset(key, model.toMap());
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(long itemId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.MODULE_ITEM_INFO_KEY_PREFIX, itemId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public FeedModuleItem getInfo(final long itemId) throws Exception
	{
		RedisWorker<FeedModuleItem> worker = new RedisWorker<FeedModuleItem>()
		{
			@Override
			public FeedModuleItem execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.MODULE_ITEM_INFO_KEY_PREFIX, itemId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map || map.size() == 0)
					return null;
				
				return new FeedModuleItem(map);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void updateDisplayOrder(final long itemId, final int displayOrder) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.MODULE_ITEM_INFO_KEY_PREFIX, itemId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "display_order", String.valueOf(displayOrder));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addModuleThreadList(long moduleId, long itemId, long score) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.MODULE_THREAD_LIST_KEY_PREFIX, moduleId);
		RedisFaster.zadd(key, score, itemId);
	}

	@Override
	public void deleteFromModuleThreadList(long moduleId, long itemId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.MODULE_THREAD_LIST_KEY_PREFIX, moduleId);
		RedisFaster.zrem(key, itemId);
	}

	@Override
	public Set<String> getModuleThreadList(long moduleId, int start, int end) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.MODULE_THREAD_LIST_KEY_PREFIX, moduleId);
		return RedisFaster.zrevrange(key, start, end);
	}

	@Override
	public long getModuleThreadCount(long moduleId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.MODULE_THREAD_LIST_KEY_PREFIX, moduleId);
		return RedisFaster.zcard(key);
	}

	@Override
	public void deleteModuleThreadListByModuleId(long moduleId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.MODULE_THREAD_LIST_KEY_PREFIX, moduleId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
	
	@Override
	public List<FeedModuleItem> convertEntityList(final Set<String> idSet) throws Exception
	{
		RedisWorker<List<FeedModuleItem>> worker = new RedisWorker<List<FeedModuleItem>>()
		{
			@Override
			public List<FeedModuleItem> execute(Jedis jedis) throws Exception
			{
				if(null == idSet || idSet.size() == 0)
					return null;
				
				List<FeedModuleItem> list = new ArrayList<FeedModuleItem>();
				FeedModuleItem model = null;
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
	
	private FeedModuleItem convertEntity(Jedis jedis, long itemId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.MODULE_ITEM_INFO_KEY_PREFIX, itemId);
		Map<String, String> map = jedis.hgetAll(key);
		if(null == map || map.size() == 0)
			return null;
		
		FeedModuleItem model = new FeedModuleItem(map);
		if(null != model)
		{
			String threadInfoKey = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, model.getThreadId());
			Map<String, String> threadMap = jedis.hgetAll(threadInfoKey);
			if(null == threadMap || threadMap.size() == 0)
				return null;
			
			FeedThread threadInfo = new FeedThread(threadMap);
			
			String threadPostListKey = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, model.getThreadId());
			Set<String> postIdSet = jedis.zrange(threadPostListKey, 0, 0);
			if(null != postIdSet && postIdSet.size() > 0)
			{
				FeedPost postInfo = null;
				for(String postId : postIdSet)
				{
					String postInfoKey = RedisKey.buildRedisKey(RedisKey.POST_INFO_KEY_PREFIX, postId);
					Map<String, String> postMap = jedis.hgetAll(postInfoKey);
					if(null != postMap && postMap.size() > 0)
					{
						postInfo = new FeedPost(postMap);
						threadInfo.setPost(postInfo);
						break;
					}
				}
			}
			model.setThread(threadInfo);
		}
		return model;
	}
}