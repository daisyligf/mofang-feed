package com.mofang.feed.data.transfer.handler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.feed.data.transfer.ForumChangeUtil;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedUserFavoriteTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_user_favorite(user_id, thread_id, create_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	
	public void exec()
	{
		truncate();
		
		///获取收藏数据
		System.out.println("get user_favorite data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_user_favorite data is null.");
			return;
		}
		
		System.out.println("prepare handle user_favorite data......");
		handle(rs);
		System.out.println("user_favorite data transfer completed!");
		
		System.out.println("prepare add user_favorite index......");
		//addIndex();
		System.out.println("add user_favorite index completed!");
		
		System.gc();
	}
	
	private ResultSet getData()
	{
		String forumIds = ForumChangeUtil.convertToRetainForumString(ForumChangeUtil.RetainForumSet);
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.tid, a.uid, a.create_time ");
		strSql.append("from feed_favorite a ");
		strSql.append("left join ");
		strSql.append("(select tid from feed_thread where fid in (" + forumIds + ")) b on a.tid = b.tid ");
		strSql.append("where b.tid is not null and a.uid > 0 group by a.tid, a.uid ");
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
					System.out.println("insert user_favorite data total: " + total);
				}
				total++;
			}
			
			batchExec();
			rs.close();
			rs = null;
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
			long threadId = rs.getLong(2);
			long createTime = rs.getTimestamp(3).getTime();
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + userId + "," + threadId + "," + createTime + "),");
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
		String strSql = "truncate table feed_user_favorite";
		execute(strSql);
	}

	private void addIndex()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("ALTER TABLE feed_user_favorite ADD INDEX idx_user_id(user_id);");
		execute(strSql.toString());
	}
}