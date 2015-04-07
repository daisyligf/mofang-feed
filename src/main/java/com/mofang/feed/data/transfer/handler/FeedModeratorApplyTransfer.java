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
public class FeedModeratorApplyTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_moderator_apply(apply_id, forum_id, forum_name, user_id, nick_name, register_time, contact_qq, contact_mobile, reason, status, create_time, update_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取版块数据
		System.out.println("get moderator_apply data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_moderator_apply data is null.");
			return;
		}
		
		System.out.println("prepare handle moderator_apply data......");
		handle(rs);
		System.out.println("moderator_apply data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select apply_id, forum_id, forum_name, user_id, nick_name, register_time, contact_qq, contact_mobile, reason, status, create_time, update_time ");
		strSql.append("from feed_moderator_apply ");
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
					System.out.println("insert moderator_apply data total: " + total);
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
			int applyId = rs.getInt(1);
			long forumId = rs.getLong(2);
			String forumName = rs.getString(3) == null ? "" : rs.getString(3);
			long userId = rs.getLong(4);
			String nickName = rs.getString(5) == null ? "" : rs.getString(5);
			long registerTime = rs.getLong(6);
			String contactQQ = rs.getString(7) == null ? "" : rs.getString(7);
			String contactMobile = rs.getString(8) == null ? "" : rs.getString(8);
			String reason = rs.getString(9) == null ? "" : rs.getString(9);
			int status = rs.getInt(10);
			long createTime = rs.getTimestamp(11).getTime();
			long updateTime = rs.getTimestamp(12).getTime();

			forumName = StringUtil.safeSql(forumName);
			nickName = StringUtil.safeSql(nickName);
			contactQQ = StringUtil.safeSql(contactQQ);
			contactMobile = StringUtil.safeSql(contactMobile);
			reason = StringUtil.safeSql(reason);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + applyId + "," + forumId + ",'" + forumName + "'," + userId + ",'" + nickName + "'," + registerTime + ",");
			strSql.append("'" + contactQQ + "','" + contactMobile + "','" + reason + "'," + status + "," + createTime + "," + updateTime + "),");
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
		String strSql = "truncate table feed_moderator_apply";
		execute(strSql);
	}
}