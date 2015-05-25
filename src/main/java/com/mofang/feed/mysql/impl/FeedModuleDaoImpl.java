package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedModule;
import com.mofang.feed.mysql.FeedModuleDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModuleDaoImpl extends AbstractMysqlSupport<FeedModule> implements FeedModuleDao
{
	private final static FeedModuleDaoImpl DAO = new FeedModuleDaoImpl();
	
	private FeedModuleDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedModuleDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedModule model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedModule model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(long moduleId) throws Exception
	{
		super.deleteByPrimaryKey(moduleId);
	}

	@Override
	public FeedModule getInfo(long moduleId) throws Exception
	{
		return super.getByPrimaryKey(moduleId);
	}

	@Override
	public void incrThreads(long moduleId, int threads) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_module set threads = threads + " + threads + " where module_id=" + moduleId);
		super.execute(strSql.toString());
	}
}