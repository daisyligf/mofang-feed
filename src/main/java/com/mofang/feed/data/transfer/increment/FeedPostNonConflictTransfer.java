package com.mofang.feed.data.transfer.increment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.feed.data.transfer.ForumChangeUtil;
import com.mofang.framework.util.StringUtil;

/**
 * 无冲突楼层导入
 * @author milo
 *
 */
public class FeedPostNonConflictTransfer extends BaseTransfer implements FeedTransfer
{
	private final static long THREAD_ID_STEP = 50000L;
	private final static long POST_ID_STEP = 300000L;
	private final static int BATCH_EXEC_STEP = 1000;
	
	private final static String SQL_PREFIX = "insert into feed_post(post_id, forum_id, thread_id, user_id, content, content_filter, content_mark, html_content, html_content_filter, html_content_mark, pictures, video_id, thumbnail, duration, position, comments, recommends, status, create_time, update_time) values ";
	private List<String> sqlList = new ArrayList<String>();

	@Override
	public void exec()
	{
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_post data is null.");
			return;
		}
		
		System.out.println("prepare handle post data......");
		handle(rs);
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
					System.out.println("insert post data total: " + total);
				}
				total++;
			}
			
			batchExec();
			rs.close();
			rs = null;
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
	
	private String buildSql(ResultSet rs)
	{
		try
		{
			long postId = rs.getLong(1) + POST_ID_STEP;
			long forumId = rs.getLong(2);
			long threadId = rs.getLong(3) + THREAD_ID_STEP;
			long userId = rs.getLong(4);
			long postTime = rs.getTimestamp(5).getTime();
			String message = rs.getString(6) == null ? "" : rs.getString(6);
			String originalMessage = rs.getString(7) == null ? "" : rs.getString(7);
			int position = rs.getInt(8);
			int comments = rs.getInt(9);
			String pic = rs.getString(10) == null ? "" : rs.getString(10);
			int status = rs.getInt(11);
			int recommends = rs.getInt(12);
			long videoId = rs.getLong(13);
			String thumbnail = rs.getString(14) == null ? "" : rs.getString(14);
			int duration = rs.getInt(15);
			String htmlMessage = rs.getString(16) == null ? "" : rs.getString(16);
			String originalHtmlMessage = rs.getString(17) == null ? "" : rs.getString(17);
			
			message = StringUtil.safeSql(message);
			originalMessage = StringUtil.safeSql(originalMessage);
			pic = StringUtil.safeSql(pic);
			thumbnail = StringUtil.safeSql(thumbnail);
			htmlMessage = StringUtil.safeSql(htmlMessage);
			originalHtmlMessage = StringUtil.safeSql(originalHtmlMessage);
			recommends = recommends < 0 ? 0 : recommends;
			
			if(ForumChangeUtil.ForumToForumMap.containsKey(forumId))
				forumId = ForumChangeUtil.ForumToForumMap.get(forumId);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + postId + "," + forumId + "," + threadId + "," + userId + ",'" + originalMessage + "','" + message + "','" + message + "',");
			strSql.append("'" + originalHtmlMessage + "','" + htmlMessage + "','" + htmlMessage + "','" + pic + "'," + videoId + ",'" + thumbnail + "',");
			strSql.append(duration + "," + position + "," + comments + "," + recommends + "," + status + "," + postTime + "," + postTime + "),");
			return strSql.toString();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取无冲突数据
	 * @return
	 */
	private ResultSet getData()
	{
		ResultSet rs = getHaveUpdateThreadId();
		String threadIds = null;
		if(null != rs)
			threadIds = resultSetToString(rs);
		
		String forumIds = ForumChangeUtil.convertToRetainForumString(ForumChangeUtil.RetainForumSet);
		StringBuilder strSql = new StringBuilder();
		strSql.append("select pid, fid, tid, user_id, post_time, message, original_message, position, comments, pic, status, recommends, video_id, thumbnail, duration, html_message, original_html_message ");
		strSql.append("from feed_post where cpid = 0  ");
		strSql.append("and pid > 6193654 ");
		strSql.append("and tid > 938054 ");
		strSql.append("and fid in(" + forumIds + ") ");
		if(null != threadIds)
			strSql.append("and tid not in (" + threadIds + ")");
		return getData(strSql.toString());
	}
	
	private ResultSet getHaveUpdateThreadId()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id from feed_post ");
		strSql.append("where post_id > 6193654 and thread_id <=938054 and position > 1 group by thread_id");
		return query(strSql.toString());
	}
	
	private String resultSetToString(ResultSet rs)
	{
		StringBuilder result = new StringBuilder();
		try
		{
			while(rs.next())
			{
				result.append(rs.getString(1) + ",");
			}
			if(result.length() > 0)
				return result.substring(0, result.length() - 1);
			
			return null;
		}
		catch (SQLException e) 
		{	
			e.printStackTrace();
			return null;
		}
	}
}