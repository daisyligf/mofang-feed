package com.mofang.feed.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.common.ThreadUpDown;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.framework.data.redis.RedisWorker;
import com.mofang.framework.data.redis.workers.DeleteWorker;
import com.mofang.framework.data.redis.workers.IncrWorker;
import com.mofang.framework.data.redis.workers.SetWorker;

/**
 * 写这个类的时候，手都抽筋了……
 * @author zhaodx
 *
 */
public class FeedThreadRedisImpl implements FeedThreadRedis
{
	private final static FeedThreadRedisImpl REDIS = new FeedThreadRedisImpl();
	
	private FeedThreadRedisImpl()
	{}
	
	public static FeedThreadRedisImpl getInstance()
	{
		return REDIS;
	}

	@Override
	public long makeUniqueId() throws Exception
	{
		String key = RedisKey.THREAD_INCREMENT_ID_KEY;
		RedisWorker<Long> worker = new IncrWorker(key);
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void initUniqueId(long threadId) throws Exception
	{
		String key = RedisKey.THREAD_INCREMENT_ID_KEY;
		RedisWorker<Boolean> worker = new SetWorker(key, String.valueOf(threadId));
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void save(final FeedThread model) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, model.getThreadId());
				jedis.hmset(key, model.toMap());
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public boolean exists(final long threadId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				return jedis.exists(key);
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void delete(long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public FeedThread getInfo(final long threadId) throws Exception
	{
		RedisWorker<FeedThread> worker = new RedisWorker<FeedThread>()
		{
			@Override
			public FeedThread execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map || map.size() == 0)
					return null;
				
				return new FeedThread(map);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public FeedThread getFullInfo(final long threadId) throws Exception
	{
		RedisWorker<FeedThread> worker = new RedisWorker<FeedThread>()
		{
			@Override
			public FeedThread execute(Jedis jedis) throws Exception
			{
				return convertEntity(jedis, threadId);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public void updateLastPost(final long threadId, final long lastPostUid, final long lastPostTime) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "last_post_uid", String.valueOf(lastPostUid));
				jedis.hset(key, "last_post_time", String.valueOf(lastPostTime));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void updateElite(final long threadId, final boolean isElite) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "is_elite", String.valueOf(isElite));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void updateTop(final long threadId, final boolean isTop, final long topTime) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "is_top", String.valueOf(isTop));
				jedis.hset(key,"top_time", String.valueOf(topTime));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void updateClosed(final long threadId, final boolean isClosed) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "is_closed", String.valueOf(isClosed));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void updateMark(final long threadId, final boolean isMark) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "is_mark", String.valueOf(isMark));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void updateVideo(final long threadId, final boolean isVideo) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				if(!jedis.exists(key))
					return false;
				
				jedis.hset(key, "is_video", String.valueOf(isVideo));
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void updateUpDown(final long threadId, final int updown, final long updownTime) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map)
					return false;
				
				///更新实体信息
				jedis.hset(key, "updown", String.valueOf(updown));
				jedis.hset(key, "updown_time", String.valueOf(updownTime));
				
				///更新版块主题列表的score
				FeedThread threadInfo = new FeedThread(map);
				long lastPostTime = threadInfo.getLastPostTime();
				long score = lastPostTime;
				if(updown == ThreadUpDown.UP)
					score += updownTime;
				else if(updown == ThreadUpDown.DOWN)
					score -= updownTime;
				
				///保存到版块对应的帖子列表
				addForumThreadList(threadInfo.getForumId(), threadId, score);
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void updateForumId(final FeedThread model, final long destForumId) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				long threadId = model.getThreadId();
				String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
				Map<String, String> map = jedis.hgetAll(key);
				if(null == map)
					return false;
				
				///更新实体信息
				jedis.hset(key, "forum_id", String.valueOf(destForumId));
				
				///从原版块主题列表中删除
				long oriForumId = model.getForumId();
				deleteFromForumThreadList(oriForumId, threadId);
				
				///从原版块置顶主题列表中删除
				deleteFromForumTopThreadList(oriForumId, threadId);
				
				///添加到目标版块主题列表中
				addForumThreadList(destForumId, threadId, model.getLastPostTime());
				return true;
			}
		};
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void incrReplies(final long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		RedisFaster.hincrBy(key, "replies", 1);
	}

	@Override
	public void decrReplies(final long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		RedisFaster.hincrBy(key, "replies", -1);
	}

	@Override
	public long incrRecommends(final long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		return RedisFaster.hincrBy(key, "recommends", 1);
	}

	@Override
	public void decrRecommends(final long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		RedisFaster.hincrBy(key, "recommends", -1);
	}

	@Override
	public void incrShareTimes(final long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		RedisFaster.hincrBy(key, "share_times", 1);
	}

	@Override
	public void incrPageView(final long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		RedisFaster.hincrBy(key, "page_view", 1);
	}

	@Override
	public void addForumThreadList(long forumId, long threadId, long score) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_THREAD_LIST_KEY_PREFIX, forumId);
		RedisFaster.zadd(key, score, threadId);
	}

	@Override
	public void deleteFromForumThreadList(long forumId, long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_THREAD_LIST_KEY_PREFIX, forumId);
		RedisFaster.zrem(key, threadId);
	}

	@Override
	public Set<String> getForumThreadList(long forumId, int start, int end) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_THREAD_LIST_KEY_PREFIX, forumId);
		return RedisFaster.zrevrange(key, start, end);
	}
	
	@Override
	public long getForumThreadCount(long forumId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_THREAD_LIST_KEY_PREFIX, forumId);
		return RedisFaster.zcard(key);
	}
	
	@Override
	public void deleteForumThreadListByForumId(long forumId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_THREAD_LIST_KEY_PREFIX, forumId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addForumTopThreadList(long forumId, long threadId, long score) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_TOP_THREAD_LIST_KEY_PREFIX, forumId);
		RedisFaster.zadd(key, score, threadId);
	}

	@Override
	public void deleteFromForumTopThreadList(long forumId, long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_TOP_THREAD_LIST_KEY_PREFIX, forumId);
		RedisFaster.zrem(key, threadId);
	}

	@Override
	public Set<String> getForumTopThreadList(long forumId, int start, int end) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_TOP_THREAD_LIST_KEY_PREFIX, forumId);
		return RedisFaster.zrevrange(key, start, end);
	}

	@Override
	public long getForumTopThreadCount(long forumId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_TOP_THREAD_LIST_KEY_PREFIX, forumId);
		return RedisFaster.zcard(key);
	}

	@Override
	public void deleteForumTopThreadListByForumId(long forumId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.FORUM_TOP_THREAD_LIST_KEY_PREFIX, forumId);
		RedisWorker<Boolean> worker = new DeleteWorker(key);
		GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}

	@Override
	public void addUserRecommendThreadList(long userId, long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_RECOMMEND_THREAD_LIST_KEY_PREFIX, userId);
		RedisFaster.sadd(key, threadId);
	}

	@Override
	public void deleteFromUserRecommendThreadList(long userId, long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_RECOMMEND_THREAD_LIST_KEY_PREFIX, userId);
		RedisFaster.srem(key, threadId);
	}

	@Override
	public boolean existsRecommendThread(long userId, long threadId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_RECOMMEND_THREAD_LIST_KEY_PREFIX, userId);
		return RedisFaster.sismember(key, threadId);
	}

	@Override
	public Set<String> getUserRecommendThreadSet(long userId) throws Exception
	{
		String key = RedisKey.buildRedisKey(RedisKey.USER_RECOMMEND_THREAD_LIST_KEY_PREFIX, userId);
		return RedisFaster.smembers(key);
	}

	@Override
	public List<FeedThread> convertEntityList(final Set<String> idSet) throws Exception
	{
		RedisWorker<List<FeedThread>> worker = new RedisWorker<List<FeedThread>>()
		{
			@Override
			public List<FeedThread> execute(Jedis jedis) throws Exception
			{
				if(null == idSet || idSet.size() == 0)
					return null;
				
				List<FeedThread> list = new ArrayList<FeedThread>();
				FeedThread threadInfo = null;
				for(String threadId : idSet)
				{
					threadInfo = convertEntity(jedis, Long.parseLong(threadId));
					if(null == threadInfo)
						continue;
					list.add(threadInfo);
				}
				return list;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}

	@Override
	public List<FeedThread> convertEntityList(final List<Long> idList) throws Exception
	{
		RedisWorker<List<FeedThread>> worker = new RedisWorker<List<FeedThread>>()
		{
			@Override
			public List<FeedThread> execute(Jedis jedis) throws Exception
			{
				if(null == idList || idList.size() == 0)
					return null;
				
				List<FeedThread> list = new ArrayList<FeedThread>();
				FeedThread threadInfo = null;
				for(long threadId : idList)
				{
					threadInfo = convertEntity(jedis, threadId);
					if(null == threadInfo)
						continue;
					list.add(threadInfo);
				}
				return list;
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
	
	private FeedThread convertEntity(Jedis jedis, long threadId) throws Exception
	{
		String threadInfoKey = RedisKey.buildRedisKey(RedisKey.THREAD_INFO_KEY_PREFIX, threadId);
		Map<String, String> threadMap = jedis.hgetAll(threadInfoKey);
		if(null == threadMap || threadMap.size() == 0)
			return null;
		
		FeedThread threadInfo = new FeedThread(threadMap);
		String threadPostListKey = RedisKey.buildRedisKey(RedisKey.THREAD_POST_LIST_KEY_PREFIX, threadId);
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
		return threadInfo;
	}
}