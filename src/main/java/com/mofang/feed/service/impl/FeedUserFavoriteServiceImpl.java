package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.FeedUserFavorite;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedUserFavoriteDao;
import com.mofang.feed.mysql.impl.FeedUserFavoriteDaoImpl;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.service.FeedUserFavoriteService;
import com.mofang.feed.util.MysqlPageNumber;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedUserFavoriteServiceImpl implements FeedUserFavoriteService
{
	private final static FeedUserFavoriteServiceImpl SERVICE = new FeedUserFavoriteServiceImpl();
	private FeedUserFavoriteDao favoriteDao = FeedUserFavoriteDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	
	private FeedUserFavoriteServiceImpl()
	{}
	
	public static FeedUserFavoriteServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(FeedUserFavorite model) throws Exception
	{
		try
		{
			favoriteDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedUserFavoriteServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long userId, long threadId) throws Exception
	{
		try
		{
			favoriteDao.delete(userId, threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedUserFavoriteServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean exists(long userId, long threadId) throws Exception
	{
		try
		{
			return favoriteDao.exists(userId, threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedUserFavoriteServiceImpl.exists throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getUserFavoriteThreadList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = favoriteDao.getUserFavoriteThreadCount(userId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = favoriteDao.getUserFavoriteThreadList(userId, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getThreadPostList throw an error.", e);
			throw e;
		}
	}
	
	private Page<FeedThread> convertEntityList(long total, List<Long> idList) throws Exception
	{
		if(null == idList || idList.size() == 0)
			return null;
		
		List<FeedThread> list = threadRedis.convertEntityList(idList);
		Page<FeedThread> page = new Page<FeedThread>(total, list);
		return page;
	}
}