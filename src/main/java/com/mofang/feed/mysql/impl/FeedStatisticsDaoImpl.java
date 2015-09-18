package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.mysql.FeedStatisticsDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

public class FeedStatisticsDaoImpl extends AbstractMysqlSupport<Object> implements FeedStatisticsDao {

	private static final FeedStatisticsDaoImpl DAO = new FeedStatisticsDaoImpl();
	
	private FeedStatisticsDaoImpl(){
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}		
	}
	
	public static FeedStatisticsDaoImpl getInstance() {
		return DAO;
	}
	
	@Override
	public List<Object[]> forumNameList(int start, int end) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id, name, type from feed_forum order by forum_id asc limit " + start + "," + end);
		
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		
		List<Object[]> list = new ArrayList<Object[]>(rows.size());
		for(RowData row : rows) {
			Object[] objArr = new Object[3];
			objArr[0] = row.getLong(0);
			objArr[1] = row.getString(1);
			objArr[2] = row.getInteger(2);
			list.add(objArr);
		}
		return list;
	}
	
	@Override
	public List<Object[]> forumNameList(Set<Long> forumIdSet)
			throws Exception {
		String strForumIds = "";
		for (long strForumId : forumIdSet)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id, name, type from feed_forum where forum_id in(" +  strForumIds  +")");

		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		
		List<Object[]> list = new ArrayList<Object[]>(rows.size());
		for(RowData row : rows) {
			Object[] objArr = new Object[3];
			objArr[0] = row.getLong(0);
			objArr[1] = row.getString(1);
			objArr[2] = row.getInteger(2);
			list.add(objArr);
		}
		return list;

	}

	@Override
	public Map<Long, Integer> forumThreadCount(Set<Long> forumIdSet,
			long startTime, long endTime) throws Exception {
		String strForumIds = "";
		for (long strForumId : forumIdSet)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);

		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id, count(1) from feed_thread where ");
		if(startTime != 0l && endTime != 0l) {
			strSql.append("create_time >=" + startTime);
			strSql.append(" and create_time <=" + endTime + " and");
		}
		strSql.append(" forum_id in(" + strForumIds + ") group by forum_id");
		
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		
		Map<Long, Integer> map = new HashMap<Long, Integer>(rows.size());
		for(RowData row : rows) {
			map.put(row.getLong(0), row.getInteger(1));
		}
		return map;
	}

	@Override
	public Map<Long, Integer> forumPostCount(Set<Long> forumIdSet,
			long startTime, long endTime) throws Exception {
		String strForumIds = "";
		for (long strForumId : forumIdSet)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);

		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id, count(1) from feed_post where ");
		if(startTime != 0l && endTime != 0l) {
			strSql.append("create_time >=" + startTime);
			strSql.append(" and create_time <=" + endTime + " and");
		}
		strSql.append(" forum_id in(" + strForumIds + ") group by forum_id");
		
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		
		Map<Long, Integer> map = new HashMap<Long, Integer>(rows.size());
		for(RowData row : rows) {
			map.put(row.getLong(0), row.getInteger(1));
		}
		return map;
	}

	@Override
	public Map<Long, Integer> forumCommentCount(Set<Long> forumIdSet,
			long startTime, long endTime) throws Exception {
		String strForumIds = "";
		for (long strForumId : forumIdSet)
			strForumIds += strForumId + ",";
		if (strForumIds.length() > 0)
			strForumIds = strForumIds.substring(0, strForumIds.length() - 1);

		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id, count(1) from feed_comment where ");
		if(startTime != 0l && endTime != 0l) {
			strSql.append("create_time >=" + startTime);
			strSql.append(" and create_time <=" + endTime + " and");
		}
		strSql.append(" forum_id in(" + strForumIds + ") group by forum_id");
		
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		
		Map<Long, Integer> map = new HashMap<Long, Integer>(rows.size());
		for(RowData row : rows) {
			map.put(row.getLong(0), row.getInteger(1));
		}
		return map;
	}


}
