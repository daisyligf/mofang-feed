package com.mofang.feed.data.transfer.handler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_post(post_id, forum_id, thread_id, user_id, content, content_filter, content_mark, html_content, html_content_filter, html_content_mark, pictures, video_id, thumbnail, duration, position, comments, recommends, status, create_time, update_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取楼层数据
		System.out.println("get post data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_post data is null.");
			return;
		}
		
		System.out.println("prepare handle post data......");
		handle(rs);
		
		///获取fid=0的楼层数据
		System.out.println("get post data......");
		rs = getDataWithNonFid();
		if(null == rs)
		{
			System.out.println("feed_post data is null.");
			return;
		}
		
		System.out.println("prepare handle post data......");
		handle(rs);
		System.out.println("post data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select pid, fid, tid, user_id, post_time, message, original_message, position, comments, pic, status, recommends, video_id, thumbnail, duration, html_message, original_html_message ");
		strSql.append("from feed_post where cpid = 0 and fid > 0 ");
		return getData(strSql.toString());
	}
	
	private ResultSet getDataWithNonFid()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.pid, case when b.fid is NULL then 0 else b.fid end as fid, a.tid, a.user_id, a.post_time, a.message, a.original_message, a.position, a.comments, a.pic, a.status, a.recommends, a.video_id, a.thumbnail, a.duration, a.html_message, a.original_html_message ");
		strSql.append("from feed_post a left join feed_thread b on a.tid = b.tid where a.cpid = 0 and a.fid = 0 ");
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
					System.out.println("insert post data total: " + total);
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
	
	private String buildSql(ResultSet rs)
	{
		try
		{
			long postId = rs.getLong(1);
			long forumId = rs.getLong(2);
			long threadId = rs.getLong(3);
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
	
	private void truncate()
	{
		String strSql = "truncate table feed_post";
		execute(strSql);
	}
}