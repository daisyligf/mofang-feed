package com.mofang.feed.mysql.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForumFollow;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.feed.mysql.FeedForumFollowDao;
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
	public Map<Long, ForumCount> getFollowCount(Set<Long> forumIds, long startTime, long endTime) throws Exception {
		String strForumIds = "";
		for (long strForumId : forumIds)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);

		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) from feed_forum_follow where is_follow=1 and forum_id in (" + strForumIds + ")");
		strSql.append(" and create_time > " + startTime);
		strSql.append(" and create_time < " + endTime);
		strSql.append(" group by forum_id");
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		Map<Long, ForumCount> map = new HashMap<Long, ForumCount>(rows.size());
		for (RowData row : rows){
			ForumCount count = new ForumCount();
			count.count  = row.getLong(0);
			count.forumId = row.getLong(1);
			map.put(count.forumId, count);
		}
		return map;
	}

}
