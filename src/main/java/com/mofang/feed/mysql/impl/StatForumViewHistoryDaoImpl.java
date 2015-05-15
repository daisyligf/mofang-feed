package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.StatForumViewHistory;
import com.mofang.feed.mysql.StatForumViewHistoryDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

public class StatForumViewHistoryDaoImpl extends
		AbstractMysqlSupport<StatForumViewHistory> implements
		StatForumViewHistoryDao {

	private static final StatForumViewHistoryDaoImpl DAO = new StatForumViewHistoryDaoImpl();

	private StatForumViewHistoryDaoImpl() {
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}

	public static StatForumViewHistoryDaoImpl getInstance() {
		return DAO;
	}

	@Override
	public void add(StatForumViewHistory model) throws Exception {
		super.insert(model);
	}

	@Override
	public long getUV(long startTime, long endTime) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) from stat_forum_view_history where create_time > "
				+ startTime);
		strSql.append(" and create_time < " + endTime);
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return 0;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return 0;
		return rows.get(0).getLong(0);
	}

}
