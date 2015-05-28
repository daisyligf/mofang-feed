package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedTag;
import com.mofang.feed.mysql.FeedTagDao;
import com.mofang.feed.mysql.impl.FeedTagDaoImpl;
import com.mofang.feed.redis.FeedTagRedis;
import com.mofang.feed.redis.impl.FeedTagRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedTagLoad implements FeedLoad
{
	private FeedTagDao tagDao = FeedTagDaoImpl.getInstance();
	private FeedTagRedis tagRedis = FeedTagRedisImpl.getInstance();
	
	public void exec()
	{
		List<FeedTag> list = getData();
		if(null == list || list.size() == 0)
			return;
		
		for(FeedTag tagInfo : list)
		{
			handleRedis(tagInfo);
		}
		
		///更新redis自增ID的值
		initUniqueId();
		
		list = null;
		System.gc();
	}
	
	private void handleRedis(FeedTag tagInfo)
	{
		try
		{
			tagRedis.set(tagInfo.getTagId(), tagInfo.getTagName());
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedTagLoad.handleRedis throw an error.", e);
		}
	}
	
	private void initUniqueId()
	{
		try
		{
			int maxId = tagDao.getMaxId();
			tagRedis.initUniqueId(maxId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedTagLoad.initUniqueId throw an error.", e);
		}
	}
	
	private List<FeedTag> getData()
	{
		try
		{
			return tagDao.getList();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedTagLoad.getData throw an error.", e);
			return null;
		}
	}
}