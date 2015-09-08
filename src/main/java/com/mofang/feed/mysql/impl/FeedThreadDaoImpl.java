package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.QueryTimeType;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.GreaterThanOrEqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.InOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.LessThanOrEqualOperand;
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
		catch (Exception e) 
		{}
	}

	public static FeedThreadDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public long getMaxId() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select max(thread_id) from feed_thread ");
		ResultData result = super.executeQuery(strSql.toString());
		if (null == result)
			return 0L;

		List<RowData> rows = result.getQueryResult();
		if (null == rows || rows.size() == 0)
			return 0L;

		String value = rows.get(0).getString(0);
		return Long.parseLong(value);
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
		strSql.append("update feed_thread set is_top = " + isTop + ", top_time='" + topTime.getTime() + "' where thread_id=" + threadId);
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
		
		Operand hiddenForum = new Operand() {
			@Override
			protected String toExpression() {
				return "forum_id not in (select forum_id from feed_forum where is_hidden = 1) ";
			}
		};

		if (forumId > 0)
			where.append(forumEqual).append(and).append(hiddenForum).append(and).append(statusEqual).append(orderby).append(limit);
		else
			where.append(statusEqual).append(and).append(hiddenForum).append(orderby).append(limit);
		
		return super.getList(where);
	}

	@Override
	public long getThreadCount(long forumId, int status) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand statusEqual = new EqualOperand("status", status);
		Operand and = new AndOperand();
		Operand hiddenForum = new Operand() {
			@Override
			protected String toExpression() {
				return "forum_id not in (select forum_id from feed_forum where is_hidden = 1)";
			}
		};

		if (forumId > 0)
			where.append(forumEqual).append(and).append(hiddenForum).append(and).append(statusEqual);
		else
			where.append(statusEqual).append(and).append(hiddenForum);
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
	public long getUserThreadCount(long userId, long startTime, long endTime) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand timeGreat = new GreaterThanOrEqualOperand("create_time", startTime);
		Operand timeLess = new LessThanOrEqualOperand("create_time", endTime);
		Operand and = new AndOperand();
		where.append(userEqual).append(and).append(timeGreat).append(and).append(timeLess);
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
	public List<Long> getForumEliteThreadList(Set<Long> forumIds, int start, int end) throws Exception
	{
		String strForumIds = "";
		for (long strForumId : forumIds)
			strForumIds += strForumId + ",";

		if (strForumIds.length() > 0)
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

	/**
	 * 获取指定版块的精华帖总数(用于游戏宝,用户关注的版块精华帖总数)
	 */
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
		if (null == data)
			return null;

		List<RowData> rows = data.getQueryResult();
		if (null == rows || rows.size() == 0)
			return null;

		List<Long> list = new ArrayList<Long>();
		for (RowData row : rows)
			list.add(row.getLong(0));

		return list;
	}

	@Override
	public Map<Long, ForumCount> getThreadCount(Set<Long> forumIds, long startTime, long endTime) throws Exception
	{
		String strForumIds = "";
		for (long strForumId : forumIds)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1), forum_id from feed_thread where forum_id in (" + strForumIds + ")");
		strSql.append(" and create_time >= " + startTime);
		strSql.append(" and create_time <= " + endTime);
		strSql.append(" group by forum_id");
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		Map<Long, ForumCount> map = new HashMap<Long,ForumCount>(rows.size());
		for (RowData row : rows){
			ForumCount count = new ForumCount();
			count.count  = row.getLong(0);
			count.forumId = row.getLong(1);
			map.put(count.forumId, count);
		}
		return map;
	}

	@Override
	public long getUserTopOrEliteThreadCount(long userId) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) from feed_thread ");
		strSql.append("where user_id =" + userId + " and (is_top =1 or is_elite=1) ");
		strSql.append("and status = " + ThreadStatus.NORMAL);
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data || null == data.getQueryResult() || data.getQueryResult().size() == 0)
			return 0L;
		
		List<RowData> rows = data.getQueryResult();
		return rows.get(0).getLong(0);
	}

	@Override
	public long getGlobalEliteThreadCount() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) from feed_thread ");	
		strSql.append("where is_elite=1");
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data || null == data.getQueryResult() || data.getQueryResult().size() == 0)
			return 0L;
		
		List<RowData> rows = data.getQueryResult();
		return rows.get(0).getLong(0);
	}

	@Override
	public List<Long> getGlobalEliteThreadList(int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");	
		strSql.append("where is_elite=1");
		//全局调用精华帖修改成调用活动、灌水、推游版块的精华帖。
		strSql.append(" and forum_id in (10, 11, 12)");
		strSql.append(" order by create_time desc");
		strSql.append(" limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public List<Long> getThreadIdList(long forumId, long startTime, long endTime, int limitEnd) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append(" where status = 1 and forum_id = " + forumId);
		
		if(startTime != 0 && endTime != 0) {
			strSql.append(" and create_time >= " + startTime);
			strSql.append(" and create_time <= " + endTime);
		}else if(startTime != 0 && limitEnd < 7) {
			strSql.append(" and create_time < " + startTime);
		}
		
		strSql.append(" order by replies desc limit 0," + limitEnd);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	/**
	 * 根据不同条件获取版块下的主题列表(用于web端)
	 * 注意: 需要排除掉置顶帖
	 * @param forumId 版块ID
	 * @param tagId 标签ID(等于0时为全部主题)
	 * @param isElite 是否过滤精华帖
	 * @param timeType 排序时间类型
	 * @param start 起始记录数
	 * @param end 截止记录数
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Long> getForumThreadListByCondition(long forumId, int tagId, boolean isElite, int timeType, int start, int end) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_thread ");
		strSql.append("where forum_id = " + forumId + " ");
		strSql.append("and status = " + ThreadStatus.NORMAL + " ");      ///非删除帖
		strSql.append("and is_top = 0 ");												///非置顶帖
		if(tagId > 0)
			strSql.append("and tag_id = " + tagId + " ");							///特定标签ID
		if(isElite)
			strSql.append("and is_elite = 1 ");											///精华帖
		
		if(timeType == QueryTimeType.LAST_POST_TIME)
			strSql.append("order by last_post_time desc ");
		else if(timeType == QueryTimeType.CREATE_TIME)
			strSql.append("order by create_time desc ");
		
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		return convertResultDataToList(data);
	}

	@Override
	public long getForumThreadCountByCondition(long forumId, int tagId, boolean isElite) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1) from feed_thread ");	
		strSql.append("where forum_id = " + forumId + " ");
		strSql.append("and status = " + ThreadStatus.NORMAL + " ");      ///非删除帖
		strSql.append("and is_top = 0 ");												///非置顶帖
		if(tagId > 0)
			strSql.append("and tag_id = " + tagId + " ");							///特定标签ID
		if(isElite)
			strSql.append("and is_elite = 1 ");		
		
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data || null == data.getQueryResult() || data.getQueryResult().size() == 0)
			return 0L;
		
		List<RowData> rows = data.getQueryResult();
		return rows.get(0).getLong(0);
	}

	@Override
	public Map<Long, Integer> getForumYestodayThreadsMap(long startTime, long endTime) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id, count(1) as total from feed_thread ");
		strSql.append("where create_time >= " + startTime + " and create_time <= " + endTime + " ");
		strSql.append("group by forum_id");
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data || null == data.getQueryResult() || data.getQueryResult().size() == 0)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		for(RowData row : rows)
			map.put(row.getLong(0), row.getInteger(1));
		return map;
	}
}