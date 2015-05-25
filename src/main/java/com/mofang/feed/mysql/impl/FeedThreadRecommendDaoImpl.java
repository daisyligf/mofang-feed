package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedThreadRecommend;
import com.mofang.feed.mysql.FeedThreadRecommendDao;
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
public class FeedThreadRecommendDaoImpl extends AbstractMysqlSupport<FeedThreadRecommend> implements FeedThreadRecommendDao
{
	private final static FeedThreadRecommendDaoImpl DAO = new FeedThreadRecommendDaoImpl();
	
	private FeedThreadRecommendDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedThreadRecommendDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedThreadRecommend model) throws Exception
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
}