package com.mofang.feed.data.load.increment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedCommentRedisImpl;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.framework.data.mysql.core.criterion.operand.LessThanOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;

public class FeedPostConflictLoad implements FeedLoad
{
	private FeedPostDao postDao  = FeedPostDaoImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	private static Map<Long, Integer> POSITION_MAP = new HashMap<Long, Integer>();
	
	public void exec()
	{
		List<FeedPost> list = getData();
		if(null == list || list.size() == 0)
			return;
		
		Map<Long, List<FeedPost>> map = listToMap(list);
		if(null == map)
			return;
		
		handle(map);
		
		list = null;
		System.gc();
		
		///	更新position
		initPosition();
		POSITION_MAP = null;
		System.gc();
	}
	
	private void handle(Map<Long, List<FeedPost>> map)
	{
		List<FeedPost> postList = null;
		try
		{
			for(long threadId : map.keySet())
			{
				FeedThread threadInfo = threadRedis.getInfo(threadId);
				if(null == threadInfo)
					continue;
				
				///删除主题的楼层列表和楼主列表
				postRedis.deleteThreadPostListByThreadId(threadId);
				postRedis.deleteHostPostListByThreadId(threadId);
				
				int position = 2;
				int posts = 0;
				int comments = 0;
				long lastPostTime = System.currentTimeMillis();
				long lastPostUserId = 0L;
				postList = map.get(threadId);
				
				///排序楼层
				Comparator<FeedPost> ascComparator = new PostComparator();
				Collections.sort(postList, ascComparator);
				
				for(FeedPost postInfo : postList)
				{
					///重新生成主题的楼层列表和楼主列表
					if(postInfo.getStatus() == PostStatus.NORMAL)
					{
						postRedis.addThreadPostList(threadId, postInfo.getPostId(), position);
						long hostId = threadInfo.getUserId();
						if(hostId == postInfo.getUserId())
						{
							postRedis.addHostPostList(threadId, postInfo.getPostId(), position);
						}
						
						///计算楼层数
						posts++;
					}
					
					///计算评论数
					comments += commentRedis.getPostCommentCount(postInfo.getPostId());
					
					///更新楼层的position
					postInfo.setPosition(position);
					postRedis.save(postInfo);
					postDao.update(postInfo);
					
					lastPostTime = postInfo.getCreateTime();
					lastPostUserId = postInfo.getUserId();
					position++;
				}
				
				///更新主题的回复数以及最后回复时间和最后回复用户ID
				int replies = posts + comments;
				threadInfo.setReplies(replies);
				threadInfo.setLastPostTime(lastPostTime);
				threadInfo.setLastPostUid(lastPostUserId);
				threadRedis.save(threadInfo);
				threadDao.update(threadInfo);
				
				///更新版块主题列表中该主题的score(只需要更新非置顶帖的score)
				if(!threadInfo.isTop())
					threadRedis.addForumThreadList(threadInfo.getForumId(), threadId, lastPostTime);
				
				///保存主题对应的最大position
				POSITION_MAP.put(threadId, position);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void initPosition()
	{
		Iterator<Long> iterator = POSITION_MAP.keySet().iterator();
		long threadId = 0L;
		int position = 0;
		try
		{
			while(iterator.hasNext())
			{
				threadId = iterator.next();
				position = POSITION_MAP.get(threadId);
				postRedis.initPosition(threadId, position);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.initPosition throw an error.", e);
		}
	}
	
	private Map<Long, List<FeedPost>> listToMap(List<FeedPost> list)
	{
		Map<Long, List<FeedPost>> map = new HashMap<Long, List<FeedPost>>();
		long threadId = 0L;
		for(FeedPost postInfo : list)
		{
			threadId = postInfo.getThreadId();
			List<FeedPost> postList = map.get(threadId);
			if(null == postList)
				postList = new ArrayList<FeedPost>();
			
			postList.add(postInfo);
			map.put(threadId, list);
		}
		return map;
	}
	
	private List<FeedPost> getData()
	{
		try
		{
			Operand where = new WhereOperand();
			Operand postIdLesserThan = new LessThanOperand("thread_id", 938054);
			where.append(postIdLesserThan);
			return postDao.getList(where);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.getData throw an error.", e);
			return null;
		}
	}
	
	class PostComparator implements Comparator<FeedPost>
	{
		@Override
		public int compare(FeedPost o1, FeedPost o2)
		{
			return (int)(o1.getCreateTime() - o2.getCreateTime());
		}
	}
}