package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedBlackList;
import com.mofang.feed.mysql.FeedBlackListDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedBlackListDaoImpl extends AbstractMysqlSupport<FeedBlackList> implements FeedBlackListDao
{
	private final static FeedBlackListDaoImpl DAO = new FeedBlackListDaoImpl();
	
	private FeedBlackListDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedBlackListDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedBlackList model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void delete(long forumId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(userEqual);
		super.deleteByWhere(where);
	}

	@Override
	public void deleteByForumId(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		where.append(forumEqual);
		super.deleteByWhere(where);
	}

	@Override
	public List<FeedBlackList> getUserListByForumId(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		where.append(forumEqual);
		return super.getList(where);
	}
}