package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedThreadRecommend;
import com.mofang.feed.mysql.FeedThreadRecommendDao;
import com.mofang.feed.mysql.impl.FeedThreadRecommendDaoImpl;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadRecommendLoad implements FeedLoad
{
	private FeedThreadRecommendDao threadRecommendDao  = FeedThreadRecommendDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();

	public void exec()
	{
		List<FeedThreadRecommend> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("thread recommend data is null or empty.");
			return;
		}
		
		for(FeedThreadRecommend recommendInfo : list)
		{
			handle(recommendInfo);
		}
		list = null;
		System.gc();
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
	
	private List<FeedThreadRecommend> getData()
	{
		try
		{
			return threadRecommendDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadRecommendLoad.getData throw an error.", e);
			return null;
		}
	}
}