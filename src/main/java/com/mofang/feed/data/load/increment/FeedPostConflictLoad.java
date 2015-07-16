package com.mofang.feed.data.load.increment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.GreaterThanOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.InOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.LessThanOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;
import com.mofang.framework.util.StringUtil;

public class FeedPostConflictLoad implements FeedLoad
{
	private FeedPostDao postDao  = FeedPostDaoImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	
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
	}
	
	private void handle(Map<Long, List<FeedPost>> map)
	{
		List<FeedPost> postList = null;
		try
		{
			int total = map.keySet().size();
			int current = 1;
			for(long threadId : map.keySet())
			{
				FeedThread threadInfo = threadRedis.getInfo(threadId);
				if(null == threadInfo)
					continue;
				
				///删除主题的楼层列表和楼主列表
				postRedis.deleteThreadPostListByThreadId(threadId);
				postRedis.deleteHostPostListByThreadId(threadId);
				
				int position = 0;
				int posts = 0;
				int comments = 0;
				long lastPostTime = System.currentTimeMillis();
				long lastPostUserId = 0L;
				postList = map.get(threadId);
				
				for(FeedPost postInfo : postList)
				{
					position += 1;
					
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
						posts += 1;
					}
					
					///计算评论数
					comments += commentRedis.getPostCommentCount(postInfo.getPostId());
					
					///更新楼层的position
					postInfo.setPosition(position);
					postRedis.save(postInfo);
					postDao.update(postInfo);
					
					lastPostTime = postInfo.getCreateTime();
					lastPostUserId = postInfo.getUserId();
				}
				
				///更新主题的回复数以及最后回复时间和最后回复用户ID
				int replies = posts + comments;
				threadInfo.setReplies(replies);
				threadInfo.setLastPostTime(lastPostTime);
				threadInfo.setLastPostUid(lastPostUserId);
				
				String subjectFilter = threadInfo.getSubjectFilter();
				String subjectMark = threadInfo.getSubjectMark();
				if(StringUtil.isNullOrEmpty(subjectFilter))
					subjectFilter = threadInfo.getSubject();
				if(StringUtil.isNullOrEmpty(subjectMark))
					subjectMark = threadInfo.getSubject();
				
				threadInfo.setSubjectFilter(subjectFilter);
				threadInfo.setSubjectMark(subjectMark);
				threadRedis.save(threadInfo);
				threadDao.update(threadInfo);
				
				///更新版块主题列表中该主题的score(只需要更新非置顶帖的score)
				if(!threadInfo.isTop())
					threadRedis.addForumThreadList(threadInfo.getForumId(), threadId, lastPostTime);
				
				///保存主题对应的最大position
				postRedis.initPosition(threadId, position);
				
				System.out.println(total + " thread need handle,  no. " +  current + " thread be handling, current thread_id: " + threadId);
				current++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
			map.put(threadId, postList);
		}
		return map;
	}
	
	private List<FeedPost> getData()
	{
		try
		{
			Set<Long> set = getConflictThreadId();
			if(null == set)
				return null;
			
			Operand where = new WhereOperand();
			Operand in = new InOperand("thread_id", set);
			where.append(in);
			return postDao.getList(where);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.getData throw an error.", e);
			return null;
		}
	}
	
	private Set<Long> getConflictThreadId()
	{
		try
		{
			Operand where = new WhereOperand();
			Operand threadIdLesserThan = new LessThanOperand("thread_id", 938054);
			Operand postIdGreatThan = new GreaterThanOperand("post_id", (6193654 + 300000));
			Operand and = new AndOperand();
			OrderByEntry entry = new OrderByEntry("create_time", SortType.Asc);
			Operand orderby = new OrderByOperand(entry);
			where.append(threadIdLesserThan).append(and).append(postIdGreatThan).append(orderby);
			List<FeedPost> list = postDao.getList(where);
			if(null == list || list.size() == 0)
				return null;
			
			Set<Long> set = new HashSet<Long>();
			for(FeedPost postInfo : list)
				set.add(postInfo.getThreadId());
			
			return set;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.getConflictThreadId throw an error.", e);
			return null;
		}
	}
}