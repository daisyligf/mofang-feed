package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.mysql.FeedSysUserRoleDao;
import com.mofang.feed.mysql.impl.FeedSysUserRoleDaoImpl;
import com.mofang.feed.redis.FeedSysUserRoleRedis;
import com.mofang.feed.redis.impl.FeedSysUserRoleRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysUserRoleLoad implements FeedLoad
{
	private FeedSysUserRoleDao sysUserRoleDao  = FeedSysUserRoleDaoImpl.getInstance();
	private FeedSysUserRoleRedis sysUserRoleRedis = FeedSysUserRoleRedisImpl.getInstance();

	public void exec()
	{
		List<FeedSysUserRole> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("sys user role data is null or empty.");
			return;
		}
		
		for(FeedSysUserRole userRoleInfo : list)
		{
			handle(userRoleInfo);
		}
	}
	
	private void handle(FeedSysUserRole sysUserInfo)
	{
		try
		{
			sysUserRoleRedis.save(sysUserInfo);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleLoad.handle throw an error.", e);
		}
	}
	
	private List<FeedSysUserRole> getData()
	{
		try
		{
			return sysUserRoleDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleLoad.getData throw an error.", e);
			return null;
		}
	}
}