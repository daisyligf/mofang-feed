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
public class FeedModuleItemTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_module_item(item_id, module_id, thread_id, title, subtitle, pic_url, display_order, status, online_time, create_time, update_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取版块数据
		System.out.println("get module_item data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_module_item is null.");
			return;
		}
		
		System.out.println("prepare handle module_item data......");
		handle(rs);
		System.out.println("module_item data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select id, vid, tid, subject, subtitle, pic_url, display_order, status, online_time, create_time, modify_time ");
		strSql.append("from feed_virtual_thread ");
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
					System.out.println("insert module_item data total: " + total);
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
			long itemId = rs.getLong(1);
			long moduleId = rs.getLong(2);
			long threadId = rs.getLong(3);
			String title = rs.getString(4) == null ? "" : rs.getString(4);
			String subtitle = rs.getString(5) == null ? "" : rs.getString(5);
			String picUrl = rs.getString(6) == null ? "" : rs.getString(6);
			int displayOrder = rs.getInt(7);
			int status = rs.getInt(8);
			long onlineTime = 0L;
			String strOnlineTime = rs.getString(9);
			if(StringUtil.isDate(strOnlineTime, "yyyy-MM-dd HH:mm:ss"))
				onlineTime = rs.getTimestamp(9).getTime();
			
			long createTime = rs.getTimestamp(10).getTime();
			long updateTime = rs.getTimestamp(11).getTime();
			
			title = StringUtil.safeSql(title);
			subtitle = StringUtil.safeSql(subtitle);
			picUrl = StringUtil.safeSql(picUrl);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + itemId + "," + moduleId + "," + threadId + ",'" + title + "','" + subtitle + "',");
			strSql.append("'" + picUrl + "'," + displayOrder + "," + status + "," + onlineTime + "," + createTime + "," + updateTime + "),");
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
		String strSql = "truncate table feed_module_item";
		execute(strSql);
	}
}