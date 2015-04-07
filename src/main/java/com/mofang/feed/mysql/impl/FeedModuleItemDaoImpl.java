package com.mofang.feed.mysql.impl;

import java.util.Date;
import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedModuleItem;
import com.mofang.feed.mysql.FeedModuleItemDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
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
public class FeedModuleItemDaoImpl extends AbstractMysqlSupport<FeedModuleItem> implements FeedModuleItemDao
{
	private final static FeedModuleItemDaoImpl DAO = new FeedModuleItemDaoImpl();
	
	private FeedModuleItemDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedModuleItemDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedModuleItem model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedModuleItem model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(long itemId) throws Exception
	{
		super.deleteByPrimaryKey(itemId);
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
	public FeedModuleItem getInfo(long itemId) throws Exception
	{
		return super.getByPrimaryKey(itemId);
	}

	@Override
	public void updateDisplayOrder(long itemId, int displayOrder) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_module_item set display_order = " + displayOrder + " where item_id=" + itemId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateStauts(long itemId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_module_item set status = " + status + " where item_id=" + itemId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateOnlineTime(long itemId, Date onlineTime) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_module_item set online_time = '" + onlineTime + "' where item_id=" + itemId);
		super.execute(strSql.toString());
	}

	@Override
	public List<FeedModuleItem> getItemList(long moduleId, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand moduleEqual = new EqualOperand("module_id", moduleId);
		OrderByEntry entry = new OrderByEntry("item_id", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		
		if(moduleId > 0)
			where.append(moduleEqual).append(orderby).append(limit);
		else
			where.append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getItemCount(long moduleId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand moduleEqual = new EqualOperand("module_id", moduleId);
		
		if(moduleId > 0)
			where.append(moduleEqual);
		else
			where.append(new EqualOperand("1", "1"));
		return super.getCount(where);
	}
}