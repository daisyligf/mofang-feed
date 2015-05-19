package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		}
		return list;
	}

	@Override
	public Map<Long,ForumCount> getRecommendCount(Set<Long> forumIds,
			long startTime, long endTime) throws Exception {
		String strForumIds = "";
		for (long strForumId : forumIds)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select sum(a.forum_count),forum_id from (select count(1) as forum_count,fourm_id ");
		strSql.append("from feed_thread_recommend where forum_id in (" + strForumIds +") group by forum_id ");
		strSql.append("union all select count(1) as forum_count,forum_id from feed_post_recommend ");
		strSql.append("where forum_id in (" + strForumIds + ") group by forum_id) a group by a.forum_id");
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
}