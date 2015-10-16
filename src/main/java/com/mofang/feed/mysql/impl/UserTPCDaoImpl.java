package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.external.Pair;
import com.mofang.feed.mysql.UserTPCDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

/***
 * 
 * @author linjx
 */
public class UserTPCDaoImpl extends AbstractMysqlSupport<Object>implements UserTPCDao {

	private static final UserTPCDaoImpl DAO = new UserTPCDaoImpl();
	
	private UserTPCDaoImpl() {
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch (Exception e) 
		{}
	}
	
	public static UserTPCDaoImpl getInstance() {
		return DAO;
	}
	
	@Override
	public List<Pair<Long, Long>> getForumIdThreadIdPairList(long userId, int start, int end)
			throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id, thread_id from feed_thread ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("and status = 1 ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());

		if (null == data)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (null == rows || rows.size() == 0)
			return null;
		
		List<Pair<Long, Long>> list = new ArrayList<Pair<Long, Long>>(rows.size());
		for(RowData row : rows) {
			Pair<Long, Long> pair = new Pair<Long, Long>();
			pair.left = row.getLong(0);
			pair.right = row.getLong(1);
			list.add(pair);
		}
		return list;
	}
	
	@Override
	public void deleteThreadAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_thread where user_id = " + userId);
		super.execute(strSql.toString());
		
	}

	@Override
	public void deleteThreadPostAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_post a inner join feed_thread b on a.thread_id = b.thread_id where b.user_id = " + userId);
		super.execute(strSql.toString());

	}

	@Override
	public void deleteThreadCommentAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_comment a inner join feed_thread b on a.thread_id = b.thread_id where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public void deleteThreadRecommendAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_thread_recommend a inner join feed_thread b on a.thread_id = b.thread_id where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public void deleteThreadFavorateAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_user_favorite a inner join feed_thread b on a.thread_id = b.thread_id where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public List<Pair<Long, Long>> getThreadIdPostIdPairList(long userId,
			int start, int end) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id, post_id from feed_post ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("and status = 1 ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;
		
		List<Pair<Long, Long>> list = new ArrayList<Pair<Long, Long>>(rows.size());
		for(RowData row : rows) {
			Pair<Long, Long> pair = new Pair<Long, Long>();
			pair.left = row.getLong(0);
			pair.right = row.getLong(1);
			list.add(pair);
		}
		return list;
	}

	@Override
	public void deletePostAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_post where user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public void deletePostRecommendAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_post_recommend a inner join feed_post b on a.post_id = b.post_id where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public void updatePostThreadRepliesAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread a inner join feed_post b on a.thread_id = b.thread_id set replies = replies - 1 where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public void deletePostCommentAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_comment a inner join feed_post b on a.post_id = b.post_id where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public List<Object[]> getPostIdCommentIdPairList(long userId,
			int start, int end) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select thread_id, post_id, comment_id from feed_comment ");
		strSql.append("where user_id = " + userId + " ");
		strSql.append("and status = 1 ");
		strSql.append("order by create_time desc ");
		strSql.append("limit " + start + ", " + end);
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;
		
		List<Object[]> list = new ArrayList<Object[]>(rows.size());
		for(RowData row : rows) {
			Object[] objArr = new Object[3];
			objArr[0] = row.getLong(0);
			objArr[1] = row.getLong(1);
			objArr[2] = row.getLong(2);
			list.add(objArr);
		}
		return list;
	}

	@Override
	public void deleteCommentAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_comment where user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateCommentThreadRepliesAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_thread a inner join feed_comment b on a.thread_id = b.thread_id set replies = replies - 1 where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

	@Override
	public void updateCommentPostRepliesAll(long userId) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_post a inner join feed_comment b on a.post_id = b.post_id set comments = comments - 1 where b.user_id = " + userId);
		super.execute(strSql.toString());
	}

}
