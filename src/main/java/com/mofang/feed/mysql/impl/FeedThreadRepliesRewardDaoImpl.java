package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedThreadRepliesReward;
import com.mofang.feed.mysql.FeedThreadRepliesRewardDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;

public class FeedThreadRepliesRewardDaoImpl extends AbstractMysqlSupport<FeedThreadRepliesReward>
		implements FeedThreadRepliesRewardDao {

	private static final FeedThreadRepliesRewardDaoImpl DAO = new FeedThreadRepliesRewardDaoImpl();
	
	private FeedThreadRepliesRewardDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch (Exception e) 
		{}
	}
	
	public static FeedThreadRepliesRewardDaoImpl getInstance() {
		return DAO;
	}
	
	@Override
	public FeedThreadRepliesReward getModel(long threadId) throws Exception {
		return super.getByPrimaryKey(threadId);
	}

	@Override
	public void update(long threadId, int level, int exp) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread_replies_reward set level = " + level + ", exp =" + exp + " where thread_id = " + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void add(FeedThreadRepliesReward model) throws Exception {
		super.insert(model);
	}

}
