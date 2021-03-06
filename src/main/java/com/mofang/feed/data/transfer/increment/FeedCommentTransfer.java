package com.mofang.feed.data.transfer.increment;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.feed.data.transfer.ForumChangeUtil;
import com.mofang.framework.util.StringUtil;

public class FeedCommentTransfer extends BaseTransfer implements FeedTransfer
{
	private final static long THREAD_ID_STEP = 50000L;
	private final static long POST_ID_STEP = 300000L;
	private final static long COMMENT_ID_STEP = 50000L;
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_comment(comment_id, forum_id, thread_id, post_id, user_id, content, content_filter, content_mark, status, create_time, update_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		///获取评论数据
		System.out.println("get comment data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_comment data is null.");
			return;
		}
		
		System.out.println("prepare handle comment data......");
		handle(rs);
		System.out.println("comment data transfer completed!");
		
		System.gc();
	}
	
	private ResultSet getData()
	{
		String forumIds = ForumChangeUtil.convertToRetainForumString(ForumChangeUtil.RetainForumSet);
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.pid, case when b.fid is NULL then 0 else b.fid end as fid, a.tid, a.cpid, a.user_id, a.post_time, a.message, a.original_message, a.status ");
		strSql.append("from feed_post a left join feed_thread b on a.tid = b.tid where a.cpid > 0 and a.pid > 6193666 and b.fid in (" + forumIds + ")");
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
					System.out.println("insert comment data total: " + total);
				}
				total++;
			}
			
			batchExec();
			rs.close();
			rs =null;
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
			long commentId = rs.getLong(1) + COMMENT_ID_STEP;
			long forumId = rs.getLong(2);
			long threadId = rs.getLong(3);
			if(threadId > 938054)
				threadId += THREAD_ID_STEP;
			long postId = rs.getLong(4) + POST_ID_STEP;
			long userId = rs.getLong(5);
			long postTime = rs.getTimestamp(6).getTime();
			String message = rs.getString(7) == null ? "" : rs.getString(7);
			String originalMessage = rs.getString(8) == null ? "" : rs.getString(8);
			int status = rs.getInt(9);

			message = StringUtil.safeSql(message);
			originalMessage = StringUtil.safeSql(originalMessage);
			
			if(ForumChangeUtil.ForumToForumMap.containsKey(forumId))
				forumId = ForumChangeUtil.ForumToForumMap.get(forumId);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + commentId + "," + forumId + "," + threadId + "," + postId + "," + userId + ",'" + originalMessage + "',");
			strSql.append("'" + message + "','" + message + "'," + status + "," + postTime + "," + postTime + "),");
			return strSql.toString();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
	}
}
