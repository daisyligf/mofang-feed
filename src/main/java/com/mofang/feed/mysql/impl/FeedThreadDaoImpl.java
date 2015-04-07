package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.InOperand;
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
public class FeedThreadDaoImpl extends AbstractMysqlSupport<FeedThread> implements FeedThreadDao
{
	private final static FeedThreadDaoImpl DAO = new FeedThreadDaoImpl();
	
	private FeedThreadDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedThreadDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedThread model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedThread model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(long threadId) throws Exception
	{
		super.deleteByPrimaryKey(threadId);
	}

	@Override
	public FeedThread getInfo(long threadId) throws Exception 
	{
		return super.getByPrimaryKey(threadId);
	}

	@Override
	public void updateStatus(long threadId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set status = " + status + " where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateStatusByForumId(long forumId, int status) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set status = " + status + " where forum_id=" + forumId);
		super.invokeExecute(strSql.toString());
	}

	@Override
	public void updateLastPost(long threadId, long lastPostUid, long lastPostTime) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set ");
		strSql.append("last_post_uid = " + lastPostUid + ", ");
		strSql.append("last_post_time = " + lastPostTime + " ");	
		strSql.append("where thread_id=" + threadId);	
		super.execute(strSql.toString());
	}

	@Override
	public void updateElite(long threadId, boolean isElite) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set is_elite = " + isElite + " where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateTop(long threadId, boolean isTop, Date topTime) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set is_top = " + isTop + ", top_time='" + topTime + "' where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateVideo(long threadId, boolean isVideo) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set is_video = " + isVideo + " where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateMark(long threadId, boolean isMark) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set is_mark = " + isMark + " where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateClosed(long threadId, boolean isClosed) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set is_closed = " + isClosed + " where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateUpDown(long threadId, int updown, long updownTime) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set updown = " + updown + ", updown_time=" + updownTime + " where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateForumId(long threadId, long destForumId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set forum_id = " + destForumId + " where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void incrReplies(long threadId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set replies = replies + 1 where thread_id=" + threadId);
		super.execute(strSql.toString());
	}
	
	@Override
	public void decrReplies(long threadId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set replies = replies - 1 where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void incrRecommends(long threadId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set recommends = recommends + 1 where thread_id=" + threadId);
		super.execute(strSql.toString());
	}
	
	@Override
	public void decrRecommends(long threadId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set recommends = recommends - 1 where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void incrShareTimes(long threadId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set share_times = share_times + 1 where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public void incrPageView(long threadId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread set page_view = page_view + 1 where thread_id=" + threadId);
		super.execute(strSql.toString());
	}

	@Override
	public List<FeedThread> getThreadList(long forumId, int status, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", status);
		OrderByEntry entry = new OrderByEntry("thread_id", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		Operand and = new AndOperand();
		
		if(forumId > 0)
			where.append(forumEqual).append(and).append(statusEqual).append(orderby).append(limit);
		else
			where.append(statusEqual).append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getThreadCount(long forumId, int status) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", status);
		Operand and = new AndOperand();
		
		if(forumId > 0)
			where.append(forumEqual).append(and).append(statusEqual);
		else
			where.append(statusEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getForumEliteThreadList(long forumId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where forum_id = " + forumId + " ");
		strSql.append("and status = 1 and is_elite = 1 ");
		strSql.append("order by last_post_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getForumEliteThreadCount(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand isEliteEqual = new EqualOperand("is_elite", 1);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(statusEqual).append(and).append(isEliteEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getForumVideoThreadList(long forumId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where forum_id = " + forumId + " ");
		strSql.append("and status = 1 and is_video = 1 ");
		strSql.append("order by last_post_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getForumVideoThreadCount(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand isVideoEqual = new EqualOperand("is_video", 1);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(statusEqual).append(and).append(isVideoEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getForumHotVideoThreadList(long forumId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where forum_id = " + forumId + " ");
		strSql.append("and status = 1 and is_video = 1 ");
		strSql.append("order by page_view desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getForumHotVideoThreadCount(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand isEliteEqual = new EqualOperand("is_video", 1);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(statusEqual).append(and).append(isEliteEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getForumQuestionThreadList(long forumId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where forum_id = " + forumId + " ");
		strSql.append("and status = 1 and type = " + ThreadType.QUESTION + " ");
		strSql.append("order by last_post_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getForumQuestionThreadCount(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand typeEqual = new EqualOperand("type", ThreadType.QUESTION);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(statusEqual).append(and).append(typeEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getForumMarkThreadList(long forumId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where forum_id = " + forumId + " ");
		strSql.append("and status = 1 and is_mark = 1 ");
		strSql.append("order by last_post_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getForumMarkThreadCount(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand isMarkEqual = new EqualOperand("is_mark", 1);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(statusEqual).append(and).append(isMarkEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getUserThreadList(long userId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("and status = 1 ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getUserThreadCount(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(statusEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getUserEliteThreadList(long userId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("and status = 1 and is_elite = 1 ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getUserEliteThreadCount(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand isEliteEqual = new EqualOperand("is_elite", 1);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(statusEqual).append(and).append(isEliteEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getUserQuestionThreadList(long userId, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("and status = 1 and type = " + ThreadType.QUESTION + " ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getUserQuestionThreadCount(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand typeEqual = new EqualOperand("type", ThreadType.QUESTION);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(statusEqual).append(and).append(typeEqual);
		return super.getCount(where);
	}

	@Override
	public List<Long> getForumEliteThreadList(Set<Long> forumIds, int start, int end) throws Exception
	{
		String strForumIds = "";
		for(long strForumId : forumIds)
			strForumIds += strForumId + ",";
		
		if(strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where forum_id in (" + strForumIds + ") ");
		strSql.append("and status = 1 and is_elite = 1 ");
		strSql.append("order by last_post_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getForumEliteThreadCount(Set<Long> forumIds) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumIn = new InOperand("forum_id", forumIds);
		Operand statusEqual = new EqualOperand("status", 1);
		Operand isEliteEqual = new EqualOperand("is_elite", 1);
		Operand and = new AndOperand();
		where.append(forumIn).append(and).append(statusEqual).append(and).append(isEliteEqual);
		return super.getCount(where);
	}
	
	private List<Long> convertResultDataToList(ResultData data) throws Exception
	{
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
}