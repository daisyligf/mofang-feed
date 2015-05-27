package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.external.FeedForumOrder;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.LimitOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumDaoImpl extends AbstractMysqlSupport<FeedForum> implements FeedForumDao
{
	private final static FeedForumDaoImpl DAO = new FeedForumDaoImpl();
	
	private FeedForumDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedForumDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public long getMaxId() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select max(forum_id) from feed_forum ");
		ResultData result = super.executeQuery(strSql.toString());
		if(null == result)
			return 0L;
		
		List<RowData> rows = result.getQueryResult();
		if(null == rows || rows.size() == 0)
			return 0L;
		
		String value = rows.get(0).getString(0);
		return Long.parseLong(value);
	}
	
	@Override
	public void add(FeedForum model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedForum model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(long forumId) throws Exception
	{
		super.deleteByPrimaryKey(forumId);
	}

	@Override
	public void incrThreads(long forumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_forum set threads = threads + 1 where forum_id=" + forumId);
		super.execute(strSql.toString());
	}

	@Override
	public void decrThreads(long forumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_forum set threads = threads - 1 where forum_id=" + forumId);
		super.execute(strSql.toString());
	}

	@Override
	public void incrFollows(long forumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_forum set follows = follows + 1 where forum_id=" + forumId);
		super.execute(strSql.toString());
	}

	@Override
	public void decrFollows(long forumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_forum set follows = follows - 1 where forum_id=" + forumId);
		super.execute(strSql.toString());
	}

	@Override
	public FeedForum getInfo(long forumId) throws Exception
	{
		return super.getByPrimaryKey(forumId);
	}

	@Override
	public List<FeedForum> getForumList(int type, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand parentIdEqual = new EqualOperand("type", type);
		OrderByEntry entry = new OrderByEntry("forum_id", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		where.append(parentIdEqual).append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getForumCount(int type) throws Exception
	{
		Operand where = new WhereOperand();
		Operand parentIdEqual = new EqualOperand("type", type);
		where.append(parentIdEqual);
		return super.getCount(where);
	}

	@Override
	public List<FeedForumOrder> getForumOrderList(long type) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id,create_time from feed_forum ");
		strSql.append("where type = "+ type);
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		List<FeedForumOrder> list = new ArrayList<FeedForumOrder>(rows.size());
		for (RowData row : rows){
			FeedForumOrder forumOrder = new FeedForumOrder();
			forumOrder.setForumId(row.getLong(0));
			forumOrder.setCreateTime(row.getLong(1));
			list.add(forumOrder);
		}
		return list;
	}

	@Override
	public Map<Long,ForumCount> getPostRecommendCount(int type,
			long startTime, long endTime) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) as recommend_count, b.forum_id from ");
		strSql.append("(select post_id,a.forum_id from feed_post right join ");
		strSql.append("(select forum_id from feed_forum where type = " + type);
		strSql.append(" ) a on a.forum_id = feed_post.forum_id) b left join ");
		strSql.append("(select post_id from feed_post_recommend where ");
		strSql.append("create_time >= " + startTime + " and create_time <= " + endTime +") c ");
		strSql.append("on b.post_id = c.post_id group by b.forum_id");
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		Map<Long,ForumCount> map = new HashMap<Long,ForumCount>(rows.size());
		for (RowData row : rows){
			ForumCount count = new ForumCount();
			count.count  = row.getLong(0);
			count.forumId = row.getLong(1);
			map.put(count.forumId, count);
		}
		return map;
	}


	@Override
	public Map<Long, ForumCount> getThreadRecommendCount(int type,
			long startTime, long endTime) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) as recommend_count, b.forum_id from ");
		strSql.append("(select thread_id,a.forum_id from feed_thread right join ");
		strSql.append("(select forum_id from feed_forum where type = " + type);
		strSql.append(" ) a on a.forum_id = feed_thread.forum_id) b left join ");
		strSql.append("(select thread_id from feed_thread_recommend where ");
		strSql.append("create_time >= " + startTime + " and create_time <= " + endTime +") c ");
		strSql.append("on b.thread_id = c.thread_id group by b.forum_id");
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
	public List<Long> getForumIdList() throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id from feed_forum ");
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		List<Long> list = new ArrayList<Long>(rows.size());
		for(RowData row : rows){
			list.add(row.getLong(0));
		}
		return list;
	}
	
}