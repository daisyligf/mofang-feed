package com.mofang.feed.solr.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedCommentRedisImpl;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedCommentSolrImpl extends BaseSolr implements FeedCommentSolr
{
	private final static FeedCommentSolrImpl SOLR = new FeedCommentSolrImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	private FeedCommentDao commentDao = FeedCommentDaoImpl.getInstance();
	
	private FeedCommentSolrImpl()
	{}
	
	public static FeedCommentSolrImpl getInstance()
	{
		return SOLR;
	}

	@Override
	public void add(FeedComment model) throws Exception
	{
		IndexAdd add = new IndexAdd(model);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void batchAdd(List<FeedComment> list) throws Exception
	{
		IndexBatchAdd add = new IndexBatchAdd(list);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void deleteById(long commentId) throws Exception
	{
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(commentId));
		deleteByIds(ids);
	}

	@Override
	public void deleteByIds(List<String> commentIds) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		IndexDeleteById delete = new IndexDeleteById(solrServer, commentIds);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByForumId(long forumId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		String query = "forum_id:" + forumId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByThreadId(long threadId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		String query = "thread_id:" + threadId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByPostId(long postId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		String query = "post_id:" + postId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public Page<FeedComment> search(long forumId, String forumName, String author, String keyword, int status, int start, int size) throws Exception
	{
		SolrQuery query = new SolrQuery();
		StringBuilder strQuery = new StringBuilder();
		if(forumId > 0)
			strQuery.append(" AND forum_id:" + forumId);
		if(!StringUtil.isNullOrEmpty(forumName))
			strQuery.append(" AND forum_name:" + forumName);
		if(!StringUtil.isNullOrEmpty(author))
			strQuery.append(" AND nickname:" + author);
		if(!StringUtil.isNullOrEmpty(keyword))
			strQuery.append(" AND content:" + keyword);
		strQuery.append(" AND status:" + status);
		
		if(strQuery.length() == 0)
			return null;
		String queryParam = strQuery.substring(4, strQuery.length());
		queryParam = "(" + queryParam + ")";
		
		query.setQuery(queryParam);
		query.setStart(start);
		query.setRows(size);
		query.setSort("time", ORDER.desc);
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		QueryResponse response = solrServer.query(query);
		if(null == response)
			return null;
		
		SolrDocumentList docList = response.getResults();
		long total = docList.getNumFound();
		
		List<FeedComment> list = new ArrayList<FeedComment>();
		FeedComment commentInfo = null;
		Iterator<SolrDocument> iterator = docList.iterator();
		String strCommentId;
		while(iterator.hasNext())
		{
			SolrDocument doc = iterator.next();
			strCommentId = doc.getFieldValue("id").toString();
			if(!StringUtil.isLong(strCommentId))
				continue;
			
			if(status == CommentStatus.NORMAL)
				commentInfo = commentRedis.getInfo(Long.parseLong(strCommentId));
			else if(status == CommentStatus.DELETED)
				commentInfo = commentDao.getInfo(Long.parseLong(strCommentId));
			if(null == commentInfo)
				continue;
			
			list.add(commentInfo);
		}
		return new Page<FeedComment>(total, list);
	}

	class IndexAdd implements Runnable
	{
		private FeedComment commentInfo;
		
		public IndexAdd(FeedComment commentInfo)
		{
			this.commentInfo = commentInfo;
		}

		@Override
		public void run()
		{
			try
			{
				SolrInputDocument solrDoc = convertToDoc(commentInfo);
				GlobalObject.SOLR_SERVER_COMMENT.add(solrDoc);
				GlobalObject.SOLR_SERVER_COMMENT.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedCommentSolrImpl.IndexAdd.run throw an error", e);
			}
		}
	}
	
	class IndexBatchAdd implements Runnable
	{
		private List<FeedComment> commentList;
		
		public IndexBatchAdd(List<FeedComment> commentList)
		{
			this.commentList = commentList;
		}

		@Override
		public void run()
		{
			try
			{
				List<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
				for(FeedComment commentInfo : commentList)
				{
					SolrInputDocument solrDoc = convertToDoc(commentInfo);
					list.add(solrDoc);
				}
				GlobalObject.SOLR_SERVER_COMMENT.add(list);
				GlobalObject.SOLR_SERVER_COMMENT.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedCommentSolrImpl.IndexBatchAdd.run throw an error", e);
			}
		}
	}
	
	private SolrInputDocument convertToDoc(FeedComment commentInfo)
	{
		SolrInputDocument solrDoc = new SolrInputDocument();
		try
		{
			long forumId = commentInfo.getForumId();
			long userId = commentInfo.getUserId();
		
			///获取版块信息
			String forumName = "";
			FeedForum forumInfo = forumRedis.getInfo(forumId);
			if(null != forumInfo)
				forumName = forumInfo.getName();
			
			///获取用户信息
			String nickName = "";
			User userInfo = UserComponent.getInfo(userId);
			if(null != userInfo)
				nickName = userInfo.getNickName();
			
			solrDoc.addField("id", commentInfo.getCommentId());
			solrDoc.addField("forum_id", forumId);
			solrDoc.addField("forum_name", forumName);
			solrDoc.addField("thread_id", commentInfo.getThreadId());
			solrDoc.addField("post_id", commentInfo.getPostId());
			solrDoc.addField("user_id", userId);
			solrDoc.addField("nickname", nickName);
			solrDoc.addField("status", commentInfo.getStatus());
			solrDoc.addField("time", commentInfo.getCreateTime());
			solrDoc.addField("content", commentInfo.getContent());
			return solrDoc;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentSolrImpl.convertToDoc throw an error", e);
			return null;
		}
	}
}