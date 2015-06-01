package com.mofang.feed.data.transfer.handler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.feed.data.transfer.ForumChangeUtil;
import com.mofang.feed.global.common.ForumType;
import com.mofang.framework.net.http.HttpRequester;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_forum(forum_id, game_id, name, name_spell, icon, color, type, is_edit, is_hidden, threads, create_time, update_time) values ";
	private List<String> sqlList = new ArrayList<String>();
	private Map<Long, Integer> forumGameMap = new HashMap<Long, Integer>();
	private int bbsActivityThreads = 0;
	private int recommendThreads = 0;
	private int ChatThreads = 0;
	private int FeedBackThreads = 0;
	
	public void exec()
	{
		truncate();
		
		///初始化版块对应游戏ID
		initForumGameMap();
		
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
		
		System.out.println("prepare add forum tag data......");
		addForumTag();
		System.out.println("add forum tag data completed!");
		
		System.out.println("prepare add new forum data......");
		addNewForum();
		System.out.println("add new forum data completed!");
		
		System.out.println("prepare add forum index......");
		addIndex();
		System.out.println("add forum index completed!");
	}
	
	private ResultSet getData()
	{
		String retainForumIds = ForumChangeUtil.convertToRetainForumString(ForumChangeUtil.RetainForumSet);
		StringBuilder strSql = new StringBuilder();
		strSql.append("select fid, name, threads, icon, color, name_spell, is_hidden, create_time ");
		strSql.append("from feed_forum where fid in (" + retainForumIds + ")");
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
			String name = rs.getString(2) == null ? "" : rs.getString(2);
			int threads = rs.getInt(3);
			String icon = rs.getString(4) == null ? "" : rs.getString(4);
			String color = rs.getString(5) == null ? "" : rs.getString(5);
			String nameSpell = rs.getString(6) == null ? "" : rs.getString(6);
			int hidden = rs.getInt(7);
			long createTime = rs.getTimestamp(8).getTime();

			name = StringUtil.safeSql(name);
			icon = StringUtil.safeSql(icon);
			color = StringUtil.safeSql(color);
			nameSpell = StringUtil.safeSql(nameSpell);
			
			boolean isEdit = true;
			boolean isHidden = (hidden == 1);
			int type = ForumType.HOT_FORUM;  ///热游
			if(ForumChangeUtil.HotGameForumSet.contains(forumId))
				type = ForumType.HOT_FORUM;
			else if(ForumChangeUtil.NewGameForumSet.contains(forumId))
				type = ForumType.RECOMMEND_GAME;
			
			if(isHidden)
				type = ForumType.HIDDEN;
			
			int gameId = 0;
			if(forumGameMap.containsKey(forumId))
				gameId = forumGameMap.get(forumId);
			
			if(forumId == 40765L || forumId == 38825L || forumId == 287L || forumId == 36774L)  ///社区活动
				bbsActivityThreads += threads;
			else if(forumId == 41962L || forumId == 38821L || forumId == 326L || forumId == 38767L)  ///魔方推荐
				recommendThreads += threads;
			else if(forumId == 38823L || forumId == 36772L || forumId == 327L || forumId == 36773L || forumId == 325L)  ///灌水闲聊
				ChatThreads += threads;
			else if(forumId == 38824L)  ///问题反馈
				FeedBackThreads += threads;
			
			if(ForumChangeUtil.OfficalForumSet.contains(forumId))
				return null;
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + forumId + ", " + gameId + ",'" + name + "','" + nameSpell + "',");
			strSql.append("'" + icon + "','" + color + "'," + type + ", " + isEdit + " ," + isHidden +  "," + threads + "," + createTime + "," + createTime + "),");
			return strSql.toString();
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return null;
		}
	}
	
	private void addNewForum()
	{
		long now = System.currentTimeMillis();
		StringBuilder strSql = new StringBuilder();
		strSql.append(SQL_PREFIX);
		strSql.append("(" + ForumChangeUtil.BbsActivityForumId + ", 0, '社区活动', 'SQHD', '', '', " + ForumType.OFFICAL + ", false, false, " + bbsActivityThreads + ", " +  now + ", " + now + "),");
		strSql.append("(" + ForumChangeUtil.RecommendForumId + ", 0, '魔方推荐', 'MFTJ', '', '', " + ForumType.OFFICAL + ", false, false, " + recommendThreads + ", " +  now + ", " + now + "),");
		strSql.append("(" + ForumChangeUtil.ChatForumId + ", 0, '灌水闲聊', 'GSXL', '', '', " + ForumType.OFFICAL + ", false, false, " + ChatThreads + ", " +  now + ", " + now + "),");
		strSql.append("(" + ForumChangeUtil.FeedBackForumId + ", 0, '问题反馈', 'WTFK', '', '', " + ForumType.OFFICAL + ", false, false, " + FeedBackThreads + ", " +  now + ", " + now + ")");
		execute(strSql.toString());
		
		strSql = strSql.delete(0, strSql.length());
		
		strSql.append("insert into feed_forum_tag(forum_id, tag_id, create_time) values ");
		strSql.append("(" + ForumChangeUtil.BbsActivityForumId + ", 4, " + now + "),");
		strSql.append("(" + ForumChangeUtil.BbsActivityForumId + ", 5, " + now + "),");
		strSql.append("(" + ForumChangeUtil.BbsActivityForumId + ", 6, " + now + "),");
		strSql.append("(" + ForumChangeUtil.BbsActivityForumId + ", 7, " + now + "),");
		strSql.append("(" + ForumChangeUtil.RecommendForumId + ", 8, " + now + "),");
		strSql.append("(" + ForumChangeUtil.RecommendForumId + ", 9, " + now + "),");
		strSql.append("(" + ForumChangeUtil.RecommendForumId + ", 10, " + now + "),");
		strSql.append("(" + ForumChangeUtil.RecommendForumId + ", 11, " + now + "),");
		strSql.append("(" + ForumChangeUtil.ChatForumId + ", 12, " + now + "),");
		strSql.append("(" + ForumChangeUtil.ChatForumId + ", 13, " + now + "),");
		strSql.append("(" + ForumChangeUtil.ChatForumId + ", 14, " + now + "),");
		strSql.append("(" + ForumChangeUtil.ChatForumId + ", 15, " + now + "),");
		strSql.append("(" + ForumChangeUtil.ChatForumId + ", 16, " + now + "),");
		strSql.append("(" + ForumChangeUtil.FeedBackForumId + ", 17, " + now + "),");
		strSql.append("(" + ForumChangeUtil.FeedBackForumId + ", 18, " + now + ")");
		execute(strSql.toString());
	}
	
	private void addForumTag()
	{
		long now = System.currentTimeMillis();
		StringBuilder strSql = new StringBuilder();
		strSql.append("insert into feed_forum_tag(forum_id, tag_id, create_time) values ");
		for(Long forumId : ForumChangeUtil.HotGameForumSet)
		{
			strSql.append("(" + forumId + ", 1, " + now + "),");
			strSql.append("(" + forumId + ", 2, " + now + "),");
			strSql.append("(" + forumId + ", 3, " + now + "),");
		}
		
		for(Long forumId : ForumChangeUtil.NewGameForumSet)
		{
			strSql.append("(" + forumId + ", 1, " + now + "),");
			strSql.append("(" + forumId + ", 2, " + now + "),");
			strSql.append("(" + forumId + ", 3, " + now + "),");
		}
		
		String sql = null;
		if(strSql.length() > 0)
			sql = strSql.substring(0, strSql.length() - 1);
		
		if(!StringUtil.isNullOrEmpty(sql))
			execute(sql);
	}
	
	
	private void initForumGameMap()
	{
		String requrl = "http://game.mofang.com/api/game/listfromforumids?type=1&ids=";
		String param = ForumChangeUtil.convertToRetainForumString(ForumChangeUtil.RetainForumSet);
		requrl += param;
		HttpRequester request = new HttpRequester(requrl);
		String response = request.execute(3);
		if(!StringUtil.isNullOrEmpty(response))
		{
			try
			{
				JSONObject json = new JSONObject(response);
				JSONObject data = json.optJSONObject("data");
				if(null != data)
				{
					@SuppressWarnings("unchecked")
					Iterator<String> iterator = data.keys();
					JSONObject jsonItem = null;
					while(iterator.hasNext())
					{
						String key = iterator.next();
						if(StringUtil.isLong(key))
						{
							jsonItem = data.optJSONObject(key);
							int gameId = jsonItem.optInt("id");
							forumGameMap.put(Long.parseLong(key), gameId);
						}
					}
				}
			}
			catch(Exception e)
			{}
		}
	}
	
	private void truncate()
	{
		String strSql = "truncate table feed_forum";
		execute(strSql);
		
		strSql = "truncate table feed_forum_tag";
		execute(strSql);
	}
	
	private void addIndex()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("ALTER TABLE feed_forum ADD INDEX idx_type(type);");
		execute(strSql.toString());
	}
}