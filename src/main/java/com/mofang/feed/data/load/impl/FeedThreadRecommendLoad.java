package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedThreadRecommend;
import com.mofang.feed.mysql.FeedThreadRecommendDao;
import com.mofang.feed.mysql.impl.FeedThreadRecommendDaoImpl;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.framework.data.mysql.core.criterion.operand.LimitOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadRecommendLoad implements FeedLoad
{
	private FeedThreadRecommendDao threadRecommendDao  = FeedThreadRecommendDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private final static int MAX_RECOMMEND_ID = 3000000;
	private final static int STEP = 100000;

	public void exec()
	{
		for(int i=0; i< MAX_RECOMMEND_ID; i = i+STEP)
		{
			List<FeedThreadRecommend> list = getData(i, i + STEP);
			if(null == list || list.size() == 0)
				continue;
			
			for(FeedThreadRecommend recommendInfo : list)
			{
				handle(recommendInfo);
			}
			list = null;
			System.gc();
		}
	}
	
	private void handle(FeedThreadRecommend recommendInfo)
	{
		try
		{
			threadRedis.addUserRecommendThreadList(recommendInfo.getUserId(), recommendInfo.getThreadId());
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadRecommendLoad.handle throw an error.", e);
		}
	}
	
	private List<FeedThreadRecommend> getData(int start, int end)
	{
		try
		{
			System.out.println("start: " + start + ", end: " + end);
			Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
			return threadRecommendDao.getList(limit);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadRecommendLoad.getData throw an error.", e);
			return null;
		}
	}
}