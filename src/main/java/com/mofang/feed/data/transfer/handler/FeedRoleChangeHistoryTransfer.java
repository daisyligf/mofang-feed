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
public class FeedRoleChangeHistoryTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_role_change_history(history_id, user_id, nick_name, original_role_id, original_role_name, current_role_id, current_role_name, forum_id, forum_name, operate_reason, operator_id, operator_name, create_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取版块数据
		System.out.println("get role_change_history data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_role_change_history data is null.");
			return;
		}
		
		System.out.println("prepare handle role_change_history data......");
		handle(rs);
		System.out.println("role_change_history data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select history_id, user_id, nick_name, original_role_id, original_role_name, current_role_id, current_role_name, forum_id, forum_name, operate_reason, operator_id, operator_name, create_time ");
		strSql.append("from feed_role_change_history ");
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
					System.out.println("insert role_change_history data total: " + total);
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
			long historyId = rs.getLong(1);
			long userId = rs.getLong(2);
			String nickName = rs.getString(3) == null ? "" : rs.getString(3);
			int originalRoleId = rs.getInt(4);
			String originalRoleName = rs.getString(5) == null ? "" : rs.getString(5);
			int currentRoleId = rs.getInt(6);
			String currentRoleName = rs.getString(7) == null ? "" : rs.getString(7);
			long forumId = rs.getLong(8);
			String forumName = rs.getString(9) == null ? "" : rs.getString(9);
			String operateReason = rs.getString(10) == null ? "" : rs.getString(10);
			long operatorId = rs.getInt(11);
			String operatorName = rs.getString(12) == null ? "" : rs.getString(12);
			long createTime = rs.getTimestamp(13).getTime();
			
			nickName = StringUtil.safeSql(nickName);
			originalRoleName = StringUtil.safeSql(originalRoleName);
			currentRoleName = StringUtil.safeSql(currentRoleName);
			forumName = StringUtil.safeSql(forumName);
			operateReason = StringUtil.safeSql(operateReason);
			operatorName = StringUtil.safeSql(operatorName);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + historyId + "," + userId + ",'" + nickName + "'," + originalRoleId + ",'" + originalRoleName + "',");
			strSql.append(currentRoleId + ",'" + currentRoleName + "'," + forumId + ",'" + forumName + "','" + operateReason + "',");
			strSql.append(operatorId + ",'" + operatorName + "'," + createTime + "),");
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
		String strSql = "truncate table feed_role_change_history";
		execute(strSql);
	}
}