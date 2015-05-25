package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedRoleChangeHistory;
import com.mofang.feed.mysql.FeedRoleChangeHistoryDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;

/**
 * 
 * @author milo
 *
 */
public class FeedRoleChangeHistoryDaoImpl extends AbstractMysqlSupport<FeedRoleChangeHistory> implements FeedRoleChangeHistoryDao
{
	private final static FeedRoleChangeHistoryDaoImpl DAO = new FeedRoleChangeHistoryDaoImpl();
	
	private FeedRoleChangeHistoryDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedRoleChangeHistoryDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedRoleChangeHistory model) throws Exception
	{
		super.insert(model);
	}
}