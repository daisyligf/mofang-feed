package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedModule;
import com.mofang.feed.mysql.FeedModuleDao;
import com.mofang.feed.mysql.impl.FeedModuleDaoImpl;
import com.mofang.feed.service.FeedModuleService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModuleServiceImpl implements FeedModuleService
{
	private final static FeedModuleServiceImpl SERVICE = new FeedModuleServiceImpl();
	private FeedModuleDao moduleDao = FeedModuleDaoImpl.getInstance();
	
	private FeedModuleServiceImpl()
	{}
	
	public static FeedModuleServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(FeedModule model) throws Exception
	{
		try
		{
			moduleDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void edit(FeedModule model) throws Exception
	{
		try
		{
			moduleDao.update(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleServiceImpl.edit throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long moduleId) throws Exception
	{
		try
		{
			moduleDao.delete(moduleId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedModule getInfo(long moduleId) throws Exception
	{
		try
		{
			return moduleDao.getInfo(moduleId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedModule> getList() throws Exception
	{
		try
		{
			return moduleDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleServiceImpl.getList throw an error.", e);
			throw e;
		}
	}
}