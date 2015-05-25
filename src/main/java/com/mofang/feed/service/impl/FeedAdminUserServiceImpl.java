package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedAdminUser;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedAdminUserDao;
import com.mofang.feed.mysql.impl.FeedAdminUserDaoImpl;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.util.MysqlPageNumber;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedAdminUserServiceImpl implements FeedAdminUserService
{
	private final static FeedAdminUserServiceImpl SERVICE = new FeedAdminUserServiceImpl();
	private FeedAdminUserDao adminDao = FeedAdminUserDaoImpl.getInstance();
	
	private FeedAdminUserServiceImpl()
	{}
	
	public static FeedAdminUserServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public boolean exists(long userId) throws Exception
	{
		try
		{
			return adminDao.exists(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedAdminUserServiceImpl.exists throw an error.", e);
			throw e;
		}
	}

	@Override
	public void add(FeedAdminUser model) throws Exception
	{
		try
		{
			adminDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedAdminUserServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long userId) throws Exception
	{
		try
		{
			adminDao.delete(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedAdminUserServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedAdminUser> getAdminList(int pageNum, int pageSize) throws Exception
	{
		try 
		{
			long total = adminDao.getCount();
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int size = pageNumber.getEnd();
			List<FeedAdminUser> list = adminDao.getList(start, size);
			return new Page<FeedAdminUser>(total, list); 
		} 
		catch (Exception e) 
		{
			GlobalObject.ERROR_LOG.error("at FeedAdminUserServiceImpl.getAdminList throw an error.", e);
			throw e;
		}
	}
}