package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedAdminUser;
import com.mofang.feed.mysql.FeedAdminUserDao;
import com.mofang.feed.mysql.impl.FeedAdminUserDaoImpl;
import com.mofang.feed.redis.FeedAdminUserRedis;
import com.mofang.feed.redis.impl.FeedAdminUserRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedAdminUserLoad implements FeedLoad
{
	private FeedAdminUserDao adminDao  = FeedAdminUserDaoImpl.getInstance();
	private FeedAdminUserRedis adminRedis = FeedAdminUserRedisImpl.getInstance();

	public void exec()
	{
		List<FeedAdminUser> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("admin user data is null or empty.");
			return;
		}
		
		for(FeedAdminUser adminInfo : list)
		{
			handle(adminInfo);
		}
	}
	
	private void handle(FeedAdminUser adminInfo)
	{
		try
		{
			adminRedis.add(adminInfo.getUserId());
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedAdminUserLoad.handle throw an error.", e);
		}
	}
	
	private List<FeedAdminUser> getData()
	{
		try
		{
			return adminDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedAdminUserLoad.getData throw an error.", e);
			return null;
		}
	}
}