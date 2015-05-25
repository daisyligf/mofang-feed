package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedModuleItem;
import com.mofang.feed.mysql.FeedModuleItemDao;
import com.mofang.feed.mysql.impl.FeedModuleItemDaoImpl;
import com.mofang.feed.redis.FeedModuleItemRedis;
import com.mofang.feed.redis.impl.FeedModuleItemRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModuleItemLoad implements FeedLoad
{
	private FeedModuleItemDao itemDao  = FeedModuleItemDaoImpl.getInstance();
	private FeedModuleItemRedis itemRedis = FeedModuleItemRedisImpl.getInstance();

	public void exec()
	{
		List<FeedModuleItem> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("module item data is null or empty.");
			return;
		}
		
		for(FeedModuleItem itemInfo : list)
		{
			handle(itemInfo);
		}
		
		///更新redis自增ID的值
		initUniqueId();
				
		list = null;
		System.gc();
	}
	
	private void handle(FeedModuleItem itemInfo)
	{
		try
		{
			///保存模块主题信息
			itemRedis.save(itemInfo);
			///将模块主题ID添加到模块主题列表中
			itemRedis.addModuleThreadList(itemInfo.getModuleId(), itemInfo.getItemId(), itemInfo.getDisplayOrder());
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemLoad.handle throw an error.", e);
		}
	}
	
	private void initUniqueId()
	{
		try
		{
			long maxId = itemDao.getMaxId();
			itemRedis.initUniqueId(maxId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemLoad.initUniqueId throw an error.", e);
		}
	}
	
	private List<FeedModuleItem> getData()
	{
		try
		{
			return itemDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemLoad.getData throw an error.", e);
			return null;
		}
	}
}