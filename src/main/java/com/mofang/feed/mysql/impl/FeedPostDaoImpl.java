package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedReply;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
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
public class FeedPostDaoImpl extends AbstractMysqlSupport<FeedPost> implements FeedPostDao
{
	private final static FeedPostDaoImpl DAO = new FeedPostDaoImpl();
	
	private FeedPostDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedPostDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public long getMaxId() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select max(post_id) from feed_post ");
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
	public void add(FeedPost model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedPost model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(long postId) throws Exception
	{
		super.deleteByPrimaryKey(postId);
	}
	
	@Override
	public void deleteByThreadId(long threadId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		where.append(threadEqual);
		super.invokeDeleteByWhere(where);
	}

	@Override
	public FeedPost getInfo(long postId) throws Exception
	{
		return super.getByPrimaryKey(postId);
	}

	@Override
	public void updateStatus(long postId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set status = " + status + " where post_id=" + postId);
		super.execute(strSql.toString());
	}
	
	@Override
	public void updateStatusByThreadId(long threadId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set status = " + status + " where thread_id=" + threadId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public void updateStatusByForumId(long forumId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set status = " + status + " where forum_id=" + forumId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public void updateForumIdByThreadId(long threadId, long destForumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set forum_id = " + destForumId + " where thread_id=" + threadId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public void incrComments(long postId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set comments = comments + 1 where post_id=" + postId);
		super.execute(strSql.toString());
	}

	@Override
	public void decrComments(long postId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set comments = comments - 1 where post_id=" + postId);
		super.execute(strSql.toString());
	}

	@Override
	public void incrRecommends(long postId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set recommends = recommends + 1 where post_id=" + postId);
		super.execute(strSql.toString());
	}

	@Override
	public void decrRecommends(long postId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post set recommends = recommends - 1 where post_id=" + postId);
		super.execute(strSql.toString());
	}

	@Override
	public List<FeedPost> getPostList(long threadId, int status, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", status);
		OrderByEntry entry = new OrderByEntry("post_id", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		Operand and = new AndOperand();
		
		if(threadId > 0)
			where.append(threadEqual).append(and).append(statusEqual).append(orderby).append(limit);
		else
			where.append(statusEqual).append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getPostCount(long threadId, int status) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", status);
		Operand and = new AndOperand();
		
		if(threadId > 0)
			where.append(threadEqual).append(and).append(statusEqual);
		else
			where.append(statusEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getUserPostList(long userId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select post_id from feed_post ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("and status = 1 ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;
		
		List<Long> list = new ArrayList<Long>();
		for(RowData row : rows)
			list.add(row.getLong(0));
		
		return list;
	}

	@Override
	public long getUserPostCount(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(statusEqual);
		return super.getCount(where);
	}

	@Override
	public List<FeedReply> getUserReplyList(long userId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select source_id, type from( ");
		strSql.append("select post_id as source_id, 'post' as type from feed_post where user_id = " + userId + " and status = 1 ");
		strSql.append("union all ");
		strSql.append("select comment_id as source_id, 'comment' as type from feed_comment where user_id = " + userId + " and status = 1 ");
		strSql.append(") a order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;
		
		List<FeedReply> list = new ArrayList<FeedReply>();
		FeedReply replyInfo = null;
		for(RowData row : rows)
		{
			replyInfo = new FeedReply();
			replyInfo.setSourceId(row.getLong(0));
			replyInfo.setType(row.getInteger(1));
			list.add(replyInfo);
		}
		return list;
	}

	@Override
	public long getUserReplyCount(long userId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) from( ");
		strSql.append("select post_id as source_id, 'post' as type from feed_post where user_id = " + userId + " and status = 1 ");
		strSql.append("union all ");
		strSql.append("select comment_id as source_id, 'comment' as type from feed_comment where user_id = " + userId + " and status = 1 ");
		strSql.append(") a ");
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return 0L;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return 0L;
		
		return rows.get(0).getLong(0);
	}

	@Override
	public List<ForumCount> getReplyCount(Set<Long> forumIds, long startTime, long endTime) throws Exception{
		String strForumIds = "";
		for (long strForumId : forumIds)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1),forum_id from feed_post where forum_id in (" + strForumIds + ")");
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