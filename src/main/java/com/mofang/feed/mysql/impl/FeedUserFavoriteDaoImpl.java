package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedUserFavorite;
import com.mofang.feed.mysql.FeedUserFavoriteDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedUserFavoriteDaoImpl extends AbstractMysqlSupport<FeedUserFavorite> implements FeedUserFavoriteDao
{
	private final static FeedUserFavoriteDaoImpl DAO = new FeedUserFavoriteDaoImpl();
	
	private FeedUserFavoriteDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedUserFavoriteDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public boolean exists(long userId, long threadId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(threadEqual);
		long count = super.getCount(where);
		return count > 0;
	}

	@Override
	public void add(FeedUserFavorite model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void delete(long userId, long threadId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(threadEqual);
		super.deleteByWhere(where);
	}

	@Override
	public void deleteByThreadId(long threadId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		where.append(threadEqual);
		super.deleteByWhere(where);
	}

	@Override
	public void deleteByUserId(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		where.append(userEqual);
		super.deleteByWhere(where);
	}

	@Override
	public List<FeedUserFavorite> getListByThreadId(long threadId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		where.append(threadEqual);
		return super.getList(where);
	}

	@Override
	public List<Long> getUserFavoriteThreadList(long userId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_user_favorite ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;
		
		List<Long> list = new ArrayList<Long>();
		for(RowData row : rows)
			list.add(row.getLong(0));
		
		return list;
	}

	@Override
	public long getUserFavoriteThreadCount(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		where.append(userEqual);
		return super.getCount(where);
	}
}