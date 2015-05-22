package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedAdminUser;
import com.mofang.feed.mysql.FeedAdminUserDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.LimitOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedAdminUserDaoImpl extends AbstractMysqlSupport<FeedAdminUser> implements FeedAdminUserDao
{
	private final static FeedAdminUserDaoImpl DAO = new FeedAdminUserDaoImpl();
	
	private FeedAdminUserDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedAdminUserDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public boolean exists(long userId) throws Exception
	{
		FeedAdminUser model = super.getByPrimaryKey(userId);
		return null != model;
	}

	@Override
	public void add(FeedAdminUser model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void delete(long userId) throws Exception
	{
		super.deleteByPrimaryKey(userId);
	}

	@Override
	public List<FeedAdminUser> getList(int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		OrderByEntry entry = new OrderByEntry("create_time", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		where.append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getCount() throws Exception
	{
		return super.getCount(null);
	}
}