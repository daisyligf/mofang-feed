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
public class FeedForumTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_forum(forum_id, parent_id, name, name_spell, icon, color, type, is_edit, threads, create_time, update_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取版块数据
		System.out.println("get forum data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_forum data is null.");
			return;
		}
		
		System.out.println("prepare handle forum data......");
		handle(rs);
		System.out.println("forum data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select fid, parent_id, name, threads, icon, color, type, name_spell, create_time ");
		strSql.append("from feed_forum ");
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
					System.out.println("insert forum data total: " + total);
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
			long forumId = rs.getLong(1);
			long parentId = rs.getLong(2);
			String name = rs.getString(3) == null ? "" : rs.getString(3);
			int threads = rs.getInt(4);
			String icon = rs.getString(5) == null ? "" : rs.getString(5);
			String color = rs.getString(6) == null ? "" : rs.getString(6);
			int type = rs.getInt(7);
			String nameSpell = rs.getString(8) == null ? "" : rs.getString(8);
			long createTime = rs.getTimestamp(9).getTime();

			name = StringUtil.safeSql(name);
			icon = StringUtil.safeSql(icon);
			color = StringUtil.safeSql(color);
			nameSpell = StringUtil.safeSql(nameSpell);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + forumId + "," + parentId + ",'" + name + "','" + nameSpell + "',");
			strSql.append("'" + icon + "','" + color + "'," + type + ",1," + threads + "," + createTime + "," + createTime + "),");
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
		String strSql = "truncate table feed_forum";
		execute(strSql);
	}
}