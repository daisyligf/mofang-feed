package com.mofang.feed.data.transfer.handler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.feed.data.transfer.ForumChangeUtil;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_thread(thread_id, forum_id, user_id, subject, subject_filter, subject_mark, link_url, type, status, is_top, is_closed, is_elite, replies, recommends, share_times, page_view, last_post_uid, top_time, last_post_time, tag_id, create_time, update_time) values ";
	private final static int ELITE_TAG_ID = 1;
	private final static int DEFAULT_TAG_ID = 0;
	private List<String> sqlList = new ArrayList<String>();
	private Set<Long> eliteSet = new HashSet<Long>();
	
	public void exec()
	{
		truncate();
		
		///获取主题标签
		System.out.println("get elite thread data......");
		eliteSet = getEliteThreadSet();
		
		///获取主题数据
		System.out.println("get thread data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_forum data is null.");
			return;
		}
		
		System.out.println("prepare handle thread data......");
		handle(rs);
		System.out.println("thread data transfer completed!");
	}
	
	private ResultSet getData()
	{
		String forumIds = ForumChangeUtil.convertToRetainForumString(ForumChangeUtil.RetainForumSet);
		StringBuilder strSql = new StringBuilder();
		strSql.append("select tid, fid, user_id, subject, end_post_time, last_poster_id, is_closed, replies, recommends, share_times, page_view, status, linkurl, type, top_time, create_time ");
		strSql.append("from feed_thread where fid in (" + forumIds + ")");
		return getData(strSql.toString());
	}
	
	private void handle(ResultSet rs)
	{
		try
		{
			String execSql = null;
			int total = 1;
			while(rs.next())
			{
				execSql = buildSql(rs);
				if(StringUtil.isNullOrEmpty(execSql))
					continue;

				sqlList.add(execSql);
				if(total % BATCH_EXEC_STEP == 0)
				{
					batchExec();
					System.out.println("insert thread data total: " + total);
				}
				total++;
			}
			
			batchExec();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void batchExec()
	{
		if(sqlList.size() == 0)
			return;
		
		StringBuilder strSql = new StringBuilder();
		strSql.append(SQL_PREFIX);
		for(String execSql : sqlList)
			strSql.append(execSql);
		
		String sql = strSql.substring(0, strSql.length() - 1);
		execute(sql);
		sqlList.clear();
	}
	
	private Set<Long> getEliteThreadSet()
	{
		Set<Long> set = new HashSet<Long>();
		String strSql = "select tid from feed_tag_thread where tagid = " + ELITE_TAG_ID;
		try
		{
			ResultSet rs = getData(strSql);
			if(null != rs)
			{
				long threadId = 0L;
				while(rs.next())
				{
					threadId = rs.getLong(1);
					set.add(threadId);
				}
			}
		}
		catch(Exception e)
		{}
		return set;
	}
	
	private String buildSql(ResultSet rs)
	{
		try
		{
			long threadId = rs.getLong(1);
			long forumId = rs.getLong(2);
			long userId = rs.getLong(3);
			String subject = rs.getString(4) == null ? "" : rs.getString(4);
			long lastPostTime = rs.getTimestamp(5).getTime();
			long lastPostUid = rs.getLong(6);
			boolean isClosed = rs.getBoolean(7);
			int replies = rs.getInt(8);
			int recommends = rs.getInt(9);
			int shareTimes = rs.getInt(10);
			int pageView = rs.getInt(11);
			int status = rs.getInt(12);
			String linkUrl = rs.getString(13) == null ? "" : rs.getString(13);
			int type = rs.getInt(14) == 0 ? ThreadType.NORMAL : rs.getInt(14);
			
			String strTopTime = rs.getString(15);
			long topTime = 0L;
			if(StringUtil.isDate(strTopTime, "yyyy-MM-dd HH:mm:ss"))
				topTime = rs.getTimestamp(15).getTime();
			
			long createTime = rs.getTimestamp(16).getTime();
			boolean isTop = topTime > 0;
			boolean isElite = eliteSet.contains(threadId);
			
			subject = StringUtil.safeSql(subject);
			linkUrl = StringUtil.safeSql(linkUrl);
			
			int tagId = DEFAULT_TAG_ID;
			if(ForumChangeUtil.ForumToTagMap.containsKey(forumId))
				tagId = ForumChangeUtil.ForumToTagMap.get(forumId);
			
			if(ForumChangeUtil.ForumToForumMap.containsKey(forumId))
				forumId = ForumChangeUtil.ForumToForumMap.get(forumId);
			
			recommends = recommends < 0 ? 0 : recommends;
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + threadId + "," + forumId + "," + userId + ",'" + subject + "','" + subject + "','" + subject + "',");
			strSql.append("'" + linkUrl + "'," + type + "," + status + "," + isTop + ", " + isClosed + "," + isElite + "," + replies + "," + recommends + ",");
			strSql.append(shareTimes + "," + pageView + "," + lastPostUid + "," + topTime + "," + lastPostTime + "," + tagId + "," + createTime + "," + createTime + "),");
			return strSql.toString();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
	}
	
	private void truncate()
	{
		String strSql = "truncate table feed_thread";
		execute(strSql);
	}
}