package com.mofang.feed.mysql.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForumFollow;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.feed.mysql.FeedForumFollowDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
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
	public boolean isFollow(long forumId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand followEqual = new EqualOperand("is_follow", 1);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(userEqual).append(and).append(followEqual);
		long count = super.getCount(where);
		return count > 0;
	}

	@Override
	public boolean exists(long forumId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(userEqual);
		long count = super.getCount(where);
		return count > 0;
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

	@Override
	public long getFollowTime(long forumId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand followEqual = new EqualOperand("is_follow", 1);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(userEqual).append(and).append(followEqual);
		List<FeedForumFollow> list = super.getList(where);
		if(null == list || list.size() == 0)
			return 0L;
		
		return list.get(0).getCreateTime();
	}

	@Override
	public Set<Long> getForumIds(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id from feed_forum_follow where is_follow=1 and user_id = " + userId);
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		Set<Long> set = new HashSet<Long>(rows.size());
		for(RowData row : rows){
			set.add(row.getLong(0));
		}
		return set;
	}
}
