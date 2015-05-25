package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedBlackList;
import com.mofang.feed.mysql.FeedBlackListDao;
import com.mofang.feed.mysql.impl.FeedBlackListDaoImpl;
import com.mofang.feed.redis.FeedBlackListRedis;
import com.mofang.feed.redis.impl.FeedBlackListRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedBlackListLoad implements FeedLoad
{
	private FeedBlackListDao blackListDao  = FeedBlackListDaoImpl.getInstance();
	private FeedBlackListRedis blackListRedis = FeedBlackListRedisImpl.getInstance();

	public void exec()
	{
		List<FeedBlackList> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("black list data is null or empty.");
			return;
		}
		
		for(FeedBlackList blackListInfo : list)
		{
			handle(blackListInfo);
		}
	}
	
	private void handle(FeedBlackList blackListInfo)
	{
		try
		{
			blackListRedis.save(blackListInfo);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedBlackListLoad.handle throw an error.", e);
		}
	}
	
	private List<FeedBlackList> getData()
	{
		try
		{
			return blackListDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedBlackListLoad.getData throw an error.", e);
			return null;
		}
	}
}