package com.mofang.feed.data.transfer.handler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.data.transfer.BaseTransfer;
import com.mofang.feed.data.transfer.FeedTransfer;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadTransfer extends BaseTransfer implements FeedTransfer
{
	private final static int BATCH_EXEC_STEP = 1000;
	private final static String SQL_PREFIX = "insert into feed_thread(thread_id, forum_id, user_id, subject, subject_filter, subject_mark, link_url, type, status, is_top, is_closed, is_elite, is_mark, is_video, replies, recommends, share_times, page_view, last_post_uid, updown, top_time, updown_time, last_post_time, game_id, create_time, update_time) values ";
	private final static int ELITE_TAG_ID = 1;
	private final static int VIDEO_TAG_ID = 4;
	private final static int QUESTION_TAG_ID = 9;
	private final static int MARK_TAG_ID = 10;
	private List<String> sqlList = new ArrayList<String>();
	private Set<Long> eliteSet = new HashSet<Long>();
	private Set<Long> videoSet = new HashSet<Long>();
	private Set<Long> questionSet = new HashSet<Long>();
	private Set<Long> markSet = new HashSet<Long>();
	private Map<Long, Long> threadGameIdMap = null;
	
	public void exec()
	{
		truncate();
		
		///获取主题标签
		System.out.println("get thread tag data......");
		Map<Integer, Set<Long>> threadTagMap = getThreadTag();
		if(threadTagMap.containsKey(ELITE_TAG_ID))
			eliteSet = threadTagMap.get(ELITE_TAG_ID);
		if(threadTagMap.containsKey(VIDEO_TAG_ID))
			videoSet = threadTagMap.get(VIDEO_TAG_ID);
		if(threadTagMap.containsKey(QUESTION_TAG_ID))
			questionSet = threadTagMap.get(QUESTION_TAG_ID);
		if(threadTagMap.containsKey(MARK_TAG_ID))
			markSet = threadTagMap.get(MARK_TAG_ID);
		
		///获取主题对应的游戏ID
		System.out.println("get thread gameid data......");
		threadGameIdMap = getThreadGameId();
		
		///获取主题数据
		System.out.println("get thread data......");
		ResultSet rs = getData();
		if(null == rs)
		{
			System.out.println("feed_forum data is null.");
			return;
		}
		
		System.out.println("prepare handle thread data......");
		handle(rs);
		System.out.println("thread data transfer completed!");
	}
	
	private ResultSet getData()
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select tid, fid, user_id, subject, end_post_time, last_poster_id, is_closed, replies, recommends, share_times, page_view, status, linkurl, type, top_time, create_time ");
		strSql.append("from feed_thread ");
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
					System.out.println("insert thread data total: " + total);
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
	
	private Map<Integer, Set<Long>> getThreadTag()
	{
		Map<Integer, Set<Long>> map = new HashMap<Integer, Set<Long>>();
		String strSql = "select tagid, tid from feed_tag_thread where tagid in (1,4,9,10)";
		
		try
		{
			ResultSet rs = getData(strSql);
			if(null != rs)
			{
				int tagId = 0;
				long threadId = 0L;
				while(rs.next())
				{
					tagId = rs.getInt(1);
					threadId = rs.getLong(2);
					Set<Long> set = new HashSet<Long>();
					if(map.containsKey(tagId))
						set = map.get(tagId);
					
					set.add(threadId);
					map.put(tagId, set);
				}
			}
		}
		catch(Exception e)
		{}
		return map;
	}
	
	private Map<Long, Long> getThreadGameId()
	{
		Map<Long, Long> map = new HashMap<Long, Long>();
		String strSql = "select tid, game_id from feed_thread_game where game_id > 0";
		try
		{
			ResultSet rs = getData(strSql);
			if(null != rs)
			{
				long threadId = 0L;
				long gameId = 0L;
				while(rs.next())
				{
					threadId = rs.getLong(1);
					gameId = rs.getLong(2);
					map.put(threadId, gameId);
				}
			}
		}
		catch(Exception e)
		{}
		return map;
	}
	
	private String buildSql(ResultSet rs)
	{
		try
		{
			long threadId = rs.getLong(1);
			long forumId = rs.getLong(2);
			long userId = rs.getLong(3);
			String subject = rs.getString(4) == null ? "" : rs.getString(4);
			long lastPostTime = rs.getTimestamp(5).getTime();
			long lastPostUid = rs.getLong(6);
			boolean isClosed = rs.getBoolean(7);
			int replies = rs.getInt(8);
			int recommends = rs.getInt(9);
			int shareTimes = rs.getInt(10);
			int pageView = rs.getInt(11);
			int status = rs.getInt(12);
			String linkUrl = rs.getString(13) == null ? "" : rs.getString(13);
			int type = rs.getInt(14) == 0 ? ThreadType.NORMAL : rs.getInt(14);
			
			String strTopTime = rs.getString(15);
			long topTime = 0L;
			if(StringUtil.isDate(strTopTime, "yyyy-MM-dd HH:mm:ss"))
				topTime = rs.getTimestamp(15).getTime();
			
			long createTime = rs.getTimestamp(16).getTime();
			boolean isTop = topTime > 0;
			
			boolean isElite = eliteSet.contains(threadId);
			boolean isMark = markSet.contains(threadId);
			boolean isVideo = videoSet.contains(threadId);
			
			if(questionSet.contains(threadId))
				type = ThreadType.QUESTION;
			
			long gameId = 0L;
			if(threadGameIdMap.containsKey(threadId))
				gameId = threadGameIdMap.get(threadId);
			
			subject = StringUtil.safeSql(subject);
			linkUrl = StringUtil.safeSql(linkUrl);
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("(" + threadId + "," + forumId + "," + userId + ",'" + subject + "','" + subject + "','" + subject + "',");
			strSql.append("'" + linkUrl + "'," + type + "," + status + "," + isTop + ", " + isClosed + "," + isElite + "," + isMark + "," + isVideo + "," + replies + "," + recommends + ",");
			strSql.append(shareTimes + "," + pageView + "," + lastPostUid + ",0," + topTime + ",0," + lastPostTime + "," + gameId + "," + createTime + "," + createTime + "),");
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
		String strSql = "truncate table feed_thread";
		execute(strSql);
	}
}