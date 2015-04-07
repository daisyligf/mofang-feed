package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.mysql.FeedOperateHistoryDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedOperateHistoryDaoImpl extends AbstractMysqlSupport<FeedOperateHistory> implements FeedOperateHistoryDao
{
	private final static FeedOperateHistoryDaoImpl DAO = new FeedOperateHistoryDaoImpl();
	
	private FeedOperateHistoryDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedOperateHistoryDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedOperateHistory model) throws Exception
	{
		super.insert(model);
	}
}