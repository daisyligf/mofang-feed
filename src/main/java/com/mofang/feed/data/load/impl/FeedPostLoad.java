package com.mofang.feed.data.load.impl;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.redis.impl.WaterproofWallRedisImpl;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.feed.solr.impl.FeedPostSolrImpl;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.GreaterThanOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.LessThanOrEqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostLoad implements FeedLoad
{
	private FeedPostDao postDao  = FeedPostDaoImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private WaterproofWallRedis waterproofWallRedis = WaterproofWallRedisImpl.getInstance();
	private FeedPostSolr postSolr = FeedPostSolrImpl.getInstance();
	private final static int MAX_POST_ID = 6000000;
	private final static int STEP = 50000;
	
	public void exec()
	{
		for(int i=0; i< MAX_POST_ID; i = i+STEP)
		{
			List<FeedPost> list = getData(i, i + STEP);
			if(null == list || list.size() == 0)
			{
				continue;
			}
			
			int total = 1;
			List<FeedPost> solrList = new ArrayList<FeedPost>();
			for(FeedPost postInfo : list)
			{
				handleRedis(postInfo);
				///添加到solr列表中
				solrList.add(postInfo);
				if(total % STEP == 0 || total == list.size())
				{
					//handleSolr(solrList);
					solrList.clear();
				}
				total++;
			}
			///更新redis自增ID的值
			initUniqueId();
			
			list = null;
			System.gc();
		}
	}
	
	private void handleRedis(FeedPost postInfo)
	{
		try
		{
			long threadId = postInfo.getThreadId();
			long postId = postInfo.getPostId();
			int position = postInfo.getPosition();
			long userId = postInfo.getUserId();
			long postTime = postInfo.getCreateTime();
			
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
				///更新用户最后发帖时间
				waterproofWallRedis.updateUserLastPostTime(userId, postTime);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.handleRedis throw an error.", e);
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
	
	private List<FeedPost> getData(int startId, int endId)
	{
		try
		{
			System.out.println("startId: " + startId + ", endId: " + endId);
			Operand where = new WhereOperand();
			Operand postIdGreaterThan = new GreaterThanOperand("post_id", startId);
			Operand postIdLessThan = new LessThanOrEqualOperand("post_id", endId);
			Operand and = new AndOperand();
			where.append(postIdGreaterThan).append(and).append(postIdLessThan);
			return postDao.getList(where);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostLoad.getData throw an error.", e);
			return null;
		}
	}
}