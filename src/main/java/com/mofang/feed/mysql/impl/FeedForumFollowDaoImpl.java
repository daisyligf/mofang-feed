package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForumFollow;
import com.mofang.feed.mysql.FeedForumFollowDao;
import com.mofang.feed.util.TimeUtil;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

public class FeedForumFollowDaoImpl extends AbstractMysqlSupport<FeedForumFollow>
		implements FeedForumFollowDao {

	private static final FeedForumFollowDaoImpl DAO = new FeedForumFollowDaoImpl();
	
	private FeedForumFollowDaoImpl(){
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	public static FeedForumFollowDaoImpl getInstance(){
		return DAO;
	}
	
	@Override
	public void add(FeedForumFollow model) throws Exception {
		super.insert(model);
	}

	@Override
	public void edit(FeedForumFollow model) throws Exception {
		super.updateByPrimaryKey(model);
	}

	@Override
	public long getYesterdayFollow() throws Exception {
		long yesterdayStartTime = TimeUtil.getYesterdyStartTime();
		long yesterdayEndTime = TimeUtil.getYesterdyEndTime();
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) from feed_forum_follow where is_follow=1 and create_time > "
				+ yesterdayStartTime);
		strSql.append(" and create_time < " + yesterdayEndTime);
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return 0;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return 0;
		return rows.get(0).getLong(0);
	}

}
