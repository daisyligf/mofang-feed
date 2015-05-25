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
public class FeedPostRecommendTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_post_recommend(post_id, user_id, create_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取版块数据
		System.out.println("get post_recommend data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_post_recommend data is null.");
			return;
		}
		
		System.out.println("prepare handle post_recommend data......");
		handle(rs);
		System.out.println("post_recommend data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select pid, user_id, create_time from feed_post_recommend group by pid, user_id");
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
					System.out.println("insert post_recommend data total: " + total);
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
			long userId = rs.getLong(2);
			long createTime = rs.getTimestamp(3).getTime();
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + postId + "," + userId + "," + createTime + "),");
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
		String strSql = "truncate table feed_post_recommend";
		execute(strSql);
	}
}