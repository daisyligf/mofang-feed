package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedReply;
import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;
import com.mofang.feed.model.external.FeedActivityUser;
import com.mofang.feed.model.external.ForumCountByTime;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.GreaterThanOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.InOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.LimitOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.NotInOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;
import com.mofang.framework.util.StringUtil;

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
	public List<FeedPost> getPostList(long threadId, int status, SortType sortType, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", status);
		OrderByEntry entry = new OrderByEntry("post_id", sortType);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		Operand and = new AndOperand();
		
		if(threadId > 0)
			where.append(threadEqual).append(and).append(statusEqual).append(orderby).append(limit);
		else
			where.append(statusEqual).append(orderby).append(limit);
		
		String strWhere = where.toString();
		return getListByWhere(strWhere);
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
	public long getPostCount(long threadId, int status, final Set<Long> userIds, final boolean include) throws Exception 
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", status);
		Operand and = new AndOperand();

		Operand userIdsOperand = new Operand() {
			@Override
			protected String toExpression() {
				String strUserIds = "";
				for (long strForumId : userIds)
					strUserIds += strForumId + ",";
				if (strUserIds.length() > 0)
					strUserIds = strUserIds.substring(0, strUserIds.length() - 1);
				
				if(include)
					return String.format("user_id in (%s)", strUserIds);
				else 
					return String.format("user_id not in (%s)", strUserIds);
			}
		};
		where.append(threadEqual).append(and).append(statusEqual).append(and).append(userIdsOperand);
		return super.getCount(where);
	}


	@Override
	public List<FeedPost> getUserPostList(long userId, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand statusEqual = new EqualOperand("status", PostStatus.NORMAL);
		Operand and = new AndOperand();
		OrderByEntry entry = new OrderByEntry("post_id", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		where.append(userEqual).append(and).append(statusEqual).append(orderby).append(limit);
		
		String strWhere = where.toString();
		return getListByWhere(strWhere);
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
		strSql.append("select post_id as source_id, '1' as type, create_time from feed_post where user_id = " + userId + " and status = 1 and position > 1 ");
		strSql.append("and forum_id not in(select forum_id from feed_forum where is_hidden = 1) ");
		strSql.append("union all ");
		strSql.append("select comment_id as source_id, '2' as type, create_time from feed_comment where user_id = " + userId + " and status = 1 ");
		strSql.append("and forum_id not in(select forum_id from feed_forum where is_hidden = 1) ");
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
		strSql.append("select post_id as source_id from feed_post where user_id = " + userId + " and status = 1 and position > 1 ");
		strSql.append("and forum_id not in(select forum_id from feed_forum where is_hidden = 1) ");
		strSql.append("union all ");
		strSql.append("select comment_id as source_id from feed_comment where user_id = " + userId + " and status = 1 ");
		strSql.append("and forum_id not in(select forum_id from feed_forum where is_hidden = 1) ");
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
	public Map<Long, ForumCountByTime> getReplyCount(Set<Long> forumIds, long startTime, long endTime) throws Exception
	{
		String strForumIds = "";
		for (long strForumId : forumIds)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select count(1),forum_id from feed_post where forum_id in (" + strForumIds + ")");
		strSql.append(" and create_time >= " + startTime);
		strSql.append(" and create_time <= " + endTime);
		strSql.append(" group by forum_id");
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		Map<Long,ForumCountByTime> map = new HashMap<Long,ForumCountByTime>(rows.size());
		for (RowData row : rows){
			ForumCountByTime count = new ForumCountByTime();
			count.followCount  = row.getLong(0);
			count.forumId = row.getLong(1);
			map.put(count.forumId, count);
		}
		return map;
	}

	@Override
	public Map<Long, String> getThreadContentMap(String where) throws Exception
	{
		Map<Long, String> map = new HashMap<Long, String>();
		String strSql = "select thread_id, content_filter from feed_post where position = 1 ";
		if(!StringUtil.isNullOrEmpty(where))
			strSql += " and " + where;
		ResultData  data = super.executeQuery(strSql);
		if(null == data)
			return map;
		
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return map;
		
		long threadId = 0L;
		String content = "";
		for (RowData row : rows)
		{
			threadId = row.getLong(0);
			content = row.getString(1);
			map.put(threadId, content);
		}
		return map;
	}

	@Override
	public List<FeedPost> getPostList(long threadId, int status, int start, int end, final Set<Long> userIds, final boolean include , boolean sort) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", status);
		Operand and = new AndOperand();
		
		Operand userInclude = null;
		if(include)
			userInclude = new InOperand("user_id", userIds);
		else
			userInclude = new NotInOperand("user_id", userIds);
		
		OrderByEntry entry = new OrderByEntry("post_id", SortType.Asc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		where.append(threadEqual).append(and).append(statusEqual).append(and).append(userInclude).append(orderby).append(limit);
		
		String strWhere = where.toString();
		return getListByWhere(strWhere);
	}

	@Override
	public List<FeedActivityUser> getUserByCondition(long threadId, FeedActivityThreadRewardCondition condition) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select user_id, position, forum_id from feed_post where thread_id = " + threadId);
		if(condition.startTime > 0 && condition.endTime > 0) {
			strSql.append(" and create_time >= " + condition.startTime + " and create_time <= " + condition.endTime);
		}
		if(condition.havePic) {
			strSql.append(" and (content like '%<img%' or pictures !='')");
		}
		strSql.append(" group by user_id order by position asc");
		
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;		
		
		List<FeedActivityUser> result = new ArrayList<FeedActivityUser>(rows.size());
		for(RowData row : rows) {
			FeedActivityUser user = new FeedActivityUser();
			user.setUserId(row.getLong(0));
			user.setPostion(row.getInteger(1));
			user.setForumId(row.getLong(2));
			result.add(user);
		}
		return result;
	}

	@Override
	public FeedPost getStartPost(long threadId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand positionEqual = new EqualOperand("position", 1);
		Operand and = new AndOperand();
		where.append(threadEqual).append(and).append(positionEqual);
		List<FeedPost> list = super.getList(where);
		if(null == list || list.size() == 0)
			return null;
		
		return list.get(0);
	}

	@Override
	public List<FeedPost> getHostPostList(long threadId, long userId, int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", PostStatus.NORMAL);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand and = new AndOperand();
		OrderByEntry entry = new OrderByEntry("post_id", SortType.Asc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		where.append(threadEqual).append(and).append(statusEqual).append(and).append(userEqual).append(orderby).append(limit);
		String strWhere = where.toString();
		return getListByWhere(strWhere);
	}

	@Override
	public long getHostPostCount(long threadId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", PostStatus.NORMAL);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand and = new AndOperand();
		where.append(threadEqual).append(and).append(statusEqual).append(and).append(userEqual);
		return super.getCount(where);
	}

	@Override
	public List<FeedPost> getPostListFromPostId(long threadId, long postId, int size) throws Exception
	{
		Operand where = new WhereOperand();
		Operand threadEqual = new EqualOperand("thread_id", threadId);
		Operand statusEqual = new EqualOperand("status", PostStatus.NORMAL);
		Operand greatePost = new GreaterThanOperand("post_id", postId);
		Operand and = new AndOperand();
		Operand limit = new LimitOperand(0L, Integer.valueOf(size).longValue());
		where.append(threadEqual).append(and).append(statusEqual).append(and).append(greatePost).append(limit);
		String strWhere = where.toString();
		return getListByWhere(strWhere);
	}

	@Override
	public List<FeedPost> getPostListByPostIds(List<Long> postIds) throws Exception
	{
		Operand where = new WhereOperand();
		Operand postIdIn = new InOperand("post_id", postIds);
		where.append(postIdIn);
		String strWhere = where.toString();
		return getListByWhere(strWhere);
	}
	
	@Override
	public List<FeedPost> getPostListByPostIds(List<Long> postIds, int orderType) throws Exception
	{
		Operand where = new WhereOperand();
		Operand postIdIn = new InOperand("post_id", postIds);
		where.append(postIdIn);
		OrderByEntry entry = null;
		if (orderType == 1) {
			entry = new OrderByEntry("post_id", SortType.Desc);
		} else {
			entry = new OrderByEntry("post_id", SortType.Asc);
		}
		
		Operand orderby = new OrderByOperand(entry);
		where.append(orderby);
		String strWhere = where.toString();
		return getListByWhere(strWhere);
	}
	
	private List<FeedPost> getListByWhere(String where) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select post_id,forum_id,thread_id,user_id,content_filter,html_content_filter,pictures,position,comments,recommends,status,create_time,update_time ");
		strSql.append("from feed_post ");
		if(!StringUtil.isNullOrEmpty(where))
			strSql.append(where);
		
		ResultData rs = super.executeQuery(strSql.toString());
		if(null == rs)
			return null;
		
		List<RowData> rows = rs.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;
		
		List<FeedPost> list = new ArrayList<FeedPost>();
		FeedPost postInfo = null;
		for(RowData row : rows)
		{
			postInfo = new FeedPost();
			postInfo.setPostId(row.getLong(0));
			postInfo.setForumId(row.getLong(1));
			postInfo.setThreadId(row.getLong(2));
			postInfo.setUserId(row.getLong(3));
			postInfo.setContent(row.getString(4));
			postInfo.setContentFilter(row.getString(4));
			postInfo.setHtmlContent(row.getString(5));
			postInfo.setHtmlContentFilter(row.getString(5));
			postInfo.setPictures(row.getString(6));
			postInfo.setPosition(row.getInteger(7));
			postInfo.setComments(row.getInteger(8));
			postInfo.setRecommends(row.getInteger(9));
			postInfo.setStatus(row.getInteger(10));
			postInfo.setCreateTime(row.getLong(11));
			postInfo.setUpdateTime(row.getLong(12));
			list.add(postInfo);
		}
		return list;
	}
}