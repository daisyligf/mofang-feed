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
public class FeedPostCommentsUpdateTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_post(post_id,  comments) values ";
	private final static String SQL_SUFFIX = " on duplicate key update comments=values(comments)";
	private List<String> sqlList = new ArrayList<String>();

	@Override
	public void exec()
	{
		///清空楼层评论数
		clearPostComments();
		
		ResultSet rs = getPostComments();
		if(null == rs)
		{
			System.out.println("post comments data is null.");
			return;
		}
		
		System.out.println("prepare update post comments......");
		handle(rs);
		System.out.println("post comments update completed!");
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
					System.out.println("update post comments data total: " + total);
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
			long postId = rs.getLong(1);
			int comments = rs.getInt(2);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + postId + ", " + comments + "),");
			return strSql.toString();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
	}
	
	private ResultSet getPostComments()
	{
		String strSql = "select post_id, count(1) from feed_comment where status = 1 group by post_id";
		return query(strSql);
	}
	
	private void clearPostComments()
	{
		String strSql = "update feed_post set comments = 0";
		execute(strSql);
	}
}