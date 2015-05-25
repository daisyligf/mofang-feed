package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedPostRecommend;
import com.mofang.feed.mysql.FeedPostRecommendDao;
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
public class FeedPostRecommendDaoImpl extends AbstractMysqlSupport<FeedPostRecommend> implements FeedPostRecommendDao
{
	private final static FeedPostRecommendDaoImpl DAO = new FeedPostRecommendDaoImpl();
	
	private FeedPostRecommendDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedPostRecommendDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedPostRecommend model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void delete(long userId, long postId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand postEqual = new EqualOperand("post_id", postId);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(postEqual);
		super.deleteByWhere(where);
	}

	@Override
	public void deleteByPostId(long postId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand postEqual = new EqualOperand("post_id", postId);
		where.append(postEqual);
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
