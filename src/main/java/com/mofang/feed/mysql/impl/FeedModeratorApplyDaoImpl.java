package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.mysql.FeedModeratorApplyDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
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
public class FeedModeratorApplyDaoImpl extends AbstractMysqlSupport<FeedModeratorApply> implements FeedModeratorApplyDao
{
	private final static FeedModeratorApplyDaoImpl DAO = new FeedModeratorApplyDaoImpl();
	
	private FeedModeratorApplyDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedModeratorApplyDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedModeratorApply model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void updateStatus(int applyId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_moderator_apply set status = " + status + " where apply_id=" + applyId);
		super.execute(strSql.toString());
	}

	@Override
	public FeedModeratorApply getInfo(int applyId) throws Exception
	{
		return super.getByPrimaryKey(applyId);
	}

	@Override
	public List<FeedModeratorApply> getApplyList(int start, int end) throws Exception
	{
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		return super.getList(limit);
	}

	@Override
	public long getApplyCount() throws Exception
	{
		return super.getCount(null);
	}

	@Override
	public FeedModeratorApply getLastApply(long userId, long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand and = new AndOperand();
		OrderByEntry entry = new OrderByEntry("create_time", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(0L, 1L);
		where.append(userEqual).append(and).append(forumEqual).append(orderby).append(limit);
		List<FeedModeratorApply> list = super.getList(where);
		if(null == list || list.size() == 0)
			return null;
		return list.get(0);
	}
}