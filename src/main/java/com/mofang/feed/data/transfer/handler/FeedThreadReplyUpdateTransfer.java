package com.mofang.feed.data.transfer.handler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadReplyUpdateTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_thread(thread_id,  replies) values ";
	private final static String SQL_SUFFIX = " on duplicate key update replies=values(replies)";
	private List<String> sqlList = new ArrayList<String>();

	@Override
	public void exec()
	{
		///清空主题的回复数
		clearThreadReply();
		
		///获取主题楼层数map
		System.out.println("prepare get thread posts......");
		Map<Long, Integer> postsMap = getThreadPosts();
		System.out.println("get thread posts completed!");
		
		///获取主题评论数map
		System.out.println("prepare get thread comments......");
		Map<Long, Integer> commentsMap = getThreadComments();
		System.out.println("get thread comments completed!");
		
		System.out.println("prepare handle update forum threads......");
		handle(postsMap, commentsMap);
		System.out.println("forum threads update completed!");
	}
	
	private void handle(Map<Long, Integer> threadPostsMap, Map<Long, Integer> threadCommentsMap)
	{
		try
		{
			String execSql = null;
			int total = 1;
			Iterator<Long> iterator = threadPostsMap.keySet().iterator();
			
			long threadId = 0L;
			int posts = 0;
			int replies = 0;
			while(iterator.hasNext())
			{
				int comments = 0;
				threadId = iterator.next();
				posts = threadPostsMap.get(threadId);
				if(threadCommentsMap.containsKey(threadId))
					comments = threadCommentsMap.get(threadId);
				
				replies = posts + comments;
				execSql = buildSql(threadId, replies);
				if(StringUtil.isNullOrEmpty(execSql))
					continue;

				sqlList.add(execSql);
				if(total % BATCH_EXEC_STEP == 0)
				{
					batchExec();
					System.out.println("update thread replies data total: " + total);
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
		
		String sql = strSql.substring(0, strSql.length() - 1) + SQL_SUFFIX;
		execute(sql);
		sqlList.clear();
	}
	
	private String buildSql(long threadId, int replies)
	{
		try
		{
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + threadId + ", " + replies + "),");
			return strSql.toString();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
	}
	
	private Map<Long, Integer> getThreadPosts()
	{
		String strSql = "select thread_id, count(1) from feed_post where status = 1 and position > 1 group by thread_id";
		ResultSet rs = query(strSql);
		if(null == rs)
			return null;
		
		try
		{
			Map<Long, Integer> map = new HashMap<Long, Integer>();
			long threadId = 0L;
			int posts = 0;
			while(rs.next())
			{
				threadId = rs.getLong(1);
				posts = rs.getInt(2);
				map.put(threadId, posts);
			}
			return map;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private Map<Long, Integer> getThreadComments()
	{
		String strSql = "select thread_id, count(1) from feed_comment where status = 1 group by thread_id";
		ResultSet rs = query(strSql);
		if(null == rs)
			return null;
		
		try
		{
			Map<Long, Integer> map = new HashMap<Long, Integer>();
			long threadId = 0L;
			int comments = 0;
			while(rs.next())
			{
				threadId = rs.getLong(1);
				comments = rs.getInt(2);
				map.put(threadId, comments);
			}
			return map;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private void clearThreadReply()
	{
		String strSql = "update feed_thread set replies = 0";
		execute(strSql);
	}
}