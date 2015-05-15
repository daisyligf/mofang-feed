package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.StatForumViewHistory;
import com.mofang.feed.model.external.ForumCount;
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
	public List<ForumCount> getUV(Set<Long> forumIds, long startTime, long endTime) throws Exception {
		String strForumIds = "";
		for (long strForumId : forumIds)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);

		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1),forum_id from stat_forum_view_history where forum_id in (" + strForumIds + ")");
		strSql.append(" and create_time > " + startTime);
		strSql.append(" and create_time < " + endTime);
		strSql.append(" group by forum_id");
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		List<ForumCount> list = new ArrayList<ForumCount>(rows.size());
		for (RowData row : rows){
			ForumCount count = new ForumCount();
			count.count  = row.getLong(0);
			count.forumId = row.getLong(1);
			list.add(count);
		}
		return list;
	}

}
