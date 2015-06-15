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
public class FeedForumThreadsUpdateTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_forum(forum_id,  threads) values ";
	private final static String SQL_SUFFIX = " on duplicate key update threads=values(threads)";
	private List<String> sqlList = new ArrayList<String>();

	@Override
	public void exec()
	{
		///清空版块主题数
		clearForumThreads();
		
		ResultSet rs = getForumThreads();
		if(null == rs)
		{
			System.out.println("forum threads data is null.");
			return;
		}
		
		System.out.println("prepare handle update forum threads......");
		handle(rs);
		System.out.println("forum threads update completed!");
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
					System.out.println("update forum threads data total: " + total);
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
	
	private String buildSql(ResultSet rs)
	{
		try
		{
			long forumId = rs.getLong(1);
			int threads = rs.getInt(2);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + forumId + ", " + threads + "),");
			return strSql.toString();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
	}
	
	private ResultSet getForumThreads()
	{
		String strSql = "select forum_id, count(1) from feed_thread where status = 1 group by forum_id";
		return query(strSql);
	}
	
	private void clearForumThreads()
	{
		String strSql = "update feed_forum set threads = 0";
		execute(strSql);
	}
}