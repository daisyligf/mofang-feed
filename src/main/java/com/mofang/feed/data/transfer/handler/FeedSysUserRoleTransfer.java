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
public class FeedSysUserRoleTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_sys_user_role(user_id, forum_id, role_id, create_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取版块数据
		System.out.println("get sys_user_role data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_sys_user_role data is null.");
			return;
		}
		
		System.out.println("prepare handle sys_user_role data......");
		handle(rs);
		System.out.println("sys_user_role data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select user_id, forum_id, role_id,create_time from feed_sys_user_role group by forum_id, user_id ");
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
					System.out.println("insert sys_user_role data total: " + total);
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
			long userId = rs.getLong(1);
			long forumId = rs.getLong(2);
			int roleId = rs.getInt(3);
			long createTime = rs.getTimestamp(4).getTime();
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + userId + "," + forumId + "," + roleId + "," + createTime + "),");
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
		String strSql = "truncate table feed_sys_user_role";
		execute(strSql);
	}
}