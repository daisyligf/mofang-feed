package com.mofang.feed.service.impl;

import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ModuleItemStatus;
import com.mofang.feed.model.FeedModuleItem;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedModuleItemDao;
import com.mofang.feed.mysql.impl.FeedModuleItemDaoImpl;
import com.mofang.feed.redis.FeedModuleItemRedis;
import com.mofang.feed.redis.impl.FeedModuleItemRedisImpl;
import com.mofang.feed.service.FeedModuleItemService;
import com.mofang.feed.util.MysqlPageNumber;
import com.mofang.feed.util.RedisPageNumber;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModuleItemServiceImpl implements FeedModuleItemService
{
	private final static FeedModuleItemServiceImpl SERVICE = new FeedModuleItemServiceImpl();
	private FeedModuleItemRedis itemRedis = FeedModuleItemRedisImpl.getInstance();
	private FeedModuleItemDao itemDao = FeedModuleItemDaoImpl.getInstance();
	
	private FeedModuleItemServiceImpl()
	{}
	
	public static FeedModuleItemServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(FeedModuleItem model) throws Exception
	{
		try
		{
			long itemId = itemRedis.makeUniqueId();
			model.setItemId(itemId);
			model.setStatus(ModuleItemStatus.UNCONFIG);
			long moduleId = model.getModuleId();
			int displayOrder = model.getDisplayOrder();
			/******************************redis操作******************************/
			///保存模块主题信息
			itemRedis.save(model);
			///将模块主题ID添加到模块主题列表中
			itemRedis.addModuleThreadList(moduleId, itemId, displayOrder);
			
			/******************************数据库操作******************************/
			///保存模块主题信息
			itemDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemServicecImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void edit(FeedModuleItem model) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///保存模块主题信息
			itemRedis.save(model);
			
			/******************************数据库操作******************************/
			///保存模块主题信息
			itemDao.update(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemServicecImpl.edit throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(FeedModuleItem model) throws Exception
	{
		try
		{
			long itemId = model.getItemId();
			long moduleId = model.getModuleId();
			/******************************redis操作******************************/
			///删除模块主题信息
			itemRedis.delete(itemId);
			///将模块主题ID从模块主题列表中删除
			itemRedis.deleteFromModuleThreadList(moduleId, itemId);
			
			/******************************数据库操作******************************/
			///删除模块主题信息
			itemDao.delete(itemId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemServicecImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedModuleItem getInfo(long itemId) throws Exception
	{
		try
		{
			return itemRedis.getInfo(itemId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemServicecImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public void updateDisplayOrder(FeedModuleItem model) throws Exception
	{
		try
		{
			long itemId = model.getItemId();
			long moduleId = model.getModuleId();
			int displayOrder = model.getDisplayOrder();
			/******************************redis操作******************************/
			///更新模块主题的display字段
			itemRedis.updateDisplayOrder(itemId, displayOrder);
			///更新模块主题ID在模块主题列表中的分值
			itemRedis.addModuleThreadList(moduleId, itemId, displayOrder);
			
			/******************************数据库操作******************************/
			///更新模块主题的display字段
			itemDao.updateDisplayOrder(itemId, displayOrder);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItemServicecImpl.updateDisplayOrder throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedModuleItem> getItemList(long moduleId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = itemDao.getItemCount(moduleId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedModuleItem> list = itemDao.getItemList(moduleId, start, end);
			return new Page<FeedModuleItem>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getItemList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedModuleItem> getModuleThreadList(long moduleId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = itemRedis.getModuleThreadCount(moduleId);
			RedisPageNumber pageNumber = new RedisPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			Set<String> idSet = itemRedis.getModuleThreadList(moduleId, start, end);
			return convertEntityList(total, idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getModuleThreadList throw an error.", e);
			throw e;
		}
	}
	
	private Page<FeedModuleItem> convertEntityList(long total, Set<String> idSet) throws Exception
	{
		if(null == idSet || idSet.size() == 0)
			return null;
		
		List<FeedModuleItem> list = itemRedis.convertEntityList(idSet);
		Page<FeedModuleItem> page = new Page<FeedModuleItem>(total, list);
		return page;
	}
}