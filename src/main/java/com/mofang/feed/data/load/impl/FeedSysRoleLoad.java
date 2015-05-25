package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.mysql.FeedSysRoleDao;
import com.mofang.feed.mysql.impl.FeedSysRoleDaoImpl;
import com.mofang.feed.redis.FeedSysRoleRedis;
import com.mofang.feed.redis.impl.FeedSysRoleRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysRoleLoad implements FeedLoad
{
	private FeedSysRoleDao sysRoleDao  = FeedSysRoleDaoImpl.getInstance();
	private FeedSysRoleRedis sysRoleRedis = FeedSysRoleRedisImpl.getInstance();

	public void exec()
	{
		List<FeedSysRole> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("sys role data is null or empty.");
			return;
		}
		
		for(FeedSysRole roleInfo : list)
		{
			handle(roleInfo);
		}
		
		///更新redis自增ID的值
		initUniqueId();
	}
	
	private void handle(FeedSysRole roleInfo)
	{
		try
		{
			sysRoleRedis.save(roleInfo);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleLoad.handle throw an error.", e);
		}
	}
	
	private void initUniqueId()
	{
		try
		{
			int maxId = sysRoleDao.getMaxId();
			sysRoleRedis.initUniqueId(maxId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleLoad.initUniqueId throw an error.", e);
		}
	}
	
	private List<FeedSysRole> getData()
	{
		try
		{
			return sysRoleDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleLoad.getData throw an error.", e);
			return null;
		}
	}
}