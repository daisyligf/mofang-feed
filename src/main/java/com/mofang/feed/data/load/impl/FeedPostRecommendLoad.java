package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedPostRecommend;
import com.mofang.feed.mysql.FeedPostRecommendDao;
import com.mofang.feed.mysql.impl.FeedPostRecommendDaoImpl;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostRecommendLoad implements FeedLoad
{
	private FeedPostRecommendDao postRecommendDao  = FeedPostRecommendDaoImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();

	public void exec()
	{
		List<FeedPostRecommend> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("post recommend data is null or empty.");
			return;
		}
		
		for(FeedPostRecommend recommendInfo : list)
		{
			handle(recommendInfo);
		}
	}
	
	private void handle(FeedPostRecommend recommendInfo)
	{
		try
		{
			postRedis.addUserRecommendPostList(recommendInfo.getUserId(), recommendInfo.getPostId());
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostRecommendLoad.handle throw an error.", e);
		}
	}
	
	private List<FeedPostRecommend> getData()
	{
		try
		{
			return postRecommendDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostRecommendLoad.getData throw an error.", e);
			return null;
		}
	}
}