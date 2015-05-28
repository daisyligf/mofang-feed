package com.mofang.feed.data.load.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.redis.impl.WaterproofWallRedisImpl;
import com.mofang.feed.solr.FeedThreadSolr;
import com.mofang.feed.solr.impl.FeedThreadSolrImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadLoad implements FeedLoad
{
	private final static int STEP = 50000;
	private FeedThreadDao threadDao  = FeedThreadDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private WaterproofWallRedis waterproofWallRedis = WaterproofWallRedisImpl.getInstance();
	private FeedThreadSolr threadSolr = FeedThreadSolrImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();

	public void exec()
	{
		List<FeedThread> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("thread data is null or empty.");
			return;
		}
		
		Map<Long, String> map = getThreadContentMap();
		
		int total = 1;
		List<FeedThread> solrList = new ArrayList<FeedThread>();
		FeedPost postInfo = null;
		for(FeedThread threadInfo : list)
		{
			if(map.containsKey(threadInfo.getThreadId()))
			{
				postInfo = new FeedPost();
				postInfo.setContentFilter(map.get(threadInfo.getThreadId()));
				threadInfo.setPost(postInfo);
			}
			
			handleRedis(threadInfo);
			///添加到solr列表中
			solrList.add(threadInfo);
			if(total % STEP == 0 || total == list.size())
			{
				handleSolr(solrList);
				solrList.clear();
			}
			total++;
		}
		///更新redis自增ID的值
		initUniqueId();
		
		list = null;
		System.gc();
	}
	
	private void handleRedis(FeedThread threadInfo)
	{
		try
		{
			long forumId = threadInfo.getForumId();
			long threadId = threadInfo.getThreadId();
			long createTime = threadInfo.getCreateTime();
			long userId = threadInfo.getUserId();
			long topTime = threadInfo.getTopTime();
			
			if(threadInfo.getStatus() == ThreadStatus.NORMAL)
			{
				///保存主题信息
				threadRedis.save(threadInfo);
				///保存到版块对应的帖子列表
				threadRedis.addForumThreadList(forumId, threadId, createTime);
				///更新用户最后发帖时间
				waterproofWallRedis.updateUserLastPostTime(userId, createTime);
				///如果是置顶帖
				if(threadInfo.isTop())
				{
					///保存到版块置顶主题列表
					threadRedis.addForumTopThreadList(forumId, threadId, topTime);
				}
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadLoad.handleRedis throw an error.", e);
		}
	}
	
	private void initUniqueId()
	{
		try
		{
			long maxId = threadDao.getMaxId();
			threadRedis.initUniqueId(maxId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadLoad.initUniqueId throw an error.", e);
		}
	}
	
	private void handleSolr(List<FeedThread> solrList)
	{
		try
		{	
			if(solrList.size() == 0)
				return;
		
			final List<FeedThread> list = new ArrayList<FeedThread>();
			list.addAll(solrList);
			threadSolr.batchAdd(list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadLoad.handleSolr throw an error.", e);
		}
	}
	
	private List<FeedThread> getData()
	{
		try
		{
			return threadDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadLoad.getData throw an error.", e);
			return null;
		}
	}
	
	private Map<Long, String> getThreadContentMap()
	{
		try
		{
			return postDao.getThreadContentMap();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadLoad.getThreadContentMap throw an error.", e);
			return new HashMap<Long, String>();
		}
	}
}