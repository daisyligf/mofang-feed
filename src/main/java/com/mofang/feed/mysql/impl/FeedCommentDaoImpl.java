package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.mysql.FeedCommentDao;
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
public class FeedCommentDaoImpl extends AbstractMysqlSupport<FeedComment> implements FeedCommentDao
{
	private final static FeedCommentDaoImpl DAO = new FeedCommentDaoImpl();
	
	private FeedCommentDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedCommentDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public long getMaxId() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select max(comment_id) from feed_comment ");
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
	public void add(FeedComment model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedComment model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(long commentId) throws Exception
	{
		super.deleteByPrimaryKey(commentId);
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
	public void deleteByPostId(long postId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand postEqual = new EqualOperand("post_id", postId);
		where.append(postEqual);
		super.invokeDeleteByWhere(where);
	}

	@Override
	public FeedComment getInfo(long commentId) throws Exception
	{
		return super.getByPrimaryKey(commentId);
	}

	@Override
	public void updateStatus(long commentId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_comment set status = " + status + " where comment_id=" + commentId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateStatusByThreadId(long threadId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_comment set status = " + status + " where thread_id=" + threadId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public void updateStatusByPostId(long postId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_comment set status = " + status + " where post_id=" + postId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public void updateStatusByForumId(long forumId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_comment set status = " + status + " where forum_id=" + forumId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public void updateForumIdByThreadId(long threadId, long destForumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_comment set forum_id = " + destForumId + " where thread_id=" + threadId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public List<FeedComment> getCommentList(long postId, int status, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand postEqual = new EqualOperand("post_id", postId);
		Operand statusEqual = new EqualOperand("status", status);
		OrderByEntry entry = new OrderByEntry("comment_id", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		Operand and = new AndOperand();
		
		if(postId > 0)
			where.append(postEqual).append(and).append(statusEqual).append(orderby).append(limit);
		else
			where.append(statusEqual).append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getCommentCount(long postId, int status) throws Exception
	{
		Operand where = new WhereOperand();
		Operand postEqual = new EqualOperand("post_id", postId);
		Operand statusEqual = new EqualOperand("status", status);
		Operand and = new AndOperand();
		
		if(postId > 0)
			where.append(postEqual).append(and).append(statusEqual);
		else
			where.append(statusEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getUserCommentList(long userId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select comment_id from feed_comment ");
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
	public long getUserCommentCount(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(statusEqual);
		return super.getCount(where);
	}
}