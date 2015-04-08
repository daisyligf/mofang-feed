package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.LimitOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumDaoImpl extends AbstractMysqlSupport<FeedForum> implements FeedForumDao
{
	private final static FeedForumDaoImpl DAO = new FeedForumDaoImpl();
	
	private FeedForumDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedForumDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public long getMaxId() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select max(forum_id) from feed_forum ");
		ResultData result = super.executeQuery(strSql.toString());
		if(null == result)
			return 0L;
		
		List<RowData> rows = result.getQueryResult();
		if(null == rows || rows.size() == 0)
			return 0L;
		
		String value = rows.get(0).getString(0);
		return Long.parseLong(value);
	}
	
	@Override
	public void add(FeedForum model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedForum model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(long forumId) throws Exception
	{
		super.deleteByPrimaryKey(forumId);
	}

	@Override
	public void incrThreads(long forumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_forum set threads = threads + 1 where forum_id=" + forumId);
		super.execute(strSql.toString());
	}

	@Override
	public void decrThreads(long forumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_forum set threads = threads - 1 where forum_id=" + forumId);
		super.execute(strSql.toString());
	}

	@Override
	public FeedForum getInfo(long forumId) throws Exception
	{
		return super.getByPrimaryKey(forumId);
	}

	@Override
	public List<FeedForum> getForumList(long parentId, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand parentIdEqual = new EqualOperand("parent_id", parentId);
		OrderByEntry entry = new OrderByEntry("forum_id", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		
		if(parentId > 0)
			where.append(parentIdEqual).append(orderby).append(limit);
		else
			where.append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getForumCount(long parentId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand parentIdEqual = new EqualOperand("parent_id", parentId);
		
		if(parentId > 0)
			where.append(parentIdEqual);
		else
			where.append(new EqualOperand("1", "1"));
		
		return super.getCount(where);
	}
}