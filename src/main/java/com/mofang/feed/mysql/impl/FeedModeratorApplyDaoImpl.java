package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.mysql.FeedModeratorApplyDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.LimitOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

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
}