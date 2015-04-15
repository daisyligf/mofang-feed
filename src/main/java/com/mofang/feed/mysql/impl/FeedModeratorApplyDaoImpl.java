package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.mysql.FeedModeratorApplyDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
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
	public List<FeedModeratorApply> getApplyList(Operand operand) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getApplyCount(Operand operand) throws Exception
	{
		// TODO Auto-generated method stub
		return 0;
	}
}