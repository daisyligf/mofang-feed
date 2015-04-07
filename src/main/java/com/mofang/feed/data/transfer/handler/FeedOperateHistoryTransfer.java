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
public class FeedOperateHistoryTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_operate_history(history_id, user_id, nick_name, forum_id, forum_name, privilege_id, source_type, source_id, operate_behavior, operate_reason, operator_id, operator_name, create_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取版块数据
		System.out.println("get operate_history data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_operate_history data is null.");
			return;
		}
		
		System.out.println("prepare handle operate_history data......");
		handle(rs);
		System.out.println("operate_history data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select history_id, user_id, nick_name, forum_id, forum_name, privilege_id, source_type, source_id, operate_behavior, operate_reason, operator_id, operator_name, create_time ");
		strSql.append("from feed_operate_history ");
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
					System.out.println("insert operate_history data total: " + total);
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
			long forumId = rs.getLong(4);
			String forumName = rs.getString(5) == null ? "" : rs.getString(5);
			int privilegeId = rs.getInt(6);
			int sourceType = rs.getInt(7);
			long sourceId = rs.getLong(8);
			int operateBehavior = rs.getInt(9);
			String operateReason = rs.getString(10) == null ? "" : rs.getString(10);
			long operatorId = rs.getLong(11);
			String operatorName = rs.getString(12) == null ? "" : rs.getString(12);
			long createTime = rs.getTimestamp(13).getTime();
			
			nickName = StringUtil.safeSql(nickName);
			forumName = StringUtil.safeSql(forumName);
			operateReason = StringUtil.safeSql(operateReason);
			operatorName = StringUtil.safeSql(operatorName);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + historyId + "," + userId + ",'" + nickName + "'," + forumId + ",'" + forumName + "',");
			strSql.append(privilegeId + "," + sourceType + "," + sourceId + "," + operateBehavior + ",'" + operateReason + "',");
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
		String strSql = "truncate table feed_operate_history";
		execute(strSql);
	}
}