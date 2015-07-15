package com.mofang.feed.data.load.increment;

import java.util.ArrayList;
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
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.feed.solr.impl.FeedPostSolrImpl;
import com.mofang.framework.data.mysql.core.criterion.operand.GreaterThanOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostLoad implements FeedLoad
{
	private final static int STEP = 50000;
	private FeedPostDao postDao  = FeedPostDaoImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedPostSolr postSolr = FeedPostSolrImpl.getInstance();
	private static Map<Long, Integer> POSITION_MAP = new HashMap<Long, Integer>();
	
	public void exec()
	{
		List<FeedPost> list = getData();
		if(null == list || list.size() == 0)
			return;
		
		int total = 1;
		List<FeedPost> solrList = new ArrayList<FeedPost>();
		for(FeedPost postInfo : list)
		{
			///设置主题的最大楼层数
			int position = postInfo.getPosition();
			if(POSITION_MAP.containsKey(postInfo.getThreadId()))
			{
				if(POSITION_MAP.get(postInfo.getThreadId()) > position)
					position = POSITION_MAP.get(postInfo.getThreadId());
			}
			POSITION_MAP.put(postInfo.getThreadId(), position);
			
			handleRedis(postInfo);
			///添加到solr列表中
			solrList.add(postInfo);
			if(total % STEP == 0 || total == list.size())
			{
				handleSolr(solrList);
				solrList.clear();
			}
			total++;
		}
		list = null;
		System.gc();
		
		
		///更新redis自增ID的值
		initUniqueId();
		
		///	更新position
		initPosition();
		POSITION_MAP = null;
		System.gc();
	}
	
	private void handleRedis(FeedPost postInfo)
	{
		try
		{
			long threadId = postInfo.getThreadId();
			long postId = postInfo.getPostId();
			int position = postInfo.getPosition();
			long userId = postInfo.getUserId();
			
			if(postInfo.getStatus() == PostStatus.NORMAL)
			{
				///保存楼层信息
				postRedis.save(postInfo);
				///将楼层ID添加到主题楼层列表中
				postRedis.addThreadPostList(threadId, postId, position);
				///将楼层ID添加到楼主楼层列表(需要判断是否为楼主)
				FeedThread threadInfo = threadRedis.getInfo(threadId);
				if(null != threadInfo)
				{
					long hostId = threadInfo.getUserId();
					if(hostId == userId)
					{
						postRedis.addHostPostList(threadId, postId, position);
					}
				}
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.handleRedis throw an error.", e);
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
	
	private void initUniqueId()
	{
		try
		{
			long maxId = postDao.getMaxId();
			postRedis.initUniqueId(maxId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.initUniqueId throw an error.", e);
		}
	}
	
	private void handleSolr(List<FeedPost> solrList)
	{
		try
		{
			if(solrList.size() == 0)
				return;
			
			final List<FeedPost> list = new ArrayList<FeedPost>();
			list.addAll(solrList);
			postSolr.batchAdd(list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.handleSolr throw an error.", e);
		}
	}
	
	private List<FeedPost> getData()
	{
		try
		{
			Operand where = new WhereOperand();
			Operand postIdGreaterThan = new GreaterThanOperand("post_id", (6193654 + 300000));
			where.append(postIdGreaterThan);
			return postDao.getList(where);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.getData throw an error.", e);
			return null;
		}
	}
}